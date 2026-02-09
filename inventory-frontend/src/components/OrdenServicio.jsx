import React, { useEffect, useMemo, useState } from "react";
import api from "./utils/axiosConfig";
import DataTable from "./DataTable";

export default function OrdenServicio() {
  const [ordenes, setOrdenes] = useState([]);
  const [productos, setProductos] = useState([]);
  const [clienteEncontrado, setClienteEncontrado] = useState(null);
  const [clienteElectrodomesticos, setClienteElectrodomesticos] = useState([]);
  const [selectedElectrodomestico, setSelectedElectrodomestico] = useState(null);
  const [activeView, setActiveView] = useState("LISTA");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [tecnicos, setTecnicos] = useState([]);
  const [tecnicosError, setTecnicosError] = useState("");
  const [selectedOrdenAsignar, setSelectedOrdenAsignar] = useState("");
  const [selectedTecnico, setSelectedTecnico] = useState("");
  const [selectedOrdenCierre, setSelectedOrdenCierre] = useState("");
  const [cierreForm, setCierreForm] = useState({
    diagnostico: "",
    solucion: "",
    partesCambiadas: "",
    costoServicio: "",
    costoRepuestos: "",
    garantiaServicio: "",
    observaciones: "",
    estado: "ENTREGADO"
  });
  const [clientMatches, setClientMatches] = useState([]);
  const [showClientMatches, setShowClientMatches] = useState(false);
  const [servicioCodeInput, setServicioCodeInput] = useState("");
  const [productCodeInput, setProductCodeInput] = useState("");
  const [showServiceSearch, setShowServiceSearch] = useState(false);
  const [showProductSearch, setShowProductSearch] = useState(false);
  const [showPriceConfirm, setShowPriceConfirm] = useState(false);
  const [pendingPriceItemId, setPendingPriceItemId] = useState(null);
  const [priceEditItemId, setPriceEditItemId] = useState(null);
  const [productFilters, setProductFilters] = useState({
    codigo: "",
    nombre: "",
    descripcion: "",
    categoria: "",
    electrodomestico: "",
    soloActivos: true,
    minPrecio: "",
    maxPrecio: "",
    minStock: ""
  });

  const [formulario, setFormulario] = useState({
    documento: "",
    nombreCliente: "",
    descripcionProblema: "",
    observaciones: "",
    items: []
  });

  useEffect(() => {
    cargarOrdenes();
    cargarProductos();
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

  const cargarProductos = async () => {
    try {
      const response = await api.get("/api/products/listar");
      const data = Array.isArray(response.data) ? response.data : [];
      setProductos(data);
    } catch (err) {
      console.error("Error al cargar productos:", err);
      setProductos([]);
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
      setTecnicosError(
        "No se pudo cargar la lista de tecnicos. Verifica permisos."
      );
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
        setClienteElectrodomesticos(response.data || []);
        setSelectedElectrodomestico(null);
      } catch (err) {
        console.error("Error al cargar electrodomesticos:", err);
        setClienteElectrodomesticos([]);
      }
    };

    fetchElectrodomesticos();
  }, [clienteEncontrado]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    if (name === "documento") {
      setClienteEncontrado(null);
      setClienteElectrodomesticos([]);
      setSelectedElectrodomestico(null);
      setServicioCodeInput("");
      setProductCodeInput("");
      setPriceEditItemId(null);
      setPendingPriceItemId(null);
      setShowPriceConfirm(false);
      setFormulario((prev) => ({
        ...prev,
        documento: value,
        nombreCliente: "",
        items: []
      }));
      return;
    }

    setFormulario((prev) => ({ ...prev, [name]: value }));
  };

  const handleDocumentoKeyDown = async (e) => {
    if (e.key !== "Enter") return;
    e.preventDefault();
    await fetchClienteByDocumento(formulario.documento);
  };

  const fetchClienteByDocumento = async (documento) => {
    if (!documento || !documento.trim()) {
      setError("Ingresa un documento valido");
      return;
    }

    try {
      setError("");
      const response = await api.get(`/api/clientes/${documento.trim()}`);
      const data = response.data;

      if (Array.isArray(data)) {
        if (data.length === 1) {
          seleccionarCliente(data[0]);
        } else if (data.length > 1) {
          setClientMatches(data);
          setShowClientMatches(true);
        } else {
          setClienteEncontrado(null);
          setError("Cliente no encontrado con ese documento");
        }
        return;
      }

      if (data) {
        seleccionarCliente(data);
      } else {
        setClienteEncontrado(null);
        setError("Cliente no encontrado con ese documento");
      }
    } catch (err) {
      setClienteEncontrado(null);
      setError("Cliente no encontrado con ese documento");
    }
  };

  const seleccionarCliente = (cliente) => {
    setClienteEncontrado(cliente);
    setFormulario((prev) => ({
      ...prev,
      documento: cliente?.documento || prev.documento,
      nombreCliente: cliente?.nombre || prev.nombreCliente
    }));
    setClientMatches([]);
    setShowClientMatches(false);
  };

  const addProductoToItems = (producto) => {
    setFormulario((prev) => {
      const existing = prev.items.find(
        (item) => String(item.productId) === String(producto.id)
      );

      if (existing) {
        return {
          ...prev,
          items: prev.items.map((item) =>
            String(item.productId) === String(producto.id)
              ? { ...item, cantidad: parseInt(item.cantidad || 0) + 1 }
              : item
          )
        };
      }

      const nuevoItem = {
        id: `${producto.id}-${Date.now()}`,
        productId: producto.id,
        productName: producto.name,
        cantidad: 1,
        precioUnitario: producto.price || 0,
        categoryId: getCategoryId(producto)
      };

      return {
        ...prev,
        items: [...prev.items, nuevoItem]
      };
    });
  };

  const getCategoryId = (producto) =>
    producto?.categoryId || producto?.category?.id || "";

  const isLista = activeView === "LISTA";
  const isCrear = activeView === "CREAR";
  const isAsignar = activeView === "ASIGNAR";
  const isCierre = activeView === "CIERRE";

  const changeView = (view) => {
    setActiveView(view);
    setError("");
    setSuccessMessage("");
  };

  const ordenAsignar = useMemo(
    () => ordenes.find((orden) => String(orden.id) === String(selectedOrdenAsignar)),
    [ordenes, selectedOrdenAsignar]
  );

  const ordenCierre = useMemo(
    () => ordenes.find((orden) => String(orden.id) === String(selectedOrdenCierre)),
    [ordenes, selectedOrdenCierre]
  );

  useEffect(() => {
    if (!ordenCierre) {
      setCierreForm({
        diagnostico: "",
        solucion: "",
        partesCambiadas: "",
        costoServicio: "",
        costoRepuestos: "",
        garantiaServicio: "",
        observaciones: "",
        estado: "ENTREGADO"
      });
      return;
    }

    setCierreForm({
      diagnostico: ordenCierre.diagnostico || "",
      solucion: ordenCierre.solucion || "",
      partesCambiadas: ordenCierre.partesCambiadas || "",
      costoServicio: ordenCierre.costoServicio != null ? String(ordenCierre.costoServicio) : "",
      costoRepuestos: ordenCierre.costoRepuestos != null ? String(ordenCierre.costoRepuestos) : "",
      garantiaServicio: ordenCierre.garantiaServicio != null ? String(ordenCierre.garantiaServicio) : "",
      observaciones: ordenCierre.observaciones || "",
      estado: ordenCierre.estado || "ENTREGADO"
    });
  }, [ordenCierre]);

  const clienteValido = Boolean(clienteEncontrado?.documento);
  const electroValido = Boolean(selectedElectrodomestico?.id);
  const tieneServicio = formulario.items.some((item) => item.categoryId === "S");

  const handleAddServiceByCode = () => {
    if (!clienteValido) {
      setError("Debes cargar un cliente valido antes de agregar productos");
      return;
    }
    if (!electroValido) {
      setError("Debes seleccionar un electrodomestico antes de agregar productos");
      return;
    }

    const code = servicioCodeInput.trim();
    if (!code) {
      setError("Ingresa un codigo de servicio");
      return;
    }

    const producto = productos.find((p) => String(p.id) === String(code));
    if (!producto) {
      setError("Producto no encontrado con ese codigo");
      return;
    }

    if (producto.activo === false) {
      setError("El producto seleccionado esta inactivo");
      return;
    }

    if (getCategoryId(producto) !== "S") {
      setError("El producto no es de tipo SERVICIO (S)");
      return;
    }

    addProductoToItems(producto);
    setServicioCodeInput("");
    setError("");
  };

  const handleServiceCodeKeyDown = (e) => {
    if (!clienteValido) {
      e.preventDefault();
      setError("Debes cargar un cliente valido antes de buscar productos");
      return;
    }
    if (!electroValido) {
      e.preventDefault();
      setError("Debes seleccionar un electrodomestico antes de buscar productos");
      return;
    }

    if (e.key === "F2") {
      e.preventDefault();
      setShowServiceSearch(true);
      return;
    }

    if (e.key === "Enter") {
      e.preventDefault();
      handleAddServiceByCode();
    }
  };

  const handleAddOtherByCode = () => {
    if (!clienteValido) {
      setError("Debes cargar un cliente valido antes de agregar productos");
      return;
    }
    if (!electroValido) {
      setError("Debes seleccionar un electrodomestico antes de agregar productos");
      return;
    }
    if (!tieneServicio) {
      setError("Primero agrega un producto de tipo SERVICIO (S)");
      return;
    }

    const code = productCodeInput.trim();
    if (!code) {
      setError("Ingresa un codigo de producto");
      return;
    }

    const producto = productos.find((p) => String(p.id) === String(code));
    if (!producto) {
      setError("Producto no encontrado con ese codigo");
      return;
    }

    if (producto.activo === false) {
      setError("El producto seleccionado esta inactivo");
      return;
    }

    if (getCategoryId(producto) === "S") {
      setError("Este producto es SERVICIO (S). Agregalo en la seccion de servicio");
      return;
    }

    addProductoToItems(producto);
    setProductCodeInput("");
    setError("");
  };

  const handleProductCodeKeyDown = (e) => {
    if (!clienteValido) {
      e.preventDefault();
      setError("Debes cargar un cliente valido antes de buscar productos");
      return;
    }
    if (!electroValido) {
      e.preventDefault();
      setError("Debes seleccionar un electrodomestico antes de buscar productos");
      return;
    }
    if (!tieneServicio) {
      e.preventDefault();
      setError("Primero agrega un producto de tipo SERVICIO (S)");
      return;
    }

    if (e.key === "F2") {
      e.preventDefault();
      setShowProductSearch(true);
      return;
    }

    if (e.key === "Enter") {
      e.preventDefault();
      handleAddOtherByCode();
    }
  };

  const handleSelectServicio = (producto) => {
    if (!clienteValido) {
      setShowServiceSearch(false);
      setError("Debes cargar un cliente valido antes de agregar productos");
      return;
    }
    if (!electroValido) {
      setShowServiceSearch(false);
      setError("Debes seleccionar un electrodomestico antes de agregar productos");
      return;
    }
    if (!producto) {
      return;
    }

    if (producto.activo === false) {
      setError("El producto seleccionado esta inactivo");
      return;
    }

    if (getCategoryId(producto) !== "S") {
      setError("El producto no es de tipo SERVICIO (S)");
      return;
    }

    addProductoToItems(producto);
    setServicioCodeInput("");
    setShowServiceSearch(false);
    setError("");
  };

  const handleSelectOtro = (producto) => {
    if (!clienteValido) {
      setShowProductSearch(false);
      setError("Debes cargar un cliente valido antes de agregar productos");
      return;
    }
    if (!electroValido) {
      setShowProductSearch(false);
      setError("Debes seleccionar un electrodomestico antes de agregar productos");
      return;
    }
    if (!producto) {
      return;
    }

    if (producto.activo === false) {
      setError("El producto seleccionado esta inactivo");
      return;
    }

    if (getCategoryId(producto) === "S") {
      setError("Este producto es SERVICIO (S). Agregalo en la seccion de servicio");
      return;
    }

    if (!tieneServicio) {
      setError("Primero agrega un producto de tipo SERVICIO (S)");
      return;
    }

    addProductoToItems(producto);
    setProductCodeInput("");
    setShowProductSearch(false);
    setError("");
  };

  const updateProductFilter = (field, value) => {
    setProductFilters((prev) => ({
      ...prev,
      [field]: value
    }));
  };

  const handleRemoveItem = (itemId) => {
    if (!clienteValido || !electroValido) {
      setError("Debes cargar cliente y electrodomestico antes de editar productos");
      return;
    }
    if (priceEditItemId === itemId) {
      setPriceEditItemId(null);
    }
    if (pendingPriceItemId === itemId) {
      setPendingPriceItemId(null);
      setShowPriceConfirm(false);
    }
    setFormulario((prev) => ({
      ...prev,
      items: prev.items.filter((item) => item.id !== itemId)
    }));
  };

  const handleItemChange = (itemId, field, value) => {
    if (!clienteValido || !electroValido) {
      setError("Debes cargar cliente y electrodomestico antes de editar productos");
      return;
    }
    setFormulario((prev) => ({
      ...prev,
      items: prev.items.map((item) =>
        item.id === itemId ? { ...item, [field]: value } : item
      )
    }));
  };

  const requestPriceEdit = (itemId) => {
    if (priceEditItemId === itemId) {
      return;
    }
    setPendingPriceItemId(itemId);
    setShowPriceConfirm(true);
  };

  const confirmPriceEdit = () => {
    if (!pendingPriceItemId) {
      setShowPriceConfirm(false);
      return;
    }
    setPriceEditItemId(pendingPriceItemId);
    setPendingPriceItemId(null);
    setShowPriceConfirm(false);
  };

  const cancelPriceEdit = () => {
    setPendingPriceItemId(null);
    setShowPriceConfirm(false);
  };

  const handleCierreChange = (e) => {
    const { name, value } = e.target;
    setCierreForm((prev) => ({
      ...prev,
      [name]: value
    }));
  };

  const handleAsignarTecnico = async () => {
    if (!selectedOrdenAsignar) {
      setError("Selecciona una orden para asignar");
      return;
    }
    if (!selectedTecnico) {
      setError("Selecciona un tecnico");
      return;
    }

    setLoading(true);
    setError("");
    setSuccessMessage("");
    try {
      const payload = {
        tecnicoAsignadoUsername: selectedTecnico,
        estado: ordenAsignar?.estado || "RECIBIDO"
      };
      await api.put(`/api/servicios-reparacion/${selectedOrdenAsignar}`, payload);
      setSuccessMessage("Tecnico asignado correctamente");
      setSelectedOrdenAsignar("");
      setSelectedTecnico("");
      cargarOrdenes();
    } catch (err) {
      setError(
        "Error al asignar tecnico: " + (err.response?.data?.message || err.message)
      );
    } finally {
      setLoading(false);
    }
  };

  const handleCerrarOrden = async () => {
    if (!selectedOrdenCierre) {
      setError("Selecciona una orden para cerrar");
      return;
    }
    if (!cierreForm.diagnostico.trim() || !cierreForm.solucion.trim()) {
      setError("Completa diagnostico y solucion");
      return;
    }

    setLoading(true);
    setError("");
    setSuccessMessage("");
    try {
      const payload = {
        diagnostico: cierreForm.diagnostico.trim(),
        solucion: cierreForm.solucion.trim(),
        partesCambiadas: cierreForm.partesCambiadas.trim() || null,
        costoServicio: cierreForm.costoServicio ? parseFloat(cierreForm.costoServicio) : null,
        costoRepuestos: cierreForm.costoRepuestos ? parseFloat(cierreForm.costoRepuestos) : null,
        garantiaServicio: cierreForm.garantiaServicio ? parseInt(cierreForm.garantiaServicio, 10) : null,
        observaciones: cierreForm.observaciones.trim() || null,
        estado: cierreForm.estado || "ENTREGADO"
      };
      await api.put(`/api/servicios-reparacion/${selectedOrdenCierre}`, payload);
      setSuccessMessage("Orden cerrada correctamente");
      setSelectedOrdenCierre("");
      cargarOrdenes();
    } catch (err) {
      setError(
        "Error al cerrar orden: " + (err.response?.data?.message || err.message)
      );
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    setSuccessMessage("");

    try {
      if (!clienteValido) {
        setError("Debes ingresar un documento valido y cargar el cliente");
        setLoading(false);
        return;
      }
      if (!electroValido) {
        setError("Debes seleccionar un electrodomestico");
        setLoading(false);
        return;
      }
      if (!formulario.descripcionProblema.trim()) {
        setError("Debes describir el problema");
        setLoading(false);
        return;
      }
      if (formulario.items.length === 0) {
        setError("Agrega al menos un producto a la orden");
        setLoading(false);
        return;
      }
      if (!tieneServicio) {
        setError("Debes agregar al menos un producto de tipo SERVICIO (S)");
        setLoading(false);
        return;
      }

      const payload = {
        clienteId: clienteEncontrado.documento,
        clienteTipoDocumentoId: clienteEncontrado.tipoDocumentoId,
        electrodomesticoId: selectedElectrodomestico.id,
        tipoServicio: "REPARACION",
        descripcionProblema: formulario.descripcionProblema.trim(),
        observaciones: formulario.observaciones?.trim() || null,
        productos: formulario.items.map((item) => ({
          productId: item.productId,
          cantidad: item.cantidad || 1,
          precioUnitario: item.precioUnitario
        }))
      };

      const response = await api.post("/api/servicios-reparacion/registrar", payload);

      setSuccessMessage(
        `Orden de servicio creada exitosamente. ID: ${response.data?.id || "N/A"}`
      );

      setFormulario({
        documento: "",
        nombreCliente: "",
        descripcionProblema: "",
        observaciones: "",
        items: []
      });
      setClienteEncontrado(null);
      setClienteElectrodomesticos([]);
      setSelectedElectrodomestico(null);
      setServicioCodeInput("");
      setProductCodeInput("");
      setPriceEditItemId(null);
      setPendingPriceItemId(null);
      setShowPriceConfirm(false);
      setActiveView("LISTA");

      cargarOrdenes();
      cargarProductos();
      setTimeout(() => setSuccessMessage(""), 3000);
    } catch (err) {
      setError(
        "Error al crear la orden: " + (err.response?.data?.message || err.message)
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
      minute: "2-digit"
    });
  };

  const filterProductos = (lista) => {
    let result = lista;
    const {
      codigo,
      nombre,
      descripcion,
      categoria,
      electrodomestico,
      soloActivos,
      minPrecio,
      maxPrecio,
      minStock
    } = productFilters;

    if (soloActivos) {
      result = result.filter((p) => p.activo !== false);
    }

    if (codigo) {
      const value = codigo.toLowerCase();
      result = result.filter((p) => String(p.id || "").toLowerCase().includes(value));
    }

    if (nombre) {
      const value = nombre.toLowerCase();
      result = result.filter((p) => String(p.name || "").toLowerCase().includes(value));
    }

    if (descripcion) {
      const value = descripcion.toLowerCase();
      result = result.filter((p) =>
        String(p.description || "").toLowerCase().includes(value)
      );
    }

    if (categoria) {
      const value = categoria.toLowerCase();
      result = result.filter((p) =>
        String(getCategoryId(p)).toLowerCase().includes(value)
      );
    }

    if (electrodomestico) {
      const value = electrodomestico.toLowerCase();
      result = result.filter((p) =>
        String(p.categoriaElectrodomesticoId || "").toLowerCase().includes(value)
      );
    }

    const minPrecioValue = parseFloat(minPrecio);
    if (!Number.isNaN(minPrecioValue)) {
      result = result.filter((p) => parseFloat(p.price || 0) >= minPrecioValue);
    }

    const maxPrecioValue = parseFloat(maxPrecio);
    if (!Number.isNaN(maxPrecioValue)) {
      result = result.filter((p) => parseFloat(p.price || 0) <= maxPrecioValue);
    }

    const minStockValue = parseInt(minStock, 10);
    if (!Number.isNaN(minStockValue)) {
      result = result.filter((p) => parseInt(p.quantity || 0, 10) >= minStockValue);
    }

    return result;
  };

  const filteredServicios = useMemo(() => {
    const result = filterProductos(productos);
    return result.filter((p) => getCategoryId(p) === "S");
  }, [productos, productFilters]);

  const filteredOtros = useMemo(() => {
    const result = filterProductos(productos);
    return result.filter((p) => getCategoryId(p) !== "S");
  }, [productos, productFilters]);

  return (
    <div className="p-6 bg-gray-50 min-h-screen">
      <div className="max-w-7xl mx-auto">
        <div className="mb-4 flex flex-wrap gap-2 border-b border-gray-200 pb-2">
          <button
            type="button"
            onClick={() => changeView("LISTA")}
            className={`h-9 px-3 rounded-lg text-sm font-medium transition-all ${
              isLista
                ? "bg-blue-600 text-white"
                : "bg-white text-gray-700 border border-gray-300 hover:bg-gray-50"
            }`}
          >
            Historial
          </button>
          <button
            type="button"
            onClick={() => changeView("CIERRE")}
            className={`h-9 px-3 rounded-lg text-sm font-medium transition-all ${
              isCierre
                ? "bg-blue-600 text-white"
                : "bg-white text-gray-700 border border-gray-300 hover:bg-gray-50"
            }`}
          >
            Responder servicio
          </button>
        </div>

        {isLista && (
          <div className="flex justify-between items-center mb-4 flex-wrap gap-2">
            <h3 className="text-sm md:text-base font-bold">
              Historial de ordenes de servicio ({ordenes.length})
            </h3>
            <button
              onClick={() => changeView("CREAR")}
              className="h-9 px-4 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-all text-sm font-medium"
            >
              + Nueva Orden
            </button>
          </div>
        )}

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

        {isCrear && (
          <div className="mb-6 bg-white p-4 md:p-5 rounded-lg shadow">
            <h2 className="text-xl font-semibold mb-3 text-gray-900">
              Registrar Nueva Orden de Servicio
            </h2>
            <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-3 gap-3">
              <div>
                <label className="block text-xs font-semibold text-gray-700 mb-1">
                  Documento *
                </label>
                <input
                  type="text"
                  name="documento"
                  value={formulario.documento}
                  onChange={handleInputChange}
                  onKeyDown={handleDocumentoKeyDown}
                  className="w-full h-9 px-3 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600"
                  placeholder="Numero de documento"
                  required
                />
                <p className="text-[11px] text-gray-500 mt-1">Enter para buscar</p>
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-700 mb-1">
                  Nombre *
                </label>
                <input
                  type="text"
                  name="nombreCliente"
                  value={formulario.nombreCliente}
                  onChange={handleInputChange}
                  readOnly
                  className="w-full h-9 px-3 text-sm border border-gray-300 rounded-lg bg-gray-100 text-gray-700"
                  placeholder="Nombre del cliente"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-700 mb-1">
                  Electrodomestico *
                </label>
                <select
                  value={selectedElectrodomestico ? selectedElectrodomestico.id : ""}
                  onChange={(event) => {
                    const selectedId = Number(event.target.value);
                    const electro = clienteElectrodomesticos.find(
                      (item) => item.id === selectedId
                    );
                    setSelectedElectrodomestico(electro || null);
                  }}
                  disabled={!clienteValido}
                  className="w-full h-9 px-3 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600 disabled:bg-gray-100 disabled:text-gray-500"
                >
                  <option value="">Seleccionar electrodomestico...</option>
                  {clienteElectrodomesticos.map((electro) => (
                    <option key={electro.id} value={electro.id}>
                      {electro.electrodomesticoTipo} - {electro.electrodomesticoModelo} ({electro.numeroSerie})
                    </option>
                  ))}
                </select>
              </div>

              

              <div>
                <label className="block text-xs font-semibold text-gray-700 mb-1">
                  Codigo servicio (S) *
                </label>
                <input
                  type="text"
                  name="servicioCode"
                  value={servicioCodeInput}
                  onChange={(e) => setServicioCodeInput(e.target.value)}
                  onKeyDown={handleServiceCodeKeyDown}
                  className="w-full h-9 px-3 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600 disabled:bg-gray-100 disabled:text-gray-500"
                  placeholder="Codigo de servicio"
                  disabled={!clienteValido || !electroValido}
                />
                <p className="text-[11px] text-gray-500 mt-1">
                  {clienteValido && electroValido
                    ? "Enter agrega, F2 busca"
                    : "Primero carga cliente y electrodomestico"}
                </p>
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-700 mb-1">
                  Codigo producto
                </label>
                <input
                  type="text"
                  name="productCode"
                  value={productCodeInput}
                  onChange={(e) => setProductCodeInput(e.target.value)}
                  onKeyDown={handleProductCodeKeyDown}
                  className="w-full h-9 px-3 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600 disabled:bg-gray-100 disabled:text-gray-500"
                  placeholder="Codigo del producto"
                  disabled={!clienteValido || !electroValido || !tieneServicio}
                />
                <p className="text-[11px] text-gray-500 mt-1">
                  {clienteValido && electroValido && tieneServicio
                    ? "Enter agrega, F2 busca"
                    : "Agrega primero un servicio (S)"}
                </p>
              </div>

              <div className="md:col-span-2">
                <label className="block text-xs font-semibold text-gray-700 mb-1">
                  Descripcion del problema *
                </label>
                <textarea
                  name="descripcionProblema"
                  value={formulario.descripcionProblema}
                  onChange={handleInputChange}
                  className="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600"
                  rows="2"
                  placeholder="Describe el problema reportado"
                  required
                ></textarea>
              </div>

              <div className="md:col-span-3">
                {formulario.items.length === 0 ? (
                  <p className="text-sm text-gray-500">No hay productos agregados</p>
                ) : (
                  <div className="overflow-x-auto border border-gray-200 rounded-lg max-h-[200px] overflow-y-auto">
                    <table className="w-full">
                      <thead className="bg-gray-100 border-b border-gray-200">
                        <tr>
                          <th className="px-3 py-2 text-left text-sm font-semibold text-gray-900">
                            Producto
                          </th>
                          <th className="px-3 py-2 text-left text-sm font-semibold text-gray-900">
                            Cantidad
                          </th>
                          <th className="px-3 py-2 text-left text-sm font-semibold text-gray-900">
                            Precio Unit.
                          </th>
                          <th className="px-3 py-2 text-left text-sm font-semibold text-gray-900">
                            Subtotal
                          </th>
                          <th className="px-3 py-2 text-left text-sm font-semibold text-gray-900"></th>
                        </tr>
                      </thead>
                      <tbody className="divide-y divide-gray-200">
                        {formulario.items.map((item) => (
                          <tr key={item.id}>
                            <td className="px-3 py-2 text-sm text-gray-900">
                              {item.productName}
                            </td>
                            <td className="px-3 py-2 text-sm text-gray-900">
                              <input
                                type="number"
                                min="1"
                                value={item.cantidad}
                                onChange={(e) =>
                                  handleItemChange(
                                    item.id,
                                    "cantidad",
                                    parseInt(e.target.value || 0)
                                  )
                                }
                                className="w-20 px-2 py-1 border border-gray-300 rounded text-sm"
                              />
                            </td>
                            <td className="px-3 py-2 text-sm text-gray-900">
                              <input
                                type="number"
                                min="0"
                                step="0.01"
                                value={item.precioUnitario}
                                readOnly={priceEditItemId !== item.id}
                                onChange={(e) =>
                                  handleItemChange(
                                    item.id,
                                    "precioUnitario",
                                    parseFloat(e.target.value || 0)
                                  )
                                }
                                onClick={() => requestPriceEdit(item.id)}
                                className="w-24 px-2 py-1 border border-gray-300 rounded text-sm read-only:bg-gray-100 read-only:text-gray-500"
                              />
                            </td>
                            <td className="px-3 py-2 text-sm text-gray-900">
                              ${(
                                parseFloat(item.cantidad || 0) *
                                parseFloat(item.precioUnitario || 0)
                              ).toFixed(2)}
                            </td>
                            <td className="px-3 py-2 text-right">
                              <button
                                type="button"
                                onClick={() => handleRemoveItem(item.id)}
                                className="text-red-600 hover:text-red-700 text-xs"
                              >
                                Quitar
                              </button>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>

              <div className="md:col-span-3">
                <label className="block text-xs font-semibold text-gray-700 mb-1">
                  Observaciones
                </label>
                <textarea
                  name="observaciones"
                  value={formulario.observaciones}
                  onChange={handleInputChange}
                  className="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600"
                  rows="2"
                  placeholder="Notas adicionales (opcional)"
                ></textarea>
              </div>

              <div className="md:col-span-3 flex gap-3">
                <button
                  type="submit"
                  disabled={loading}
                  className="flex-1 h-9 px-3 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-all font-medium disabled:bg-gray-400 text-sm"
                >
                  {loading ? "Registrando..." : "Crear Orden"}
                </button>
                <button
                  type="button"
                  onClick={() => changeView("LISTA")}
                  className="flex-1 h-9 px-3 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400 transition-all font-medium text-sm"
                >
                  Cancelar
                </button>
              </div>
            </form>
          </div>
        )}

        {isLista && (
          <div className="overflow-x-auto">
            <div className="min-w-[1000px]">
              <DataTable
                data={ordenes}
                columns={[
                  {
                    key: "fechaIngreso",
                    label: "Fecha",
                    sortable: true,
                    filterable: true,
                    render: (orden) => formatearFecha(orden.fechaIngreso)
                  },
                  {
                    key: "id",
                    label: "Orden",
                    sortable: true,
                    filterable: true,
                    render: (orden) => orden.id || "-"
                  },
                  {
                    key: "clienteNombre",
                    label: "Cliente",
                    sortable: true,
                    filterable: true,
                    render: (orden) => orden.clienteNombre || "-"
                  },
                  {
                    key: "electrodomesticoTipo",
                    label: "Electrodomestico",
                    sortable: true,
                    filterable: true,
                    render: (orden) => orden.electrodomesticoTipo || "-"
                  },
                  {
                    key: "estado",
                    label: "Estado",
                    sortable: true,
                    filterable: true,
                    render: (orden) => orden.estado || "-"
                  },
                  {
                    key: "totalCosto",
                    label: "Total",
                    sortable: true,
                    filterable: false,
                    render: (orden) =>
                      orden.totalCosto != null ? `$${orden.totalCosto}` : "-"
                  },
                  {
                    key: "usuarioNombre",
                    label: "Usuario",
                    sortable: true,
                    filterable: true,
                    render: (orden) => orden.usuarioNombre || orden.usuarioUsername || "-"
                  }
                ]}
              />
            </div>
          </div>
        )}

        {isAsignar && (
          <div className="mb-6 bg-white p-4 md:p-5 rounded-lg shadow">
            <h2 className="text-xl font-semibold mb-3 text-gray-900">
              Asignar tecnico a orden
            </h2>
            {tecnicosError && (
              <div className="mb-3 p-3 bg-yellow-50 border border-yellow-200 text-yellow-800 rounded text-sm">
                {tecnicosError}
              </div>
            )}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
              <div>
                <label className="block text-xs font-semibold text-gray-700 mb-1">
                  Orden de servicio
                </label>
                <select
                  value={selectedOrdenAsignar}
                  onChange={(e) => setSelectedOrdenAsignar(e.target.value)}
                  className="w-full h-9 px-3 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600"
                >
                  <option value="">Seleccionar orden...</option>
                  {ordenes.map((orden) => (
                    <option key={orden.id} value={orden.id}>
                      {orden.id} - {orden.clienteNombre || "Cliente"} ({orden.estado || ""})
                    </option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-xs font-semibold text-gray-700 mb-1">
                  Tecnico
                </label>
                <select
                  value={selectedTecnico}
                  onChange={(e) => setSelectedTecnico(e.target.value)}
                  className="w-full h-9 px-3 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600"
                >
                  <option value="">Seleccionar tecnico...</option>
                  {tecnicos.map((tecnico) => (
                    <option key={tecnico.username} value={tecnico.username}>
                      {tecnico.firstName} {tecnico.lastName} ({tecnico.username})
                    </option>
                  ))}
                </select>
              </div>
            </div>
            {ordenAsignar && (
              <div className="mt-3 text-xs text-gray-600">
                Orden seleccionada: {ordenAsignar.id} - {ordenAsignar.electrodomesticoTipo || ""}
              </div>
            )}
            <div className="mt-4 flex gap-3">
              <button
                type="button"
                onClick={handleAsignarTecnico}
                disabled={loading}
                className="flex-1 h-9 px-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-all font-medium disabled:bg-gray-400 text-sm"
              >
                {loading ? "Asignando..." : "Asignar tecnico"}
              </button>
            </div>
          </div>
        )}

        {isCierre && (
          <div className="mb-6 bg-white p-4 md:p-5 rounded-lg shadow">
            <h2 className="text-xl font-semibold mb-3 text-gray-900">
              Resumen y cierre de servicio
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
              <div className="md:col-span-2">
                <label className="block text-xs font-semibold text-gray-700 mb-1">
                  Orden de servicio
                </label>
                <select
                  value={selectedOrdenCierre}
                  onChange={(e) => setSelectedOrdenCierre(e.target.value)}
                  className="w-full h-9 px-3 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600"
                >
                  <option value="">Seleccionar orden...</option>
                  {ordenes.map((orden) => (
                    <option key={orden.id} value={orden.id}>
                      {orden.id} - {orden.clienteNombre || "Cliente"} ({orden.estado || ""})
                    </option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-xs font-semibold text-gray-700 mb-1">
                  Diagnostico *
                </label>
                <textarea
                  name="diagnostico"
                  value={cierreForm.diagnostico}
                  onChange={handleCierreChange}
                  className="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600"
                  rows="2"
                ></textarea>
              </div>
              <div>
                <label className="block text-xs font-semibold text-gray-700 mb-1">
                  Solucion *
                </label>
                <textarea
                  name="solucion"
                  value={cierreForm.solucion}
                  onChange={handleCierreChange}
                  className="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600"
                  rows="2"
                ></textarea>
              </div>
              <div>
                <label className="block text-xs font-semibold text-gray-700 mb-1">
                  Partes cambiadas
                </label>
                <input
                  type="text"
                  name="partesCambiadas"
                  value={cierreForm.partesCambiadas}
                  onChange={handleCierreChange}
                  className="w-full h-9 px-3 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-gray-700 mb-1">
                  Costo servicio
                </label>
                <input
                  type="number"
                  min="0"
                  step="0.01"
                  name="costoServicio"
                  value={cierreForm.costoServicio}
                  onChange={handleCierreChange}
                  className="w-full h-9 px-3 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-gray-700 mb-1">
                  Costo repuestos
                </label>
                <input
                  type="number"
                  min="0"
                  step="0.01"
                  name="costoRepuestos"
                  value={cierreForm.costoRepuestos}
                  onChange={handleCierreChange}
                  className="w-full h-9 px-3 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-gray-700 mb-1">
                  Garantia (dias)
                </label>
                <input
                  type="number"
                  min="0"
                  name="garantiaServicio"
                  value={cierreForm.garantiaServicio}
                  onChange={handleCierreChange}
                  className="w-full h-9 px-3 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-gray-700 mb-1">
                  Estado
                </label>
                <select
                  name="estado"
                  value={cierreForm.estado}
                  onChange={handleCierreChange}
                  className="w-full h-9 px-3 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600"
                >
                  <option value="RECIBIDO">RECIBIDO</option>
                  <option value="EN_PROCESO">EN_PROCESO</option>
                  <option value="LISTO">LISTO</option>
                  <option value="ENTREGADO">ENTREGADO</option>
                </select>
              </div>
              <div className="md:col-span-2">
                <label className="block text-xs font-semibold text-gray-700 mb-1">
                  Observaciones
                </label>
                <textarea
                  name="observaciones"
                  value={cierreForm.observaciones}
                  onChange={handleCierreChange}
                  className="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600"
                  rows="2"
                ></textarea>
              </div>
            </div>
            <div className="mt-4 flex gap-3">
              <button
                type="button"
                onClick={handleCerrarOrden}
                disabled={loading}
                className="flex-1 h-9 px-3 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-all font-medium disabled:bg-gray-400 text-sm"
              >
                {loading ? "Guardando..." : "Guardar y cerrar"}
              </button>
            </div>
          </div>
        )}

        {showPriceConfirm && (
          <div className="fixed inset-0 bg-gray-900 bg-opacity-40 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-lg shadow-xl w-full max-w-md">
              <div className="px-5 py-4 border-b border-gray-200">
                <h3 className="text-lg font-semibold text-gray-900">
                  Confirmar cambio de precio
                </h3>
              </div>
              <div className="p-5">
                <p className="text-sm text-gray-700">
                  ¿Deseas cambiar el precio del producto seleccionado?
                </p>
              </div>
              <div className="px-5 py-4 border-t border-gray-200 flex gap-3">
                <button
                  type="button"
                  onClick={confirmPriceEdit}
                  className="flex-1 h-9 px-3 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-all font-medium text-sm"
                >
                  Si, cambiar
                </button>
                <button
                  type="button"
                  onClick={cancelPriceEdit}
                  className="flex-1 h-9 px-3 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400 transition-all font-medium text-sm"
                >
                  Cancelar
                </button>
              </div>
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
                  Se encontraron varias coincidencias. Doble click para seleccionar.
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
                          <td className="px-4 py-2 text-sm text-gray-900">{cliente.nombre}</td>
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

        {showServiceSearch && (
          <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center z-50 p-4">
            <div className="bg-gradient-to-br from-slate-50 to-amber-50 rounded-2xl shadow-2xl w-full max-w-3xl border border-slate-200/70">
              <div className="px-4 py-3 border-b border-slate-200/70 flex items-center justify-between">
                <div>
                  <h3 className="text-base font-semibold text-slate-900">
                    Busqueda rapida de servicios (S)
                  </h3>
                  <p className="text-xs text-slate-500">
                    Enter agrega, F2 abre, doble click selecciona.
                  </p>
                </div>
                <button
                  type="button"
                  onClick={() => setShowServiceSearch(false)}
                  className="text-slate-500 hover:text-slate-700 text-sm"
                >
                  Cerrar
                </button>
              </div>
              <div className="p-4">
                <div className="grid grid-cols-2 md:grid-cols-4 gap-2 mb-3">
                  <input
                    type="text"
                    value={productFilters.codigo}
                    onChange={(e) => updateProductFilter("codigo", e.target.value)}
                    className="w-full h-9 px-3 text-sm bg-white/80 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-400"
                    placeholder="Codigo"
                  />
                  <input
                    type="text"
                    value={productFilters.nombre}
                    onChange={(e) => updateProductFilter("nombre", e.target.value)}
                    className="w-full h-9 px-3 text-sm bg-white/80 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-400"
                    placeholder="Nombre"
                  />
                  <input
                    type="text"
                    value={productFilters.descripcion}
                    onChange={(e) => updateProductFilter("descripcion", e.target.value)}
                    className="w-full h-9 px-3 text-sm bg-white/80 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-400"
                    placeholder="Descripcion"
                  />
                  <input
                    type="text"
                    value={productFilters.categoria}
                    onChange={(e) => updateProductFilter("categoria", e.target.value)}
                    className="w-full h-9 px-3 text-sm bg-white/80 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-400"
                    placeholder="Categoria"
                  />
                  <input
                    type="text"
                    value={productFilters.electrodomestico}
                    onChange={(e) => updateProductFilter("electrodomestico", e.target.value)}
                    className="w-full h-9 px-3 text-sm bg-white/80 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-400"
                    placeholder="Electrodomestico"
                  />
                  <input
                    type="number"
                    value={productFilters.minPrecio}
                    onChange={(e) => updateProductFilter("minPrecio", e.target.value)}
                    className="w-full h-9 px-3 text-sm bg-white/80 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-400"
                    placeholder="Min $"
                    min="0"
                    step="0.01"
                  />
                  <input
                    type="number"
                    value={productFilters.maxPrecio}
                    onChange={(e) => updateProductFilter("maxPrecio", e.target.value)}
                    className="w-full h-9 px-3 text-sm bg-white/80 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-400"
                    placeholder="Max $"
                    min="0"
                    step="0.01"
                  />
                  <input
                    type="number"
                    value={productFilters.minStock}
                    onChange={(e) => updateProductFilter("minStock", e.target.value)}
                    className="w-full h-9 px-3 text-sm bg-white/80 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-400"
                    placeholder="Stock min"
                    min="0"
                    step="1"
                  />
                </div>

                <div className="flex items-center justify-between mb-3">
                  <label className="inline-flex items-center gap-2 text-xs text-slate-700 bg-white/80 border border-slate-200 rounded-full px-3 py-1">
                    <input
                      type="checkbox"
                      checked={productFilters.soloActivos}
                      onChange={(e) => updateProductFilter("soloActivos", e.target.checked)}
                    />
                    Solo activos
                  </label>
                  <span className="text-xs text-slate-500">
                    {filteredServicios.length} resultados
                  </span>
                </div>

                <div className="max-h-[45vh] overflow-y-auto border border-slate-200 rounded-xl bg-white/70">
                  <table className="w-full">
                    <thead className="bg-slate-100/80 border-b border-slate-200 sticky top-0">
                      <tr>
                        <th className="px-3 py-2 text-left text-xs font-semibold text-slate-700">Codigo</th>
                        <th className="px-3 py-2 text-left text-xs font-semibold text-slate-700">Producto</th>
                        <th className="px-3 py-2 text-left text-xs font-semibold text-slate-700">Precio</th>
                        <th className="px-3 py-2 text-left text-xs font-semibold text-slate-700">Stock</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-200">
                      {filteredServicios.map((producto) => (
                        <tr
                          key={producto.id}
                          onDoubleClick={() => handleSelectServicio(producto)}
                          className="hover:bg-amber-50/60 cursor-pointer"
                        >
                          <td className="px-3 py-2 text-xs text-slate-700">{producto.id}</td>
                          <td className="px-3 py-2 text-xs text-slate-700">{producto.name}</td>
                          <td className="px-3 py-2 text-xs text-slate-700">${producto.price}</td>
                          <td className="px-3 py-2 text-xs text-slate-700">{producto.quantity}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        )}

        {showProductSearch && (
          <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center z-50 p-4">
            <div className="bg-gradient-to-br from-slate-50 to-amber-50 rounded-2xl shadow-2xl w-full max-w-3xl border border-slate-200/70">
              <div className="px-4 py-3 border-b border-slate-200/70 flex items-center justify-between">
                <div>
                  <h3 className="text-base font-semibold text-slate-900">
                    Busqueda rapida de productos
                  </h3>
                  <p className="text-xs text-slate-500">
                    Enter agrega, F2 abre, doble click selecciona.
                  </p>
                </div>
                <button
                  type="button"
                  onClick={() => setShowProductSearch(false)}
                  className="text-slate-500 hover:text-slate-700 text-sm"
                >
                  Cerrar
                </button>
              </div>
              <div className="p-4">
                <div className="grid grid-cols-2 md:grid-cols-4 gap-2 mb-3">
                  <input
                    type="text"
                    value={productFilters.codigo}
                    onChange={(e) => updateProductFilter("codigo", e.target.value)}
                    className="w-full h-9 px-3 text-sm bg-white/80 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-400"
                    placeholder="Codigo"
                  />
                  <input
                    type="text"
                    value={productFilters.nombre}
                    onChange={(e) => updateProductFilter("nombre", e.target.value)}
                    className="w-full h-9 px-3 text-sm bg-white/80 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-400"
                    placeholder="Nombre"
                  />
                  <input
                    type="text"
                    value={productFilters.descripcion}
                    onChange={(e) => updateProductFilter("descripcion", e.target.value)}
                    className="w-full h-9 px-3 text-sm bg-white/80 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-400"
                    placeholder="Descripcion"
                  />
                  <input
                    type="text"
                    value={productFilters.categoria}
                    onChange={(e) => updateProductFilter("categoria", e.target.value)}
                    className="w-full h-9 px-3 text-sm bg-white/80 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-400"
                    placeholder="Categoria"
                  />
                  <input
                    type="text"
                    value={productFilters.electrodomestico}
                    onChange={(e) => updateProductFilter("electrodomestico", e.target.value)}
                    className="w-full h-9 px-3 text-sm bg-white/80 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-400"
                    placeholder="Electrodomestico"
                  />
                  <input
                    type="number"
                    value={productFilters.minPrecio}
                    onChange={(e) => updateProductFilter("minPrecio", e.target.value)}
                    className="w-full h-9 px-3 text-sm bg-white/80 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-400"
                    placeholder="Min $"
                    min="0"
                    step="0.01"
                  />
                  <input
                    type="number"
                    value={productFilters.maxPrecio}
                    onChange={(e) => updateProductFilter("maxPrecio", e.target.value)}
                    className="w-full h-9 px-3 text-sm bg-white/80 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-400"
                    placeholder="Max $"
                    min="0"
                    step="0.01"
                  />
                  <input
                    type="number"
                    value={productFilters.minStock}
                    onChange={(e) => updateProductFilter("minStock", e.target.value)}
                    className="w-full h-9 px-3 text-sm bg-white/80 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-400"
                    placeholder="Stock min"
                    min="0"
                    step="1"
                  />
                </div>

                <div className="flex items-center justify-between mb-3">
                  <label className="inline-flex items-center gap-2 text-xs text-slate-700 bg-white/80 border border-slate-200 rounded-full px-3 py-1">
                    <input
                      type="checkbox"
                      checked={productFilters.soloActivos}
                      onChange={(e) => updateProductFilter("soloActivos", e.target.checked)}
                    />
                    Solo activos
                  </label>
                  <span className="text-xs text-slate-500">
                    {filteredOtros.length} resultados
                  </span>
                </div>

                <div className="max-h-[45vh] overflow-y-auto border border-slate-200 rounded-xl bg-white/70">
                  <table className="w-full">
                    <thead className="bg-slate-100/80 border-b border-slate-200 sticky top-0">
                      <tr>
                        <th className="px-3 py-2 text-left text-xs font-semibold text-slate-700">Codigo</th>
                        <th className="px-3 py-2 text-left text-xs font-semibold text-slate-700">Producto</th>
                        <th className="px-3 py-2 text-left text-xs font-semibold text-slate-700">Precio</th>
                        <th className="px-3 py-2 text-left text-xs font-semibold text-slate-700">Stock</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-200">
                      {filteredOtros.map((producto) => (
                        <tr
                          key={producto.id}
                          onDoubleClick={() => handleSelectOtro(producto)}
                          className="hover:bg-amber-50/60 cursor-pointer"
                        >
                          <td className="px-3 py-2 text-xs text-slate-700">{producto.id}</td>
                          <td className="px-3 py-2 text-xs text-slate-700">{producto.name}</td>
                          <td className="px-3 py-2 text-xs text-slate-700">${producto.price}</td>
                          <td className="px-3 py-2 text-xs text-slate-700">{producto.quantity}</td>
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
