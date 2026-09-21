import React from 'react';
import {
  Download,
  Share2,
  RefreshCw,
  Edit3,
  ZoomIn,
  ZoomOut,
  Maximize2,
  ChevronLeft,
  ChevronRight,
  FileImage,
  FileText,
  Printer
} from 'lucide-react';
import { NoteStyle } from '../../types';

interface PageToolbarProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
  zoom: number;
  onZoomChange: (zoom: number) => void;
  onOpenRegenerate: () => void;
  onOpenEditor: () => void;
  onDownloadPdf: () => void;
  onDownloadPng: () => void;
  onShare: () => void;
  onPrint: () => void;
  isExporting: boolean;
  styles: NoteStyle[];
  currentStyle: NoteStyle;
  onStyleChange: (style: NoteStyle) => void;
}

export const PageToolbar: React.FC<PageToolbarProps> = ({
  currentPage,
  totalPages,
  onPageChange,
  zoom,
  onZoomChange,
  onOpenRegenerate,
  onOpenEditor,
  onDownloadPdf,
  onDownloadPng,
  onShare,
  onPrint,
  isExporting,
  styles,
  currentStyle,
  onStyleChange,
}) => {
  return (
    <div className="sticky top-16 z-30 bg-white/95 backdrop-blur-md border-b border-slate-200 py-2.5 px-4 shadow-xs">
      <div className="max-w-7xl mx-auto flex flex-wrap items-center justify-between gap-3">
        
        {/* Left: Page Navigator & Style Switcher */}
        <div className="flex items-center gap-2 sm:gap-3">
          
          {totalPages > 1 && (
            <div className="flex items-center bg-slate-100 rounded-lg p-0.5 border border-slate-200">
              <button
                onClick={() => onPageChange(Math.max(1, currentPage - 1))}
                disabled={currentPage === 1}
                className="p-1.5 text-slate-600 hover:text-slate-900 disabled:opacity-30 disabled:cursor-not-allowed rounded"
                title="Previous Page"
              >
                <ChevronLeft className="w-4 h-4" />
              </button>
              
              <span className="px-2 text-xs font-bold text-slate-800">
                {currentPage} / {totalPages}
              </span>

              <button
                onClick={() => onPageChange(Math.min(totalPages, currentPage + 1))}
                disabled={currentPage === totalPages}
                className="p-1.5 text-slate-600 hover:text-slate-900 disabled:opacity-30 disabled:cursor-not-allowed rounded"
                title="Next Page"
              >
                <ChevronRight className="w-4 h-4" />
              </button>
            </div>
          )}

          {/* Quick Style Switcher Dropdown */}
          <select
            value={currentStyle}
            onChange={(e) => onStyleChange(e.target.value as NoteStyle)}
            className="text-xs font-bold text-slate-700 bg-slate-50 border border-slate-200 rounded-lg px-2.5 py-1.5 focus:ring-2 focus:ring-indigo-500"
          >
            {styles.map((s) => (
              <option key={s} value={s}>
                Style: {s}
              </option>
            ))}
          </select>

        </div>

        {/* Center: Zoom Controls */}
        <div className="hidden md:flex items-center gap-1 bg-slate-100 p-1 rounded-lg border border-slate-200">
          <button
            onClick={() => onZoomChange(Math.max(50, zoom - 15))}
            className="p-1 text-slate-600 hover:text-slate-900 rounded"
            title="Zoom Out"
          >
            <ZoomOut className="w-3.5 h-3.5" />
          </button>
          
          <span className="text-[11px] font-bold text-slate-700 w-12 text-center">
            {zoom}%
          </span>

          <button
            onClick={() => onZoomChange(Math.min(150, zoom + 15))}
            className="p-1 text-slate-600 hover:text-slate-900 rounded"
            title="Zoom In"
          >
            <ZoomIn className="w-3.5 h-3.5" />
          </button>

          <button
            onClick={() => onZoomChange(100)}
            className="p-1 text-slate-500 hover:text-slate-800 rounded ml-1 text-[10px] font-semibold"
            title="Reset Zoom"
          >
            100%
          </button>
        </div>

        {/* Right: Actions (Regenerate, Edit, Download, Share) */}
        <div className="flex items-center gap-1.5 sm:gap-2">
          
          <button
            onClick={onOpenRegenerate}
            className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-bold text-amber-800 bg-amber-50 hover:bg-amber-100 border border-amber-200 rounded-lg transition-colors"
            title="Regenerate this specific page"
          >
            <RefreshCw className="w-3.5 h-3.5" />
            <span className="hidden sm:inline">Regenerate Page</span>
          </button>

          <button
            onClick={onOpenEditor}
            className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-bold text-slate-700 bg-slate-100 hover:bg-slate-200 border border-slate-200 rounded-lg transition-colors"
            title="Edit page text & formulas"
          >
            <Edit3 className="w-3.5 h-3.5" />
            <span className="hidden sm:inline">Edit Notes</span>
          </button>

          <button
            onClick={onDownloadPdf}
            disabled={isExporting}
            className="flex items-center gap-1.5 px-3 sm:px-3.5 py-1.5 text-xs font-bold text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 rounded-lg shadow-xs transition-all active:scale-95"
            title="Download full multi-page PDF"
          >
            <Download className="w-3.5 h-3.5" />
            <span>PDF</span>
          </button>

          <button
            onClick={onDownloadPng}
            disabled={isExporting}
            className="p-1.5 text-slate-600 hover:text-indigo-600 hover:bg-indigo-50 border border-slate-200 rounded-lg transition-colors"
            title="Download high-res PNG image"
          >
            <FileImage className="w-4 h-4" />
          </button>

          <button
            onClick={onShare}
            className="p-1.5 text-slate-600 hover:text-indigo-600 hover:bg-indigo-50 border border-slate-200 rounded-lg transition-colors"
            title="Share notes"
          >
            <Share2 className="w-4 h-4" />
          </button>

        </div>

      </div>
    </div>
  );
};
