import React from 'react';
import {
  ChevronLeft,
  ChevronRight,
  ZoomIn,
  ZoomOut,
  RotateCcw,
  Sparkles,
  Edit3,
  Download,
  Image as ImageIcon,
  Share2,
  ArrowLeft,
} from 'lucide-react';

interface PageToolbarProps {
  currentPage: number;
  totalPages: number;
  topicTitle: string;
  onPageChange: (page: number) => void;
  zoom: number;
  onZoomChange: (zoom: number) => void;
  onOpenRegenerate: () => void;
  onOpenEditor: () => void;
  onDownloadPdf: () => void;
  onDownloadPng: () => void;
  onShare: () => void;
  onBack: () => void;
  isExporting: boolean;
}

export function PageToolbar({
  currentPage,
  totalPages,
  topicTitle,
  onPageChange,
  zoom,
  onZoomChange,
  onOpenRegenerate,
  onOpenEditor,
  onDownloadPdf,
  onDownloadPng,
  onShare,
  onBack,
  isExporting,
}: PageToolbarProps) {
  return (
    <div className="sticky top-16 z-30 bg-white/95 backdrop-blur-md border-b border-slate-200 px-4 py-3 shadow-xs">
      <div className="max-w-7xl mx-auto flex flex-wrap items-center justify-between gap-3">
        {/* Left: Back & Title */}
        <div className="flex items-center gap-3 min-w-0">
          <button
            onClick={onBack}
            className="flex items-center gap-1 px-2.5 py-1.5 rounded-lg text-slate-600 hover:text-slate-900 hover:bg-slate-100 text-sm font-medium transition-colors"
          >
            <ArrowLeft className="w-4 h-4" />
            <span className="hidden sm:inline">Home</span>
          </button>
          <div className="h-4 w-px bg-slate-200 hidden sm:block" />
          <h2 className="font-bold text-slate-800 text-sm sm:text-base truncate max-w-[200px] sm:max-w-xs md:max-w-md">
            {topicTitle}
          </h2>
        </div>

        {/* Center: Pagination & Zoom */}
        <div className="flex items-center gap-2 sm:gap-4">
          {/* Page Selector */}
          <div className="flex items-center bg-slate-100 rounded-lg p-0.5">
            <button
              onClick={() => onPageChange(currentPage - 1)}
              disabled={currentPage <= 1}
              className="p-1 rounded-md text-slate-600 hover:text-slate-900 hover:bg-white disabled:opacity-30 disabled:hover:bg-transparent transition-all"
              title="Previous Page"
            >
              <ChevronLeft className="w-4 h-4" />
            </button>
            <span className="px-2 text-xs font-semibold text-slate-700 select-none">
              {currentPage} / {totalPages}
            </span>
            <button
              onClick={() => onPageChange(currentPage + 1)}
              disabled={currentPage >= totalPages}
              className="p-1 rounded-md text-slate-600 hover:text-slate-900 hover:bg-white disabled:opacity-30 disabled:hover:bg-transparent transition-all"
              title="Next Page"
            >
              <ChevronRight className="w-4 h-4" />
            </button>
          </div>

          {/* Zoom controls */}
          <div className="hidden sm:flex items-center bg-slate-100 rounded-lg p-0.5 text-xs font-medium text-slate-600">
            <button
              onClick={() => onZoomChange(Math.max(50, zoom - 10))}
              className="p-1 rounded-md hover:text-slate-900 hover:bg-white transition-colors"
              title="Zoom Out"
            >
              <ZoomOut className="w-3.5 h-3.5" />
            </button>
            <span className="px-1.5 select-none">{zoom}%</span>
            <button
              onClick={() => onZoomChange(Math.min(150, zoom + 10))}
              className="p-1 rounded-md hover:text-slate-900 hover:bg-white transition-colors"
              title="Zoom In"
            >
              <ZoomIn className="w-3.5 h-3.5" />
            </button>
            {zoom !== 100 && (
              <button
                onClick={() => onZoomChange(100)}
                className="p-1 rounded-md hover:text-slate-900 hover:bg-white transition-colors ml-0.5"
                title="Reset Zoom"
              >
                <RotateCcw className="w-3 h-3" />
              </button>
            )}
          </div>
        </div>

        {/* Right: Actions */}
        <div className="flex items-center gap-1.5 sm:gap-2">
          {/* Edit Page */}
          <button
            onClick={onOpenEditor}
            className="flex items-center gap-1 px-2.5 py-1.5 rounded-lg border border-slate-200 text-xs font-semibold text-slate-700 hover:bg-slate-50 transition-colors"
            title="Edit Page Content"
          >
            <Edit3 className="w-3.5 h-3.5 text-slate-500" />
            <span className="hidden md:inline">Edit</span>
          </button>

          {/* Regenerate Page */}
          <button
            onClick={onOpenRegenerate}
            className="flex items-center gap-1 px-2.5 py-1.5 rounded-lg border border-indigo-200 text-xs font-semibold text-indigo-700 bg-indigo-50/50 hover:bg-indigo-50 transition-colors"
            title="Regenerate this page with new instructions"
          >
            <Sparkles className="w-3.5 h-3.5 text-indigo-600" />
            <span className="hidden md:inline">Refine</span>
          </button>

          {/* Export PNG */}
          <button
            onClick={onDownloadPng}
            disabled={isExporting}
            className="p-1.5 sm:px-2.5 sm:py-1.5 rounded-lg border border-slate-200 text-xs font-semibold text-slate-700 hover:bg-slate-50 transition-colors flex items-center gap-1"
            title="Download Page as PNG Image"
          >
            <ImageIcon className="w-3.5 h-3.5 text-slate-500" />
            <span className="hidden lg:inline">PNG</span>
          </button>

          {/* Export PDF */}
          <button
            onClick={onDownloadPdf}
            disabled={isExporting}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-semibold shadow-xs disabled:opacity-50 transition-all"
            title="Download complete notes as PDF"
          >
            <Download className="w-3.5 h-3.5" />
            <span>{isExporting ? 'Exporting...' : 'PDF'}</span>
          </button>

          {/* Share */}
          <button
            onClick={onShare}
            className="p-1.5 rounded-lg text-slate-500 hover:text-slate-800 hover:bg-slate-100 transition-colors"
            title="Share Notes"
          >
            <Share2 className="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  );
}
