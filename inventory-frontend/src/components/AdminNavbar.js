import React, { useState, useRef, useEffect, useCallback } from 'react';
import styles from '../styles/AdminNavbar.module.css';
import ProfileMenu from './ProfileMenu';

// AdminNavbar optimizado: accesible, confiable y resistente a fallos
// Cambios principales:
// - type="button" en botones para evitar comportamiento submit
// - listener 'pointerdown' para detectar clics fuera de los dropdowns
// - Escape para cerrar dropdowns
// - default no-op para onSubmenuOptionClick (evita errores si padre no manda prop)
// - se pasa userName a ProfileMenu

export default function AdminNavbar({ activeModule = 'inventory', onSubmenuOptionClick = () => {}, userRole = 'ADMIN' }) {
  const [isProductsOpen, setIsProductsOpen] = useState(false);
  const [isCategoriesOpen, setIsCategoriesOpen] = useState(false);
  const productsDropdownRef = useRef(null);
  const categoriesDropdownRef = useRef(null);
  const [userName, setUserName] = useState('');

  const isAdmin = userRole === 'ADMIN';

  // cargar nombre desde localStorage al montar
  useEffect(() => {
    try {
      const stored = localStorage.getItem('userName');
      if (stored) setUserName(stored);
    } catch (err) {
      // localStorage puede fallar en algunos entornos; no rompemos el componente
      console.warn('No se pudo leer userName de localStorage', err);
    }
  }, []);

  const closeAll = useCallback(() => {
    setIsProductsOpen(false);
    setIsCategoriesOpen(false);
  }, []);

  // cerrar al hacer click fuera y cerrar con Escape
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
    <header className={styles.navbar}>
      <div className={styles.navbarBrand}>
        <h1>Panel Administrativo</h1>
      </div>

      {/* Si solo quieres mostrar el menú cuando activeModule === 'inventory', deja la condición */}
      {activeModule === 'inventory' && (
        <nav className={styles.navbarNav} aria-label="Navegación administrativa">
          <ul className={styles.navbarMenu}>
            {/* Productos */}
            <li className={styles.dropdownItem} ref={productsDropdownRef}>
              <button
                type="button"
                className={`${styles['dropdown-button'] || ''} ${isProductsOpen ? styles.active : ''}`}
                aria-haspopup="menu"
                aria-expanded={isProductsOpen}
                onClick={toggleProductsDropdown}
              >
                <i className="fas fa-box" aria-hidden="true" />
                <span className={styles.label}>Productos</span>
              </button>

              {isProductsOpen && (
                <ul className={`${styles['dropdown-content'] || ''} ${isProductsOpen ? styles.show || '' : ''}`} role="menu" aria-label="Opciones de productos">
                  <li>
                    <button type="button" role="menuitem" onClick={() => handleOptionClick('products', 'list')}>
                      <i className="fas fa-list" aria-hidden="true" /> Listar Productos
                    </button>
                  </li>

                  {isAdmin && (
                    <li>
                      <button type="button" role="menuitem" onClick={() => handleOptionClick('products', 'add')}>
                        <i className="fas fa-plus" aria-hidden="true" /> Añadir Producto
                      </button>
                    </li>
                  )}
                </ul>
              )}
            </li>

            {/* Categorías */}
            <li className={styles.dropdownItem} ref={categoriesDropdownRef}>
              <button
                type="button"
                className={`${styles['dropdown-button'] || ''} ${isCategoriesOpen ? styles.active : ''}`}
                aria-haspopup="menu"
                aria-expanded={isCategoriesOpen}
                onClick={toggleCategoriesDropdown}
              >
                <i className="fas fa-tags" aria-hidden="true" />
                <span className={styles.label}>Categorías</span>
              </button>

              {isCategoriesOpen && (
                <ul className={`${styles['dropdown-content'] || ''} ${isCategoriesOpen ? styles.show || '' : ''}`} role="menu" aria-label="Opciones de categorías">
                  <li>
                    <button type="button" role="menuitem" onClick={() => handleOptionClick('categories', 'list')}>
                      <i className="fas fa-list" aria-hidden="true" /> Listar Categorías
                    </button>
                  </li>

                  {isAdmin && (
                    <li>
                      <button type="button" role="menuitem" onClick={() => handleOptionClick('categories', 'add')}>
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

      <div className={styles.profileSection}>
        <span className={styles.welcome}>Bienvenido, {userName || 'Administrador'}</span>
        <ProfileMenu userName={userName} />
      </div>
    </header>
  );
}
