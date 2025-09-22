import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './components/auth/Login';
import Dashboard from './components/Dashboard';
import ProtectedRoute from './components/ProtectedRoute';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        
        {/*
          Ruta única y protegida para todos los usuarios.
          El componente Dashboard se encargará de mostrar el contenido
          basado en el rol del usuario que se le pase.
        */}
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          }
        />

        {/* Redirige a /dashboard si está autenticado, de lo contrario al login */}
        <Route path="/" element={<Navigate to="/dashboard" />} />
        
        {/* Manejo de rutas no encontradas */}
        <Route path="*" element={<h1>404: Página no encontrada</h1>} />
        
      </Routes>
    </Router>
  );
}

export default App;