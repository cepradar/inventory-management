// src/components/ProductForm.js
import React from 'react';
import styles from '../styles/ProductForm.module.css';

function ProductForm({ productForm, categories, handleInputChange, handleFormSubmit, editingProductId, handleCancelEdit }) {
  return (
    // Asegúrate de que esta clase coincida con tu CSS: .product-form
    <div className={styles['product-form']}>
      <h2>{editingProductId ? 'Editar Producto' : 'Agregar Producto'}</h2>
      <form onSubmit={(e) => handleFormSubmit(e, 'product')}>
        <div className={styles['form-group']}>
          <label htmlFor="productName">Nombre del Producto:</label>
          <input
            type="text"
            id="productName"
            name="name"
            placeholder="Nombre del Producto"
            value={productForm.name || ''}
            onChange={handleInputChange}
            required
            autoComplete="off"
          />
        </div>

        <div className={styles['form-group']}>
          <label htmlFor="productDescription">Descripción:</label>
          <textarea
            id="productDescription"
            name="description"
            placeholder="Descripción"
            value={productForm.description || ''}
            onChange={handleInputChange}
            autoComplete="off"
          ></textarea>
        </div>

        <div className={styles['form-group']}>
          <label htmlFor="productPrice">Precio:</label>
          <input
            type="number"
            id="productPrice"
            name="price"
            placeholder="Precio"
            value={productForm.price || ''}
            onChange={handleInputChange}
            required
            min="0"
            step="0.01" // Permite valores decimales
            autoComplete="off"
          />
        </div>

        <div className={styles['form-group']}>
          <label htmlFor="productQuantity">Cantidad:</label>
          <input
            type="number"
            id="productQuantity"
            name="quantity"
            placeholder="Cantidad"
            value={productForm.quantity || ''}
            onChange={handleInputChange}
            required
            min="0"
            readOnly={!!editingProductId} // Cantidad no editable en modo edición
            autoComplete="off"
          />
        </div>

        <div className={styles['form-group']}>
          <label htmlFor="productCategory">Categoría:</label>
          <select
            id="productCategory"
            name="categoryId"
            value={productForm.categoryId || ''}
            onChange={handleInputChange}
            required
            autoComplete="off"
          >
            <option value="">Seleccionar Categoría</option>
            {/* Asegúrate de que 'categories' se esté pasando correctamente y tenga la estructura { id, name } */}
            {categories.map(category => (
              <option key={category.id} value={category.id}>{category.name}</option>
            ))}
          </select>
        </div>

        <div className={styles['form-actions']}>
          <button type="submit" className={styles['submit-button']}>
            {editingProductId ? 'Actualizar Producto' : 'Agregar Producto'}
          </button>
          {editingProductId && (
            <button type="button" onClick={handleCancelEdit} className={styles['cancel-button']}>
              Cancelar Edición
            </button>
          )}
        </div>
      </form>
    </div>
  );
}

export default ProductForm;