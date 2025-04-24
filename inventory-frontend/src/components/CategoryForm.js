import React from 'react';
import styles from '../styles/CategoryForm.module.css';

function CategoryForm({ categoryForm, handleInputChange, handleFormSubmit, editingCategoryId, handleCancelEdit }) {
  return (
    <div className={styles['category-form']}>
      <h3>{editingCategoryId ? 'Editar Categoría' : 'Crear Categoría'}</h3>
      <form onSubmit={(e) => handleFormSubmit(e, 'category')}>
        <input
          type="text"
          name="categoryName"
          placeholder="Nombre de la Categoría"
          value={categoryForm.name || ''}
          onChange={handleInputChange}
          autoComplete="off"
        />
        <input
          type="text"
          name="categoryDescription"
          placeholder="Descripción de la Categoría"
          value={categoryForm.description || ''}
          onChange={handleInputChange}
          autoComplete="off"
        />
        <button type="submit">
          {editingCategoryId ? 'Actualizar Categoría' : 'Crear Categoría'}
        </button>
      </form>
      {editingCategoryId && (
        <button onClick={handleCancelEdit} className={styles['cancel-button']}>Cancelar Edición</button>
      )}
    </div>
  );
}

export default CategoryForm;