import React from 'react';
import styles from '../styles/AdminSidebar.module.css';

function AdminSidebar({ onModuleChange }) {
  return (
    <div className={styles.sidebar}>
      <h2>Módulos</h2>
      <ul>
        <li>
          <button onClick={() => onModuleChange('inventory')}>Inventario</button>
        </li>
        {/* Agrega más botones para otros módulos */}
      </ul>
    </div>
  );
}

export default AdminSidebar;