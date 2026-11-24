import React from 'react';

export default function AuthShell({ title, subtitle, children }) {
  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center p-6">
      <div className="w-full max-w-md bg-white rounded-xl shadow p-8">
        <div className="text-blue-600 font-bold mb-4">BeFit</div>
        <h2 className="text-2xl font-bold text-gray-900 mb-1">{title}</h2>
        <p className="text-sm text-gray-500 mb-6">{subtitle}</p>
        {children}
      </div>
    </div>
  );
}

export const inputCls = 'w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none';
export const btnCls = 'w-full bg-blue-600 hover:bg-blue-700 text-white font-medium py-3 rounded-lg';