import { genres } from '../data/genres';

// Mock API service
const generateMockPlaylists = (genreNames) => {
  const artists = {
    'Pop': ['Taylor Swift', 'Ed Sheeran', 'Ariana Grande'],
    'Rock': ['Led Zeppelin', 'Queen', 'Foo Fighters'],
    'Jazz': ['Miles Davis', 'John Coltrane', 'Duke Ellington'],
    'Hip Hop': ['Kendrick Lamar', 'Drake', 'J. Cole'],
    'Electronic': ['Daft Punk', 'Deadmau5', 'Calvin Harris'],
  };

  const defaultArtists = ['Artist 1', 'Artist 2', 'Artist 3'];
  
  return genreNames.flatMap((genreName, genreIndex) => {
    const genreArtists = artists[genreName] || defaultArtists;
    return genreArtists.map((artist, index) => ({
      id: Math.random().toString(36).substr(2, 9),
      followers: genreIndex * 3 + index + 1,
      title: `${genreName} Playlist ${index + 1}`,
      artist: artist,
      genre: genreName
    }));
  });
};

export async function fetchPlaylistsByGenres(genres) {
  // Simulate API delay
  await new Promise(resolve => setTimeout(resolve, 1000));
  
  if (!genres || genres.length === 0) {
    throw new Error('At least one genre is required');
  }
  
  // Generate mock playlists for the selected genres
  const playlists = generateMockPlaylists(genres);
  return playlists;
}