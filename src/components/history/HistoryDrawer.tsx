import React, { useState } from 'react';
import {
  X,
  Search,
  BookOpen,
  Calendar,
  FileText,
  Trash2,
  Edit2,
  Download,
  Check,
} from 'lucide-react';
import { NoteDocument } from '../../types';

interface HistoryDrawerProps {
  isOpen: boolean;
  onClose: () => void;
  documents: NoteDocument[];
  onSelectDocument: (id: number) => void;
  onDeleteDocument: (id: number) => void;
  onRenameDocument: (id: number, newTitle: string) => void;
  onSearch: (query: string) => void;
  onDownloadPdf?: (id: number) => void;
}

export function HistoryDrawer({
  isOpen,
  onClose,
  documents,
  onSelectDocument,
  onDeleteDocument,
  onRenameDocument,
  onSearch,
  onDownloadPdf,
}: HistoryDrawerProps) {
  const [searchQuery, setSearchQuery] = useState('');
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editingTitle, setEditingTitle] = useState('');

  if (!isOpen) return null;

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const val = e.target.value;
    setSearchQuery(val);
    onSearch(val);
  };

  const startEditing = (doc: NoteDocument, e: React.MouseEvent) => {
    e.stopPropagation();
    setEditingId(doc.id);
    setEditingTitle(doc.title);
  };

  const saveEditing = (id: number, e: React.MouseEvent) => {
    e.stopPropagation();
    if (editingTitle.trim()) {
      onRenameDocument(id, editingTitle.trim());
    }
    setEditingId(null);
  };

  return (
    <div className="fixed inset-0 z-50 overflow-hidden bg-slate-900/40 backdrop-blur-xs flex justify-end">
      <div className="w-full max-w-md bg-white h-full shadow-2xl flex flex-col animate-in slide-in-from-right duration-200">
        {/* Header */}
        <div className="p-4 sm:p-5 border-b border-slate-200 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <BookOpen className="w-5 h-5 text-indigo-600" />
            <h2 className="font-bold text-slate-900 text-lg">My Notes Library</h2>
          </div>
          <button
            onClick={onClose}
            className="p-1 rounded-lg text-slate-400 hover:text-slate-700 hover:bg-slate-100 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Search */}
        <div className="p-4 border-b border-slate-100">
          <div className="relative">
            <Search className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              value={searchQuery}
              onChange={handleSearchChange}
              placeholder="Search by topic or prompt..."
              className="w-full pl-9 pr-3 py-2 text-sm bg-slate-50 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:bg-white"
            />
          </div>
        </div>

        {/* Document list */}
        <div className="flex-1 overflow-y-auto p-4 space-y-3">
          {documents.length === 0 ? (
            <div className="text-center py-12 text-slate-400 space-y-2">
              <FileText className="w-8 h-8 mx-auto opacity-50" />
              <p className="text-sm">No notes found</p>
            </div>
          ) : (
            documents.map((doc) => (
              <div
                key={doc.id}
                onClick={() => onSelectDocument(doc.id)}
                className="group border border-slate-200 hover:border-indigo-300 rounded-xl p-3.5 hover:bg-indigo-50/30 transition-all cursor-pointer space-y-2"
              >
                <div className="flex items-start justify-between gap-2">
                  {editingId === doc.id ? (
                    <div className="flex items-center gap-1.5 flex-1" onClick={(e) => e.stopPropagation()}>
                      <input
                        type="text"
                        value={editingTitle}
                        onChange={(e) => setEditingTitle(e.target.value)}
                        className="w-full text-sm font-semibold p-1 border border-indigo-400 rounded focus:outline-none"
                        autoFocus
                      />
                      <button
                        onClick={(e) => saveEditing(doc.id, e)}
                        className="p-1 text-emerald-600 hover:bg-emerald-50 rounded"
                      >
                        <Check className="w-4 h-4" />
                      </button>
                    </div>
                  ) : (
                    <h3 className="font-semibold text-slate-900 text-sm group-hover:text-indigo-600 line-clamp-1 flex-1">
                      {doc.title}
                    </h3>
                  )}

                  <div className="flex items-center gap-1 shrink-0" onClick={(e) => e.stopPropagation()}>
                    <button
                      onClick={(e) => startEditing(doc, e)}
                      className="p-1 text-slate-400 hover:text-indigo-600 hover:bg-slate-100 rounded"
                      title="Rename"
                    >
                      <Edit2 className="w-3.5 h-3.5" />
                    </button>
                    {onDownloadPdf && (
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          onDownloadPdf(doc.id);
                        }}
                        className="p-1 text-slate-400 hover:text-indigo-600 hover:bg-slate-100 rounded"
                        title="Download PDF"
                      >
                        <Download className="w-3.5 h-3.5" />
                      </button>
                    )}
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        if (confirm(`Delete '${doc.title}'?`)) {
                          onDeleteDocument(doc.id);
                        }
                      }}
                      className="p-1 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded"
                      title="Delete"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </button>
                  </div>
                </div>

                <p className="text-xs text-slate-500 line-clamp-2">
                  {doc.originalPrompt}
                </p>

                <div className="flex items-center justify-between text-[11px] text-slate-400 pt-1 border-t border-slate-100">
                  <span className="flex items-center gap-1">
                    <FileText className="w-3 h-3" />
                    {doc.pageCount || doc.pages?.length || 1} {doc.pageCount === 1 ? 'Page' : 'Pages'}
                  </span>
                  <span className="flex items-center gap-1">
                    <Calendar className="w-3 h-3" />
                    {new Date(doc.createdAt).toLocaleDateString(undefined, { month: 'short', day: 'numeric' })}
                  </span>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
}
