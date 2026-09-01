import { QueryClientProvider } from '@tanstack/react-query';

import { queryClient } from '@/lib/queryClient';
import { AppRoutes } from '@/routes/AppRoutes';
import { ToastViewport } from '@/components/ui/Toast';

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <AppRoutes />
      <ToastViewport />
    </QueryClientProvider>
  );
}
