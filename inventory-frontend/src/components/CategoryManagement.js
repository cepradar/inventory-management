import React from 'react';
import styles from '../styles/CategoryManagement.module.css';

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
  const userRole = localStorage.getItem('userRole'); // Get user role
  const isAdmin = userRole === 'ADMIN';

  return (
    <div className={styles.categoryManagementContainer}>
      <h2>Gestión de Categorías</h2>

      {/* Sección de Formulario de Categorías (solo para ADMIN) */}
      {isAdmin && (
        <div className={styles.formSection}>
          <h3>{editingCategoryId ? 'Editar Categoría' : 'Añadir Nueva Categoría'}</h3>
          <form onSubmit={(e) => handleAddEditSubmit(e, 'category')}>
            {/* Note: The 'Id' field in CategoryForm.js was previously named 'name' and linked to categoryForm.name.
                I'm assuming you intended to have a separate ID input or that the ID is auto-generated.
                If ID is auto-generated, this input is not needed for adding/editing.
                If you need an ID to be manually entered, ensure your backend handles it.
                For now, I'm keeping only name and description as they are common for create/update.
            */}
            {/* <div className={styles['form-group']}>
              <label htmlFor="categoryId">Código para la Categoría:</label>
              <input
                type="text"
                id="categoryId"
                name="id" // Assuming your API expects 'id' for a code
                placeholder="ID de la Categoría"
                value={categoryForm.id || ''} // Assuming categoryForm now has an 'id' field
                onChange={handleInputChange}
                required
                autoComplete="off"
                readOnly={!!editingCategoryId} // Usually ID is read-only when editing
              />
            </div> */}

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
      )}

      {/* Sección de Listado de Categorías con Scroll */}
      <div className={styles.tableContainer}>
        <table className={styles.table}>
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Descripción</th>
              {isAdmin && <th>Acciones</th>} {/* Only show Actions column for admin */}
            </tr>
          </thead>
          <tbody>
            {categories && categories.length > 0 ? (
              categories.map(category => (
                <tr key={category.id}>
                  <td data-label="Nombre">{category.name}</td>
                  <td data-label="Descripción">{category.description}</td>
                  {isAdmin && ( // Only show action buttons for admin
                    <td data-label="Acciones">
                      <button onClick={() => onEdit(category.id)} className={`${styles.actionButton} ${styles.editButton}`}>Editar</button>
                      <button onClick={() => onDelete(category.id)} className={`${styles.actionButton} ${styles.deleteButton}`}>Eliminar</button>
                    </td>
                  )}
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={isAdmin ? "3" : "2"} style={{ textAlign: 'center', padding: '20px' }}>No hay categorías registradas.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default CategoryManagement;