import React from 'react';
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
  return (
    <div
      id={`visual-note-page-${pageNumber}`}
      className="a4-page-container relative p-8 sm:p-12 rounded-xl bg-white border border-slate-200 shadow-page overflow-hidden flex flex-col justify-between font-hand text-[#172554]"
      style={{ minHeight: '1140px' }}
    >
      {/* Top Header */}
      <div>
        {/* Continuation Banner */}
        {content.isContinuation && (
          <div className="mb-3 px-3 py-1.5 rounded-lg bg-blue-50/70 border border-blue-200 text-[#1e3a8a] flex items-center justify-between text-sm font-semibold">
            <span>← Continued from Page {pageNumber - 1}</span>
            <span className="font-bold">{content.pagePartTitle || `Part ${pageNumber} of ${totalPages}`}</span>
          </div>
        )}

        {/* Page Title & Subject Header */}
        <div className="flex items-start justify-between gap-4 border-b-2 border-slate-200 pb-3 mb-5">
          <div className="flex-1">
            <div className="flex items-center gap-2 mb-1">
              <span className="text-xs font-bold uppercase tracking-wider px-2.5 py-0.5 rounded-md bg-blue-50 text-[#1e3a8a] border border-blue-200">
                {content.categoryBadge || 'Study Notes'}
              </span>
              {content.difficultyLevel && (
                <span className="text-xs font-semibold text-slate-500">
                  • {content.difficultyLevel}
                </span>
              )}
              {content.pagePartTitle && (
                <span className="text-xs font-bold text-[#1e3a8a] bg-slate-100 px-2 py-0.5 rounded border border-slate-200">
                  {content.pagePartTitle}
                </span>
              )}
            </div>

            <h1 className="text-3xl sm:text-4xl font-bold tracking-tight text-[#172554] double-underline-blue">
              {content.topicTitle}
            </h1>

            {content.topicSubtitle && (
              <p className="text-base text-[#1e3a8a]/80 mt-1 italic">
                {content.topicSubtitle}
              </p>
            )}
          </div>

          <div className="text-right shrink-0">
            <div className="inline-block px-3 py-1 rounded-md bg-blue-50 border border-blue-200 text-xs font-bold text-[#1e3a8a]">
              Page {pageNumber} of {totalPages}
            </div>
            <div className="text-[10px] text-slate-400 mt-1 font-semibold tracking-widest uppercase">
              AI Visual Notes
            </div>
          </div>
        </div>

        {/* 1. Core Definition & Main Intuition */}
        {(content.definition || content.purpose || content.mainIdea || content.simpleExplanation) && (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
            {content.definition && (
              <div className="p-4 rounded-lg bg-slate-50/70 border border-slate-200">
                <div className="font-bold text-sm text-[#172554] ink-section-blue mb-1">
                  1. Definition &amp; Foundations
                </div>
                <p className="text-base text-[#1e3a8a] leading-relaxed">
                  {content.definition}
                </p>
                {content.purpose && (
                  <p className="text-base text-[#1e3a8a] mt-2 font-medium">
                    <span className="font-bold text-[#172554]">Purpose: </span>
                    {content.purpose}
                  </p>
                )}
              </div>
            )}

            {(content.mainIdea || content.simpleExplanation) && (
              <div className="p-4 rounded-lg bg-blue-50/40 border border-blue-200/80">
                <div className="font-bold text-sm text-[#172554] ink-section-blue mb-1">
                  2. Core Working Principle
                </div>
                <p className="text-base text-[#1e3a8a] leading-relaxed">
                  {content.mainIdea || content.simpleExplanation}
                </p>
              </div>
            )}
          </div>
        )}

        {/* 2. Structured Comparison Table (if present) */}
        {content.comparisonTable && (
          <div className="my-4 p-4 rounded-lg bg-white border-2 border-slate-200 shadow-2xs">
            <div className="font-bold text-base text-[#172554] ink-section-blue mb-2.5">
              📊 {content.comparisonTable.title || 'Comparative Analysis'}
            </div>
            <div className="overflow-x-auto">
              <table className="handwritten-table">
                <thead>
                  <tr>
                    {content.comparisonTable.headers.map((header, hidx) => (
                      <th key={hidx} className="text-sm font-bold text-[#172554] bg-slate-100 border-b-2 border-blue-900">
                        {header}
                      </th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {content.comparisonTable.rows.map((row, ridx) => (
                    <tr key={ridx} className="border-b border-slate-200">
                      {row.map((cell, cidx) => (
                        <td key={cidx} className={`text-sm py-2 px-3 ${cidx === 0 ? 'font-bold text-[#172554]' : 'text-[#1e3a8a]'}`}>
                          {cell}
                        </td>
                      ))}
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            {content.comparisonTable.conclusion && (
              <div className="mt-2.5 pt-2 border-t border-slate-200 text-xs text-[#1e3a8a] font-medium italic">
                <span className="font-bold text-[#172554]">Key Takeaway: </span>
                {content.comparisonTable.conclusion}
              </div>
            )}
          </div>
        )}

        {/* 3. Labeled Visual Diagram */}
        {content.diagram && (
          <div className="my-4">
            <DiagramRenderer diagram={content.diagram} style={style} />
          </div>
        )}

        {/* 4. Structured Topic Sections */}
        {content.sections && content.sections.length > 0 && (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 my-4">
            {content.sections.map((sec, sidx) => (
              <div
                key={sidx}
                className="p-4 rounded-lg bg-white border border-slate-200 shadow-2xs"
              >
                <div className="flex items-center justify-between mb-2">
                  <h4 className="font-bold text-base text-[#172554] ink-section-blue">
                    {sec.heading}
                  </h4>
                  {sec.badge && (
                    <span className="text-xs font-bold px-2 py-0.5 rounded bg-blue-50 text-[#1e3a8a] border border-blue-200">
                      {sec.badge}
                    </span>
                  )}
                </div>

                {sec.content && (
                  <p className="text-sm text-[#1e3a8a] leading-relaxed mb-2">
                    {sec.content}
                  </p>
                )}

                {sec.bulletPoints && sec.bulletPoints.length > 0 && (
                  <ul className="space-y-1">
                    {sec.bulletPoints.map((bp, bidx) => (
                      <li key={bidx} className="text-sm text-[#1e3a8a] flex items-start gap-2">
                        <span className="text-[#172554] font-bold shrink-0">•</span>
                        <span>{bp}</span>
                      </li>
                    ))}
                  </ul>
                )}
              </div>
            ))}
          </div>
        )}

        {/* 5. Algorithm & Pseudocode (if present) */}
        {((content.algorithm && content.algorithm.length > 0) || content.pseudocode) && (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 my-4">
            {content.algorithm && content.algorithm.length > 0 && (
              <div className="p-4 rounded-lg bg-slate-50/80 border border-slate-200">
                <div className="font-bold text-sm text-[#172554] ink-section-blue mb-2.5">
                  Step-by-Step Algorithm
                </div>
                <div className="space-y-2">
                  {content.algorithm.map((step, idx) => (
                    <div key={idx} className="flex items-start gap-2 text-sm text-[#1e3a8a]">
                      <span className="w-5 h-5 rounded-full bg-[#1e3a8a] text-white font-bold text-xs shrink-0 flex items-center justify-center">
                        {step.stepNumber}
                      </span>
                      <div className="flex-1">
                        <span className="font-semibold">{step.instruction}</span>
                        {step.codeSnippet && (
                          <pre className="mt-1 p-2 rounded bg-slate-900 text-emerald-300 font-mono text-xs overflow-x-auto">
                            {step.codeSnippet}
                          </pre>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {content.pseudocode && (
              <div className="p-4 rounded-lg bg-slate-50/80 border border-slate-200">
                <div className="font-bold text-sm text-[#172554] ink-section-blue mb-2">
                  Pseudocode Implementation
                </div>
                <pre className="p-3 rounded bg-slate-900 text-emerald-300 font-mono text-xs overflow-x-auto leading-relaxed border border-slate-700">
                  {content.pseudocode}
                </pre>
              </div>
            )}
          </div>
        )}

        {/* 6. Formula & Worked Example Row */}
        {(content.formula || content.example || content.complexity) && (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 my-4">
            {/* Worked Example */}
            {content.example && (
              <div className="p-4 rounded-lg bg-amber-50/40 border border-amber-200/80">
                <div className="font-bold text-sm text-[#172554] ink-section-blue mb-1.5">
                  ✏️ Example: {content.example.title}
                </div>
                {content.example.scenario && (
                  <p className="text-xs text-[#1e3a8a] mb-2 italic">
                    {content.example.scenario}
                  </p>
                )}
                {content.example.stepByStep && (
                  <div className="space-y-1 my-2">
                    {content.example.stepByStep.map((s, si) => (
                      <div key={si} className="text-xs text-[#1e3a8a] bg-white/90 p-1.5 rounded border border-amber-200">
                        {s}
                      </div>
                    ))}
                  </div>
                )}
                {content.example.outputOrResult && (
                  <div className="mt-2 p-2 rounded bg-emerald-50 text-emerald-950 border border-emerald-200 text-xs font-semibold">
                    ✓ Result: {content.example.outputOrResult}
                  </div>
                )}
              </div>
            )}

            {/* Formula or Complexity */}
            <div className="space-y-3">
              {content.formula && (
                <div className="p-4 rounded-lg bg-slate-50/80 border border-slate-200">
                  <div className="font-bold text-sm text-[#172554] ink-section-blue mb-1">
                    📐 {content.formula.title}
                  </div>
                  <div className="p-2.5 rounded bg-blue-950 text-blue-100 font-mono text-sm font-bold text-center my-1.5 tracking-wide">
                    {content.formula.expression}
                  </div>
                  {content.formula.explanation && (
                    <p className="text-xs text-[#1e3a8a] mt-1.5">
                      {content.formula.explanation}
                    </p>
                  )}
                  {content.formula.variables && content.formula.variables.length > 0 && (
                    <div className="mt-2 pt-2 border-t border-slate-200 space-y-0.5">
                      {content.formula.variables.map((v, vi) => (
                        <div key={vi} className="text-xs text-[#1e3a8a]">
                          <span className="font-bold text-[#172554]">{v.symbol}:</span> {v.meaning} {v.unit ? `(${v.unit})` : ''}
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              )}

              {content.complexity && (
                <div className="p-3.5 rounded-lg bg-slate-50/80 border border-slate-200">
                  <div className="flex items-center justify-between mb-1.5">
                    <span className="font-bold text-sm text-[#172554] ink-section-blue">
                      ⏱️ Complexity Analysis
                    </span>
                    {content.complexity.space && (
                      <span className="text-xs font-bold text-[#1e3a8a] bg-blue-50 px-2 py-0.5 rounded border border-blue-200">
                        Space: {content.complexity.space}
                      </span>
                    )}
                  </div>
                  <div className="grid grid-cols-3 gap-1.5 text-center my-1.5">
                    <div className="bg-white p-1.5 rounded border border-slate-200">
                      <span className="text-[10px] text-slate-400 block font-bold">BEST</span>
                      <span className="text-xs font-bold text-emerald-800">{content.complexity.timeBest}</span>
                    </div>
                    <div className="bg-white p-1.5 rounded border border-slate-200">
                      <span className="text-[10px] text-slate-400 block font-bold">AVG</span>
                      <span className="text-xs font-bold text-amber-800">{content.complexity.timeAverage}</span>
                    </div>
                    <div className="bg-white p-1.5 rounded border border-slate-200">
                      <span className="text-[10px] text-slate-400 block font-bold">WORST</span>
                      <span className="text-xs font-bold text-rose-800">{content.complexity.timeWorst}</span>
                    </div>
                  </div>
                  {content.complexity.explanation && (
                    <p className="text-xs text-[#1e3a8a] mt-1 italic">
                      {content.complexity.explanation}
                    </p>
                  )}
                </div>
              )}
            </div>
          </div>
        )}

        {/* 7. Advantages & Limitations (if present) */}
        {((content.advantages && content.advantages.length > 0) || (content.limitations && content.limitations.length > 0)) && (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 my-4">
            {content.advantages && content.advantages.length > 0 && (
              <div className="p-3.5 rounded-lg bg-slate-50/70 border border-slate-200">
                <div className="font-bold text-xs uppercase tracking-wider text-[#172554] mb-1.5">
                  ✓ Advantages / Strengths
                </div>
                <ul className="space-y-1">
                  {content.advantages.map((adv, ai) => (
                    <li key={ai} className="text-xs text-[#1e3a8a] flex items-start gap-1.5">
                      <span className="text-emerald-700 font-bold">✓</span>
                      <span>{adv}</span>
                    </li>
                  ))}
                </ul>
              </div>
            )}

            {content.limitations && content.limitations.length > 0 && (
              <div className="p-3.5 rounded-lg bg-slate-50/70 border border-slate-200">
                <div className="font-bold text-xs uppercase tracking-wider text-[#172554] mb-1.5">
                  ✗ Limitations / Trade-offs
                </div>
                <ul className="space-y-1">
                  {content.limitations.map((lim, li) => (
                    <li key={li} className="text-xs text-[#1e3a8a] flex items-start gap-1.5">
                      <span className="text-rose-700 font-bold">✗</span>
                      <span>{lim}</span>
                    </li>
                  ))}
                </ul>
              </div>
            )}
          </div>
        )}
      </div>

      {/* Footer Revision & Key Points */}
      <div className="mt-5 pt-3 border-t-2 border-slate-200">
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          {/* Key Points */}
          {content.keyPoints && content.keyPoints.length > 0 && (
            <div className="p-3 rounded-lg bg-slate-50/70 border border-slate-200">
              <div className="font-bold text-xs uppercase tracking-wider text-[#172554] mb-1">
                ★ Key Study Takeaways
              </div>
              <ul className="space-y-1">
                {content.keyPoints.slice(0, 3).map((kp, kpi) => (
                  <li key={kpi} className="text-xs text-[#1e3a8a] flex items-start gap-1.5">
                    <span className="text-[#172554] font-bold">★</span>
                    <span>{kp.point}</span>
                  </li>
                ))}
              </ul>
            </div>
          )}

          {/* Exam Tips */}
          {content.examTips && content.examTips.length > 0 && (
            <div className="p-3 rounded-lg bg-slate-50/70 border border-slate-200">
              <div className="font-bold text-xs uppercase tracking-wider text-[#172554] mb-1">
                💡 High-Yield Exam Tips
              </div>
              {content.examTips[0].tip && (
                <p className="text-xs text-[#1e3a8a]">
                  {content.examTips[0].tip}
                </p>
              )}
              {content.examTips[0].mnemonic && (
                <div className="mt-1 text-[11px] font-bold text-[#1e3a8a] bg-blue-50 px-2 py-0.5 rounded border border-blue-200 inline-block">
                  💡 Mnemonic: {content.examTips[0].mnemonic}
                </div>
              )}
            </div>
          )}
        </div>

        {/* Continuation Footer Banner */}
        {content.continuesOnNextPage && (
          <div className="mt-3 py-1.5 px-3 rounded-lg bg-blue-50/60 border border-blue-200 text-[#1e3a8a] flex items-center justify-between text-xs font-semibold">
            <span>Notes continue on next page</span>
            <span className="font-bold text-[#172554]">
              Next Page →
            </span>
          </div>
        )}
      </div>
    </div>
  );
};
