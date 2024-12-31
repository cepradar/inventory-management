// React Component: AdminPage
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/AdminPage.css';

function AdminPage() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [productForm, setProductForm] = useState({ name: '', description: '', price: '', quantity: '', categoryId: '' });
  const [categoryForm, setCategoryForm] = useState('');
  const [formType, setFormType] = useState(null);
  const [editingProductId, setEditingProductId] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    fetchProducts();
    fetchCategories();
  }, []);

  const verifyToken = () => {
    const token = localStorage.getItem('authToken');
    if (!token) {
      alert('No estás autorizado. Redirigiendo al login...');
      navigate('/login');
      return null;
    }
    return token;
  };

  const fetchProducts = async () => {
    const token = verifyToken();
    if (!token) return;
    try {
      const response = await fetch('http://localhost:8080/products/listar', {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });
      if (!response.ok) {
        throw new Error(`Error: ${response.statusText}`);
      }
      const data = await response.json();
      setProducts(data);
    } catch (error) {
      console.error('Error al obtener productos:', error);
    }
  };

  const fetchCategories = async () => {
    const token = verifyToken();
    if (!token) return;
    try {
      const response = await fetch('http://localhost:8080/products/categorias', {
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });
      if (!response.ok) {
        throw new Error(`Error: ${response.statusText}`);
      }
      const data = await response.json();
      setCategories(data);
    } catch (error) {
      console.error('Error al obtener categorías:', error);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    if (formType === 'product') {
      setProductForm({ ...productForm, [name]: value });
    } else if (formType === 'category') {
      setCategoryForm(value);
    }
  };

  const handleAddProduct = async (e) => {
    e.preventDefault();
    const token = verifyToken();
    if (!token) return;
    try {
      const response = await fetch('http://localhost:8080/products/agregar', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify(productForm),
      });
      if (response.ok) {
        fetchProducts();
        setProductForm({ name: '', description: '', price: '', quantity: '', categoryId: '' });
        setFormType(null);
      } else {
        console.error('Error al agregar producto:', response.statusText);
      }
    } catch (error) {
      console.error('Error al agregar producto:', error);
    }
  };

  const handleAddCategory = async (e) => {
    e.preventDefault();
    const token = verifyToken();
    if (!token) return;
    try {
      const response = await fetch('http://localhost:8080/products/categorias', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({ name: categoryForm }),
      });
      if (response.ok) {
        fetchCategories();
        setCategoryForm('');
        setFormType(null);
      } else {
        console.error('Error al crear categoría:', response.statusText);
      }
    } catch (error) {
      console.error('Error al crear categoría:', error);
    }
  };

  const handleEditProduct = (productId) => {
    const productToEdit = products.find(product => product.id === productId);
    setProductForm({
      name: productToEdit.name,
      description: productToEdit.description,
      price: productToEdit.price,
      quantity: productToEdit.quantity,
      categoryId: productToEdit.categoryId,
    });
    setEditingProductId(productId);
    setFormType('product');
  };

  const handleUpdateProduct = async (e) => {
    e.preventDefault();
    const token = verifyToken();
    if (!token) return;

    const productData = {
      name: productForm.name,
      description: productForm.description,
      price: parseFloat(productForm.price),
      quantity: parseInt(productForm.quantity, 10),
      categoryId: productForm.categoryId ? parseInt(productForm.categoryId, 10) : null,
    };

    const response = await fetch(`http://localhost:8080/products/actualizar/${editingProductId}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
      body: JSON.stringify(productData),
    });

    if (response.ok) {
      fetchProducts();
      setProductForm({ name: '', description: '', price: '', quantity: '', categoryId: '' });
      setFormType(null);
      setEditingProductId(null);
    } else {
      console.error('Error al actualizar producto:', response.statusText);
    }
  };

  const handleDeleteProduct = async (productId) => {
    const token = verifyToken();
    if (!token) return;

    const confirmDelete = window.confirm('¿Estás seguro de que deseas eliminar este producto?');
    if (!confirmDelete) return;

    const response = await fetch(`http://localhost:8080/products/eliminar/${productId}`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${token}`,
        },
    });

    if (response.ok) {
        fetchProducts(); // Actualizar la lista de productos después de eliminar
    } else if (response.status === 404) {
        alert('Producto no encontrado.');
    } else {
        console.error('Error al eliminar el producto:', response.statusText);
    }
};


  const handleCancelEdit = () => {
    setProductForm({ name: '', description: '', price: '', quantity: '', categoryId: '' });
    setEditingProductId(null);
    setFormType(null);
  };

  return (
    <div className="admin-container">
      <div className="sidebar">
        <h3>Admin Dashboard</h3>
        <nav>
          <ul>
            <li><button onClick={() => fetchProducts()}>Ver Productos</button></li>
            <li>
              <button onClick={() => setFormType(formType === 'product' ? null : 'product')}>
                {formType === 'product' ? 'Cancelar Agregar Producto' : 'Agregar Producto'}
              </button>
            </li>
            <li>
              <button onClick={() => setFormType(formType === 'category' ? null : 'category')}>
                {formType === 'category' ? 'Cancelar Crear Categoría' : 'Crear Categoría'}
              </button>
            </li>
          </ul>
        </nav>
      </div>

      <div className="main-content">
        <h2>Gestión de Productos</h2>
        {formType === 'product' && (
          <div className="product-form active">
            <h3>{editingProductId ? 'Editar Producto' : 'Agregar Producto'}</h3>
            <form onSubmit={editingProductId ? handleUpdateProduct : handleAddProduct}>
              <input
                type="text"
                name="name"
                placeholder="Nombre del Producto"
                value={productForm.name || ''}
                onChange={handleInputChange}
              />
              <input
                type="text"
                name="description"
                placeholder="Descripción"
                value={productForm.description || ''}
                onChange={handleInputChange}
              />
              <input
                type="number"
                name="price"
                placeholder="Precio"
                value={productForm.price || ''}
                onChange={handleInputChange}
              />
              <input
                type="number"
                name="quantity"
                placeholder="Cantidad"
                value={productForm.quantity || ''}
                onChange={handleInputChange}
                readOnly={!!editingProductId}
              />
              <select
                name="categoryId"
                value={productForm.categoryId || ''}
                onChange={handleInputChange}
              >
                <option value="">Seleccionar Categoría</option>
                {categories.map(category => (
                  <option key={category.id} value={category.id}>{category.name}</option>
                ))}
              </select>
              <button type="submit">{editingProductId ? 'Actualizar Producto' : 'Agregar Producto'}</button>
            </form>
            {editingProductId && (
              <button onClick={handleCancelEdit} className="cancel-button">Cancelar Edición</button>
            )}
          </div>
        )}

        {formType === 'category' && (
          <div className="category-form active">
            <h3>Crear Categoría</h3>
            <form onSubmit={handleAddCategory}>
              <input
                type="text"
                name="categoryName"
                placeholder="Nombre de la Categoría"
                value={categoryForm || ''}
                onChange={handleInputChange}
              />
              <button type="submit">Crear Categoría</button>
            </form>
          </div>
        )}

        <div className="product-list">
          <h3>Lista de Productos</h3>
          <table>
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
              {products.map(product => (
                <tr key={product.id}>
                  <td>{product.name}</td>
                  <td>{product.description}</td>
                  <td>{product.price}</td>
                  <td>{product.quantity}</td>
                  <td>{product.categoryName || 'Sin Categoría'}</td>
                  <td>
                    <button className="edit-button" onClick={() => handleEditProduct(product.id)}>Editar</button>
                    <button className="delete-button" onClick={() => handleDeleteProduct(product.id)}>Eliminar</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

export default AdminPage;
