import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './components/auth/Login';
import Dashboard from './components/Dashboard';
import ProtectedRoute from './components/ProtectedRoute';
import LandingPage from './components/LandingPage';

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

        <Route path="/" element={<LandingPage />} />
        
        {/* Manejo de rutas no encontradas */}
        <Route path="*" element={<h1>404: Página no encontrada</h1>} />
        
      </Routes>
    </Router>
  );
}

export default App;