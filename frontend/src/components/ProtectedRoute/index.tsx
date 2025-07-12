import React from 'react';
import { Navigate } from 'react-router-dom';

type ProtectedRouteProps = {
  children: React.ReactNode;
};

export function ProtectedRoute({ children }: ProtectedRouteProps) {
    const token = localStorage.getItem('accessToken');

    const isAuthenticated = token ? true : false;

    if (isAuthenticated) {
        return <>{children}</>;
    } else {
        return <Navigate to="/login" replace />;
    }
}