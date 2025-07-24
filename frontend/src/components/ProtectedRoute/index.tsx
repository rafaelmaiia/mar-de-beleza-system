import { Navigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

type ProtectedRouteProps = {
  children: React.ReactNode;
  allowedRoles?: string[];
};

export function ProtectedRoute({ children, allowedRoles }: ProtectedRouteProps) {
  const { isAuthenticated, user } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  // VERIFICAÇÃO: Se a rota exige perfis específicos E o usuário não tem um deles...
  if (allowedRoles && user && !allowedRoles.includes(user.role)) {
    // Redireciona para o dashboard, pois ele não tem permissão
    return <Navigate to="/dashboard" replace />;
  }

  // Se passou por todas as verificações, mostra a página
  return <>{children}</>;
}