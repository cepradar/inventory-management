// src/App.js
import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Login from './components/Login';
import Dashboard from './components/Dashboard'; // This is your AdminLayout now
import UserPage from './components/UserPage';
import ProtectedRoute from './components/ProtectedRoute';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/Login" element={<Login />} />
        {/* The main admin/dashboard page, protected for ADMIN role */}
        <Route path="/admin" element={<ProtectedRoute role="ADMIN"><Dashboard /></ProtectedRoute>} />
        {/* User-specific page, protected for USER role */}
        <Route path="/user" element={<ProtectedRoute role="USER"><UserPage /></ProtectedRoute>} />
        {/* Redirect any other path to login, or a default landing if preferred */}
        <Route path="*" element={<Login />} />
      </Routes>
    </Router>
  );
}

export default App;