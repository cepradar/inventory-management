import React, { useState, useMemo } from 'react';
import { ChevronUpDownIcon, ChevronUpIcon, ChevronDownIcon } from '@heroicons/react/24/outline';

export default function DataTable({ 
  data = [], 
  columns = [], 
  onEdit, 
  onDelete,
  title
}) {
  const [sortConfig, setSortConfig] = useState({ key: null, direction: 'asc' });
  const [filters, setFilters] = useState({});

  // Aplicar filtros
  const filteredData = useMemo(() => {
    return data.filter(item => {
      return Object.keys(filters).every(key => {
        const filterValue = filters[key].toLowerCase();
        // Para campos personalizados (render), intentar buscar en el objeto
        const itemValue = String(item[key] || '').toLowerCase();
        return itemValue.includes(filterValue);
      });
    });
  }, [data, filters]);

  // Aplicar ordenamiento
  const sortedData = useMemo(() => {
    if (!sortConfig.key) return filteredData;

    return [...filteredData].sort((a, b) => {
      const aValue = a[sortConfig.key];
      const bValue = b[sortConfig.key];

      if (aValue == null && bValue == null) return 0;
      if (aValue == null) return 1;
      if (bValue == null) return -1;

      // Ordenamiento para strings
      if (typeof aValue === 'string') {
        const comparison = aValue.localeCompare(bValue);
        return sortConfig.direction === 'asc' ? comparison : -comparison;
      }

      // Ordenamiento para números
      const comparison = aValue > bValue ? 1 : -1;
      return sortConfig.direction === 'asc' ? comparison : -comparison;
    });
  }, [filteredData, sortConfig]);

  const handleSort = (key) => {
    setSortConfig(prev => ({
      key,
      direction: prev.key === key && prev.direction === 'asc' ? 'desc' : 'asc'
    }));
  };

  const handleFilterChange = (key, value) => {
    setFilters(prev => ({
      ...prev,
      [key]: value
    }));
  };

  const getSortIcon = (columnKey) => {
    if (sortConfig.key !== columnKey) {
      return <ChevronUpDownIcon className="h-4 w-4 opacity-40" />;
    }
    return sortConfig.direction === 'asc' 
      ? <ChevronUpIcon className="h-4 w-4" /> 
      : <ChevronDownIcon className="h-4 w-4" />;
  };

  return (
    <div className="w-full">
      {title && <h3 className="text-lg font-semibold mb-4 text-gray-800">{title}</h3>}
      
      <div className="overflow-x-auto border border-gray-200 rounded-lg">
        <table className="w-full border-collapse">
          <thead>
            <tr className="bg-gray-100 border-b-2 border-gray-300">
              {columns.map(col => (
                <th key={col.key} className="px-4 py-3 text-left">
                  {col.sortable !== false ? (
                    <button
                      onClick={() => handleSort(col.key)}
                      className="flex items-center gap-2 font-semibold text-gray-700 hover:text-gray-900 w-full group"
                    >
                      {col.label}
                      <span className="group-hover:opacity-100">
                        {getSortIcon(col.key)}
                      </span>
                    </button>
                  ) : (
                    <span className="font-semibold text-gray-700">{col.label}</span>
                  )}
                </th>
              ))}
            </tr>

            {/* Fila de filtros */}
            <tr className="bg-gray-50 border-b border-gray-200">
              {columns.map(col => (
                <th key={`filter-${col.key}`} className="px-4 py-2">
                  {col.filterable !== false && (
                    <input
                      type="text"
                      placeholder={`Filtrar ${col.label.toLowerCase()}...`}
                      value={filters[col.key] || ''}
                      onChange={(e) => handleFilterChange(col.key, e.target.value)}
                      className="w-full px-2 py-1 text-sm border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  )}
                </th>
              ))}
            </tr>
          </thead>

          <tbody>
            {sortedData.length === 0 ? (
              <tr>
                <td colSpan={columns.length + (onEdit || onDelete ? 1 : 0)} className="px-4 py-8 text-center text-gray-500">
                  No hay datos disponibles
                </td>
              </tr>
            ) : (
              sortedData.map((item, index) => {
                // Generar una clave única: usar id si existe, sino documento, sino el índice
                const key = item?.id || item?.documento || item?.username || `row-${index}`;
                return (
                  <tr key={key} className="border-b border-gray-200 hover:bg-gray-50 transition-colors">
                    {columns.map(col => (
                      <td key={`${key}-${col.key}`} className="px-4 py-3 text-sm text-gray-700">
                        {col.render ? col.render(item) : (item?.[col.key] || '-')}
                      </td>
                    ))}
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>

      {sortedData.length > 0 && (
        <div className="mt-2 text-sm text-gray-600">
          Mostrando {sortedData.length} de {data.length} registros
        </div>
      )}
    </div>
  );
}
