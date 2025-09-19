// src/components/Dashboard.js
import React, { useState, useEffect, useRef } from 'react';
import styles from '../styles/AdminLayout.module.css'; // Reutilizamos los estilos de AdminLayout
import AdminSidebar from './AdminSidebar';
import AdminNavbar from './AdminNavbar';
import ProductList from './ProductList';
import CategoryManagement from './CategoryManagement'; // Componente de gestión unificada
import ProductForm from './ProductForm';
import Modal from './Modal';
import { useNavigate } from 'react-router-dom';
import axios from '../utils/axiosConfig';

function Dashboard() { // Ahora Dashboard funciona como el antiguo AdminLayout
  const [activeModule, setActiveModule] = useState('inventory');
  const [activeInventoryView, setActiveInventoryView] = useState('products');
  const [showForm, setShowForm] = useState(null);

  const [productForm, setProductForm] = useState({ name: '', description: '', price: '', quantity: '', categoryId: '' });
  const [categoryForm, setCategoryForm] = useState({ name: '', description: '' });

  const [editingProductId, setEditingProductId] = useState(null);
  const [editingCategoryId, setEditingCategoryId] = useState(null);

  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);

  const [isSidebarExpanded, setIsSidebarExpanded] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [modalMessage, setModalMessage] = useState('');
  const [modalAction, setModalAction] = useState(() => {});

  const sidebarRef = useRef(null);
  const navigate = useNavigate();
  const [userRole, setUserRole] = useState(null); // Estado para almacenar el rol del usuario

  useEffect(() => {
    const role = localStorage.getItem('userRole');
    if (role) {
      setUserRole(role);
    } else {
      // Si no hay rol, redirigir al login (aunque ProtectedRoute ya lo haría)
      navigate('/login');
    }
    fetchProducts();
    fetchCategories();
  }, [navigate]); // Añadir navigate a las dependencias para evitar warnings

  useEffect(() => {
    function handleClickOutside(event) {
      if (sidebarRef.current && !sidebarRef.current.contains(event.target)) {
        setIsSidebarExpanded(false);
      }
    }

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [sidebarRef]);

  const toggleSidebar = () => {
    setIsSidebarExpanded(!isSidebarExpanded);
  };

  const fetchProducts = async () => {
    try {
      const token = localStorage.getItem('authToken');
      if (!token) {
        navigate('/login');
        return;
      }
      const response = await axios.get('http://localhost:8080/api/products/listar', {
        headers: { Authorization: `Bearer ${token}` },
      });
      setProducts(response.data);
    } catch (error) {
      console.error('Error al cargar productos:', error);
      if (error.response && error.response.status === 403) {
        alert('No tienes permiso para ver productos. Redirigiendo al login.');
        localStorage.clear();
        navigate('/login');
      }
    }
  };

  const fetchCategories = async () => {
    try {
      const token = localStorage.getItem('authToken');
      if (!token) {
        navigate('/login');
        return;
      }
      const response = await axios.get('http://localhost:8080/api/categories/listarCategoria', {
        headers: { Authorization: `Bearer ${token}` },
      });
      setCategories(response.data);
    } catch (error) {
      console.error('Error al cargar categorías:', error);
      if (error.response && error.response.status === 403) {
        alert('No tienes permiso para ver categorías. Redirigiendo al login.');
        localStorage.clear();
        navigate('/login');
      }
    }
  };

  const handleModuleChange = (module) => {
    setActiveModule(module);
    setShowForm(null); // Reset form visibility when changing modules
    setEditingProductId(null); // Clear any editing state
    setEditingCategoryId(null);
    if (module === 'logout') {
      setShowModal(true);
      setModalMessage('¿Estás seguro de que quieres cerrar sesión?');
      setModalAction(() => () => {
        localStorage.clear(); // Clear all stored data
        navigate('/login');
        setShowModal(false);
      });
    } else if (module === 'inventory') {
      setActiveInventoryView('products'); // Default to products list when entering inventory
      fetchProducts();
      fetchCategories();
    }
  };

  const handleInventoryOptionClick = (type, action) => {
    if (type === 'products') {
      if (action === 'list') {
        setActiveInventoryView('products');
        setShowForm(null);
        setEditingProductId(null);
        fetchProducts();
      } else if (action === 'add') {
        setActiveInventoryView('products'); // Keep view on products list for context
        setShowForm('product');
        setEditingProductId(null);
        setProductForm({ name: '', description: '', price: '', quantity: '', categoryId: '' });
      }
    } else if (type === 'categories') {
      if (action === 'list') {
        setActiveInventoryView('categories');
        setShowForm(null);
        setEditingCategoryId(null);
        fetchCategories();
      } else if (action === 'add') {
        setActiveInventoryView('categories'); // Keep view on categories list for context
        setShowForm('category');
        setEditingCategoryId(null);
        setCategoryForm({ name: '', description: '' });
      }
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    if (showForm === 'product') {
      setProductForm({ ...productForm, [name]: value });
    } else if (showForm === 'category') {
      setCategoryForm({ ...categoryForm, [name]: value });
    }
  };

  const handleAddEditSubmit = async (e, type) => {
    e.preventDefault();
    const token = localStorage.getItem('authToken');
    if (!token) {
      navigate('/login');
      return;
    }

    try {
      if (type === 'product') {
        const method = editingProductId ? 'put' : 'post';
        const url = editingProductId
          ? `http://localhost:8080/api/products/${editingProductId}`
          : 'http://localhost:8080/api/products';

        await axios[method](url, productForm, {
          headers: { Authorization: `Bearer ${token}` },
        });
        alert(`Producto ${editingProductId ? 'actualizado' : 'agregado'} exitosamente.`);
        setProductForm({ name: '', description: '', price: '', quantity: '', categoryId: '' });
        setEditingProductId(null);
        setShowForm(null); // Hide form after submission
        fetchProducts();
      } else if (type === 'category') {
        const method = editingCategoryId ? 'put' : 'post';
        const url = editingCategoryId
          ? `http://localhost:8080/api/categories/${editingCategoryId}`
          : 'http://localhost:8080/api/categories';

        await axios[method](url, categoryForm, {
          headers: { Authorization: `Bearer ${token}` },
        });
        alert(`Categoría ${editingCategoryId ? 'actualizada' : 'agregada'} exitosamente.`);
        setCategoryForm({ name: '', description: '' });
        setEditingCategoryId(null);
        setShowForm(null); // Hide form after submission
        fetchCategories();
      }
    } catch (error) {
      console.error(`Error al ${editingProductId || editingCategoryId ? 'actualizar' : 'agregar'} ${type}:`, error);
      alert(`Error al ${editingProductId || editingCategoryId ? 'actualizar' : 'agregar'} ${type}.`);
    }
  };

  const handleEditProduct = (id) => {
    const productToEdit = products.find((product) => product.id === id);
    if (productToEdit) {
      setProductForm(productToEdit);
      setEditingProductId(id);
      setShowForm('product');
      setActiveInventoryView('products'); // Ensure product list is visible when editing
    }
  };

  const handleDeleteProduct = (id) => {
    setShowModal(true);
    setModalMessage('¿Estás seguro de que quieres eliminar este producto?');
    setModalAction(() => async () => {
      try {
        const token = localStorage.getItem('authToken');
        if (!token) {
          navigate('/login');
          return;
        }
        await axios.delete(`http://localhost:8080/api/products/${id}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        alert('Producto eliminado exitosamente.');
        fetchProducts();
      } catch (error) {
        console.error('Error al eliminar producto:', error);
        alert('Error al eliminar producto.');
      } finally {
        setShowModal(false);
      }
    });
  };

  const handleEditCategory = (id) => {
    const categoryToEdit = categories.find((category) => category.id === id);
    if (categoryToEdit) {
      setCategoryForm(categoryToEdit);
      setEditingCategoryId(id);
      setShowForm('category');
      setActiveInventoryView('categories'); // Ensure category list is visible when editing
    }
  };

  const handleDeleteCategory = (id) => {
    setShowModal(true);
    setModalMessage('¿Estás seguro de que quieres eliminar esta categoría?');
    setModalAction(() => async () => {
      try {
        const token = localStorage.getItem('authToken');
        if (!token) {
          navigate('/login');
          return;
        }
        await axios.delete(`http://localhost:8080/api/categories/${id}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        alert('Categoría eliminada exitosamente.');
        fetchCategories();
      } catch (error) {
        console.error('Error al eliminar categoría:', error);
        alert('Error al eliminar categoría.');
      } finally {
        setShowModal(false);
      }
    });
  };

  const handleCancelEdit = () => {
    setEditingProductId(null);
    setEditingCategoryId(null);
    setShowForm(null);
    setProductForm({ name: '', description: '', price: '', quantity: '', categoryId: '' });
    setCategoryForm({ name: '', description: '' });
  };

  const renderContent = () => {
    if (activeModule === 'inventory') {
      if (activeInventoryView === 'products') {
        if (showForm === 'product') {
          return (
            <ProductForm
              productForm={productForm}
              categories={categories}
              handleInputChange={handleInputChange}
              handleFormSubmit={handleAddEditSubmit}
              editingProductId={editingProductId}
              handleCancelEdit={handleCancelEdit}
            />
          );
        } else {
          return (
            <ProductList
              products={products}
              categories={categories}
              onEdit={handleEditProduct}
              onDelete={handleDeleteProduct}
            />
          );
        }
      } else if (activeInventoryView === 'categories') {
        // Now CategoryManagement combines list and form
        return (
          <CategoryManagement
            categories={categories}
            onEdit={handleEditCategory}
            onDelete={handleDeleteCategory}
            categoryForm={categoryForm}
            handleInputChange={handleInputChange}
            handleAddEditSubmit={handleAddEditSubmit}
            editingCategoryId={editingCategoryId}
            handleCancelEdit={handleCancelEdit}
          />
        );
      }
    } else if (activeModule === 'users') {
      return (
        <div style={{ textAlign: 'center', marginTop: '50px', fontSize: '1.2em', color: '#666' }}>
          <h2>Gestión de Usuarios</h2>
          <p>Funcionalidad de gestión de usuarios en desarrollo.</p>
          {userRole === 'ADMIN' && <p>Como administrador, aquí podrías ver y editar usuarios.</p>}
          {userRole === 'USER' && <p>Como usuario normal, solo verías tu perfil.</p>}
        </div>
      );
    } else if (activeModule === 'settings') {
      return (
        <div style={{ textAlign: 'center', marginTop: '50px', fontSize: '1.2em', color: '#666' }}>
          <h2>Configuración de la Aplicación</h2>
          <p>Funcionalidad de configuración en desarrollo.</p>
          {userRole === 'ADMIN' && <p>Como administrador, aquí podrías ajustar la configuración global.</p>}
        </div>
      );
    }

    // Mensaje de bienvenida por defecto
    return (
      <div style={{ textAlign: 'center', marginTop: '50px', fontSize: '1.2em', color: '#666' }}>
        <h2>Bienvenido al Panel de Administración</h2>
        <p>Selecciona una opción del menú o de la barra superior para comenzar.</p>
      </div>
    );
  };

  return (
    <div className={`${styles.container} ${isSidebarExpanded ? styles.sidebarExpanded : ''}`}>
      <AdminSidebar
        sidebarRef={sidebarRef}
        onModuleChange={handleModuleChange}
        isExpanded={isSidebarExpanded}
        toggleSidebar={toggleSidebar}
        activeModule={activeModule}
        userRole={userRole}
      />
      <div className={`${styles.mainContent}`}>
        <AdminNavbar
          activeModule={activeModule}
          onSubmenuOptionClick={handleInventoryOptionClick}
          userRole={userRole}
        />
        <div className={styles.canvas}>
          {renderContent()}
        </div>
      </div>

      {showModal && (
        <Modal
          message={modalMessage}
          onConfirm={modalAction}
          onCancel={() => setShowModal(false)}
        />
      )}
    </div>
  );
}

export default Dashboard;