import { createContext, useState, useContext, ReactNode, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext, AuthContextType } from './AuthContext';
import type { AuthRequest } from '../dtos/AuthRequest';

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(localStorage.getItem('accessToken'));
  const navigate = useNavigate();

  useEffect(() => {
    if (!token) {
      localStorage.removeItem('accessToken');
    }
  }, [token]);

  const login = async (data: AuthRequest) => {
    const response = await fetch('http://localhost:8080/api/v1/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => null);
      throw new Error(errorData?.message || 'Falha no login.');
    }

    const responseData = await response.json();
    localStorage.setItem('accessToken', responseData.token);
    setToken(responseData.token);
    navigate('/dashboard');
  };

  const logout = () => {
    localStorage.removeItem('accessToken');
    setToken(null);
    navigate('/login');
  };

  const value = {
    isAuthenticated: !!token,
    token,
    login,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}