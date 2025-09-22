import React from 'react';
import {
  Bars3Icon,
  ArchiveBoxIcon,
  UsersIcon,
  Cog6ToothIcon,
  ArrowLeftOnRectangleIcon,
} from '@heroicons/react/24/outline';

function AdminSidebar({ onModuleChange, activeModule, isExpanded, toggleSidebar, sidebarRef, userRole }) {
  const isAdmin = userRole === 'ADMIN';

  return (
    <div 
      className={`fixed top-0 left-0 h-full bg-gray-900 text-white flex flex-col transition-all duration-300 z-40
      ${isExpanded ? 'w-64' : 'w-20'}`}
      ref={sidebarRef}
    >
      <div className={`p-4 flex items-center mb-6 h-16 ${isExpanded ? 'justify-start' : 'justify-center'}`}>
        <button onClick={toggleSidebar} className="text-gray-400 hover:text-white transition-colors">
          <Bars3Icon className="h-6 w-6 text-xl" />
        </button>
        {isExpanded && <span className="ml-4 text-xl font-bold">Módulos</span>}
      </div>

      <ul className="flex-1 overflow-y-auto space-y-2">
        <li>
          <button
            onClick={() => onModuleChange('inventory')}
            className={`w-full flex items-center py-3 px-4 rounded-lg transition-colors
              ${activeModule === 'inventory' ? 'bg-gray-700 text-white' : 'text-gray-400 hover:bg-gray-800'}`}
          >
            <ArchiveBoxIcon className="h-6 w-6 text-lg" />
            {isExpanded && <span className="ml-4 font-medium">Inventario</span>}
          </button>
        </li>
        {isAdmin && (
          <li>
            <button
              onClick={() => onModuleChange('users')}
              className={`w-full flex items-center py-3 px-4 rounded-lg transition-colors
                ${activeModule === 'users' ? 'bg-gray-700 text-white' : 'text-gray-400 hover:bg-gray-800'}`}
            >
              <UsersIcon className="h-6 w-6 text-lg" />
              {isExpanded && <span className="ml-4 font-medium">Usuarios</span>}
            </button>
          </li>
        )}
        {isAdmin && (
          <li>
            <button
              onClick={() => onModuleChange('settings')}
              className={`w-full flex items-center py-3 px-4 rounded-lg transition-colors
                ${activeModule === 'settings' ? 'bg-gray-700 text-white' : 'text-gray-400 hover:bg-gray-800'}`}
            >
              <Cog6ToothIcon className="h-6 w-6 text-lg" />
              {isExpanded && <span className="ml-4 font-medium">Configuración</span>}
            </button>
          </li>
        )}
        <li>
          <button
            onClick={() => onModuleChange('logout')}
            className={`w-full flex items-center py-3 px-4 rounded-lg transition-colors
              ${activeModule === 'logout' ? 'bg-gray-700 text-white' : 'text-gray-400 hover:bg-gray-800'}`}
          >
            <ArrowLeftOnRectangleIcon className="h-6 w-6 text-lg" />
            {isExpanded && <span className="ml-4 font-medium">Cerrar Sesión</span>}
          </button>
        </li>
      </ul>
    </div>
  );
}

export default AdminSidebar;