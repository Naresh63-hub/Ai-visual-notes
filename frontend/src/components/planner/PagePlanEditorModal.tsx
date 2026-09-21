import React, { useState } from 'react';
import { Layers, Sparkles, Wand2, Edit3, ArrowRight, RefreshCw, CheckCircle2 } from 'lucide-react';
import { Modal } from '../common/Modal';
import { PagePlan, PagePlanItem } from '../../types';

interface PagePlanEditorModalProps {
  isOpen: boolean;
  onClose: () => void;
  plan: PagePlan | null;
  onProceedToGenerate: (finalPlan: PagePlan) => void;
  onRegeneratePlan: () => void;
  isPlanning: boolean;
}

export const PagePlanEditorModal: React.FC<PagePlanEditorModalProps> = ({
  isOpen,
  onClose,
  plan,
  onProceedToGenerate,
  onRegeneratePlan,
  isPlanning,
}) => {
  const [editablePlan, setEditablePlan] = useState<PagePlan | null>(plan);

  React.useEffect(() => {
    setEditablePlan(plan);
  }, [plan]);

  if (!editablePlan) return null;

  const handlePageTitleChange = (index: number, newTitle: string) => {
    if (!editablePlan) return;
    const updatedPages = [...editablePlan.pages];
    updatedPages[index] = { ...updatedPages[index], pageTitle: newTitle };
    setEditablePlan({ ...editablePlan, pages: updatedPages });
  };

  const handleProceed = () => {
    if (editablePlan) {
      onProceedToGenerate(editablePlan);
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title="Proposed Visual Page Plan"
      subtitle={`${editablePlan.totalPages} balanced study pages generated`}
      maxWidth="2xl"
    >
      <div className="space-y-4">
        
        {/* Document summary banner */}
        <div className="p-3.5 bg-gradient-to-r from-indigo-50 to-purple-50 rounded-xl border border-indigo-100 flex items-center justify-between">
          <div>
            <span className="text-[10px] font-bold text-indigo-700 uppercase tracking-wider">
              Document Architecture
            </span>
            <h4 className="text-sm font-extrabold text-slate-900 mt-0.5">
              {editablePlan.documentTitle}
            </h4>
          </div>
          <div className="flex items-center gap-1 text-xs font-semibold px-2.5 py-1 rounded-full bg-white text-indigo-700 border border-indigo-200 shadow-2xs">
            <Layers className="w-3.5 h-3.5" />
            <span>{editablePlan.totalPages} {editablePlan.totalPages === 1 ? 'Page' : 'Pages'}</span>
          </div>
        </div>

        {/* Page List Breakdown */}
        <div className="space-y-2.5 max-h-[50vh] overflow-y-auto pr-1">
          {editablePlan.pages.map((item: PagePlanItem, idx: number) => (
            <div
              key={idx}
              className="p-3.5 rounded-xl border border-slate-200 bg-white hover:border-indigo-300 transition-all shadow-2xs"
            >
              <div className="flex items-center justify-between gap-2 mb-2">
                <div className="flex items-center gap-2">
                  <span className="w-6 h-6 rounded-md bg-indigo-600 text-white font-black text-xs flex items-center justify-center">
                    {item.pageNumber}
                  </span>
                  <input
                    type="text"
                    value={item.pageTitle}
                    onChange={(e) => handlePageTitleChange(idx, e.target.value)}
                    className="text-xs sm:text-sm font-bold text-slate-900 bg-transparent border-b border-transparent hover:border-slate-300 focus:border-indigo-500 focus:outline-hidden px-1 py-0.5"
                  />
                </div>
                <span className="text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded bg-amber-50 text-amber-800 border border-amber-200">
                  {item.plannedDiagramType || 'Diagram Included'}
                </span>
              </div>

              <div className="flex flex-wrap gap-1 mb-2">
                {item.topics.map((t, ti) => (
                  <span
                    key={ti}
                    className="text-[11px] font-medium px-2 py-0.5 rounded bg-slate-100 text-slate-700"
                  >
                    • {t}
                  </span>
                ))}
              </div>

              <p className="text-[11px] text-slate-500 italic">
                Focus: {item.focusArea}
              </p>
            </div>
          ))}
        </div>

        {/* Action Controls */}
        <div className="pt-4 border-t border-slate-100 flex items-center justify-between gap-3">
          <button
            type="button"
            onClick={onRegeneratePlan}
            disabled={isPlanning}
            className="flex items-center gap-1.5 px-3.5 py-2 text-xs font-semibold text-slate-600 hover:bg-slate-100 rounded-lg transition-colors"
          >
            <RefreshCw className={`w-3.5 h-3.5 ${isPlanning ? 'animate-spin' : ''}`} />
            <span>Regenerate Plan</span>
          </button>

          <button
            type="button"
            onClick={handleProceed}
            className="flex items-center gap-2 px-6 py-2.5 text-xs sm:text-sm font-bold text-white bg-gradient-to-r from-indigo-600 to-purple-600 hover:from-indigo-700 hover:to-purple-700 rounded-xl shadow-md shadow-indigo-600/20 transition-all active:scale-95"
          >
            <Wand2 className="w-4 h-4" />
            <span>Generate All Notes</span>
            <ArrowRight className="w-4 h-4" />
          </button>
        </div>

      </div>
    </Modal>
  );
};
