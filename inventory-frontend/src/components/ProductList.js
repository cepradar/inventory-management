// src/components/ProductList.js
import React from 'react';
import styles from '../styles/ProductList.module.css';

function ProductList({ products, categories, onEdit, onDelete }) {
  // Función para obtener el nombre de la categoría por su ID
  const getCategoryName = (categoryId) => {
    const category = categories.find(cat => cat.id === categoryId);
    return category ? category.name : 'Sin Categoría';
  };

  return (
    <div className={styles.productListContainer}> {/* Cambiado de .product-list a .productListContainer */}
      <h2>Lista de Productos</h2>
      <div className={styles.tableContainer}>
        <table className={styles.table}>
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Descripción</th>
              <th>Precio</th>
              <th>Cantidad</th>
              <th>Categoría</th> 
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {products && products.length > 0 ? (
              products.map(product => (
                <tr key={product.id}>
                  <td data-label="Nombre">{product.name}</td>
                  <td data-label="Descripción">{product.description}</td>
                  <td data-label="Precio">${product.price.toFixed(2)}</td>
                  <td data-label="Cantidad">{product.quantity}</td>
                  <td data-label="Categoría">{getCategoryName(product.categoryId)}</td>
                  <td data-label="Acciones">
                    <button onClick={() => onEdit(product.id)} className={styles.actionButton + ' ' + styles.editButton}>Editar</button>
                    <button onClick={() => onDelete(product.id)} className={styles.actionButton + ' ' + styles.deleteButton}>Eliminar</button>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="6" style={{ textAlign: 'center', padding: '20px' }}>No hay productos registrados.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default ProductList;