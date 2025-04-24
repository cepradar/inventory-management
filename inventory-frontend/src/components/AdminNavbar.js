import React, { useState, useRef, useEffect } from 'react';
import styles from '../styles/AdminNavbar.module.css';

function AdminNavbar({ activeModule, onSubmenuOptionClick }) {
  const [isProductsOpen, setIsProductsOpen] = useState(false);
  const [isCategoriesOpen, setIsCategoriesOpen] = useState(false);
  const productsDropdownRef = useRef(null);
  const categoriesDropdownRef = useRef(null);

  const toggleProductsDropdown = () => {
    setIsProductsOpen(!isProductsOpen);
    setIsCategoriesOpen(false); // Cerrar el otro dropdown si está abierto
  };

  const toggleCategoriesDropdown = () => {
    setIsCategoriesOpen(!isCategoriesOpen);
    setIsProductsOpen(false); // Cerrar el otro dropdown si está abierto
  };

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

  return (
    <div className={styles.navbar}>
      <h3>
        {activeModule === 'inventory' ? 'Gestión de Inventario' : 'Panel de Administración'}
      </h3>
      {activeModule === 'inventory' && (
        <nav>
          <ul>
            <li className={styles.dropdown} ref={productsDropdownRef}>
              <button onClick={toggleProductsDropdown} className={styles['dropdown-button']}>
                Productos
              </button>
              {isProductsOpen && (
                <ul className={styles['dropdown-content']}>
                  <li>
                    <button onClick={() => onSubmenuOptionClick('products', 'list')}>Lista de Productos</button>
                  </li>
                  <li>
                    <button onClick={() => onSubmenuOptionClick('products', 'add')}>Agregar Producto</button>
                  </li>
                </ul>
              )}
            </li>
            <li className={styles.dropdown} ref={categoriesDropdownRef}>
              <button onClick={toggleCategoriesDropdown} className={styles['dropdown-button']}>
                Categorías
              </button>
              {isCategoriesOpen && (
                <ul className={styles['dropdown-content']}>
                  <li>
                    <button onClick={() => onSubmenuOptionClick('categories', 'list')}>Lista de Categorías</button>
                  </li>
                  <li>
                    <button onClick={() => onSubmenuOptionClick('categories', 'add')}>Crear Categoría</button>
                  </li>
                </ul>
              )}
            </li>
            {/* Aquí se podrían agregar más opciones principales de inventario */}
          </ul>
        </nav>
      )}
    </div>
  );
}

export default AdminNavbar;