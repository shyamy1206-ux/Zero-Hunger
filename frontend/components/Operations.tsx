"use client";

import React, { useState } from 'react';

// Types
type Beneficiary = {
  id: number | string;
  name: string;
};

type Donation = {
  id: number | string;
  item_name: string;
  timestamp: string;
};

export default function Operations() {
  // Local state for optimistic updates
  const [waitingList, setWaitingList] = useState<Beneficiary[]>([]);
  const [newPersonName, setNewPersonName] = useState('');
  
  // We initialize with a couple of mock items so the undo action is testable visually.
  // In a full app, these would be fetched from a GET endpoint.
  const [donations, setDonations] = useState<Donation[]>([
    { id: 'mock1', item_name: '2 Boxes of Cereal', timestamp: '10 mins ago' },
    { id: 'mock2', item_name: '5 lbs Fresh Apples', timestamp: '2 mins ago' }
  ]);
  
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  const showError = () => {
    setErrorMsg("Something went wrong. Please try again.");
    setTimeout(() => setErrorMsg(null), 5000);
  };

  // --- Beneficiary Actions (FIFO / Queue) ---
  const handleAddPerson = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newPersonName.trim()) return;

    const newPerson: Beneficiary = {
      id: Date.now(), // Temporary ID for optimistic UI
      name: newPersonName.trim(),
    };

    // Optimistic UI update: Add to the end of the line
    setWaitingList((prev) => [...prev, newPerson]);
    setNewPersonName('');

    try {
      const res = await fetch('http://localhost:8000/beneficiary/enqueue', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name: newPerson.name })
      });
      if (!res.ok) throw new Error('Network error');
      
      const data = await res.json();
      // Update with the real database ID
      setWaitingList((prev) => 
        prev.map((p) => (p.id === newPerson.id ? { ...p, id: data.id } : p))
      );
    } catch (err) {
      // Revert optimistic update
      setWaitingList((prev) => prev.filter((p) => p.id !== newPerson.id));
      setNewPersonName(newPerson.name);
      showError();
    }
  };

  const handleServeNext = async () => {
    if (waitingList.length === 0) return;

    const personToServe = waitingList[0]; // First in line (FIFO)

    // Optimistic UI update: Remove from the front of the line
    setWaitingList((prev) => prev.slice(1));

    try {
      const res = await fetch('http://localhost:8000/beneficiary/dequeue', {
        method: 'POST'
      });
      if (!res.ok) throw new Error('Network error');
    } catch (err) {
      // Revert optimistic update: Put them back at the front
      setWaitingList((prev) => [personToServe, ...prev]);
      showError();
    }
  };

  // --- Donation Actions (LIFO / Stack) ---
  const handleUndoDonation = async () => {
    if (donations.length === 0) return;

    const lastDonation = donations[donations.length - 1]; // Last added (LIFO)

    // Optimistic UI update: Remove the most recent item
    setDonations((prev) => prev.slice(0, -1));

    try {
      const res = await fetch('http://localhost:8000/donation/pop', {
        method: 'POST'
      });
      // Allow 400s if the database stack is actually empty on the backend side but we had mock data
      if (!res.ok && res.status !== 400) throw new Error('Network error');
    } catch (err) {
      // Revert optimistic update: Put it back at the end
      setDonations((prev) => [...prev, lastDonation]);
      showError();
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 font-sans text-stone-800 bg-stone-50 min-h-screen">
      
      {/* Global Error Message */}
      {errorMsg && (
        <div className="mb-10 p-6 bg-red-50 border-l-8 border-red-500 rounded-2xl shadow-sm flex items-center transition-all">
          <p className="text-2xl text-red-800 font-medium">{errorMsg}</p>
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-12">
        
        {/* --- SECTION 1: People Waiting (Queue) --- */}
        <section className="bg-white rounded-3xl p-10 shadow-sm border border-stone-100 flex flex-col h-full">
          <div className="mb-10 border-b border-stone-100 pb-6">
            <h2 className="text-4xl font-bold text-stone-900 mb-4">People Waiting</h2>
            <p className="text-xl text-stone-500">Manage the line of beneficiaries.</p>
          </div>

          <form onSubmit={handleAddPerson} className="flex flex-col sm:flex-row gap-4 mb-10">
            <input
              type="text"
              placeholder="Enter person's name..."
              className="flex-1 px-6 py-5 rounded-2xl border-2 border-stone-200 text-xl focus:outline-none focus:border-emerald-500 focus:ring-4 focus:ring-emerald-50 transition-all placeholder:text-stone-400"
              value={newPersonName}
              onChange={(e) => setNewPersonName(e.target.value)}
            />
            <button 
              type="submit" 
              disabled={!newPersonName.trim()}
              className="px-8 py-5 bg-stone-100 hover:bg-stone-200 text-stone-800 font-semibold text-xl rounded-2xl transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Add Person
            </button>
          </form>

          <div className="flex-1 flex flex-col">
            {waitingList.length === 0 ? (
              <div className="flex-1 flex items-center justify-center p-12 bg-stone-50 rounded-3xl border-2 border-dashed border-stone-200">
                <p className="text-2xl text-stone-500 font-medium">No one is waiting right now.</p>
              </div>
            ) : (
              <ul className="space-y-4 mb-10 flex-1">
                {waitingList.map((person, index) => (
                  <li 
                    key={person.id} 
                    className="flex items-center gap-6 p-6 bg-stone-50 rounded-2xl border border-stone-100"
                  >
                    <div className="w-14 h-14 bg-white shadow-sm rounded-full flex items-center justify-center text-stone-600 font-bold text-2xl border border-stone-200">
                      {index + 1}
                    </div>
                    <div>
                      <p className="font-semibold text-2xl text-stone-900">{person.name}</p>
                      {index === 0 && (
                        <p className="text-emerald-600 font-medium text-lg mt-1">Next to be served</p>
                      )}
                    </div>
                  </li>
                ))}
              </ul>
            )}
            
            <button 
              onClick={handleServeNext}
              disabled={waitingList.length === 0}
              className="w-full mt-auto py-6 bg-emerald-600 hover:bg-emerald-700 disabled:bg-stone-200 disabled:text-stone-400 text-white font-bold text-2xl rounded-2xl transition-all shadow-md hover:shadow-lg"
            >
              Serve Next Person
            </button>
          </div>
        </section>

        {/* --- SECTION 2: Recent Donations (Stack) --- */}
        <section className="bg-white rounded-3xl p-10 shadow-sm border border-stone-100 flex flex-col h-full">
          <div className="mb-10 border-b border-stone-100 pb-6">
            <h2 className="text-4xl font-bold text-stone-900 mb-4">Recent Donations</h2>
            <p className="text-xl text-stone-500">Latest items added to the inventory.</p>
          </div>

          <div className="flex-1 flex flex-col">
            {donations.length === 0 ? (
              <div className="flex-1 flex items-center justify-center p-12 bg-stone-50 rounded-3xl border-2 border-dashed border-stone-200">
                <p className="text-2xl text-stone-500 font-medium">No donations have been added yet.</p>
              </div>
            ) : (
              <ul className="space-y-4 mb-10 flex-1">
                {donations.map((donation, index) => {
                  const isMostRecent = index === donations.length - 1;
                  return (
                    <li 
                      key={donation.id} 
                      className={`p-6 rounded-2xl border transition-all ${
                        isMostRecent ? 'bg-orange-50/50 border-orange-100' : 'bg-stone-50 border-stone-100'
                      }`}
                    >
                      <div className="flex justify-between items-start">
                        <div>
                          <p className="font-semibold text-2xl text-stone-900">{donation.item_name}</p>
                          <p className="text-stone-500 text-lg mt-2">{donation.timestamp}</p>
                        </div>
                        {isMostRecent && (
                          <span className="px-4 py-1.5 bg-orange-100 text-orange-800 text-sm font-bold rounded-full">
                            Most Recent
                          </span>
                        )}
                      </div>
                    </li>
                  );
                })}
              </ul>
            )}

            <button 
              onClick={handleUndoDonation}
              disabled={donations.length === 0}
              className="w-full mt-auto py-6 bg-white border-4 border-stone-200 hover:border-stone-300 hover:bg-stone-50 disabled:border-stone-100 disabled:text-stone-300 text-stone-600 font-bold text-2xl rounded-2xl transition-all flex items-center justify-center gap-4"
            >
              <svg xmlns="http://www.w3.org/2000/svg" className="h-8 w-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={3}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M3 10h10a8 8 0 018 8v2M3 10l6 6m-6-6l6-6" />
              </svg>
              Undo Last Donation
            </button>
          </div>
        </section>

      </div>
    </div>
  );
}
