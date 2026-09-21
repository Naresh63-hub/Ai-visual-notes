import React, { useState } from 'react';
import { Save, Sparkles, X, Plus, Trash2 } from 'lucide-react';
import { Modal } from '../common/Modal';
import { PageContent } from '../../types';

interface PageEditorModalProps {
  isOpen: boolean;
  onClose: () => void;
  pageNumber: number;
  initialContent: PageContent;
  onSave: (updatedContent: PageContent) => void;
  isSaving: boolean;
}

export const PageEditorModal: React.FC<PageEditorModalProps> = ({
  isOpen,
  onClose,
  pageNumber,
  initialContent,
  onSave,
  isSaving,
}) => {
  const [content, setContent] = useState<PageContent>(() => JSON.parse(JSON.stringify(initialContent)));

  React.useEffect(() => {
    setContent(JSON.parse(JSON.stringify(initialContent)));
  }, [initialContent]);

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    onSave(content);
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={`Edit Page ${pageNumber} Content`}
      subtitle="Modify definitions, formulas, examples or key takeaways"
      maxWidth="2xl"
    >
      <form onSubmit={handleSave} className="space-y-4">
        
        {/* Title */}
        <div>
          <label className="text-xs font-bold text-slate-700 uppercase tracking-wider block mb-1">
            Topic Title
          </label>
          <input
            type="text"
            value={content.topicTitle}
            onChange={(e) => setContent({ ...content, topicTitle: e.target.value })}
            className="w-full text-xs font-bold p-2.5 rounded-xl border border-slate-200 focus:ring-2 focus:ring-indigo-500"
          />
        </div>

        {/* Definition & Simple Explanation */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
          <div>
            <label className="text-xs font-bold text-slate-700 uppercase tracking-wider block mb-1">
              Definition
            </label>
            <textarea
              value={content.definition || ''}
              onChange={(e) => setContent({ ...content, definition: e.target.value })}
              rows={3}
              className="w-full text-xs p-2.5 rounded-xl border border-slate-200 focus:ring-2 focus:ring-indigo-500 resize-none"
            />
          </div>

          <div>
            <label className="text-xs font-bold text-slate-700 uppercase tracking-wider block mb-1">
              Simple Explanation
            </label>
            <textarea
              value={content.simpleExplanation || ''}
              onChange={(e) => setContent({ ...content, simpleExplanation: e.target.value })}
              rows={3}
              className="w-full text-xs p-2.5 rounded-xl border border-slate-200 focus:ring-2 focus:ring-indigo-500 resize-none"
            />
          </div>
        </div>

        {/* Formula */}
        {content.formula && (
          <div className="p-3 bg-purple-50/50 rounded-xl border border-purple-200">
            <label className="text-xs font-bold text-purple-900 uppercase tracking-wider block mb-1">
              Mathematical Formula / Equation
            </label>
            <input
              type="text"
              value={content.formula.expression || ''}
              onChange={(e) => setContent({
                ...content,
                formula: { ...content.formula!, expression: e.target.value }
              })}
              className="w-full text-xs font-mono font-bold p-2 rounded-lg border border-purple-300 focus:ring-2 focus:ring-purple-500 bg-white"
            />
          </div>
        )}

        {/* Example Output */}
        {content.example && (
          <div className="p-3 bg-amber-50/50 rounded-xl border border-amber-200">
            <label className="text-xs font-bold text-amber-900 uppercase tracking-wider block mb-1">
              Example Result / Takeaway
            </label>
            <input
              type="text"
              value={content.example.outputOrResult || ''}
              onChange={(e) => setContent({
                ...content,
                example: { ...content.example!, outputOrResult: e.target.value }
              })}
              className="w-full text-xs font-semibold p-2 rounded-lg border border-amber-300 focus:ring-2 focus:ring-amber-500 bg-white"
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
            disabled={isSaving}
            className="flex items-center gap-1.5 px-5 py-2 text-xs sm:text-sm font-bold text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 rounded-xl shadow-md shadow-indigo-600/20 transition-all active:scale-95"
          >
            <Save className="w-3.5 h-3.5" />
            <span>{isSaving ? 'Saving...' : 'Save & Re-render'}</span>
          </button>
        </div>

      </form>
    </Modal>
  );
};
