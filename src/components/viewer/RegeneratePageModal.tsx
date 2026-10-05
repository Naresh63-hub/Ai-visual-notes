import React, { useState } from 'react';
import { X, Sparkles, Wand2 } from 'lucide-react';
import { NoteStyle } from '../../types';

interface RegeneratePageModalProps {
  isOpen: boolean;
  onClose: () => void;
  pageNumber: number;
  topicTitle: string;
  onRegenerate: (instruction: string, customModifier: string, style?: NoteStyle) => void;
  isRegenerating: boolean;
  currentStyle: NoteStyle;
}

const PRESET_INSTRUCTIONS = [
  'Add more comprehensive step-by-step worked examples',
  'Simplify the explanation for complete beginners (ELI5 style)',
  'Include more high-yield exam formulas, derivations & mnemonics',
  'Focus deeper on worst-case edge cases and runtime trade-offs',
  'Make the content more concise and cheat-sheet oriented',
];

export function RegeneratePageModal({
  isOpen,
  onClose,
  pageNumber,
  topicTitle,
  onRegenerate,
  isRegenerating,
  currentStyle,
}: RegeneratePageModalProps) {
  const [selectedPreset, setSelectedPreset] = useState<string>(PRESET_INSTRUCTIONS[0]);
  const [customModifier, setCustomModifier] = useState<string>('');

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onRegenerate(selectedPreset, customModifier, currentStyle);
  };

  return (
    <div className="fixed inset-0 z-50 bg-slate-900/50 backdrop-blur-xs flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl shadow-2xl border border-slate-200 max-w-lg w-full overflow-hidden animate-in fade-in zoom-in-95 duration-150">
        <div className="flex items-center justify-between p-4 sm:p-5 border-b border-slate-100">
          <div className="flex items-center gap-2">
            <div className="p-2 rounded-lg bg-indigo-50 text-indigo-600">
              <Sparkles className="w-5 h-5" />
            </div>
            <div>
              <h3 className="font-bold text-slate-900 text-base">Refine Page {pageNumber}</h3>
              <p className="text-xs text-slate-500 line-clamp-1">{topicTitle}</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1 rounded-lg text-slate-400 hover:text-slate-700 hover:bg-slate-100 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-4 sm:p-5 space-y-4">
          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">
              Select Refinement Focus
            </label>
            <div className="space-y-2">
              {PRESET_INSTRUCTIONS.map((preset, idx) => (
                <label
                  key={idx}
                  className={`flex items-start gap-2.5 p-3 rounded-xl border text-xs sm:text-sm cursor-pointer transition-all ${
                    selectedPreset === preset
                      ? 'border-indigo-600 bg-indigo-50/50 text-indigo-950 font-medium'
                      : 'border-slate-200 hover:border-slate-300 text-slate-700'
                  }`}
                >
                  <input
                    type="radio"
                    name="instructionPreset"
                    checked={selectedPreset === preset}
                    onChange={() => setSelectedPreset(preset)}
                    className="mt-0.5 text-indigo-600 focus:ring-indigo-500"
                  />
                  <span>{preset}</span>
                </label>
              ))}
            </div>
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">
              Additional Custom Directives (Optional)
            </label>
            <textarea
              rows={2}
              value={customModifier}
              onChange={(e) => setCustomModifier(e.target.value)}
              placeholder="e.g. Include C++ STL vector code snippet, or emphasize space complexity..."
              className="w-full p-2.5 text-sm rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500 placeholder-slate-400 resize-none"
            />
          </div>

          <div className="pt-2 flex items-center justify-end gap-2">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 text-sm font-semibold text-slate-600 hover:text-slate-800 rounded-lg hover:bg-slate-100 transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isRegenerating}
              className="flex items-center gap-1.5 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold text-sm rounded-lg shadow-sm disabled:opacity-50 transition-all"
            >
              <Wand2 className="w-4 h-4" />
              <span>{isRegenerating ? 'Updating...' : 'Regenerate Page'}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
