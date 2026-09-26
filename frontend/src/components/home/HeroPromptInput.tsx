import React, { useState } from 'react';
import { Sparkles, ArrowRight } from 'lucide-react';

interface HeroPromptInputProps {
  onGenerate: (prompt: string, pageCount: number) => void;
  isLoading: boolean;
}

export const HeroPromptInput: React.FC<HeroPromptInputProps> = ({
  onGenerate,
  isLoading,
}) => {
  const [prompt, setPrompt] = useState('');
  const [selectedPageOption, setSelectedPageOption] = useState<'1' | '2' | '3' | '4' | 'custom'>('2');
  const [customPageCount, setCustomPageCount] = useState<number>(5);

  const getEffectivePageCount = (): number => {
    if (selectedPageOption === 'custom') {
      return Math.max(1, Math.min(10, customPageCount || 1));
    }
    return parseInt(selectedPageOption, 10);
  };

  const handleSubmit = (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    if (!prompt.trim() || isLoading) return;
    onGenerate(prompt.trim(), getEffectivePageCount());
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
      <div className="bg-white rounded-2xl shadow-xl shadow-slate-200/50 border border-slate-200 p-5 sm:p-7 transition-all focus-within:border-[#1e3a8a] focus-within:ring-4 focus-within:ring-blue-900/10">
        <form onSubmit={handleSubmit} className="space-y-5">
          {/* 1. Topic Textarea */}
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

          {/* 2. Number of Pages Control */}
          <div className="pt-4 border-t border-slate-100 flex flex-col sm:flex-row sm:items-center justify-between gap-3">
            <div className="flex items-center gap-2">
              <span className="text-sm font-bold text-slate-700">
                Number of Pages:
              </span>
            </div>

            <div className="flex flex-wrap items-center gap-2">
              {(['1', '2', '3', '4'] as const).map((count) => (
                <button
                  key={count}
                  type="button"
                  onClick={() => setSelectedPageOption(count)}
                  className={`px-3.5 py-1.5 rounded-lg text-xs sm:text-sm font-bold transition-all ${
                    selectedPageOption === count
                      ? 'bg-[#1e3a8a] text-white shadow-sm scale-105'
                      : 'bg-slate-100 text-slate-700 hover:bg-slate-200 border border-slate-200/60'
                  }`}
                >
                  {count} {count === '1' ? 'Page' : 'Pages'}
                </button>
              ))}

              <button
                type="button"
                onClick={() => setSelectedPageOption('custom')}
                className={`px-3.5 py-1.5 rounded-lg text-xs sm:text-sm font-bold transition-all ${
                  selectedPageOption === 'custom'
                    ? 'bg-[#1e3a8a] text-white shadow-sm scale-105'
                    : 'bg-slate-100 text-slate-700 hover:bg-slate-200 border border-slate-200/60'
                }`}
              >
                Custom
              </button>

              {selectedPageOption === 'custom' && (
                <div className="flex items-center gap-1.5 ml-1 animate-in fade-in zoom-in-95 duration-150">
                  <input
                    type="number"
                    min={1}
                    max={10}
                    value={customPageCount}
                    onChange={(e) => setCustomPageCount(Math.max(1, Math.min(10, parseInt(e.target.value, 10) || 1)))}
                    className="w-16 px-2.5 py-1 text-sm font-bold text-slate-900 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-900 focus:border-blue-900"
                  />
                  <span className="text-xs text-slate-500 font-semibold">pages</span>
                </div>
              )}
            </div>
          </div>

          {/* 3. Generate Notes Button */}
          <div className="pt-3 flex items-center justify-center sm:justify-end">
            <button
              type="submit"
              disabled={!prompt.trim() || isLoading}
              className={`w-full sm:w-auto flex items-center justify-center gap-2 px-8 py-3.5 rounded-xl font-bold text-sm text-white shadow-md transition-all active:scale-95 ${
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
