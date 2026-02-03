import React, { useState, useEffect } from "react";
import axios from "axios";

const AuditModule = () => {
  const [movimientos, setMovimientos] = useState([]);
  const [filtroTipo, setFiltroTipo] = useState("TODOS");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  // Cargar movimientos al montar el componente
  useEffect(() => {
    cargarMovimientos();
  }, []);

  const cargarMovimientos = async () => {
    setLoading(true);
    setError("");
    try {
      let response;
      if (filtroTipo === "TODOS") {
        response = await axios.get("/api/auditoria/movimientos");
      } else {
        response = await axios.get(`/api/auditoria/tipo/${filtroTipo}`);
      }
      // Asegurar que siempre sea un array
      const data = Array.isArray(response.data) ? response.data : [];
      setMovimientos(data);
    } catch (err) {
      setError("Error al cargar movimientos: " + (err.response?.data?.message || err.message));
      console.error("Error:", err);
      setMovimientos([]); // Resetear a array vacío en caso de error
    } finally {
      setLoading(false);
    }
  };

  const handleFiltroChange = (tipo) => {
    setFiltroTipo(tipo);
  };

  useEffect(() => {
    cargarMovimientos();
  }, [filtroTipo]);

  const formatearFecha = (fecha) => {
    if (!fecha) return "-";
    const date = new Date(fecha);
    return date.toLocaleDateString("es-ES", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const obtenerColorTipo = (tipo) => {
    return tipo === "INGRESO" 
      ? "bg-green-100 text-green-800" 
      : "bg-red-100 text-red-800";
  };

  return (
    <div className="p-6 bg-gray-50 min-h-screen">
      <div className="max-w-7xl mx-auto">
        <h1 className="text-3xl font-bold mb-6 text-gray-900">Auditoría de Productos</h1>

        {error && (
          <div className="mb-4 p-4 bg-red-100 border border-red-400 text-red-700 rounded">
            {error}
          </div>
        )}

        {/* Filtros */}
        <div className="mb-6 bg-white p-4 rounded-lg shadow">
          <h2 className="text-lg font-semibold mb-3 text-gray-700">Filtrar por tipo:</h2>
          <div className="flex gap-3">
            <button
              onClick={() => handleFiltroChange("TODOS")}
              className={`px-4 py-2 rounded font-medium transition-all ${
                filtroTipo === "TODOS"
                  ? "bg-blue-600 text-white"
                  : "bg-gray-200 text-gray-700 hover:bg-gray-300"
              }`}
            >
              Todos
            </button>
            <button
              onClick={() => handleFiltroChange("INGRESO")}
              className={`px-4 py-2 rounded font-medium transition-all ${
                filtroTipo === "INGRESO"
                  ? "bg-green-600 text-white"
                  : "bg-gray-200 text-gray-700 hover:bg-gray-300"
              }`}
            >
              Ingresos
            </button>
            <button
              onClick={() => handleFiltroChange("SALIDA")}
              className={`px-4 py-2 rounded font-medium transition-all ${
                filtroTipo === "SALIDA"
                  ? "bg-red-600 text-white"
                  : "bg-gray-200 text-gray-700 hover:bg-gray-300"
              }`}
            >
              Salidas
            </button>
            <button
              onClick={cargarMovimientos}
              className="px-4 py-2 rounded font-medium bg-blue-100 text-blue-700 hover:bg-blue-200 transition-all ml-auto"
            >
              Recargar
            </button>
          </div>
        </div>

        {/* Tabla de movimientos */}
        <div className="bg-white rounded-lg shadow overflow-hidden">
          {loading ? (
            <div className="p-8 text-center">
              <p className="text-gray-500 text-lg">Cargando movimientos...</p>
            </div>
          ) : movimientos.length === 0 ? (
            <div className="p-8 text-center">
              <p className="text-gray-500 text-lg">No hay movimientos registrados</p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-100 border-b border-gray-200">
                  <tr>
                    <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">
                      Fecha
                    </th>
                    <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">
                      Producto
                    </th>
                    <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">
                      Cantidad
                    </th>
                    <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">
                      Tipo
                    </th>
                    <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">
                      Descripción
                    </th>
                    <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">
                      Usuario
                    </th>
                    <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">
                      Referencia
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  {movimientos.map((movimiento) => (
                    <tr key={movimiento.id} className="hover:bg-gray-50 transition-colors">
                      <td className="px-6 py-4 text-sm text-gray-900">
                        {formatearFecha(movimiento.fecha)}
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-900 font-medium">
                        {movimiento.productNombre}
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-900">
                        {movimiento.cantidad}
                      </td>
                      <td className="px-6 py-4">
                        <span className={`inline-block px-3 py-1 rounded-full text-sm font-semibold ${obtenerColorTipo(movimiento.tipo)}`}>
                          {movimiento.tipo}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-600 max-w-xs truncate">
                        {movimiento.descripcion || "-"}
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-900">
                        {movimiento.usuarioNombre}
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-600">
                        {movimiento.referencia || "-"}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>

        {/* Resumen */}
        {movimientos.length > 0 && (
          <div className="mt-6 grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="bg-white p-4 rounded-lg shadow">
              <p className="text-gray-600 text-sm mb-2">Total de Movimientos</p>
              <p className="text-3xl font-bold text-gray-900">{movimientos.length}</p>
            </div>
            <div className="bg-white p-4 rounded-lg shadow">
              <p className="text-gray-600 text-sm mb-2">Ingresos</p>
              <p className="text-3xl font-bold text-green-600">
                {movimientos.filter((m) => m.tipo === "INGRESO").length}
              </p>
            </div>
            <div className="bg-white p-4 rounded-lg shadow">
              <p className="text-gray-600 text-sm mb-2">Salidas</p>
              <p className="text-3xl font-bold text-red-600">
                {movimientos.filter((m) => m.tipo === "SALIDA").length}
              </p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default AuditModule;
