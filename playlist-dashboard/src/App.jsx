import { useState, useEffect } from 'react';
import { Header } from './components/Header/Header';
import { GenreSelector } from './components/GenreSelector/GenreSelector';
import { PlaylistList } from './components/PlaylistList/PlaylistList';
import { SidebarFilter } from './components/SidebarFilter/SidebarFilter';
import { SingleGenreSelector } from './components/SingleGenreSelector/SingleGenreSelector';
import { GeneratePlaylist } from './components/GeneratePlaylist/GeneratePlaylist';
import { fetchPlaylistsByGenres } from './services/apiService';
import { TestButton } from './components/TestButton/TestButton';


//import { fetchPlaylistsByGenres } from './services/api';
import './index.css';

function App() {
  const [selectedGenres, setSelectedGenres] = useState([]);
  const [playlists, setPlaylists] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [genre1, setGenre1] = useState(null);
  const [genre2, setGenre2] = useState(null);

  

  // Combine selected genres to check the Generate button's state
  const singleSelectedGenres = [genre1, genre2].filter(Boolean);

  // Update selectedGenres when genre1 or genre2 changes
  useEffect(() => {
    const newSelected = [genre1, genre2].filter(Boolean); // Combine non-null genres
    setSelectedGenres(newSelected);
  }, [genre1, genre2]);

  const isGenreSelected = (genre) =>
    selectedGenres.some((g) => g.id === genre.id);

  const handleFetchPlaylists = async (genre1, genre2) => {
    try {
      setLoading(true);
      setError(null);

      const data = await fetchPlaylistsByGenres(genre1, genre2);
      setPlaylists(data);
    } catch (err) {
      console.error("Error fetching playlists:", err.message);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const fetchPlaylists = async () => {
      if (!genre1 || !genre2) {
        setPlaylists([]);
        return;
      }

      try {
        setLoading(true);
        setError(null);

        const data = await fetchPlaylistsByGenres(genre1, genre2);
        console.log("Fetched Playlists:", data); // Debug log
        setPlaylists(data);
      } catch (err) {
        setError(err.message);
        setPlaylists([]);
      } finally {
        setLoading(false);
      }
    };

    fetchPlaylists();
  }, [genre1, genre2]); // Fetch whenever genre1 or genre2 changes

    // Exclude genres already selected in the other dropdown
    const excludedGenresForGenre1 = genre2 ? [genre2] : [];
    const excludedGenresForGenre2 = genre1 ? [genre1] : [];

  const [popularitySortOrder, setPopularitySortOrder] = useState('desc'); // Track sorting state: 'desc' or 'asc'
  const [originalPlaylists, setOriginalPlaylists] = useState([]); // To preserve original order

  useEffect(() => {
    setOriginalPlaylists(playlists); // Keep the original order
  }, [playlists]);

  const sortByPopularity = () => {
    setPlaylists((prev) => {
      const sorted = [...prev].sort((a, b) => {
        if (popularitySortOrder === 'desc') {
          return b.num_followers - a.num_followers; // Sort by most followers
        } else {
          return a.num_followers - b.num_followers; // Sort by least followers
        }
      });
      return sorted;
    });
    // Toggle sort order between ascending and descending
    setPopularitySortOrder((prevOrder) => (prevOrder === 'desc' ? 'asc' : 'desc'));
  };


  const sortByNewest = () => {
    console.log("Newest filter selected");
    // Placeholder for newest functionality
  };

  return (
    <div className="min-h-screen bg-gray-100 dark:bg-gray-900">
      <Header />
      <main className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="flex gap-6">
          {/* Main Content */}
          <div className="flex-1">
            <h2 className="text-xl font-semibold mb-4 text-center dark:text-white">
              Select Genres
            </h2>
            {/* Two Genre Selectors */}
      <div className="flex flex-col lg:flex-row gap-6 items-center justify-center">
        <SingleGenreSelector
          label="Select Genre 1"
          selectedGenres={singleSelectedGenres}
          selectedGenre={genre1}
          onChange={setGenre1}
          excludedGenres={genre2 ? [genre2] : []} // Exclude the genre selected in Genre 2
        />
        <SingleGenreSelector
          label="Select Genre 2"
          selectedGenres={singleSelectedGenres}
          selectedGenre={genre2}
          onChange={setGenre2}
          excludedGenres={genre1 ? [genre1] : []} // Exclude the genre selected in Genre 1
        />
        
      </div>
      <GeneratePlaylist disabled={singleSelectedGenres.length < 2} genre1={genre1} genre2={genre2} />
      {/**
            <GenreSelector
              selected={singleSelectedGenres}
              onChange={handleGenreChange}
            />
           */  }

            {/*Search and filter */}
            <div className="flex flex-col lg:flex-row">
              {/* Sidebar */}
              <div className="lg:w-1/6 p-4 mt-6">
                <SidebarFilter
                  onPopularityFilter={sortByPopularity}
                  onNewestFilter={sortByNewest}
                />
              </div>
              
              {/* Playlist Search */}
              <div className='lg:w-4/6 p-4'>
                <div className="mt-6">
                  <PlaylistList
                    playlists={playlists}
                    loading={loading}
                    error={error}
                  />
                </div>
              </div>
              <TestButton />
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}

export default App;