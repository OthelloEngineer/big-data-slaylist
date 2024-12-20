import axios from "axios";

const API_BASE_URL = "http://localhost:8080/catalouge";

/**
 * Fetch playlists based on genres.
 * @param {string} genre1 - The first genre.
 * @param {string} genre2 - The second genre.
 * @returns {Promise<Array>} - The API response containing playlist data.
 */
export async function fetchPlaylistsByGenres(genre1, genre2) {
  const url = 'http://localhost:8080/catalouge';

  const requestBody = {
    genre1: genre1?.name.toLowerCase() || '', // Use the name property of the genre object
    genre2: genre2?.name.toLowerCase() || '',
  };

  try {
    const response = await axios.post(API_BASE_URL, requestBody, {
      headers: {
        "Content-Type": "application/json",
      },
    });
    console.log("Request Body:", requestBody); // Log the payload sent to the API

    // Axios automatically parses the JSON response
    return response.data; // Assuming the API returns an array
  } catch (error) {
    console.error("Error fetching playlists:", error.message);
    throw new Error("Failed to fetch playlists");
  }
}