import { useState } from 'react';
import { MagnifyingGlassIcon } from '@heroicons/react/24/outline';

export function PlaylistList({ playlists, loading, error }) {
  const [searchTerm, setSearchTerm] = useState('');

  // Format duration from milliseconds to MM:SS format
  const formatDuration = (ms) => {
    const minutes = Math.floor(ms / 60000);
    const seconds = Math.floor((ms % 60000) / 1000).toString().padStart(2, '0');
    return `${minutes}:${seconds}`;
  };

  const formatGenres = (genres) => {
    return Object.entries(genres) // Convert the object into an array of [genre, count] pairs
      .sort((a, b) => b[1] - a[1]) // Sort by count in descending order
      .map(([genre]) => genre) // Extract the genre names only
      .join(', '); // Join the genres into a comma-separated string
  };


  {/**
    // Generate genre list from genres object
  const formatGenres = (genres) => {
    return Object.keys(genres)
    .map((genre) => `${genre} (${genres[genre]})`)
    .join(', ');
  };
    */}
  

    // Filter playlists based on the search term
    const filteredPlaylists = playlists.filter(
      (playlist) => playlist.name?.toLowerCase().includes(searchTerm.toLowerCase())
    );

  {/* 
  const filteredPlaylists = playlists.filter(playlist =>
    playlist.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
    playlist.artist.toLowerCase().includes(searchTerm.toLowerCase()) ||
    playlist.genre.toLowerCase().includes(searchTerm.toLowerCase())
  );
  **/}

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-500"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="text-center text-red-500 p-4">
        Error loading playlists: {error}
      </div>
    );
  }

  return (
    <div className="w-full">
      {/* Search bar */}
      <div className="mb-4 relative">
        <input
          type="text"
          placeholder="Search playlists..."
          className="w-full pl-10 pr-4 py-2 rounded-lg border dark:border-gray-600 dark:bg-gray-700 dark:text-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
        <MagnifyingGlassIcon className="absolute left-3 top-2.5 h-5 w-5 text-gray-400" />
      </div>

      {/* Playlist table */}
      <div className="bg-white dark:bg-gray-800 shadow-md rounded-lg overflow-hidden">
        <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
          <thead className="bg-gray-50 dark:bg-gray-700">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                Name
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                Followers
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                Tracks
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                Duration
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                Genres
              </th>
            </tr>
          </thead>
          <tbody className="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
            {filteredPlaylists.map((playlist) => (
              <tr key={playlist.pid}>
                {/* Playlist name */}
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900 dark:text-white">
                  {playlist.name}
                </td>
                {/* Followers */}
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-400">
                  {playlist.num_followers.toLocaleString()}
                </td>
                {/* Tracks */}
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-400">
                  {playlist.num_tracks}
                </td>
                {/* Duration */}
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-400">
                  {formatDuration(playlist.duration_ms)}
                </td>
                {/* Genres */}
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-400">
                  {formatGenres(playlist.genres)}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}