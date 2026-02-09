import React, { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import axios from './utils/axiosConfig';

function LandingPage() {
  const [companyInfo, setCompanyInfo] = useState(null);
  const [logoUrl, setLogoUrl] = useState('');
  const [status, setStatus] = useState('loading');

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

  const infoItems = useMemo(() => {
    if (!companyInfo) return [];
    const locationParts = [companyInfo.ciudad, companyInfo.departamento].filter(Boolean).join(', ');

    return [
      { label: 'NIT', value: companyInfo.nit },
      { label: 'Direccion', value: companyInfo.direccion },
      { label: 'Ciudad / Depto', value: locationParts },
      { label: 'Codigo postal', value: companyInfo.codigoPostal },
      { label: 'Telefono', value: companyInfo.telefono },
      { label: 'Correo', value: companyInfo.correo },
      { label: 'Sitio web', value: companyInfo.sitioWeb },
      { label: 'Representante', value: companyInfo.representanteLegal },
      { label: 'Numero regimen', value: companyInfo.numeroRegimen },
    ].filter((item) => item.value);
  }, [companyInfo]);

  const title = companyInfo?.razonSocial || 'Empresa';

  return (
    <div className="relative min-h-screen overflow-hidden bg-gradient-to-br from-amber-50 via-white to-slate-100 text-slate-900">
      <div className="absolute -top-32 right-[-12rem] h-72 w-72 rounded-full bg-amber-200/60 blur-3xl" />
      <div className="absolute bottom-[-14rem] left-[-10rem] h-80 w-80 rounded-full bg-sky-200/60 blur-3xl" />

      <header className="relative z-10 mx-auto flex w-full max-w-6xl items-center justify-between px-6 py-6 md:px-10">
        <div className="flex items-center gap-4">
          <div className="flex h-14 w-14 items-center justify-center rounded-2xl bg-white shadow-md">
            {logoUrl ? (
              <img src={logoUrl} alt={title} className="h-10 w-10 object-contain" />
            ) : (
              <span className="text-lg font-semibold text-slate-600">IM</span>
            )}
          </div>
          <div>
            <p className="text-xs uppercase tracking-[0.2em] text-slate-500">Sistema de inventario</p>
            <h1 className="text-xl font-semibold text-slate-800">{title}</h1>
          </div>
        </div>
        <Link
          to="/login"
          className="rounded-full bg-slate-900 px-5 py-2.5 text-sm font-semibold text-white shadow-lg transition hover:-translate-y-0.5 hover:bg-slate-800"
        >
          Iniciar sesion
        </Link>
      </header>

      <main className="relative z-10 mx-auto flex w-full max-w-6xl flex-col gap-10 px-6 pb-16 md:flex-row md:gap-12 md:px-10">
        <section className="flex-1 space-y-6">
          <div className="inline-flex items-center gap-2 rounded-full bg-slate-900/10 px-4 py-2 text-xs font-semibold uppercase tracking-[0.25em] text-slate-700">
            {status === 'loading' ? 'Cargando informacion' : 'Informacion corporativa'}
          </div>
          <h2 className="text-4xl font-semibold leading-tight text-slate-900 md:text-5xl">
            {title}
          </h2>
          <p className="max-w-xl text-lg text-slate-600">
            Gestiona inventario, ventas y servicios desde una plataforma unificada.
            {companyInfo?.direccion ? ` Operamos desde ${companyInfo.direccion}.` : ''}
          </p>
          <div className="flex flex-wrap gap-3">
            <Link
              to="/login"
              className="rounded-2xl bg-amber-500 px-6 py-3 text-sm font-semibold text-slate-900 shadow-lg transition hover:-translate-y-0.5 hover:bg-amber-400"
            >
              Acceder al sistema
            </Link>
            <a
              href={companyInfo?.sitioWeb || '#'}
              className="rounded-2xl border border-slate-300 px-6 py-3 text-sm font-semibold text-slate-700 transition hover:border-slate-400"
            >
              {companyInfo?.sitioWeb ? 'Visitar sitio web' : 'Conoce mas'}
            </a>
          </div>
        </section>

        <section className="w-full max-w-xl">
          <div className="rounded-3xl bg-white/90 p-6 shadow-xl shadow-slate-200/70 backdrop-blur md:p-8">
            <div className="flex items-start justify-between gap-6">
              <div>
                <p className="text-xs uppercase tracking-[0.3em] text-slate-400">Perfil</p>
                <h3 className="text-2xl font-semibold text-slate-900">Ficha empresarial</h3>
              </div>
              <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-slate-900 text-white">
                {companyInfo?.razonSocial ? companyInfo.razonSocial.charAt(0).toUpperCase() : 'E'}
              </div>
            </div>

            {status === 'error' ? (
              <p className="mt-6 text-sm text-rose-600">No se pudo cargar la informacion.</p>
            ) : (
              <div className="mt-6 grid gap-4 md:grid-cols-2">
                {infoItems.length === 0 ? (
                  <div className="rounded-2xl border border-dashed border-slate-200 p-4 text-sm text-slate-500">
                    Agrega informacion de la empresa desde el panel de configuracion.
                  </div>
                ) : (
                  infoItems.map((item) => (
                    <div key={item.label} className="rounded-2xl border border-slate-200/70 bg-slate-50/80 p-4">
                      <p className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">
                        {item.label}
                      </p>
                      <p className="mt-1 text-sm font-semibold text-slate-800">{item.value}</p>
                    </div>
                  ))
                )}
              </div>
            )}
          </div>
        </section>
      </main>

      <footer className="relative z-10 mx-auto w-full max-w-6xl px-6 pb-10 text-sm text-slate-500 md:px-10">
        <div className="flex flex-wrap items-center justify-between gap-4">
          <span>{title} · Gestion de inventarios y servicios</span>
          <span>&copy; {new Date().getFullYear()} {title}. Todos los derechos reservados.</span>
        </div>
      </footer>
    </div>
  );
}

export default LandingPage;
