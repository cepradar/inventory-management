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
    <header className="flex flex-col md:flex-row items-center justify-between bg-gray-200 px-2 md:px-4 py-2 gap-2">
      {/* Título */}
      <div className="text-sm md:text-lg font-bold w-full md:w-auto text-center md:text-left">
        {getModuleTitle()}
      </div>

      {/* Menú dinámico de botones */}
      <div className="flex-1 flex justify-center w-full md:w-auto overflow-x-auto">
        <MenuButtons 
          activeModule={activeModule}
          onProductsClick={onProductsClick}
          onCategoriesClick={onCategoriesClick}
          onCreateUserClick={onCreateUserClick}
          onEditUserClick={onEditUserClick}
        />
      </div>

      {/* Menú de usuario */}
      <div className="w-full md:w-auto flex justify-center md:justify-end">
        <ProfileMenu />
      </div>
    </header>
  );
}