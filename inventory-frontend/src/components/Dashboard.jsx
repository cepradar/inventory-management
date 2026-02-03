import React, { useState, useEffect, useRef } from 'react';
import AdminSidebar from './SideBar';
import AdminNavbar from './NavBar';
import CrudManager from './CrudManager';
import UserManager from './UserManager';
import AuditModule from './AuditModule';
import SalesModule from './SalesModule';
import Modal from './Modal';
import ClientManager from './ClientManager';
import IngresoElectrodomestico from './IngresoElectrodomestico';
import OrdenServicio from './OrdenServicio';
import { useNavigate } from 'react-router-dom';
import axios from './utils/axiosConfig';

function Dashboard() {
  const [activeModule, setActiveModule] = useState('home');
  const [activeInventoryView, setActiveInventoryView] = useState(null);
  const [showUserForm, setShowUserForm] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [modalMessage, setModalMessage] = useState('');
  const [modalAction, setModalAction] = useState(() => { });
  const [isSidebarExpanded, setIsSidebarExpanded] = useState(true);
  const [userRole, setUserRole] = useState(null);
  const [companyInfo, setCompanyInfo] = useState(null);
  const [companyLogoUrl, setCompanyLogoUrl] = useState(null);

  const sidebarRef = useRef(null);
  const navRef = useRef(null);
  const [sidebarWidth, setSidebarWidth] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    const role = localStorage.getItem('userRole');
    if (role) setUserRole(role);
    else navigate('/login');
  }, [navigate]);

  useEffect(() => {
    let isMounted = true;
    let objectUrl = null;

    const fetchCompanyInfo = async () => {
      try {
        const infoResponse = await axios.get('/api/company/info');
        if (!isMounted) return;
        setCompanyInfo(infoResponse.data);

        if (infoResponse.data?.id) {
          const logoResponse = await axios.get(`/api/company/${infoResponse.data.id}/logo`, {
            responseType: 'blob',
          });
          if (!isMounted) return;
          objectUrl = URL.createObjectURL(logoResponse.data);
          setCompanyLogoUrl(objectUrl);
        }
      } catch (error) {
        console.warn('No se pudo cargar la información de la empresa:', error);
      }
    };

    fetchCompanyInfo();

    return () => {
      isMounted = false;
      if (objectUrl) {
        URL.revokeObjectURL(objectUrl);
      }
    };
  }, []);

  useEffect(() => {
    function handleClickOutside(event) {
      if (isSidebarExpanded && sidebarRef.current && !sidebarRef.current.contains(event.target)) {
        setIsSidebarExpanded(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [isSidebarExpanded]);

  useEffect(() => {
    // Calcular el ancho esperado sincronicamente basado en isSidebarExpanded
    // 196px cuando está expandido, 44px cuando está colapsado
    const expectedWidth = isSidebarExpanded ? 196 : 44;
    setSidebarWidth(expectedWidth);
  }, [isSidebarExpanded]);

  // Effect adicional para manejar cambios de pantalla (redimensión)
  useEffect(() => {
    const handleResize = () => {
      if (sidebarRef.current) {
        setSidebarWidth(sidebarRef.current.offsetWidth);
      }
    };

    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

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

  const handleProductsClick = () => {
    setActiveModule('inventory');
    setActiveInventoryView('products');
  };

  const handleCategoriesClick = () => {
    setActiveModule('inventory');
    setActiveInventoryView('categories');
  };

  const handleCreateUserClick = () => {
    setActiveModule('users');
    setShowUserForm(true);
  };

  const handleEditUserClick = () => {
    setActiveModule('users');
    setShowUserForm(false);
  };

  const renderContent = () => {
    switch (activeModule) {
      case 'home':
        return (
          <div className="flex items-center justify-center h-full">
            <div className="text-center">
              <div className="mb-8">
                <div className="w-72 h-72 mx-auto bg-gray-200 rounded-2xl flex items-center justify-center border-2 border-gray-300 overflow-hidden">
                  {companyLogoUrl ? (
                    <img
                      src={companyLogoUrl}
                      alt={companyInfo?.razonSocial || 'Logo de la empresa'}
                      className="w-full h-full object-cover"
                    />
                  ) : (
                    <span className="text-gray-500 text-sm font-medium">Logo de la empresa</span>
                  )}
                </div>
              </div>
              <h1 className="text-3xl font-bold text-gray-800 mb-4">Bienvenido</h1>
              <p className="text-gray-600">Selecciona una opción en la barra lateral para comenzar</p>
            </div>
          </div>
        );
      case 'inventory':
        return <CrudManager resourceType={activeInventoryView} userRole={userRole} />;
      case 'users':
        return <UserManager forceShowForm={showUserForm} />;
      case 'audit':
        return <AuditModule />;
      case 'sales':
        return <SalesModule />;
      case 'clients':
        return (
          <div className="bg-white rounded-lg shadow-md p-6">
            <ClientManager />
          </div>
        );
      case 'ordenes-servicio':
        return <OrdenServicio />;
      case 'ingresos':
        return <IngresoElectrodomestico />;
      default:
        return null;
    }
  };

  return (
    <div className="min-h-screen">
      {/* Sidebar fijo */}
      <AdminSidebar
        sidebarRef={sidebarRef}
        onModuleChange={handleModuleChange}
        isExpanded={isSidebarExpanded}
        toggleSidebar={toggleSidebar}
        activeModule={activeModule}
        userRole={userRole}
      />

      {/* Navbar fijo: calculamos offset según ancho real del sidebar */}
      <div
        style={{
          position: 'fixed',
          top: 0,
          left: sidebarWidth,
          right: 0,
          height: 64,
          zIndex: 40,
          transition: 'left 0.3s ease',
        }}
      >
        <AdminNavbar
          navRef={navRef}
          activeModule={activeModule}
          onProductsClick={handleProductsClick}
          onCategoriesClick={handleCategoriesClick}
          onCreateUserClick={handleCreateUserClick}
          onEditUserClick={handleEditUserClick}
          userRole={userRole}
        />
      </div>

      {/* Contenido principal — desplazable dentro del área central */}
      <main
        className="bg-gray-50 flex flex-col"
        style={{
          marginTop: 64,
          marginLeft: sidebarWidth,
          minHeight: `calc(100vh - 64px)`,
          overflowY: 'auto',
          transition: 'margin-left 0.3s ease',
        }}
      >
        <div className="p-4 md:p-8 flex-1">
          {renderContent()}
        </div>
      </main>
    </div>
  );
}

export default Dashboard;
