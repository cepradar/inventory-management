import React from 'react';
import {
  Bars3Icon,
  ArchiveBoxIcon,
  UsersIcon,
  Cog6ToothIcon,
  DocumentTextIcon,
  ShoppingCartIcon,
  WrenchScrewdriverIcon,
  UserGroupIcon,
  ClipboardDocumentListIcon,
} from '@heroicons/react/24/outline';

function Sidebar({ onModuleChange, activeModule, isExpanded, toggleSidebar, sidebarRef, userRole }) {
  const isAdmin = userRole && (userRole === 'ADMIN' || userRole.trim().toUpperCase() === 'ADMIN');
  
  // Debug: mostrar rol en consola
  React.useEffect(() => {
    console.log('userRole en SideBar:', userRole, 'isAdmin:', isAdmin);
  }, [userRole, isAdmin]);

  return (
    <div 
    style={{
      width: isExpanded ? '196px' : '44px',
      transition: 'width 0.3s ease'
    }}
    className="fixed inset-y-0 left-0 bg-gray-900 text-white flex flex-col z-50"
    ref={sidebarRef}
  >
      <div className={`p-2 flex items-center ${isExpanded ? 'mb-2 h-14' : 'mb-1 h-14 justify-center'}`}>
        <button onClick={toggleSidebar} className="text-gray-400 hover:text-white transition-colors flex-shrink-0 p-2">
          <Bars3Icon className="h-5 w-5" />
        </button>
        {isExpanded && <span className="ml-2 text-base font-bold whitespace-nowrap">Módulos</span>}
      </div>

      <ul className="flex-1 overflow-y-auto space-y-0.5 px-1">
        <li>
          <button
            onClick={() => onModuleChange('inventory')}
            className={`w-full flex items-center gap-2 py-2 px-2 rounded-md transition-colors
              ${activeModule === 'inventory' ? 'bg-gray-700 text-white' : 'text-gray-400 hover:bg-gray-800'}`}
          >
            <ArchiveBoxIcon className="h-5 w-5 flex-shrink-0" />
            {isExpanded && <span className="text-sm font-medium whitespace-nowrap">Inventario</span>}
          </button>
        </li>

        {isAdmin && (
          <li>
            <button
              onClick={() => onModuleChange('users')}
              className={`w-full flex items-center gap-2 py-2 px-2 rounded-md transition-colors
                ${activeModule === 'users' ? 'bg-gray-700 text-white' : 'text-gray-400 hover:bg-gray-800'}`}
            >
              <UsersIcon className="h-5 w-5 flex-shrink-0" />
              {isExpanded && <span className="text-sm font-medium whitespace-nowrap">Usuarios</span>}
            </button>
          </li>
        )}

        {isAdmin && (
          <li>
            <button
              onClick={() => onModuleChange('audit')}
              className={`w-full flex items-center gap-2 py-2 px-2 rounded-md transition-colors
                ${activeModule === 'audit' ? 'bg-gray-700 text-white' : 'text-gray-400 hover:bg-gray-800'}`}
            >
              <DocumentTextIcon className="h-5 w-5 flex-shrink-0" />
              {isExpanded && <span className="text-sm font-medium whitespace-nowrap">Auditoría</span>}
            </button>
          </li>
        )}

        {isAdmin && (
          <li>
            <button
              onClick={() => onModuleChange('sales')}
              className={`w-full flex items-center gap-2 py-2 px-2 rounded-md transition-colors
                ${activeModule === 'sales' ? 'bg-gray-700 text-white' : 'text-gray-400 hover:bg-gray-800'}`}
            >
              <ShoppingCartIcon className="h-5 w-5 flex-shrink-0" />
              {isExpanded && <span className="text-sm font-medium whitespace-nowrap">Ventas</span>}
            </button>
          </li>
        )}

        {isAdmin && (
          <li>
            <button
              onClick={() => onModuleChange('settings')}
              className={`w-full flex items-center gap-2 py-2 px-2 rounded-md transition-colors
                ${activeModule === 'settings' ? 'bg-gray-700 text-white' : 'text-gray-400 hover:bg-gray-800'}`}
            >
              <Cog6ToothIcon className="h-5 w-5 flex-shrink-0" />
              {isExpanded && <span className="text-sm font-medium whitespace-nowrap">Configuración</span>}
            </button>
          </li>
        )}

        <li>
          <button
            onClick={() => onModuleChange('clients')}
            className={`w-full flex items-center gap-2 py-2 px-2 rounded-md transition-colors
              ${activeModule === 'clients' ? 'bg-gray-700 text-white' : 'text-gray-400 hover:bg-gray-800'}`}
          >
            <UserGroupIcon className="h-5 w-5 flex-shrink-0" />
            {isExpanded && <span className="text-sm font-medium whitespace-nowrap">Clientes</span>}
          </button>
        </li>

        <li>
          <button
            onClick={() => onModuleChange('ordenes-servicio')}
            className={`w-full flex items-center gap-2 py-2 px-2 rounded-md transition-colors
              ${activeModule === 'ordenes-servicio' ? 'bg-gray-700 text-white' : 'text-gray-400 hover:bg-gray-800'}`}
          >
            <ClipboardDocumentListIcon className="h-5 w-5 flex-shrink-0" />
            {isExpanded && <span className="text-sm font-medium whitespace-nowrap">Órdenes de Servicio</span>}
          </button>
        </li>
      </ul>
    </div>
  );
}

export default Sidebar;
