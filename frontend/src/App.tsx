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
import { TopicCountModal } from './components/planner/TopicCountModal';
import { PagePlanEditorModal } from './components/planner/PagePlanEditorModal';
import { VisualNotePage } from './components/viewer/VisualNotePage';
import { PageToolbar } from './components/viewer/PageToolbar';
import { RegeneratePageModal } from './components/viewer/RegeneratePageModal';
import { PageEditorModal } from './components/viewer/PageEditorModal';
import { HistoryDrawer } from './components/history/HistoryDrawer';
import { AuthModal } from './components/auth/AuthModal';

import {
  NoteDocument,
  NotePage,
  PromptAnalysisResponse,
  PagePlan,
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
  const [currentStyle, setCurrentStyle] = useState<NoteStyle>('Handwritten');

  // Multi-Topic & Planning states
  const [analysis, setAnalysis] = useState<PromptAnalysisResponse | null>(null);
  const [pagePlan, setPagePlan] = useState<PagePlan | null>(null);
  const [isTopicCountModalOpen, setIsTopicCountModalOpen] = useState(false);
  const [isPlanEditorOpen, setIsPlanEditorOpen] = useState(false);
  const [currentPrompt, setCurrentPrompt] = useState<string>('');
  const [currentAudience, setCurrentAudience] = useState<string>('B.Tech / College');
  const [currentDetail, setCurrentDetail] = useState<string>('Standard');

  // Loading & Progress states
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [isGenerating, setIsGenerating] = useState(false);
  const [isExporting, setIsExporting] = useState(false);
  const [generationStep, setGenerationStep] = useState<string>('');

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

  // Step 1: Prompt Submission -> Analysis
  const handleStartPrompt = async (
    prompt: string,
    style: NoteStyle,
    detailLevel: string,
    audience: string
  ) => {
    setCurrentPrompt(prompt);
    setCurrentStyle(style);
    setCurrentDetail(detailLevel);
    setCurrentAudience(audience);
    setIsAnalyzing(true);
    setGenerationStep('Analyzing topic requirements...');

    try {
      const analysisResult = await noteService.analyzePrompt(prompt, style);
      setAnalysis(analysisResult);

      // Check if multi-topic or explicit page count
      if (analysisResult.topicCount > 1 && !analysisResult.requestedPageCount) {
        setIsAnalyzing(false);
        setIsTopicCountModalOpen(true);
      } else {
        // Direct plan generation
        const pagesToPlan = analysisResult.requestedPageCount || analysisResult.suggestedPageCount || 1;
        await generatePlanAndProceed(prompt, pagesToPlan, audience, style, analysisResult.difficulty);
      }
    } catch (err: any) {
      setIsAnalyzing(false);
      addToast('error', err.message || 'Failed to analyze prompt');
    }
  };

  // Step 2: Confirm Page Count (from modal)
  const handleConfirmPageCount = async (count: number) => {
    setIsTopicCountModalOpen(false);
    setIsAnalyzing(true);
    setGenerationStep('Synthesizing structured page plan...');

    try {
      await generatePlanAndProceed(
        currentPrompt,
        count,
        currentAudience,
        currentStyle,
        analysis?.difficulty || 'Intermediate'
      );
    } catch (err: any) {
      setIsAnalyzing(false);
      addToast('error', err.message || 'Failed to plan pages');
    }
  };

  const generatePlanAndProceed = async (
    prompt: string,
    pages: number,
    audience: string,
    style: NoteStyle,
    difficulty: string
  ) => {
    const plan = await noteService.planPages(prompt, pages, audience, style, difficulty);
    setPagePlan(plan);
    setIsAnalyzing(false);

    if (pages > 1) {
      setIsPlanEditorOpen(true);
    } else {
      // Single page directly generates
      await handleExecuteGeneration(plan);
    }
  };

  // Step 3: Execute final generation
  const handleExecuteGeneration = async (finalPlan: PagePlan) => {
    setIsPlanEditorOpen(false);
    setIsGenerating(true);
    setGenerationStep(`Rendering ${finalPlan.totalPages} high-precision visual note pages...`);

    try {
      const doc = await noteService.generateNotes({
        prompt: currentPrompt,
        pageCount: finalPlan.totalPages,
        style: currentStyle,
        audience: currentAudience,
        detailLevel: currentDetail,
        customPlan: finalPlan,
      });

      setCurrentDocument(doc);
      setCurrentPageIndex(0);
      setIsGenerating(false);
      loadRecentNotes();
      addToast('success', `✓ Successfully generated ${doc.pageCount} visual study note page(s)!`);
    } catch (err: any) {
      setIsGenerating(false);
      addToast('error', err.message || 'Failed to generate study notes');
    }
  };

  // Single Page Regeneration
  const handleRegeneratePage = async (instruction: string, customModifier: string, style?: NoteStyle) => {
    if (!currentDocument) return;
    const pageNum = currentPageIndex + 1;
    setIsGenerating(true);
    setGenerationStep(`Regenerating Page ${pageNum}...`);

    try {
      const updatedPage = await noteService.regeneratePage(
        currentDocument.id,
        pageNum,
        instruction,
        customModifier,
        style || currentStyle
      );

      const updatedPages = [...currentDocument.pages];
      updatedPages[currentPageIndex] = updatedPage;
      setCurrentDocument({ ...currentDocument, pages: updatedPages });

      setIsRegenerateOpen(false);
      setIsGenerating(false);
      addToast('success', `✓ Page ${pageNum} regenerated with new refinements!`);
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
      addToast('success', '✓ Page updated and re-rendered!');
    } catch (err: any) {
      addToast('error', err.message || 'Failed to update page');
    }
  };

  // Select document from history
  const handleSelectDocument = async (id: number) => {
    try {
      const doc = await noteService.getDocument(id);
      setCurrentDocument(doc);
      setCurrentStyle(doc.style || 'Handwritten');
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
    addToast('info', 'Compiling multi-page vector A4 PDF...');

    try {
      // Allow React to unhide all pages in DOM before canvas capture
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
      // Fallback to backend PDF generator
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
          text: `Check out these visual study notes on ${currentDocument.title} created with AI Visual Notes!`,
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
        
        {/* Loading Overlay */}
        {(isAnalyzing || isGenerating) && (
          <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-md flex flex-col items-center justify-center p-6 text-white animate-in fade-in">
            <div className="w-16 h-16 relative mb-4">
              <div className="w-16 h-16 border-4 border-indigo-400/30 border-t-indigo-400 rounded-full animate-spin" />
            </div>
            <h3 className="text-xl font-bold tracking-tight mb-1">
              AI Visual Notes Engine
            </h3>
            <p className="text-sm text-indigo-200 font-medium animate-pulse">
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
              onPageChange={(p) => setCurrentPageIndex(p - 1)}
              zoom={zoom}
              onZoomChange={setZoom}
              onOpenRegenerate={() => setIsRegenerateOpen(true)}
              onOpenEditor={() => setIsEditorOpen(true)}
              onDownloadPdf={() => handleDownloadPdf()}
              onDownloadPng={handleDownloadPng}
              onShare={handleShare}
              onPrint={() => window.print()}
              isExporting={isExporting}
              styles={['Handwritten', 'Clean Digital', 'Exam Notes', 'Minimal', 'Colorful Study Notes']}
              currentStyle={currentStyle}
              onStyleChange={(s) => setCurrentStyle(s)}
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
                        style={currentStyle}
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
          /* Home Screen: Prompt Input & Recent Cards */
          <div className="px-4 py-8 sm:py-12">
            <HeroPromptInput
              onGenerate={handleStartPrompt}
              isLoading={isAnalyzing || isGenerating}
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

      {/* Multi-Topic Count Detection Modal */}
      <TopicCountModal
        isOpen={isTopicCountModalOpen}
        onClose={() => setIsTopicCountModalOpen(false)}
        analysis={analysis}
        onConfirmPageCount={handleConfirmPageCount}
      />

      {/* Page Plan Review & Editing Modal */}
      <PagePlanEditorModal
        isOpen={isPlanEditorOpen}
        onClose={() => setIsPlanEditorOpen(false)}
        plan={pagePlan}
        onProceedToGenerate={handleExecuteGeneration}
        onRegeneratePlan={() => handleConfirmPageCount(pagePlan?.totalPages || 1)}
        isPlanning={isAnalyzing}
      />

      {/* Single Page Regenerate Modal */}
      {currentDocument && currentDocument.pages[currentPageIndex] && (
        <RegeneratePageModal
          isOpen={isRegenerateOpen}
          onClose={() => setIsRegenerateOpen(false)}
          pageNumber={currentPageIndex + 1}
          topicTitle={currentDocument.pages[currentPageIndex].topicTitle}
          onRegenerate={handleRegeneratePage}
          isRegenerating={isGenerating}
          currentStyle={currentStyle}
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
