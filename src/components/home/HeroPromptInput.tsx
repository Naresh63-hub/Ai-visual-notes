import React, { useState } from 'react';
import { Sparkles, FileText, ArrowRight, Lightbulb, GraduationCap } from 'lucide-react';

interface HeroPromptInputProps {
  onGenerate: (prompt: string, pageCount?: number) => void;
  isLoading: boolean;
}

const EXAMPLE_TOPICS = [
  'Binary Search Algorithm with array pointer trace',
  'OSI 7 Layers model with protocol encapsulation',
  'Quick Sort with pivot partitioning and time complexity',
  'TCP vs UDP header structure and handshake',
  'Dijkstra Shortest Path Algorithm step-by-step',
  'SQL Joins with Venn diagrams and syntax',
];

export function HeroPromptInput({ onGenerate, isLoading }: HeroPromptInputProps) {
  const [prompt, setPrompt] = useState('');
  const [pageCount, setPageCount] = useState<number>(2);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!prompt.trim() || isLoading) return;
    onGenerate(prompt.trim(), pageCount);
  };

  const handleSelectExample = (topic: string) => {
    setPrompt(topic);
  };

  return (
    <div className="max-w-3xl mx-auto text-center space-y-6">
      {/* Badge */}
      <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-indigo-50 border border-indigo-200 text-xs font-semibold text-indigo-700">
        <Sparkles className="w-3.5 h-3.5 text-indigo-600" />
        <span>University-grade visual revision sheets & diagrams</span>
      </div>

      {/* Hero Headline */}
      <div className="space-y-2">
        <h1 className="text-3xl sm:text-5xl font-extrabold text-slate-900 tracking-tight leading-tight">
          Handwritten-Style Study Notes, <br />
          <span className="text-transparent bg-clip-text bg-gradient-to-r from-indigo-600 via-violet-600 to-purple-600">
            Powered by Visual Diagrams
          </span>
        </h1>
        <p className="text-base sm:text-lg text-slate-600 max-w-2xl mx-auto">
          Type any engineering or science topic. Get structured, notebook-ruled revision sheets with vector schematics, formulas, and high-yield exam tips.
        </p>
      </div>

      {/* Input Card */}
      <form onSubmit={handleSubmit} className="bg-white rounded-2xl shadow-xl border border-slate-200 p-4 sm:p-6 text-left space-y-4">
        <div>
          <label htmlFor="prompt-input" className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">
            Enter Topic or Question
          </label>
          <div className="relative">
            <textarea
              id="prompt-input"
              rows={3}
              value={prompt}
              onChange={(e) => setPrompt(e.target.value)}
              placeholder="e.g. Quick Sort Algorithm with pivot partitioning, worst case derivation, and code walkthrough..."
              className="w-full p-3.5 text-sm sm:text-base rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 placeholder-slate-400 resize-none"
              disabled={isLoading}
            />
          </div>
        </div>

        {/* Options Row */}
        <div className="flex flex-wrap items-center justify-between gap-4 pt-1">
          {/* Page count selector */}
          <div className="flex items-center gap-2">
            <span className="text-xs font-semibold text-slate-600 flex items-center gap-1">
              <FileText className="w-3.5 h-3.5 text-slate-400" />
              Pages:
            </span>
            <div className="inline-flex rounded-lg border border-slate-200 bg-slate-50 p-0.5">
              {[1, 2, 3, 4].map((num) => (
                <button
                  key={num}
                  type="button"
                  onClick={() => setPageCount(num)}
                  className={`px-2.5 py-1 text-xs font-semibold rounded-md transition-colors ${
                    pageCount === num
                      ? 'bg-white text-indigo-700 shadow-sm'
                      : 'text-slate-600 hover:text-slate-900'
                  }`}
                >
                  {num} {num === 1 ? 'Page' : 'Pages'}
                </button>
              ))}
            </div>
          </div>

          {/* Generate Button */}
          <button
            type="submit"
            disabled={!prompt.trim() || isLoading}
            className="flex-1 sm:flex-initial inline-flex items-center justify-center gap-2 px-6 py-2.5 bg-gradient-to-r from-indigo-600 to-violet-600 hover:from-indigo-700 hover:to-violet-700 text-white font-semibold text-sm rounded-xl shadow-md shadow-indigo-200 hover:shadow-indigo-300 disabled:opacity-50 disabled:cursor-not-allowed transition-all"
          >
            {isLoading ? (
              <>
                <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                <span>Synthesizing...</span>
              </>
            ) : (
              <>
                <GraduationCap className="w-4 h-4" />
                <span>Create Visual Notes</span>
                <ArrowRight className="w-4 h-4" />
              </>
            )}
          </button>
        </div>
      </form>

      {/* Suggested Topics */}
      <div className="text-left space-y-2 pt-2">
        <div className="flex items-center gap-1.5 text-xs font-semibold text-slate-500">
          <Lightbulb className="w-3.5 h-3.5 text-amber-500" />
          <span>Popular topics to try:</span>
        </div>
        <div className="flex flex-wrap gap-2">
          {EXAMPLE_TOPICS.map((topic, i) => (
            <button
              key={i}
              type="button"
              onClick={() => handleSelectExample(topic)}
              className="text-xs text-slate-600 bg-white hover:bg-indigo-50 hover:text-indigo-700 hover:border-indigo-200 border border-slate-200 rounded-lg px-2.5 py-1.5 transition-colors text-left"
            >
              {topic}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}
