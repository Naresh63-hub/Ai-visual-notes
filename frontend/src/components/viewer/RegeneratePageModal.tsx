import React, { useState } from 'react';
import { RefreshCw, Sparkles, ArrowRight, Wand2 } from 'lucide-react';
import { Modal } from '../common/Modal';
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

export const RegeneratePageModal: React.FC<RegeneratePageModalProps> = ({
  isOpen,
  onClose,
  pageNumber,
  topicTitle,
  onRegenerate,
  isRegenerating,
  currentStyle,
}) => {
  const [selectedInstruction, setSelectedInstruction] = useState('Make exam-oriented');
  const [customModifier, setCustomModifier] = useState('');
  const [selectedStyle, setSelectedStyle] = useState<NoteStyle>(currentStyle);

  const quickInstructions = [
    { label: 'Make Exam-Oriented', text: 'Make exam-oriented with high-yield tips and mnemonics' },
    { label: 'Simplify Explanation', text: 'Make it simpler and beginner-friendly' },
    { label: 'Add More Detail', text: 'Add more in-depth explanation and practical applications' },
    { label: 'Enhance Diagram', text: 'Improve diagram visual clarity and labeled elements' },
    { label: 'Make Concise', text: 'Reduce content and focus strictly on core bullet points' },
    { label: 'B.Tech Specific', text: 'Explain for B.Tech engineering semester exam format' }
  ];

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onRegenerate(selectedInstruction, customModifier.trim(), selectedStyle);
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={`Regenerate Page ${pageNumber}`}
      subtitle={`Topic: ${topicTitle}`}
      maxWidth="md"
    >
      <form onSubmit={handleSubmit} className="space-y-4">
        
        <p className="text-xs text-slate-600">
          Only <strong>Page {pageNumber}</strong> will be regenerated. Your other pages will remain untouched and safe.
        </p>

        {/* Quick Instructions Chips */}
        <div>
          <label className="text-xs font-bold text-slate-700 uppercase tracking-wider block mb-2">
            Choose Improvement Goal
          </label>
          <div className="grid grid-cols-2 gap-2">
            {quickInstructions.map((item, idx) => (
              <button
                type="button"
                key={idx}
                onClick={() => setSelectedInstruction(item.text)}
                className={`p-2.5 rounded-xl border text-left transition-all text-xs font-semibold ${
                  selectedInstruction === item.text
                    ? 'border-amber-500 bg-amber-50 text-amber-950 ring-2 ring-amber-500/20'
                    : 'border-slate-200 bg-slate-50/60 text-slate-700 hover:bg-slate-100'
                }`}
              >
                {item.label}
              </button>
            ))}
          </div>
        </div>

        {/* Custom Modifier Textarea */}
        <div>
          <label className="text-xs font-bold text-slate-700 uppercase tracking-wider block mb-1.5">
            Custom Instruction (Optional)
          </label>
          <textarea
            value={customModifier}
            onChange={(e) => setCustomModifier(e.target.value)}
            placeholder="e.g., 'Focus more on the mathematical derivation and edge cases'"
            rows={2}
            className="w-full text-xs p-2.5 rounded-xl border border-slate-200 focus:ring-2 focus:ring-amber-500 focus:border-amber-500 resize-none"
          />
        </div>

        {/* Footer */}
        <div className="pt-3 border-t border-slate-100 flex items-center justify-end gap-2">
          <button
            type="button"
            onClick={onClose}
            className="px-4 py-2 text-xs font-semibold text-slate-600 hover:bg-slate-100 rounded-lg transition-colors"
          >
            Cancel
          </button>
          
          <button
            type="submit"
            disabled={isRegenerating}
            className="flex items-center gap-1.5 px-5 py-2 text-xs sm:text-sm font-bold text-white bg-amber-600 hover:bg-amber-700 disabled:opacity-50 rounded-xl shadow-md shadow-amber-600/20 transition-all active:scale-95"
          >
            <RefreshCw className={`w-3.5 h-3.5 ${isRegenerating ? 'animate-spin' : ''}`} />
            <span>{isRegenerating ? 'Regenerating...' : 'Regenerate Page'}</span>
          </button>
        </div>

      </form>
    </Modal>
  );
};
