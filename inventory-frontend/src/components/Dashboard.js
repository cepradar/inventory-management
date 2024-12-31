// src/components/Dashboard.js
import React, { useEffect, useState } from 'react';
import axios from 'axios';

function Dashboard() {
  const [products, setProducts] = useState([]);

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const token = localStorage.getItem('authToken');
        const response = await axios.get('http://localhost:8080/api/products', {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setProducts(response.data);
      } catch (error) {
        console.error('Error al cargar los productos', error);
      }
    };

    fetchProducts();
  }, []);

  return (
    <div>
      <h2>Inventario</h2>
      <ul>
        {products.map((product) => (
          <li key={product.id}>{product.name} - {product.quantity}</li>
        ))}
      </ul>
    </div>
  );
}

export default Dashboard;
