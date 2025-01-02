import React, { useState } from "react";
import { GeneratedPlaylist } from "./GeneratedPlaylist";

export function GeneratePlaylist({ disabled, genre1, genre2, playlists }) {
  const [playlistName, setPlaylistName] = useState("New Playlist");
  const [playlist, setPlaylist] = useState([]);
  const [currentCount, setCurrentCount] = useState(10);
  const [genreWeight, setGenreWeight] = useState(50);
  const [popularityWeight, setPopularityWeight] = useState(50);
  const [showSliders, setShowSliders] = useState(false); // New state to control slider visibility

  const generatePlaylistName = (genre1, genre2) => {
    if (!genre1 || !genre2) return "New Playlist";
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

  const handleGeneratePlaylist = () => {
    if (genre1 && genre2 && playlists && playlists.length > 0) {
      const generatedPlaylist = generatePlaylistFromData(
        playlists,
        genre1,
        genre2,
        currentCount,
        genreWeight,
        popularityWeight
      );
      setPlaylistName(generatePlaylistName(genre1, genre2));
      setPlaylist(generatedPlaylist);
      setShowSliders(true); // Show sliders when the playlist is generated
    }
  };

  const handleLoadMore = () => {
    const newCount = currentCount + 10;
    setCurrentCount(newCount);
    const updatedPlaylist = generatePlaylistFromData(
      playlists,
      genre1,
      genre2,
      newCount,
      genreWeight,
      popularityWeight
    );
    setPlaylist(updatedPlaylist);
  };

  const generatePlaylistFromData = (
    playlists,
    genre1,
    genre2,
    currentCount,
    genreWeight,
    popularityWeight
  ) => {
    const songs = [];
    const trackSet = new Set();
    const artistSet = new Set();

    const scaledPopularityWeight = Math.cbrt(popularityWeight / 100) * 100;

    playlists.forEach((playlist) => {
      playlist.tracks?.forEach((track) => {
        const trackName = Array.isArray(track) ? track[4] : track.name;
        const trackArtist = Array.isArray(track) ? track[1] : track.artist;
        const trackGenres =
          Array.isArray(track) &&
          Array.isArray(track[8]) &&
          Array.isArray(track[8][0])
            ? track[8][0]
            : [];
        const trackStreams = Array.isArray(track) ? track[6] : track.streams;

        const genresToCheck = trackGenres.filter(
          (g) => typeof g === "string" && !g.startsWith("spotify")
        );
        const selectedGenres =
          genresToCheck.length > 0 ? genresToCheck.slice(0, 3) : ["Unknown Genre"];

        const genreRelevance = selectedGenres.reduce(
          (score, genre) => {
            if (
              genre1 &&
              genre1.name &&
              typeof genre1.name === "string" &&
              genre.toLowerCase().includes(genre1.name.toLowerCase())
            ) {
              score.genre2 += 1; // Flipped for UI
            }
            if (
              genre2 &&
              genre2.name &&
              typeof genre2.name === "string" &&
              genre.toLowerCase().includes(genre2.name.toLowerCase())
            ) {
              score.genre1 += 1; // Flipped for UI
            }
            return score;
          },
          { genre1: 0, genre2: 0 }
        );

        const weightedGenreRelevance =
          genreRelevance.genre1 * (genreWeight / 100) +
          genreRelevance.genre2 * ((100 - genreWeight) / 100);

        const normalizedStreams = trackStreams
          ? Math.log10(trackStreams + 1)
          : 0;

        const uniqueKey = `${trackName}-${trackArtist}`;
        const finalScore =
          weightedGenreRelevance * ((100 - scaledPopularityWeight) / 100) +
          normalizedStreams * (scaledPopularityWeight / 100);

        if (
          trackName &&
          trackArtist &&
          !trackSet.has(uniqueKey) &&
          !artistSet.has(trackArtist) &&
          finalScore > 0 &&
          !selectedGenres.includes("Unknown Genre") // Exclude tracks with "Unknown Genre"
        ) {
          songs.push({
            id: uniqueKey,
            name: trackName,
            artist: trackArtist,
            genre: selectedGenres.join(", "),
            streams: trackStreams || 0,
            finalScore,
          });
          trackSet.add(uniqueKey);
          artistSet.add(trackArtist);
        }
      });
    });

    return songs.sort((a, b) => b.finalScore - a.finalScore).slice(0, currentCount);
  };

  const handleRemoveSong = (songId) => {
    setPlaylist((prevPlaylist) => prevPlaylist.filter((song) => song.id !== songId));
  };

  return (
    <div className="mt-6 bg-gray-100 dark:bg-gray-900 flex flex-col items-center">
      <div className="w-full max-w-4xl bg-white dark:bg-gray-800 p-6 rounded-lg shadow-md">
        {/* Sliders */}
        {showSliders && (
          <>
            <div className="mb-6">
              <label className="block text-gray-700 dark:text-gray-300 mb-2">
                Genre Weight ({100 - genreWeight}% {genre1?.name || "Genre 1"} /{" "}
                {genreWeight}% {genre2?.name || "Genre 2"})
              </label>
              <input
                type="range"
                min="0"
                max="100"
                step="5"
                value={genreWeight}
                onChange={(e) => setGenreWeight(Number(e.target.value))}
                className="w-full"
              />
            </div>
            <div className="mb-6">
              <label className="block text-gray-700 dark:text-gray-300 mb-2">
                Popularity vs Relevance ({popularityWeight}% Popularity /{" "}
                {100 - popularityWeight}% Relevance)
              </label>
              <input
                type="range"
                min="0"
                max="100"
                step="5"
                value={popularityWeight}
                onChange={(e) => setPopularityWeight(Number(e.target.value))}
                className="w-full"
              />
            </div>
          </>
        )}

        {/* Generate Button */}
        <button
          onClick={handleGeneratePlaylist}
          className={`w-full px-4 py-2 rounded-lg ${
            disabled || !genre1 || !genre2
              ? "bg-gray-400 cursor-not-allowed text-gray-700"
              : "bg-blue-500 hover:bg-blue-700 text-white"
          } font-medium`}
          disabled={disabled || !genre1 || !genre2}
        >
          {disabled && genre1 && genre2 ? "Loading..." : "Generate Playlist"}
        </button>

        {/* Generated Playlist Table */}
        {playlist.length > 0 && (
          <GeneratedPlaylist playlist={playlist} onRemove={handleRemoveSong} />
        )}

        {/* Load More Button */}
        {playlist.length > 0 && (
          <div className="flex justify-center">
            <button
              onClick={handleLoadMore}
              className="mt-4 px-4 py-2 rounded-lg bg-green-500 hover:bg-green-700 text-white font-medium"
            >
              Load More
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
