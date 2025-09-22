// src/api/axiosConfig.js
import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 10000,
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
        if (error.response && (error.response.status === 401 || error.response.status === 403)) {
            console.error('[Axios] Token inválido o permiso denegado');
            localStorage.removeItem('authToken');
            localStorage.removeItem('userRole');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export default api;
