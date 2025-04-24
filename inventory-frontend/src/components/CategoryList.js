import React from 'react';
import styles from '../styles/CategoryList.module.css';

function CategoryList({ categories, onEdit, onDelete }) {
  return (
    <div className={styles['category-list']}>
      <h3>Lista de Categorías</h3>
      <table>
        <thead>
          <tr>
            <th>Nombre</th>
            <th>Descripción</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {categories && categories.map(category => (
            <tr key={category.id}>
              <td>{category.name}</td>
              <td>{category.description}</td>
              <td>
                <button onClick={() => onEdit(category.id)} className={styles['edit-button']}>Editar</button>
                <button onClick={() => onDelete(category.id)} className={styles['delete-button']}>Eliminar</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default CategoryList;