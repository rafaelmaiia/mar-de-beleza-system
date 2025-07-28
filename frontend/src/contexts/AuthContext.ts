import { createContext } from 'react';
import type { AuthRequest } from '../dtos/AuthRequest';
import type { Contact } from '../types/contact';

export type AuthUser = {
  id: number;
  name: string;
  email: string;
  role: string;
  contact?: Contact | null;
  specialties?: string[];
};

export type AuthContextType = {
  isAuthenticated: boolean;
  token: string | null;
  user: AuthUser | null;
  login: (data: AuthRequest) => Promise<void>;
  logout: () => void;
};

export const AuthContext = createContext<AuthContextType | undefined>(undefined);