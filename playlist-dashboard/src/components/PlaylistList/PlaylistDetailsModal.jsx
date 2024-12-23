import React from 'react';

export const PlaylistDetailsModal = ({ playlist, onClose }) => {
  if (!playlist) return null; // Don't render if no playlist is selected

  const formatDuration = (ms) => {
    const minutes = Math.floor(ms / 60000);
    const seconds = Math.floor((ms % 60000) / 1000).toString().padStart(2, '0');
    return `${minutes}:${seconds}`;
  };
  console.log('Selected Playlist:', playlist);

  // Fallback to an empty array if tracks are missing
  const tracks = playlist.tracks || [];


  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
      <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-lg max-w-3xl w-full relative">
        <button
          className="absolute top-2 right-2 text-gray-500 hover:text-gray-700 dark:text-gray-300"
          onClick={onClose}
        >
          Close
        </button>
        <h2 className="text-2xl font-bold mb-4 dark:text-white">
          {playlist.name}
        </h2>
        <p className="mb-2 text-sm text-gray-500 dark:text-gray-400">
          Followers: {playlist.num_followers.toLocaleString()}
        </p>
        <p className="mb-2 text-sm text-gray-500 dark:text-gray-400">
          Tracks: {playlist.num_tracks}
        </p>
        <p className="mb-4 text-sm text-gray-500 dark:text-gray-400">
          Duration: {formatDuration(playlist.duration_ms)}
        </p>
        <h3 className="text-xl font-semibold mb-4 dark:text-white">
          Tracks
        </h3>
        <div className="overflow-y-auto max-h-60">
          <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
            <thead className="bg-gray-50 dark:bg-gray-700">
              <tr>
                <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">
                  Track Name
                </th>
                <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">
                  Artist
                </th>
                <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">
                  Duration
                </th>
              </tr>
            </thead>
            <tbody className="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
              {playlist.tracks.map((track, index) => (
                <tr key={index}>
                  <td className="px-4 py-2 text-sm text-gray-500 dark:text-gray-400">
                    {track[4] || 'Unknown Track Name'} {/* Track Name */}
                  </td>
                  <td className="px-4 py-2 text-sm text-gray-500 dark:text-gray-400">
                    {track[1] || 'Unknown Artist'} {/* Artist */}
                  </td>
                  <td className="px-4 py-2 text-sm text-gray-500 dark:text-gray-400">
                    {track[6] ? formatDuration(track[6]) : 'Unknown Duration'} {/* Duration */}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
