import React, { useState, useEffect } from 'react';
import api from './utils/axiosConfig'; // Axios con interceptor JWT
import Modal from './Modal';
import { useNavigate } from 'react-router-dom';
import { PencilIcon, TrashIcon } from '@heroicons/react/24/outline';
import DataTable from './DataTable';

// Formulario dinámico con etiquetas y estándares de usabilidad mejorados
const ResourceForm = ({ resourceType, formData, categories, handleInputChange, handleFormSubmit, editingId, handleCancelEdit }) => {
  return (
    <form onSubmit={(e) => handleFormSubmit(e, resourceType)} className="max-w-2xl mx-auto p-4 md:p-6 bg-white border border-gray-300 rounded-lg shadow-md">
      <h2 className="text-xl md:text-2xl font-bold mb-4 md:mb-6 text-gray-800">
        {editingId ? `Editar ${resourceType === 'products' ? 'Producto' : 'Categoría'}` : `Crear ${resourceType === 'products' ? 'Producto' : 'Categoría'}`}
      </h2>
      
      <div className="space-y-4 md:space-y-5">
        {resourceType === 'products' ? (
          <>
            {/* Campo ID */}
            <div className="form-group">
              <label htmlFor="id" className="block text-xs md:text-sm font-semibold text-gray-700 mb-1">
                ID del Producto <span className="text-red-500">*</span>
              </label>
              <input
                id="id"
                type="text"
                name="id"
                value={formData.id || ''}
                onChange={handleInputChange}
                className="w-full px-3 py-1.5 text-sm border border-gray-300 rounded focus:outline-none focus:ring-1 focus:ring-blue-500 focus:border-transparent transition-all"
                placeholder="Ej: PROD-001"
                required
                readOnly={!!editingId}
                disabled={!!editingId}
              />
              {editingId && <p className="text-xs text-gray-500 mt-1">El ID no puede modificarse una vez creado</p>}
            </div>

            {/* Campo Nombre */}
            <div className="form-group">
              <label htmlFor="name" className="block text-xs md:text-sm font-semibold text-gray-700 mb-1">
                Nombre del Producto <span className="text-red-500">*</span>
              </label>
              <input
                id="name"
                type="text"
                name="name"
                value={formData.name || ''}
                onChange={handleInputChange}
                className="w-full px-3 py-1.5 text-sm border border-gray-300 rounded focus:outline-none focus:ring-1 focus:ring-blue-500 focus:border-transparent transition-all"
                placeholder="Ej: Laptop Dell XPS"
                required
              />
            </div>

            {/* Campo Descripción */}
            <div className="form-group">
              <label htmlFor="description" className="block text-xs md:text-sm font-semibold text-gray-700 mb-1">
                Descripción <span className="text-red-500">*</span>
              </label>
              <textarea
                id="description"
                name="description"
                value={formData.description || ''}
                onChange={handleInputChange}
                className="w-full px-3 py-1.5 text-sm border border-gray-300 rounded focus:outline-none focus:ring-1 focus:ring-blue-500 focus:border-transparent transition-all resize-none"
                placeholder="Describe brevemente las características del producto"
                  rows="2"
                required
              />
            </div>

            {/* Campo Categoría */}
            <div className="form-group">
              <label htmlFor="categoryId" className="block text-xs md:text-sm font-semibold text-gray-700 mb-1">
                Categoría <span className="text-red-500">*</span>
              </label>
              <select
                id="categoryId"
                name="categoryId"
                value={formData.categoryId || ''}
                onChange={handleInputChange}
                className="w-full px-3 py-1.5 text-sm border border-gray-300 rounded focus:outline-none focus:ring-1 focus:ring-blue-500 focus:border-transparent transition-all bg-white"
                required
              >
                <option value="">-- Selecciona una categoría --</option>
                {categories.map(cat => <option key={cat.id} value={cat.id}>{cat.name}</option>)}
              </select>
            </div>

            {/* Campo Cantidad */}
            <div className="form-group">
              <label htmlFor="quantity" className="block text-xs md:text-sm font-semibold text-gray-700 mb-1">
                Cantidad <span className="text-red-500">*</span>
              </label>
              <input
                id="quantity"
                type="number"
                name="quantity"
                value={formData.quantity || ''}
                onChange={handleInputChange}
                className="w-full px-3 py-1.5 text-sm border border-gray-300 rounded focus:outline-none focus:ring-1 focus:ring-blue-500 focus:border-transparent transition-all"
                placeholder="0"
                min="0"
                required
              />
            </div>

            {/* Campo Precio */}
            <div className="form-group">
              <label htmlFor="price" className="block text-xs md:text-sm font-semibold text-gray-700 mb-1">
                Precio ($) <span className="text-red-500">*</span>
              </label>
              <input
                id="price"
                type="number"
                name="price"
                value={formData.price || ''}
                onChange={handleInputChange}
                className="w-full px-3 py-1.5 text-sm border border-gray-300 rounded focus:outline-none focus:ring-1 focus:ring-blue-500 focus:border-transparent transition-all"
                placeholder="0.00"
                min="0"
                step="0.01"
                required
              />
            </div>
          </>
        ) : (
          <>
            {/* Categoría - Campo Nombre */}
            <div className="form-group">
              <label htmlFor="name" className="block text-xs md:text-sm font-semibold text-gray-700 mb-1">
                Nombre de la Categoría <span className="text-red-500">*</span>
              </label>
              <input
                id="name"
                type="text"
                name="name"
                value={formData.name || ''}
                onChange={handleInputChange}
                className="w-full px-3 py-1.5 text-sm border border-gray-300 rounded focus:outline-none focus:ring-1 focus:ring-blue-500 focus:border-transparent transition-all"
                placeholder="Ej: Electrónica"
                required
              />
            </div>

            {/* Categoría - Campo Descripción */}
            <div className="form-group">
              <label htmlFor="description" className="block text-xs md:text-sm font-semibold text-gray-700 mb-1">
                Descripción <span className="text-red-500">*</span>
              </label>
              <textarea
                id="description"
                name="description"
                value={formData.description || ''}
                onChange={handleInputChange}
                className="w-full px-3 py-1.5 text-sm border border-gray-300 rounded focus:outline-none focus:ring-1 focus:ring-blue-500 focus:border-transparent transition-all resize-none"
                placeholder="Describe brevemente esta categoría"
                rows="3"
                required
              />
            </div>
          </>
        )}
      </div>

      {/* Botones de acción */}
      <div className="flex flex-col sm:flex-row gap-2 mt-4 md:mt-6 pt-3 md:pt-4 border-t border-gray-200">
        <button
          type="submit"
          className="flex-1 bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 px-3 rounded transition-colors duration-200 text-xs md:text-sm"
        >
          {editingId ? '💾 Actualizar' : '✚ Crear'}
        </button>
        <button
          type="button"
          onClick={handleCancelEdit}
          className="flex-1 bg-gray-400 hover:bg-gray-500 text-white font-semibold py-2 px-3 rounded transition-colors duration-200 text-xs md:text-sm"
        >
          ✕ Cancelar
        </button>
      </div>

      {/* Indicador de campos requeridos */}
      <p className="text-xs text-gray-500 mt-2 text-center">
        Los campos marcados con <span className="text-red-500 font-bold">*</span> son obligatorios
      </p>
    </form>
  );
};

const ResourceList = ({ resourceType, data, categories, userRole, onEdit, onDelete, onAdd }) => {
  const isAdmin = userRole === 'ADMIN';
  const [isMobile, setIsMobile] = useState(false);
  const [mobileSearch, setMobileSearch] = useState('');
  const getCategoryName = (categoryId) => categories.find(cat => cat.id === categoryId)?.name || 'N/A';

  useEffect(() => {
    const checkMobile = () => setIsMobile(window.innerWidth < 768);
    checkMobile();
    window.addEventListener('resize', checkMobile);
    return () => window.removeEventListener('resize', checkMobile);
  }, []);

  const filteredData = React.useMemo(() => {
    if (!isMobile || resourceType !== 'products' || !mobileSearch.trim()) return data;
    const query = mobileSearch.toLowerCase();

    return data.filter((item) => {
      const categoryName = getCategoryName(item.categoryId);
      const values = [
        item?.id,
        item?.name,
        item?.description,
        categoryName,
        item?.categoriaElectrodomestico,
        item?.categoriaElectrodomesticoNombre,
        item?.categoriaElectrodomesticoName
      ]
        .filter(Boolean)
        .map((value) => String(value).toLowerCase());

      return values.some((value) => value.includes(query));
    });
  }, [data, isMobile, mobileSearch, resourceType, categories]);

  return (
    <div className="p-1 md:p-2">
      <div className="flex justify-between items-center mb-2 flex-wrap gap-2">
        <h3 className="text-sm md:text-base font-bold">Listado de {resourceType === 'products' ? 'Productos' : 'Categorías'}</h3>
        {isAdmin && <button onClick={onAdd} className="bg-green-500 hover:bg-green-600 text-white px-2 md:px-3 py-1.5 rounded text-xs md:text-sm transition-colors">Crear {resourceType === 'products' ? 'Producto' : 'Categoría'}</button>}
      </div>

      {isMobile && resourceType === 'products' && (
        <div className="md:hidden mb-2 space-y-2">
          <input
            type="text"
            value={mobileSearch}
            onChange={(e) => setMobileSearch(e.target.value)}
            placeholder="Buscar por ID, nombre, descripción o categoría..."
            className="w-full px-3 py-1.5 text-sm border border-gray-300 rounded focus:outline-none focus:ring-1 focus:ring-blue-500"
          />
          {isAdmin && (
            <button
              onClick={onAdd}
              className="w-full bg-green-500 hover:bg-green-600 text-white px-3 py-2 rounded text-sm transition-colors"
            >
              Crear Producto
            </button>
          )}
        </div>
      )}
      
      <DataTable
        data={filteredData}
        columns={
          resourceType === 'products'
            ? [
                { key: 'id', label: 'ID', sortable: true, filterable: true },
                { key: 'name', label: 'Nombre', sortable: true, filterable: true },
                { key: 'description', label: 'Descripción', sortable: true, filterable: true },
                {
                  key: 'categoryId',
                  label: 'Categoría',
                  sortable: true,
                  filterable: true,
                  render: (item) => getCategoryName(item.categoryId)
                },
                { key: 'quantity', label: 'Cantidad', sortable: true, filterable: false },
                { key: 'price', label: 'Precio', sortable: true, filterable: false },
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

export default function CrudManager({ resourceType, userRole, onFormStateChange }) {
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
      const response = await api[method](url, payload);

      // 🔔 Registrar evento de auditoría
      if (resourceType === 'products') {
        // Usar el ID del producto retornado por el backend (puede diferir del temporal)
        const productIdReal = response.data?.id || payload.id;
        await registrarEventoProducto(productIdReal, editingId ? 'ACTUALIZACION' : 'CREACION', payload.quantity);
      }

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

  // 🔔 Función para registrar eventos de auditoría
  const registrarEventoProducto = async (productId, tipoEvento, cantidad) => {
    try {
      const username = localStorage.getItem('username') || 'ADMIN';
      
      let tipo, descripcion;
      
      switch(tipoEvento) {
        case 'CREACION':
          tipo = 'CP';  // CREACION_PRODUCTO
          descripcion = `Producto creado con cantidad inicial: ${cantidad}`;
          break;
        case 'ACTUALIZACION':
          tipo = 'MA';  // MOVIMIENTO_AJUSTE
          descripcion = `Producto actualizado - Cantidad: ${cantidad}`;
          break;
        case 'ELIMINACION':
          tipo = 'EP';  // ELIMINACION_PRODUCTO
          descripcion = `Producto eliminado del inventario - Cantidad anterior: ${cantidad}`;
          break;
        default:
          tipo = 'MA';  // MOVIMIENTO_AJUSTE
          descripcion = `Operación ${tipoEvento} en producto`;
      }

      await api.post('/api/auditoria/registrar', null, {
        params: {
          productId: productId,
          cantidad: cantidad,
          tipo: tipo,
          descripcion: descripcion,
          usuarioUsername: username,
          referencia: `${tipoEvento}_PRODUCTO_${productId}_${Date.now()}`
        }
      });

      console.log(`✅ Evento ${tipoEvento} registrado para producto ${productId} por usuario ${username}`);
    } catch (error) {
      console.error('⚠️ Error al registrar evento de auditoría:', error);
      // No bloquear la operación principal si falla la auditoría
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

  // Notificar al Dashboard sobre cambios en el estado del formulario
  useEffect(() => {
    if (onFormStateChange) {
      onFormStateChange(showForm || editingId !== null);
    }
  }, [showForm, editingId, onFormStateChange]);

  // Resetear formulario cuando cambia el resourceType (cambio de pestaña)
  useEffect(() => {
    setShowForm(false);
    setEditingId(null);
    setFormData({});
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
        // Obtener datos del producto antes de eliminarlo para auditoría
        const productoAEliminar = data.find(item => item.id === id);
        
        await api.delete(apiEndpoints[resourceType].eliminar, {
          data: { id }
        });

        // 🔔 Registrar evento de auditoría para eliminación
        if (resourceType === 'products' && productoAEliminar) {
          await registrarEventoProducto(
            id, 
            'ELIMINACION', 
            productoAEliminar.quantity || 0
          );
        }

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
