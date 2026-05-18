import React, { useEffect, useMemo, useRef, useState } from 'react';
import api from './utils/axiosConfig';
import { usePermissions } from './utils/PermissionsContext';
import ReportesModule from './ReportesModule';
import {
  UsersIcon,
  ArchiveBoxIcon,
  UserGroupIcon,
  ShoppingCartIcon,
  ClipboardDocumentListIcon,
  DocumentTextIcon,
  Cog6ToothIcon,
  ChevronDownIcon,
  ChevronRightIcon,
  DocumentChartBarIcon,
} from '@heroicons/react/24/outline';

const CONFIG_OPTIONS = [
  { id: 'roles',       label: 'Tipos de usuarios',               perm: 'config.roles.read' },
  { id: 'documentos', label: 'Tipos de documento',              perm: 'config.roles.read' },
  { id: 'cat-electro', label: 'Categorias de electrodomesticos', perm: 'config.roles.read' },
  { id: 'cat-productos', label: 'Categorias de productos',       perm: 'config.roles.read' },
  { id: 'permisos',   label: 'Permisos por rol',                perm: 'config.roles.read' },
  { id: 'reportes',   label: 'Reportes',                        perm: 'reports.read' },
];

const MODULE_CONFIG = [
  { key: 'users',    label: 'Usuarios',            Icon: UsersIcon },
  { key: 'inventory', label: 'Inventario',           Icon: ArchiveBoxIcon },
  { key: 'clients',  label: 'Clientes',             Icon: UserGroupIcon },
  { key: 'sales',    label: 'Ventas',               Icon: ShoppingCartIcon },
  { key: 'orders',   label: 'Órdenes de Servicio',  Icon: ClipboardDocumentListIcon },
  { key: 'audit',    label: 'Auditoría',            Icon: DocumentTextIcon },
  { key: 'reports',  label: 'Reportes',             Icon: DocumentChartBarIcon },
  { key: 'config',   label: 'Configuración',        Icon: Cog6ToothIcon },
];

function ModuleCheckbox({ allActive, someActive, onChange }) {
  const ref = useRef(null);
  useEffect(() => {
    if (ref.current) ref.current.indeterminate = someActive && !allActive;
  }, [allActive, someActive]);
  return (
    <input
      ref={ref}
      type="checkbox"
      checked={allActive}
      onChange={onChange}
      className="h-4 w-4 rounded border-gray-300 text-blue-600 focus:ring-blue-500 cursor-pointer flex-shrink-0"
      onClick={(e) => e.stopPropagation()}
    />
  );
}

export default function ConfigDashboard() {
  const { permissions } = usePermissions();
  const can = (c) => permissions.includes(c);

  const [activeOption, setActiveOption] = useState('roles');

  // Ajustar opción activa según permisos disponibles
  useEffect(() => {
    if (permissions.length === 0) return;
    if (!can('config.roles.read') && can('reports.read')) {
      setActiveOption('reportes');
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [permissions]);
  const [roles, setRoles] = useState([]);
  const [allPerms, setAllPerms] = useState([]);
  const [rolePerms, setRolePerms] = useState([]);
  const [selectedRole, setSelectedRole] = useState('ADMIN');
  const [roleForm, setRoleForm] = useState({ name: '', color: '#4f46e5', description: '' });
  const [loadingPerms, setLoadingPerms] = useState(false);
  const [expandedModules, setExpandedModules] = useState(() => {
    const s = {};
    MODULE_CONFIG.forEach((m) => { s[m.key] = true; });
    return s;
  });

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
      setAllPerms(response.data || []);
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
      setExpandedModules(() => {
        const s = {};
        MODULE_CONFIG.forEach((m) => { s[m.key] = true; });
        return s;
      });
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
        perm.id === permId ? { ...perm, assigned: !perm.assigned } : perm
      )
    );
  };

  const toggleModuleExpand = (modKey) => {
    setExpandedModules((prev) => ({ ...prev, [modKey]: !prev[modKey] }));
  };

  const toggleAllInModule = (modKey) => {
    const inModule = rolePerms.filter((p) => (p.moduleKey || 'general') === modKey);
    const allActive = inModule.every((p) => p.assigned);
    setRolePerms((prev) =>
      prev.map((p) =>
        (p.moduleKey || 'general') === modKey ? { ...p, assigned: !allActive } : p
      )
    );
  };

  const handleSavePermissions = async () => {
    try {
      const payload = rolePerms.map((p) => ({
        permissionCode: p.code,
        active: p.assigned ?? false,
        reason: '',
      }));
      await api.put(`/api/permissions/role/${selectedRole}`, payload);
      fetchRolePermissions(selectedRole);
    } catch (err) {
      console.error('Error al guardar permisos:', err);
      alert('No se pudieron guardar los permisos');
    }
  };

  const roleOptions = useMemo(() => roles.map((r) => r.name), [roles]);

  const permsByModule = useMemo(() => {
    const groups = {};
    rolePerms.forEach((p) => {
      const mod = p.moduleKey || 'general';
      if (!groups[mod]) groups[mod] = [];
      groups[mod].push(p);
    });
    // Solo mostrar módulos definidos en MODULE_CONFIG (evita duplicados por claves legacy en DB)
    return MODULE_CONFIG
      .filter((m) => groups[m.key]?.length > 0)
      .map((m) => ({ ...m, perms: groups[m.key] }));
  }, [rolePerms]);

  return (
    <div className="bg-white rounded-lg shadow p-4 md:p-6">
      <div className="grid grid-cols-1 md:grid-cols-[220px_1fr] gap-4">
        <div className="border border-gray-200 rounded-lg overflow-hidden">
          <div className="bg-gray-50 px-3 py-2 text-sm font-semibold text-gray-700">Configuracion</div>
          <div className="divide-y divide-gray-200">
            {CONFIG_OPTIONS.filter((opt) => can(opt.perm)).map((opt) => (
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

              {loadingPerms ? (
                <div className="py-8 text-sm text-gray-500 text-center">Cargando permisos...</div>
              ) : permsByModule.length === 0 ? (
                <div className="py-8 text-sm text-gray-500 text-center">No hay permisos configurados para este rol.</div>
              ) : (
                <div className="space-y-2">
                  {permsByModule.map(({ key, label, Icon, perms }, idx) => {
                    const activeCount = perms.filter((p) => p.assigned).length;
                    const allActive = activeCount === perms.length;
                    const someActive = activeCount > 0;
                    const isExpanded = expandedModules[key] ?? true;
                    return (
                      <div key={key} className="border border-gray-200 rounded-lg overflow-hidden">
                        <div
                          className="flex items-center gap-2 px-4 py-3 bg-gray-50 cursor-pointer select-none hover:bg-gray-100 transition-colors"
                          onClick={() => toggleModuleExpand(key)}
                        >
                          <span className="text-xs font-bold text-gray-400 w-5 text-right flex-shrink-0">{idx + 1}.</span>
                          <ModuleCheckbox
                            allActive={allActive}
                            someActive={someActive}
                            onChange={() => toggleAllInModule(key)}
                          />
                          {Icon && <Icon className="h-5 w-5 text-gray-600 flex-shrink-0" />}
                          <span className="flex-1 text-sm font-semibold text-gray-800">{label}</span>
                          <span className="text-xs text-gray-500 mr-1">
                            {activeCount}/{perms.length} activos
                          </span>
                          {isExpanded
                            ? <ChevronDownIcon className="h-4 w-4 text-gray-400 flex-shrink-0" />
                            : <ChevronRightIcon className="h-4 w-4 text-gray-400 flex-shrink-0" />}
                        </div>
                        {isExpanded && (
                          <div className="divide-y divide-gray-100">
                            {perms.map((perm) => (
                              <label
                                key={perm.id}
                                className="flex items-center gap-3 pl-14 pr-4 py-2.5 cursor-pointer hover:bg-blue-50 transition-colors"
                              >
                                <input
                                  type="checkbox"
                                  checked={perm.assigned ?? false}
                                  onChange={() => togglePermission(perm.id)}
                                  className="h-4 w-4 rounded border-gray-300 text-blue-600 focus:ring-blue-500 flex-shrink-0"
                                />
                                <div className="flex-1 min-w-0">
                                  <div className="text-sm text-gray-800">· {perm.label}</div>
                                  <div className="text-xs text-gray-400 font-mono">{perm.code}</div>
                                </div>
                                {perm.critical && (
                                  <span className="text-xs bg-red-100 text-red-700 px-1.5 py-0.5 rounded font-semibold flex-shrink-0">critico</span>
                                )}
                              </label>
                            ))}
                          </div>
                        )}
                      </div>
                    );
                  })}
                </div>
              )}

              <div className="flex justify-end pt-2">
                <button
                  type="button"
                  onClick={handleSavePermissions}
                  className="h-9 px-6 bg-green-600 hover:bg-green-700 text-white text-sm font-semibold rounded"
                >
                  Guardar permisos
                </button>
              </div>
            </div>
          )}

          {activeOption === 'reportes' && (
            <div className="-m-4">
              <ReportesModule />
            </div>
          )}

          {activeOption !== 'roles' && activeOption !== 'permisos' && activeOption !== 'reportes' && (
            <div className="text-sm text-gray-500">
              Esta seccion estara disponible proximamente.
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
