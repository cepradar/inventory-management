import React, { useState, useEffect } from 'react';
import api from './utils/axiosConfig'; // Axios con interceptor JWT
import Modal from './Modal';
import { useNavigate } from 'react-router-dom';
import { PencilIcon, TrashIcon } from '@heroicons/react/24/outline';
import DataTable from './DataTable';

// Formulario dinámico
const ResourceForm = ({ resourceType, formData, categories, handleInputChange, handleFormSubmit, editingId, handleCancelEdit }) => {
  return (
    <form onSubmit={(e) => handleFormSubmit(e, resourceType)} className="p-4 border rounded shadow-sm">
      <h3 className="text-xl font-bold mb-4">{editingId ? `Editar ${resourceType}` : `Crear ${resourceType}`}</h3>
      {resourceType === 'products' ? (
        <>
          <input
            type="text"
            name="id"
            placeholder="ID del Producto"
            value={formData.id || ''}
            onChange={handleInputChange}
            className="w-full p-2 mb-2 border rounded"
            required
            readOnly={!!editingId}   // 👈 solo se puede editar en creación
          />
          <input type="text" name="name" placeholder="Nombre del Producto" value={formData.name || ''} onChange={handleInputChange} className="w-full p-2 mb-2 border rounded" required />
          <input type="text" name="description" placeholder="Descripción del Producto" value={formData.description || ''} onChange={handleInputChange} className="w-full p-2 mb-2 border rounded" required />
          <input type="number" name="price" placeholder="Precio" value={formData.price || ''} onChange={handleInputChange} className="w-full p-2 mb-2 border rounded" required />
          <input type="number" name="quantity" placeholder="Cantidad" value={formData.quantity || ''} onChange={handleInputChange} className="w-full p-2 mb-2 border rounded" required />
          <select name="categoryId" value={formData.categoryId || ''} onChange={handleInputChange} className="w-full p-2 mb-2 border rounded" required>
            <option value="">Selecciona una categoría</option>
            {categories.map(cat => <option key={cat.id} value={cat.id}>{cat.name}</option>)}
          </select>
        </>
      ) : (
        <>
          <input type="text" name="name" placeholder="Nombre de la Categoría" value={formData.name || ''} onChange={handleInputChange} className="w-full p-2 mb-2 border rounded" required />
          <input type="text" name="description" placeholder="Descripción de la Categoría" value={formData.description || ''} onChange={handleInputChange} className="w-full p-2 mb-2 border rounded" required />
        </>
      )}
      <button type="submit" className="bg-blue-500 text-white p-2 rounded mr-2">{editingId ? 'Actualizar' : 'Añadir'}</button>
      <button type="button" onClick={handleCancelEdit} className="bg-gray-500 text-white p-2 rounded">Cancelar</button>
    </form>
  );
};

const ResourceList = ({ resourceType, data, categories, userRole, onEdit, onDelete, onAdd }) => {
  const isAdmin = userRole === 'ADMIN';
  const getCategoryName = (categoryId) => categories.find(cat => cat.id === categoryId)?.name || 'N/A';

  return (
    <div className="p-4">
      <div className="flex justify-between items-center mb-4 flex-wrap gap-2">
        <h3 className="text-lg md:text-xl font-bold">Listado de {resourceType === 'products' ? 'Productos' : 'Categorías'}</h3>
        {isAdmin && <button onClick={onAdd} className="bg-green-500 text-white p-2 rounded text-sm md:text-base">Crear {resourceType === 'products' ? 'Producto' : 'Categoría'}</button>}
      </div>
      
      <DataTable
        title={resourceType === 'products' ? 'Productos' : 'Categorías'}
        data={data}
        columns={
          resourceType === 'products'
            ? [
                { key: 'id', label: 'ID', sortable: true, filterable: true },
                { key: 'name', label: 'Nombre', sortable: true, filterable: true },
                { key: 'description', label: 'Descripción', sortable: true, filterable: true },
                { key: 'price', label: 'Precio', sortable: true, filterable: false },
                { key: 'quantity', label: 'Cantidad', sortable: true, filterable: false },
                {
                  key: 'categoryId',
                  label: 'Categoría',
                  sortable: true,
                  filterable: true,
                  render: (item) => getCategoryName(item.categoryId)
                },
                ...(isAdmin ? [{
                  key: 'acciones',
                  label: 'Acciones',
                  sortable: false,
                  filterable: false,
                  render: (item) => (
                    <div className="flex justify-center items-center gap-2 flex-nowrap">
                      <button 
                        onClick={() => onEdit(item.id)} 
                        className="inline-flex items-center justify-center bg-yellow-500 hover:bg-yellow-600 text-white p-2 rounded transition-colors flex-shrink-0"
                        title="Editar"
                      >
                        <PencilIcon className="w-4 h-4" />
                      </button>
                      <button 
                        onClick={() => onDelete(item.id)} 
                        className="inline-flex items-center justify-center bg-red-500 hover:bg-red-600 text-white p-2 rounded transition-colors flex-shrink-0"
                        title="Eliminar"
                      >
                        <TrashIcon className="w-4 h-4" />
                      </button>
                    </div>
                  )
                }] : [])
              ]
            : [
                { key: 'id', label: 'ID', sortable: true, filterable: true },
                { key: 'name', label: 'Nombre', sortable: true, filterable: true },
                { key: 'description', label: 'Descripción', sortable: true, filterable: true },
                ...(isAdmin ? [{
                  key: 'acciones',
                  label: 'Acciones',
                  sortable: false,
                  filterable: false,
                  render: (item) => (
                    <div className="flex justify-center items-center gap-2 flex-nowrap">
                      <button 
                        onClick={() => onEdit(item.id)} 
                        className="inline-flex items-center justify-center bg-yellow-500 hover:bg-yellow-600 text-white p-2 rounded transition-colors flex-shrink-0"
                        title="Editar"
                      >
                        <PencilIcon className="w-4 h-4" />
                      </button>
                      <button 
                        onClick={() => onDelete(item.id)} 
                        className="inline-flex items-center justify-center bg-red-500 hover:bg-red-600 text-white p-2 rounded transition-colors flex-shrink-0"
                        title="Eliminar"
                      >
                        <TrashIcon className="w-4 h-4" />
                      </button>
                    </div>
                  )
                }] : [])
              ]
        }
      />
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
  const [modalAction, setModalAction] = useState(() => { });
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const apiEndpoints = {
    products: { list: '/api/products/listar', base: '/api/products', eliminar: '/api/products/eliminar' },
    categories: { list: '/api/categories/listarCategoria', base: '/api/categories' },
  };

  const cargarProductos = async () => {
    setLoading(true);
    try {
      const response = await api.get(apiEndpoints[resourceType].list);
      setData(response.data);
    } catch (error) {
      console.error(`Error al cargar ${resourceType}:`, error);
      if (error.response && error.response.status === 403) {
        alert('No tienes permiso. Redirigiendo al login.');
        localStorage.clear();
        navigate('/login');
      }
    } finally {
      setLoading(false);
    }
  };

  const agregarEditarProductos = async (e, type) => {
    e.preventDefault();

    const payload = {
      id: editingId ? editingId : (formData.id || Date.now().toString()), // 👈 mismo id si edita
      name: formData.name,
      description: formData.description,
      price: parseFloat(formData.price),
      quantity: parseInt(formData.quantity),
      categoryId: formData.categoryId
    };

    try {
      const method = editingId ? 'put' : 'post';
      const url = editingId
        ? `${apiEndpoints[type].base}/actualizar/${editingId}`
        : `${apiEndpoints[type].base}/agregar`;

      console.log('[CrudManager] Payload enviado:', payload);
      await api[method](url, payload);

      alert(`${type} ${editingId ? 'actualizado' : 'agregado'} exitosamente.`);
      setFormData({});
      setEditingId(null);
      setShowForm(false);
      cargarProductos();
    } catch (error) {
      console.error(`Error al ${editingId ? 'actualizar' : 'agregar'} ${type}:`, error);
      alert(`Error al ${editingId ? 'actualizar' : 'agregar'} ${type}.`);
    }
  };

  const cargarCategorias = async () => {
    try {
      const response = await api.get(apiEndpoints.categories.list);
      setCategories(response.data);
    } catch (error) {
      console.error('Error al cargar categorías:', error);
    }
  };

  useEffect(() => {
    cargarProductos();
    if (resourceType === 'products') cargarCategorias();
  }, [resourceType]);

  const handleInputChange = (e) => setFormData({ ...formData, [e.target.name]: e.target.value });
  const handleEdit = (id) => {
    const itemToEdit = data.find(item => item.id === id);
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
        await api.delete(apiEndpoints[resourceType].eliminar, {
          data: { id }
        });

        alert(`${resourceType} eliminado exitosamente.`);
        cargarProductos();
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
    setFormData({
      id: Date.now().toString(),  // 👈 generamos ID por defecto
      name: '',
      description: '',
      price: '',
      quantity: '',
      categoryId: ''
    });
    setShowForm(true);
  };

  if (loading) return <div className="p-8 text-center">Cargando {resourceType}...</div>;

  return (
    <>
      {showForm ? (
        <ResourceForm
          resourceType={resourceType}
          formData={formData}
          categories={categories}
          handleInputChange={handleInputChange}
          handleFormSubmit={agregarEditarProductos}
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
