import React, { useState, useEffect, useRef } from 'react';
import { Combobox, Transition } from '@headlessui/react';
import { MagnifyingGlassIcon, ChevronUpDownIcon, CheckIcon } from '@heroicons/react/20/solid';
import { genres } from '../../data/genres';
import { clsx } from 'clsx';
import { useClickAway } from 'react-use';

export function SingleGenreSelector({ label, selectedGenre, onChange, excludedGenres = [] }) {
  const [query, setQuery] = useState('');
  const [filteredGenres, setFilteredGenres] = useState([]);
  const dropdownRef = useRef(null);
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);

  // Close dropdown on outside click
  useClickAway(dropdownRef, () => {
    setIsDropdownOpen(false);
    setQuery('');
  });

  // Filter genres dynamically, excluding already selected genres
  useEffect(() => {
    const filtered = genres.filter(
      (genre) =>
        genre.name.toLowerCase().includes(query.toLowerCase()) &&
        !excludedGenres.some((ex) => ex.id === genre.id) // Exclude already selected genres
    );
    setFilteredGenres(filtered);
  }, [query, excludedGenres]);

  // Handle genre selection
  const handleSelectGenre = (genre) => {
    onChange(genre); // Replace the selected genre
    setIsDropdownOpen(false);
  };

  return (
    <div className="w-full max-w-xs relative" ref={dropdownRef}>
      <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
        {label}
      </label>
      <Combobox value={selectedGenre} onChange={handleSelectGenre}>
        <div className="relative">
          {/* Input Field with Search Icon */}
          <Combobox.Input
            className="w-full pl-10 pr-10 py-2 rounded-lg border dark:border-gray-600 dark:bg-gray-700 dark:text-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
            onChange={(event) => setQuery(event.target.value)}
            onFocus={() => setIsDropdownOpen(true)} // Open dropdown on focus
            displayValue={(genre) => genre?.name || ''}
            placeholder="Search genre..."
          />
          {/* Search Icon */}
          <MagnifyingGlassIcon className="absolute left-3 top-2.5 h-5 w-5 text-gray-400" />

          {/* Dropdown */}
          <Transition
            show={isDropdownOpen}
            enter="transition ease-out duration-200"
            enterFrom="opacity-0 scale-95"
            enterTo="opacity-100 scale-100"
            leave="transition ease-in duration-150"
            leaveFrom="opacity-100 scale-100"
            leaveTo="opacity-0 scale-95"
          >
            <Combobox.Options className="absolute mt-1 max-h-60 w-full overflow-auto rounded-lg bg-white dark:bg-gray-700 py-1 text-base shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none sm:text-sm z-50">
              {filteredGenres.length === 0 ? (
                <div className="relative cursor-default select-none px-4 py-2 text-gray-700 dark:text-gray-300">
                  No genres found.
                </div>
              ) : (
                filteredGenres.map((genre) => (
                  <Combobox.Option
                    key={genre.id}
                    value={genre}
                    className={({ active }) =>
                      clsx(
                        'relative cursor-pointer select-none py-2 pl-10 pr-4',
                        active ? 'bg-indigo-600 text-white' : 'text-gray-900 dark:text-gray-100'
                      )
                    }
                  >
                    {({ selected }) => (
                      <>
                        <span
                          className={`block truncate ${
                            selected ? 'font-medium' : 'font-normal'
                          }`}
                        >
                          {genre.name}
                        </span>
                        {selectedGenre?.id === genre.id && (
                          <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-indigo-600 dark:text-indigo-300">
                            <CheckIcon className="h-5 w-5" aria-hidden="true" />
                          </span>
                        )}
                      </>
                    )}
                  </Combobox.Option>
                ))
              )}
            </Combobox.Options>
          </Transition>
        </div>
      </Combobox>
    </div>
  );
}
