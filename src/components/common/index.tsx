import React from 'react';
import { BookOpen, Sparkles, History, LogIn, LogOut, User as UserIcon, CheckCircle2, AlertCircle, Info, X } from 'lucide-react';
import { User } from '../../types';

export interface ToastMessage {
  id: string;
  type: 'success' | 'error' | 'info';
  message: string;
}

interface NavbarProps {
  user: User | null;
  onOpenAuth: () => void;
  onOpenHistory: () => void;
  onLogout: () => void;
  onNewNotes: () => void;
}

export function Navbar({ user, onOpenAuth, onOpenHistory, onLogout, onNewNotes }: NavbarProps) {
  return (
    <header className="sticky top-0 z-40 bg-white/95 backdrop-blur-md border-b border-slate-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 h-16 flex items-center justify-between">
        {/* Brand */}
        <button
          onClick={onNewNotes}
          className="flex items-center gap-2.5 text-left group focus:outline-none"
        >
          <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-indigo-600 to-violet-500 flex items-center justify-center text-white shadow-md shadow-indigo-100 group-hover:scale-105 transition-transform">
            <BookOpen className="w-5 h-5" />
          </div>
          <div>
            <div className="flex items-center gap-1.5">
              <span className="font-bold text-lg text-slate-900 tracking-tight">AI Visual Notes</span>
              <span className="inline-flex items-center px-1.5 py-0.5 rounded text-[10px] font-semibold bg-indigo-50 text-indigo-700 border border-indigo-200">
                Handwritten
              </span>
            </div>
            <p className="text-xs text-slate-500 hidden sm:block">Exam Revision & Vector Diagrams</p>
          </div>
        </button>

        {/* Actions */}
        <div className="flex items-center gap-2 sm:gap-3">
          <button
            onClick={onNewNotes}
            className="flex items-center gap-1.5 px-3 py-1.5 text-sm font-medium text-indigo-700 bg-indigo-50 hover:bg-indigo-100 rounded-lg transition-colors"
          >
            <Sparkles className="w-4 h-4 text-indigo-600" />
            <span className="hidden sm:inline">New Notes</span>
          </button>

          <button
            onClick={onOpenHistory}
            className="flex items-center gap-1.5 px-3 py-1.5 text-sm font-medium text-slate-700 hover:text-slate-900 hover:bg-slate-100 rounded-lg transition-colors"
            title="Saved Notes"
          >
            <History className="w-4 h-4 text-slate-500" />
            <span className="hidden sm:inline">My Library</span>
          </button>

          {user ? (
            <div className="flex items-center gap-2 pl-2 border-l border-slate-200">
              <div className="flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-slate-100 text-xs font-medium text-slate-700">
                <UserIcon className="w-3.5 h-3.5 text-slate-500" />
                <span className="max-w-[100px] truncate">{user.name}</span>
              </div>
              <button
                onClick={onLogout}
                className="p-1.5 text-slate-500 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                title="Sign out"
              >
                <LogOut className="w-4 h-4" />
              </button>
            </div>
          ) : (
            <button
              onClick={onOpenAuth}
              className="flex items-center gap-1.5 px-3 py-1.5 text-sm font-medium text-slate-700 hover:text-slate-900 hover:bg-slate-100 rounded-lg transition-colors"
            >
              <LogIn className="w-4 h-4 text-slate-500" />
              <span>Sign In</span>
            </button>
          )}
        </div>
      </div>
    </header>
  );
}

export function Footer() {
  return (
    <footer className="mt-auto border-t border-slate-200 bg-white py-6">
      <div className="max-w-7xl mx-auto px-4 text-center text-xs text-slate-500 space-y-1">
        <p className="font-medium text-slate-700">AI Visual Notes &bull; Handwritten Study Sheet Engine</p>
        <p>Interactive algorithm traces, formula breakdowns, and university exam pointers</p>
      </div>
    </footer>
  );
}

interface ToastContainerProps {
  toasts: ToastMessage[];
  onRemove: (id: string) => void;
}

export function ToastContainer({ toasts, onRemove }: ToastContainerProps) {
  if (toasts.length === 0) return null;

  return (
    <div className="fixed bottom-5 right-5 z-50 flex flex-col gap-2 max-w-sm w-full pointer-events-none">
      {toasts.map((toast) => {
        const isSuccess = toast.type === 'success';
        const isError = toast.type === 'error';
        return (
          <div
            key={toast.id}
            className={`pointer-events-auto flex items-center justify-between p-3.5 rounded-xl shadow-lg border backdrop-blur-sm transition-all animate-in slide-in-from-bottom-2 ${
              isSuccess
                ? 'bg-emerald-50/95 border-emerald-300 text-emerald-900'
                : isError
                ? 'bg-rose-50/95 border-rose-300 text-rose-900'
                : 'bg-slate-900/95 border-slate-700 text-white'
            }`}
          >
            <div className="flex items-center gap-2.5 text-sm font-medium">
              {isSuccess && <CheckCircle2 className="w-4 h-4 text-emerald-600 shrink-0" />}
              {isError && <AlertCircle className="w-4 h-4 text-rose-600 shrink-0" />}
              {!isSuccess && !isError && <Info className="w-4 h-4 text-sky-400 shrink-0" />}
              <span>{toast.message}</span>
            </div>
            <button
              onClick={() => onRemove(toast.id)}
              className="ml-3 p-1 rounded-md opacity-70 hover:opacity-100 transition-opacity"
            >
              <X className="w-3.5 h-3.5" />
            </button>
          </div>
        );
      })}
    </div>
  );
}
