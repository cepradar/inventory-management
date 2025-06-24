// src/App.js
import React from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom'; // Asegúrate de importar Navigate
import Login from './components/Login';
import Dashboard from './components/Dashboard'; // ¡Importa Dashboard!
// import AdminPage from './components/AdminLayout'; // ELIMINA ESTA LÍNEA
import UserPage from './components/UserPage'; // Si tienes una página de usuario
import ProtectedRoute from './components/ProtectedRoute';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/Login" element={<Login />} />
        {/*
          Modifica la ruta /dashboard para que esté PROTEGIDA y use Dashboard.
          La ruta /admin ya no es necesaria y la eliminamos.
        */}
        <Route path="/dashboard" element={<ProtectedRoute role="ADMIN"><Dashboard /></ProtectedRoute>} />

        {/* Si tenías una ruta /admin, ELIMÍNALA. Ya no se usa. */}
        {/* <Route path="/admin" element={<ProtectedRoute role="ADMIN"><AdminPage /></ProtectedRoute>} /> */}

        {/* Ruta protegida para usuarios regulares, si aplica */}
        <Route path="/user" element={<ProtectedRoute role="USER"><UserPage /></ProtectedRoute>} />

        {/* Añade una ruta por defecto que redirija al login o a donde consideres. */}
        <Route path="/" element={<Navigate to="/Login" replace />} />
      </Routes>
    </Router>
  );
}

export default App;