// ProfileMenu.js

import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/ProfileMenu.css';

function ProfileMenu() {
  const navigate = useNavigate();
  const [isOpen, setIsOpen] = useState(false);  // Estado para abrir/cerrar el menú

  const verifyToken = () => {
    const token = localStorage.getItem('authToken');
    if (!token) {
      alert('No estás autorizado. Redirigiendo al login...');
      navigate('/login');
      return null;
    }
    return token;
  };

  const handleLogout = () => {
    localStorage.removeItem('authToken');
    alert('Sesión cerrada.');
    navigate('/login');
  };

  const handlePasswordChange = async () => {
    const token = verifyToken();
    if (!token) return;

    const newPassword = prompt('Introduce tu nueva contraseña:');
    if (!newPassword) return;

    try {
      const response = await fetch('http://localhost:8080/auth/change-password', {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ newPassword }),
      });

      if (!response.ok) {
        throw new Error('Error al cambiar la contraseña');
      }

      const data = await response.json();
      alert(data.message);
    } catch (error) {
      console.error('Error al cambiar la contraseña:', error);
    }
  };

  const handleProfilePictureUpdate = async (event) => {
    const token = verifyToken();
    if (!token) return;

    const file = event.target.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append('file', file);

    try {
      const response = await fetch('http://localhost:8080/auth/update-profile-picture', {
        method: 'POST',
        headers: { Authorization: `Bearer ${token}` },
        body: formData,
      });

      if (!response.ok) {
        throw new Error('Error al actualizar la foto de perfil');
      }

      const data = await response.json();
      alert(data.message);
    } catch (error) {
      console.error('Error al actualizar la foto de perfil:', error);
    }
  };

  // Imagen por defecto en caso de no tener foto de perfil
  const profilePicture = 'path/to/default-avatar.jpg';  // Coloca la ruta de la imagen por defecto aquí

  return (
    <div className="profile-menu-container">
      <div className="profile-icon" onClick={() => setIsOpen(!isOpen)}>
        <img 
          src={profilePicture} 
          alt="Profile" 
          className="profile-pic"
        />
      </div>
      
      {/* Menú desplegable */}
      {isOpen && (
        <div className="profile-menu">
          <ul>
            <li><button onClick={handlePasswordChange}>Cambiar Contraseña</button></li>
            <li>
              <label>
                Actualizar Foto de Perfil
                <input type="file" onChange={handleProfilePictureUpdate} />
              </label>
            </li>
            <li><button onClick={handleLogout}>Cerrar Sesión</button></li>
          </ul>
        </div>
      )}
    </div>
  );
}

export default ProfileMenu;
