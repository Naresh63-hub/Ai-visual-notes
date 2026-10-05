import React, { useState, useEffect } from 'react';
import { X, Save, Edit3 } from 'lucide-react';
import { PageContent } from '../../types';

interface PageEditorModalProps {
  isOpen: boolean;
  onClose: () => void;
  pageNumber: number;
  initialContent: PageContent;
  onSave: (content: PageContent) => void;
  isSaving: boolean;
}

export function PageEditorModal({
  isOpen,
  onClose,
  pageNumber,
  initialContent,
  onSave,
  isSaving,
}: PageEditorModalProps) {
  const [topicTitle, setTopicTitle] = useState(initialContent?.topicTitle || '');
  const [topicSubtitle, setTopicSubtitle] = useState(initialContent?.topicSubtitle || '');
  const [definition, setDefinition] = useState(initialContent?.definition || '');
  const [simpleExplanation, setSimpleExplanation] = useState(initialContent?.simpleExplanation || '');
  const [formulaExpression, setFormulaExpression] = useState(initialContent?.formula?.expression || '');
  const [timeComplexity, setTimeComplexity] = useState(initialContent?.complexity?.timeAverage || '');

  useEffect(() => {
    if (initialContent) {
      setTopicTitle(initialContent.topicTitle || '');
      setTopicSubtitle(initialContent.topicSubtitle || '');
      setDefinition(initialContent.definition || '');
      setSimpleExplanation(initialContent.simpleExplanation || '');
      setFormulaExpression(initialContent.formula?.expression || '');
      setTimeComplexity(initialContent.complexity?.timeAverage || '');
    }
  }, [initialContent]);

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const updated: PageContent = {
      ...initialContent,
      topicTitle,
      topicSubtitle,
      definition,
      simpleExplanation,
      formula: initialContent.formula
        ? { ...initialContent.formula, expression: formulaExpression }
        : formulaExpression
        ? { title: 'Mathematical Model', expression: formulaExpression }
        : undefined,
      complexity: initialContent.complexity
        ? { ...initialContent.complexity, timeAverage: timeComplexity }
        : timeComplexity
        ? { timeAverage: timeComplexity }
        : undefined,
    };
    onSave(updated);
  };

  return (
    <div className="fixed inset-0 z-50 bg-slate-900/50 backdrop-blur-xs flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl shadow-2xl border border-slate-200 max-w-xl w-full max-h-[90vh] flex flex-col overflow-hidden animate-in fade-in zoom-in-95 duration-150">
        <div className="flex items-center justify-between p-4 sm:p-5 border-b border-slate-100">
          <div className="flex items-center gap-2">
            <div className="p-2 rounded-lg bg-indigo-50 text-indigo-600">
              <Edit3 className="w-5 h-5" />
            </div>
            <div>
              <h3 className="font-bold text-slate-900 text-base">Edit Page {pageNumber}</h3>
              <p className="text-xs text-slate-500">Fine-tune headings, definitions, and equations</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1 rounded-lg text-slate-400 hover:text-slate-700 hover:bg-slate-100 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-4 sm:p-5 overflow-y-auto space-y-4 flex-1">
          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
              Topic Heading
            </label>
            <input
              type="text"
              value={topicTitle}
              onChange={(e) => setTopicTitle(e.target.value)}
              className="w-full p-2.5 text-sm rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500"
              required
            />
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
              Subtitle
            </label>
            <input
              type="text"
              value={topicSubtitle}
              onChange={(e) => setTopicSubtitle(e.target.value)}
              className="w-full p-2.5 text-sm rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
              Definition
            </label>
            <textarea
              rows={3}
              value={definition}
              onChange={(e) => setDefinition(e.target.value)}
              className="w-full p-2.5 text-sm rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500 resize-none"
            />
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
              Intuitive Explanation (ELI5)
            </label>
            <textarea
              rows={2}
              value={simpleExplanation}
              onChange={(e) => setSimpleExplanation(e.target.value)}
              className="w-full p-2.5 text-sm rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500 resize-none"
            />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                Formula Expression
              </label>
              <input
                type="text"
                value={formulaExpression}
                onChange={(e) => setFormulaExpression(e.target.value)}
                className="w-full p-2.5 text-sm font-mono rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                placeholder="e.g. O(log N)"
              />
            </div>
            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                Time Complexity
              </label>
              <input
                type="text"
                value={timeComplexity}
                onChange={(e) => setTimeComplexity(e.target.value)}
                className="w-full p-2.5 text-sm font-mono rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                placeholder="e.g. O(N log N)"
              />
            </div>
          </div>

          <div className="pt-4 border-t border-slate-100 flex items-center justify-end gap-2">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 text-sm font-semibold text-slate-600 hover:text-slate-800 rounded-lg hover:bg-slate-100 transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isSaving}
              className="flex items-center gap-1.5 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold text-sm rounded-lg shadow-sm disabled:opacity-50 transition-all"
            >
              <Save className="w-4 h-4" />
              <span>{isSaving ? 'Saving...' : 'Save Changes'}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
