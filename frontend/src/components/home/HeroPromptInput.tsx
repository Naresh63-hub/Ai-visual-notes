import React, { useState } from 'react';
import { Sparkles, ArrowRight } from 'lucide-react';

interface HeroPromptInputProps {
  onGenerate: (prompt: string) => void;
  isLoading: boolean;
}

export const HeroPromptInput: React.FC<HeroPromptInputProps> = ({
  onGenerate,
  isLoading,
}) => {
  const [prompt, setPrompt] = useState('');

  const handleSubmit = (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    if (!prompt.trim() || isLoading) return;
    onGenerate(prompt.trim());
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if ((e.ctrlKey || e.metaKey) && e.key === 'Enter') {
      e.preventDefault();
      handleSubmit();
    }
  };

  return (
    <div className="w-full max-w-3xl mx-auto">
      {/* Hero Headline & Subtitle */}
      <div className="text-center mb-8">
        <h1 className="text-3xl sm:text-5xl font-black text-slate-900 tracking-tight leading-tight">
          Turn Any Topic Into <span className="text-[#1e3a8a]">Clear Notes</span>
        </h1>
        <p className="mt-3 text-base sm:text-lg text-slate-600 font-medium">
          Create clear handwritten notes from any topic.
        </p>
      </div>

      {/* Main Clean Input Box */}
      <div className="bg-white rounded-2xl shadow-xl shadow-slate-200/50 border border-slate-200 p-4 sm:p-6 transition-all focus-within:border-blue-600 focus-within:ring-4 focus-within:ring-blue-600/10">
        <form onSubmit={handleSubmit}>
          <div className="relative">
            <textarea
              value={prompt}
              onChange={(e) => setPrompt(e.target.value)}
              onKeyDown={handleKeyDown}
              placeholder="What do you want to study?"
              rows={4}
              className="w-full text-base sm:text-lg text-slate-800 placeholder-slate-400 bg-transparent border-0 focus:ring-0 resize-y min-h-[120px] leading-relaxed font-sans"
              autoFocus
            />
          </div>

          <div className="flex items-center justify-end pt-4 mt-2 border-t border-slate-100">
            <button
              type="submit"
              disabled={!prompt.trim() || isLoading}
              className={`w-full sm:w-auto flex items-center justify-center gap-2 px-7 py-3 rounded-xl font-bold text-sm text-white shadow-md transition-all active:scale-95 ${
                !prompt.trim() || isLoading
                  ? 'bg-slate-300 cursor-not-allowed shadow-none'
                  : 'bg-[#1e3a8a] hover:bg-[#172554] shadow-blue-900/20'
              }`}
            >
              {isLoading ? (
                <>
                  <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                  <span>Creating your notes...</span>
                </>
              ) : (
                <>
                  <Sparkles className="w-4 h-4" />
                  <span>Generate Notes</span>
                  <ArrowRight className="w-4 h-4 ml-1" />
                </>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
