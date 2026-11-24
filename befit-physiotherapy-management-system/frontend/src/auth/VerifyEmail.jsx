import React, { useEffect, useState } from 'react';
import axios, { errorMessage } from '../api';
import { useLocation, useNavigate, Link } from 'react-router-dom';
import AuthShell, { inputCls, btnCls } from './AuthShell';

const COOLDOWN = 60;

export default function VerifyEmail() {
  const { state } = useLocation();
  const navigate = useNavigate();
  const [email, setEmail] = useState(state?.email || '');
  const [otp, setOtp] = useState('');
  const [msg, setMsg] = useState('');
  const [error, setError] = useState('');
  const [wait, setWait] = useState(state?.email ? COOLDOWN : 0);

  useEffect(() => {
    if (wait <= 0) return;
    const t = setTimeout(() => setWait((w) => w - 1), 1000);
    return () => clearTimeout(t);
  }, [wait]);

  const resend = async () => {
    setError(''); setMsg('');
    try {
      await axios.post('/api/auth/resend-verification', { email });
      setMsg('If the email is registered, a new code has been sent.');
      setWait(COOLDOWN);
    } catch (err) { setError(await errorMessage(err)); }
  };

  // Login var "not verified" ala tar navin code pathva
  useEffect(() => { if (state?.sendNow && state?.email) resend(); /* eslint-disable-next-line */ }, []);

  const submit = async (e) => {
    e.preventDefault(); setError('');
    try {
      await axios.post('/api/auth/verify-email', { email, otp });
      navigate('/login');
    } catch (err) { setError(await errorMessage(err)); }
  };

  return (
    <AuthShell title="Verify your email" subtitle="Enter the 6-digit code we sent to your email.">
      {error && <div className="mb-4 p-3 bg-red-100 text-red-700 rounded-md text-sm">{error}</div>}
      {msg && <div className="mb-4 p-3 bg-green-100 text-green-700 rounded-md text-sm">{msg}</div>}
      <form onSubmit={submit} className="space-y-4">
        <input className={inputCls} type="email" placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        <input className={inputCls} inputMode="numeric" maxLength={6} placeholder="6-digit code" value={otp} onChange={(e) => setOtp(e.target.value)} required />
        <button className={btnCls} type="submit">Verify</button>
      </form>
      <button onClick={resend} disabled={wait > 0 || !email} className="mt-4 text-sm text-blue-600 disabled:text-gray-400">
        {wait > 0 ? `Resend code in ${wait}s` : 'Resend code'}
      </button>
      <p className="mt-4 text-sm text-gray-600"><Link to="/login" className="text-blue-600">Back to login</Link></p>
    </AuthShell>
  );
}