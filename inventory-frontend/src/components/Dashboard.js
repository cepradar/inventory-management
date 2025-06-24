// src/components/Dashboard.js (ESTE ES TU NUEVO PANEL PRINCIPAL INTEGRADO)
import React, { useState, useEffect, useRef } from 'react';
// Reutilizamos los estilos de AdminLayout porque Dashboard ahora cumple ese rol
import styles from '../styles/AdminLayout.module.css';
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
  const [activeInventoryView, setActiveInventoryView] = useState('products'); // Default view
  const [showForm, setShowForm] = useState(null); // 'add-product', 'edit-product', etc.

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

  const navigate = useNavigate();
  const sidebarRef = useRef(null);

  // Lógica de VERIFICACIÓN DE TOKEN y CARGA INICIAL
  useEffect(() => {
    const token = localStorage.getItem('authToken'); // Asegúrate de usar 'authToken'
    const userRole = localStorage.getItem('userRole'); // Asegúrate de guardar el rol

    if (!token || userRole !== 'ADMIN') { // Redirige si no hay token o no es ADMIN
      alert('No estás autorizado o tu sesión ha expirado. Redirigiendo al login...');
      localStorage.clear(); // Limpia cualquier token inválido
      navigate('/login');
      return;
    }
    // Cargar datos iniciales según la vista activa
    if (activeInventoryView === 'products') {
      fetchProducts();
    } else if (activeInventoryView === 'categories') {
      fetchCategories();
    }
  }, [navigate, activeInventoryView]); // Dependencias para re-ejecutar cuando cambian

  // Lógica para cerrar el sidebar al hacer clic fuera
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (isSidebarExpanded && sidebarRef.current && !sidebarRef.current.contains(event.target)) {
        setIsSidebarExpanded(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [isSidebarExpanded]);

  const fetchProducts = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/products/listar');
      setProducts(response.data);
      // También asegúrate de cargar las categorías para el ProductForm
      if (categories.length === 0) { // Cargar solo si no están ya cargadas
        await fetchCategories();
      }
    } catch (error) {
      handleAuthError(error, 'ver productos');
    }
  };

  const fetchCategories = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/categories/listarCategoria');
      setCategories(response.data);
    } catch (error) {
      handleAuthError(error, 'ver categorías');
    }
  };

  const handleAuthError = (error, action) => {
    console.error(`Error al ${action}:`, error);
    if (error.response?.status === 403 || error.response?.status === 401) {
      alert(`No tienes permiso para ${action} o tu sesión ha expirado. Redirigiendo al login.`);
      localStorage.clear();
      navigate('/login');
    } else {
      alert(`Ocurrió un error inesperado al ${action}.`);
    }
  };

  const handleAddEditSubmit = async (e, type) => {
    e.preventDefault();
    try {
      if (type === 'product') {
        if (editingProductId) {
          await axios.put(`http://localhost:8080/api/products/actualizar/${editingProductId}`, productForm);
          alert('Producto actualizado con éxito');
        } else {
          await axios.post('http://localhost:8080/api/products/agregar/', productForm);
          alert('Producto agregado con éxito');
        }
        setProductForm({ name: '', description: '', price: '', quantity: '', categoryId: '' });
        setEditingProductId(null);
        fetchProducts(); // Refresh product list
      } else if (type === 'category') {
        if (editingCategoryId) {
          await axios.put(`http://localhost:8080/api/categories/actualizarCategoria/${editingCategoryId}`, categoryForm); // Asumo esta URL para actualizar
          alert('Categoría actualizada con éxito');
        } else {
          await axios.post('http://localhost:8080/api/categories/crearCategoria', categoryForm);
          alert('Categoría agregada con éxito');
        }
        setCategoryForm({ name: '', description: '' });
        setEditingCategoryId(null);
        fetchCategories(); // Refresh category list
      }
      setShowForm(null); // Close form after submission
    } catch (error) {
      console.error(`Error al ${editingProductId || editingCategoryId ? 'actualizar' : 'agregar'} ${type}:`, error);
      alert(`Error al ${editingProductId || editingCategoryId ? 'actualizar' : 'agregar'} ${type}. Verifica los datos e intenta de nuevo.`);
      handleAuthError(error, `${editingProductId || editingCategoryId ? 'actualizar' : 'agregar'} ${type}`);
    }
  };

  const handleEditProduct = async (productId) => {
    try {
      const response = await axios.get(`http://localhost:8080/api/products/${productId}`);
      setProductForm(response.data);
      setEditingProductId(productId);
      setShowForm('edit-product');
    } catch (error) {
      console.error('Error al cargar producto para edición:', error);
      alert('Error al cargar el producto para edición.');
      handleAuthError(error, 'editar productos');
    }
  };

  const handleEditCategory = async (categoryId) => {
    try {
      const response = await axios.get(`http://localhost:8080/api/categories/${categoryId}`); // Asumo esta URL para obtener categoría
      setCategoryForm(response.data);
      setEditingCategoryId(categoryId);
      // Aquí no necesitas cambiar showForm a 'add-category' si CategoryManagement ya maneja esto internamente
    } catch (error) {
      console.error('Error al cargar categoría para edición:', error);
      alert('Error al cargar la categoría para edición.');
      handleAuthError(error, 'editar categorías');
    }
  };

  const handleDeleteProduct = (productId) => {
    setModalMessage('¿Estás seguro de que quieres eliminar este producto?');
    setModalAction(() => async () => {
      try {
        await axios.delete(`http://localhost:8080/api/products/${productId}`);
        alert('Producto eliminado con éxito');
        fetchProducts();
      } catch (error) {
        console.error('Error al eliminar producto:', error);
        alert('Error al eliminar el producto.');
        handleAuthError(error, 'eliminar productos');
      } finally {
        setShowModal(false);
      }
    });
    setShowModal(true);
  };

  const handleDeleteCategory = (categoryId) => {
    setModalMessage('¿Estás seguro de que quieres eliminar esta categoría? Esto también afectará a los productos asociados.');
    setModalAction(() => async () => {
      try {
        await axios.delete(`http://localhost:8080/api/categories/${categoryId}`); // Asumo esta URL para eliminar
        alert('Categoría eliminada con éxito');
        fetchCategories();
        fetchProducts(); // Refrescar productos por si estaban asociados
      } catch (error) {
        console.error('Error al eliminar categoría:', error);
        alert('Error al eliminar la categoría. Asegúrate de que no tenga productos asociados.');
        handleAuthError(error, 'eliminar categorías');
      } finally {
        setShowModal(false);
      }
    });
    setShowModal(true);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    // Determinar qué formulario se está editando para actualizar el estado correcto
    if (showForm?.includes('product') || editingProductId) {
      setProductForm(prev => ({ ...prev, [name]: value }));
    } else if (activeInventoryView === 'categories' || editingCategoryId) { // Si estamos en la vista de categorías o editando una categoría
      setCategoryForm(prev => ({ ...prev, [name]: value }));
    }
  };

  const handleCancelEdit = () => {
    setShowForm(null);
    setEditingProductId(null);
    setEditingCategoryId(null);
    setProductForm({ name: '', description: '', price: '', quantity: '', categoryId: '' });
    setCategoryForm({ name: '', description: '' });
  };

  const handleModuleChange = (module) => {
    setActiveModule(module);
    setActiveInventoryView(null); // Reset sub-view when module changes
    setShowForm(null); // Close any open forms
    setEditingProductId(null); // Clear editing state
    setEditingCategoryId(null);
    if (module === 'logout') {
      localStorage.clear();
      navigate('/login');
    } else if (module === 'inventory') {
      setActiveInventoryView('products'); // Default to products when entering inventory
    }
    // Si añades otros módulos, puedes añadir lógica aquí
  };

  const handleInventoryOptionClick = (type, action) => {
    if (type === 'products') {
      setActiveInventoryView('products');
      if (action === 'add') {
        setShowForm('add-product');
        setEditingProductId(null); // Asegurarse de que no estamos en modo edición
        setProductForm({ name: '', description: '', price: '', quantity: '', categoryId: '' }); // Limpiar formulario
      } else {
        setShowForm(null);
      }
      fetchProducts(); // Siempre refrescar la lista de productos
    } else if (type === 'categories') {
      setActiveInventoryView('categories');
      setShowForm(null); // CategoryManagement maneja su propio formulario internamente
      setEditingCategoryId(null); // Reset editing state for category form
      setCategoryForm({ name: '', description: '' }); // Clear form
      fetchCategories(); // Ensure categories are fetched
    }
  };

  const toggleSidebar = () => setIsSidebarExpanded(prev => !prev);

  const renderContent = () => {
    if (activeModule === 'inventory') {
      if (activeInventoryView === 'products') {
        return showForm?.includes('product') ? (
          <ProductForm
            productForm={productForm}
            categories={categories} // Pasar categorías para el dropdown
            handleInputChange={handleInputChange}
            handleFormSubmit={handleAddEditSubmit}
            editingProductId={editingProductId}
            handleCancelEdit={handleCancelEdit}
          />
        ) : (
          <ProductList
            products={products}
            categories={categories}
            onEdit={handleEditProduct}
            onDelete={handleDeleteProduct}
          />
        );
      } else if (activeInventoryView === 'categories') {
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
    }
    // Puedes añadir contenido para otros módulos aquí (ej. 'users', 'settings')
    // Por defecto, muestra un mensaje de bienvenida
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
      />
      <div className={`${styles.mainContent}`}>
        <AdminNavbar
          activeModule={activeModule}
          onSubmenuOptionClick={handleInventoryOptionClick}
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