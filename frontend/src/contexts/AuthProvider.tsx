import { useState, ReactNode, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext, AuthUser } from './AuthContext';
import type { AuthRequest } from '../dtos/AuthRequest';


const decodeToken = (token: string): { sub: string, fullName: string, userId: number, role: string } | null => {
  try {
    return JSON.parse(atob(token.split('.')[1]));
  } catch (error) {
    return null;
  }
};

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(localStorage.getItem('accessToken'));
  const [user, setUser] = useState<AuthUser | null>(null);
  
  const navigate = useNavigate();

  useEffect(() => {
    const localToken = localStorage.getItem('accessToken');
    if (localToken) {
      const decoded = decodeToken(localToken);
      if (decoded) {
        setUser({ 
          id: decoded.userId, 
          name: decoded.fullName, 
          email: decoded.sub, 
          role: decoded.role 
        });
      }
      setToken(localToken);
    }
  }, []);

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
    const newToken = responseData.token;
    localStorage.setItem('accessToken', newToken);
    
    const decoded = decodeToken(newToken);
    if (decoded) {
      setUser({ 
        id: decoded.userId, 
        name: decoded.fullName, 
        email: decoded.sub, 
        role: decoded.role 
      });
    }
    setToken(newToken);
    navigate('/dashboard');
  };

  const logout = () => {
    localStorage.removeItem('accessToken');
    setToken(null);
    setUser(null);
    navigate('/login');
  };

  const value = { isAuthenticated: !!token, token, user, login, logout };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}