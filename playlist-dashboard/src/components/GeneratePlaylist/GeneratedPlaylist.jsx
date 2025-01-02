import React from "react";
import { TrashIcon } from "@heroicons/react/20/solid";

export function GeneratedPlaylist({ playlist, onRemove }) {
  return (
    <div className="mt-6 w-full bg-gray-100 dark:bg-gray-900 rounded-lg shadow-md overflow-hidden">
      <table className="min-w-full divide-y divide-gray-300 dark:divide-gray-700">
        {/* Table Header */}
        <thead className="bg-gray-50 dark:bg-gray-800">
          <tr>
            <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">
              #
            </th>
            <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">
              Song Name
            </th>
            <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">
              Artist Name
            </th>
            <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">
              Genre
            </th>
            <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">
              Streams
            </th>

            <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase">
              Actions
            </th>
          </tr>
        </thead>
        {/* Table Body */}
        <tbody className="divide-y divide-gray-200 dark:divide-gray-700">
          {playlist.map((song, index) => (
            <tr key={song.id} className="hover:bg-gray-50 dark:hover:bg-gray-800">
              {/* Track Number */}
              <td className="px-4 py-3 text-sm font-medium text-gray-500 dark:text-gray-400">
                {index + 1}
              </td>
              {/* Song Name */}
              <td className="px-4 py-3 text-sm text-gray-900 dark:text-white">
                {song.name}
              </td>
              {/* Artist Name */}
              <td className="px-4 py-3 text-sm text-gray-600 dark:text-gray-300">
                {song.artist}
              </td>
              {/* Genre */}
              <td className="px-4 py-3 text-sm text-gray-600 dark:text-gray-300">
                {song.genre}
              </td>
              <td className="px-4 py-3 text-sm text-gray-600 dark:text-gray-300">
                {song.streams}
              </td>

              {/* Actions */}
              <td className="px-4 py-3 space-x-4 flex justify-center items-center">
                <button
                  className="text-red-600 hover:text-red-800"
                  onClick={() => onRemove(song.id)}
                >
                  <TrashIcon className="h-5 w-5" title="Remove" />
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
