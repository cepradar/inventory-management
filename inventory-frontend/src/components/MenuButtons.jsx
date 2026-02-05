// src/components/MenuButtons.js
import React from "react";

export default function MenuButtons({ activeModule, onProductsClick, onCategoriesClick, onCreateUserClick, onEditUserClick }) {
  // Mostrar botones según el módulo activo
  if (activeModule === 'inventory') {
    return (
      <nav className="flex items-center flex-nowrap gap-2 overflow-x-auto">
        <button
          className="px-3 md:px-4 py-1.5 md:py-2 rounded-md bg-gray-100 hover:bg-gray-200 whitespace-nowrap font-medium transition-colors text-sm md:text-base"
          onClick={onProductsClick}
        >
          Productos
        </button>
        <button
          className="px-3 md:px-4 py-1.5 md:py-2 rounded-md bg-gray-100 hover:bg-gray-200 whitespace-nowrap font-medium transition-colors text-sm md:text-base"
          onClick={onCategoriesClick}
        >
          Categorías
        </button>
      </nav>
    );
  }

  if (activeModule === 'users') {
    return (
      <nav className="flex items-center flex-nowrap gap-2 overflow-x-auto">
        <button
          className="px-3 md:px-4 py-1.5 md:py-2 rounded-md bg-gray-100 hover:bg-gray-200 whitespace-nowrap font-medium transition-colors text-sm md:text-base"
          onClick={onEditUserClick}
        >
          Listar Usuarios
        </button>
        <button
          className="px-3 md:px-4 py-1.5 md:py-2 rounded-md bg-gray-100 hover:bg-gray-200 whitespace-nowrap font-medium transition-colors text-sm md:text-base"
          onClick={onCreateUserClick}
        >
          Crear Usuario
        </button>
      </nav>
    );
  }

  return null;
}