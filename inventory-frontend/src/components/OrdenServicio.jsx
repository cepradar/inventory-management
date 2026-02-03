import React, { useState, useEffect } from 'react';
import {
  PlusIcon,
  TrashIcon,
  ChevronLeftIcon,
  CheckIcon,
  ExclamationTriangleIcon
} from '@heroicons/react/24/outline';
import api from './utils/axiosConfig';

export default function OrdenServicio() {
  // Pestañas
  const [currentTab, setCurrentTab] = useState(1);

  // Tab 1: Validación
  const [clientes, setClientes] = useState([]);
  const [selectedCliente, setSelectedCliente] = useState(null);
  const [clienteElectrodomesticos, setClienteElectrodomesticos] = useState([]);
  const [selectedElectrodomestico, setSelectedElectrodomestico] = useState(null);

  // Tab 2: Productos
  const [productosDisponibles, setProductosDisponibles] = useState([]);
  const [selectedProducto, setSelectedProducto] = useState('');
  const [ordenItems, setOrdenItems] = useState([]); // Items agregados a la orden
  const [descripcionProblema, setDescripcionProblema] = useState('');

  // Estados y validación
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);

  // Cargar datos iniciales
  useEffect(() => {
    const fetchInitialData = async () => {
      try {
        setLoading(true);
        const [clientesRes, productosRes] = await Promise.all([
          api.get('/api/clientes/listar'),
          api.get('/api/products/listar')
        ]);

        setClientes(clientesRes.data || []);
        setProductosDisponibles(productosRes.data || []);
      } catch (err) {
        setError('Error al cargar datos iniciales: ' + err.message);
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchInitialData();
  }, []);

  // Cargar electrodomésticos cuando se selecciona un cliente
  useEffect(() => {
    if (selectedCliente) {
      const fetchElectrodomesticos = async () => {
        try {
          // Usar documento como clienteId (es el ID en el backend)
          const response = await api.get(`/api/cliente-electrodomestico/cliente/${selectedCliente.documento}`);
          setClienteElectrodomesticos(response.data || []);
          setSelectedElectrodomestico(null); // Reset selection
        } catch (err) {
          console.error('Error al cargar electrodomésticos:', err);
          setClienteElectrodomesticos([]);
        }
      };

      fetchElectrodomesticos();
    } else {
      setClienteElectrodomesticos([]);
      setSelectedElectrodomestico(null);
    }
  }, [selectedCliente]);

  // Validación de Tab 1
  const isTab1Valid = () => selectedCliente && selectedElectrodomestico;

  // Agregar producto a la orden
  const handleAddProducto = () => {
    if (!selectedProducto) {
      setError('Selecciona un producto');
      return;
    }

    const producto = productosDisponibles.find(p => p.id === selectedProducto);
    const nuevoItem = {
      id: Date.now(), // ID temporal para el frontend
      productId: selectedProducto,
      productNombre: producto?.name || 'Producto',
      cantidad: 1,
      precioUnitario: producto?.price
    };

    setOrdenItems([...ordenItems, nuevoItem]);
    setSelectedProducto('');
    setError(null);
  };

  // Remover producto de la orden
  const handleRemoveItem = (itemId) => {
    setOrdenItems(ordenItems.filter(item => item.id !== itemId));
  };

  // Guardar la orden
  const handleSubmitOrden = async () => {
    try {
      if (!isTab1Valid() || ordenItems.length === 0 || !descripcionProblema.trim()) {
        setError('Debe seleccionar cliente, electrodoméstico, agregar al menos un producto y describir el problema');
        return;
      }

      setLoading(true);

      // Preparar payload según la estructura del backend
      const payload = {
        clienteId: selectedCliente.documento, // El documento es el ID del cliente
        electrodomesticoId: selectedElectrodomestico.id,
        tipoServicio: 'REPARACION', // O el valor que corresponda
        descripcionProblema: descripcionProblema.trim(),
        productos: ordenItems.map(item => ({
          productId: item.productId,
          cantidad: item.cantidad || 1,
          precioUnitario: item.precioUnitario
        }))
      };

      const response = await api.post('/api/servicios-reparacion/registrar', payload);
      
      setSuccessMessage(`Orden de servicio creada exitosamente. ID: ${response.data.id || 'N/A'}`);
      
      // Reset form
      setSelectedCliente(null);
      setSelectedElectrodomestico(null);
      setOrdenItems([]);
      setDescripcionProblema('');
      setCurrentTab(1);

      setTimeout(() => setSuccessMessage(null), 5000);
    } catch (err) {
      setError('Error al crear la orden: ' + (err.response?.data?.message || err.message));
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 p-4 md:p-8">
      <div className="max-w-6xl mx-auto">
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-3xl font-bold text-gray-800">Nueva Orden de Servicio</h1>
          <button
            onClick={() => window.history.back()}
            className="flex items-center gap-2 bg-gray-600 hover:bg-gray-700 text-white px-4 py-2 rounded"
          >
            <ChevronLeftIcon className="w-4 h-4" />
            Atrás
          </button>
        </div>

        {/* Mensajes de estado */}
        {error && (
          <div className="mb-4 p-4 bg-red-100 border border-red-400 text-red-700 rounded flex items-center gap-2">
            <ExclamationTriangleIcon className="w-5 h-5" />
            {error}
          </div>
        )}

        {successMessage && (
          <div className="mb-4 p-4 bg-green-100 border border-green-400 text-green-700 rounded flex items-center gap-2">
            <CheckIcon className="w-5 h-5" />
            {successMessage}
          </div>
        )}

        {/* Pestaña 1: Validación */}
        <div className={`${currentTab === 1 ? 'block' : 'hidden'} bg-white rounded-lg shadow-lg p-6`}>
          <h2 className="text-2xl font-semibold mb-6 text-gray-800">Paso 1: Selecciona Cliente y Electrodoméstico</h2>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Selección de Cliente */}
            <div>
              <label className="block text-sm font-semibold mb-2 text-gray-700">Cliente *</label>
              <select
                value={selectedCliente ? selectedCliente.documento : ''}
                onChange={(e) => {
                  const cliente = clientes.find(c => c.documento === e.target.value);
                  setSelectedCliente(cliente || null);
                }}
                className="w-full border-2 border-gray-300 rounded px-4 py-2 focus:border-blue-500 focus:outline-none"
              >
                <option value="">Seleccionar cliente...</option>
                {clientes.map((cliente) => (
                  <option key={cliente.documento} value={cliente.documento}>
                    {cliente.nombre} {cliente.apellido} - {cliente.documento}
                  </option>
                ))}
              </select>
            </div>

            {/* Selección de Electrodoméstico */}
            <div>
              <label className="block text-sm font-semibold mb-2 text-gray-700">Electrodoméstico *</label>
              <select
                value={selectedElectrodomestico ? selectedElectrodomestico.id : ''}
                onChange={(event) => {
                  const selectedId = Number(event.target.value);
                  const electro = clienteElectrodomesticos.find((item) => item.id === selectedId);
                  setSelectedElectrodomestico(electro || null);
                }}
                disabled={!selectedCliente}
                className="w-full border-2 border-gray-300 rounded px-4 py-2 focus:border-blue-500 focus:outline-none disabled:bg-gray-100 disabled:cursor-not-allowed"
              >
                <option value="">Seleccionar electrodoméstico...</option>
                {clienteElectrodomesticos.map((electro) => (
                  <option key={electro.id} value={electro.id}>
                    {electro.electrodomesticoTipo} - {electro.electrodomesticoModelo} ({electro.numeroSerie})
                  </option>
                ))}
              </select>
            </div>
          </div>

          {/* Información del cliente y electrodoméstico */}
          {selectedCliente && selectedElectrodomestico && (
            <div className="mt-6 p-4 bg-blue-50 border-l-4 border-blue-500 rounded">
              <h3 className="font-semibold text-gray-800 mb-2">Información Confirmada:</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
                <div>
                  <p className="text-gray-600">Cliente:</p>
                  <p className="font-semibold">{selectedCliente.nombre} {selectedCliente.apellido}</p>
                  <p className="text-gray-600">Documento: {selectedCliente.documento}</p>
                  <p className="text-gray-600">Teléfono: {selectedCliente.telefono}</p>
                </div>
                <div>
                  <p className="text-gray-600">Electrodoméstico:</p>
                  <p className="font-semibold">{selectedElectrodomestico.electrodomesticoTipo}</p>
                  <p className="text-gray-600">Modelo: {selectedElectrodomestico.electrodomesticoModelo}</p>
                  <p className="text-gray-600">Serie: {selectedElectrodomestico.numeroSerie}</p>
                </div>
              </div>
            </div>
          )}

          {/* Botón para pasar a Tab 2 */}
          <div className="mt-6 flex justify-end">
            <button
              onClick={() => {
                if (isTab1Valid()) {
                  setCurrentTab(2);
                  setError(null);
                } else {
                  setError('Debes seleccionar un cliente y un electrodoméstico');
                }
              }}
              className="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded font-semibold"
            >
              Continuar a Productos →
            </button>
          </div>
        </div>

        {/* Pestaña 2: Productos */}
        <div className={`${currentTab === 2 ? 'block' : 'hidden'} bg-white rounded-lg shadow-lg p-6`}>
          <h2 className="text-2xl font-semibold mb-6 text-gray-800">Paso 2: Agregue Productos</h2>

          {/* Resumen del paso 1 */}
          <div className="mb-6 p-4 bg-gray-100 rounded border border-gray-300">
            <p className="text-sm font-semibold text-gray-700">
              Orden para: <span className="text-blue-600">{selectedCliente?.nombre}</span> - 
              <span className="text-blue-600"> {selectedElectrodomestico?.electrodomesticoTipo}</span>
            </p>
          </div>

          {/* Descripción del problema */}
          <div className="mb-6 p-4 bg-yellow-50 border-l-4 border-yellow-500 rounded">
            <h3 className="font-semibold text-gray-800 mb-3">Descripción del Problema *</h3>
            <textarea
              value={descripcionProblema}
              onChange={(e) => setDescripcionProblema(e.target.value)}
              rows={3}
              className="w-full border-2 border-gray-300 rounded px-3 py-2 focus:border-blue-500"
              placeholder="Describe el problema reportado por el cliente..."
              required
            />
          </div>

          {/* Agregar productos */}
          <div className="mb-6 p-4 bg-blue-50 border-l-4 border-blue-500 rounded">
            <h3 className="font-semibold text-gray-800 mb-4">Agregar Producto</h3>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
              {/* Selector de producto */}
              <div>
                <label className="block text-sm font-medium mb-1">Producto *</label>
                <select
                  value={selectedProducto}
                  onChange={(e) => setSelectedProducto(e.target.value)}
                  className="w-full border-2 border-gray-300 rounded px-3 py-2 focus:border-blue-500"
                >
                  <option value="">Seleccionar producto...</option>
                  {productosDisponibles.map((item) => (
                    <option key={item.id} value={item.id}>
                      {item.name} {item.price ? `- $${item.price}` : ''}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <button
              onClick={handleAddProducto}
              className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded flex items-center gap-2 font-semibold"
            >
              <PlusIcon className="w-4 h-4" />
              Agregar a Orden
            </button>
          </div>

          {/* Tabla de productos agregados */}
          <div className="mb-6">
            <h3 className="font-semibold text-gray-800 mb-3">Productos en la Orden ({ordenItems.length})</h3>

            {ordenItems.length > 0 ? (
              <table className="w-full border-collapse border border-gray-300">
                <thead className="bg-gray-200">
                  <tr>
                    <th className="border border-gray-300 px-4 py-2 text-left">Producto</th>
                    <th className="border border-gray-300 px-4 py-2 text-left">Cantidad</th>
                    <th className="border border-gray-300 px-4 py-2 text-center">Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {ordenItems.map((item) => (
                    <tr key={item.id} className="border border-gray-300 hover:bg-gray-50">
                      <td className="border border-gray-300 px-4 py-2">{item.productNombre}</td>
                      <td className="border border-gray-300 px-4 py-2">{item.cantidad}</td>
                      <td className="border border-gray-300 px-4 py-2 text-center">
                        <button
                          onClick={() => handleRemoveItem(item.id)}
                          className="bg-red-600 hover:bg-red-700 text-white p-2 rounded inline-flex"
                        >
                          <TrashIcon className="w-4 h-4" />
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : (
              <div className="text-center py-8 text-gray-500 border border-dashed border-gray-300 rounded">
                <p>No hay productos agregados aún</p>
              </div>
            )}
          </div>

          {/* Botones de acción */}
          <div className="flex gap-4 justify-between">
            <button
              onClick={() => {
                setCurrentTab(1);
                setError(null);
              }}
              className="bg-gray-600 hover:bg-gray-700 text-white px-6 py-2 rounded font-semibold flex items-center gap-2"
            >
              <ChevronLeftIcon className="w-4 h-4" />
              ← Atrás
            </button>

            <button
              onClick={handleSubmitOrden}
              disabled={!isTab1Valid() || ordenItems.length === 0 || loading}
              className="bg-green-600 hover:bg-green-700 text-white px-6 py-2 rounded font-semibold disabled:bg-gray-400 disabled:cursor-not-allowed"
            >
              {loading ? 'Guardando...' : 'Crear Orden de Servicio'}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
