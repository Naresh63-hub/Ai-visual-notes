import React, { useState } from 'react';
import { Layers, Sparkles, ArrowRight, CheckCircle2 } from 'lucide-react';
import { Modal } from '../common/Modal';
import { PromptAnalysisResponse } from '../../types';

interface TopicCountModalProps {
  isOpen: boolean;
  onClose: () => void;
  analysis: PromptAnalysisResponse | null;
  onConfirmPageCount: (pageCount: number) => void;
}

export const TopicCountModal: React.FC<TopicCountModalProps> = ({
  isOpen,
  onClose,
  analysis,
  onConfirmPageCount,
}) => {
  const [selectedCount, setSelectedCount] = useState<number>(
    analysis?.suggestedPageCount || 1
  );
  const [customCount, setCustomCount] = useState<string>('');
  const [isCustom, setIsCustom] = useState<boolean>(false);

  if (!analysis) return null;

  const handleSelectOption = (count: number) => {
    setSelectedCount(count);
    setIsCustom(false);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const finalCount = isCustom ? (parseInt(customCount) || selectedCount) : selectedCount;
    onConfirmPageCount(Math.max(1, Math.min(20, finalCount)));
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title="Intelligent Topic Distribution"
      subtitle={`Detected ${analysis.topicCount} topics in your request`}
      maxWidth="lg"
    >
      <form onSubmit={handleSubmit} className="space-y-5">
        
        {/* Detected topics pill cloud */}
        <div className="p-3.5 bg-slate-50 rounded-xl border border-slate-200/80">
          <div className="flex items-center gap-2 mb-2">
            <Sparkles className="w-4 h-4 text-indigo-600" />
            <span className="text-xs font-bold text-slate-700 uppercase tracking-wider">
              Detected Topics ({analysis.topicCount})
            </span>
          </div>
          <div className="flex flex-wrap gap-1.5">
            {analysis.detectedTopics.map((topic, i) => (
              <span
                key={i}
                className="text-xs font-semibold px-2.5 py-1 rounded-md bg-white border border-slate-200 text-slate-800 shadow-2xs"
              >
                {topic}
              </span>
            ))}
          </div>
        </div>

        {/* Question prompt */}
        <div className="text-center py-2">
          <h3 className="text-base sm:text-lg font-extrabold text-slate-900">
            How many pages would you like?
          </h3>
          <p className="text-xs text-slate-500 mt-1">
            The AI page planner will automatically balance content and diagrams across your chosen pages.
          </p>
        </div>

        {/* Options grid */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-2.5">
          
          <button
            type="button"
            onClick={() => handleSelectOption(1)}
            className={`p-3 rounded-xl border text-center transition-all ${
              selectedCount === 1 && !isCustom
                ? 'border-indigo-600 bg-indigo-50/80 text-indigo-950 ring-2 ring-indigo-600 font-bold'
                : 'border-slate-200 bg-white hover:bg-slate-50 text-slate-700'
            }`}
          >
            <div className="text-sm font-extrabold">1 Page</div>
            <div className="text-[10px] text-slate-500 mt-0.5">High-yield summary</div>
          </button>

          {analysis.topicCount > 1 && (
            <button
              type="button"
              onClick={() => handleSelectOption(Math.max(2, Math.round(analysis.topicCount / 2)))}
              className={`p-3 rounded-xl border text-center transition-all ${
                selectedCount === Math.max(2, Math.round(analysis.topicCount / 2)) && !isCustom
                  ? 'border-indigo-600 bg-indigo-50/80 text-indigo-950 ring-2 ring-indigo-600 font-bold'
                  : 'border-slate-200 bg-white hover:bg-slate-50 text-slate-700'
              }`}
            >
              <div className="text-sm font-extrabold">
                {Math.max(2, Math.round(analysis.topicCount / 2))} Pages
              </div>
              <div className="text-[10px] text-slate-500 mt-0.5">Balanced depth</div>
            </button>
          )}

          <button
            type="button"
            onClick={() => handleSelectOption(analysis.topicCount)}
            className={`p-3 rounded-xl border text-center transition-all ${
              selectedCount === analysis.topicCount && !isCustom
                ? 'border-indigo-600 bg-indigo-50/80 text-indigo-950 ring-2 ring-indigo-600 font-bold'
                : 'border-slate-200 bg-white hover:bg-slate-50 text-slate-700'
            }`}
          >
            <div className="text-sm font-extrabold">1 Per Topic</div>
            <div className="text-[10px] text-slate-500 mt-0.5">
              {analysis.topicCount} full pages
            </div>
          </button>

          <button
            type="button"
            onClick={() => setIsCustom(true)}
            className={`p-3 rounded-xl border text-center transition-all ${
              isCustom
                ? 'border-indigo-600 bg-indigo-50/80 text-indigo-950 ring-2 ring-indigo-600 font-bold'
                : 'border-slate-200 bg-white hover:bg-slate-50 text-slate-700'
            }`}
          >
            <div className="text-sm font-extrabold">Custom</div>
            <div className="text-[10px] text-slate-500 mt-0.5">Enter number</div>
          </button>

        </div>

        {/* Custom Input field if chosen */}
        {isCustom && (
          <div className="p-3 bg-indigo-50/50 rounded-xl border border-indigo-200 flex items-center justify-between gap-3">
            <span className="text-xs font-semibold text-indigo-900">
              Enter desired number of pages:
            </span>
            <input
              type="number"
              min={1}
              max={20}
              value={customCount}
              onChange={(e) => setCustomCount(e.target.value)}
              placeholder="e.g. 3"
              autoFocus
              className="w-20 px-3 py-1.5 text-sm font-bold text-center border border-indigo-300 rounded-lg bg-white focus:ring-2 focus:ring-indigo-500"
            />
          </div>
        )}

        {/* Footer Actions */}
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
            className="flex items-center gap-1.5 px-5 py-2.5 text-xs sm:text-sm font-bold text-white bg-indigo-600 hover:bg-indigo-700 rounded-xl shadow-md shadow-indigo-600/20 transition-all active:scale-95"
          >
            <span>Review Page Plan</span>
            <ArrowRight className="w-4 h-4" />
          </button>
        </div>

      </form>
    </Modal>
  );
};
