import React, { useState } from 'react';
import { ArrowPathRoundedSquareIcon } from '@heroicons/react/24/outline'; // Heroicons refresh icon
import { GeneratedPlaylist } from "./GeneratedPlaylist";

export function GeneratePlaylist({ disabled, genre1, genre2, playlists }) {
  const [playlistName, setPlaylistName] = useState('New Playlist');
  const [playlist, setPlaylist] = useState([]);
  const [currentCount, setCurrentCount] = useState(10);

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
    return names[Math.floor(Math.random() * names.length)];
  };

  const handleClick = () => {
    if (onGenerate && genre1 && genre2) {
      onGenerate(genre1, genre2);
    }
  };

  // Handle playlist generation
  const handleGeneratePlaylist = () => {
    if (genre1 && genre2 && playlists && playlists.length > 0) {
      const generatedPlaylist = generatePlaylistFromData(playlists, genre1, genre2, currentCount);
      setPlaylistName(generatePlaylistName(genre1, genre2));
      setPlaylist(generatedPlaylist);
    }
  };

  const handleLoadMore = () => {
    const newCount = currentCount + 10;
    setCurrentCount(newCount);
    const updatedPlaylist = generatePlaylistFromData(
      playlists,
      genre1,
      genre2,
      newCount
    );
    setPlaylist(updatedPlaylist);
  };
  
  const generatePlaylistFromData = (playlists, genre1, genre2, currentCount = 10) => {
    const songs = [];
    const trackSet = new Set();
    const artistSet = new Set(); // To track added artists
  
    playlists.forEach((playlist) => {
      playlist.tracks?.forEach((track) => {
        const trackName = Array.isArray(track) ? track[4] : track.name;
        const trackArtist = Array.isArray(track) ? track[1] : track.artist;
        const trackGenres =
          Array.isArray(track) &&
          Array.isArray(track[8]) &&
          Array.isArray(track[8][0])
            ? track[8][0] // Extract the first array inside `track[8]`
            : []; // Ensure genres are an array
        const trackStreams = Array.isArray(track) ? track[6] : track.streams;
  
        const genresToCheck = trackGenres.filter(
          (g) => typeof g === "string" && !g.startsWith("spotify")
        );
        const selectedGenres = genresToCheck.length > 0 ? genresToCheck.slice(0, 3) : ["Unknown Genre"];
  
        // Calculate genre relevance score for both genres
        const genreRelevance = selectedGenres.reduce(
          (score, genre) => {
            if (
              genre1 &&
              genre1.name &&
              typeof genre1.name === "string" &&
              genre.toLowerCase().includes(genre1.name.toLowerCase())
            ) {
              score.genre1 += 1;
            }
            if (
              genre2 &&
              genre2.name &&
              typeof genre2.name === "string" &&
              genre.toLowerCase().includes(genre2.name.toLowerCase())
            ) {
              score.genre2 += 1;
            }
            return score;
          },
          { genre1: 0, genre2: 0 } // Initialize scores for both genres
        );
  
        const uniqueKey = `${trackName}-${trackArtist}`;
        const balanceScore = Math.min(genreRelevance.genre1, genreRelevance.genre2);
  
        if (
          trackName &&
          trackArtist &&
          !trackSet.has(uniqueKey) &&
          !artistSet.has(trackArtist) &&
          balanceScore > 0
        ) {
          songs.push({
            id: uniqueKey,
            name: trackName,
            artist: trackArtist,
            genre: selectedGenres.join(", ") || "Unknown Genre",
            streams: trackStreams || 0,
            balanceScore,
          });
          trackSet.add(uniqueKey);
          artistSet.add(trackArtist); // Add artist to the set
        }
      });
    });
  
    console.log("Generated songs:", songs); // Debug log
    return songs.sort((a, b) => b.balanceScore - a.balanceScore).slice(0, currentCount);
  };
  
  
  

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

        {playlist.length > 0 && (
          <>
            <button
              onClick={handleLoadMore}
              className="mt-4 px-4 py-2 rounded-lg bg-green-500 hover:bg-green-700 text-white font-medium"
            >
              + 10
            </button>
          </>
        )}
      </div>
    </div>
  );
}
