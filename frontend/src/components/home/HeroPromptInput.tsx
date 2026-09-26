import React, { useState } from 'react';
import { Sparkles, ArrowRight, Wand2, Lightbulb, BookOpen, Layers, Check } from 'lucide-react';
import { NoteStyle } from '../../types';

interface HeroPromptInputProps {
  onGenerate: (prompt: string, style: NoteStyle, detailLevel: string, audience: string) => void;
  isLoading: boolean;
}

export const HeroPromptInput: React.FC<HeroPromptInputProps> = ({
  onGenerate,
  isLoading,
}) => {
  const [prompt, setPrompt] = useState('');
  const [selectedStyle, setSelectedStyle] = useState<NoteStyle>('Handwritten');
  const [selectedPages, setSelectedPages] = useState<string>('auto');
  const [customPagesInput, setCustomPagesInput] = useState<string>('');
  const [selectedDetail, setSelectedDetail] = useState('Detailed');
  const [selectedAudience, setSelectedAudience] = useState('B.Tech / College');

  const pageOptions = [
    { id: 'auto', label: '✨ Auto (AI decides)', desc: 'Optimal pages for topic' },
    { id: '1', label: '1 Page', desc: 'Summary / cheat-sheet' },
    { id: '2', label: '2 Pages', desc: 'Detailed + worked trace' },
    { id: '3', label: '3 Pages', desc: 'In-depth multi-section' },
    { id: '4', label: '4 Pages', desc: 'Complete module' },
    { id: 'custom', label: 'Custom', desc: 'Enter page limit' },
  ];

  const detailLevels = [
    { id: 'Quick', label: 'Quick', desc: 'Core points & key formula' },
    { id: 'Normal', label: 'Normal', desc: 'Standard exam level' },
    { id: 'Detailed', label: 'Detailed (Academic)', desc: 'Full algorithm, proofs & traces' },
    { id: 'Very Detailed', label: 'Very Detailed', desc: 'Exhaustive notes with examples' },
  ];

  const examplePrompts = [
    {
      label: 'Binary Search Algorithm',
      text: 'Explain Binary Search with algorithm, example, time & space complexity, and step-by-step array partition diagram.',
      tag: 'Algorithms'
    },
    {
      label: 'Photosynthesis Cycle',
      text: 'Explain photosynthesis with a clear labeled diagram, light vs dark reactions, and the chemical equation.',
      tag: 'Biology'
    },
    {
      label: 'OSI 7-Layer Model',
      text: 'Explain the OSI Model in simple language with all 7 layers, protocols, and data encapsulation flow.',
      tag: 'Networking'
    },
    {
      label: 'DBMS Normalization',
      text: 'Explain Normalization in DBMS (1NF, 2NF, 3NF, BCNF) with anomalies and example schema transformations.',
      tag: 'Database'
    },
    {
      label: 'Quick Sort vs Merge Sort',
      text: 'Explain Quick Sort and Merge Sort for B.Tech exams in 2 pages with partition diagrams, recursion trees and recurrence relations.',
      tag: 'Multi-Topic'
    },
    {
      label: 'TCP 3-Way Handshake',
      text: 'Explain TCP Three-Way Handshake connection establishment and termination with sequence numbers and flag diagram.',
      tag: 'Protocols'
    },
    {
      label: 'Newton’s Laws of Motion',
      text: 'Explain Newton’s 3 Laws of Motion with physical diagrams, vector equations, and everyday examples.',
      tag: 'Physics'
    }
  ];

  const styles: { id: NoteStyle; name: string; desc: string; previewClass: string }[] = [
    {
      id: 'Handwritten',
      name: 'Handwritten',
      desc: 'Ruled paper, authentic handwriting font, ink doodles & highlighter boxes',
      previewClass: 'border-amber-300 bg-amber-50/70 text-amber-950 font-hand text-base font-bold'
    },
    {
      id: 'Clean Digital',
      name: 'Clean Digital',
      desc: 'Modern layout, crisp cards, clean typography & subtle borders',
      previewClass: 'border-indigo-300 bg-indigo-50/70 text-indigo-950 font-sans font-semibold'
    },
    {
      id: 'Exam Notes',
      name: 'Exam Notes',
      desc: 'High-contrast bullet cards, star markers, mnemonics & formula callouts',
      previewClass: 'border-rose-300 bg-rose-50/70 text-rose-950 font-sans font-bold'
    },
    {
      id: 'Minimal',
      name: 'Minimal',
      desc: 'Monochromatic, clean Swiss grid spacing & distraction-free structure',
      previewClass: 'border-slate-300 bg-slate-100 text-slate-900 font-mono text-xs'
    },
    {
      id: 'Colorful Study Notes',
      name: 'Colorful',
      desc: 'Pastel colored study blocks, vibrant diagram chips & cheerful accents',
      previewClass: 'border-purple-300 bg-purple-50/70 text-purple-950 font-sans font-medium'
    }
  ];

  const handleSubmit = (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    if (!prompt.trim() || isLoading) return;
    
    // If explicit page count is set, append page hint if not already in prompt
    let finalPrompt = prompt.trim();
    if (selectedPages !== 'auto') {
      const pCount = selectedPages === 'custom' ? parseInt(customPagesInput) || 1 : parseInt(selectedPages);
      if (pCount === 1 && !/in (1|one) page/i.test(finalPrompt)) {
        finalPrompt += ` (in 1 page)`;
      } else if (pCount > 1 && !/in \d+ pages/i.test(finalPrompt)) {
        finalPrompt += ` (in ${pCount} pages)`;
      }
    }

    onGenerate(finalPrompt, selectedStyle, selectedDetail, selectedAudience);
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if ((e.ctrlKey || e.metaKey) && e.key === 'Enter') {
      e.preventDefault();
      handleSubmit();
    }
  };

  return (
    <div className="w-full max-w-4xl mx-auto">
      
      {/* Hero Header */}
      <div className="text-center mb-8 sm:mb-10">
        <div className="flex justify-center mb-4">
          <div className="inline-flex items-center gap-2.5 px-4 py-1.5 rounded-full bg-blue-50/90 border border-blue-200/80 text-blue-800 text-xs sm:text-sm font-bold shadow-sm backdrop-blur-xs">
            <img
              src="/logo-icon.png"
              alt="AI Visual Notes Logo"
              className="w-5 h-5 object-contain"
            />
            <span>AI Visual Notes — Handwritten Notes</span>
          </div>
        </div>
        <h1 className="text-3xl sm:text-5xl font-black text-slate-900 tracking-tight leading-tight sm:leading-tight">
          Turn Any Topic Into <span className="bg-gradient-to-r from-blue-600 via-indigo-600 to-purple-600 bg-clip-text text-transparent">Clear Notes</span>
        </h1>
        <p className="mt-3 text-sm sm:text-base text-slate-600 max-w-2xl mx-auto font-medium">
          Generates comprehensive handwritten visual notes with structured topics, key concepts, formulas, algorithms, step-by-step traces, and clean diagrams.
        </p>
      </div>

      {/* Main Input Box */}
      <div className="bg-white rounded-2xl shadow-xl shadow-slate-200/60 border border-slate-200/90 p-4 sm:p-6 transition-all focus-within:border-blue-500 focus-within:ring-4 focus-within:ring-blue-500/10">
        <form onSubmit={handleSubmit}>
          
          <div className="relative">
            <textarea
              value={prompt}
              onChange={(e) => setPrompt(e.target.value)}
              onKeyDown={handleKeyDown}
              placeholder="What do you want to study?&#10;&#10;Examples:&#10;• &quot;Explain Binary Search with algorithm, example, complexity and diagram.&quot;&#10;• &quot;Newton's 3 Laws of Motion with vector equations and everyday examples.&quot;&#10;• &quot;Photosynthesis light and dark reactions with labeled diagram.&quot;"
              rows={4}
              className="w-full text-sm sm:text-base text-slate-800 placeholder-slate-400 bg-transparent border-0 focus:ring-0 resize-y min-h-[110px] leading-relaxed"
            />
          </div>

          {/* Target Page Count Selection */}
          <div className="pt-4 mt-2 border-t border-slate-100">
            <div className="flex items-center justify-between mb-2">
              <label className="text-xs font-bold text-slate-700 uppercase tracking-wider flex items-center gap-1.5">
                <BookOpen className="w-3.5 h-3.5 text-indigo-600" />
                <span>Page Count Strategy</span>
              </label>
              <span className="text-[11px] text-slate-400">Readability &gt; Cramming into 1 page</span>
            </div>

            <div className="grid grid-cols-2 sm:grid-cols-6 gap-1.5">
              {pageOptions.map((opt) => (
                <button
                  type="button"
                  key={opt.id}
                  onClick={() => setSelectedPages(opt.id)}
                  className={`p-2 rounded-xl border text-center transition-all ${
                    selectedPages === opt.id
                      ? 'border-indigo-600 bg-indigo-50/80 text-indigo-950 ring-2 ring-indigo-600 font-bold shadow-2xs'
                      : 'border-slate-200 bg-slate-50/50 text-slate-700 hover:bg-slate-100'
                  }`}
                >
                  <div className="text-xs font-bold">{opt.label}</div>
                  <div className="text-[9px] text-slate-500 truncate">{opt.desc}</div>
                </button>
              ))}
            </div>

            {selectedPages === 'custom' && (
              <div className="mt-2 p-2 bg-indigo-50/60 rounded-lg border border-indigo-200 flex items-center gap-2">
                <span className="text-xs font-semibold text-indigo-900">Enter page count:</span>
                <input
                  type="number"
                  min={1}
                  max={20}
                  value={customPagesInput}
                  onChange={(e) => setCustomPagesInput(e.target.value)}
                  placeholder="e.g. 2"
                  className="w-20 px-2 py-1 bg-white border border-indigo-300 rounded text-xs font-bold text-slate-900 text-center"
                />
              </div>
            )}
          </div>

          {/* Note Detail Level */}
          <div className="pt-3 mt-2 border-t border-slate-100">
            <div className="flex items-center justify-between mb-2">
              <label className="text-xs font-bold text-slate-700 uppercase tracking-wider flex items-center gap-1.5">
                <Sparkles className="w-3.5 h-3.5 text-purple-600" />
                <span>Depth & Detail Level</span>
              </label>
            </div>

            <div className="grid grid-cols-2 sm:grid-cols-4 gap-1.5">
              {detailLevels.map((lvl) => (
                <button
                  type="button"
                  key={lvl.id}
                  onClick={() => setSelectedDetail(lvl.id)}
                  className={`p-2 rounded-xl border text-left transition-all ${
                    selectedDetail === lvl.id
                      ? 'border-purple-600 bg-purple-50/80 text-purple-950 ring-2 ring-purple-600 font-bold shadow-2xs'
                      : 'border-slate-200 bg-slate-50/50 text-slate-700 hover:bg-slate-100'
                  }`}
                >
                  <div className="text-xs font-bold">{lvl.label}</div>
                  <div className="text-[9px] text-slate-500 truncate">{lvl.desc}</div>
                </button>
              ))}
            </div>
          </div>

          {/* Style Selector Section */}
          <div className="pt-3 mt-2 border-t border-slate-100">
            <div className="flex items-center justify-between mb-2.5">
              <label className="text-xs font-bold text-slate-700 uppercase tracking-wider flex items-center gap-1.5">
                <Layers className="w-3.5 h-3.5 text-indigo-600" />
                <span>Visual Note Style</span>
              </label>
              <span className="text-[11px] text-slate-400">Default: Handwritten Notebook</span>
            </div>

            <div className="grid grid-cols-2 sm:grid-cols-5 gap-2">
              {styles.map((style) => (
                <button
                  type="button"
                  key={style.id}
                  onClick={() => setSelectedStyle(style.id)}
                  className={`relative p-2.5 rounded-xl border text-left transition-all ${
                    selectedStyle === style.id
                      ? `${style.previewClass} ring-2 ring-indigo-600 shadow-sm`
                      : 'border-slate-200 bg-slate-50/60 text-slate-600 hover:bg-slate-100 hover:border-slate-300'
                  }`}
                >
                  <div className="flex items-center justify-between mb-1">
                    <span className="text-xs font-bold truncate">{style.name}</span>
                    {selectedStyle === style.id && (
                      <Check className="w-3.5 h-3.5 text-indigo-600 shrink-0" />
                    )}
                  </div>
                  <p className="text-[10px] text-slate-500 line-clamp-2 leading-tight">
                    {style.desc}
                  </p>
                </button>
              ))}
            </div>
          </div>

          {/* Audience & Submit */}
          <div className="flex flex-wrap items-center justify-between gap-3 pt-4 mt-4 border-t border-slate-100">
            <div className="flex flex-wrap items-center gap-2">
              <span className="text-xs font-semibold text-slate-500">Audience:</span>
              {['B.Tech / College', 'High School / Beginner', 'Competitive Exams'].map((aud) => (
                <button
                  type="button"
                  key={aud}
                  onClick={() => setSelectedAudience(aud)}
                  className={`text-xs px-2.5 py-1 rounded-full font-medium transition-colors ${
                    selectedAudience.includes(aud.split(' ')[0])
                      ? 'bg-indigo-100 text-indigo-800 font-semibold'
                      : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                  }`}
                >
                  {aud}
                </button>
              ))}
            </div>

            {/* Submit CTA Button */}
            <button
              type="submit"
              disabled={!prompt.trim() || isLoading}
              className={`w-full sm:w-auto flex items-center justify-center gap-2 px-6 py-3 rounded-xl font-bold text-sm text-white shadow-lg transition-all active:scale-95 ${
                !prompt.trim() || isLoading
                  ? 'bg-slate-300 cursor-not-allowed shadow-none'
                  : 'bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-700 hover:to-indigo-700 shadow-indigo-600/30'
              }`}
            >
              {isLoading ? (
                <>
                  <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                  <span>Planning & Rendering Notes...</span>
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

      {/* Example Prompt Chips */}
      <div className="mt-8">
        <div className="flex items-center gap-2 mb-3">
          <Lightbulb className="w-4 h-4 text-amber-500" />
          <h2 className="text-xs font-bold text-slate-600 uppercase tracking-wider">
            Quick Try Example Prompts
          </h2>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-2.5">
          {examplePrompts.map((ex, i) => (
            <button
              key={i}
              onClick={() => setPrompt(ex.text)}
              className="group p-3 rounded-xl bg-white border border-slate-200/80 hover:border-indigo-300 hover:shadow-md text-left transition-all flex flex-col justify-between"
            >
              <div>
                <div className="flex items-center justify-between mb-1.5">
                  <span className="text-xs font-bold text-slate-800 group-hover:text-indigo-600 transition-colors">
                    {ex.label}
                  </span>
                  <span className="text-[10px] font-semibold px-2 py-0.5 rounded-full bg-slate-100 text-slate-600">
                    {ex.tag}
                  </span>
                </div>
                <p className="text-[11px] text-slate-500 line-clamp-2">
                  {ex.text}
                </p>
              </div>
              <div className="mt-2 flex items-center gap-1 text-[11px] font-semibold text-indigo-600 opacity-0 group-hover:opacity-100 transition-opacity">
                <span>Click to use prompt</span>
                <ArrowRight className="w-3 h-3" />
              </div>
            </button>
          ))}
        </div>
      </div>

    </div>
  );
};
