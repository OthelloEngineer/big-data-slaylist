import {React, useState} from 'react';

export const PlaylistDetailsModal = ({ playlist, onClose, selectedGenres = [] }) => {

  const [showAllGenres, setShowAllGenres] = useState(false); // State to toggle genres
  if (!playlist) return null; // Don't render if no playlist is selected

  const formatDuration = (ms) => {
    const minutes = Math.floor((ms % 3600000) / 60000);
    const seconds = Math.floor((ms % 60000) / 1000).toString().padStart(2, '0');
    return `${minutes.toString().padStart(2, '0')}:${seconds}`;
  };
  console.log('Selected Playlist:', playlist);

    // Format duration as hh:mm:ss for the playlist
    const formatDurationHHMMSS = (ms) => {
        const hours = Math.floor(ms / 3600000);
        const minutes = Math.floor((ms % 3600000) / 60000);
        const seconds = Math.floor((ms % 60000) / 1000).toString().padStart(2, '0');
        return `${hours}:${minutes.toString().padStart(2, '0')}:${seconds}`;
        };

    // Convert UNIX timestamp to a readable date
  const formatModifiedDate = (timestamp) => {
    if (!timestamp) return 'Unknown Date'; // Handle missing timestamp
    const date = new Date(timestamp * 1000); // Convert seconds to milliseconds
    return isNaN(date.getTime()) ? 'Invalid Date' : date.toLocaleDateString(); // Validate and format
  };

  console.log('Modified At:', playlist.modified_at); // Debug modified_at value

  // Fallback to an empty array if tracks are missing
  const tracks = playlist.tracks || [];

  // Calculate relevance score
  const calculateRelevanceScore = (genreCounts = {}) => {
    if (!selectedGenres.length) return 0;
  
    const totalMatches = selectedGenres.reduce((total, selectedGenre) => {
      const genreName = selectedGenre?.name?.toLowerCase();
  
      const genreRegex = new RegExp(`\\b${genreName}\\b`, 'i'); // Match whole words, case-insensitive
  
      // Sum up matches where raw genre matches the regex
      const genreMatchCount = Object.entries(genreCounts).reduce((matchSum, [rawGenre, count]) => {
        if (genreRegex.test(rawGenre)) {
          return matchSum + count;
        }
        return matchSum;
      }, 0);
  
      return total + genreMatchCount;
    }, 0);
  
    const totalGenreCount = Object.values(genreCounts).reduce((sum, count) => sum + count, 0);
    return totalGenreCount > 0 ? (totalMatches / totalGenreCount) * 100 : 0;
  };
  
  const genreCounts = playlist.genre_counts || {}; // Fallback to empty object if missing
  const relevanceScore = calculateRelevanceScore(genreCounts);
  
  const matchingGenres = selectedGenres
    .filter((genre) => {
      const genreRegex = new RegExp(`\\b${genre?.name?.toLowerCase()}\\b`, 'i');
      return Object.keys(genreCounts).some((rawGenre) => genreRegex.test(rawGenre));
    })
    .map(
      (genre) =>
        `${genre.name} (${Object.entries(genreCounts)
          .filter(([rawGenre]) => new RegExp(`\\b${genre.name.toLowerCase()}\\b`, 'i').test(rawGenre))
          .reduce((sum, [, count]) => sum + count, 0)})`
    )
    .join(', ');
  
    const sortedGenres = Object.entries(genreCounts).sort(([, countA], [, countB]) => countB - countA);

    // Show top 10 or all genres based on the toggle state
    const displayedGenres = showAllGenres
      ? sortedGenres
      : sortedGenres.slice(0, 3);
  
    const rawGenres = displayedGenres
      .map(([genre, count]) => `${genre} (${count})`)
      .join(', ');

    // Handle outside click to close modal
  const handleOutsideClick = (e) => {
    if (e.target.id === 'modal-overlay') {
      onClose();
    }
  };


  return (
    <div
    id="modal-overlay"
        className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50"
        onClick={handleOutsideClick}>
    
      <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-lg max-w-3xl w-full relative"
      onClick={(e) => e.stopPropagation()}
      >
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
          Duration: {formatDurationHHMMSS(playlist.duration_ms)}
        </p>
        <p className="mb-4 text-sm text-gray-500 dark:text-gray-400">
          Modified At: {formatModifiedDate(playlist.modified_at)}
        </p>
        <h3 className="text-xl font-semibold mb-4 dark:text-white">
          Tracks
        </h3>

        <h3 className="text-lg font-semibold mb-2 dark:text-white">Relevance</h3>
        <p className="text-sm text-gray-500 dark:text-gray-400 mb-2">
          Relevance Score: {relevanceScore.toFixed(1)}%
        </p>
        <p className="text-sm text-gray-500 dark:text-gray-400">
          Matching Genres: {matchingGenres || 'No matching genres'}
        </p>
        <h3
          className="text-lg font-semibold mt-4 dark:text-indigo-400 cursor-pointer hover:text-indigo-600"
          onClick={() => setShowAllGenres((prev) => !prev)}
        >
          {showAllGenres ? 'Hide Genres' : 'Show All Genres'}
        </h3>
        <p className="text-sm text-gray-500 dark:text-gray-400">{rawGenres || 'No genres available'}</p>

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
