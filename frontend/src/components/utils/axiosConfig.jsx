// src/api/axiosConfig.js
import axios from 'axios';

//-------------------------
const API_BASE =
  import.meta.env.VITE_API_BASE_URL ||
  `${window.location.protocol}//${window.location.hostname}:8080`;

const api = axios.create({
    baseURL: API_BASE,
    timeout: 30000, // 30 segundos para permitir carga de imágenes
});

// Interceptor de request para añadir token
api.interceptors.request.use(
    (config) => {
        const url = config.url || '';
        const isAuthEndpoint = url.includes('/auth/login') || url.includes('/auth/register');
        if (isAuthEndpoint) {
            config.silent = true;
            return config;
        }
        const token = localStorage.getItem('authToken');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
            console.log('[Axios] Token añadido al header:', config.headers['Authorization']);
        } else {
            console.warn('[Axios] No hay token en localStorage');
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Interceptor de response para manejar 401/403
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.config && error.config.silent) {
            return Promise.reject(error);
        }

        const status = error.response?.status;

        if (status === 401) {
            // Sesión expirada o token inválido: limpiar credenciales y redirigir de inmediato.
            // La redirección es síncrona para evitar que un re-render de React active
            // ProtectedRoute antes de que la navegación se complete.
            console.warn('[Axios] Sesión expirada (401). Redirigiendo a /login.');
            localStorage.removeItem('authToken');
            localStorage.removeItem('userRole');
            localStorage.removeItem('username');
            window.location.replace('/login');
        } else if (status === 403) {
            // Permisos insuficientes: NO cerrar sesión. Solo rechazar con mensaje claro.
            console.error('[Axios] Acceso denegado (403):', error.config?.url);
            const serverMsg = error.response?.data?.error || 'No tienes permisos para realizar esta acción.';
            const enhancedError = new Error(serverMsg);
            enhancedError.status = 403;
            enhancedError.originalError = error;
            return Promise.reject(enhancedError);
        } else {
            console.error(`[Axios] Error ${status ?? 'desconocido'}:`, error.response?.data ?? error.message);
        }

        return Promise.reject(error);
    }
);

export default api;
