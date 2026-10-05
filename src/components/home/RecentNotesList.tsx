import React from 'react';
import { BookOpen, Calendar, Trash2, ChevronRight, FileText } from 'lucide-react';
import { NoteDocument } from '../../types';

interface RecentNotesListProps {
  documents: NoteDocument[];
  onSelectDocument: (id: number) => void;
  onDeleteDocument: (id: number) => void;
}

export function RecentNotesList({
  documents,
  onSelectDocument,
  onDeleteDocument,
}: RecentNotesListProps) {
  if (documents.length === 0) {
    return null;
  }

  return (
    <div className="max-w-4xl mx-auto mt-14 space-y-4">
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-bold text-slate-800 flex items-center gap-2">
          <BookOpen className="w-5 h-5 text-indigo-600" />
          Recent Note Sets
        </h2>
        <span className="text-xs text-slate-500 font-medium">
          {documents.length} document{documents.length === 1 ? '' : 's'} stored
        </span>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {documents.map((doc) => (
          <div
            key={doc.id}
            onClick={() => onSelectDocument(doc.id)}
            className="group relative bg-white border border-slate-200 hover:border-indigo-300 rounded-xl p-4 shadow-sm hover:shadow-md transition-all cursor-pointer flex flex-col justify-between"
          >
            <div>
              <div className="flex items-start justify-between gap-3 mb-2">
                <h3 className="font-semibold text-slate-900 group-hover:text-indigo-600 transition-colors line-clamp-1">
                  {doc.title}
                </h3>
                <span className="shrink-0 text-[10px] font-semibold uppercase px-2 py-0.5 rounded-full bg-slate-100 text-slate-600 group-hover:bg-indigo-50 group-hover:text-indigo-700 transition-colors">
                  {doc.style || 'Handwritten'}
                </span>
              </div>
              <p className="text-xs text-slate-500 line-clamp-2 mb-3">
                {doc.originalPrompt}
              </p>
            </div>

            <div className="pt-2 border-t border-slate-100 flex items-center justify-between text-xs text-slate-400">
              <div className="flex items-center gap-3">
                <span className="flex items-center gap-1">
                  <FileText className="w-3.5 h-3.5 text-slate-400" />
                  {doc.pageCount || doc.pages?.length || 1} {doc.pageCount === 1 ? 'Page' : 'Pages'}
                </span>
                <span className="flex items-center gap-1">
                  <Calendar className="w-3.5 h-3.5 text-slate-400" />
                  {new Date(doc.createdAt).toLocaleDateString(undefined, { month: 'short', day: 'numeric' })}
                </span>
              </div>

              <div className="flex items-center gap-1">
                <button
                  type="button"
                  onClick={(e) => {
                    e.stopPropagation();
                    if (confirm(`Delete '${doc.title}'?`)) {
                      onDeleteDocument(doc.id);
                    }
                  }}
                  className="p-1 rounded text-slate-400 hover:text-rose-600 hover:bg-rose-50 transition-colors"
                  title="Delete Document"
                >
                  <Trash2 className="w-3.5 h-3.5" />
                </button>
                <ChevronRight className="w-4 h-4 text-slate-300 group-hover:text-indigo-600 group-hover:translate-x-0.5 transition-all" />
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
