"use client";

import React, { useState, useEffect } from 'react';

type FoodItem = {
  id: number;
  name: string;
  quantity: number;
  days_to_expiry: number;
  location?: string;
};

export default function Inventory() {
  const [items, setItems] = useState<FoodItem[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState<string>('');

  const fetchInventory = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch('http://localhost:8000/inventory');
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      const data = await response.json();
      setItems(data);
    } catch (err) {
      setError('We are having trouble loading the food items right now. Please try again in a moment.');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!searchQuery.trim()) {
      fetchInventory();
      return;
    }

    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`http://localhost:8000/inventory/search?name=${encodeURIComponent(searchQuery)}`);
      
      if (response.status === 404) {
        setItems([]);
      } else if (!response.ok) {
        throw new Error('Search failed');
      } else {
        const data = await response.json();
        // The search endpoint returns a single item, wrap it in an array for the table
        setItems([data]);
      }
    } catch (err) {
      setError('We had an issue searching for that item. Please try again in a moment.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchInventory();
  }, []);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 font-sans text-stone-800">
      
      {/* Header & Search */}
      <div className="mb-12">
        <h1 className="text-4xl font-bold text-stone-900 mb-8">Food Inventory</h1>
        
        <form onSubmit={handleSearch} className="flex flex-col sm:flex-row gap-4 max-w-3xl">
          <input
            type="text"
            placeholder="Search for food..."
            className="flex-1 px-6 py-4 rounded-2xl border-2 border-stone-200 text-xl focus:outline-none focus:border-emerald-500 focus:ring-4 focus:ring-emerald-50 transition-all placeholder:text-stone-400 shadow-sm"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          <button 
            type="submit" 
            className="px-10 py-4 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold text-xl rounded-2xl transition-all shadow-sm hover:shadow-md flex-shrink-0"
          >
            Search
          </button>
          {searchQuery && (
            <button 
              type="button"
              onClick={() => {
                setSearchQuery('');
                fetchInventory();
              }}
              className="px-8 py-4 bg-stone-100 hover:bg-stone-200 text-stone-700 font-medium text-xl rounded-2xl transition-colors flex-shrink-0"
            >
              Clear
            </button>
          )}
        </form>
      </div>

      {/* Content Area */}
      <div className="bg-white rounded-3xl shadow-sm border border-stone-100 overflow-hidden">
        {loading ? (
          <div className="p-20 text-center">
            <p className="text-2xl text-stone-500 font-medium animate-pulse">Loading food items, please wait a moment...</p>
          </div>
        ) : error ? (
          <div className="p-20 text-center">
            <p className="text-2xl text-red-600 font-medium">{error}</p>
            <button 
              onClick={fetchInventory} 
              className="mt-8 px-8 py-4 bg-stone-100 hover:bg-stone-200 text-stone-800 rounded-xl font-semibold text-lg transition-colors"
            >
              Try Again
            </button>
          </div>
        ) : items.length === 0 ? (
          <div className="p-20 text-center">
            <p className="text-3xl text-stone-500 font-medium">No food items found.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse min-w-[800px]">
              <thead>
                <tr className="border-b-2 border-stone-100 bg-stone-50/50">
                  <th className="px-10 py-6 text-xl font-semibold text-stone-600">Food Item</th>
                  <th className="px-10 py-6 text-xl font-semibold text-stone-600">Quantity</th>
                  <th className="px-10 py-6 text-xl font-semibold text-stone-600">Expiry</th>
                  <th className="px-10 py-6 text-xl font-semibold text-stone-600">Location</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-stone-100">
                {items.map((item) => {
                  const isExpiringSoon = item.days_to_expiry < 3;
                  return (
                    <tr 
                      key={item.id} 
                      className={`transition-colors ${isExpiringSoon ? 'bg-orange-50/60 hover:bg-orange-50' : 'hover:bg-stone-50/60'}`}
                    >
                      <td className="px-10 py-8">
                        <div className="flex items-center gap-4">
                          <span className="text-2xl font-medium text-stone-900">{item.name}</span>
                          {isExpiringSoon && (
                            <span className="inline-flex items-center px-4 py-1.5 rounded-full text-sm font-bold bg-orange-100 text-orange-800 border border-orange-200">
                              Use soon
                            </span>
                          )}
                        </div>
                      </td>
                      <td className="px-10 py-8 text-2xl text-stone-700">{item.quantity}</td>
                      <td className="px-10 py-8 text-2xl text-stone-700">
                        {item.days_to_expiry} {item.days_to_expiry === 1 ? 'day' : 'days'}
                      </td>
                      {/* Fallback to 'Main Pantry' as our current backend doesn't store location */}
                      <td className="px-10 py-8 text-2xl text-stone-500">{item.location || 'Main Pantry'}</td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
