import React from 'react';
import { Navigate } from 'react-router-dom';

function ProtectedRoute({ children }) {
  const isAuthenticated = localStorage.getItem('authToken');

  if (!isAuthenticated) {
    // Si no está autenticado, lo envía al login
    return <Navigate to="/login" replace />;
  }

  // Si está autenticado, permite el acceso
  return children;
}

export default ProtectedRoute;