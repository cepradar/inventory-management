import React, { useEffect, useMemo, useState } from 'react';
import api from './utils/axiosConfig';

const CONFIG_OPTIONS = [
  { id: 'roles', label: 'Tipos de usuarios' },
  { id: 'documentos', label: 'Tipos de documento' },
  { id: 'cat-electro', label: 'Categorias de electrodomesticos' },
  { id: 'cat-productos', label: 'Categorias de productos' },
  { id: 'permisos', label: 'Permisos por rol' }
];

export default function ConfigDashboard() {
  const [activeOption, setActiveOption] = useState('roles');
  const [roles, setRoles] = useState([]);
  const [permissions, setPermissions] = useState([]);
  const [rolePerms, setRolePerms] = useState([]);
  const [selectedRole, setSelectedRole] = useState('ADMIN');
  const [roleForm, setRoleForm] = useState({ name: '', color: '#4f46e5', description: '' });
  const [loadingPerms, setLoadingPerms] = useState(false);

  const fetchRoles = async () => {
    try {
      const response = await api.get('/api/roles');
      setRoles(response.data || []);
      if (response.data?.length && !response.data.find((r) => r.name === selectedRole)) {
        setSelectedRole(response.data[0].name);
      }
    } catch (err) {
      console.error('Error al cargar roles:', err);
    }
  };

  const fetchPermissions = async () => {
    try {
      const response = await api.get('/api/permissions');
      setPermissions(response.data || []);
    } catch (err) {
      console.error('Error al cargar permisos:', err);
    }
  };

  const fetchRolePermissions = async (roleName) => {
    if (!roleName) return;
    setLoadingPerms(true);
    try {
      const response = await api.get(`/api/permissions/role/${roleName}`);
      setRolePerms(response.data || []);
    } catch (err) {
      console.error('Error al cargar permisos del rol:', err);
    } finally {
      setLoadingPerms(false);
    }
  };

  useEffect(() => {
    fetchRoles();
    fetchPermissions();
  }, []);

  useEffect(() => {
    if (selectedRole) {
      fetchRolePermissions(selectedRole);
    }
  }, [selectedRole]);

  const handleRoleFormChange = (e) => {
    const { name, value } = e.target;
    setRoleForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleCreateRole = async (e) => {
    e.preventDefault();
    try {
      await api.post('/api/roles', roleForm);
      setRoleForm({ name: '', color: '#4f46e5', description: '' });
      fetchRoles();
    } catch (err) {
      console.error('Error al crear rol:', err);
      alert('No se pudo crear el rol');
    }
  };

  const togglePermission = (permId) => {
    setRolePerms((prev) =>
      prev.map((perm) =>
        perm.permissionId === permId ? { ...perm, active: !perm.active } : perm
      )
    );
  };

  const handleSavePermissions = async () => {
    try {
      await api.put(`/api/permissions/role/${selectedRole}`, rolePerms);
      fetchRolePermissions(selectedRole);
    } catch (err) {
      console.error('Error al guardar permisos:', err);
      alert('No se pudieron guardar los permisos');
    }
  };

  const roleOptions = useMemo(() => roles.map((r) => r.name), [roles]);

  return (
    <div className="bg-white rounded-lg shadow p-4 md:p-6">
      <div className="grid grid-cols-1 md:grid-cols-[220px_1fr] gap-4">
        <div className="border border-gray-200 rounded-lg overflow-hidden">
          <div className="bg-gray-50 px-3 py-2 text-sm font-semibold text-gray-700">Configuracion</div>
          <div className="divide-y divide-gray-200">
            {CONFIG_OPTIONS.map((opt) => (
              <button
                key={opt.id}
                onClick={() => setActiveOption(opt.id)}
                className={`w-full text-left px-3 py-2 text-sm transition-colors ${
                  activeOption === opt.id
                    ? 'bg-blue-50 text-blue-700 font-semibold'
                    : 'text-gray-700 hover:bg-gray-50'
                }`}
              >
                {opt.label}
              </button>
            ))}
          </div>
        </div>

        <div className="border border-gray-200 rounded-lg p-4">
          {activeOption === 'roles' && (
            <div className="space-y-4">
              <div>
                <h3 className="text-lg font-semibold text-gray-800">Tipos de usuarios</h3>
                <p className="text-sm text-gray-500">Crea y consulta roles del sistema.</p>
              </div>

              <form onSubmit={handleCreateRole} className="grid grid-cols-1 md:grid-cols-3 gap-3">
                <input
                  type="text"
                  name="name"
                  value={roleForm.name}
                  onChange={handleRoleFormChange}
                  className="w-full h-9 px-3 text-sm border border-gray-300 rounded"
                  placeholder="Nombre del rol"
                  required
                />
                <input
                  type="text"
                  name="description"
                  value={roleForm.description}
                  onChange={handleRoleFormChange}
                  className="w-full h-9 px-3 text-sm border border-gray-300 rounded"
                  placeholder="Descripcion"
                />
                <div className="flex gap-2">
                  <input
                    type="color"
                    name="color"
                    value={roleForm.color}
                    onChange={handleRoleFormChange}
                    className="h-9 w-12 border border-gray-300 rounded"
                    title="Color"
                  />
                  <button
                    type="submit"
                    className="flex-1 h-9 bg-blue-600 hover:bg-blue-700 text-white text-sm rounded"
                  >
                    Crear rol
                  </button>
                </div>
              </form>

              <div className="border border-gray-200 rounded">
                <div className="grid grid-cols-[1fr_1fr_80px] gap-2 px-3 py-2 bg-gray-50 text-xs font-semibold text-gray-700">
                  <span>Rol</span>
                  <span>Descripcion</span>
                  <span>Color</span>
                </div>
                <div className="divide-y divide-gray-200">
                  {roles.map((rol) => (
                    <div key={rol.name} className="grid grid-cols-[1fr_1fr_80px] gap-2 px-3 py-2 text-sm">
                      <span className="font-semibold text-gray-800">{rol.name}</span>
                      <span className="text-gray-600">{rol.description || '-'}</span>
                      <span
                        className="inline-flex h-6 w-6 rounded"
                        style={{ backgroundColor: rol.color || '#4f46e5' }}
                      ></span>
                    </div>
                  ))}
                  {roles.length === 0 && (
                    <div className="px-3 py-3 text-sm text-gray-500">No hay roles registrados</div>
                  )}
                </div>
              </div>
            </div>
          )}

          {activeOption === 'permisos' && (
            <div className="space-y-4">
              <div>
                <h3 className="text-lg font-semibold text-gray-800">Permisos por rol</h3>
                <p className="text-sm text-gray-500">Activa o desactiva accesos por rol.</p>
              </div>

              <div className="flex flex-wrap items-center gap-3">
                <label className="text-sm font-semibold text-gray-700">Rol</label>
                <select
                  value={selectedRole}
                  onChange={(e) => setSelectedRole(e.target.value)}
                  className="h-9 px-3 text-sm border border-gray-300 rounded"
                >
                  {roleOptions.map((role) => (
                    <option key={role} value={role}>{role}</option>
                  ))}
                </select>
                <button
                  type="button"
                  onClick={() => fetchRolePermissions(selectedRole)}
                  className="h-9 px-3 bg-gray-200 hover:bg-gray-300 text-sm rounded"
                >
                  Refrescar
                </button>
              </div>

              <div className="border border-gray-200 rounded">
                <div className="grid grid-cols-[1fr_120px] px-3 py-2 bg-gray-50 text-xs font-semibold text-gray-700">
                  <span>Permiso</span>
                  <span className="text-center">Activo</span>
                </div>
                {loadingPerms ? (
                  <div className="px-3 py-3 text-sm text-gray-500">Cargando permisos...</div>
                ) : (
                  <div className="divide-y divide-gray-200">
                    {rolePerms.map((perm) => (
                      <div key={perm.permissionId} className="grid grid-cols-[1fr_120px] px-3 py-2 text-sm">
                        <span className="text-gray-800">{perm.permissionName}</span>
                        <div className="flex justify-center">
                          <input
                            type="checkbox"
                            checked={perm.active}
                            onChange={() => togglePermission(perm.permissionId)}
                          />
                        </div>
                      </div>
                    ))}
                    {rolePerms.length === 0 && (
                      <div className="px-3 py-3 text-sm text-gray-500">No hay permisos configurados</div>
                    )}
                  </div>
                )}
              </div>

              <div className="flex justify-end">
                <button
                  type="button"
                  onClick={handleSavePermissions}
                  className="h-9 px-4 bg-green-600 hover:bg-green-700 text-white text-sm rounded"
                >
                  Guardar permisos
                </button>
              </div>
            </div>
          )}

          {activeOption !== 'roles' && activeOption !== 'permisos' && (
            <div className="text-sm text-gray-500">
              Esta seccion estara disponible proximamente.
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
