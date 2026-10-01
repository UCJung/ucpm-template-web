#!/usr/bin/env bash
# prod 백업 스크립트 (템플릿)
#
# 프로젝트 고유값은 이 파일에 쓰지 않고 설정 파일(BACKUP_ENV_FILE, 기본
# /etc/<앱키>/prod-backup.env)에서 주입함. operation/backup/systemd/prod-backup.env.example 참조.
#
# 사용법: prod-backup.sh --reason {scheduled|pre-deploy|manual}
#   scheduled  → daily/      (최근 N개 유지)
#   pre-deploy → pre-deploy/ (기존 삭제 후 1개 생성, 실패 시 배포 중단)
#   manual     → manual/     (자동 정리 없음)
set -euo pipefail
umask 077

usage() { echo "usage: $0 --reason {scheduled|pre-deploy|manual}" >&2; }
reason=""
while (($#)); do
  case "$1" in
    --reason) shift; (($#)) || { usage; exit 2; }; reason="$1" ;;
    --reason=*) reason="${1#--reason=}" ;;
    -h|--help) usage; exit 0 ;;
    *) usage; exit 2 ;;
  esac
  shift
done
case "$reason" in scheduled|pre-deploy|manual) ;; *) usage; exit 2 ;; esac

# SSH·systemd·수동 실행이 같은 신뢰 설정 파일을 공유함. 빈 경로는 격리 테스트 전용.
# [치환] /etc/app/ 의 app 을 프로젝트 앱키로 변경 (예: /etc/myapp/prod-backup.env).
config="${BACKUP_ENV_FILE-/etc/app/prod-backup.env}"
if [[ -n "$config" ]]; then
  [[ -r "$config" ]] || { echo "backup configuration is not readable: $config" >&2; exit 2; }
  set -a
  source "$config"
  set +a
fi

# 필수 설정 — 기본값을 두지 않고 설정 파일에서 명시적으로 받음.
: "${BACKUP_ROOT:?BACKUP_ROOT must be explicitly configured}"
: "${DB_CONTAINER:?DB_CONTAINER must be explicitly configured}"
: "${UPLOAD_VOLUME:?UPLOAD_VOLUME must be explicitly configured}"
[[ "$BACKUP_ROOT" == /* && "$BACKUP_ROOT" != / ]] || { echo 'invalid backup root' >&2; exit 2; }
BACKUP_RETENTION_DAILY="${BACKUP_RETENTION_DAILY:-7}"
[[ "$BACKUP_RETENTION_DAILY" =~ ^[1-9][0-9]{0,3}$ ]] || { echo 'invalid daily retention' >&2; exit 2; }
case "$reason" in scheduled) tier=daily ;; pre-deploy) tier=pre-deploy ;; manual) tier=manual ;; esac
DOCKER_COMMAND="${DOCKER_COMMAND:-docker}"
ARCHIVE_IMAGE="${ARCHIVE_IMAGE:-alpine:3.20}"
[[ "${TRANSFER_ENABLED:-false}" == false ]] || { echo 'external transfer is disabled by policy' >&2; exit 2; }

mkdir -p -- "$BACKUP_ROOT"
[[ ! -L "$BACKUP_ROOT" && -O "$BACKUP_ROOT" ]] || { echo 'backup root must be owned by executor and not a symlink' >&2; exit 2; }
BACKUP_ROOT="$(cd "$BACKUP_ROOT" && pwd -P)"
[[ "$BACKUP_ROOT" != / && "$BACKUP_ROOT" != "$HOME" ]] || exit 2
chmod 700 "$BACKUP_ROOT"
for path in "$BACKUP_ROOT/.backup.lock" "$BACKUP_ROOT/audit.log" "$BACKUP_ROOT/.staging" "$BACKUP_ROOT/$tier"; do
  [[ ! -L "$path" ]] || { echo 'symlink in backup control paths' >&2; exit 2; }
done
exec 9>"$BACKUP_ROOT/.backup.lock"
if ! flock -n 9; then echo 'another backup is running' >&2; exit 75; fi
mkdir -p "$BACKUP_ROOT/.staging" "$BACKUP_ROOT/$tier"
chmod 700 "$BACKUP_ROOT/.staging" "$BACKUP_ROOT/$tier"
audit="$BACKUP_ROOT/audit.log"
touch "$audit"
chmod 600 "$audit" "$BACKUP_ROOT/.backup.lock"
record() { printf '%s reason=%s tier=%s %s\n' "$(date -u +%FT%TZ)" "$reason" "$tier" "$*" >> "$audit"; }
record 'event=start'
record 'event=lock status=acquired'
record 'event=policy encryption=disabled transfer=disabled restore_rehearsal=skipped'
stage=""
cleanup() {
  local status=$?
  trap - EXIT
  if [[ -n "$stage" && -d "$stage" ]]; then rm -rf -- "$stage"; fi
  if ((status == 0)); then record 'event=complete status=success'; else record "event=complete status=failed exit=$status"; fi
  exit "$status"
}
trap cleanup EXIT

# 선검사 — 볼륨명 오타로 Docker가 빈 볼륨을 생성하는 상황을 차단함.
"$DOCKER_COMMAND" volume inspect "$UPLOAD_VOLUME" >/dev/null
if [[ -z "${PG_DUMP_COMMAND:-}" || -z "${PG_RESTORE_COMMAND:-}" ]]; then
  "$DOCKER_COMMAND" exec "$DB_CONTAINER" pg_dump --version >/dev/null
  "$DOCKER_COMMAND" exec "$DB_CONTAINER" pg_restore --version >/dev/null
fi

# 생성 규칙에 맞는 이름만, 선택된 비심볼릭링크 tier 안에서만 삭제함.
remove_set() {
  local dir="$1" name="$2"
  [[ "$dir" == "$BACKUP_ROOT/$tier" && ! -L "$dir" && "$name" =~ ^[0-9]{8}T[0-9]{6}Z-[0-9]+$ && -d "$dir/$name" && ! -L "$dir/$name" ]] || return 2
  rm -rf -- "$dir/$name"
  record "event=retention removed=$name"
}
list_sets() {
  find "$BACKUP_ROOT/$tier" -mindepth 1 -maxdepth 1 -type d -printf '%f\n' |
    sort -r | while IFS= read -r name; do
      if [[ "$name" =~ ^[0-9]{8}T[0-9]{6}Z-[0-9]+$ ]]; then printf '%s\n' "$name"; fi
    done
}
if [[ "$reason" == pre-deploy ]]; then
  mapfile -t old_sets < <(list_sets)
  for name in "${old_sets[@]}"; do remove_set "$BACKUP_ROOT/$tier" "$name"; done
  record 'event=pre-deploy-clear status=success order=before-backup'
fi

stamp="$(date -u +%Y%m%dT%H%M%SZ)-$$"
stage="$BACKUP_ROOT/.staging/$stamp"
mkdir "$stage"
dump="$stage/postgres.dump"
uploads="$stage/uploads.tar.gz"
if [[ -n "${PG_DUMP_COMMAND:-}" ]]; then
  "$PG_DUMP_COMMAND" -Fc -f "$dump"
else
  "$DOCKER_COMMAND" exec "$DB_CONTAINER" sh -c 'exec pg_dump -U "$POSTGRES_USER" -d "$POSTGRES_DB" -Fc' > "$dump"
fi
[[ -s "$dump" ]]
record 'event=pg_dump status=success format=custom'
"$DOCKER_COMMAND" run --rm --network none -v "$UPLOAD_VOLUME:/source:ro" "$ARCHIVE_IMAGE" tar -czf - -C /source . > "$uploads"
[[ -s "$uploads" ]]
( cd "$stage" && sha256sum postgres.dump uploads.tar.gz > manifest.sha256 && sha256sum -c manifest.sha256 >/dev/null )
record 'event=manifest status=success'
if [[ -n "${PG_RESTORE_COMMAND:-}" ]]; then
  "$PG_RESTORE_COMMAND" --list "$dump" >/dev/null
else
  "$DOCKER_COMMAND" exec -i "$DB_CONTAINER" pg_restore --list < "$dump" >/dev/null
fi
tar -tzf "$uploads" >/dev/null
record 'event=read_validation pg_restore_list=pass archive_list=pass restore_rehearsal=skipped'
chmod 600 "$dump" "$uploads" "$stage/manifest.sha256"
target="$BACKUP_ROOT/$tier/$stamp"
mv -T "$stage" "$target"
stage=""
record "event=publish status=success set=$target"
if [[ "$tier" == daily ]]; then
  mapfile -t sets < <(list_sets)
  for ((i=BACKUP_RETENTION_DAILY; i<${#sets[@]}; i++)); do remove_set "$BACKUP_ROOT/$tier" "${sets[i]}"; done
fi
record 'event=transfer status=disabled'
printf '%s\n' "$target"
