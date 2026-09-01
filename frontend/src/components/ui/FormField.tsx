import { forwardRef, type InputHTMLAttributes, type ReactNode } from 'react';

export interface FormFieldProps extends InputHTMLAttributes<HTMLInputElement> {
  label: ReactNode;
  required?: boolean;
  error?: string;
}

/** 라벨 + input + 에러메시지를 묶은 공통 폼 필드(.form-field, components.css). */
export const FormField = forwardRef<HTMLInputElement, FormFieldProps>(
  ({ label, required, error, id, ...props }, ref) => {
    const inputId = id ?? props.name;
    return (
      <div className="form-field">
        <label htmlFor={inputId}>
          {label}
          {required && <span className="req">*</span>}
        </label>
        <input ref={ref} id={inputId} {...props} />
        {error && <p className="field-error">{error}</p>}
      </div>
    );
  },
);
FormField.displayName = 'FormField';
