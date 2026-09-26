import React, { useState } from 'react';
import { Search, BookOpen, Trash2, Edit2, Download, X, Clock, Layers, ArrowRight } from 'lucide-react';
import { NoteDocument } from '../../types';

interface HistoryDrawerProps {
  isOpen: boolean;
  onClose: () => void;
  documents: NoteDocument[];
  onSelectDocument: (id: number) => void;
  onDeleteDocument: (id: number) => void;
  onRenameDocument: (id: number, newTitle: string) => void;
  onSearch: (query: string) => void;
  onDownloadPdf: (id: number) => void;
}

export const HistoryDrawer: React.FC<HistoryDrawerProps> = ({
  isOpen,
  onClose,
  documents,
  onSelectDocument,
  onDeleteDocument,
  onRenameDocument,
  onSearch,
  onDownloadPdf,
}) => {
  const [searchQuery, setSearchQuery] = useState('');
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editTitle, setEditTitle] = useState('');

  if (!isOpen) return null;

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchQuery(e.target.value);
    onSearch(e.target.value);
  };

  const startRename = (doc: NoteDocument) => {
    setEditingId(doc.id);
    setEditTitle(doc.title);
  };

  const saveRename = (id: number) => {
    if (editTitle.trim()) {
      onRenameDocument(id, editTitle.trim());
    }
    setEditingId(null);
  };

  return (
    <div className="fixed inset-0 z-50 overflow-hidden bg-slate-900/40 backdrop-blur-xs flex justify-end animate-in fade-in duration-200">
      <div 
        className="w-full max-w-md bg-white h-full shadow-2xl flex flex-col border-l border-slate-200 animate-in slide-in-from-right duration-300"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="p-4 sm:p-5 border-b border-slate-100 flex items-center justify-between bg-slate-50/70">
          <div className="flex items-center gap-2.5">
            <img
              src="/logo-icon.png"
              alt="AI Visual Notes Logo"
              className="w-7 h-7 object-contain"
            />
            <div>
              <h3 className="font-extrabold text-sm sm:text-base text-slate-900 leading-tight">My Notes</h3>
              <p className="text-[10px] font-bold text-blue-600">AI Visual Notes</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 text-slate-400 hover:text-slate-600 hover:bg-slate-100 rounded-lg"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Search Bar */}
        <div className="p-4 border-b border-slate-100">
          <div className="relative">
            <Search className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              value={searchQuery}
              onChange={handleSearchChange}
              placeholder="Search previous notes by topic..."
              className="w-full pl-9 pr-3 py-2 text-xs font-medium rounded-xl border border-slate-200 bg-slate-50/50 focus:bg-white focus:ring-2 focus:ring-indigo-500"
            />
          </div>
        </div>

        {/* Notes List */}
        <div className="flex-1 overflow-y-auto p-4 space-y-3">
          {documents.length === 0 ? (
            <div className="text-center py-12 text-slate-400">
              <BookOpen className="w-8 h-8 mx-auto mb-2 opacity-40" />
              <p className="text-xs font-medium">No saved study notes found</p>
            </div>
          ) : (
            documents.map((doc) => (
              <div
                key={doc.id}
                className="group relative p-3.5 rounded-xl border border-slate-200 hover:border-indigo-400 hover:shadow-xs transition-all bg-white"
              >
                <div className="flex items-center justify-between gap-2 mb-1.5">
                  <span className="text-[10px] font-bold px-2 py-0.5 rounded bg-indigo-50 text-indigo-700">
                    {doc.style || 'Handwritten'}
                  </span>
                  <span className="text-[10px] font-semibold text-slate-400">
                    {doc.pageCount} {doc.pageCount === 1 ? 'Page' : 'Pages'}
                  </span>
                </div>

                {editingId === doc.id ? (
                  <div className="flex items-center gap-1.5 my-1">
                    <input
                      type="text"
                      value={editTitle}
                      onChange={(e) => setEditTitle(e.target.value)}
                      autoFocus
                      className="text-xs font-bold px-2 py-1 border border-indigo-400 rounded w-full"
                    />
                    <button
                      onClick={() => saveRename(doc.id)}
                      className="px-2 py-1 text-[10px] font-bold bg-indigo-600 text-white rounded"
                    >
                      Save
                    </button>
                  </div>
                ) : (
                  <h4 
                    onClick={() => {
                      onSelectDocument(doc.id);
                      onClose();
                    }}
                    className="text-xs sm:text-sm font-bold text-slate-900 hover:text-indigo-600 cursor-pointer line-clamp-1"
                  >
                    {doc.title}
                  </h4>
                )}

                <p className="text-[11px] text-slate-500 line-clamp-1 mt-0.5">
                  {doc.originalPrompt}
                </p>

                <div className="mt-3 pt-2 border-t border-slate-100 flex items-center justify-between">
                  <span className="text-[10px] text-slate-400">
                    {new Date(doc.createdAt).toLocaleDateString(undefined, { month: 'short', day: 'numeric' })}
                  </span>

                  <div className="flex items-center gap-1">
                    <button
                      onClick={() => onDownloadPdf(doc.id)}
                      className="p-1 text-slate-400 hover:text-indigo-600 rounded"
                      title="Download PDF"
                    >
                      <Download className="w-3.5 h-3.5" />
                    </button>
                    <button
                      onClick={() => startRename(doc)}
                      className="p-1 text-slate-400 hover:text-amber-600 rounded"
                      title="Rename"
                    >
                      <Edit2 className="w-3.5 h-3.5" />
                    </button>
                    <button
                      onClick={() => onDeleteDocument(doc.id)}
                      className="p-1 text-slate-400 hover:text-rose-600 rounded"
                      title="Delete"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </button>
                  </div>
                </div>

              </div>
            ))
          )}
        </div>

      </div>
    </div>
  );
};
