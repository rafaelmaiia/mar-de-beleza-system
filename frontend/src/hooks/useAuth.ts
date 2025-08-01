import { useContext } from 'react';
import { AuthContext, AuthContextType } from '../contexts/AuthContext'; 

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth deve ser usado dentro de um AuthProvider');
  }
  return context;
}