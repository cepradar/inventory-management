import React from 'react';
import styles from '../styles/ProductList.module.css';

function ProductList({ products, onEdit, onDelete }) {
  return (
    <div className={styles['product-list']}>
      <h3>Lista de Productos</h3>
      <table>
        <thead>
          <tr>
            <th>Nombre</th>
            <th>Descripción</th>
            <th>Precio</th>
            <th>Cantidad</th>
            <th>Categoría ID</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {products && products.map(product => (
            <tr key={product.id}>
              <td>{product.name}</td>
              <td>{product.description}</td>
              <td>{product.price}</td>
              <td>{product.quantity}</td>
              <td>{product.categoryId}</td>
              <td>
                <button onClick={() => onEdit(product.id)} className={styles['edit-button']}>Editar</button>
                <button onClick={() => onDelete(product.id)} className={styles['delete-button']}>Eliminar</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default ProductList;