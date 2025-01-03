// AdminPage.js
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/AdminPage.css';
import ProfileMenu from './ProfileMenu'; // Ajusta la ruta si es necesario


function AdminPage() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [productForm, setProductForm] = useState({ name: '', description: '', price: '', quantity: '', categoryId: '' });
  const [categoryForm, setCategoryForm] = useState('');
  const [formType, setFormType] = useState(null);
  const [editingProductId, setEditingProductId] = useState(null);
  const [userName, setUserName] = useState([]);  // Asegúrate de definir userName correctamente aquí
  const [showProfileMenu, setShowProfileMenu] = useState(false); // Estado para mostrar u ocultar el menú de perfil
  const [submenuVisible, setSubmenuVisible] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    fetchProducts();
    fetchCategories();
    // Aquí podrías obtener el nombre de usuario, por ejemplo, desde el localStorage, si es necesario
    const storedUserName = localStorage.getItem('userName');
    if (storedUserName) {
      setUserName(storedUserName);  // Si tienes el nombre en el localStorage
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
    if (name === 'categoryName') {
      setCategoryForm(value);
    } else {
      setProductForm({ ...productForm, [name]: value });
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
  
    // Validar que todos los campos requeridos estén completos
    if (!productForm.name || !productForm.description || !productForm.price || !productForm.quantity || !productForm.categoryId) {
      alert('Por favor, llena todos los campos.');
      return;
    }
  
    // Asegúrate de que los valores de price y quantity sean válidos
    if (isNaN(productForm.price) || isNaN(productForm.quantity)) {
      alert('El precio y la cantidad deben ser números válidos.');
      return;
    }
  
    const token = verifyToken();
    if (!token) return;
  
    // Crear los datos del producto para enviarlos al backend
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
        // Si la respuesta es correcta, actualizar la lista de productos
        fetchProducts();
        setProductForm({ name: '', description: '', price: '', quantity: '', categoryId: '' });
        setFormType(null);
      } else {
        // Si la respuesta no es ok, mostrar un error detallado
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

  const toggleProfileMenu = () => {
    setShowProfileMenu(!showProfileMenu);
  };

  const toggleSubmenu = () => {
    setSubmenuVisible(!submenuVisible); // Cambiar visibilidad del submenú
  };

  return (
    <div className="admin-container">
      <div className="sidebar">
        {/* Foto de perfil y mensaje de bienvenida */}
        <div className="profile-section">
          <img
            src="https://via.placeholder.com/50" // Puedes reemplazar esto con la imagen del perfil
            alt="Foto de Perfil"
            className="profile-image"
          />
          <div className="profile-text">
            <h2>Bienvenido, {userName}</h2>
            <p>Dashboard</p>
          </div>
        </div>

        {/* Botones del sidebar */}
        <nav>
          <ul>
            <li><button onClick={toggleSubmenu}>Ver Productos</button></li>
            {submenuVisible && (
              <div className="submenu">
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
              </div>
            )}
          </ul>
        </nav>
      </div>

      <div className="main-content">
        <h2>Gestión de Productos</h2>

        {/* Mostrar formulario dependiendo del tipo de formulario */}
        {formType && (
          <div className={`${formType}-form active`}>
            <h3>{formType === 'product' ? (editingProductId ? 'Editar Producto' : 'Agregar Producto') : 'Crear Categoría'}</h3>
            <form onSubmit={(e) => handleFormSubmit(e, formType)}>
              {formType === 'product' ? (
                <>
                {/* Campos del formulario para productos */}
                  <input
                    type="text"
                    name="name"
                    placeholder="Nombre del Producto"
                    value={productForm.name || ''}
                    onChange={handleInputChange}
                    autoComplete="off" // Cambia según el contexto si el autocompletado es relevante
                  />
                  <input
                    type="text"
                    name="description"
                    placeholder="Descripción"
                    value={productForm.description || ''}
                    onChange={handleInputChange}
                    autoComplete="off" // Cambia según el contexto si el autocompletado es relevante
                  />
                  <input
                    type="number"
                    name="price"
                    placeholder="Precio"
                    value={productForm.price || ''}
                    onChange={handleInputChange}
                    autoComplete="off" // Cambia según el contexto si el autocompletado es relevante
                  />
                  <input
                    type="number"
                    name="quantity"
                    placeholder="Cantidad"
                    value={productForm.quantity || ''}
                    onChange={handleInputChange}
                    readOnly={!!editingProductId}
                    autoComplete="off" // Cambia según el contexto si el autocompletado es relevante
                  />
                  <select
                    name="categoryId"
                    value={productForm.categoryId || ''}
                    onChange={handleInputChange}
                    autoComplete="off" // Cambia según el contexto si el autocompletado es relevante
                  >
                    <option value="">Seleccionar Categoría</option>
                    {categories.map(category => (
                      <option key={category.id} value={category.id}>{category.name}</option>
                    ))}
                  </select>
                </>
              ) : (
                <input
                  type="text"
                  name="categoryName"
                  placeholder="Nombre de la Categoría"
                  value={categoryForm || ''}
                  onChange={handleInputChange}
                  autoComplete="off" // Cambia según el contexto si el autocompletado es relevante
                />
              )}
              <button type="submit">
                {formType === 'product' ? (editingProductId ? 'Actualizar Producto' : 'Agregar Producto') : 'Crear Categoría'}
              </button>
            </form>
            {formType === 'product' && editingProductId && (
              <button onClick={handleCancelEdit} className="cancel-button">Cancelar Edición</button>
            )}
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