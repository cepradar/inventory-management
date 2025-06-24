// src/components/ProtectedRoute.js
import React from 'react';
import { Navigate } from 'react-router-dom';
// NO es necesario jwtDecode si ya guardas el rol en localStorage
// import { jwtDecode } from 'jwt-decode'; // Puedes eliminar esta importación si no la usas más

function ProtectedRoute({ children, role }) {
  // Usar 'authToken' para verificar la existencia del token
  const token = localStorage.getItem('authToken');
  // Usar 'userRole' para verificar el rol del usuario
  const userRole = localStorage.getItem('userRole');

  if (!token) {
    // Si no hay token, redirige al login
    return <Navigate to="/login" replace />;
  }

  // Si se requiere un rol específico y el usuario no tiene ese rol
  // Nota: Esto asume que el userRole en localStorage es el valor exacto que esperas (ej. "ADMIN")
  if (role && userRole !== role) {
    // Podrías redirigir a una página de "Acceso Denegado" o al login
    alert('Acceso denegado. No tienes los permisos necesarios.');
    localStorage.clear(); // Limpia la sesión si el rol no coincide
    return <Navigate to="/login" replace />;
  }

  // Si todo está bien, renderiza los componentes hijos (la ruta protegida)
  return children;
}

export default ProtectedRoute;