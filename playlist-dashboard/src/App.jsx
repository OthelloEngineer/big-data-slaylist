import { useState, useEffect } from 'react';
import { Header } from './components/Header/Header';
import { PlaylistList } from './components/PlaylistList/PlaylistList';
import { SidebarFilter } from './components/SidebarFilter/SidebarFilter';
import { SingleGenreSelector } from './components/SingleGenreSelector/SingleGenreSelector';
import { GeneratePlaylist } from './components/GeneratePlaylist/GeneratePlaylist';
import { fetchPlaylistsByGenres } from './services/apiService';
import { TestButton } from './components/TestButton/TestButton';
import { PlaylistDetailsModal } from './components/PlaylistList/PlaylistDetailsModal';
import { About } from './components/About/About';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

//import { fetchPlaylistsByGenres } from './services/api';
import './index.css';

function App() {
  const [selectedGenres, setSelectedGenres] = useState([]);
  const [playlists, setPlaylists] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [genre1, setGenre1] = useState(null);
  const [genre2, setGenre2] = useState(null);

  const [selectedPlaylist, setSelectedPlaylist] = useState(null);
  

  // Combine selected genres to check the Generate button's state
  const singleSelectedGenres = [genre1, genre2].filter(Boolean);

  // Update selectedGenres when genre1 or genre2 changes
  useEffect(() => {
    const newSelected = [genre1, genre2].filter(Boolean); // Combine non-null genres
    setSelectedGenres(newSelected);
  }, [genre1, genre2]);


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
  const [durationSortOrder, setDurationSortOrder] = useState('desc'); 

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

  const [modifiedSortOrder, setModifiedSortOrder] = useState('asc');

const sortByModified = () => {
  setPlaylists((prev) => {
    const sorted = [...prev].sort((a, b) => {
      return modifiedSortOrder === 'asc'
        ? a.modified_at - b.modified_at // Ascending
        : b.modified_at - a.modified_at; // Descending
    });
    return sorted;
  });
  setModifiedSortOrder((prev) => (prev === 'asc' ? 'desc' : 'asc')); // Toggle order
};

  const sortByDuration = () => {
    setPlaylists((prev) => {
      const sorted = [...prev].sort((a, b) => {
        if (durationSortOrder === 'desc') {
          return b.duration_ms - a.duration_ms; // Sort by longest duration
        } else {
          return a.duration_ms - b.duration_ms; // Sort by shortest duration
        }
      });
      return sorted;
    });
    setDurationSortOrder((prevOrder) => (prevOrder === 'desc' ? 'asc' : 'desc'));
  };

  const sortByRelevance = () => {
    setPlaylists((prev) => {
      const sorted = [...prev].sort((a, b) => {
        const calculateRelevance = (playlist) => {
          const playlistGenres = playlist.genres || [];
          const relevantCount = playlistGenres.filter((genre) =>
            selectedGenres.some((selected) => selected?.name?.toLowerCase() === genre.toLowerCase())
          ).length;
          return selectedGenres.length > 0 ? relevantCount / selectedGenres.length : 0; // Avoid division by zero
        };
        return calculateRelevance(b) - calculateRelevance(a);
      });
      return sorted;
    });
  };


  return (
    <Router>
    <div className="min-h-screen bg-gray-100 dark:bg-gray-900">
      <Header />
      <main className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <Routes>
      <Route path="/about" element={<About />} />
      <Route
              path="/"
              element={
          
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

            {/*Search and filter */}
            <div className="flex flex-col lg:flex-row">
              {/* Sidebar */}
              <div className="lg:w-1/6 p-4 mt-6">
                <SidebarFilter
                  onPopularityFilter={sortByPopularity}
                  onNewestFilter={sortByModified}
                  onDurationFilter={sortByDuration}
                  onRelevanceFilter={sortByRelevance}
                />
              </div>
              
              {/* Playlist Search */}
              <div className='lg:w-4/6 p-4'>
                <div className="mt-6">
                  <PlaylistList
                    playlists={playlists}
                    loading={loading}
                    error={error}
                    onPlaylistSelect={setSelectedPlaylist}
                    selectedGenres={selectedGenres}
                  />
                </div>
              </div>
              {/* Render Modal */}
              {selectedPlaylist && (
                <PlaylistDetailsModal
                  playlist={selectedPlaylist}
                  selectedGenres={selectedGenres}
                  onClose={() => setSelectedPlaylist(null)}
                />
              )}
              {/* Test API Button 
              <TestButton />
              */}
            </div>
          </div>
        </div>
              }
        />
      </Routes>  
      </main>
    </div>
    </Router>
  );
}

export default App;