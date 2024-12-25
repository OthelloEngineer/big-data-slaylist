import React from 'react';

export function SidebarFilter({ onPopularityFilter, onNewestFilter, onDurationFilter, onRelevanceFilter }) {
  return (
    <div className="w-30 bg-white dark:bg-gray-800 p-4 shadow-md rounded-md">
      <h3 className="text-lg font-semibold mb-4 text-gray-900 dark:text-white">Sort by:</h3>
      <div className="space-y-2">
        <button
          className="w-full px-3 py-2 text-left rounded-md text-sm font-medium transition-colors duration-200
                     bg-gray-100 dark:bg-gray-700 text-gray-900 dark:text-white hover:bg-indigo-500 hover:text-white"
          onClick={onPopularityFilter}
        >
          Followers
        </button>
        <button
          className="w-full px-3 py-2 text-left rounded-md text-sm font-medium transition-colors duration-200
                     bg-gray-100 dark:bg-gray-700 text-gray-900 dark:text-white hover:bg-indigo-500 hover:text-white"
          onClick={onNewestFilter}
        >
          Date Modified
        </button>
        <button
          className="w-full px-3 py-2 text-left rounded-md text-sm font-medium transition-colors duration-200
                   bg-gray-100 dark:bg-gray-700 text-gray-900 dark:text-white hover:bg-indigo-500 hover:text-white"
          onClick={onDurationFilter}>
          Duration
        </button>
        <button
          className='w-full px-3 py-2 text-left rounded-md text-sm font-medium transition-colors duration-200
                   bg-gray-100 dark:bg-gray-700 text-gray-900 dark:text-white hover:bg-indigo-500 hover:text-white'
          onClick={onRelevanceFilter}
          >
          Relevance
          </button>
      </div>
    </div>
  );
}
