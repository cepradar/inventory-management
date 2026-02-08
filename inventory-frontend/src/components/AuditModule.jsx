import React, { useState, useEffect } from "react";
import axios from "./utils/axiosConfig";

const AuditModule = () => {
  const [movimientos, setMovimientos] = useState([]);
  const [categoriaActiva, setCategoriaActiva] = useState("TODOS");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  // Categorías disponibles
  const categorias = [
    { id: "TODOS", label: "Todos", icon: "📊" },
    { id: "INVENTARIO", label: "Inventario", icon: "📦" },
    { id: "VENTA", label: "Ventas", icon: "💰" },
    { id: "ORDEN", label: "Órdenes", icon: "🔧" },
  ];

  const cargarMovimientos = async () => {
    setLoading(true);
    setError("");
    try {
      let response;
      if (categoriaActiva === "TODOS") {
        response = await axios.get("/api/auditoria/movimientos");
      } else {
        response = await axios.get(`/api/auditoria/categoria/${categoriaActiva}`);
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

  const handleCategoriaChange = (categoria) => {
    setCategoriaActiva(categoria);
  };

  useEffect(() => {
    cargarMovimientos();
  }, [categoriaActiva]);

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

  const obtenerColorTipo = (tipoEventoNombre) => {
    if (!tipoEventoNombre) return "bg-gray-100 text-gray-800";
    const tipoUpper = tipoEventoNombre.toUpperCase();
    
    // Eventos de entrada/ingreso
    if (tipoUpper.includes("ENTRADA") || tipoUpper.includes("CREACION") || tipoUpper.includes("COMPRA")) {
      return "bg-green-100 text-green-800";
    } 
    // Eventos de salida/eliminación
    else if (tipoUpper.includes("SALIDA") || tipoUpper.includes("ELIMINACION") || tipoUpper.includes("VENTA")) {
      return "bg-red-100 text-red-800";
    } 
    // Eventos de ajuste
    else if (tipoUpper.includes("AJUSTE")) {
      return "bg-yellow-100 text-yellow-800";
    }
    // Eventos de servicio
    else if (tipoUpper.includes("SERVICIO") || tipoUpper.includes("ORDEN")) {
      return "bg-purple-100 text-purple-800";
    }
    // Eventos de garantía
    else if (tipoUpper.includes("GARANTIA")) {
      return "bg-orange-100 text-orange-800";
    }
    // Por defecto
    else {
      return "bg-blue-100 text-blue-800";
    }
  };

  return (
    <div className="p-2 md:p-4 bg-gray-50 min-h-screen">
      <div className="max-w-7xl mx-auto">
      <h1 className="text-xl md:text-2xl font-bold mb-3 text-gray-900">📊 Auditoría del Sistema</h1>

        {error && (
          <div className="mb-3 p-2 md:p-3 bg-red-100 border border-red-400 text-red-700 rounded text-sm">
            {error}
          </div>
        )}

        {/* Pestañas por categoría */}
        <div className="mb-3 bg-white rounded shadow overflow-hidden">
          <div className="flex flex-wrap md:flex-nowrap border-b border-gray-200 overflow-x-auto">
            {categorias.map((categoria) => (
              <button
                key={categoria.id}
                onClick={() => handleCategoriaChange(categoria.id)}
                className={`flex items-center justify-center gap-1.5 px-2 md:px-4 py-2 md:py-3 font-medium transition-colors text-xs md:text-sm whitespace-nowrap flex-shrink-0 ${
                  categoriaActiva === categoria.id
                    ? "border-b-2 border-blue-600 text-blue-600 bg-blue-50"
                    : "text-gray-700 hover:bg-gray-50 border-b-2 border-transparent"
                }`}
              >
                <span className="text-lg md:text-base">{categoria.icon}</span>
                <span className="hidden sm:inline">{categoria.label}</span>
              </button>
            ))}
            <button
              onClick={cargarMovimientos}
              className="ml-auto px-3 md:px-6 py-3 md:py-4 text-gray-700 hover:bg-gray-50 font-medium flex items-center gap-2 flex-shrink-0 text-sm md:text-base"
              title="Recargar datos"
            >
              🔄 <span className="hidden sm:inline">Recargar</span>
            </button>
          </div>
        </div>

        {/* Tabla de movimientos - Vista Desktop */}
        <div className="hidden md:block bg-white rounded-lg shadow overflow-hidden">
          {loading ? (
            <div className="p-8 text-center">
              <p className="text-gray-500 text-lg">Cargando movimientos...</p>
            </div>
          ) : movimientos.length === 0 ? (
            <div className="p-4 md:p-6 text-center">
              <p className="text-gray-500 text-sm md:text-base">No hay movimientos registrados en esta categoría</p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full min-w-full">
                <thead className="bg-gray-50 border-b border-gray-200">
                  <tr>
                    <th className="px-2 py-1.5 text-left text-xs font-semibold text-gray-900 whitespace-nowrap">
                      Fecha
                    </th>
                    <th className="px-2 py-1.5 text-left text-xs font-semibold text-gray-900 whitespace-nowrap">
                      Producto
                    </th>
                    <th className="px-2 py-1.5 text-left text-xs font-semibold text-gray-900 whitespace-nowrap">
                      Cant. Inicial
                    </th>
                    <th className="px-2 py-1.5 text-left text-xs font-semibold text-gray-900 whitespace-nowrap">
                      Cant. Final
                    </th>
                    <th className="px-2 py-1.5 text-left text-xs font-semibold text-gray-900 whitespace-nowrap">
                      Precio Inicial
                    </th>
                    <th className="px-2 py-1.5 text-left text-xs font-semibold text-gray-900 whitespace-nowrap">
                      Precio Final
                    </th>
                    <th className="px-2 py-1.5 text-left text-xs font-semibold text-gray-900 whitespace-nowrap">
                      Tipo
                    </th>
                    <th className="px-2 py-1.5 text-left text-xs font-semibold text-gray-900 whitespace-nowrap">
                      Descripción
                    </th>
                    <th className="px-2 py-1.5 text-left text-xs font-semibold text-gray-900 whitespace-nowrap">
                      Usuario
                    </th>
                    <th className="px-2 py-1.5 text-left text-xs font-semibold text-gray-900 whitespace-nowrap">
                      Referencia
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  {movimientos.map((movimiento) => (
                    <tr key={movimiento.id} className="hover:bg-gray-50 transition-colors even:bg-gray-50/50">
                      <td className="px-2 py-1.5 text-xs text-gray-900 whitespace-nowrap">
                        {formatearFecha(movimiento.fecha)}
                      </td>
                      <td className="px-2 py-1.5 text-xs text-gray-900 font-medium">
                        {movimiento.productName || "-"}
                      </td>
                      <td className="px-2 py-1.5 text-xs text-gray-900 whitespace-nowrap">
                        {movimiento.cantidadInicial ?? "-"}
                      </td>
                      <td className="px-2 py-1.5 text-xs text-gray-900 whitespace-nowrap">
                        {movimiento.cantidadFinal ?? "-"}
                      </td>
                      <td className="px-2 py-1.5 text-xs text-gray-900 whitespace-nowrap">
                        {movimiento.precioInicial ?? "-"}
                      </td>
                      <td className="px-2 py-1.5 text-xs text-gray-900 whitespace-nowrap">
                        {movimiento.precioFinal ?? "-"}
                      </td>
                      <td className="px-2 py-1.5 whitespace-nowrap">
                        <span className={`inline-block px-1.5 py-0.5 rounded-full text-xs font-semibold ${obtenerColorTipo(movimiento.tipoEventoNombre)}`}>
                          {movimiento.tipoEventoNombre}
                        </span>
                      </td>
                      <td className="px-2 py-1.5 text-xs text-gray-600 max-w-xs truncate">
                        {movimiento.descripcion || "-"}
                      </td>
                      <td className="px-2 py-1.5 text-xs text-gray-900 whitespace-nowrap">
                        {movimiento.usuarioNombreCompleto || movimiento.usuarioUsername}
                      </td>
                      <td className="px-2 py-1.5 text-xs text-gray-600 max-w-xs truncate">
                        {movimiento.referencia || "-"}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>

        {/* Vista de Cards para Móvil */}
        <div className="md:hidden space-y-3">
          {loading ? (
            <div className="bg-white rounded-lg p-6 text-center">
              <p className="text-gray-500">Cargando movimientos...</p>
            </div>
          ) : movimientos.length === 0 ? (
            <div className="bg-white rounded-lg p-6 text-center">
              <p className="text-gray-500">No hay movimientos registrados</p>
            </div>
          ) : (
            movimientos.map((movimiento) => (
              <div key={movimiento.id} className="bg-white rounded-lg shadow border border-gray-200 p-4 space-y-2">
                <div className="flex justify-between items-start gap-2 pb-2 border-b border-gray-200">
                  <span className="text-xs text-gray-500">
                    {formatearFecha(movimiento.fecha)}
                  </span>
                  <span className={`inline-block px-2 py-1 rounded-full text-xs font-semibold ${obtenerColorTipo(movimiento.tipoEventoNombre)}`}>
                    {movimiento.tipoEventoNombre}
                  </span>
                </div>
                
                <div className="space-y-1.5">
                  <div className="flex justify-between gap-2">
                    <span className="text-sm font-semibold text-gray-600">Producto:</span>
                    <span className="text-sm text-gray-900 text-right">{movimiento.productName || "-"}</span>
                  </div>
                  
                  <div className="flex justify-between gap-2">
                    <span className="text-sm font-semibold text-gray-600">Cant. Inicial:</span>
                    <span className="text-sm text-gray-900">{movimiento.cantidadInicial ?? "-"}</span>
                  </div>

                  <div className="flex justify-between gap-2">
                    <span className="text-sm font-semibold text-gray-600">Cant. Final:</span>
                    <span className="text-sm text-gray-900">{movimiento.cantidadFinal ?? "-"}</span>
                  </div>

                  <div className="flex justify-between gap-2">
                    <span className="text-sm font-semibold text-gray-600">Precio Inicial:</span>
                    <span className="text-sm text-gray-900">{movimiento.precioInicial ?? "-"}</span>
                  </div>

                  <div className="flex justify-between gap-2">
                    <span className="text-sm font-semibold text-gray-600">Precio Final:</span>
                    <span className="text-sm text-gray-900">{movimiento.precioFinal ?? "-"}</span>
                  </div>
                  
                  {movimiento.descripcion && (
                    <div className="pt-1">
                      <span className="text-sm font-semibold text-gray-600">Descripción:</span>
                      <p className="text-sm text-gray-700 mt-1">{movimiento.descripcion}</p>
                    </div>
                  )}
                  
                  <div className="flex justify-between gap-2">
                    <span className="text-sm font-semibold text-gray-600">Usuario:</span>
                    <span className="text-sm text-gray-900 text-right">
                      {movimiento.usuarioNombreCompleto || movimiento.usuarioUsername}
                    </span>
                  </div>
                  
                  {movimiento.referencia && (
                    <div className="flex justify-between gap-2">
                      <span className="text-sm font-semibold text-gray-600">Ref:</span>
                      <span className="text-sm text-gray-600 text-right truncate max-w-[200px]">
                        {movimiento.referencia}
                      </span>
                    </div>
                  )}
                </div>
              </div>
            ))
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
              <p className="text-gray-600 text-sm mb-2">Últimas 24 horas</p>
              <p className="text-3xl font-bold text-blue-600">
                {movimientos.filter((m) => {
                  const fecha = new Date(m.fecha);
                  const ahora = new Date();
                  const hace24h = new Date(ahora.getTime() - 24 * 60 * 60 * 1000);
                  return fecha >= hace24h;
                }).length}
              </p>
            </div>
            <div className="bg-white p-4 rounded-lg shadow">
              <p className="text-gray-600 text-sm mb-2">Usuarios involucrados</p>
              <p className="text-3xl font-bold text-purple-600">
                {new Set(movimientos.map((m) => m.usuarioUsername)).size}
              </p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default AuditModule;
