import React, { useState, useEffect } from 'react';
import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';
import {
  Navbar,
  Footer,
  ToastContainer,
  ToastMessage,
} from './components/common';
import { HeroPromptInput } from './components/home/HeroPromptInput';
import { RecentNotesList } from './components/home/RecentNotesList';
import { VisualNotePage } from './components/viewer/VisualNotePage';
import { PageToolbar } from './components/viewer/PageToolbar';
import { RegeneratePageModal } from './components/viewer/RegeneratePageModal';
import { PageEditorModal } from './components/viewer/PageEditorModal';
import { HistoryDrawer } from './components/history/HistoryDrawer';
import { AuthModal } from './components/auth/AuthModal';

import {
  NoteDocument,
  NotePage,
  NoteStyle,
  PageContent,
  User,
} from './types';
import { noteService } from './services/noteService';
import { authService } from './services/authService';

export function App() {
  // User & Auth state
  const [user, setUser] = useState<User | null>(() => authService.getStoredUser());
  const [isAuthOpen, setIsAuthOpen] = useState(false);
  const [isAuthLoading, setIsAuthLoading] = useState(false);

  // Document & Viewer state
  const [currentDocument, setCurrentDocument] = useState<NoteDocument | null>(null);
  const [currentPageIndex, setCurrentPageIndex] = useState<number>(0);
  const [zoom, setZoom] = useState<number>(100);

  // Loading & Progress states
  const [isGenerating, setIsGenerating] = useState(false);
  const [isExporting, setIsExporting] = useState(false);
  const [generationStep, setGenerationStep] = useState<string>('Understanding your topic...');

  // Modals & Drawers
  const [isRegenerateOpen, setIsRegenerateOpen] = useState(false);
  const [isEditorOpen, setIsEditorOpen] = useState(false);
  const [isHistoryOpen, setIsHistoryOpen] = useState(false);
  const [recentDocuments, setRecentDocuments] = useState<NoteDocument[]>([]);

  // Toasts
  const [toasts, setToasts] = useState<ToastMessage[]>([]);

  const addToast = (type: 'success' | 'error' | 'info', message: string) => {
    const id = Math.random().toString(36).substring(2, 9);
    setToasts((prev) => [...prev, { id, type, message }]);
  };

  const removeToast = (id: string) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  };

  // Fetch initial history
  useEffect(() => {
    loadRecentNotes();
  }, [user]);

  const loadRecentNotes = async (searchQuery?: string) => {
    try {
      const docs = await noteService.getRecentDocuments(searchQuery);
      setRecentDocuments(docs);
    } catch {
      // Ignore background fetch error
    }
  };

  // Seamless Generation: User enters topic -> AI handles everything
  const handleStartPrompt = async (prompt: string) => {
    setIsGenerating(true);
    setGenerationStep('Understanding your topic...');

    const stepTimer1 = setTimeout(() => {
      setGenerationStep('Creating your notes...');
    }, 900);

    const stepTimer2 = setTimeout(() => {
      setGenerationStep('Preparing your handwritten pages...');
    }, 1800);

    try {
      const analysisResult = await noteService.analyzePrompt(prompt, 'Handwritten');
      const pagesToPlan = analysisResult.requestedPageCount || analysisResult.suggestedPageCount || 1;
      const plan = await noteService.planPages(prompt, pagesToPlan, 'B.Tech / College', 'Handwritten', analysisResult.difficulty);

      const doc = await noteService.generateNotes({
        prompt: prompt,
        pageCount: plan.totalPages,
        style: 'Handwritten',
        audience: 'B.Tech / College',
        detailLevel: 'Detailed',
        customPlan: plan,
      });

      clearTimeout(stepTimer1);
      clearTimeout(stepTimer2);
      setCurrentDocument(doc);
      setCurrentPageIndex(0);
      setIsGenerating(false);
      loadRecentNotes();
      addToast('success', `✓ Successfully created your notes!`);
    } catch (err: any) {
      clearTimeout(stepTimer1);
      clearTimeout(stepTimer2);
      setIsGenerating(false);
      addToast('error', err.message || 'Failed to create notes');
    }
  };

  // Single Page Regeneration
  const handleRegeneratePage = async (instruction: string, customModifier: string, style?: NoteStyle) => {
    if (!currentDocument) return;
    const pageNum = currentPageIndex + 1;
    setIsGenerating(true);
    setGenerationStep('Preparing your handwritten pages...');

    try {
      const updatedPage = await noteService.regeneratePage(
        currentDocument.id,
        pageNum,
        instruction,
        customModifier,
        style || 'Handwritten'
      );

      const updatedPages = [...currentDocument.pages];
      updatedPages[currentPageIndex] = updatedPage;
      setCurrentDocument({ ...currentDocument, pages: updatedPages });

      setIsRegenerateOpen(false);
      setIsGenerating(false);
      addToast('success', `✓ Page ${pageNum} updated!`);
    } catch (err: any) {
      setIsGenerating(false);
      addToast('error', err.message || 'Failed to regenerate page');
    }
  };

  // In-Place Page Editing
  const handleSavePageEdit = async (updatedContent: PageContent) => {
    if (!currentDocument) return;
    const pageNum = currentPageIndex + 1;

    try {
      const updatedPage = await noteService.updatePageContent(
        currentDocument.id,
        pageNum,
        updatedContent.topicTitle,
        updatedContent.topicSubtitle ? updatedContent : { ...updatedContent, topicSubtitle: currentDocument.pages[currentPageIndex].content.topicSubtitle }
      );

      const updatedPages = [...currentDocument.pages];
      updatedPages[currentPageIndex] = updatedPage;
      setCurrentDocument({ ...currentDocument, pages: updatedPages });

      setIsEditorOpen(false);
      addToast('success', '✓ Page updated!');
    } catch (err: any) {
      addToast('error', err.message || 'Failed to update page');
    }
  };

  // Select document from history
  const handleSelectDocument = async (id: number) => {
    try {
      const doc = await noteService.getDocument(id);
      setCurrentDocument(doc);
      setCurrentPageIndex(0);
      setIsHistoryOpen(false);
      addToast('info', `Opened '${doc.title}'`);
    } catch (err: any) {
      addToast('error', 'Failed to load document');
    }
  };

  // Delete document
  const handleDeleteDocument = async (id: number) => {
    try {
      await noteService.deleteDocument(id);
      if (currentDocument?.id === id) {
        setCurrentDocument(null);
      }
      loadRecentNotes();
      addToast('info', 'Document deleted');
    } catch (err: any) {
      addToast('error', 'Failed to delete document');
    }
  };

  // Rename document
  const handleRenameDocument = async (id: number, newTitle: string) => {
    try {
      const doc = await noteService.renameDocument(id, newTitle);
      if (currentDocument?.id === id) {
        setCurrentDocument(doc);
      }
      loadRecentNotes();
      addToast('success', 'Renamed note title');
    } catch (err: any) {
      addToast('error', 'Failed to rename note');
    }
  };

  // PDF Export
  const handleDownloadPdf = async (targetDocId?: number) => {
    const docId = targetDocId || currentDocument?.id;
    if (!docId || !currentDocument) return;
    setIsExporting(true);
    addToast('info', 'Compiling PDF document...');

    try {
      await new Promise((resolve) => setTimeout(resolve, 150));

      const pdf = new jsPDF('p', 'mm', 'a4');
      const totalP = currentDocument.pages.length || 1;
      let renderedCount = 0;

      for (let i = 0; i < totalP; i++) {
        const pageEl = document.getElementById(`visual-note-page-${i + 1}`);
        if (pageEl) {
          const canvas = await html2canvas(pageEl, {
            scale: 2,
            useCORS: true,
            logging: false,
            backgroundColor: '#ffffff'
          });
          const imgData = canvas.toDataURL('image/jpeg', 0.95);
          if (renderedCount > 0) pdf.addPage();
          pdf.addImage(imgData, 'JPEG', 0, 0, 210, 297);
          renderedCount++;
        }
      }

      if (renderedCount > 0) {
        pdf.save(`${currentDocument.title || 'Visual_Notes'}.pdf`);
        setIsExporting(false);
        addToast('success', '✓ PDF Downloaded successfully!');
      } else {
        throw new Error('No pages found for export');
      }
    } catch (e) {
      window.open(noteService.getPdfDownloadUrl(docId), '_blank');
      setIsExporting(false);
    }
  };

  // PNG Image Export
  const handleDownloadPng = async () => {
    const pageNum = currentPageIndex + 1;
    const pageEl = document.getElementById(`visual-note-page-${pageNum}`);
    if (!pageEl) return;

    setIsExporting(true);
    try {
      const canvas = await html2canvas(pageEl, {
        scale: 2,
        useCORS: true,
        logging: false,
        backgroundColor: '#ffffff'
      });
      const link = document.createElement('a');
      link.download = `${currentDocument?.title || 'Note'}_Page_${pageNum}.png`;
      link.href = canvas.toDataURL('image/png');
      link.click();
      setIsExporting(false);
      addToast('success', `✓ Downloaded Page ${pageNum} as PNG!`);
    } catch {
      setIsExporting(false);
      addToast('error', 'Failed to generate PNG image');
    }
  };

  // Web Share API
  const handleShare = async () => {
    if (navigator.share && currentDocument) {
      try {
        await navigator.share({
          title: currentDocument.title,
          text: `Check out these handwritten study notes on ${currentDocument.title} created with AI Visual Notes!`,
          url: window.location.href,
        });
      } catch {
        // User canceled
      }
    } else {
      navigator.clipboard.writeText(window.location.href);
      addToast('info', 'Link copied to clipboard!');
    }
  };

  // Auth operations
  const handleLogin = async (email: string, pass: string) => {
    setIsAuthLoading(true);
    try {
      const res = await authService.login(email, pass);
      setUser(res.user);
      setIsAuthLoading(false);
      addToast('success', `Welcome back, ${res.user.name}!`);
    } catch (err: any) {
      setIsAuthLoading(false);
      throw err;
    }
  };

  const handleRegister = async (name: string, email: string, pass: string) => {
    setIsAuthLoading(true);
    try {
      const res = await authService.register(name, email, pass);
      setUser(res.user);
      setIsAuthLoading(false);
      addToast('success', `Account created! Welcome, ${res.user.name}.`);
    } catch (err: any) {
      setIsAuthLoading(false);
      throw err;
    }
  };

  const handleLogout = () => {
    authService.logout();
    setUser(null);
    addToast('info', 'Signed out');
  };

  return (
    <div className="min-h-screen flex flex-col bg-slate-50 text-slate-800">
      {/* Toast notifications */}
      <ToastContainer toasts={toasts} onRemove={removeToast} />

      {/* Navigation Bar */}
      <Navbar
        user={user}
        onOpenAuth={() => setIsAuthOpen(true)}
        onOpenHistory={() => setIsHistoryOpen(true)}
        onLogout={handleLogout}
        onNewNotes={() => {
          setCurrentDocument(null);
          window.scrollTo({ top: 0, behavior: 'smooth' });
        }}
      />

      {/* Main Content Area */}
      <main className="flex-1">
        {/* Simple User-Friendly Loading Overlay */}
        {isGenerating && (
          <div className="fixed inset-0 z-50 bg-slate-900/50 backdrop-blur-sm flex flex-col items-center justify-center p-6 text-white animate-in fade-in">
            <div className="w-14 h-14 relative mb-4">
              <div className="w-14 h-14 border-4 border-blue-400/30 border-t-blue-400 rounded-full animate-spin" />
            </div>
            <p className="text-base text-blue-100 font-medium animate-pulse">
              {generationStep}
            </p>
          </div>
        )}

        {/* View Mode: If document is generated or loaded */}
        {currentDocument && currentDocument.pages && currentDocument.pages.length > 0 ? (
          <div>
            {/* Toolbar */}
            <PageToolbar
              currentPage={currentPageIndex + 1}
              totalPages={currentDocument.pages.length}
              topicTitle={currentDocument.title}
              onPageChange={(p) => setCurrentPageIndex(p - 1)}
              zoom={zoom}
              onZoomChange={setZoom}
              onOpenRegenerate={() => setIsRegenerateOpen(true)}
              onOpenEditor={() => setIsEditorOpen(true)}
              onDownloadPdf={() => handleDownloadPdf()}
              onDownloadPng={handleDownloadPng}
              onShare={handleShare}
              onBack={() => setCurrentDocument(null)}
              isExporting={isExporting}
            />

            {/* Document Content View */}
            <div className="max-w-5xl mx-auto px-4 py-8 flex flex-col items-center">
              <div 
                className="w-full flex justify-center transition-transform origin-top"
                style={{ transform: `scale(${zoom / 100})` }}
              >
                <div className="space-y-12 w-full max-w-[800px]">
                  {currentDocument.pages.map((page: NotePage, idx: number) => (
                    <div 
                      key={page.id || idx}
                      className={idx === currentPageIndex || zoom <= 75 || isExporting ? 'block' : 'hidden'}
                    >
                      <VisualNotePage
                        content={page.content}
                        style="Handwritten"
                        pageNumber={page.pageNumber}
                        totalPages={currentDocument.pages.length}
                      />
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        ) : (
          /* Home Screen: Single clean textarea & Compact Recent Notes */
          <div className="px-4 py-10 sm:py-16">
            <HeroPromptInput
              onGenerate={handleStartPrompt}
              isLoading={isGenerating}
            />

            <RecentNotesList
              documents={recentDocuments}
              onSelectDocument={handleSelectDocument}
              onDeleteDocument={handleDeleteDocument}
            />
          </div>
        )}
      </main>

      {/* Footer */}
      <Footer />

      {/* Single Page Regenerate Modal */}
      {currentDocument && currentDocument.pages[currentPageIndex] && (
        <RegeneratePageModal
          isOpen={isRegenerateOpen}
          onClose={() => setIsRegenerateOpen(false)}
          pageNumber={currentPageIndex + 1}
          topicTitle={currentDocument.pages[currentPageIndex].topicTitle}
          onRegenerate={handleRegeneratePage}
          isRegenerating={isGenerating}
          currentStyle="Handwritten"
        />
      )}

      {/* In-Place Content Editor Modal */}
      {currentDocument && currentDocument.pages[currentPageIndex] && (
        <PageEditorModal
          isOpen={isEditorOpen}
          onClose={() => setIsEditorOpen(false)}
          pageNumber={currentPageIndex + 1}
          initialContent={currentDocument.pages[currentPageIndex].content}
          onSave={handleSavePageEdit}
          isSaving={false}
        />
      )}

      {/* History Drawer */}
      <HistoryDrawer
        isOpen={isHistoryOpen}
        onClose={() => setIsHistoryOpen(false)}
        documents={recentDocuments}
        onSelectDocument={handleSelectDocument}
        onDeleteDocument={handleDeleteDocument}
        onRenameDocument={handleRenameDocument}
        onSearch={(q) => loadRecentNotes(q)}
        onDownloadPdf={handleDownloadPdf}
      />

      {/* Authentication Modal */}
      <AuthModal
        isOpen={isAuthOpen}
        onClose={() => setIsAuthOpen(false)}
        onLogin={handleLogin}
        onRegister={handleRegister}
        isLoading={isAuthLoading}
      />
    </div>
  );
}

export default App;
