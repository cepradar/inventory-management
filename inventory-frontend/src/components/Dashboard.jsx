import React, { useState, useEffect, useRef } from 'react';
import AdminSidebar from './Sidebar';
import AdminNavbar from './Navbar';
import CrudManager from './CrudManager';
import Modal from './Modal';
import { useNavigate } from 'react-router-dom';

function Dashboard() {
  const [activeModule, setActiveModule] = useState('inventory');
  const [activeInventoryView, setActiveInventoryView] = useState('products');
  const [showModal, setShowModal] = useState(false);
  const [modalMessage, setModalMessage] = useState('');
  const [modalAction, setModalAction] = useState(() => {});
  const [isSidebarExpanded, setIsSidebarExpanded] = useState(true);
  const [userRole, setUserRole] = useState(null);

  const sidebarRef = useRef(null);
  const navigate = useNavigate();

  useEffect(() => {
    const role = localStorage.getItem('userRole');
    if (role) setUserRole(role);
    else navigate('/login');
  }, [navigate]);

  useEffect(() => {
    function handleClickOutside(event) {
      if (isSidebarExpanded && sidebarRef.current && !sidebarRef.current.contains(event.target)) {
        setIsSidebarExpanded(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [isSidebarExpanded]);

  const toggleSidebar = () => setIsSidebarExpanded(!isSidebarExpanded);

  const handleModuleChange = (module) => {
    setActiveModule(module);
    if (module === 'logout') {
      setShowModal(true);
      setModalMessage('¿Estás seguro de que quieres cerrar sesión?');
      setModalAction(() => () => {
        localStorage.clear();
        navigate('/login');
        setShowModal(false);
      });
    } else if (module === 'inventory') {
      setActiveInventoryView('products');
    }
  };

  const handleInventoryOptionClick = (type) => setActiveInventoryView(type === 'products' ? 'products' : 'categories');

  const renderContent = () => {
    switch (activeModule) {
      case 'inventory':
        return <CrudManager resourceType={activeInventoryView} userRole={userRole} />;
      case 'users':
        return (
          <div className="text-center mt-12 text-lg text-gray-600">
            <h2 className="text-2xl font-semibold mb-2">{userRole === 'ADMIN' ? 'Gestión de Usuarios' : 'Mi Perfil'}</h2>
            <p>{userRole === 'ADMIN' ? 'Funcionalidad en desarrollo. Aquí podrías ver y editar usuarios.' : 'Solo puedes ver tu propio perfil.'}</p>
          </div>
        );
      case 'settings':
        return (
          <div className={`text-center mt-12 text-lg ${userRole === 'ADMIN' ? 'text-gray-600' : 'text-red-500'}`}>
            <h2 className="text-2xl font-semibold mb-2">{userRole === 'ADMIN' ? 'Configuración de la Aplicación' : 'Acceso Denegado'}</h2>
            <p>{userRole === 'ADMIN' ? 'Funcionalidad en desarrollo. Ajustes globales.' : 'No tienes permisos para ver esta sección.'}</p>
          </div>
        );
      default:
        return (
          <div className="text-center mt-12 text-lg text-gray-600">
            <h2 className="text-2xl font-semibold mb-2">Bienvenido al Panel de Administración</h2>
            <p>Selecciona una opción del menú o de la barra superior para comenzar.</p>
          </div>
        );
    }
  };

  return (
    <div className={`flex min-h-screen ${isSidebarExpanded ? 'lg:pl-64' : 'lg:pl-20'} transition-all duration-300`}>
      <AdminSidebar
        sidebarRef={sidebarRef}
        onModuleChange={handleModuleChange}
        isExpanded={isSidebarExpanded}
        toggleSidebar={toggleSidebar}
        activeModule={activeModule}
        userRole={userRole}
      />
      <div className="flex-1 flex flex-col transition-all duration-300">
        <AdminNavbar
          activeModule={activeModule}
          onSubmenuOptionClick={handleInventoryOptionClick}
          userRole={userRole}
        />
        <main className="flex-1 p-8 bg-gray-50 overflow-auto">
          {renderContent()}
        </main>
      </div>

      {showModal && (
        <Modal message={modalMessage} onConfirm={modalAction} onCancel={() => setShowModal(false)} />
      )}
    </div>
  );
}

export default Dashboard;
