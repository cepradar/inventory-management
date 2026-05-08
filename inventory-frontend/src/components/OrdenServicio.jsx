import React, { useEffect, useState } from "react";
import api from "./utils/axiosConfig";
import DataTable from "./DataTable";

/**
 * COMPONENTE REFACTORIZADO: OrdenServicio
 * 
 * CAMBIOS:
 * - Eliminada toda lógica de productos embebida
 * - Los productos ahora se manejan en el módulo de Ventas
 * - Una orden puede tener múltiples ventas asociadas
 * - Simplificado: solo maneja datos técnicos de la orden
 */
export default function OrdenServicio() {
  const [ordenes, setOrdenes] = useState([]);
  const [clienteEncontrado, setClienteEncontrado] = useState(null);
  const [clienteElectrodomesticos, setClienteElectrodomesticos] = useState([]);
  const [selectedElectrodomestico, setSelectedElectrodomestico] = useState(null);
  const [activeView, setActiveView] = useState("LISTA");
  const [selectedOrdenEntrega, setSelectedOrdenEntrega] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [tecnicos, setTecnicos] = useState([]);
  const [tecnicosError, setTecnicosError] = useState("");
  const [selectedOrdenAsignar, setSelectedOrdenAsignar] = useState("");
  const [selectedTecnico, setSelectedTecnico] = useState("");
  const [selectedOrdenCierre, setSelectedOrdenCierre] = useState("");
  const [clientMatches, setClientMatches] = useState([]);
  const [showClientMatches, setShowClientMatches] = useState(false);

  // Formulario simplificado: SIN items/productos
  const [formulario, setFormulario] = useState({
    documento: "",
    nombreCliente: "",
    tipoServicio: "REPARACION",
    descripcionProblema: "",
    observaciones: ""
  });

  // Formulario de cierre
  const [cierreForm, setCierreForm] = useState({
    diagnostico: "",
    solucion: "",
    partesCambiadas: "",
    garantiaServicio: 30,
    observaciones: ""
  });

  useEffect(() => {
    cargarOrdenes();
    cargarTecnicos();
  }, []);

  const cargarOrdenes = async () => {
    try {
      const response = await api.get("/api/servicios-reparacion/listar");
      const data = Array.isArray(response.data) ? response.data : [];
      setOrdenes(data);
    } catch (err) {
      console.error("Error al cargar ordenes:", err);
      setOrdenes([]);
    }
  };

  const cargarTecnicos = async () => {
    try {
      const response = await api.get("/api/users/technicians");
      const data = Array.isArray(response.data) ? response.data : [];
      setTecnicos(data);
      setTecnicosError("");
    } catch (err) {
      setTecnicos([]);
      setTecnicosError("No se pudo cargar la lista de técnicos");
    }
  };

  useEffect(() => {
    if (!clienteEncontrado) {
      setClienteElectrodomesticos([]);
      setSelectedElectrodomestico(null);
      return;
    }

    const fetchElectrodomesticos = async () => {
      try {
        const response = await api.get(
          `/api/cliente-electrodomestico/cliente/${clienteEncontrado.documento}/${clienteEncontrado.tipoDocumentoId}`
        );
        const data = Array.isArray(response.data) ? response.data : [];
        setClienteElectrodomesticos(data);
        setSelectedElectrodomestico(null);
      } catch (err) {
        console.error("Error al cargar electrodomésticos:", err);
        setClienteElectrodomesticos([]);
      }
    };

    fetchElectrodomesticos();
  }, [clienteEncontrado]);

  useEffect(() => {
    if (!selectedOrdenCierre) {
      setCierreForm({
        diagnostico: "",
        solucion: "",
        partesCambiadas: "",
        garantiaServicio: 30,
        observaciones: ""
      });
      return;
    }

    const ordenSeleccionada = ordenes.find((o) => o.id === selectedOrdenCierre);
    if (!ordenSeleccionada) return;

    setCierreForm({
      diagnostico: ordenSeleccionada.diagnostico || "",
      solucion: ordenSeleccionada.solucion || "",
      partesCambiadas: ordenSeleccionada.partesCambiadas || "",
      garantiaServicio: ordenSeleccionada.garantiaServicio ?? 30,
      observaciones: ordenSeleccionada.observaciones || ""
    });
  }, [selectedOrdenCierre, ordenes]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    
    if (name === "documento") {
      setClienteEncontrado(null);
      setClientMatches([]);
      setShowClientMatches(false);
      setClienteElectrodomesticos([]);
      setSelectedElectrodomestico(null);
      setFormulario((prev) => ({
        ...prev,
        documento: value,
        nombreCliente: ""
      }));
      return;
    }

    setFormulario((prev) => ({ ...prev, [name]: value }));
  };

  const seleccionarCliente = (cliente) => {
    if (!cliente) return;
    setClienteEncontrado(cliente);
    setFormulario((prev) => ({
      ...prev,
      documento: cliente.documento || prev.documento,
      nombreCliente: `${cliente.nombre || ""} ${cliente.apellido || ""}`.trim()
    }));
    setClientMatches([]);
    setShowClientMatches(false);
    setError("");
  };

  const handleDocumentoKeyDown = async (e) => {
    if (e.key !== "Enter") return;
    e.preventDefault();

    if (!formulario.documento.trim()) {
      setError("Por favor ingresa un documento");
      return;
    }

    try {
      setLoading(true);
      setError("");
      const response = await api.get(
        `/api/clientes/${formulario.documento}`
      );
      const data = response.data;

      if (Array.isArray(data)) {
        if (data.length === 1) {
          seleccionarCliente(data[0]);
        } else if (data.length > 1) {
          setClienteEncontrado(null);
          setClientMatches(data);
          setShowClientMatches(true);
        } else {
          setClienteEncontrado(null);
          setClientMatches([]);
          setShowClientMatches(false);
          setError("Cliente no encontrado");
        }
      } else {
        seleccionarCliente(data);
      }
    } catch (err) {
      setError("No se pudo buscar el cliente");
      setClienteEncontrado(null);
      setClientMatches([]);
      setShowClientMatches(false);
    } finally {
      setLoading(false);
    }
  };

  const handleCrearOrden = async (e) => {
    e.preventDefault();

    if (!clienteEncontrado) {
      setError("Debes cargar un cliente válido");
      return;
    }

    if (!selectedElectrodomestico) {
      setError("Debes seleccionar un electrodoméstico");
      return;
    }

    if (!formulario.descripcionProblema.trim()) {
      setError("La descripción del problema es requerida");
      return;
    }

    try {
      setLoading(true);
      
      const payload = {
        clienteId: clienteEncontrado.documento,
        clienteTipoDocumentoId: clienteEncontrado.tipoDocumentoId,
        electrodomesticoId: selectedElectrodomestico.id,
        tipoServicio: formulario.tipoServicio,
        descripcionProblema: formulario.descripcionProblema,
        observaciones: formulario.observaciones
      };

      const response = await api.post(
        "/api/servicios-reparacion/registrar",
        payload
      );

      if (response.data) {
        setSuccessMessage(`Orden creada exitosamente: ${response.data.id}`);
        setFormulario({
          documento: "",
          nombreCliente: "",
          tipoServicio: "REPARACION",
          descripcionProblema: "",
          observaciones: ""
        });
        setClienteEncontrado(null);
        setSelectedElectrodomestico(null);
        setActiveView("LISTA");
        cargarOrdenes();

        setTimeout(() => setSuccessMessage(""), 3000);
      }
    } catch (err) {
      setError("Error al crear la orden: " + err.response?.data?.message || err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleAsignarTecnico = async () => {
    if (!selectedOrdenAsignar || !selectedTecnico) {
      setError("Selecciona una orden y un técnico");
      return;
    }

    try {
      setLoading(true);
      const orden = ordenes.find((o) => o.id === selectedOrdenAsignar);
      
      const payload = {
        ...orden,
        tecnicoAsignadoUsername: selectedTecnico
      };

      await api.put(`/api/servicios-reparacion/${selectedOrdenAsignar}`, payload);
      
      setSuccessMessage("Técnico asignado correctamente");
      setSelectedOrdenAsignar("");
      setSelectedTecnico("");
      cargarOrdenes();
      
      setTimeout(() => setSuccessMessage(""), 3000);
    } catch (err) {
      setError("Error al asignar técnico");
    } finally {
      setLoading(false);
    }
  };

  const handleGuardarCierreConEstado = async (nuevoEstado, mensajeExito) => {
    if (!selectedOrdenCierre) {
      setError("Selecciona una orden");
      return;
    }

    try {
      setLoading(true);

      const ordenActual = ordenes.find((o) => o.id === selectedOrdenCierre);
      if (!ordenActual) {
        setError("No se encontró la orden seleccionada");
        return;
      }

      const payload = {
        ...ordenActual,
        diagnostico: cierreForm.diagnostico,
        solucion: cierreForm.solucion,
        partesCambiadas: cierreForm.partesCambiadas,
        garantiaServicio: cierreForm.garantiaServicio,
        observaciones: cierreForm.observaciones
      };

      await api.put(`/api/servicios-reparacion/${selectedOrdenCierre}`, payload);
      await api.put(
        `/api/servicios-reparacion/${selectedOrdenCierre}/estado/${nuevoEstado}`,
        {}
      );

      setSuccessMessage(mensajeExito);
      setCierreForm({
        diagnostico: "",
        solucion: "",
        partesCambiadas: "",
        garantiaServicio: 30,
        observaciones: ""
      });
      setSelectedOrdenCierre("");
      cargarOrdenes();

      setTimeout(() => setSuccessMessage(""), 3000);
    } catch (err) {
      setError("Error al guardar/cambiar estado");
    } finally {
      setLoading(false);
    }
  };

  const columns = [
    { key: "id", label: "ID Orden" },
    {
      key: "clienteNombre",
      label: "Cliente",
      render: (row) => `${row.clienteNombre || ""} ${row.clienteApellido || ""}`
    },
    { key: "electrodomesticoTipo", label: "Electrodoméstico" },
    { key: "tipoServicio", label: "Tipo" },
    { key: "estado", label: "Estado" },
    {
      key: "fechaIngreso",
      label: "Fecha",
      render: (row) => new Date(row.fechaIngreso).toLocaleDateString()
    }
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 p-6">
      <div className="max-w-7xl mx-auto">
        <h1 className="text-3xl font-bold text-gray-800 mb-6">Órdenes de Servicio</h1>

        {error && (
          <div className="mb-4 p-4 bg-red-50 border border-red-200 rounded-lg text-red-700">
            {error}
            <button
              onClick={() => setError("")}
              className="ml-4 text-sm underline"
            >
              Descartar
            </button>
          </div>
        )}

        {successMessage && (
          <div className="mb-4 p-4 bg-green-50 border border-green-200 rounded-lg text-green-700">
            {successMessage}
          </div>
        )}

        <div className="flex gap-4 mb-6">
          <button
            onClick={() => {
              setActiveView("LISTA");
              setError("");
            }}
            className={`px-6 py-2 rounded-lg font-medium transition ${
              activeView === "LISTA"
                ? "bg-blue-600 text-white"
                : "bg-white text-gray-700 border border-gray-300 hover:bg-gray-50"
            }`}
          >
            Listar
          </button>
          <button
            onClick={() => {
              setActiveView("CREAR");
              setError("");
            }}
            className={`px-6 py-2 rounded-lg font-medium transition ${
              activeView === "CREAR"
                ? "bg-blue-600 text-white"
                : "bg-white text-gray-700 border border-gray-300 hover:bg-gray-50"
            }`}
          >
            Crear Orden
          </button>
          <button
            onClick={() => {
              setActiveView("ASIGNAR");
              setError("");
            }}
            className={`px-6 py-2 rounded-lg font-medium transition ${
              activeView === "ASIGNAR"
                ? "bg-blue-600 text-white"
                : "bg-white text-gray-700 border border-gray-300 hover:bg-gray-50"
            }`}
          >
            Asignar Técnico
          </button>
          <button
            onClick={() => {
              setActiveView("Responder Orden");
              setError("");
            }}
            className={`px-6 py-2 rounded-lg font-medium transition ${
              activeView === "Responder Orden"
                ? "bg-blue-600 text-white"
                : "bg-white text-gray-700 border border-gray-300 hover:bg-gray-50"
            }`}
          >
            Responder Orden
          </button>
        </div>

        {activeView === "LISTA" && (
          <div className="bg-white rounded-lg shadow-lg p-6">
            <h2 className="text-xl font-semibold mb-4 text-gray-800">Órdenes Registradas</h2>
            <DataTable columns={columns} data={ordenes} />
          </div>
        )}

        {activeView === "CREAR" && (
          <div className="bg-white rounded-lg shadow-lg p-6 max-w-2xl">
            <h2 className="text-xl font-semibold mb-4 text-gray-800">Nueva Orden de Servicio</h2>
            
            <form onSubmit={handleCrearOrden} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Documento Cliente
                </label>
                <input
                  type="text"
                  name="documento"
                  value={formulario.documento}
                  onChange={handleInputChange}
                  onKeyDown={handleDocumentoKeyDown}
                  placeholder="Ingresa y presiona Enter"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                {clienteEncontrado && (
                  <p className="mt-2 text-sm text-green-600">
                    ✓ Cliente: {clienteEncontrado.nombre}
                  </p>
                )}
              </div>

              {clienteEncontrado && (
                <>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Electrodoméstico
                    </label>
                    <select
                      value={selectedElectrodomestico?.id || ""}
                      onChange={(e) => {
                        const selected = clienteElectrodomesticos.find(
                          (ce) => String(ce.id) === String(e.target.value)
                        );
                        setSelectedElectrodomestico(selected);
                      }}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    >
                      <option value="">-- Selecciona electrodoméstico --</option>
                      {clienteElectrodomesticos.map((ce) => (
                        <option key={ce.id} value={ce.id}>
                          {ce.electrodomesticoTipo} - {ce.electrodomesticoMarca} {ce.electrodomesticoModelo}
                        </option>
                      ))}
                    </select>
                    {clienteElectrodomesticos.length === 0 && (
                      <p className="mt-2 text-sm text-amber-600">
                        Este cliente no tiene electrodomésticos registrados para el tipo de documento seleccionado.
                      </p>
                    )}
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Tipo de Servicio
                    </label>
                    <select
                      name="tipoServicio"
                      value={formulario.tipoServicio}
                      onChange={handleInputChange}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    >
                      <option value="REPARACION">Reparación</option>
                      <option value="MANTENIMIENTO">Mantenimiento</option>
                      <option value="DIAGNOSTICO">Diagnóstico</option>
                    </select>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Descripción del Problema
                    </label>
                    <textarea
                      name="descripcionProblema"
                      value={formulario.descripcionProblema}
                      onChange={handleInputChange}
                      placeholder="Describe el problema del electrodoméstico"
                      rows="4"
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Observaciones
                    </label>
                    <textarea
                      name="observaciones"
                      value={formulario.observaciones}
                      onChange={handleInputChange}
                      placeholder="Observaciones adicionales (opcional)"
                      rows="3"
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>

                  <button
                    type="submit"
                    disabled={loading}
                    className="w-full py-2 bg-blue-600 text-white rounded-lg font-medium hover:bg-blue-700 disabled:opacity-50"
                  >
                    {loading ? "Creando..." : "Crear Orden"}
                  </button>
                </>
              )}

              {!clienteEncontrado && formulario.documento && (
                <p className="text-sm text-yellow-600">
                  Presiona Enter para buscar el cliente
                </p>
              )}
            </form>

            <div className="mt-6 p-4 bg-blue-50 rounded-lg">
              <p className="text-sm text-blue-700">
                <strong>Nota:</strong> Una vez creada la orden, puedes agregar productos/servicios mediante el módulo de Ventas.
              </p>
            </div>
          </div>
        )}

        {activeView === "ASIGNAR" && (
          <div className="bg-white rounded-lg shadow-lg p-6 max-w-2xl">
            <h2 className="text-xl font-semibold mb-4 text-gray-800">Asignar Técnico</h2>
            
            {tecnicosError && (
              <p className="text-sm text-red-600 mb-4">{tecnicosError}</p>
            )}

            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Selecciona Orden
                </label>
                <select
                  value={selectedOrdenAsignar}
                  onChange={(e) => setSelectedOrdenAsignar(e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="">-- Selecciona orden --</option>
                  {ordenes.map((orden) => (
                    <option key={orden.id} value={orden.id}>
                      {orden.id} - {orden.clienteNombre}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Selecciona Técnico
                </label>
                <select
                  value={selectedTecnico}
                  onChange={(e) => setSelectedTecnico(e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="">-- Selecciona técnico --</option>
                  {tecnicos.map((tecnico) => (
                    <option key={tecnico.username} value={tecnico.username}>
                      {tecnico.firstName} {tecnico.lastName}
                    </option>
                  ))}
                </select>
              </div>

              <button
                onClick={handleAsignarTecnico}
                disabled={loading}
                className="w-full py-2 bg-blue-600 text-white rounded-lg font-medium hover:bg-blue-700 disabled:opacity-50"
              >
                {loading ? "Asignando..." : "Asignar Técnico"}
              </button>
            </div>
          </div>
        )}

        {activeView === "Responder Orden" && (
          <div className="bg-white rounded-lg shadow-lg p-6 max-w-2xl">
            <h2 className="text-xl font-semibold mb-4 text-gray-800">Cierre de Orden</h2>
            
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Selecciona Orden
                </label>
                <select
                  value={selectedOrdenCierre}
                  onChange={(e) => setSelectedOrdenCierre(e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="">-- Selecciona orden --</option>
                  {ordenes.map((orden) => (
                    <option key={orden.id} value={orden.id}>
                      {orden.id} - {orden.clienteNombre} ({orden.estado})
                    </option>
                  ))}
                </select>
              </div>

              {selectedOrdenCierre && (
                <>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Diagnóstico
                    </label>
                    <textarea
                      value={cierreForm.diagnostico}
                      onChange={(e) =>
                        setCierreForm((prev) => ({
                          ...prev,
                          diagnostico: e.target.value
                        }))
                      }
                      rows="3"
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Solución
                    </label>
                    <textarea
                      value={cierreForm.solucion}
                      onChange={(e) =>
                        setCierreForm((prev) => ({
                          ...prev,
                          solucion: e.target.value
                        }))
                      }
                      rows="3"
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Partes Cambiadas
                    </label>
                    <input
                      type="text"
                      value={cierreForm.partesCambiadas}
                      onChange={(e) =>
                        setCierreForm((prev) => ({
                          ...prev,
                          partesCambiadas: e.target.value
                        }))
                      }
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Garantía (días)
                    </label>
                    <input
                      type="number"
                      value={cierreForm.garantiaServicio}
                      onChange={(e) =>
                        setCierreForm((prev) => ({
                          ...prev,
                          garantiaServicio: parseInt(e.target.value)
                        }))
                      }
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Observaciones
                    </label>
                    <textarea
                      value={cierreForm.observaciones}
                      onChange={(e) =>
                        setCierreForm((prev) => ({
                          ...prev,
                          observaciones: e.target.value
                        }))
                      }
                      rows="2"
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>

                  <button
                    onClick={() =>
                      handleGuardarCierreConEstado("EN_PROCESO", "Información guardada y orden en proceso")
                    }
                    disabled={loading}
                    className="w-full py-2 bg-amber-600 text-white rounded-lg font-medium hover:bg-amber-700 disabled:opacity-50"
                  >
                    {loading ? "Procesando..." : "Salvar"}
                  </button>

                  <button
                    onClick={() =>
                      handleGuardarCierreConEstado("REPARADO", "Orden cerrada y marcada como REPARADO")
                    }
                    disabled={loading}
                    className="w-full py-2 bg-blue-600 text-white rounded-lg font-medium hover:bg-blue-700 disabled:opacity-50"
                  >
                    {loading ? "Procesando..." : "Cerrar"}
                  </button>

                  <button
                    onClick={() =>
                      handleGuardarCierreConEstado("ENTREGADO", "Orden entregada correctamente")
                    }
                    disabled={loading}
                    className="w-full py-2 bg-green-600 text-white rounded-lg font-medium hover:bg-green-700 disabled:opacity-50"
                  >
                    {loading ? "Procesando..." : "Entregar"}
                  </button>
                </>
              )}
            </div>
          </div>
        )}

        {showClientMatches && (
          <div className="fixed inset-0 bg-gray-900 bg-opacity-40 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-lg shadow-xl w-full max-w-3xl">
              <div className="px-6 py-4 border-b border-gray-200 flex items-center justify-between">
                <h3 className="text-lg font-semibold text-gray-900">
                  Selecciona el cliente
                </h3>
                <button
                  type="button"
                  onClick={() => setShowClientMatches(false)}
                  className="text-gray-500 hover:text-gray-700"
                >
                  Cerrar
                </button>
              </div>
              <div className="p-6">
                <p className="text-sm text-gray-600 mb-3">
                  Se encontraron varias coincidencias para el documento. Doble click para seleccionar.
                </p>
                <div className="overflow-x-auto border border-gray-200 rounded">
                  <table className="w-full">
                    <thead className="bg-gray-100 border-b border-gray-200">
                      <tr>
                        <th className="px-4 py-2 text-left text-sm font-semibold text-gray-900">Documento</th>
                        <th className="px-4 py-2 text-left text-sm font-semibold text-gray-900">Tipo</th>
                        <th className="px-4 py-2 text-left text-sm font-semibold text-gray-900">Nombre</th>
                        <th className="px-4 py-2 text-left text-sm font-semibold text-gray-900">Telefono</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-200">
                      {clientMatches.map((cliente) => (
                        <tr
                          key={`${cliente.documento}-${cliente.tipoDocumentoId || ""}`}
                          onDoubleClick={() => seleccionarCliente(cliente)}
                          className="hover:bg-gray-50 cursor-pointer"
                        >
                          <td className="px-4 py-2 text-sm text-gray-900">{cliente.documento}</td>
                          <td className="px-4 py-2 text-sm text-gray-900">
                            {cliente.tipoDocumentoName || "-"}
                          </td>
                          <td className="px-4 py-2 text-sm text-gray-900">
                            {cliente.nombre} {cliente.apellido}
                          </td>
                          <td className="px-4 py-2 text-sm text-gray-900">{cliente.telefono || "-"}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
