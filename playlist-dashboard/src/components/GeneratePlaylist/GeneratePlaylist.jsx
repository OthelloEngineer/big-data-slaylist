import React, { useState } from 'react';
import { ArrowPathRoundedSquareIcon } from '@heroicons/react/24/outline'; // Heroicons refresh icon
import { GeneratedPlaylist } from "./GeneratedPlaylist";

export function GeneratePlaylist({ disabled, genre1, genre2 }) {
  const [playlistName, setPlaylistName] = useState('New Playlist');
  const [playlist, setPlaylist] = useState([]);

  // Function to generate the playlist name
  const generatePlaylistName = (genre1, genre2) => {
    if (!genre1 || !genre2) return 'New Playlist'; // Default name if genres aren't selected
    const names = [
      `${genre1.name} Meets ${genre2.name}`,
      `Doctors HATE this ${genre1.name} x ${genre2.name} Playlist`,
      `${genre1.name} & ${genre2.name} Mix`,
      `The ${genre1.name} x ${genre2.name} Experience`,
      `A Blend of ${genre1.name} and ${genre2.name}`,
      `Sonic Fusion of ${genre1.name} + ${genre2.name}`,
      `Two Worlds, One Mix: ${genre1.name} & ${genre2.name}`,
      `${genre1.name} and ${genre2.name} Essentials`,
      `Mashup Madness: ${genre1.name} x ${genre2.name}`,
      `${genre1.name} x ${genre2.name} but it’s giving ✨vibes✨`,
      `POV: You’re Ascending to ${genre1.name} + ${genre2.name}`,
      `When ${genre1.name} and ${genre2.name} Cure Existential Dread`,
    ];
    return names[Math.floor(Math.random() * names.length)]; // Randomize name
  };

  // Handle playlist generation
  const handleGeneratePlaylist = () => {
    if (genre1 && genre2) {
      setPlaylistName(generatePlaylistName(genre1, genre2));
      setPlaylist(generateMockPlaylist());
      console.log("Generated Playlist:", playlistName);
    }
  };

    // Function to generate a mock playlist
  const generateMockPlaylist = () => {
    if (!genre1 || !genre2) return [];
    const mockSongs = [
      { id: 1, name: "Song 1", artist: "Artist A", genre: genre1.name, streams: 123456, releaseDate: "2023-01-01" },
      { id: 2, name: "Song 2", artist: "Artist B", genre: genre2.name, streams: 654321, releaseDate: "2022-12-15" },
      { id: 3, name: "Song 3", artist: "Artist C", genre: genre1.name, streams: 234567, releaseDate: "2023-02-10" },
      { id: 4, name: "Song 4", artist: "Artist D", genre: genre2.name, streams: 345678, releaseDate: "2023-03-20" },
      { id: 5, name: "Song 5", artist: "Artist E", genre: genre1.name, streams: 567890, releaseDate: "2023-04-05" },
    ];
    return mockSongs;
  };

  // Handle removing a song from the playlist
  const handleRemoveSong = (songId) => {
    setPlaylist((prevPlaylist) => prevPlaylist.filter((song) => song.id !== songId));
  };


  return (
    <div className="mt-6 bg-gray-100 dark:bg-gray-900 flex items-center justify-center">
      <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-md flex flex-col items-center">
        {/* Display the Playlist Name */}
        <div className="flex items-center gap-2 mb-4">
          <h2 className="text-2xl font-bold text-gray-900 dark:text-white">
            {playlistName}
          </h2>
        </div>

        {/* Generate Playlist Button */}
        <button
          onClick={handleGeneratePlaylist}
          className={`px-4 py-2 rounded-lg text-center font-medium transition-colors duration-200 ${
            disabled
              ? 'bg-gray-400 cursor-not-allowed text-gray-700'
              : 'bg-blue-500 hover:bg-blue-700 text-white'
          }`}
          disabled={disabled}
        >
          Generate Playlist
        </button>
        
        {/* Generated Playlist Table */}
        {playlist.length > 0 && <GeneratedPlaylist playlist={playlist} onRemove={handleRemoveSong} />}
      </div>
    </div>
  );
}
