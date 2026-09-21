import React, { useState } from 'react';
import { User as UserIcon, Lock, Mail, Sparkles, ArrowRight } from 'lucide-react';
import { Modal } from '../common/Modal';

interface AuthModalProps {
  isOpen: boolean;
  onClose: () => void;
  onLogin: (email: string, pass: string) => Promise<void>;
  onRegister: (name: string, email: string, pass: string) => Promise<void>;
  isLoading: boolean;
}

export const AuthModal: React.FC<AuthModalProps> = ({
  isOpen,
  onClose,
  onLogin,
  onRegister,
  isLoading,
}) => {
  const [isRegister, setIsRegister] = useState(false);
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    try {
      if (isRegister) {
        if (!name.trim()) throw new Error('Name is required');
        await onRegister(name.trim(), email.trim(), password);
      } else {
        await onLogin(email.trim(), password);
      }
      onClose();
    } catch (err: any) {
      setError(err.message || 'Authentication failed');
    }
  };

  const handleGuestLogin = async () => {
    setError('');
    try {
      const randomId = typeof crypto !== 'undefined' && crypto.randomUUID ? crypto.randomUUID() : Math.random().toString(36).substring(2, 15);
      const guestEmail = `guest_${randomId}@visualnotes.local`;
      const guestPassword = `guest_${randomId}_${Date.now()}`;
      await onRegister('Guest Learner', guestEmail, guestPassword);
      onClose();
    } catch (err: any) {
      setError(err.message || 'Guest session creation failed');
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={isRegister ? 'Create Account' : 'Welcome Back'}
      subtitle={isRegister ? 'Save your study notes and diagrams across devices' : 'Sign in to access all your generated notes'}
      maxWidth="sm"
    >
      <form onSubmit={handleSubmit} className="space-y-4">
        
        {error && (
          <div className="p-2.5 rounded-lg bg-rose-50 border border-rose-200 text-rose-800 text-xs font-semibold">
            {error}
          </div>
        )}

        {isRegister && (
          <div>
            <label className="text-xs font-bold text-slate-700 block mb-1">
              Full Name
            </label>
            <div className="relative">
              <UserIcon className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
              <input
                type="text"
                required
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="e.g. Alex Turing"
                className="w-full pl-9 pr-3 py-2 text-xs font-medium rounded-xl border border-slate-200 focus:ring-2 focus:ring-indigo-500"
              />
            </div>
          </div>
        )}

        <div>
          <label className="text-xs font-bold text-slate-700 block mb-1">
            Email Address
          </label>
          <div className="relative">
            <Mail className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
            <input
              type="email"
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="you@example.com"
              className="w-full pl-9 pr-3 py-2 text-xs font-medium rounded-xl border border-slate-200 focus:ring-2 focus:ring-indigo-500"
            />
          </div>
        </div>

        <div>
          <label className="text-xs font-bold text-slate-700 block mb-1">
            Password
          </label>
          <div className="relative">
            <Lock className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
            <input
              type="password"
              required
              minLength={6}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              className="w-full pl-9 pr-3 py-2 text-xs font-medium rounded-xl border border-slate-200 focus:ring-2 focus:ring-indigo-500"
            />
          </div>
        </div>

        <button
          type="submit"
          disabled={isLoading}
          className="w-full py-2.5 text-xs sm:text-sm font-bold text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 rounded-xl shadow-md shadow-indigo-600/20 transition-all"
        >
          {isLoading ? 'Processing...' : (isRegister ? 'Sign Up' : 'Sign In')}
        </button>

        <div className="relative my-3 text-center">
          <div className="absolute inset-0 flex items-center"><div className="w-full border-t border-slate-200" /></div>
          <span className="relative px-2 bg-white text-[11px] font-semibold text-slate-400">OR</span>
        </div>

        <button
          type="button"
          onClick={handleGuestLogin}
          className="w-full py-2 text-xs font-bold text-slate-700 bg-slate-100 hover:bg-slate-200 rounded-xl transition-colors"
        >
          ⚡ Instant Guest Access
        </button>

        <div className="text-center pt-2">
          <button
            type="button"
            onClick={() => {
              setIsRegister(!isRegister);
              setError('');
            }}
            className="text-xs font-semibold text-indigo-600 hover:underline"
          >
            {isRegister ? 'Already have an account? Sign In' : "Don't have an account? Sign Up"}
          </button>
        </div>

      </form>
    </Modal>
  );
};
