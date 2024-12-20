import React from 'react';
import axios from 'axios';

export function TestButton() {
  const handleTestApi = async () => {
    const url = 'http://localhost:8080/catalouge'; // Update this to the actual API endpoint

    const requestBody = {
      genre1: 'jazz',
      genre2: 'indie',
    };

    try {
      const response = await axios.post(url, requestBody, {
        headers: {
          'Content-Type': 'application/json',
        },
      });

      console.log('API Response:', response.data);
      alert(`API Response: ${JSON.stringify(response.data, null, 2)}`);
    } catch (error) {
      console.error('Error making API request:', error.message);
      alert(`Error: ${error.message}`);
    }
  };

  return (
    <div className="flex justify-center items-center mt-4">
      <button
        onClick={handleTestApi}
        className="px-6 py-2 text-white bg-blue-500 rounded-lg hover:bg-blue-700 transition-all"
      >
        Test API (jazz n indie)
      </button>
    </div>
  );
}
