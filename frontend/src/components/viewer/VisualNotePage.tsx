import React from 'react';
import { Sparkles, Star, AlertTriangle, Lightbulb, Code2, Hash, CheckCircle, XCircle, ArrowRight, ArrowLeft } from 'lucide-react';
import { PageContent, NoteStyle } from '../../types';
import { DiagramRenderer } from './DiagramRenderer';

interface VisualNotePageProps {
  content: PageContent;
  style: NoteStyle;
  pageNumber: number;
  totalPages: number;
}

export const VisualNotePage: React.FC<VisualNotePageProps> = ({
  content,
  style,
  pageNumber,
  totalPages,
}) => {
  const isHandwritten = style === 'Handwritten';
  const isExam = style === 'Exam Notes';
  const isMinimal = style === 'Minimal';
  const isColorful = style === 'Colorful Study Notes';

  // Base background & font configurations
  const pageBgClass = isHandwritten
    ? 'notebook-paper-ruled font-hand text-slate-900 border-2 border-slate-300'
    : isExam
    ? 'bg-amber-50/20 font-sans text-slate-900 border-2 border-slate-300'
    : isMinimal
    ? 'bg-white font-mono text-slate-900 border border-slate-200'
    : isColorful
    ? 'bg-gradient-to-br from-indigo-50/30 via-purple-50/20 to-pink-50/30 font-sans text-slate-900 border border-purple-200'
    : 'bg-white font-sans text-slate-900 border border-slate-200';

  return (
    <div
      id={`visual-note-page-${pageNumber}`}
      className={`a4-page-container relative p-6 sm:p-10 rounded-2xl shadow-page overflow-hidden flex flex-col justify-between transition-all ${pageBgClass}`}
      style={{ minHeight: '1140px' }}
    >
      
      {/* Top Header */}
      <div>
        {/* Continuation Top Banner */}
        {content.isContinuation && (
          <div className="mb-3 px-3 py-1.5 rounded-lg bg-indigo-50 border border-indigo-200 text-indigo-900 flex items-center justify-between text-xs font-semibold">
            <span className="flex items-center gap-1">
              <ArrowLeft className="w-3.5 h-3.5 text-indigo-600" />
              <span>Continued from Page {pageNumber - 1}</span>
            </span>
            <span className="font-bold text-indigo-700">{content.pagePartTitle || `Part ${pageNumber} of ${totalPages}`}</span>
          </div>
        )}

        <div className="flex items-start justify-between gap-4 border-b-2 pb-3 mb-4 border-slate-300">
          <div>
            <div className="flex items-center gap-2 mb-1">
              <span className={`text-xs font-bold uppercase tracking-wider px-2.5 py-0.5 rounded-full ${
                isHandwritten
                  ? 'bg-purple-100 text-purple-900 border border-purple-300'
                  : isExam
                  ? 'bg-rose-100 text-rose-800 border border-rose-300'
                  : isColorful
                  ? 'bg-purple-100 text-purple-800 border border-purple-200'
                  : 'bg-indigo-100 text-indigo-800'
              }`}>
                {content.categoryBadge || 'Academic Study Notes'}
              </span>
              {content.difficultyLevel && (
                <span className="text-[11px] font-semibold text-slate-500">
                  • {content.difficultyLevel}
                </span>
              )}
              {content.pagePartTitle && (
                <span className="text-[11px] font-bold text-indigo-600 bg-indigo-50 px-2 py-0.5 rounded border border-indigo-200">
                  {content.pagePartTitle}
                </span>
              )}
            </div>

            <h1 className={`font-black tracking-tight leading-tight ${
              isHandwritten
                ? 'font-hand text-3xl sm:text-4xl ink-title-purple double-underline-purple'
                : isExam
                ? 'text-2xl sm:text-3xl font-extrabold uppercase text-slate-900'
                : 'text-2xl sm:text-3xl font-extrabold text-slate-900'
            }`}>
              {content.topicTitle}
            </h1>

            {content.topicSubtitle && (
              <p className={`text-xs text-slate-600 mt-1 italic ${isHandwritten ? 'text-base font-hand text-indigo-900/80' : ''}`}>
                {content.topicSubtitle}
              </p>
            )}
          </div>

          <div className="text-right shrink-0">
            <div className={`inline-flex items-center gap-1 font-bold px-3 py-1 rounded-lg text-xs ${
              isHandwritten
                ? 'bg-purple-100 text-purple-950 doodle-box-purple text-sm'
                : 'bg-slate-100 text-slate-700'
            }`}>
              <span>Page {pageNumber} of {totalPages}</span>
            </div>
            <div className="text-[10px] text-slate-400 mt-1 font-semibold uppercase tracking-widest">
              AI Visual Notes
            </div>
          </div>
        </div>

        {/* 1. Definition, Purpose & Main Idea */}
        {(content.definition || content.purpose || content.mainIdea || content.simpleExplanation) && (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-3 mb-4">
            
            {content.definition && (
              <div className={`p-3.5 rounded-xl ${
                isHandwritten
                  ? 'bg-amber-50/70 border border-amber-300/80 doodle-box'
                  : isExam
                  ? 'bg-blue-50/60 border-l-4 border-blue-600 rounded-r-xl'
                  : isColorful
                  ? 'bg-indigo-50/60 border border-indigo-200'
                  : 'bg-slate-50 border border-slate-200'
              }`}>
                <div className={`flex items-center gap-1.5 mb-1 font-bold text-xs uppercase tracking-wider ${
                  isHandwritten ? 'ink-section-red' : 'text-indigo-900'
                }`}>
                  <Sparkles className="w-3.5 h-3.5 text-indigo-600" />
                  <span>1. Definition & Purpose</span>
                </div>
                <p className={`text-xs leading-relaxed text-slate-800 ${isHandwritten ? 'font-hand text-base ink-body-blue' : ''}`}>
                  {content.definition}
                </p>
                {content.purpose && (
                  <p className={`text-xs leading-relaxed text-slate-700 mt-1.5 font-medium ${isHandwritten ? 'font-hand text-base' : ''}`}>
                    <span className="font-bold text-slate-900">Purpose: </span>{content.purpose}
                  </p>
                )}
              </div>
            )}

            {(content.mainIdea || content.simpleExplanation) && (
              <div className={`p-3.5 rounded-xl ${
                isHandwritten
                  ? 'bg-emerald-50/70 border border-emerald-300/80 doodle-box'
                  : isExam
                  ? 'bg-emerald-50/60 border-l-4 border-emerald-600 rounded-r-xl'
                  : isColorful
                  ? 'bg-emerald-50/60 border border-emerald-200'
                  : 'bg-slate-50 border border-slate-200'
              }`}>
                <div className={`flex items-center gap-1.5 mb-1 font-bold text-xs uppercase tracking-wider ${
                  isHandwritten ? 'ink-section-red' : 'text-emerald-900'
                }`}>
                  <Lightbulb className="w-3.5 h-3.5 text-emerald-600" />
                  <span>2. Main Working Principle</span>
                </div>
                <p className={`text-xs leading-relaxed text-slate-800 ${isHandwritten ? 'font-hand text-base ink-body-blue' : ''}`}>
                  {content.mainIdea || content.simpleExplanation}
                </p>
              </div>
            )}

          </div>
        )}

        {/* Topic-Specific Diagram */}
        {content.diagram && (
          <div className="my-3">
            <DiagramRenderer diagram={content.diagram} style={style} />
          </div>
        )}

        {/* Algorithm Steps (if present) */}
        {content.algorithm && content.algorithm.length > 0 && (
          <div className={`my-3 p-3.5 rounded-xl ${
            isHandwritten
              ? 'bg-blue-50/40 border border-blue-300 doodle-box-blue'
              : 'bg-slate-50 border border-slate-200'
          }`}>
            <div className="flex items-center justify-between mb-2">
              <div className={`flex items-center gap-1.5 font-bold text-xs uppercase tracking-wider ${
                isHandwritten ? 'ink-section-red' : 'text-slate-800'
              }`}>
                <Code2 className="w-3.5 h-3.5 text-indigo-600" />
                <span>3. Step-by-Step Algorithm</span>
              </div>
              <span className="text-[10px] font-semibold text-slate-500 bg-white px-2 py-0.5 rounded border border-slate-200">
                {content.algorithm.length} Steps
              </span>
            </div>

            <div className="space-y-1.5">
              {content.algorithm.map((step, idx) => (
                <div key={idx} className="flex items-start gap-2.5 text-xs text-slate-800">
                  <span className="w-5 h-5 rounded-full bg-indigo-600 text-white font-bold text-[11px] shrink-0 flex items-center justify-center">
                    {step.stepNumber}
                  </span>
                  <div className="flex-1">
                    <span className={`font-semibold ${isHandwritten ? 'font-hand text-base ink-body-blue' : ''}`}>
                      {step.instruction}
                    </span>
                    {step.codeSnippet && (
                      <pre className="mt-1 p-2 rounded-lg bg-slate-900 text-emerald-300 font-mono text-[11px] overflow-x-auto leading-tight">
                        {step.codeSnippet}
                      </pre>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Boxed Pseudocode (if present) */}
        {content.pseudocode && (
          <div className="my-3 p-3.5 rounded-xl pseudocode-box">
            <div className={`flex items-center justify-between mb-2 font-bold text-xs uppercase tracking-wider ${
              isHandwritten ? 'ink-section-red' : 'text-slate-800'
            }`}>
              <div className="flex items-center gap-1.5">
                <Code2 className="w-3.5 h-3.5 text-purple-600" />
                <span>4. Pseudocode Implementation</span>
              </div>
              <span className="text-[10px] font-mono text-slate-500 bg-slate-200 px-2 py-0.5 rounded">
                Clean Standard Code
              </span>
            </div>
            <pre className="p-3 rounded bg-slate-900 text-emerald-300 font-mono text-xs overflow-x-auto leading-relaxed border border-slate-700">
              {content.pseudocode}
            </pre>
          </div>
        )}

        {/* Structured Sections */}
        {content.sections && content.sections.length > 0 && (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-3 my-3">
            {content.sections.map((sec, sidx) => (
              <div
                key={sidx}
                className={`p-3.5 rounded-xl ${
                  isHandwritten
                    ? 'bg-white/80 border border-slate-300 doodle-box shadow-2xs'
                    : isExam
                    ? 'bg-white border-2 border-slate-200 shadow-2xs'
                    : isColorful
                    ? sidx % 2 === 0
                      ? 'bg-pink-50/50 border border-pink-200'
                      : 'bg-sky-50/50 border border-sky-200'
                    : 'bg-white border border-slate-200'
                }`}
              >
                <div className="flex items-center justify-between mb-1.5">
                  <h4 className={`font-bold text-xs sm:text-sm ${
                    isHandwritten ? 'font-hand text-lg ink-section-red' : 'text-slate-900'
                  }`}>
                    {sec.heading}
                  </h4>
                  {sec.badge && (
                    <span className="text-[10px] font-bold px-2 py-0.5 rounded bg-slate-100 text-slate-700">
                      {sec.badge}
                    </span>
                  )}
                </div>

                {sec.content && (
                  <p className={`text-xs text-slate-700 leading-relaxed mb-2 ${
                    isHandwritten ? 'font-hand text-base ink-body-blue' : ''
                  }`}>
                    {sec.content}
                  </p>
                )}

                {sec.bulletPoints && sec.bulletPoints.length > 0 && (
                  <ul className="space-y-1">
                    {sec.bulletPoints.map((bp, bidx) => (
                      <li
                        key={bidx}
                        className={`text-xs text-slate-800 flex items-start gap-1.5 ${
                          isHandwritten ? 'font-hand text-base ink-body-blue' : ''
                        }`}
                      >
                        <span className="text-indigo-600 font-bold shrink-0">•</span>
                        <span>{bp}</span>
                      </li>
                    ))}
                  </ul>
                )}
              </div>
            ))}
          </div>
        )}

        {/* Advantages & Limitations (if present) */}
        {((content.advantages && content.advantages.length > 0) || (content.limitations && content.limitations.length > 0)) && (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-3 my-3">
            {content.advantages && content.advantages.length > 0 && (
              <div className={`p-3 rounded-xl ${
                isHandwritten ? 'bg-emerald-50/60 border border-emerald-300 doodle-box' : 'bg-emerald-50 border border-emerald-200'
              }`}>
                <div className="flex items-center gap-1.5 mb-1.5 font-bold text-xs uppercase tracking-wider text-emerald-900">
                  <CheckCircle className="w-3.5 h-3.5 text-emerald-600" />
                  <span>Advantages / Pros</span>
                </div>
                <ul className="space-y-1">
                  {content.advantages.map((adv, ai) => (
                    <li key={ai} className={`text-xs text-emerald-950 flex items-start gap-1.5 ${
                      isHandwritten ? 'font-hand text-base' : ''
                    }`}>
                      <span className="text-emerald-600 font-bold">✓</span>
                      <span>{adv}</span>
                    </li>
                  ))}
                </ul>
              </div>
            )}

            {content.limitations && content.limitations.length > 0 && (
              <div className={`p-3 rounded-xl ${
                isHandwritten ? 'bg-rose-50/60 border border-rose-300 doodle-box' : 'bg-rose-50 border border-rose-200'
              }`}>
                <div className="flex items-center gap-1.5 mb-1.5 font-bold text-xs uppercase tracking-wider text-rose-900">
                  <XCircle className="w-3.5 h-3.5 text-rose-600" />
                  <span>Limitations / Disadvantages</span>
                </div>
                <ul className="space-y-1">
                  {content.limitations.map((lim, li) => (
                    <li key={li} className={`text-xs text-rose-950 flex items-start gap-1.5 ${
                      isHandwritten ? 'font-hand text-base' : ''
                    }`}>
                      <span className="text-rose-600 font-bold">✗</span>
                      <span>{lim}</span>
                    </li>
                  ))}
                </ul>
              </div>
            )}
          </div>
        )}

        {/* Example & Formula / Complexity Row */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-3 my-3">
          
          {/* Concrete Worked Example */}
          {content.example && (
            <div className={`p-3.5 rounded-xl ${
              isHandwritten
                ? 'bg-amber-50/50 border border-amber-300 doodle-box'
                : 'bg-slate-50 border border-slate-200'
            }`}>
              <div className={`flex items-center gap-1.5 mb-1.5 font-bold text-xs uppercase tracking-wider ${
                isHandwritten ? 'ink-section-red' : 'text-amber-900'
              }`}>
                <Hash className="w-3.5 h-3.5 text-amber-600" />
                <span>Worked Example: {content.example.title}</span>
              </div>
              
              {content.example.scenario && (
                <p className={`text-xs text-slate-700 mb-1.5 italic ${isHandwritten ? 'font-hand text-base' : ''}`}>
                  {content.example.scenario}
                </p>
              )}

              {content.example.stepByStep && (
                <div className="space-y-1 my-1.5">
                  {content.example.stepByStep.map((s, si) => (
                    <div key={si} className={`text-[11px] text-slate-800 bg-white/80 p-1.5 rounded border border-amber-200/60 ${
                      isHandwritten ? 'font-hand text-sm' : ''
                    }`}>
                      {s}
                    </div>
                  ))}
                </div>
              )}

              {content.example.outputOrResult && (
                <div className={`mt-2 p-2 rounded-lg bg-emerald-50 text-emerald-900 border border-emerald-200 text-xs font-semibold ${
                  isHandwritten ? 'font-hand text-base' : ''
                }`}>
                  ✓ Result: {content.example.outputOrResult}
                </div>
              )}
            </div>
          )}

          {/* Formula & Complexity */}
          <div className="space-y-3">
            {content.formula && (
              <div className={`p-3.5 rounded-xl ${
                isHandwritten
                  ? 'bg-purple-50/50 border border-purple-300 doodle-box-purple'
                  : 'bg-slate-50 border border-slate-200'
              }`}>
                <div className={`font-bold text-xs uppercase tracking-wider mb-1 ${
                  isHandwritten ? 'ink-section-red' : 'text-purple-900'
                }`}>
                  📐 {content.formula.title}
                </div>
                <div className="p-2 rounded bg-purple-950 text-purple-200 font-mono text-xs font-bold text-center my-1 tracking-wide">
                  {content.formula.expression}
                </div>
                {content.formula.explanation && (
                  <p className={`text-[11px] text-slate-600 mt-1 ${isHandwritten ? 'font-hand text-sm' : ''}`}>
                    {content.formula.explanation}
                  </p>
                )}
              </div>
            )}

            {content.complexity && (
              <div className={`p-3 rounded-xl ${
                isHandwritten
                  ? 'bg-rose-50/50 border border-rose-300 doodle-box'
                  : 'bg-rose-50/40 border border-rose-200'
              }`}>
                <div className="flex items-center justify-between mb-1">
                  <span className={`font-bold text-xs uppercase tracking-wider ${
                    isHandwritten ? 'ink-section-red' : 'text-rose-900'
                  }`}>
                    ⏱️ Complexity Proof & Analysis
                  </span>
                  <span className="text-[10px] font-bold text-rose-700 bg-rose-100 px-2 py-0.5 rounded border border-rose-300">
                    Space: {content.complexity.space}
                  </span>
                </div>
                <div className="grid grid-cols-3 gap-1 text-center my-1">
                  <div className="bg-white p-1 rounded border border-rose-200">
                    <span className="text-[9px] text-slate-400 block font-bold">BEST</span>
                    <span className="text-xs font-black text-emerald-700">{content.complexity.timeBest}</span>
                  </div>
                  <div className="bg-white p-1 rounded border border-rose-200">
                    <span className="text-[9px] text-slate-400 block font-bold">AVG</span>
                    <span className="text-xs font-black text-amber-700">{content.complexity.timeAverage}</span>
                  </div>
                  <div className="bg-white p-1 rounded border border-rose-200">
                    <span className="text-[9px] text-slate-400 block font-bold">WORST</span>
                    <span className="text-xs font-black text-rose-700">{content.complexity.timeWorst}</span>
                  </div>
                </div>
                {content.complexity.explanation && (
                  <p className={`text-[10px] text-slate-600 mt-1 italic ${isHandwritten ? 'font-hand text-xs' : ''}`}>
                    {content.complexity.explanation}
                  </p>
                )}
              </div>
            )}
          </div>

        </div>

      </div>

      {/* Footer Revision, Key Points & Continuation */}
      <div className="mt-4 pt-3 border-t-2 border-slate-300">
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
          
          {/* Key Points */}
          {content.keyPoints && content.keyPoints.length > 0 && (
            <div className={`p-3 rounded-xl ${
              isHandwritten
                ? 'bg-amber-100/50 border-2 border-amber-400 doodle-box'
                : 'bg-amber-50 border border-amber-200'
            }`}>
              <div className="flex items-center gap-1 mb-1.5 font-bold text-xs uppercase tracking-wider text-amber-900">
                <Star className="w-3.5 h-3.5 text-amber-600 fill-amber-500" />
                <span>Key Study Takeaways</span>
              </div>
              <ul className="space-y-1">
                {content.keyPoints.slice(0, 3).map((kp, kpi) => (
                  <li key={kpi} className={`text-xs text-amber-950 flex items-start gap-1.5 ${
                    isHandwritten ? 'font-hand text-base font-semibold' : ''
                  }`}>
                    <span className="text-amber-600 font-bold">★</span>
                    <span>{kp.point}</span>
                  </li>
                ))}
              </ul>
            </div>
          )}

          {/* Exam Tips & Mnemonics */}
          {content.examTips && content.examTips.length > 0 && (
            <div className={`p-3 rounded-xl ${
              isHandwritten
                ? 'bg-rose-100/50 border-2 border-rose-400 doodle-box'
                : 'bg-rose-50 border border-rose-200'
            }`}>
              <div className="flex items-center gap-1 mb-1.5 font-bold text-xs uppercase tracking-wider text-rose-900">
                <AlertTriangle className="w-3.5 h-3.5 text-rose-600" />
                <span>Exam High-Yield Tips</span>
              </div>
              {content.examTips[0].tip && (
                <p className={`text-xs text-rose-950 mb-1 ${isHandwritten ? 'font-hand text-base font-semibold' : ''}`}>
                  {content.examTips[0].tip}
                </p>
              )}
              {content.examTips[0].mnemonic && (
                <div className="mt-1 text-[11px] font-bold text-indigo-800 bg-white/80 px-2 py-0.5 rounded border border-indigo-200 inline-block">
                  💡 Mnemonic: {content.examTips[0].mnemonic}
                </div>
              )}
            </div>
          )}

        </div>

        {/* Continuation Bottom Banner */}
        {content.continuesOnNextPage && (
          <div className="mt-3 py-1.5 px-3 rounded-lg bg-purple-50 border border-purple-200 text-purple-900 flex items-center justify-between text-xs font-semibold">
            <span>Detailed notes continue on next page</span>
            <span className="flex items-center gap-1 font-bold text-purple-700">
              <span>Next: Algorithm, Trace & Complexity</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </span>
          </div>
        )}
      </div>

    </div>
  );
};
