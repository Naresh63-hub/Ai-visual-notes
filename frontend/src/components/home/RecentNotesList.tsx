import React from 'react';
import { BookOpen, Clock, FileText, ArrowRight, Trash2 } from 'lucide-react';
import { NoteDocument } from '../../types';

interface RecentNotesListProps {
  documents: NoteDocument[];
  onSelectDocument: (id: number) => void;
  onDeleteDocument: (id: number) => void;
}

export const RecentNotesList: React.FC<RecentNotesListProps> = ({
  documents,
  onSelectDocument,
  onDeleteDocument,
}) => {
  if (documents.length === 0) return null;

  return (
    <div className="w-full max-w-4xl mx-auto mt-12 pt-8 border-t border-slate-200">
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-2">
          <img
            src="/logo-icon.png"
            alt="AI Visual Notes Logo"
            className="w-4 h-4 object-contain"
          />
          <h2 className="text-sm font-bold text-slate-700 uppercase tracking-wider">
            Recent Notes
          </h2>
        </div>
        <span className="text-xs text-slate-400">{documents.length} saved</span>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
        {documents.map((doc) => (
          <div
            key={doc.id}
            className="group relative bg-white p-4 rounded-xl border border-slate-200 hover:border-indigo-400 hover:shadow-md transition-all cursor-pointer flex flex-col justify-between"
            onClick={() => onSelectDocument(doc.id)}
          >
            <div>
              <div className="flex items-center justify-between gap-2 mb-2">
                <span className="text-[10px] font-bold px-2 py-0.5 rounded bg-indigo-50 text-indigo-700">
                  {doc.style || 'Handwritten'}
                </span>
                <span className="text-[10px] font-medium text-slate-400">
                  {doc.pageCount} {doc.pageCount === 1 ? 'Page' : 'Pages'}
                </span>
              </div>
              
              <h3 className="text-sm font-bold text-slate-900 group-hover:text-indigo-600 transition-colors line-clamp-1">
                {doc.title}
              </h3>
              
              <p className="text-xs text-slate-500 line-clamp-2 mt-1">
                {doc.originalPrompt}
              </p>
            </div>

            <div className="mt-3 pt-2 border-t border-slate-100 flex items-center justify-between">
              <span className="text-[11px] text-slate-400">
                {new Date(doc.createdAt).toLocaleDateString(undefined, { month: 'short', day: 'numeric' })}
              </span>

              <div className="flex items-center gap-1">
                <button
                  type="button"
                  onClick={(e) => {
                    e.stopPropagation();
                    onDeleteDocument(doc.id);
                  }}
                  className="p-1 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded transition-colors"
                  title="Delete note"
                >
                  <Trash2 className="w-3.5 h-3.5" />
                </button>
                <div className="flex items-center gap-1 text-xs font-semibold text-indigo-600">
                  <span>Open</span>
                  <ArrowRight className="w-3 h-3 group-hover:translate-x-0.5 transition-transform" />
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
