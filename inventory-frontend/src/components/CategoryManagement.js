import React from 'react';
import styles from '../styles/CategoryManagement.module.css'; // Crea este CSS

function CategoryManagement({
  categories,
  onEdit,
  onDelete,
  categoryForm,
  handleInputChange,
  handleAddEditSubmit,
  editingCategoryId,
  handleCancelEdit
}) {
  return (
    <div className={styles.categoryManagementContainer}>
      <h2>Gestión de Categorías</h2>

      {/* Sección de Listado de Categorías con Scroll */}
      <div className={styles.tableContainer}>
        <table className={styles.table}>
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Descripción</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {categories && categories.length > 0 ? (
              categories.map(category => (
                <tr key={category.id}>
                  <td data-label="Nombre">{category.name}</td>
                  <td data-label="Descripción">{category.description}</td>
                  <td data-label="Acciones">
                    <button onClick={() => onEdit(category.id)} className={`${styles.actionButton} ${styles.editButton}`}>Editar</button>
                    <button onClick={() => onDelete(category.id)} className={`${styles.actionButton} ${styles.deleteButton}`}>Eliminar</button>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="3" style={{ textAlign: 'center', padding: '20px' }}>No hay categorías registradas.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* Separador visual si lo deseas */}
      <hr className={styles.separator} />

      {/* Sección del Formulario de Creación/Edición de Categoría */}
      <div className={styles['category-form-section']}>
        <h2>{editingCategoryId ? 'Editar Categoría' : 'Crear Nueva Categoría'}</h2>
        <form onSubmit={(e) => handleAddEditSubmit(e, 'category')}>
          <div className={styles['form-group']}>
            <label htmlFor="categoryName">Nombre de la Categoría:</label>
            <input
              type="text"
              id="categoryName"
              name="name"
              placeholder="Nombre de la Categoría"
              value={categoryForm.name || ''}
              onChange={handleInputChange}
              required
              autoComplete="off"
            />
          </div>

          <div className={styles['form-group']}>
            <label htmlFor="categoryDescription">Descripción de la Categoría:</label>
            <textarea
              id="categoryDescription"
              name="description"
              placeholder="Descripción de la Categoría"
              value={categoryForm.description || ''}
              onChange={handleInputChange}
              autoComplete="off"
            ></textarea>
          </div>

          <div className={styles['form-actions']}>
            <button type="submit" className={styles['submit-button']}>
              {editingCategoryId ? 'Actualizar Categoría' : 'Agregar Categoría'}
            </button>
            {editingCategoryId && (
              <button type="button" onClick={handleCancelEdit} className={styles['cancel-button']}>
                Cancelar Edición
              </button>
            )}
          </div>
        </form>
      </div>
    </div>
  );
}

export default CategoryManagement;