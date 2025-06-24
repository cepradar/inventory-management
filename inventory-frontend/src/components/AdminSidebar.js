// src/components/AdminSidebar.js
import React from 'react';
import styles from '../styles/AdminSidebar.module.css';

// *** MODIFICADO: Acepta sidebarRef como prop ***
function AdminSidebar({ onModuleChange, activeModule, isExpanded, toggleSidebar, sidebarRef }) {
  return (
    // *** MODIFICADO: Asigna la ref al div principal del sidebar ***
    <div className={`${styles.sidebar} ${isExpanded ? styles.expanded : ''}`} ref={sidebarRef}>
      <h2 className={isExpanded ? styles.expanded : ''}>
        <button onClick={toggleSidebar} className={styles.sidebarHamburger}>
          <i className="fas fa-bars"></i>
        </button>
        {isExpanded && <span className={styles.sidebarTitleText}>Módulos</span>}
      </h2>
      <ul>
        <li>
          <button
            onClick={() => onModuleChange('inventory')}
            className={activeModule === 'inventory' ? styles.active : ''}
          >
            <i className="fas fa-box"></i>
            <span className={styles.sidebarText}>Inventario</span>
          </button>
        </li>
        <li>
          <button
            onClick={() => onModuleChange('users')}
            className={activeModule === 'users' ? styles.active : ''}
          >
            <i className="fas fa-users"></i>
            <span className={styles.sidebarText}>Usuarios</span>
          </button>
        </li>
        <li>
          <button
            onClick={() => onModuleChange('settings')}
            className={activeModule === 'settings' ? styles.active : ''}
          >
            <i className="fas fa-cog"></i>
            <span className={styles.sidebarText}>Configuración</span>
          </button>
        </li>
        <li>
          <button
            onClick={() => onModuleChange('logout')}
            className={activeModule === 'logout' ? styles.active : ''}
          >
            <i className="fas fa-sign-out-alt"></i>
            <span className={styles.sidebarText}>Cerrar Sesión</span>
          </button>
        </li>
      </ul>
    </div>
  );
}

export default AdminSidebar;