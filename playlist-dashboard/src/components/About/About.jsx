import React from 'react';

export function About() {
  return (
    <div className="min-h-screen bg-gray-100 dark:bg-gray-900 text-gray-900 dark:text-white flex items-center justify-center px-4">
      <div className="max-w-3xl bg-white dark:bg-gray-800 shadow-md rounded-lg p-6 md:p-8">
        <h2 className="text-3xl font-bold text-center mb-6">About Playlist Dashboard</h2>
        <p className="text-lg text-gray-700 dark:text-gray-300 mb-6">
          Playlist Dashboard combines the power of big data and modern web technologies to analyze and explore music playlists effortlessly. Designed for music enthusiasts and professionals, it provides insights into playlists using advanced analytics and smart filters.
        </p>

        <h2 className="text-2xl font-semibold mb-4">Technologies Used</h2>
        <ul className="list-disc pl-6 text-gray-600 dark:text-gray-400">
          <li><strong>Spotify APIs:</strong> Fetching real-time playlist data.</li>
          <li><strong>Kafka & HDFS:</strong> Scalable data ingestion and storage.</li>
          <li><strong>SparkSQL:</strong> Querying and analyzing playlist data efficiently.</li>
          <li><strong>React:</strong> Dynamic and responsive user interface.</li>
          <li><strong>Machine Learning:</strong> Predictive models for recommendations.</li>
          <li><strong>Node.js:</strong> Backend services for API requests and data handling.</li>
        </ul>

        <p className="text-center mt-8 text-gray-600 dark:text-gray-400">
          Built with passion for music and data, Playlist Dashboard bridges the gap between technology and art.
        </p>
      </div>
    </div>
  );
}
