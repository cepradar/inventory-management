// src/api/axiosConfig.js
import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 30000, // 30 segundos para permitir carga de imágenes
});

// Interceptor de request para añadir token
api.interceptors.request.use(
    (config) => {
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
        console.error('[Axios] Error completo:', error);
        if (error.response) {
            console.error('[Axios] Status:', error.response.status);
            console.error('[Axios] Data:', error.response.data);
            console.error('[Axios] Headers:', error.response.headers);
        }
        
        if (error.response && (error.response.status === 401 || error.response.status === 403)) {
            console.error('[Axios] Token inválido o permiso denegado');
            const errorMsg = error.response.data?.message || 'Token inválido o permiso denegado';
            alert(`❌ ERROR DE AUTENTICACIÓN:\n\n${errorMsg}\n\nStatus: ${error.response.status}\n\nSerás redirigido al login.`);
            localStorage.removeItem('authToken');
            localStorage.removeItem('userRole');
            setTimeout(() => {
                window.location.href = '/login';
            }, 1000);
        } else if (error.response) {
            console.error(`[Axios] Error ${error.response.status}:`, error.response.data);
        }
        return Promise.reject(error);
    }
);

export default api;
