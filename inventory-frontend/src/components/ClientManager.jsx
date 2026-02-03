import React, { useEffect, useState } from 'react';
import api from './utils/axiosConfig.jsx';
import { PlusIcon, PencilIcon, TrashIcon, ChevronDownIcon, ChevronUpIcon } from '@heroicons/react/24/outline';
import DataTable from './DataTable';

export default function ClientManager() {
  const [clients, setClients] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showForm, setShowForm] = useState(false);
  const [editingClient, setEditingClient] = useState(null);
  const [tiposDocumento, setTiposDocumento] = useState([]);
  const [marcas, setMarcas] = useState([]);
  const [categoriasElectrodomestico, setCategoriasElectrodomestico] = useState([]);
  const [expandedClientId, setExpandedClientId] = useState(null);
  const [electrodomesticos, setElectrodomesticos] = useState([]);
  const [showElectroForm, setShowElectroForm] = useState(null);
  const [formData, setFormData] = useState({
    documento: '',
    nombre: '',
    apellido: '',
    telefono: '',
    email: '',
    direccion: '',
    ciudad: '',
    tipoDocumentoId: ''
  });
  const [electroFormData, setElectroFormData] = useState({
    clienteId: '',
    numeroSerie: '',
    marcaElectrodomesticoId: '',
    electrodomesticoTipo: '',
    electrodomesticoModelo: ''
  });

  useEffect(() => {
    fetchClients();
    fetchTiposDocumento();
    fetchMarcas();
    fetchElectrodomesticos();
    fetchCategoriasElectrodomestico();
  }, []);

  const fetchTiposDocumento = async () => {
    try {
      // Intentar obtener desde endpoint específico o usar valores por defecto
      setTiposDocumento([
        { id: 'CC', name: 'Cédula de Ciudadanía' },
        { id: 'NIT', name: 'Número de Identificación Tributaria' },
        { id: 'CE', name: 'Cédula de Extranjería' },
        { id: 'PASAPORTE', name: 'Pasaporte' },
        { id: 'TI', name: 'Tarjeta de Identidad' }
      ]);
    } catch (err) {
      console.error('Error al cargar tipos de documento:', err);
    }
  };

  const fetchMarcas = async () => {
    try {
      const res = await api.get('/api/marcas-electrodomestico/listar');
      setMarcas(res.data || []);
    } catch (err) {
      console.error('Error al cargar marcas:', err);
      setMarcas([]);
    }
  };

  const fetchCategoriasElectrodomestico = async () => {
    try {
      const res = await api.get('/api/categorias-electrodomestico/listar');
      setCategoriasElectrodomestico(res.data || []);
    } catch (err) {
      console.error('Error al cargar categorías de electrodoméstico:', err);
      setCategoriasElectrodomestico([]);
    }
  };

  const fetchElectrodomesticos = async () => {
    try {
      const res = await api.get('/api/cliente-electrodomestico/listar');
      setElectrodomesticos(res.data || []);
    } catch (err) {
      console.error('Error al cargar electrodomésticos:', err);
    }
  };

  const fetchClients = async () => {
    setLoading(true);
    try {
      const res = await api.get('/api/clientes/listar');
      setClients(res.data || []);
    } catch (err) {
      console.error('Error al cargar clientes:', err);
    } finally {
      setLoading(false);
    }
  };

  const resetForm = () => {
    setFormData({
      documento: '',
      nombre: '',
      apellido: '',
      telefono: '',
      email: '',
      direccion: '',
      ciudad: '',
      tipoDocumentoId: 'CC'
    });
    setEditingClient(null);
    setShowForm(false);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingClient) {
        await api.put(`/api/clientes/actualizar/${editingClient.documento}`, formData);
      } else {
        await api.post('/api/clientes/crear', formData);
      }
      fetchClients();
      resetForm();
    } catch (err) {
      console.error('Error al guardar cliente:', err);
      alert('Error al guardar el cliente: ' + (err.response?.data?.message || err.message));
    }
  };

  const handleEdit = (client) => {
    setEditingClient(client);
    setFormData({
      documento: client.documento || '',
      nombre: client.nombre || '',
      apellido: client.apellido || '',
      telefono: client.telefono || '',
      email: client.email || '',
      direccion: client.direccion || '',
      ciudad: client.ciudad || '',
      tipoDocumentoId: client.tipoDocumentoId || 'CC'
    });
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    if (!confirm('¿Eliminar cliente?')) return;
    try {
      await api.delete(`/api/clientes/eliminar/${id}`);
      fetchClients();
    } catch (err) {
      console.error('Error al eliminar:', err);
      alert('Error al eliminar el cliente');
    }
  };

  const handleElectroInputChange = (e) => {
    const { name, value } = e.target;
    setElectroFormData(prev => ({ ...prev, [name]: value }));
  };

  const resetElectroForm = () => {
    setElectroFormData({
      clienteId: '',
      numeroSerie: '',
      marcaElectrodomesticoId: '',
      electrodomesticoTipo: '',
      electrodomesticoModelo: ''
    });
    setShowElectroForm(null);
  };

  const handleAddElectrodomestico = (clienteId) => {
    setElectroFormData(prev => ({ ...prev, clienteId }));
    setShowElectroForm(clienteId);
  };

  const handleSubmitElectrodomestico = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        clienteId: electroFormData.clienteId,
        numeroSerie: electroFormData.numeroSerie,
        marcaElectrodomesticoId: parseInt(electroFormData.marcaElectrodomesticoId),
        electrodomesticoTipo: electroFormData.electrodomesticoTipo,
        electrodomesticoModelo: electroFormData.electrodomesticoModelo
      };
      await api.post('/api/cliente-electrodomestico/registrar', payload);
      fetchElectrodomesticos();
      resetElectroForm();
      alert('✅ Electrodoméstico registrado exitosamente');
    } catch (err) {
      console.error('Error al registrar electrodoméstico:', err);
      const mensaje = err.response?.data?.message || err.response?.data || err.message || 'Error desconocido';
      alert('❌ ' + mensaje);
    }
  };

  const getClientElectrodomesticos = (clienteId) => {
    return electrodomesticos.filter(e => e.clienteId === clienteId);
  };

  if (loading) return <div className="text-center p-4">Cargando...</div>;

  return (
    <div className="p-4">
      <div className="flex justify-between items-center mb-4">
        <h3 className="text-lg font-semibold">Gestión de Clientes</h3>
        <button
          onClick={() => setShowForm(!showForm)}
          className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded flex items-center gap-2"
        >
          <PlusIcon className="w-5 h-5" />
          {showForm ? 'Cancelar' : 'Nuevo Cliente'}
        </button>
      </div>

      {showForm && (
        <div className="bg-gray-50 p-4 rounded-lg mb-4 border">
          <h4 className="font-semibold mb-3">{editingClient ? 'Editar Cliente' : 'Nuevo Cliente'}</h4>
          <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium mb-1">Tipo de Documento *</label>
              <select
                name="tipoDocumentoId"
                value={formData.tipoDocumentoId}
                onChange={handleInputChange}
                required
                className="w-full border rounded px-3 py-2"
              >
                <option value="">Seleccionar tipo...</option>
                {tiposDocumento.map((tipo) => (
                  <option key={tipo.id} value={tipo.id}>
                    {tipo.name}
                  </option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Documento/NIT *</label>
              <input
                type="text"
                name="documento"
                value={formData.documento}
                onChange={handleInputChange}
                required
                disabled={!!editingClient}
                className="w-full border rounded px-3 py-2 disabled:bg-gray-200"
                placeholder="Número de identificación"
              />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Nombre *</label>
              <input
                type="text"
                name="nombre"
                value={formData.nombre}
                onChange={handleInputChange}
                required
                className="w-full border rounded px-3 py-2"
              />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Apellido *</label>
              <input
                type="text"
                name="apellido"
                value={formData.apellido}
                onChange={handleInputChange}
                required
                className="w-full border rounded px-3 py-2"
              />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Teléfono *</label>
              <input
                type="tel"
                name="telefono"
                value={formData.telefono}
                onChange={handleInputChange}
                required
                className="w-full border rounded px-3 py-2"
              />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Email</label>
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleInputChange}
                className="w-full border rounded px-3 py-2"
              />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Dirección</label>
              <input
                type="text"
                name="direccion"
                value={formData.direccion}
                onChange={handleInputChange}
                className="w-full border rounded px-3 py-2"
              />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Ciudad</label>
              <input
                type="text"
                name="ciudad"
                value={formData.ciudad}
                onChange={handleInputChange}
                className="w-full border rounded px-3 py-2"
              />
            </div>
            <div className="md:col-span-2 flex gap-2">
              <button
                type="submit"
                className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded"
              >
                {editingClient ? 'Actualizar' : 'Guardar'}
              </button>
              <button
                type="button"
                onClick={resetForm}
                className="bg-gray-500 hover:bg-gray-600 text-white px-4 py-2 rounded"
              >
                Cancelar
              </button>
            </div>
          </form>
        </div>
      )}

      <DataTable
        title="Clientes Registrados"
        data={clients}
        columns={[
          {
            key: 'expandir',
            label: '',
            sortable: false,
            filterable: false,
            render: (client) => (
              <button
                onClick={() => setExpandedClientId(expandedClientId === client.documento ? null : client.documento)}
                className="bg-blue-500 hover:bg-blue-600 text-white p-2 rounded"
              >
                {expandedClientId === client.documento ? (
                  <ChevronUpIcon className="w-4 h-4" />
                ) : (
                  <ChevronDownIcon className="w-4 h-4" />
                )}
              </button>
            )
          },
          { key: 'documento', label: 'Documento/NIT', sortable: true, filterable: true },
          { key: 'nombre', label: 'Nombre', sortable: true, filterable: true },
          { key: 'apellido', label: 'Apellido', sortable: true, filterable: true },
          { key: 'telefono', label: 'Teléfono', sortable: true, filterable: true },
          { key: 'email', label: 'Email', sortable: true, filterable: true },
          {
            key: 'acciones',
            label: 'Acciones',
            sortable: false,
            filterable: false,
            render: (client) => (
              <div className="flex justify-center gap-2">
                <button
                  onClick={() => handleEdit(client)}
                  className="bg-yellow-500 hover:bg-yellow-600 text-white p-2 rounded inline-flex"
                  title="Editar"
                >
                  <PencilIcon className="w-4 h-4" />
                </button>
                <button
                  onClick={() => handleDelete(client.documento)}
                  className="bg-red-500 hover:bg-red-600 text-white p-2 rounded inline-flex"
                  title="Eliminar"
                >
                  <TrashIcon className="w-4 h-4" />
                </button>
              </div>
            )
          }
        ]}
      />

      {/* Sección de Electrodomésticos expandida */}
      {expandedClientId && (
        <div className="mt-6 border rounded-lg p-4 bg-gray-50">
          {clients.find(c => c.documento === expandedClientId) && (
            <>
              <div className="flex justify-between items-center mb-4">
                <h4 className="text-lg font-semibold">
                  Electrodomésticos - {clients.find(c => c.documento === expandedClientId)?.nombre}
                </h4>
                <button
                  onClick={() => handleAddElectrodomestico(expandedClientId)}
                  className="bg-blue-600 hover:bg-blue-700 text-white px-3 py-2 rounded flex items-center gap-2"
                >
                  <PlusIcon className="w-4 h-4" />
                  Agregar Electrodoméstico
                </button>
              </div>

              {showElectroForm === expandedClientId && (
                <div className="bg-white p-4 rounded-lg mb-4 border-l-4 border-blue-500">
                  <h5 className="font-semibold mb-3">Nuevo Electrodoméstico</h5>
                  <form onSubmit={handleSubmitElectrodomestico} className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <label className="block text-sm font-medium mb-1">Marca *</label>
                      <select
                        name="marcaElectrodomesticoId"
                        value={electroFormData.marcaElectrodomesticoId}
                        onChange={handleElectroInputChange}
                        required
                        className="w-full border rounded px-3 py-2"
                      >
                        <option value="">Seleccionar marca...</option>
                        {marcas.map((marca) => (
                          <option key={marca.id} value={marca.id}>
                            {marca.nombre}
                          </option>
                        ))}
                      </select>
                    </div>
                    <div>
                      <label className="block text-sm font-medium mb-1">Categoría (Tipo) *</label>
                      <select
                        name="electrodomesticoTipo"
                        value={electroFormData.electrodomesticoTipo}
                        onChange={handleElectroInputChange}
                        required
                        className="w-full border rounded px-3 py-2"
                      >
                        <option value="">Seleccionar categoría...</option>
                        {categoriasElectrodomestico.map((cat) => (
                          <option key={cat.id} value={cat.nombre}>
                            {cat.nombre}
                          </option>
                        ))}
                      </select>
                    </div>
                    <div>
                      <label className="block text-sm font-medium mb-1">Modelo *</label>
                      <input
                        type="text"
                        name="electrodomesticoModelo"
                        value={electroFormData.electrodomesticoModelo}
                        onChange={handleElectroInputChange}
                        required
                        className="w-full border rounded px-3 py-2"
                        placeholder="Modelo del electrodoméstico"
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-medium mb-1">Número de Serie *</label>
                      <input
                        type="text"
                        name="numeroSerie"
                        value={electroFormData.numeroSerie}
                        onChange={handleElectroInputChange}
                        required
                        className="w-full border rounded px-3 py-2"
                        placeholder="Número de serie único"
                      />
                    </div>
                    <div className="md:col-span-2 flex gap-2">
                      <button
                        type="submit"
                        className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded"
                      >
                        Guardar Electrodoméstico
                      </button>
                      <button
                        type="button"
                        onClick={resetElectroForm}
                        className="bg-gray-500 hover:bg-gray-600 text-white px-4 py-2 rounded"
                      >
                        Cancelar
                      </button>
                    </div>
                  </form>
                </div>
              )}

              <div className="bg-white rounded-lg overflow-hidden">
                <table className="w-full text-sm">
                  <thead className="bg-gray-200">
                    <tr>
                      <th className="px-4 py-2 text-left">Tipo</th>
                      <th className="px-4 py-2 text-left">Marca</th>
                      <th className="px-4 py-2 text-left">Modelo</th>
                      <th className="px-4 py-2 text-left">Número Serie</th>
                      <th className="px-4 py-2 text-left">Estado</th>
                    </tr>
                  </thead>
                  <tbody>
                    {getClientElectrodomesticos(expandedClientId).length > 0 ? (
                      getClientElectrodomesticos(expandedClientId).map((electro) => (
                        <tr key={electro.id} className="border-t hover:bg-gray-50">
                          <td className="px-4 py-2">{electro.electrodomesticoTipo}</td>
                          <td className="px-4 py-2">{electro.marcaElectrodomestico?.nombre}</td>
                          <td className="px-4 py-2">{electro.electrodomesticoModelo}</td>
                          <td className="px-4 py-2">{electro.numeroSerie}</td>
                          <td className="px-4 py-2">
                            <span className="bg-blue-200 text-blue-800 px-2 py-1 rounded text-xs">
                              {electro.estado}
                            </span>
                          </td>
                        </tr>
                      ))
                    ) : (
                      <tr className="border-t">
                        <td colSpan="5" className="px-4 py-3 text-center text-gray-500">
                          No hay electrodomésticos registrados
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            </>
          )}
        </div>
      )}
    </div>
  );
}
