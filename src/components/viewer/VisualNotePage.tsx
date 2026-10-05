import React from 'react';
import DOMPurify from 'dompurify';
import { Sparkles, Star, AlertTriangle, Lightbulb, Bookmark, Clock, Database, Layers } from 'lucide-react';
import { PageContent } from '../../types';

interface VisualNotePageProps {
  content: PageContent;
  style?: string;
  pageNumber: number;
  totalPages: number;
}

export function VisualNotePage({
  content,
  pageNumber,
  totalPages,
}: VisualNotePageProps) {
  // Sanitize SVG if present
  const sanitizedSvg = content.diagram?.rawSvg
    ? DOMPurify.sanitize(content.diagram.rawSvg, {
        USE_PROFILES: { svg: true, svgFilters: true },
      })
    : '';

  return (
    <div
      id={`visual-note-page-${pageNumber}`}
      className="relative w-full max-w-[800px] min-h-[1100px] bg-[#fbf9f4] text-slate-800 rounded-lg shadow-xl border border-slate-300 overflow-hidden p-6 sm:p-10 select-text font-hand text-[17px] leading-relaxed transition-all"
      style={{
        backgroundImage: `
          repeating-linear-gradient(
            transparent,
            transparent 31px,
            #e2e8f0 31px,
            #e2e8f0 32px
          )
        `,
        backgroundAttachment: 'local',
      }}
    >
      {/* Red vertical notebook margin line */}
      <div className="absolute top-0 bottom-0 left-12 sm:left-16 w-0.5 bg-rose-400/70 pointer-events-none" />

      {/* Binder hole punches aesthetic */}
      <div className="absolute top-12 left-4 w-3.5 h-3.5 rounded-full bg-slate-300/80 shadow-inner hidden sm:block" />
      <div className="absolute top-1/2 -translate-y-1/2 left-4 w-3.5 h-3.5 rounded-full bg-slate-300/80 shadow-inner hidden sm:block" />
      <div className="absolute bottom-12 left-4 w-3.5 h-3.5 rounded-full bg-slate-300/80 shadow-inner hidden sm:block" />

      {/* Page Header */}
      <div className="pl-6 sm:pl-10 pb-4 border-b border-slate-300/80 mb-6">
        <div className="flex flex-wrap items-center justify-between gap-2 text-xs font-sans font-medium text-slate-500 mb-2">
          <div className="flex items-center gap-2">
            <span className="px-2 py-0.5 rounded bg-indigo-100 text-indigo-800 font-semibold tracking-wide uppercase text-[10px]">
              {content.categoryBadge || 'Study Note'}
            </span>
            <span>{content.difficultyLevel || 'Undergraduate'}</span>
          </div>
          <div className="flex items-center gap-2 text-slate-600">
            <span>{content.documentTitle}</span>
            <span>&bull;</span>
            <span className="font-bold text-slate-900">Page {pageNumber} of {totalPages}</span>
          </div>
        </div>

        {/* Main Handwritten Title */}
        <div className="relative inline-block">
          <h1 className="text-2xl sm:text-4xl font-bold text-indigo-950 tracking-wide font-hand">
            {content.topicTitle}
          </h1>
          <div className="h-1 w-full bg-indigo-500/60 rounded-full mt-1" />
          <div className="h-0.5 w-3/4 bg-indigo-400/40 rounded-full mt-0.5" />
        </div>

        {content.topicSubtitle && (
          <p className="text-sm sm:text-base text-slate-600 font-sans italic mt-1.5">
            {content.topicSubtitle}
          </p>
        )}
      </div>

      {/* Body Content */}
      <div className="pl-6 sm:pl-10 space-y-6">
        {/* Definition & Intuition Box */}
        {(content.definition || content.simpleExplanation) && (
          <div className="relative bg-amber-50/70 border-2 border-amber-300/80 rounded-xl p-4 shadow-sm">
            <div className="absolute -top-3 left-4 bg-amber-200 text-amber-900 font-sans font-bold text-[11px] px-2 py-0.5 rounded-full shadow-xs uppercase tracking-wider flex items-center gap-1">
              <Sparkles className="w-3 h-3" /> Core Definition
            </div>
            {content.definition && (
              <p className="text-slate-800 text-[18px] leading-relaxed">
                {content.definition}
              </p>
            )}
            {content.simpleExplanation && (
              <div className="mt-2 pt-2 border-t border-amber-200/60 flex items-start gap-2 text-[16px] text-amber-950 font-hand">
                <Lightbulb className="w-4 h-4 text-amber-600 shrink-0 mt-0.5" />
                <span><strong className="font-sans text-xs uppercase tracking-wider text-amber-800">Intuition: </strong>{content.simpleExplanation}</span>
              </div>
            )}
          </div>
        )}

        {/* Vector Diagram Section */}
        {content.diagram && (
          <div className="bg-white border-2 border-slate-300 rounded-xl p-4 shadow-sm">
            <div className="flex items-center justify-between mb-2">
              <span className="font-sans text-xs font-bold text-indigo-900 uppercase tracking-wider flex items-center gap-1.5">
                <Layers className="w-3.5 h-3.5 text-indigo-600" />
                {content.diagram.title || 'Schematic / Diagram'}
              </span>
              <span className="text-[10px] font-sans font-medium px-2 py-0.5 rounded bg-slate-100 text-slate-600">
                Vector Visual
              </span>
            </div>

            {/* Embedded Raw SVG */}
            {sanitizedSvg ? (
              <div
                className="overflow-x-auto my-2 flex justify-center w-full"
                dangerouslySetInnerHTML={{ __html: sanitizedSvg }}
              />
            ) : (
              <div className="p-4 bg-slate-50 rounded-lg text-center text-xs text-slate-500 font-sans">
                Diagram trace: {content.diagram.title}
              </div>
            )}

            {content.diagram.caption && (
              <p className="text-xs text-slate-500 font-sans italic text-center mt-2">
                {content.diagram.caption}
              </p>
            )}
          </div>
        )}

        {/* Algorithm / Step-by-Step Procedure */}
        {content.algorithm && content.algorithm.length > 0 && (
          <div className="bg-sky-50/60 border border-sky-200 rounded-xl p-4 shadow-xs">
            <h3 className="font-sans font-bold text-xs uppercase tracking-wider text-sky-900 mb-3 flex items-center gap-1.5">
              <Bookmark className="w-3.5 h-3.5 text-sky-600" />
              Algorithm & Step Execution Trace
            </h3>
            <div className="space-y-2.5">
              {content.algorithm.map((step, idx) => (
                <div key={idx} className="flex items-start gap-2.5 text-[17px]">
                  <span className="w-6 h-6 rounded-full bg-sky-200 text-sky-900 font-sans font-bold text-xs flex items-center justify-center shrink-0 mt-0.5">
                    {step.stepNumber || idx + 1}
                  </span>
                  <div className="flex-1">
                    <p className="text-slate-800">{step.instruction}</p>
                    {step.codeSnippet && (
                      <code className="block mt-1 font-mono text-xs bg-white border border-sky-200 text-sky-950 px-2.5 py-1.5 rounded-md">
                        {step.codeSnippet}
                      </code>
                    )}
                    {step.note && (
                      <span className="text-xs font-sans text-sky-700 italic block mt-0.5">
                        Note: {step.note}
                      </span>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Structured Sections */}
        {content.sections && content.sections.length > 0 && (
          <div className="space-y-4">
            {content.sections.map((sec, idx) => (
              <div key={idx} className="space-y-1.5">
                <div className="flex items-center gap-2">
                  <span className="w-2 h-2 rounded-full bg-indigo-600" />
                  <h4 className="font-bold text-[19px] text-indigo-950 font-hand">
                    {sec.heading}
                  </h4>
                  {sec.badge && (
                    <span className="text-[10px] font-sans font-semibold bg-slate-200/80 text-slate-700 px-1.5 py-0.5 rounded">
                      {sec.badge}
                    </span>
                  )}
                </div>
                {sec.content && (
                  <p className="text-slate-800 text-[17px] pl-4">{sec.content}</p>
                )}
                {sec.bulletPoints && sec.bulletPoints.length > 0 && (
                  <ul className="list-disc list-inside space-y-1 pl-4 text-slate-800">
                    {sec.bulletPoints.map((pt, pIdx) => (
                      <li key={pIdx} className="leading-snug">
                        {pt}
                      </li>
                    ))}
                  </ul>
                )}
              </div>
            ))}
          </div>
        )}

        {/* Formula Details */}
        {content.formula && (
          <div className="bg-emerald-50/70 border border-emerald-300 rounded-xl p-4">
            <h4 className="font-sans font-bold text-xs uppercase tracking-wider text-emerald-900 mb-2">
              Formula: {content.formula.title}
            </h4>
            <div className="bg-white border border-emerald-200 rounded-lg p-3 text-center my-2 font-mono font-bold text-base sm:text-lg text-emerald-950">
              {content.formula.expression}
            </div>
            {content.formula.explanation && (
              <p className="text-sm font-hand text-slate-700 mt-1">
                {content.formula.explanation}
              </p>
            )}
            {content.formula.variables && content.formula.variables.length > 0 && (
              <div className="mt-2 text-xs font-sans text-slate-600 space-y-0.5">
                <span className="font-semibold text-slate-800">Variables:</span>
                {content.formula.variables.map((v, vIdx) => (
                  <div key={vIdx} className="pl-2">
                    <span className="font-mono font-bold text-emerald-800">{v.symbol}</span>: {v.meaning} {v.unit ? `(${v.unit})` : ''}
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* Worked Example */}
        {content.example && (
          <div className="bg-violet-50/60 border border-violet-200 rounded-xl p-4 shadow-xs">
            <h4 className="font-sans font-bold text-xs uppercase tracking-wider text-violet-900 mb-1.5">
              Worked Example: {content.example.title}
            </h4>
            {content.example.scenario && (
              <p className="text-sm font-sans text-slate-700 mb-2 italic">
                {content.example.scenario}
              </p>
            )}
            {content.example.input && (
              <div className="text-xs font-mono bg-white p-2 rounded border border-violet-200 text-violet-950 mb-2">
                Input: {content.example.input}
              </div>
            )}
            {content.example.stepByStep && (
              <ol className="list-decimal list-inside space-y-1 text-[16px] text-slate-800 pl-2">
                {content.example.stepByStep.map((s, sIdx) => (
                  <li key={sIdx}>{s}</li>
                ))}
              </ol>
            )}
            {content.example.outputOrResult && (
              <div className="mt-2 text-sm font-sans font-semibold text-violet-900 bg-violet-100/70 p-2 rounded">
                Outcome: {content.example.outputOrResult}
              </div>
            )}
          </div>
        )}

        {/* Comparison Table */}
        {content.comparisonTable && (
          <div className="overflow-x-auto bg-white border border-slate-300 rounded-xl p-3 shadow-xs">
            <h4 className="font-sans font-bold text-xs uppercase tracking-wider text-slate-800 mb-2">
              {content.comparisonTable.title}
            </h4>
            <table className="w-full text-left text-xs font-sans border-collapse">
              <thead>
                <tr className="bg-slate-100 border-b border-slate-300">
                  {content.comparisonTable.headers.map((h, hIdx) => (
                    <th key={hIdx} className="p-2 font-bold text-slate-800">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-200">
                {content.comparisonTable.rows.map((row, rIdx) => (
                  <tr key={rIdx} className="hover:bg-slate-50">
                    {row.map((cell, cIdx) => (
                      <td key={cIdx} className="p-2 text-slate-700">{cell}</td>
                    ))}
                  </tr>
                ))}
              </tbody>
            </table>
            {content.comparisonTable.conclusion && (
              <p className="text-xs font-sans italic text-slate-500 mt-2">
                {content.comparisonTable.conclusion}
              </p>
            )}
          </div>
        )}

        {/* Complexity Chips */}
        {content.complexity && (
          <div className="bg-slate-100/90 border border-slate-300 rounded-xl p-3 flex flex-wrap items-center justify-between gap-3 text-xs font-sans">
            <div className="flex items-center gap-2">
              <Clock className="w-4 h-4 text-indigo-600" />
              <span className="font-bold text-slate-700">Time Complexity:</span>
              <span className="font-mono bg-white px-2 py-0.5 rounded border border-slate-300 font-bold text-indigo-900">
                {content.complexity.timeAverage || content.complexity.timeWorst || 'O(n)'}
              </span>
            </div>
            <div className="flex items-center gap-2">
              <Database className="w-4 h-4 text-emerald-600" />
              <span className="font-bold text-slate-700">Aux Space:</span>
              <span className="font-mono bg-white px-2 py-0.5 rounded border border-slate-300 font-bold text-emerald-900">
                {content.complexity.space || 'O(1)'}
              </span>
            </div>
            {content.complexity.explanation && (
              <p className="w-full text-slate-600 text-[11px] pt-1 border-t border-slate-200">
                {content.complexity.explanation}
              </p>
            )}
          </div>
        )}

        {/* Key Points & High-Yield Exam Tips */}
        {((content.keyPoints && content.keyPoints.length > 0) || (content.examTips && content.examTips.length > 0)) && (
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 pt-2">
            {/* High Yield Key Points */}
            {content.keyPoints && content.keyPoints.length > 0 && (
              <div className="bg-amber-100/60 border border-amber-300 rounded-xl p-3.5 hand-box">
                <h4 className="font-sans font-bold text-xs uppercase tracking-wider text-amber-950 mb-2 flex items-center gap-1.5">
                  <Star className="w-3.5 h-3.5 text-amber-600 fill-amber-500" />
                  Key Invariants
                </h4>
                <ul className="space-y-1.5 text-[16px] text-slate-900">
                  {content.keyPoints.map((kp, kIdx) => (
                    <li key={kIdx} className="flex items-start gap-1.5">
                      <span className="text-amber-700 font-bold mt-0.5">&bull;</span>
                      <span>{kp.point}</span>
                    </li>
                  ))}
                </ul>
              </div>
            )}

            {/* Exam Tips & Common Mistakes */}
            {content.examTips && content.examTips.length > 0 && (
              <div className="bg-rose-50/80 border border-rose-300 rounded-xl p-3.5 hand-box-alt">
                <h4 className="font-sans font-bold text-xs uppercase tracking-wider text-rose-950 mb-2 flex items-center gap-1.5">
                  <AlertTriangle className="w-3.5 h-3.5 text-rose-600" />
                  Exam Traps & Mnemonics
                </h4>
                <div className="space-y-2 text-[15px] text-slate-900">
                  {content.examTips.map((tip, tIdx) => (
                    <div key={tIdx} className="space-y-0.5">
                      <p className="font-medium text-slate-800">{tip.tip}</p>
                      {tip.commonMistake && (
                        <p className="text-xs font-sans text-rose-700 font-medium">
                          Common Pitfall: {tip.commonMistake}
                        </p>
                      )}
                      {tip.mnemonic && (
                        <p className="text-xs font-sans text-indigo-700 font-semibold bg-white/80 px-2 py-0.5 rounded border border-indigo-200 inline-block">
                          Mnemonic: {tip.mnemonic}
                        </p>
                      )}
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}
      </div>

      {/* Page Footer Note */}
      <div className="pl-6 sm:pl-10 mt-10 pt-4 border-t border-slate-300 text-xs font-sans text-slate-400 flex items-center justify-between">
        <span>Handwritten Visual Revision Note &bull; AI Visual Notes</span>
        <span className="font-semibold text-slate-600">
          Page {pageNumber} of {totalPages}
        </span>
      </div>
    </div>
  );
}
