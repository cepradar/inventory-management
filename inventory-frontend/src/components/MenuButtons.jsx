// src/components/MenuButtons.js
import React from "react";

export default function MenuButtons({ activeModule, onProductsClick, onCategoriesClick, onCreateUserClick, onEditUserClick }) {
  // Mostrar botones según el módulo activo
  if (activeModule === 'inventory') {
    return (
      <nav className="flex items-center flex-nowrap w-full overflow-hidden gap-2">
        <button
          className="px-4 py-2 rounded-md bg-gray-100 hover:bg-gray-200 whitespace-nowrap font-medium transition-colors"
          onClick={onProductsClick}
        >
          Productos
        </button>
        <button
          className="px-4 py-2 rounded-md bg-gray-100 hover:bg-gray-200 whitespace-nowrap font-medium transition-colors"
          onClick={onCategoriesClick}
        >
          Categorías
        </button>
      </nav>
    );
  }

  if (activeModule === 'users') {
    return (
      <nav className="flex items-center flex-nowrap w-full overflow-hidden gap-2">
        <button
          className="px-4 py-2 rounded-md bg-gray-100 hover:bg-gray-200 whitespace-nowrap font-medium transition-colors"
          onClick={onEditUserClick}
        >
          Listar Usuarios
        </button>
        <button
          className="px-4 py-2 rounded-md bg-gray-100 hover:bg-gray-200 whitespace-nowrap font-medium transition-colors"
          onClick={onCreateUserClick}
        >
          Crear Usuario
        </button>
      </nav>
    );
  }

  return null;
}