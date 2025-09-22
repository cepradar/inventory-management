import React from 'react';

function Modal({ message, onConfirm, onCancel }) {
  return (
    // Fondo oscuro semi-transparente
    <div className="fixed inset-0 bg-gray-600 bg-opacity-50 flex items-center justify-center z-50">
      {/* Contenido del modal */}
      <div className="bg-white p-8 rounded-lg shadow-xl max-w-sm w-full transform transition-transform duration-300 scale-100">
        <h3 className="text-xl font-bold mb-4 text-center text-gray-800">Confirmación</h3>
        <p className="text-gray-600 mb-6 text-center">{message}</p>
        <div className="flex justify-center space-x-4">
          <button 
            onClick={onConfirm} 
            className="bg-red-500 text-white font-semibold py-2 px-6 rounded-lg hover:bg-red-600 transition-colors duration-300"
          >
            Confirmar
          </button>
          <button 
            onClick={onCancel} 
            className="bg-gray-300 text-gray-800 font-semibold py-2 px-6 rounded-lg hover:bg-gray-400 transition-colors duration-300"
          >
            Cancelar
          </button>
        </div>
      </div>
    </div>
  );
}

export default Modal;