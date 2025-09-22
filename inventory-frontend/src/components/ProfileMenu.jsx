import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from './utils/axiosConfig';

function ProfileMenu() {
  const navigate = useNavigate();
  const [isOpen, setIsOpen] = useState(false);
  const menuRef = useRef(null);

  // Cierra el menú si se hace clic fuera de él
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [menuRef]);

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
    localStorage.clear();
    alert('Sesión cerrada.');
    navigate('/login');
  };

  const handlePasswordChange = async () => {
    setIsOpen(false);
    const token = verifyToken();
    if (!token) return;

    const newPassword = prompt('Introduce tu nueva contraseña:');
    if (!newPassword) return;

    try {
      const response = await axios.post('/auth/change-password', { newPassword });
      alert(response.data.message);
    } catch (error) {
      console.error('Error al cambiar la contraseña:', error);
      alert('Error al cambiar la contraseña.');
    }
  };

  const handleProfilePictureUpdate = async (event) => {
    setIsOpen(false);
    const token = verifyToken();
    if (!token) return;

    const file = event.target.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append('file', file);

    try {
      const response = await axios.post('/auth/update-profile-picture', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      alert(response.data.message);
    } catch (error) {
      console.error('Error al actualizar la foto de perfil:', error);
      alert('Error al actualizar la foto de perfil.');
    }
  };

  const profilePicture = 'https://i.ibb.co/L5T9W5W/default-avatar.jpg';

  return (
    <div className="relative inline-block text-left" ref={menuRef}>
      <div>
        <button
          type="button"
          className="flex items-center justify-center h-10 w-10 rounded-full bg-gray-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-gray-100 focus:ring-blue-500 transition-transform duration-200 transform hover:scale-105"
          onClick={() => setIsOpen(!isOpen)}
        >
          <img
            src={profilePicture}
            alt="Profile"
            className="h-full w-full object-cover rounded-full"
          />
        </button>
      </div>

      {isOpen && (
        <div className="origin-top-right absolute right-0 mt-2 w-56 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 divide-y divide-gray-100 focus:outline-none z-50 animate-fade-in-down" role="menu" aria-orientation="vertical" aria-labelledby="options-menu">
          <div className="py-1" role="none">
            <button
              onClick={handlePasswordChange}
              className="text-gray-700 block w-full text-left px-4 py-2 text-sm hover:bg-gray-100"
              role="menuitem"
            >
              Cambiar Contraseña
            </button>
            <label
              className="text-gray-700 block w-full text-left px-4 py-2 text-sm hover:bg-gray-100 cursor-pointer"
              role="menuitem"
            >
              Actualizar Foto de Perfil
              <input
                type="file"
                onChange={handleProfilePictureUpdate}
                className="hidden"
              />
            </label>
          </div>
          <div className="py-1" role="none">
            <button
              onClick={handleLogout}
              className="text-gray-700 block w-full text-left px-4 py-2 text-sm hover:bg-gray-100"
              role="menuitem"
            >
              Cerrar Sesión
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

export default ProfileMenu;