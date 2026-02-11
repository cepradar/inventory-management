import React, { useEffect, useMemo, useRef, useState } from 'react';
import { Link } from 'react-router-dom';
import axios from './utils/axiosConfig';

function LandingPage() {
  const [companyInfo, setCompanyInfo] = useState(null);
  const [logoUrl, setLogoUrl] = useState('');
  const [status, setStatus] = useState('loading');
  const [activeSlide, setActiveSlide] = useState(0);
  const [dragOffset, setDragOffset] = useState(0);
  const [isDragging, setIsDragging] = useState(false);
  const [isPaused, setIsPaused] = useState(false);
  const [showMascot, setShowMascot] = useState(false);
  const dragStartX = useRef(0);
  const mascotTimeoutRef = useRef(null);

  useEffect(() => {
    let isMounted = true;
    let objectUrl = null;

    const fetchCompanyInfo = async () => {
      try {
        const infoResponse = await axios.get('/api/company/info');
        if (!isMounted) return;
        setCompanyInfo(infoResponse.data || null);

        if (infoResponse.data?.id) {
          try {
            const logo2Response = await axios.get(`/api/company/${infoResponse.data.id}/logo2`, {
              responseType: 'blob',
            });
            if (!isMounted) return;
            objectUrl = URL.createObjectURL(logo2Response.data);
            setLogoUrl(objectUrl);
          } catch (error) {
            try {
              const logoResponse = await axios.get(`/api/company/${infoResponse.data.id}/logo`, {
                responseType: 'blob',
              });
              if (!isMounted) return;
              objectUrl = URL.createObjectURL(logoResponse.data);
              setLogoUrl(objectUrl);
            } catch (logoError) {
              setLogoUrl('');
            }
          }
        }

        setStatus('ready');
      } catch (error) {
        console.warn('No se pudo cargar la informacion de la empresa:', error);
        if (isMounted) {
          setStatus('error');
        }
      }
    };

    fetchCompanyInfo();

    return () => {
      isMounted = false;
      if (objectUrl) {
        URL.revokeObjectURL(objectUrl);
      }
    };
  }, []);

  const heroTitle = 'Servicio tecnico especializado';
  const heroSubtitle = 'Electrodomesticos de gama blanca';
  const title = companyInfo?.razonSocial || 'Empresa';
  const phoneDigits = companyInfo?.telefono ? companyInfo.telefono.replace(/[^0-9]/g, '') : '';
  const whatsappLink = phoneDigits ? `https://wa.me/${phoneDigits}` : '';

  const infoItems = useMemo(() => {
    if (!companyInfo) return [];
    const locationParts = [companyInfo.ciudad, companyInfo.departamento].filter(Boolean).join(', ');

    return [
      { label: 'NIT', value: companyInfo.nit },
      { label: 'Direccion', value: companyInfo.direccion },
      { label: 'Ciudad / Depto', value: locationParts },
      { label: 'Telefono', value: companyInfo.telefono },
      { label: 'Correo', value: companyInfo.correo },
      { label: 'Sitio web', value: companyInfo.sitioWeb },
      { label: 'Representante', value: companyInfo.representanteLegal },
    ].filter((item) => item.value);
  }, [companyInfo]);

  const categories = [
    { name: 'Lavadoras', tag: 'Mantenimiento total', tone: 'from-slate-100 via-white to-slate-200' },
    { name: 'Hornos', tag: 'Reparacion rapida', tone: 'from-amber-100 via-white to-amber-200' },
    { name: 'Lavavajillas', tag: 'Limpieza interna', tone: 'from-emerald-100 via-white to-emerald-200' },
    { name: 'Frigorificos', tag: 'Frio estable', tone: 'from-sky-100 via-white to-sky-200' },
  ];

  const serviceHighlights = [
    'Diagnostico en sitio con tecnicos certificados',
    'Repuestos originales y garantia escrita',
    'Atencion prioritaria para clientes corporativos',
  ];

  const totalSlides = 4;
  const goToSlide = (index) => {
    const nextIndex = (index + totalSlides) % totalSlides;
    setActiveSlide(nextIndex);
  };

  useEffect(() => {
    if (isPaused || isDragging) return;
    const intervalId = setInterval(() => {
      setActiveSlide((prev) => (prev + 1) % totalSlides);
    }, 5000);

    return () => clearInterval(intervalId);
  }, [isPaused, isDragging, totalSlides]);

  useEffect(() => {
    if (!whatsappLink) return;

    const triggerMascot = () => {
      setShowMascot(true);
      if (mascotTimeoutRef.current) {
        clearTimeout(mascotTimeoutRef.current);
      }
      mascotTimeoutRef.current = setTimeout(() => {
        setShowMascot(false);
      }, 2400);
    };

    triggerMascot();
    const intervalId = setInterval(triggerMascot, 5000);

    return () => {
      clearInterval(intervalId);
      if (mascotTimeoutRef.current) {
        clearTimeout(mascotTimeoutRef.current);
      }
    };
  }, [whatsappLink]);

  const handleDragStart = (clientX) => {
    dragStartX.current = clientX;
    setDragOffset(0);
    setIsDragging(true);
  };

  const handleDragMove = (clientX) => {
    if (!isDragging) return;
    const delta = clientX - dragStartX.current;
    setDragOffset(delta);
  };

  const handleDragEnd = () => {
    if (!isDragging) return;
    if (dragOffset > 80) {
      goToSlide(activeSlide - 1);
    } else if (dragOffset < -80) {
      goToSlide(activeSlide + 1);
    }
    setDragOffset(0);
    setIsDragging(false);
  };

  return (
    <div className="relative h-screen overflow-hidden bg-slate-50 text-slate-900">
      <div className="pointer-events-none absolute inset-0">
        <div className="float-slow absolute -top-24 right-10 h-56 w-56 rounded-full bg-amber-200/60 blur-3xl" />
        <div className="float-medium absolute bottom-0 left-[-6rem] h-72 w-72 rounded-full bg-sky-200/60 blur-3xl" />
        <div className="float-fast absolute bottom-10 right-[-5rem] h-64 w-64 rounded-full bg-emerald-200/50 blur-3xl" />
      </div>
      <div className="relative h-full bg-[radial-gradient(circle_at_top,_#f8fafc_0%,_#eef2f7_55%,_#e2e8f0_100%)]">
        <header className="mx-auto flex w-full max-w-6xl items-center justify-between px-6 py-6 md:px-10">
          <div className="flex items-center gap-4">
            <div className="flex h-14 w-14 items-center justify-center overflow-hidden rounded-2xl bg-white shadow-lg">
              <img src="/sp-logo.png" alt={title} className="h-full w-full object-contain" />
            </div>
            <div>
              <h1 className="text-xl font-semibold text-slate-900">{title}</h1>
            </div>
          </div>
          <div className="flex items-center gap-3">
            <Link
              to="/login"
              className="rounded-full border border-slate-300 px-5 py-2.5 text-sm font-semibold text-slate-700 transition hover:border-slate-400"
            >
              Iniciar sesion
            </Link>
            <Link
              to="/login"
              className="rounded-full bg-slate-900 px-5 py-2.5 text-sm font-semibold text-white shadow-lg transition hover:-translate-y-0.5 hover:bg-slate-800"
            >
              Crear cuenta
            </Link>
          </div>
        </header>
        <section className="relative mx-auto flex h-[calc(100%-96px)] w-full max-w-6xl flex-col px-6 pb-8 md:px-10">
          <div className="relative flex-1 overflow-hidden rounded-[2.5rem] border border-slate-200 bg-white/70 shadow-xl shadow-slate-200/60 backdrop-blur">
            <div
              className="absolute inset-0"
              style={{
                backgroundImage:
                  'radial-gradient(circle at 20% 20%, rgba(251,191,36,0.25), transparent 55%), radial-gradient(circle at 85% 15%, rgba(56,189,248,0.25), transparent 55%)',
              }}
            />

            <div
              className={`flex h-full select-none ${
                isDragging ? 'cursor-grabbing transition-none' : 'cursor-grab transition-transform duration-500 ease-out'
              }`}
              style={{ transform: `translateX(calc(-${activeSlide * 100}% + ${dragOffset}px))` }}
              onMouseDown={(event) => handleDragStart(event.clientX)}
              onMouseMove={(event) => handleDragMove(event.clientX)}
              onMouseUp={handleDragEnd}
              onMouseLeave={() => { handleDragEnd(); setIsPaused(false); }}
              onTouchStart={(event) => handleDragStart(event.touches[0].clientX)}
              onTouchMove={(event) => handleDragMove(event.touches[0].clientX)}
              onTouchEnd={handleDragEnd}
              onMouseEnter={() => setIsPaused(true)}
              onTouchStartCapture={() => setIsPaused(true)}
              onTouchEndCapture={() => setIsPaused(false)}
            >
              <div className="flex h-full w-full flex-shrink-0 flex-col justify-between px-8 py-8 md:px-12">
                <div className="space-y-4">
                  <h2 className="text-3xl font-semibold leading-tight text-slate-900 md:text-4xl">
                    {heroTitle.toUpperCase()}
                  </h2>
                  <p className="text-sm font-semibold uppercase tracking-[0.35em] text-slate-500">
                    {heroSubtitle}
                  </p>
                  <div className="flex flex-wrap items-center gap-4">
                    <Link
                      to="/login"
                      className="rounded-full border border-slate-300 px-5 py-2.5 text-sm font-semibold text-slate-700 transition hover:border-slate-400"
                    >
                      Crear cuenta
                    </Link>
                  </div>
                </div>
                <div className="grid gap-5 md:grid-cols-2">
                  <div className="rounded-3xl border border-slate-200 bg-white/80 p-4 text-sm text-slate-700">
                    <p className="text-xs uppercase tracking-[0.3em] text-slate-400">Atendemos</p>
                    <p className="mt-2 text-base font-semibold text-slate-900">Hogares, comercios y contratos corporativos</p>
                    <p className="mt-2 text-xs text-slate-600">
                      {companyInfo?.direccion ? `Base operativa: ${companyInfo.direccion}` : 'Atencion inmediata en tu zona.'}
                    </p>
                  </div>
                  <div className="rounded-3xl border border-slate-200 bg-white/80 p-4 text-sm text-slate-700">
                    <p className="text-xs uppercase tracking-[0.3em] text-slate-400">Respuesta rapida</p>
                    <p className="mt-2 text-base font-semibold text-slate-900">Citas priorizadas y seguimiento digital</p>
                  </div>
                </div>
              </div>

              <div className="flex h-full w-full flex-shrink-0 flex-col justify-between px-8 py-8 md:px-12">
                <div>
                  <p className="text-xs uppercase tracking-[0.4em] text-slate-400">Categorias</p>
                  <h3 className="mt-3 text-3xl font-semibold text-slate-900">Equipos que cubrimos</h3>
                  <p className="mt-2 text-sm text-slate-600">
                    Tecnicos especializados por linea y disponibilidad inmediata.
                  </p>
                </div>
                <div className="grid gap-3 md:grid-cols-4">
                  {categories.map((category) => (
                    <div
                      key={category.name}
                      className={`group rounded-3xl border border-slate-200 bg-gradient-to-br ${category.tone} p-4 shadow-sm transition hover:-translate-y-1 hover:shadow-lg`}
                    >
                      <div className="flex h-11 w-11 items-center justify-center rounded-2xl bg-white/80 text-base font-semibold text-slate-700">
                        {category.name.charAt(0)}
                      </div>
                      <h4 className="mt-3 text-base font-semibold text-slate-900">{category.name}</h4>
                      <p className="mt-1 text-xs text-slate-600">{category.tag}</p>
                      <div className="mt-3 text-[10px] font-semibold uppercase tracking-[0.25em] text-slate-500">
                        Ver detalle
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              <div className="flex h-full w-full flex-shrink-0 flex-col gap-3 px-6 py-5 md:px-8">
                <div className="flex flex-1 flex-col gap-3 md:flex-row">
                  <div className="flex h-full flex-1 flex-col justify-between rounded-[2.5rem] bg-white p-4 shadow-xl shadow-slate-200/60">
                    <div>
                      <p className="text-xs uppercase tracking-[0.35em] text-slate-400">Servicios</p>
                      <h3 className="mt-2 text-base font-semibold text-slate-900">Especialistas en linea blanca</h3>
                      <p className="mt-2 text-[10px] text-slate-600">
                        Nuestro equipo combina diagnostico tecnico, repuestos garantizados y seguimiento en tiempo real.
                      </p>
                    </div>
                    <div className="mt-2 space-y-1.5">
                      {serviceHighlights.map((item) => (
                        <div key={item} className="flex items-start gap-3">
                          <span className="mt-1 h-2 w-2 rounded-full bg-emerald-400" />
                          <p className="text-[10px] font-medium text-slate-700">{item}</p>
                        </div>
                      ))}
                    </div>
                    <div className="mt-3 flex flex-wrap gap-2">
                      <Link
                        to="/login"
                        className="rounded-full bg-emerald-500 px-4 py-2 text-[10px] font-semibold text-white shadow-lg transition hover:-translate-y-0.5 hover:bg-emerald-400"
                      >
                        Solicitar atencion
                      </Link>
                      <a
                        href={companyInfo?.sitioWeb || '#'}
                        className="rounded-full border border-slate-300 px-4 py-2 text-[10px] font-semibold text-slate-700 transition hover:border-slate-400"
                      >
                        {companyInfo?.sitioWeb ? 'Ver cobertura' : 'Cobertura nacional'}
                      </a>
                    </div>
                  </div>

                  <div className="flex h-full w-full max-w-xs flex-col justify-between rounded-[2.5rem] bg-slate-900 p-4 text-white shadow-xl shadow-slate-200/60">
                    <div>
                      <p className="text-xs uppercase tracking-[0.35em] text-slate-400">Contacto rapido</p>
                      <h3 className="mt-2 text-base font-semibold">Solicita presupuesto</h3>
                      <p className="mt-2 text-[10px] text-slate-300">
                        Completa el formulario y te llamamos en minutos.
                      </p>
                    </div>
                    <form className="mt-2 space-y-1.5">
                      <input
                        type="text"
                        placeholder="Nombre completo"
                        className="w-full rounded-2xl border border-white/10 bg-white/10 px-3 py-2 text-[10px] text-white placeholder:text-slate-400 focus:border-white/30 focus:outline-none"
                      />
                      <input
                        type="text"
                        placeholder="Telefono"
                        className="w-full rounded-2xl border border-white/10 bg-white/10 px-3 py-2 text-[10px] text-white placeholder:text-slate-400 focus:border-white/30 focus:outline-none"
                      />
                      <input
                        type="text"
                        placeholder="Tipo de equipo"
                        className="w-full rounded-2xl border border-white/10 bg-white/10 px-3 py-2 text-[10px] text-white placeholder:text-slate-400 focus:border-white/30 focus:outline-none"
                      />
                      <button
                        type="button"
                        className="w-full rounded-2xl bg-amber-400 px-4 py-2 text-[10px] font-semibold text-slate-900 transition hover:bg-amber-300"
                      >
                        Enviar solicitud
                      </button>
                    </form>
                    <div className="mt-2 text-[9px] text-slate-400">
                      {companyInfo?.correo ? `Correo: ${companyInfo.correo}` : 'Responderemos el mismo dia.'}
                    </div>
                  </div>
                </div>
              </div>

              <div className="flex h-full w-full flex-shrink-0 flex-col justify-between px-6 py-6 md:px-9">
                <div className="rounded-[2.5rem] border border-slate-200 bg-white p-5 shadow-lg shadow-slate-200/60">
                  <div className="flex flex-wrap items-center justify-between gap-4">
                    <div>
                      <p className="text-xs uppercase tracking-[0.35em] text-slate-400">Datos corporativos</p>
                      <h3 className="mt-2 text-lg font-semibold text-slate-900">{title}</h3>
                    </div>
                    <div className="rounded-full bg-slate-900 px-4 py-2 text-[10px] font-semibold uppercase tracking-[0.3em] text-white">
                      {status === 'loading' ? 'Cargando' : 'Disponible'}
                    </div>
                  </div>
                  {status === 'error' ? (
                    <p className="mt-6 text-sm text-rose-600">No se pudo cargar la informacion.</p>
                  ) : (
                    <div className="mt-3 grid gap-3 md:grid-cols-3">
                      {infoItems.length === 0 ? (
                        <div className="rounded-2xl border border-dashed border-slate-200 p-4 text-sm text-slate-500">
                          Agrega informacion de la empresa desde el panel de configuracion.
                        </div>
                      ) : (
                        infoItems.map((item) => (
                          <div key={item.label} className="rounded-2xl border border-slate-200/70 bg-slate-50/80 p-3">
                            <p className="text-[9px] font-semibold uppercase tracking-[0.2em] text-slate-400">
                              {item.label}
                            </p>
                            <p className="mt-1 text-[11px] font-semibold text-slate-800">{item.value}</p>
                          </div>
                        ))
                      )}
                    </div>
                  )}
                </div>
                <div className="mt-3 flex flex-wrap items-center justify-between gap-4 text-[11px] text-slate-500">
                  <span>{title} · Servicio tecnico especializado</span>
                  <span>&copy; {new Date().getFullYear()} {title}. Todos los derechos reservados.</span>
                </div>
              </div>
            </div>
          </div>

          <div className="mt-6 flex items-center justify-between">
            <div className="flex items-center gap-2">
              {Array.from({ length: totalSlides }).map((_, index) => (
                <button
                  key={`dot-${index}`}
                  type="button"
                  onClick={() => goToSlide(index)}
                  className={`h-2.5 w-2.5 rounded-full transition ${
                    activeSlide === index ? 'bg-slate-900' : 'bg-slate-300'
                  }`}
                  aria-label={`Ir a slide ${index + 1}`}
                />
              ))}
            </div>
            <div className="flex items-center justify-end">
              <div className="flex items-center justify-center rounded-full bg-white/90 px-4 py-2 shadow-sm">
                <img
                  src="/cepr-logo.png"
                  alt="CEPR"
                  className="h-9 w-auto opacity-100"
                />
              </div>
            </div>
          </div>
        </section>
      </div>

      {whatsappLink && (
        <div className="fixed bottom-16 right-6 z-50 flex items-end gap-3">
          <div className="relative">
            <img
              src="/washo.png"
              alt="Washo"
              className={`washo-idle h-20 w-auto select-none ${showMascot ? 'washo-pop' : ''}`}
            />
            {showMascot && (
              <div className="mascot-bubble mascot-bubble--left">
                <div>
                  <p className="text-xs font-semibold text-slate-900">Escribenos por WhatsApp</p>
                  <p className="text-[11px] text-slate-500">Respondemos en minutos</p>
                </div>
              </div>
            )}
          </div>
          <a
            href={whatsappLink}
            className="whatsapp-pulse flex h-14 w-14 items-center justify-center rounded-full bg-emerald-500 text-white shadow-2xl transition hover:-translate-y-1"
            aria-label="WhatsApp"
          >
            WA
          </a>
        </div>
      )}
    </div>
  );
}

export default LandingPage;
