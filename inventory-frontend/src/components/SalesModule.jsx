import React, { useState, useEffect } from "react";
import axios from "axios";

const SalesModule = () => {
  const [ventas, setVentas] = useState([]);
  const [productos, setProductos] = useState([]);
  const [mostrarFormulario, setMostrarFormulario] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const [formulario, setFormulario] = useState({
    productId: "",
    cantidad: 1,
    precioUnitario: 0,
    nombreComprador: "",
    telefonoComprador: "",
    emailComprador: "",
    observaciones: "",
  });

  // Cargar ventas y productos al montar
  useEffect(() => {
    cargarVentas();
    cargarProductos();
  }, []);

  const cargarVentas = async () => {
    try {
      const response = await axios.get("/api/ventas/listar");
      // Asegurar que siempre sea un array
      const data = Array.isArray(response.data) ? response.data : [];
      setVentas(data);
    } catch (err) {
      console.error("Error al cargar ventas:", err);
      setVentas([]); // Resetear a array vacío en caso de error
    }
  };

  const cargarProductos = async () => {
    try {
      const response = await axios.get("/api/products/listar");
      setProductos(response.data);
    } catch (err) {
      console.error("Error al cargar productos:", err);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormulario({ ...formulario, [name]: value });
  };

  const handleProductoChange = (e) => {
    const productId = e.target.value;
    setFormulario({ ...formulario, productId });

    // Obtener precio del producto seleccionado
    const producto = productos.find((p) => p.id == productId);
    if (producto) {
      setFormulario((prev) => ({ ...prev, precioUnitario: producto.price || 0 }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    setSuccessMessage("");

    try {
      // Validaciones
      if (!formulario.productId) {
        setError("Por favor selecciona un producto");
        setLoading(false);
        return;
      }
      if (formulario.cantidad <= 0) {
        setError("La cantidad debe ser mayor a 0");
        setLoading(false);
        return;
      }
      if (!formulario.nombreComprador) {
        setError("Por favor ingresa el nombre del comprador");
        setLoading(false);
        return;
      }

      const producto = productos.find((p) => p.id == formulario.productId);
      if (producto.quantity < formulario.cantidad) {
        setError(
          `No hay suficiente cantidad disponible. Disponible: ${producto.quantity}`
        );
        setLoading(false);
        return;
      }

      // Enviar venta
      const response = await axios.post("/api/ventas/registrar", null, {
        params: {
          productId: formulario.productId,
          cantidad: formulario.cantidad,
          precioUnitario: formulario.precioUnitario,
          nombreComprador: formulario.nombreComprador,
          telefonoComprador: formulario.telefonoComprador,
          emailComprador: formulario.emailComprador,
          usuarioUsername: localStorage.getItem("userName") || "admin",
          observaciones: formulario.observaciones,
        },
      });

      setSuccessMessage(
        `¡Venta registrada exitosamente! Total: $${response.data.totalVenta}`
      );

      // Limpiar formulario
      setFormulario({
        productId: "",
        cantidad: 1,
        precioUnitario: 0,
        nombreComprador: "",
        telefonoComprador: "",
        emailComprador: "",
        observaciones: "",
      });

      // Recargar ventas y productos
      cargarVentas();
      cargarProductos();
      setMostrarFormulario(false);

      // Limpiar mensaje después de 3 segundos
      setTimeout(() => setSuccessMessage(""), 3000);
    } catch (err) {
      setError(
        "Error al registrar venta: " + (err.response?.data?.message || err.message)
      );
      console.error("Error:", err);
    } finally {
      setLoading(false);
    }
  };

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

  const calcularTotal = () => {
    return (
      parseFloat(formulario.cantidad || 0) *
      parseFloat(formulario.precioUnitario || 0)
    ).toFixed(2);
  };

  return (
    <div className="p-6 bg-gray-50 min-h-screen">
      <div className="max-w-7xl mx-auto">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-3xl font-bold text-gray-900">Ventas de Productos</h1>
          <button
            onClick={() => setMostrarFormulario(!mostrarFormulario)}
            className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-all font-medium"
          >
            {mostrarFormulario ? "Cancelar" : "+ Nueva Venta"}
          </button>
        </div>

        {error && (
          <div className="mb-4 p-4 bg-red-100 border border-red-400 text-red-700 rounded">
            {error}
          </div>
        )}

        {successMessage && (
          <div className="mb-4 p-4 bg-green-100 border border-green-400 text-green-700 rounded">
            {successMessage}
          </div>
        )}

        {/* Formulario de venta */}
        {mostrarFormulario && (
          <div className="mb-6 bg-white p-6 rounded-lg shadow">
            <h2 className="text-2xl font-semibold mb-4 text-gray-900">Registrar Nueva Venta</h2>
            <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {/* Producto */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Producto *
                </label>
                <select
                  name="productId"
                  value={formulario.productId}
                  onChange={handleProductoChange}
                  required
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600"
                >
                  <option value="">Selecciona un producto</option>
                  {productos.map((producto) => (
                    <option key={producto.id} value={producto.id}>
                      {producto.nombre} (Disponible: {producto.quantity})
                    </option>
                  ))}
                </select>
              </div>

              {/* Cantidad */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Cantidad *
                </label>
                <input
                  type="number"
                  name="cantidad"
                  value={formulario.cantidad}
                  onChange={handleInputChange}
                  min="1"
                  required
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600"
                />
              </div>

              {/* Precio Unitario */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Precio Unitario *
                </label>
                <input
                  type="number"
                  name="precioUnitario"
                  value={formulario.precioUnitario}
                  onChange={handleInputChange}
                  step="0.01"
                  min="0"
                  required
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600"
                />
              </div>

              {/* Total */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Total
                </label>
                <div className="px-4 py-2 bg-gray-100 border border-gray-300 rounded-lg text-lg font-bold text-gray-900">
                  ${calcularTotal()}
                </div>
              </div>

              {/* Nombre Comprador */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Nombre Comprador *
                </label>
                <input
                  type="text"
                  name="nombreComprador"
                  value={formulario.nombreComprador}
                  onChange={handleInputChange}
                  required
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600"
                  placeholder="Nombre completo"
                />
              </div>

              {/* Teléfono */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Teléfono
                </label>
                <input
                  type="tel"
                  name="telefonoComprador"
                  value={formulario.telefonoComprador}
                  onChange={handleInputChange}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600"
                  placeholder="Teléfono (opcional)"
                />
              </div>

              {/* Email */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Email
                </label>
                <input
                  type="email"
                  name="emailComprador"
                  value={formulario.emailComprador}
                  onChange={handleInputChange}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600"
                  placeholder="Email (opcional)"
                />
              </div>

              {/* Observaciones */}
              <div className="md:col-span-2">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Observaciones
                </label>
                <textarea
                  name="observaciones"
                  value={formulario.observaciones}
                  onChange={handleInputChange}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600"
                  rows="3"
                  placeholder="Notas adicionales (opcional)"
                ></textarea>
              </div>

              {/* Botones */}
              <div className="md:col-span-2 flex gap-3">
                <button
                  type="submit"
                  disabled={loading}
                  className="flex-1 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-all font-medium disabled:bg-gray-400"
                >
                  {loading ? "Registrando..." : "Registrar Venta"}
                </button>
                <button
                  type="button"
                  onClick={() => setMostrarFormulario(false)}
                  className="flex-1 px-4 py-2 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400 transition-all font-medium"
                >
                  Cancelar
                </button>
              </div>
            </form>
          </div>
        )}

        {/* Tabla de ventas */}
        <div className="bg-white rounded-lg shadow overflow-hidden">
          <div className="px-6 py-4 border-b border-gray-200 bg-gray-50">
            <h2 className="text-lg font-semibold text-gray-900">
              Historial de Ventas ({ventas.length})
            </h2>
          </div>

          {ventas.length === 0 ? (
            <div className="p-8 text-center">
              <p className="text-gray-500 text-lg">No hay ventas registradas</p>
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
                      Comprador
                    </th>
                    <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">
                      Cantidad
                    </th>
                    <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">
                      Precio Unit.
                    </th>
                    <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">
                      Total
                    </th>
                    <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">
                      Usuario
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  {ventas.map((venta) => (
                    <tr key={venta.id} className="hover:bg-gray-50 transition-colors">
                      <td className="px-6 py-4 text-sm text-gray-900">
                        {formatearFecha(venta.fecha)}
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-900 font-medium">
                        {venta.productNombre}
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-900">
                        {venta.nombreComprador}
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-900">
                        {venta.cantidad}
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-900">
                        ${venta.precioUnitario}
                      </td>
                      <td className="px-6 py-4 text-sm font-semibold text-green-600">
                        ${venta.totalVenta}
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-600">
                        {venta.usuarioNombre}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>

        {/* Resumen */}
        {ventas.length > 0 && (
          <div className="mt-6 grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="bg-white p-4 rounded-lg shadow">
              <p className="text-gray-600 text-sm mb-2">Total Ventas</p>
              <p className="text-3xl font-bold text-gray-900">{ventas.length}</p>
            </div>
            <div className="bg-white p-4 rounded-lg shadow">
              <p className="text-gray-600 text-sm mb-2">Monto Total</p>
              <p className="text-3xl font-bold text-green-600">
                ${ventas
                  .reduce((total, v) => total + parseFloat(v.totalVenta || 0), 0)
                  .toFixed(2)}
              </p>
            </div>
            <div className="bg-white p-4 rounded-lg shadow">
              <p className="text-gray-600 text-sm mb-2">Promedio por Venta</p>
              <p className="text-3xl font-bold text-blue-600">
                ${(
                  ventas.reduce((total, v) => total + parseFloat(v.totalVenta || 0), 0) /
                  ventas.length
                ).toFixed(2)}
              </p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default SalesModule;
