// src/components/AdminNavbar.js
import React, { useState, useRef, useEffect } from 'react';
import styles from '../styles/AdminNavbar.module.css';

function AdminNavbar({ activeModule, onSubmenuOptionClick }) { // No longer receives toggleSidebar
  const [isProductsOpen, setIsProductsOpen] = useState(false);
  const [isCategoriesOpen, setIsCategoriesOpen] = useState(false);
  const productsDropdownRef = useRef(null);
  const categoriesDropdownRef = useRef(null);
  const [userName, setUserName] = useState('');

  // Cargar nombre de usuario al montar
  useEffect(() => {
    const storedUserName = localStorage.getItem('userName');
    if (storedUserName) {
      setUserName(storedUserName);
    }
  }, []);

  // Cerrar los dropdowns al hacer clic fuera
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (productsDropdownRef.current && !productsDropdownRef.current.contains(event.target)) {
        setIsProductsOpen(false);
      }
      if (categoriesDropdownRef.current && !categoriesDropdownRef.current.contains(event.target)) {
        setIsCategoriesOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [productsDropdownRef, categoriesDropdownRef]);

  const toggleProductsDropdown = () => {
    setIsProductsOpen(!isProductsOpen);
    setIsCategoriesOpen(false); // Asegurarse de que el otro dropdown esté cerrado
  };

  const toggleCategoriesDropdown = () => {
    setIsCategoriesOpen(!isCategoriesOpen);
    setIsProductsOpen(false); // Asegurarse de que el otro dropdown esté cerrado
  };

  // Función para manejar clics en opciones de submenú y cerrar el dropdown
  const handleOptionClick = (type, action) => {
    onSubmenuOptionClick(type, action);
    setIsProductsOpen(false); // Cerrar dropdown de productos
    setIsCategoriesOpen(false); // Cerrar dropdown de categorías
  };

  return (
    <div className={styles.navbar}>
      <div className={styles.leftSection}>
        {/* El botón de hamburguesa se ha movido a AdminSidebar */}
        <span className={styles.brand}>Admin Panel</span>
      </div>

      {activeModule === 'inventory' && (
        <nav className={styles.modulesNav}>
          <ul>
            {/* Dropdown de Productos */}
            <li className={styles.dropdown} ref={productsDropdownRef}>
              <button
                onClick={toggleProductsDropdown}
                className={`${styles['dropdown-button']} ${isProductsOpen ? styles.active : ''}`}
              >
                <i className="fas fa-box"></i> Productos
              </button>
              {isProductsOpen && (
                <ul className={`${styles['dropdown-content']} ${isProductsOpen ? styles.show : ''}`}>
                  <li>
                    <button onClick={() => handleOptionClick('products', 'list')}>
                      <i className="fas fa-list"></i> Listar Productos
                    </button>
                  </li>
                  <li>
                    <button onClick={() => handleOptionClick('products', 'add')}>
                      <i className="fas fa-plus-circle"></i> Crear Producto
                    </button>
                  </li>
                </ul>
              )}
            </li>

            {/* Dropdown de Categorías MODIFICADO */}
            <li className={styles.dropdown} ref={categoriesDropdownRef}>
              <button
                onClick={toggleCategoriesDropdown}
                className={`${styles['dropdown-button']} ${isCategoriesOpen ? styles.active : ''}`}
              >
                <i className="fas fa-tags"></i> Categorías
              </button>
              {isCategoriesOpen && (
                // *** CONTENIDO DEL DROPDOWN MODIFICADO ***
                <ul className={`${styles['dropdown-content']} ${isCategoriesOpen ? styles.show : ''}`}>
                  <li>
                    {/* Este botón ahora solo "Lista Categorías" (muestra la vista combinada) */}
                    <button onClick={() => handleOptionClick('categories', 'list')}>
                      <i className="fas fa-list"></i> Listar Categorías
                    </button>
                  </li>
                  <li>
                    {/* Botón Adicional: sin lógica por ahora, verticalmente */}
                    <button onClick={() => console.log('Botón Adicional clickeado')} className={styles.additionalButton}>
                      Botón Adicional
                    </button>
                  </li>
                </ul>
                // *** FIN CONTENIDO DEL DROPDOWN MODIFICADO ***
              )}
            </li>
          </ul>
        </nav>
      )}

      <div className={styles.profileSection}>
        Bienvenido, {userName || 'Administrador'}
      </div>
    </div>
  );
}

export default AdminNavbar;