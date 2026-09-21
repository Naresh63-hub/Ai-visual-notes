import React from 'react';
import { Sparkles, Heart } from 'lucide-react';

export const Footer: React.FC = () => {
  return (
    <footer className="mt-16 border-t border-slate-200/80 bg-white py-8 text-center text-xs text-slate-500">
      <div className="max-w-7xl mx-auto px-4 flex flex-col sm:flex-row items-center justify-between gap-3">
        <div className="flex items-center gap-1.5 font-medium">
          <Sparkles className="w-3.5 h-3.5 text-indigo-500" />
          <span>AI Visual Notes — High-Precision Visual Study Architecture</span>
        </div>
        <div className="flex items-center gap-1 text-slate-400">
          <span>Crafted with</span>
          <Heart className="w-3.5 h-3.5 text-rose-500 fill-rose-500" />
          <span>for Students, Engineers & Lifelong Learners</span>
        </div>
      </div>
    </footer>
  );
};
