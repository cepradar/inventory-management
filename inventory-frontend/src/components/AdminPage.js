import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import styles from '../styles/AdminPage.module.css'; // Importa los estilos
import ProfileMenu from './ProfileMenu'; // Ajusta la ruta si es necesario

function AdminPage() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [productForm, setProductForm] = useState({ name: '', description: '', price: '', quantity: '', categoryId: '' });
  const [categoryForm, setCategoryForm] = useState({name:'', description: ''});
  const [formType, setFormType] = useState(null);
  const [editingProductId, setEditingProductId] = useState(null);
  const [userName, setUserName] = useState(''); // Inicializa como string vacío
  const [showProfileMenu, setShowProfileMenu] = useState(false);
  const [submenuVisible, setSubmenuVisible] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    fetchProducts();
    fetchCategories();
    const storedUserName = localStorage.getItem('userName');
    if (storedUserName) {
      setUserName(storedUserName);
    }
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
      const response = await fetch('http://localhost:8080/api/categories/listarCategoria', {
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
    if (name === 'categoryName') {
      setCategoryForm({ ...categoryForm, name: value });
    } else if (name === 'categoryDescription') {
      setCategoryForm({ ...categoryForm, description: value });
    }
  };

  const handleFormSubmit = (e, type) => {
    e.preventDefault();
    if (type === 'product') {
      editingProductId ? handleUpdateProduct(e) : handleAddProduct(e);
    } else if (type === 'category') {
      handleAddCategory(e);
    }
  };

  const handleAddProduct = async (e) => {
    e.preventDefault();

    if (!productForm.name || !productForm.description || !productForm.price || !productForm.quantity || !productForm.categoryId) {
      alert('Por favor, llena todos los campos.');
      return;
    }

    if (isNaN(productForm.price) || isNaN(productForm.quantity)) {
      alert('El precio y la cantidad deben ser números válidos.');
      return;
    }

    const token = verifyToken();
    if (!token) return;

    const productData = {
      name: productForm.name,
      description: productForm.description,
      price: parseFloat(productForm.price),
      quantity: parseInt(productForm.quantity, 10),
      categoryId: parseInt(productForm.categoryId, 10),
    };

    try {
      const response = await fetch('http://localhost:8080/products/agregar', {
        method: 'POST',
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
      } else {
        const errorData = await response.json();
        alert(`Error al agregar producto: ${errorData.message || response.statusText}`);
      }
    } catch (error) {
      console.error('Error al agregar producto:', error);
      alert('Hubo un error al agregar el producto. Intenta nuevamente más tarde.');
    }
  };

  const handleAddCategory = async (e) => {
    e.preventDefault();
    const token = verifyToken();
    if (!token) return;
    try {
      const response = await fetch('http://localhost:8080/api/categories/crearCategoria', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({ name: categoryForm.name , description: categoryForm.description}),
      });
      if (response.ok) {
        fetchCategories();
        setCategoryForm({ name: '', description: '' });
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
      fetchProducts();
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

  const toggleSubmenu = () => {
    setSubmenuVisible(!submenuVisible);
  };

  return (
    <div className={styles['admin-container']}>
      <div className={styles['sidebar']}>
        <div className={styles['profile-section']}>
          <img
            src="https://via.placeholder.com/80" // Imagen de perfil placeholder
            alt="Foto de Perfil"
            className={styles['profile-image']}
          />
          <div className={styles['profile-text']}>
            <h2>Bienvenido, {userName}</h2>
            <p>Panel de Administración</p>
          </div>
        </div>
        <nav>
          <ul>
            <li><button onClick={toggleSubmenu}>Gestión de Productos</button></li>
            {submenuVisible && (
              <div className={styles['submenu']}>
                <li>
                  <button onClick={() => setFormType(formType === 'product' ? null : 'product')}>
                    {formType === 'product' ? (editingProductId ? 'Cancelar Edición' : 'Cancelar') : 'Agregar Producto'}
                  </button>
                </li>
                <li>
                  <button onClick={() => setFormType(formType === 'category' ? null : 'category')}>
                    {formType === 'category' ? 'Cancelar' : 'Crear Categoría'}
                  </button>
                </li>
              </div>
            )}
          </ul>
        </nav>
      </div>

      <div className={styles['main-content']}>
        <h2>Gestión de {formType === 'category' ? 'Categorías' : 'Productos'}</h2>

        {formType && (
          <div className={`${styles[formType + '-form']} ${styles.active}`}>
            <h3>{formType === 'product' ? (editingProductId ? 'Editar Producto' : 'Agregar Producto') : 'Crear Categoría'}</h3>
            <form onSubmit={(e) => handleFormSubmit(e, formType)}>
              {formType === 'product' ? (
                <>
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
                </>
              ) : (
                <>
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
                </>
              )}
              <button type="submit">
                {formType === 'product' ? (editingProductId ? 'Actualizar Producto' : 'Agregar Producto') : 'Crear Categoría'}
              </button>
            </form>
            {formType === 'product' && editingProductId && (
              <button onClick={handleCancelEdit} className={styles['cancel-button']}>Cancelar Edición</button>
            )}
          </div>
        )}

        {formType === 'category' && (
          <div className={styles['category-list']}>
            <h3>Lista de Categorías</h3>
            <table>
              <thead>
                <tr>
                  <th>Nombre</th>
                  <th>Descripción</th> 
                </tr>
              </thead>
              <tbody>
                {categories.map(category => (
                  <tr key={category.id}>
                    <td>{category.name}</td>
                    <td>{category.description}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {(formType === null || formType === 'product') && (
          <div className={styles['product-list']}>
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
                      <button className={styles['edit-button']} onClick={() => handleEditProduct(product.id)}>Editar</button>
                      <button className={styles['delete-button']} onClick={() => handleDeleteProduct(product.id)}>Eliminar</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}

export default AdminPage;