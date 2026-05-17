// src/components/AdminNavbar.js
import React from "react";
import { HomeIcon } from "@heroicons/react/24/outline";
import ProfileMenu from "./ProfileMenu";

export default function AdminNavbar({ activeModule, onHomeClick, companyName }) {
  const getModuleTitle = () => {
    switch(activeModule) {
      case 'inventory':
        return 'Inventario';
      case 'users':
        return 'Usuarios';
      case 'audit':
        return 'Auditoría';
      case 'sales':
        return 'Ventas';
      case 'clients':
        return 'Clientes';
      case 'ordenes-servicio':
        return 'Órdenes de Servicio';
      case 'settings':
        return 'Configuración';
      case 'home':
      default:
        return companyName || 'Panel de Administración';
    }
  };

  return (
    <header className="flex flex-col md:flex-row items-center justify-between bg-gray-200 px-2 md:px-4 py-2 gap-2">
      {/* Título */}
      <div className="text-sm md:text-lg font-bold w-full md:w-auto text-center md:text-left">
        <span className="inline-flex items-center justify-center gap-2">
          <button
            type="button"
            onClick={onHomeClick}
            className="inline-flex items-center justify-center rounded-md p-1 text-gray-700 hover:text-gray-900 hover:bg-gray-300 transition-colors"
            aria-label="Ir a inicio"
          >
            <HomeIcon className="h-5 w-5" />
          </button>
          <span>{getModuleTitle()}</span>
        </span>
      </div>

      {/* Menú de usuario */}
      <div className="w-full md:w-auto flex justify-center md:justify-end">
        <ProfileMenu />
      </div>
    </header>
  );
}