import { Fragment, useState, useEffect, useRef } from 'react';
import { Tab } from '@headlessui/react';
import { Combobox, Transition } from '@headlessui/react';
import { CheckIcon, ChevronUpDownIcon } from '@heroicons/react/20/solid';
import { genres } from '../../data/genres';
import { genreCategories } from '../../data/genreCategories';
import { clsx } from 'clsx';
import { useClickAway } from 'react-use';
import { GeneratePlaylist } from '../GeneratePlaylist/GeneratePlaylist';


export function GenreSelector({ selected = [], onChange }) {
  const [query, setQuery] = useState('');
  const [filteredGenres, setFilteredGenres] = useState(genres);
  const dropdownRef = useRef(null);
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  

  useClickAway(dropdownRef, () => {
    setIsDropdownOpen(false);
    setQuery(''); // Clear the query on blur
  });
    

  useEffect(() => {
    const filtered = query === ''
      ? genres
      : genres.filter((genre) =>
          genre.name
            .toLowerCase()
            .replace(/\s+/g, '')
            .includes(query.toLowerCase().replace(/\s+/g, ''))
        );
    setFilteredGenres(filtered);
  }, [query]);

  const findGenreByName = (name) => {
    return genres.find(genre => genre.name === name);
  };

  const isGenreSelected = (genre) => {
    return selected.some(g => g.id === genre.id);
  };

  const toggleGenre = (genreOrGenres) => {
    const genresToToggle = Array.isArray(genreOrGenres)
    ? genreOrGenres
    : [genreOrGenres];

  const newSelected = genresToToggle.reduce((acc, genre) => {
    if (isGenreSelected(genre)) {
      return acc.filter(g => g.id !== genre.id); // Deselect
    } else {
      return [...acc, genre]; // Select
    }
  }, selected);

  onChange(newSelected);
  };

  const getGenresByCategory = (category) => {
    const genreNames = genreCategories[category] || [];
    return genres.filter(genre => genreNames.includes(genre.name));
  };

  
  

  return (
    <div className="w-full max-w-3xl mx-auto" ref={dropdownRef}>
      <Tab.Group>
        <Tab.List className="flex space-x-1 rounded-xl bg-indigo-900/20 p-1">
          <Tab
            className={({ selected }) =>
              clsx(
                'w-full rounded-lg py-2.5 text-sm font-medium leading-5',
                'ring-white ring-opacity-60 ring-offset-2 ring-offset-indigo-400 focus:outline-none focus:ring-2',
                selected
                  ? 'bg-white dark:bg-gray-700 shadow text-indigo-700 dark:text-white'
                  : 'text-gray-600 dark:text-gray-300 hover:bg-white/[0.12] hover:text-white'
              )
            }
          >
            Search
          </Tab>
          <Tab
            className={({ selected }) =>
              clsx(
                'w-full rounded-lg py-2.5 text-sm font-medium leading-5',
                'ring-white ring-opacity-60 ring-offset-2 ring-offset-indigo-400 focus:outline-none focus:ring-2',
                selected
                  ? 'bg-white dark:bg-gray-700 shadow text-indigo-700 dark:text-white'
                  : 'text-gray-600 dark:text-gray-300 hover:bg-white/[0.12] hover:text-white'
              )
            }
          >
            Categories
          </Tab>
          <Tab
            className={({ selected }) =>
              clsx(
                'w-full rounded-lg py-2.5 text-sm font-medium leading-5',
                'ring-white ring-opacity-60 ring-offset-2 ring-offset-indigo-400 focus:outline-none focus:ring-2',
                selected
                  ? 'bg-white dark:bg-gray-700 shadow text-indigo-700 dark:text-white'
                  : 'text-gray-600 dark:text-gray-300 hover:bg-white/[0.12] hover:text-white'
              )
            }
          >
            Generate Playlist
          </Tab>
        </Tab.List>
        <Tab.Panels className="mt-4">
          <Tab.Panel>
            <div className="w-full">
              <div className="relative mt-1">
                <div className="relative w-full cursor-default overflow-hidden rounded-lg bg-white dark:bg-gray-700 text-left shadow-md focus:outline-none sm:text-sm">
                  <input
                    className="w-full border-none py-2 pl-3 pr-10 text-sm leading-5 text-gray-900 dark:text-white bg-transparent focus:ring-0"
                    onChange={(event) => setQuery(event.target.value)}
                    onFocus={() => setIsDropdownOpen(true)} // Open dropdown on focus
                    placeholder="Search genres..."
                  />
                </div>
                <Transition
                  show={isDropdownOpen && (filteredGenres.length > 0 || query !== '')}
                  enter="transition ease-out duration-200"
                  enterFrom="opacity-0 scale-95"
                  enterTo="opacity-100 scale-100"
                  leave="transition ease-in duration-150"
                  leaveFrom="opacity-100 scale-100"
                  leaveTo="opacity-0 scale-95">
                <div className="absolute mt-1 max-h-60 w-full overflow-auto rounded-md bg-white dark:bg-gray-700 py-1 text-base shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none sm:text-sm z-50">
                  {filteredGenres.length === 0 && query !== '' ? (
                    <div className="relative cursor-default select-none px-4 py-2 text-gray-700 dark:text-gray-300">
                      Nothing found.
                    </div>
                  ) : (
                    filteredGenres.map((genre) => (
                      <div
                        key={genre.id}
                        className={clsx(
                          'relative cursor-pointer select-none py-2 pl-10 pr-4',
                          isGenreSelected(genre)
                            ? 'bg-indigo-100 dark:bg-indigo-600 text-indigo-900 dark:text-white'
                            : 'text-gray-900 dark:text-gray-100 hover:bg-gray-100 dark:hover:bg-gray-600'
                        )}
                        onClick={() => toggleGenre(genre)}
                      >
                        <span className={`block truncate ${isGenreSelected(genre) ? 'font-medium' : 'font-normal'}`}>
                          {genre.name}
                        </span>
                        {isGenreSelected(genre) && (
                          <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-indigo-600 dark:text-indigo-300">
                            <CheckIcon className="h-5 w-5" aria-hidden="true" />
                          </span>
                        )}
                      </div>
                    ))
                  )}
                </div>
            </Transition>

              </div>
            </div>
          </Tab.Panel>
          <Tab.Panel>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {Object.entries(genreCategories).map(([category, genreList]) => (
                <div
                  key={category}
                  className="bg-white dark:bg-gray-700 rounded-lg p-4 shadow-md"
                >
                  <h3 className="text-lg font-semibold mb-3 text-gray-900 dark:text-white" onClick={() => toggleGenre(getGenresByCategory(category))}>
                    {category}
                  </h3>
                  <div className="flex flex-wrap gap-2">
                    {genreList.map((genreName) => {
                      const genre = findGenreByName(genreName);
                      if (!genre) return null;
                      return (
                        <button
                          key={genre.id}
                          onClick={() => toggleGenre(genre)}
                          className={clsx(
                            'px-3 py-1 rounded-full text-sm font-medium transition-colors',
                            isGenreSelected(genre)
                              ? 'bg-indigo-600 text-white'
                              : 'bg-gray-100 dark:bg-gray-600 text-gray-700 dark:text-gray-200 hover:bg-indigo-100 dark:hover:bg-indigo-900'
                          )}
                        >
                          {genre.name}
                        </button>
                      );
                    })}
                  </div>
                </div>
              ))}
            </div>
          </Tab.Panel>
          <Tab.Panel>
            <GeneratePlaylist />
          </Tab.Panel>
        </Tab.Panels>
      </Tab.Group>
      {/* Horizontal Scroll for Selected Genres */}
      {selected.length > 0 && (
        <div className="mt-6 max-w-full overflow-x-auto flex space-x-4 py-2 px-4 bg-gray-50 dark:bg-gray-800 rounded-md shadow-md">
          {selected.map((genre) => (
            <div
            key={genre.id}
            className={clsx(
              "flex items-center px-2 py-2 rounded-full text-xs font-small cursor-pointer",
              "bg-indigo-100 text-indigo-800 hover:bg-red-500 hover:text-white", // Default (light mode)
              "dark:bg-indigo-700 dark:text-white dark:hover:bg-red-500 dark:hover:text-white" // Dark mode
            )}
            title={genre.name}
            onClick={() => toggleGenre(genre)}
          >
              {genre.name}
              <span className="ml-2 font-bold text-sm">&times;</span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}