import axios from "axios";

// idk man
// just 'run npm dev' in the playlist-dashboard directory locally
// kubectl port-forward svc/catalougeapi 8080:8080
const API_BASE_URL = "http://localhost:8080/catalouge";

// doesnt work:
// const API_BASE_URL = "catalougeapi:8080/catalouge";

/**
 * Fetch playlists based on genres.
 * @param {string} genre1 - The first genre.
 * @param {string} genre2 - The second genre.
 * @returns {Promise<Array>} - The API response containing playlist data.
 */
export async function fetchPlaylistsByGenres(genre1, genre2) {
  const requestBody = {
    genre1: genre1?.name.toLowerCase() || '',
    genre2: genre2?.name.toLowerCase() || '',
  };

  try {
    const response = await axios.post(API_BASE_URL, requestBody, {
      headers: {
        "Content-Type": "application/json",
      },
    });

    //console.log("Request Body:", requestBody); // Log the payload sent to the API
    console.log("API Response:", response.data); // Log the raw API response

    // Transform data to include only top 3 genres
    const playlists = response.data.map((playlist) => ({
      name: playlist.name,
      num_followers: playlist.num_followers,
      num_tracks: playlist.num_tracks,
      duration_ms: playlist.duration_ms,
      modified_at: playlist.modified_at,
      genres: Object.entries(playlist.genre_counts || {}) // Convert genre_counts object to an array
        .sort(([, countA], [, countB]) => countB - countA) // Sort by count in descending order
        .slice(0, 3) // Take the top 3 genres
        .map(([genre]) => genre), // Extract genre names
        genre_counts: playlist.genre_counts || {}, // Include the full genre_counts object
        tracks: playlist.tracks || [], // Include tracks in the transformed data
    }));

    return playlists;
  } catch (error) {
    console.error("Error fetching playlists:", error.message);
    throw new Error("Failed to fetch playlists");
  }
}
