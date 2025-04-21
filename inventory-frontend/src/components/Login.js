import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import styles from '../styles/Login.module.css';

function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState('');
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(false);

  const handleLogin = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    if (!username || !password) {
      setMessage('Por favor, completa todos los campos.');
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
        setMessage(''); // Limpiar mensaje anterior antes de la redirección

        localStorage.setItem('authToken', data.token);

        const role = data.role;

        if (role === 'ADMIN') {
          navigate('/admin');
        } else if (role === 'USER') {
          navigate('/user');
        } else {
          navigate('/unknown-role');
        }
      } else {
        const errorData = await response.json();
        setMessage(errorData.message || 'Credenciales incorrectas');
      }
    } catch (error) {
      console.error('Error:', error);
      setMessage('Error en el servidor');
    } finally {
      setIsLoading(false);
      setUsername('');
      setPassword('');
    }
  };

  return (
    <div className={styles['login-container']}>
      <h1>Iniciar Sesión</h1>
      <form onSubmit={handleLogin} className={styles['login-form']}>
        <input
          type="text"
          placeholder="Usuario"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
        <input
          type="password"
          placeholder="Contraseña"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <button type="submit" disabled={isLoading}>
          {isLoading ? 'Cargando...' : 'Iniciar Sesión'}
        </button>
      </form>
      {message && (
        <p className={`${styles['login-message']} ${message.startsWith('Bienvenido') ? styles.success : styles.error}`}>
          {message}
        </p>
      )}
    </div>
  );
}

export default Login;