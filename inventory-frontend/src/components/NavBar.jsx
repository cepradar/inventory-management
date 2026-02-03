// src/components/AdminNavbar.js
import React from "react";
import MenuButtons from "./MenuButtons";
import ProfileMenu from "./ProfileMenu";

export default function AdminNavbar({ activeModule, onProductsClick, onCategoriesClick, onCreateUserClick, onEditUserClick }) {
  const getModuleTitle = () => {
    switch(activeModule) {
      case 'inventory':
        return 'Inventario';
      case 'users':
        return 'Gestión de Usuarios';
      case 'audit':
        return 'Auditoría de Productos';
      case 'sales':
        return 'Ventas de Productos';
      case 'settings':
        return 'Configuración';
      case 'home':
      default:
        return 'Panel de Administración';
    }
  };

  return (
    <header className="flex items-center justify-between bg-gray-200 px-4 py-2">
      {/* Título */}
      <div className="text-lg font-bold">{getModuleTitle()}</div>

      {/* Menú dinámico de botones */}
      <div className="flex-1 flex justify-center">
        <MenuButtons 
          activeModule={activeModule}
          onProductsClick={onProductsClick}
          onCategoriesClick={onCategoriesClick}
          onCreateUserClick={onCreateUserClick}
          onEditUserClick={onEditUserClick}
        />
      </div>

      {/* Menú de usuario */}
      <ProfileMenu />
    </header>
  );
}