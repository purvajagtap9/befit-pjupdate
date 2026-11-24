import React, { useState } from 'react';
import axios, { errorMessage } from '../api';
import { useNavigate, Link } from 'react-router-dom';
import AuthShell, { inputCls, btnCls } from './AuthShell';

export default function ForgotPassword() {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [error, setError] = useState('');

  const submit = async (e) => {
    e.preventDefault(); setError('');
    try {
      await axios.post('/api/auth/forgot-password', { email });
      navigate('/reset-password', { state: { email } });
    } catch (err) { setError(await errorMessage(err)); }
  };

  return (
    <AuthShell title="Forgot password?" subtitle="Enter your registered email. We will send you a code.">
      {error && <div className="mb-4 p-3 bg-red-100 text-red-700 rounded-md text-sm">{error}</div>}
      <form onSubmit={submit} className="space-y-4">
        <input className={inputCls} type="email" placeholder="name@example.com" value={email} onChange={(e) => setEmail(e.target.value)} required />
        <button className={btnCls} type="submit">Send code</button>
      </form>
      <p className="mt-4 text-sm text-gray-600"><Link to="/login" className="text-blue-600">Back to login</Link></p>
    </AuthShell>
  );
}