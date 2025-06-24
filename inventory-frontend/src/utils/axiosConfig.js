// src/api/axiosConfig.js
import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080', // Tu URL base del backend
    timeout: 10000, // Opcional: tiempo de espera para las solicitudes (ej. 10 segundos)
});

// Interceptor para añadir el token JWT a cada solicitud
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('authToken');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Opcional: Interceptor para manejar respuestas de error globales (ej. token expirado/inválido)
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response && (error.response.status === 401 || error.response.status === 403)) {
            // Si el token expira o es inválido, o no tienes permisos, redirige al login
            console.error('Token inválido/expirado o permiso denegado. Redirigiendo al login.');
            localStorage.removeItem('authToken'); // Limpiar token inválido
            localStorage.removeItem('userName'); // Limpiar nombre de usuario
            // Puedes usar navigate('/login') si estás seguro de que este código se ejecuta en un contexto de React Router
            // Para una redirección más segura y fuera del contexto de React:
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export default api;