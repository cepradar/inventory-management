import React from 'react';
import styles from '../styles/ProductForm.module.css';

function ProductForm({ productForm, categories, handleInputChange, handleFormSubmit, editingProductId, handleCancelEdit }) {
  return (
    <div className={styles['product-form']}>
      <h3>{editingProductId ? 'Editar Producto' : 'Agregar Producto'}</h3>
      <form onSubmit={(e) => handleFormSubmit(e, 'product')}>
        <input
          type="text"
          name="name"
          placeholder="Nombre del Producto"
          value={productForm.name || ''}
          onChange={handleInputChange}
          autoComplete="off"
        />
        <input
          type="text"
          name="description"
          placeholder="Descripción"
          value={productForm.description || ''}
          onChange={handleInputChange}
          autoComplete="off"
        />
        <input
          type="number"
          name="price"
          placeholder="Precio"
          value={productForm.price || ''}
          onChange={handleInputChange}
          autoComplete="off"
        />
        <input
          type="number"
          name="quantity"
          placeholder="Cantidad"
          value={productForm.quantity || ''}
          onChange={handleInputChange}
          readOnly={!!editingProductId}
          autoComplete="off"
        />
        <select
          name="categoryId"
          value={productForm.categoryId || ''}
          onChange={handleInputChange}
          autoComplete="off"
        >
          <option value="">Seleccionar Categoría</option>
          {categories.map(category => (
            <option key={category.id} value={category.id}>{category.name}</option>
          ))}
        </select>
        <button type="submit">
          {editingProductId ? 'Actualizar Producto' : 'Agregar Producto'}
        </button>
      </form>
      {editingProductId && (
        <button onClick={handleCancelEdit} className={styles['cancel-button']}>Cancelar Edición</button>
      )}
    </div>
  );
}

export default ProductForm;