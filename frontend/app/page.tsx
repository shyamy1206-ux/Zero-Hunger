import React from 'react';

export default function FoodBankDashboard() {
  return (
    <div className="min-h-screen bg-stone-50 text-stone-800 font-sans">
      {/* Navigation Bar */}
      <nav className="bg-white shadow-sm border-b border-stone-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-20 items-center">
            <div className="flex items-center gap-3">
              {/* Heart Icon */}
              <svg xmlns="http://www.w3.org/2000/svg" className="h-8 w-8 text-emerald-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
              </svg>
              <span className="text-2xl font-semibold text-stone-900 tracking-tight">Community Food Bank</span>
            </div>
            <div className="hidden md:flex space-x-6">
              <a href="#" className="text-emerald-700 bg-emerald-50 px-4 py-2 rounded-lg font-medium transition-colors">Dashboard</a>
              <a href="#" className="text-stone-600 hover:text-emerald-600 hover:bg-stone-50 px-4 py-2 rounded-lg font-medium transition-colors">Inventory</a>
              <a href="#" className="text-stone-600 hover:text-emerald-600 hover:bg-stone-50 px-4 py-2 rounded-lg font-medium transition-colors">People</a>
            </div>
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        
        {/* Welcome Section */}
        <div className="mb-12">
          <h1 className="text-4xl font-bold text-stone-900 mb-3">Welcome to the Dashboard</h1>
          <p className="text-xl text-stone-600">Here is a quick overview of what is happening today.</p>
        </div>

        {/* Dashboard Cards Layout */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          
          {/* Card 1: Food Available */}
          <section className="bg-white rounded-3xl p-8 shadow-sm border border-stone-100 flex flex-col h-full">
            <div className="flex items-center gap-4 mb-8">
              <div className="p-4 bg-emerald-100 rounded-2xl text-emerald-700">
                {/* Package Icon */}
                <svg xmlns="http://www.w3.org/2000/svg" className="h-8 w-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
                </svg>
              </div>
              <h2 className="text-2xl font-bold text-stone-900">Food Available</h2>
            </div>
            
            <div className="flex-1 space-y-4">
              <div className="flex justify-between items-center py-3 border-b border-stone-100">
                <span className="text-stone-800 font-medium text-lg">Fresh Apples</span>
                <span className="text-stone-600 bg-stone-100 px-4 py-1.5 rounded-full text-base font-medium">45 items</span>
              </div>
              <div className="flex justify-between items-center py-3 border-b border-stone-100">
                <span className="text-stone-800 font-medium text-lg">Canned Beans</span>
                <span className="text-stone-600 bg-stone-100 px-4 py-1.5 rounded-full text-base font-medium">120 items</span>
              </div>
              <div className="flex justify-between items-center py-3 border-b border-stone-100">
                <span className="text-stone-800 font-medium text-lg">Whole Wheat Bread</span>
                <span className="text-stone-600 bg-stone-100 px-4 py-1.5 rounded-full text-base font-medium">30 items</span>
              </div>
            </div>

            <button className="mt-8 w-full py-4 text-emerald-700 font-semibold text-lg flex items-center justify-center gap-2 bg-emerald-50 hover:bg-emerald-100 rounded-xl transition-colors">
              Scan New Donation
            </button>
          </section>

          {/* Card 2: People Waiting */}
          <section className="bg-white rounded-3xl p-8 shadow-sm border border-stone-100 flex flex-col h-full border-t-8 border-t-emerald-500">
            <div className="flex items-center justify-between mb-8">
              <div className="flex items-center gap-4">
                <div className="p-4 bg-stone-100 rounded-2xl text-stone-700">
                  {/* Users Icon */}
                  <svg xmlns="http://www.w3.org/2000/svg" className="h-8 w-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                  </svg>
                </div>
                <h2 className="text-2xl font-bold text-stone-900">People Waiting</h2>
              </div>
              <span className="bg-emerald-100 text-emerald-800 text-xl font-bold px-4 py-1.5 rounded-full">
                5
              </span>
            </div>
            
            <div className="flex-1 space-y-4">
              <div className="flex items-center gap-4 py-3">
                <div className="w-12 h-12 bg-stone-100 rounded-full flex items-center justify-center text-stone-600 font-bold text-xl">1</div>
                <div>
                  <p className="font-semibold text-xl text-stone-900">Maria S.</p>
                  <p className="text-stone-500 text-base">Waiting for 10 mins</p>
                </div>
              </div>
              <div className="flex items-center gap-4 py-3">
                <div className="w-12 h-12 bg-stone-100 rounded-full flex items-center justify-center text-stone-600 font-bold text-xl">2</div>
                <div>
                  <p className="font-semibold text-xl text-stone-900">David L.</p>
                  <p className="text-stone-500 text-base">Waiting for 15 mins</p>
                </div>
              </div>
            </div>

            <button className="mt-8 w-full py-5 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold text-xl rounded-2xl transition-all shadow-md hover:shadow-lg flex items-center justify-center gap-3">
              Serve Next Person
              <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M14 5l7 7m0 0l-7 7m7-7H3" />
              </svg>
            </button>
          </section>

          {/* Card 3: Recent Donations */}
          <section className="bg-white rounded-3xl p-8 shadow-sm border border-stone-100 flex flex-col h-full">
            <div className="flex items-center gap-4 mb-8">
              <div className="p-4 bg-orange-50 rounded-2xl text-orange-600">
                {/* Shopping Bag Icon */}
                <svg xmlns="http://www.w3.org/2000/svg" className="h-8 w-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
                </svg>
              </div>
              <h2 className="text-2xl font-bold text-stone-900">Recent Donations</h2>
            </div>
            
            <div className="flex-1 space-y-4">
              <div className="p-5 bg-stone-50 rounded-2xl border border-stone-100">
                <p className="font-semibold text-stone-900 text-lg">2 Boxes of Cereal</p>
                <p className="text-stone-500 text-base mt-1">Received 5 mins ago</p>
              </div>
              <div className="p-5 bg-stone-50 rounded-2xl border border-stone-100">
                <p className="font-semibold text-stone-900 text-lg">10 lbs Potatoes</p>
                <p className="text-stone-500 text-base mt-1">Received 1 hour ago</p>
              </div>
            </div>

            <button className="mt-8 w-full py-4 text-stone-500 hover:text-stone-700 font-medium text-lg flex items-center justify-center gap-2 hover:bg-stone-100 rounded-xl transition-colors">
              {/* Undo Icon */}
              <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M3 10h10a8 8 0 018 8v2M3 10l6 6m-6-6l6-6" />
              </svg>
              Undo Last Entry
            </button>
          </section>

        </div>
      </main>
    </div>
  );
}
