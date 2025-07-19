import { createContext } from 'react';

export type AuthContextType = {
  isAuthenticated: boolean;
  token: string | null;
  login: (data: any) => Promise<void>; // Usando 'any' por enquanto
  logout: () => void;
};

export const AuthContext = createContext<AuthContextType | undefined>(undefined);