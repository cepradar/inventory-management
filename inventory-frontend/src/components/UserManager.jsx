import React, { useState, useEffect } from 'react';
import api from './utils/axiosConfig';
import { useNavigate } from 'react-router-dom';
import { PencilIcon, TrashIcon } from '@heroicons/react/24/outline';
import DataTable from './DataTable';

const UserForm = ({ formData, handleInputChange, handleFormSubmit, editingId, handleCancelEdit, roles, handleDelete }) => {
  return (
    <form onSubmit={handleFormSubmit} className="p-4 border rounded shadow-sm">
      <h3 className="text-xl font-bold mb-4">{editingId ? 'Editar Usuario' : 'Crear Usuario'}</h3>
      
      <input
        type="text"
        name="username"
        placeholder="Nombre de usuario"
        value={formData.username || ''}
        onChange={handleInputChange}
        className="w-full p-2 mb-2 border rounded"
        required
        readOnly={!!editingId}
      />
      
      <input
        type="email"
        name="email"
        placeholder="Correo electrónico"
        value={formData.email || ''}
        onChange={handleInputChange}
        className="w-full p-2 mb-2 border rounded"
        required
      />
      
      <input
        type="tel"
        name="telefono"
        placeholder="Teléfono"
        value={formData.telefono || ''}
        onChange={handleInputChange}
        className="w-full p-2 mb-2 border rounded"
      />
      
      {!editingId && (
        <input
          type="password"
          name="password"
          placeholder="Contraseña"
          value={formData.password || ''}
          onChange={handleInputChange}
          className="w-full p-2 mb-2 border rounded"
          required
        />
      )}
      
      <select
        name="role"
        value={formData.role || ''}
        onChange={handleInputChange}
        className="w-full p-2 mb-2 border rounded"
        required
      >
        <option value="">Seleccionar rol</option>
        {roles.map(role => (
          <option key={role.name} value={role.name}>
            {role.name}
          </option>
        ))}
      </select>

      <input
        type="text"
        name="firstName"
        placeholder="Nombre"
        value={formData.firstName || ''}
        onChange={handleInputChange}
        className="w-full p-2 mb-2 border rounded"
      />

      <input
        type="text"
        name="lastName"
        placeholder="Apellido"
        value={formData.lastName || ''}
        onChange={handleInputChange}
        className="w-full p-2 mb-2 border rounded"
      />

      <button type="submit" className="bg-blue-500 text-white p-2 rounded mr-2">
        {editingId ? 'Actualizar' : 'Crear'}
      </button>
      {editingId && (
        <button type="button" onClick={() => handleDelete(editingId)} className="bg-red-500 text-white p-2 rounded mr-2">
          Eliminar
        </button>
      )}
      <button type="button" onClick={handleCancelEdit} className="bg-gray-500 text-white p-2 rounded">
        Cancelar
      </button>
    </form>
  );
};

const UserList = ({ data, onEdit, onDelete, onAdd }) => {
  return (
    <div className="p-4">
      <div className="flex justify-between items-center mb-4 flex-wrap gap-2">
        <h3 className="text-lg md:text-xl font-bold">Listado de Usuarios</h3>
        <button onClick={onAdd} className="bg-green-500 text-white p-2 rounded text-sm md:text-base">
          Crear Usuario
        </button>
      </div>

      <DataTable
        title="Usuarios"
        data={data}
        columns={[
          { key: 'id', label: 'ID', sortable: true, filterable: true },
          {
            key: 'fullName',
            label: 'Nombre Completo',
            sortable: true,
            filterable: true,
            render: (user) => `${user.firstName || ''} ${user.lastName || ''}`.trim()
          },
          { key: 'telefono', label: 'Teléfono', sortable: true, filterable: true },
          { key: 'email', label: 'Email', sortable: true, filterable: true },
          {
            key: 'role',
            label: 'Rol',
            sortable: true,
            filterable: true,
            render: (user) => (
              <span 
                className="px-2 py-1 rounded text-white text-xs font-bold"
                style={{ backgroundColor: user.roleColor || '#2563eb' }}
              >
                {user.role}
              </span>
            )
          },
          {
            key: 'acciones',
            label: '',
            sortable: false,
            filterable: false,
            width: 44,
            headerClassName: 'px-1',
            cellClassName: 'px-1',
            render: (user) => (
              <div className="flex justify-center items-center gap-1 flex-nowrap">
                <button 
                  onClick={() => onEdit(user.username)} 
                  className="inline-flex items-center justify-center bg-yellow-500 hover:bg-yellow-600 text-white p-1 rounded transition-colors flex-shrink-0"
                  title="Editar"
                >
                  <PencilIcon className="w-3.5 h-3.5" />
                </button>
              </div>
            )
          }
        ]}
      />
    </div>
  );
};

export default function UserManager({ forceShowForm = false }) {
  const [users, setUsers] = useState([]);
  const [roles, setRoles] = useState([]);
  const [showForm, setShowForm] = useState(forceShowForm);
  const [formData, setFormData] = useState({});
  const [editingId, setEditingId] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  // Cargar usuarios y roles al montar el componente
  useEffect(() => {
    fetchUsers();
    fetchRoles();
  }, []);

  // Actualizar showForm cuando forceShowForm cambie
  useEffect(() => {
    setShowForm(forceShowForm);
  }, [forceShowForm]);

  // Cargar usuarios al montar el componente
  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const response = await api.get('/api/users');
      setUsers(response.data);
    } catch (error) {
      console.error('Error al cargar usuarios:', error);
      alert('Error al cargar los usuarios');
    } finally {
      setLoading(false);
    }
  };

  const fetchRoles = async () => {
    try {
      const response = await api.get('/api/users/roles/available');
      setRoles(response.data);
    } catch (error) {
      console.error('Error al cargar roles:', error);
      alert('Error al cargar los roles');
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleFormSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingId) {
        await api.put(`/api/users/${editingId}`, formData);
      } else {
        await api.post('/api/users', formData);
      }
      setShowForm(false);
      setFormData({});
      setEditingId(null);
      await fetchUsers();
    } catch (error) {
      console.error('Error al guardar usuario:', error);
      alert(error.response?.data?.message || 'Error al guardar el usuario');
    }
  };

  const handleEdit = async (id) => {
    try {
      const response = await api.get(`/api/users/${id}`);
      setFormData(response.data);
      setEditingId(id);
      setShowForm(true);
    } catch (error) {
      console.error('Error al cargar usuario:', error);
      alert('Error al cargar el usuario');
    }
  };

  const handleDelete = async (id) => {
    try {
      await api.delete(`/api/users/${id}`);
      await fetchUsers();
    } catch (error) {
      console.error('Error al eliminar usuario:', error);
      alert('Error al eliminar el usuario');
    }
  };

  const handleCancelEdit = () => {
    setShowForm(false);
    setFormData({});
    setEditingId(null);
  };

  const handleAdd = () => {
    setFormData({});
    setEditingId(null);
    setShowForm(true);
  };

  if (loading) {
    return <div className="p-4">Cargando...</div>;
  }

  return (
    <div>
      {showForm ? (
        <UserForm
          formData={formData}
          handleInputChange={handleInputChange}
          handleFormSubmit={handleFormSubmit}
          editingId={editingId}
          handleCancelEdit={handleCancelEdit}
          roles={roles}
          handleDelete={handleDelete}
        />
      ) : (
        <UserList
          data={users}
          onEdit={handleEdit}
          onDelete={handleDelete}
          onAdd={handleAdd}
        />
      )}
    </div>
  );
}
