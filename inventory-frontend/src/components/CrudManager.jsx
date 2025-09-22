// src/components/CrudManager.js
import React, { useState, useEffect } from 'react';
import axios from './utils/axiosConfig';
import Modal from './Modal';
import { useNavigate } from 'react-router-dom';

// Componentes de formulario genéricos para productos y categorías
const ResourceForm = ({ resourceType, formData, categories, handleInputChange, handleFormSubmit, editingId, handleCancelEdit }) => {
  return (
    <form onSubmit={(e) => handleFormSubmit(e, resourceType)} className="p-4 border rounded shadow-sm">
      <h3 className="text-xl font-bold mb-4">{editingId ? `Editar ${resourceType}` : `Añadir ${resourceType}`}</h3>
      {resourceType === 'products' ? (
        <>
          <input
            type="text"
            name="name"
            placeholder="Nombre del Producto"
            value={formData.name || ''}
            onChange={handleInputChange}
            className="w-full p-2 mb-2 border rounded"
            required
          />
          <input
            type="text"
            name="description"
            placeholder="Descripción del Producto"
            value={formData.description || ''}
            onChange={handleInputChange}
            className="w-full p-2 mb-2 border rounded"
            required
          />
          <input
            type="number"
            name="price"
            placeholder="Precio"
            value={formData.price || ''}
            onChange={handleInputChange}
            className="w-full p-2 mb-2 border rounded"
            required
          />
          <input
            type="number"
            name="quantity"
            placeholder="Cantidad"
            value={formData.quantity || ''}
            onChange={handleInputChange}
            className="w-full p-2 mb-2 border rounded"
            required
          />
          <select
            name="categoryId"
            value={formData.categoryId || ''}
            onChange={handleInputChange}
            className="w-full p-2 mb-2 border rounded"
            required
          >
            <option value="">Selecciona una categoría</option>
            {categories.map(cat => (
              <option key={cat.id} value={cat.id}>{cat.name}</option>
            ))}
          </select>
        </>
      ) : (
        <>
          <input
            type="text"
            name="name"
            placeholder="Nombre de la Categoría"
            value={formData.name || ''}
            onChange={handleInputChange}
            className="w-full p-2 mb-2 border rounded"
            required
          />
          <input
            type="text"
            name="description"
            placeholder="Descripción de la Categoría"
            value={formData.description || ''}
            onChange={handleInputChange}
            className="w-full p-2 mb-2 border rounded"
            required
          />
        </>
      )}
      <button type="submit" className="bg-blue-500 text-white p-2 rounded mr-2">{editingId ? 'Actualizar' : 'Añadir'}</button>
      <button type="button" onClick={handleCancelEdit} className="bg-gray-500 text-white p-2 rounded">Cancelar</button>
    </form>
  );
};

// Componente de lista genérica
const ResourceList = ({ resourceType, data, categories, userRole, onEdit, onDelete, onAdd }) => {
  const isAdmin = userRole === 'ADMIN';
  const getCategoryName = (categoryId) => {
    const category = categories.find(cat => cat.id === categoryId);
    return category ? category.name : 'N/A';
  };

  return (
    <div className="p-4">
      <div className="flex justify-between items-center mb-4">
        <h3 className="text-xl font-bold">Listado de {resourceType === 'products' ? 'Productos' : 'Categorías'}</h3>
        {isAdmin && (
          <button onClick={onAdd} className="bg-green-500 text-white p-2 rounded">
            Añadir {resourceType === 'products' ? 'Producto' : 'Categoría'}
          </button>
        )}
      </div>
      <table className="min-w-full bg-white border-collapse">
        <thead>
          <tr className="bg-gray-200">
            <th className="p-2 border">ID</th>
            <th className="p-2 border">Nombre</th>
            {resourceType === 'products' && (
              <>
                <th className="p-2 border">Descripción</th>
                <th className="p-2 border">Precio</th>
                <th className="p-2 border">Cantidad</th>
                <th className="p-2 border">Categoría</th>
              </>
            )}
            {isAdmin && <th className="p-2 border">Acciones</th>}
          </tr>
        </thead>
        <tbody>
          {data.map(item => (
            <tr key={item.id} className="hover:bg-gray-100">
              <td className="p-2 border">{item.id}</td>
              <td className="p-2 border">{item.name}</td>
              {resourceType === 'products' && (
                <>
                  <td className="p-2 border">{item.description}</td>
                  <td className="p-2 border">{item.price}</td>
                  <td className="p-2 border">{item.quantity}</td>
                  <td className="p-2 border">{getCategoryName(item.categoryId)}</td>
                </>
              )}
              {isAdmin && (
                <td className="p-2 border text-center">
                  <button onClick={() => onEdit(item.id)} className="bg-yellow-500 text-white p-1 rounded mr-2">Editar</button>
                  <button onClick={() => onDelete(item.id)} className="bg-red-500 text-white p-1 rounded">Eliminar</button>
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default function CrudManager({ resourceType, userRole }) {
  const [data, setData] = useState([]);
  const [categories, setCategories] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({});
  const [editingId, setEditingId] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [modalMessage, setModalMessage] = useState('');
  const [modalAction, setModalAction] = useState(() => {});
  const navigate = useNavigate();

  const apiEndpoints = {
    products: {
      list: 'http://localhost:8080/api/products/listar',
      base: 'http://localhost:8080/api/products',
    },
    categories: {
      list: 'http://localhost:8080/api/categories/listarCategoria',
      base: 'http://localhost:8080/api/categories',
    },
  };

  const fetchData = async () => {
    try {
      const token = localStorage.getItem('authToken');
      if (!token) return navigate('/login');
      const response = await axios.get(apiEndpoints[resourceType].list, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setData(response.data);
    } catch (error) {
      console.error(`Error al cargar ${resourceType}:`, error);
      if (error.response && error.response.status === 403) {
        alert('No tienes permiso para ver esta información. Redirigiendo al login.');
        localStorage.clear();
        navigate('/login');
      }
    }
  };

  const fetchCategories = async () => {
    try {
      const token = localStorage.getItem('authToken');
      if (!token) return navigate('/login');
      const response = await axios.get(apiEndpoints.categories.list, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setCategories(response.data);
    } catch (error) {
      console.error('Error al cargar categorías:', error);
    }
  };

  useEffect(() => {
    fetchData();
    if (resourceType === 'products') {
      fetchCategories();
    }
  }, [resourceType]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleAddEditSubmit = async (e, type) => {
    e.preventDefault();
    const token = localStorage.getItem('authToken');
    if (!token) return navigate('/login');

    try {
      const method = editingId ? 'put' : 'post';
      const url = editingId
        ? `${apiEndpoints[type].base}/${editingId}`
        : apiEndpoints[type].base;

      await axios[method](url, formData, {
        headers: { Authorization: `Bearer ${token}` },
      });

      alert(`${type} ${editingId ? 'actualizado' : 'agregado'} exitosamente.`);
      setFormData({});
      setEditingId(null);
      setShowForm(false);
      fetchData();
    } catch (error) {
      console.error(`Error al ${editingId ? 'actualizar' : 'agregar'} ${type}:`, error);
      alert(`Error al ${editingId ? 'actualizar' : 'agregar'} ${type}.`);
    }
  };

  const handleEdit = (id) => {
    const itemToEdit = data.find((item) => item.id === id);
    if (itemToEdit) {
      setFormData(itemToEdit);
      setEditingId(id);
      setShowForm(true);
    }
  };

  const handleDelete = (id) => {
    setShowModal(true);
    setModalMessage(`¿Estás seguro de que quieres eliminar este ${resourceType}?`);
    setModalAction(() => async () => {
      try {
        const token = localStorage.getItem('authToken');
        if (!token) return navigate('/login');
        await axios.delete(`${apiEndpoints[resourceType].base}/${id}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        alert(`${resourceType} eliminado exitosamente.`);
        fetchData();
      } catch (error) {
        console.error(`Error al eliminar ${resourceType}:`, error);
        alert(`Error al eliminar ${resourceType}.`);
      } finally {
        setShowModal(false);
      }
    });
  };

  const handleCancelEdit = () => {
    setEditingId(null);
    setShowForm(false);
    setFormData({});
  };

  const handleAdd = () => {
    setEditingId(null);
    setFormData({});
    setShowForm(true);
  };

  return (
    <>
      {showForm ? (
        <ResourceForm
          resourceType={resourceType}
          formData={formData}
          categories={categories}
          handleInputChange={handleInputChange}
          handleFormSubmit={handleAddEditSubmit}
          editingId={editingId}
          handleCancelEdit={handleCancelEdit}
        />
      ) : (
        <ResourceList
          resourceType={resourceType}
          data={data}
          categories={categories}
          userRole={userRole}
          onEdit={handleEdit}
          onDelete={handleDelete}
          onAdd={handleAdd}
        />
      )}
      {showModal && (
        <Modal
          message={modalMessage}
          onConfirm={modalAction}
          onCancel={() => setShowModal(false)}
        />
      )}
    </>
  );
}