import React, { useState, useEffect } from 'react';
import styles from '../styles/AdminLayout.module.css';
import AdminSidebar from './AdminSidebar';
import AdminNavbar from './AdminNavbar';
import ProductList from './ProductList';
import CategoryList from './CategoryList';
import ProductForm from './ProductForm';
import CategoryForm from './CategoryForm';
import { useNavigate } from 'react-router-dom';

function AdminLayout() {
  const [activeModule, setActiveModule] = useState(null);
  const [activeInventoryView, setActiveInventoryView] = useState(null);
  const [showForm, setShowForm] = useState(null);
  const [productForm, setProductForm] = useState({ name: '', description: '', price: '', quantity: '', categoryId: '' });
  const [categoryForm, setCategoryForm] = useState({ name: '', description: '' });
  const [editingProductId, setEditingProductId] = useState(null);
  const [editingCategoryId, setEditingCategoryId] = useState(null);
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    // fetchProducts(); // Se cargarán cuando se acceda a la vista
    // fetchCategories(); // Se cargarán cuando se acceda a la vista
  }, [activeInventoryView]);

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
    } else if (name === 'name') {
      setProductForm({ ...productForm, name: value });
    } else if (name === 'description') {
      setProductForm({ ...productForm, description: value });
    } else if (name === 'price') {
      setProductForm({ ...productForm, price: value });
    } else if (name === 'quantity') {
      setProductForm({ ...productForm, quantity: value });
    } else if (name === 'categoryId') {
      setProductForm({ ...productForm, categoryId: value });
    }
  };

  const handleFormSubmit = (e, type) => {
    e.preventDefault();
    if (type === 'product') {
      editingProductId ? handleUpdateProduct(e) : handleAddProduct(e);
    } else if (type === 'category') {
      editingCategoryId ? handleUpdateCategory(e) : handleAddCategory(e);
    }
  };

  const handleAddProduct = async (e) => {
    e.preventDefault();
    if (!productForm.name || !productForm.description || !productForm.price || !productForm.quantity || !productForm.categoryId) {
      alert('Por favor, llena todos los campos del producto.');
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
        setShowForm(null);
        setProductForm({ name: '', description: '', price: '', quantity: '', categoryId: '' });
      } else {
        const errorData = await response.json();
        alert(`Error al agregar producto: ${errorData.message || response.statusText}`);
      }
    } catch (error) {
      console.error('Error al agregar producto:', error);
      alert('Hubo un error al agregar el producto. Intenta nuevamente más tarde.');
    }
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
    try {
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
        setShowForm(null);
        setEditingProductId(null);
        setProductForm({ name: '', description: '', price: '', quantity: '', categoryId: '' });
      } else {
        const errorData = await response.json();
        alert(`Error al actualizar producto: ${errorData.message || response.statusText}`);
      }
    } catch (error) {
      console.error('Error al actualizar producto:', error);
      alert('Hubo un error al actualizar el producto. Intenta nuevamente más tarde.');
    }
  };

  const handleAddCategory = async (e) => {
    e.preventDefault();
    if (!categoryForm.name || !categoryForm.description) {
      alert('Por favor, llena todos los campos de la categoría.');
      return;
    }
    const token = verifyToken();
    if (!token) return;
    try {
      const response = await fetch('http://localhost:8080/api/categories/crearCategoria', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({ name: categoryForm.name, description: categoryForm.description }),
      });
      if (response.ok) {
        fetchCategories();
        setShowForm(null);
        setCategoryForm({ name: '', description: '' });
      } else {
        const errorData = await response.json();
        alert(`Error al crear categoría: ${errorData.message || response.statusText}`);
      }
    } catch (error) {
      console.error('Error al crear categoría:', error);
      alert('Hubo un error al crear la categoría. Intenta nuevamente más tarde.');
    }
  };

  const handleUpdateCategory = async (e) => {
    e.preventDefault();
    const token = verifyToken();
    if (!token) return;
    try {
      const response = await fetch(`http://localhost:8080/api/categories/actualizarCategoria/${editingCategoryId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({ name: categoryForm.name, description: categoryForm.description }),
      });
      if (response.ok) {
        fetchCategories();
        setShowForm(null);
        setEditingCategoryId(null);
        setCategoryForm({ name: '', description: '' });
      } else {
        const errorData = await response.json();
        alert(`Error al actualizar categoría: ${errorData.message || response.statusText}`);
      }
    } catch (error) {
      console.error('Error al actualizar categoría:', error);
      alert('Hubo un error al actualizar la categoría. Intenta nuevamente más tarde.');
    }
  };

  const handleDeleteProduct = async (productId) => {
    const token = verifyToken();
    if (!token) return;
    const confirmDelete = window.confirm('¿Estás seguro de que deseas eliminar este producto?');
    if (!confirmDelete) return;
    try {
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
        const errorData = await response.json();
        alert(`Error al eliminar el producto: ${errorData.message || response.statusText}`);
      }
    } catch (error) {
      console.error('Error al eliminar el producto:', error);
      alert('Hubo un error al eliminar el producto. Intenta nuevamente más tarde.');
    }
  };

  const handleDeleteCategory = async (categoryId) => {
    const token = verifyToken();
    if (!token) return;
    const confirmDelete = window.confirm('¿Estás seguro de que deseas eliminar esta categoría?');
    if (!confirmDelete) return;
    try {
      const response = await fetch(`http://localhost:8080/api/categories/eliminarCategoria/${categoryId}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });
      if (response.ok) {
        fetchCategories();
      } else if (response.status === 404) {
        alert('Categoría no encontrada.');
      } else {
        const errorData = await response.json();
        alert(`Error al eliminar la categoría: ${errorData.message || response.statusText}`);
      }
    } catch (error) {
      console.error('Error al eliminar la categoría:', error);
      alert('Hubo un error al eliminar la categoría. Intenta nuevamente más tarde.');
    }
  };

  const handleModuleChange = (module) => {
    setActiveModule(module);
    setActiveInventoryView(null); // Reseteamos la vista de inventario
    setShowForm(null);
    setEditingProductId(null);
    setEditingCategoryId(null);
  
    if (module === 'inventory') {
      setActiveInventoryView('products-list'); // Establecemos la vista a la lista de productos
      fetchProducts(); // Cargamos los productos automáticamente
    }
  };

  const handleInventoryOptionClick = (module, option) => {
    setActiveInventoryView(option);
    setShowForm(null);
    setEditingProductId(null);
    setEditingCategoryId(null);
    if (option === 'list' && module === 'products') {
      setActiveInventoryView('products-list'); // Establecemos un valor específico para la lista de productos
      fetchProducts();
    } else if (option === 'list' && module === 'categories') {
      setActiveInventoryView('categories-list'); // Establecemos un valor específico para la lista de categorías
      fetchCategories();
    } else if (option === 'add' && module === 'products') {
      setShowForm('add-product');
    } else if (option === 'add' && module === 'categories') {
      setShowForm('add-category');
    }
  };

  const handleEditProduct = (productId) => {
    const productToEdit = products.find(product => product.id === productId);
    setProductForm(productToEdit);
    setEditingProductId(productId);
    setShowForm('edit-product');
  };

  const handleEditCategory = (categoryId) => {
    const categoryToEdit = categories.find(category => category.id === categoryId);
    setCategoryForm(categoryToEdit);
    setEditingCategoryId(categoryId);
    setShowForm('edit-category');
  };

  const handleCancelEdit = () => {
    setShowForm(null);
    setEditingProductId(null);
    setEditingCategoryId(null);
    setProductForm({ name: '', description: '', price: '', quantity: '', categoryId: '' });
    setCategoryForm({ name: '', description: '' });
  };

  const renderContent = () => {
    if (activeModule === 'inventory') {
      if (activeInventoryView === 'products-list') {
        return <ProductList products={products} onEdit={handleEditProduct} onDelete={handleDeleteProduct} />;
      } else if (activeInventoryView === 'categories-list') {
        return <CategoryList categories={categories} onEdit={handleEditCategory} onDelete={handleDeleteCategory} />;
      } else if (showForm === 'add-product' || showForm === 'edit-product') {
        return (
          <ProductForm
            productForm={productForm}
            categories={categories}
            handleInputChange={handleInputChange}
            handleFormSubmit={handleFormSubmit}
            editingProductId={editingProductId}
            handleCancelEdit={handleCancelEdit}
          />
        );
      } else if (showForm === 'add-category' || showForm === 'edit-category') {
        return (
          <CategoryForm
            categoryForm={categoryForm}
            handleInputChange={handleInputChange}
            handleFormSubmit={handleFormSubmit}
            editingCategoryId={editingCategoryId}
            handleCancelEdit={handleCancelEdit}
          />
        );
      } else if (activeInventoryView === 'list') {
        return <div>Selecciona una opción de lista (Productos o Categorías).</div>; // Mensaje si solo se selecciona "Lista"
      }
    }
    return <div>Selecciona un módulo del menú principal.</div>;
  };

  return (
    <div className={styles.container}>
      <AdminSidebar onModuleChange={handleModuleChange} />
      <div className={styles.mainContent}>
        <AdminNavbar activeModule={activeModule} onSubmenuOptionClick={handleInventoryOptionClick} />
        <div className={styles.canvas}>
          {renderContent()}
        </div>
      </div>
    </div>
  );
}

export default AdminLayout;