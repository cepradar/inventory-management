// src/components/Login.js
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import styles from '../styles/Login.module.css';

function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState('');
  const [loginStatus, setLoginStatus] = useState(null); // 'success', 'error', or null
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(false);

  const handleLogin = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setMessage(''); // Clear previous message
    setLoginStatus(null); // Reset login status

    if (!username || !password) {
      setMessage('Por favor, completa todos los campos.');
      setLoginStatus('error');
      setIsLoading(false);
      return;
    }

    try {
      const response = await fetch('http://localhost:8080/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, password }),
      });

      if (response.ok) {
        const data = await response.json();
        setMessage(`Bienvenido, ${username}`);
        setLoginStatus('success');

        localStorage.setItem('authToken', data.token); // Usar 'authToken' consistentemente
        localStorage.setItem('userRole', data.role); // ¡IMPORTANTE: Guarda el rol!
        localStorage.setItem('userName', username); // Guarda el nombre de usuario

        const role = data.role;

        if (role === 'ADMIN') {
          navigate('/dashboard'); // Redirige al Dashboard
        } else if (role === 'USER') {
          navigate('/user'); // Si tienes una ruta para usuarios regulares
        } else {
          // Si el rol no es ni ADMIN ni USER, puedes redirigir a una página genérica o de error
          navigate('/unauthorized');
        }
      } else {
        const errorData = await response.json();
        setMessage(errorData.message || 'Credenciales incorrectas');
        setLoginStatus('error');
      }
    } catch (error) {
      console.error('Error:', error);
      setMessage('Error en el servidor. Inténtalo de nuevo más tarde.');
      setLoginStatus('error');
    } finally {
      setIsLoading(false);
      // Opcional: limpiar campos solo si el login fue exitoso, de lo contrario dejarlos para que el usuario corrija
      if (loginStatus === 'success') {
          setUsername('');
          setPassword('');
      }
    }
  };

  return (
    <div className={styles['login-container']}>
      <h1>Iniciar Sesión</h1>
      <form onSubmit={handleLogin} className={styles['login-form']}>
        <input
          type="text"
          id="username"
          name="username"
          placeholder="Usuario"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          autoComplete="username"
          required
        />
        <input
          type="password"
          id="password"
          name="password"
          placeholder="Contraseña"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          autoComplete="current-password"
          required
        />
        <button type="submit" disabled={isLoading}>
          {isLoading ? 'Cargando...' : 'Iniciar Sesión'}
        </button>
      </form>
      {message && (
        <p className={`${styles['login-message']} ${loginStatus === 'success' ? styles.success : styles.error}`}>
          {message}
        </p>
      )}
    </div>
  );
}

export default Login;