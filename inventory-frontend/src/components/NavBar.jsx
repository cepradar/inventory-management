import React, { useState, useRef, useEffect, useCallback } from 'react';
import ProfileMenu from './ProfileMenu';

export default function AdminNavbar({ activeModule = 'inventory', onSubmenuOptionClick = () => {}, userRole = 'ADMIN' }) {
  const [isProductsOpen, setIsProductsOpen] = useState(false);
  const [isCategoriesOpen, setIsCategoriesOpen] = useState(false);
  const productsDropdownRef = useRef(null);
  const categoriesDropdownRef = useRef(null);
  const [userName, setUserName] = useState('');

  const isAdmin = userRole === 'ADMIN';

  useEffect(() => {
    try {
      const stored = localStorage.getItem('userName');
      if (stored) setUserName(stored);
    } catch (err) {
      console.warn('No se pudo leer userName de localStorage', err);
    }
  }, []);

  const closeAll = useCallback(() => {
    setIsProductsOpen(false);
    setIsCategoriesOpen(false);
  }, []);

  useEffect(() => {
    function onPointerDown(e) {
      const t = e.target;
      const clickedInsideProducts = productsDropdownRef.current && productsDropdownRef.current.contains(t);
      const clickedInsideCategories = categoriesDropdownRef.current && categoriesDropdownRef.current.contains(t);

      if (!clickedInsideProducts) setIsProductsOpen(false);
      if (!clickedInsideCategories) setIsCategoriesOpen(false);
    }

    function onKeyDown(e) {
      if (e.key === 'Escape') closeAll();
    }

    document.addEventListener('pointerdown', onPointerDown);
    document.addEventListener('keydown', onKeyDown);

    return () => {
      document.removeEventListener('pointerdown', onPointerDown);
      document.removeEventListener('keydown', onKeyDown);
    };
  }, [closeAll]);

  const toggleProductsDropdown = useCallback((e) => {
    if (e && e.stopPropagation) e.stopPropagation();
    setIsProductsOpen((v) => !v);
    setIsCategoriesOpen(false);
  }, []);

  const toggleCategoriesDropdown = useCallback((e) => {
    if (e && e.stopPropagation) e.stopPropagation();
    setIsCategoriesOpen((v) => !v);
    setIsProductsOpen(false);
  }, []);

  const handleOptionClick = useCallback((type, action) => {
    try {
      onSubmenuOptionClick(type, action);
    } catch (err) {
      console.error('Error calling onSubmenuOptionClick', err);
    }
    closeAll();
  }, [onSubmenuOptionClick, closeAll]);

  return (
    <header className="bg-white shadow-md p-4 flex justify-between items-center z-30 relative">
      <div className="flex items-center">
        <h1 className="text-xl font-bold text-gray-800 hidden md:block">Panel Administrativo</h1>
      </div>

      {activeModule === 'inventory' && (
        <nav className="flex-grow flex justify-center" aria-label="Navegación administrativa">
          <ul className="flex items-center space-x-4">
            <li className="relative" ref={productsDropdownRef}>
              <button
                type="button"
                className={`flex items-center space-x-2 px-4 py-2 rounded-md font-medium transition-colors
                  ${isProductsOpen ? 'bg-gray-200 text-gray-900' : 'text-gray-600 hover:bg-gray-100 hover:text-gray-900'}`}
                aria-haspopup="menu"
                aria-expanded={isProductsOpen}
                onClick={toggleProductsDropdown}
              >
                <i className="fas fa-box" aria-hidden="true" />
                <span>Productos</span>
                <i className={`fas ${isProductsOpen ? 'fa-chevron-up' : 'fa-chevron-down'} text-xs ml-1`} />
              </button>

              {isProductsOpen && (
                <ul className="absolute top-full mt-2 w-48 bg-white border border-gray-200 rounded-md shadow-lg z-50 overflow-hidden" role="menu" aria-label="Opciones de productos">
                  <li>
                    <button 
                      type="button" 
                      role="menuitem" 
                      onClick={() => handleOptionClick('products', 'list')}
                      className="w-full text-left flex items-center space-x-2 p-3 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
                    >
                      <i className="fas fa-list" aria-hidden="true" /> Listar Productos
                    </button>
                  </li>

                  {isAdmin && (
                    <li>
                      <button 
                        type="button" 
                        role="menuitem" 
                        onClick={() => handleOptionClick('products', 'add')}
                        className="w-full text-left flex items-center space-x-2 p-3 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
                      >
                        <i className="fas fa-plus" aria-hidden="true" /> Añadir Producto
                      </button>
                    </li>
                  )}
                </ul>
              )}
            </li>

            <li className="relative" ref={categoriesDropdownRef}>
              <button
                type="button"
                className={`flex items-center space-x-2 px-4 py-2 rounded-md font-medium transition-colors
                  ${isCategoriesOpen ? 'bg-gray-200 text-gray-900' : 'text-gray-600 hover:bg-gray-100 hover:text-gray-900'}`}
                aria-haspopup="menu"
                aria-expanded={isCategoriesOpen}
                onClick={toggleCategoriesDropdown}
              >
                <i className="fas fa-tags" aria-hidden="true" />
                <span>Categorías</span>
                <i className={`fas ${isCategoriesOpen ? 'fa-chevron-up' : 'fa-chevron-down'} text-xs ml-1`} />
              </button>

              {isCategoriesOpen && (
                <ul className="absolute top-full mt-2 w-48 bg-white border border-gray-200 rounded-md shadow-lg z-50 overflow-hidden" role="menu" aria-label="Opciones de categorías">
                  <li>
                    <button 
                      type="button" 
                      role="menuitem" 
                      onClick={() => handleOptionClick('categories', 'list')}
                      className="w-full text-left flex items-center space-x-2 p-3 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
                    >
                      <i className="fas fa-list" aria-hidden="true" /> Listar Categorías
                    </button>
                  </li>

                  {isAdmin && (
                    <li>
                      <button 
                        type="button" 
                        role="menuitem" 
                        onClick={() => handleOptionClick('categories', 'add')}
                        className="w-full text-left flex items-center space-x-2 p-3 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
                      >
                        <i className="fas fa-plus" aria-hidden="true" /> Añadir Categoría
                      </button>
                    </li>
                  )}
                </ul>
              )}
            </li>
          </ul>
        </nav>
      )}

      <div className="flex items-center space-x-4">
        <span className="text-gray-600 text-sm hidden md:block">Bienvenido, {userName || 'Administrador'}</span>
        <ProfileMenu userName={userName} />
      </div>
    </header>
  );
}