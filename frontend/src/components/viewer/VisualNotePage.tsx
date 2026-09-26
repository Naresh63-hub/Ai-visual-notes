import React from 'react';
import { PageContent, NoteStyle } from '../../types';
import { DiagramRenderer } from './DiagramRenderer';
import { HandUnderline } from '../handwritten/HandUnderline';
import { HandwrittenBox } from '../handwritten/HandwrittenBox';
import { HandwrittenTable } from '../handwritten/HandwrittenTable';
import { getHandwritingStyle } from '../../utils/handwritingVariation';

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
  const seedPrefix = `p${pageNumber}_${content.topicTitle || 'notes'}`;

  return (
    <div
      id={`visual-note-page-${pageNumber}`}
      className="a4-page-container relative p-8 sm:p-14 bg-white border border-slate-200/80 shadow-page overflow-hidden flex flex-col justify-between font-hand text-[#172554]"
      style={{ minHeight: '1140px', backgroundColor: '#ffffff' }}
    >
      {/* Top Header & Content Flow */}
      <div className="w-full">
        {/* Top Student Header Margin / Meta Line */}
        <div className="flex items-center justify-between border-b border-[#1e3a8a]/20 pb-2 mb-6 text-sm text-[#1e3a8a]/80">
          <div className="flex items-center gap-3">
            <span
              className="font-bold text-[#172554] tracking-wide"
              style={getHandwritingStyle(`${seedPrefix}_meta_cat`, 'subtle')}
            >
              {content.categoryBadge || 'Study Notes'}
            </span>
            {content.difficultyLevel && (
              <span style={getHandwritingStyle(`${seedPrefix}_meta_diff`, 'subtle')}>
                — {content.difficultyLevel}
              </span>
            )}
            {content.pagePartTitle && (
              <span
                className="font-semibold text-[#1e3a8a]"
                style={getHandwritingStyle(`${seedPrefix}_meta_part`, 'subtle')}
              >
                ({content.pagePartTitle})
              </span>
            )}
          </div>

          <div className="text-right flex items-center gap-4">
            {content.isContinuation && (
              <span
                className="text-xs italic text-[#1e3a8a]/70"
                style={getHandwritingStyle(`${seedPrefix}_cont_hdr`, 'subtle')}
              >
                [Continued from Page {pageNumber - 1}]
              </span>
            )}
            <span
              className="font-bold text-[#172554] text-base"
              style={getHandwritingStyle(`${seedPrefix}_page_num`, 'subtle')}
            >
              Page {pageNumber} of {totalPages}
            </span>
          </div>
        </div>

        {/* 1. Main Handwritten Title */}
        <div className="mb-6">
          <div className="inline-block max-w-full">
            <h1
              className="text-3xl sm:text-4xl font-bold text-[#172554] tracking-tight leading-tight"
              style={getHandwritingStyle(`${seedPrefix}_title`, 'heading')}
            >
              {content.topicTitle}
            </h1>
            <HandUnderline
              seed={`${seedPrefix}_title_u`}
              isDouble={true}
              strokeWidth={2}
              strokeColor="#172554"
              className="mt-1"
            />
          </div>

          {content.topicSubtitle && (
            <p
              className="text-lg sm:text-xl text-[#1e3a8a]/90 mt-1.5 italic"
              style={getHandwritingStyle(`${seedPrefix}_subtitle`, 'normal')}
            >
              {content.topicSubtitle}
            </p>
          )}
        </div>

        {/* 2. Definition & Core Intuition (Continuous Handwritten Flow) */}
        {(content.definition || content.purpose || content.mainIdea || content.simpleExplanation) && (
          <div className="mb-6 space-y-3">
            {content.definition && (
              <div>
                <div className="inline-block mb-1">
                  <h3
                    className="text-xl sm:text-2xl font-bold text-[#172554]"
                    style={getHandwritingStyle(`${seedPrefix}_def_h`, 'heading')}
                  >
                    1. Definition &amp; Foundations
                  </h3>
                  <HandUnderline seed={`${seedPrefix}_def_u`} strokeWidth={1.5} strokeColor="#1e3a8a" />
                </div>
                <p
                  className="text-lg sm:text-xl text-[#1e3a8a] leading-relaxed mt-1"
                  style={getHandwritingStyle(`${seedPrefix}_def_body`, 'normal')}
                >
                  {content.definition}
                </p>
                {content.purpose && (
                  <p
                    className="text-base sm:text-lg text-[#1e3a8a] mt-1.5"
                    style={getHandwritingStyle(`${seedPrefix}_def_purp`, 'normal')}
                  >
                    <span className="font-bold text-[#172554]">Purpose: </span>
                    {content.purpose}
                  </p>
                )}
              </div>
            )}

            {(content.mainIdea || content.simpleExplanation) && (
              <div className="mt-4">
                <div className="inline-block mb-1">
                  <h3
                    className="text-xl sm:text-2xl font-bold text-[#172554]"
                    style={getHandwritingStyle(`${seedPrefix}_core_h`, 'heading')}
                  >
                    2. Core Concept &amp; Mechanism
                  </h3>
                  <HandUnderline seed={`${seedPrefix}_core_u`} strokeWidth={1.5} strokeColor="#1e3a8a" />
                </div>
                <p
                  className="text-lg sm:text-xl text-[#1e3a8a] leading-relaxed mt-1"
                  style={getHandwritingStyle(`${seedPrefix}_core_body`, 'normal')}
                >
                  {content.mainIdea || content.simpleExplanation}
                </p>
              </div>
            )}
          </div>
        )}

        {/* 3. Structured Topic Sections (Continuous Flow) */}
        {content.sections && content.sections.length > 0 && (
          <div className="mb-6 space-y-4">
            {content.sections.map((sec, sidx) => (
              <div key={sidx} className="space-y-1.5">
                <div className="inline-block">
                  <h3
                    className="text-xl sm:text-2xl font-bold text-[#172554]"
                    style={getHandwritingStyle(`${seedPrefix}_sec_h_${sidx}`, 'heading')}
                  >
                    {sec.heading}
                    {sec.badge && (
                      <span className="text-sm font-semibold text-[#1e3a8a]/80 ml-2">
                        [{sec.badge}]
                      </span>
                    )}
                  </h3>
                  <HandUnderline seed={`${seedPrefix}_sec_u_${sidx}`} strokeWidth={1.4} strokeColor="#1e3a8a" />
                </div>

                {sec.content && (
                  <p
                    className="text-lg sm:text-xl text-[#1e3a8a] leading-relaxed"
                    style={getHandwritingStyle(`${seedPrefix}_sec_c_${sidx}`, 'normal')}
                  >
                    {sec.content}
                  </p>
                )}

                {sec.bulletPoints && sec.bulletPoints.length > 0 && (
                  <ul className="space-y-1.5 mt-2 pl-2">
                    {sec.bulletPoints.map((bp, bidx) => (
                      <li
                        key={bidx}
                        className="text-base sm:text-lg text-[#1e3a8a] flex items-start gap-2.5 leading-snug"
                        style={getHandwritingStyle(`${seedPrefix}_sec_bp_${sidx}_${bidx}`, 'subtle')}
                      >
                        <span className="text-[#172554] font-bold text-lg leading-none shrink-0 mt-0.5">
                          •
                        </span>
                        <span>{bp}</span>
                      </li>
                    ))}
                  </ul>
                )}
              </div>
            ))}
          </div>
        )}

        {/* 4. Hand-drawn Diagram */}
        {content.diagram && (
          <div className="my-6">
            <DiagramRenderer diagram={content.diagram} style={style} />
          </div>
        )}

        {/* 5. Structured Comparison Table */}
        {content.comparisonTable && (
          <HandwrittenTable
            table={content.comparisonTable}
            seed={`${seedPrefix}_table`}
          />
        )}

        {/* 6. Formula / Equation in Hand-drawn Functional Box */}
        {content.formula && (
          <HandwrittenBox
            seed={`${seedPrefix}_formula_box`}
            className="my-5"
            padding="p-4 sm:p-5"
          >
            <div className="text-center">
              <div
                className="text-base sm:text-lg font-bold text-[#172554] mb-1"
                style={getHandwritingStyle(`${seedPrefix}_form_title`, 'heading')}
              >
                {content.formula.title || 'Mathematical Formulation'}
              </div>
              <div
                className="text-2xl sm:text-3xl font-bold text-[#172554] py-2 tracking-wider font-mono sm:font-hand"
                style={getHandwritingStyle(`${seedPrefix}_form_expr`, 'heading')}
              >
                {content.formula.expression}
              </div>
              {content.formula.explanation && (
                <p
                  className="text-base sm:text-lg text-[#1e3a8a] mt-1.5"
                  style={getHandwritingStyle(`${seedPrefix}_form_expl`, 'subtle')}
                >
                  {content.formula.explanation}
                </p>
              )}
              {content.formula.variables && content.formula.variables.length > 0 && (
                <div className="mt-3 pt-2 border-t border-dashed border-[#1e3a8a]/30 flex flex-wrap justify-center gap-x-5 gap-y-1 text-sm sm:text-base text-[#1e3a8a]">
                  {content.formula.variables.map((v, vi) => (
                    <div key={vi}>
                      <span className="font-bold text-[#172554]">{v.symbol}: </span>
                      <span>{v.meaning}</span>
                      {v.unit && <span className="text-[#1e3a8a]/70"> ({v.unit})</span>}
                    </div>
                  ))}
                </div>
              )}
            </div>
          </HandwrittenBox>
        )}

        {/* 7. Worked Example (Continuous or in Subtle Box) */}
        {content.example && (
          <div className="my-5 space-y-2">
            <div className="inline-block mb-1">
              <h4
                className="text-lg sm:text-xl font-bold text-[#172554]"
                style={getHandwritingStyle(`${seedPrefix}_ex_title`, 'heading')}
              >
                Example: {content.example.title}
              </h4>
              <HandUnderline seed={`${seedPrefix}_ex_u`} strokeWidth={1.4} strokeColor="#1e3a8a" />
            </div>

            {content.example.scenario && (
              <p
                className="text-base sm:text-lg text-[#1e3a8a] italic"
                style={getHandwritingStyle(`${seedPrefix}_ex_scen`, 'normal')}
              >
                {content.example.scenario}
              </p>
            )}

            {content.example.stepByStep && content.example.stepByStep.length > 0 && (
              <div className="space-y-1 pl-2">
                {content.example.stepByStep.map((s, si) => (
                  <div
                    key={si}
                    className="text-base sm:text-lg text-[#1e3a8a] flex items-start gap-2"
                    style={getHandwritingStyle(`${seedPrefix}_ex_s_${si}`, 'subtle')}
                  >
                    <span className="font-bold text-[#172554] shrink-0">→</span>
                    <span>{s}</span>
                  </div>
                ))}
              </div>
            )}

            {content.example.outputOrResult && (
              <div
                className="mt-2 text-base sm:text-lg font-bold text-[#172554]"
                style={getHandwritingStyle(`${seedPrefix}_ex_res`, 'normal')}
              >
                Result: {content.example.outputOrResult}
              </div>
            )}
          </div>
        )}

        {/* 8. Algorithm & Pseudocode (if present) */}
        {((content.algorithm && content.algorithm.length > 0) || content.pseudocode) && (
          <div className="my-5 space-y-3">
            {content.algorithm && content.algorithm.length > 0 && (
              <div>
                <div className="inline-block mb-1">
                  <h4
                    className="text-lg sm:text-xl font-bold text-[#172554]"
                    style={getHandwritingStyle(`${seedPrefix}_algo_h`, 'heading')}
                  >
                    Algorithm Steps
                  </h4>
                  <HandUnderline seed={`${seedPrefix}_algo_u`} strokeWidth={1.4} strokeColor="#1e3a8a" />
                </div>
                <div className="space-y-1.5 pl-2 mt-1">
                  {content.algorithm.map((step, idx) => (
                    <div
                      key={idx}
                      className="text-base sm:text-lg text-[#1e3a8a] flex items-start gap-2"
                      style={getHandwritingStyle(`${seedPrefix}_algo_st_${idx}`, 'subtle')}
                    >
                      <span className="font-bold text-[#172554] shrink-0">
                        Step {step.stepNumber}:
                      </span>
                      <span>{step.instruction}</span>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {content.pseudocode && (
              <HandwrittenBox
                seed={`${seedPrefix}_pseudo_box`}
                className="my-3"
                padding="p-3 sm:p-4"
              >
                <pre
                  className="font-mono text-sm sm:text-base text-[#172554] overflow-x-auto leading-relaxed whitespace-pre-wrap"
                >
                  {content.pseudocode}
                </pre>
              </HandwrittenBox>
            )}
          </div>
        )}

        {/* 9. Complexity Summary (if present) */}
        {content.complexity && (
          <div className="my-4">
            <span className="font-bold text-base sm:text-lg text-[#172554]">
              Complexity:
            </span>{' '}
            <span className="text-base sm:text-lg text-[#1e3a8a]">
              Time: {content.complexity.timeWorst || content.complexity.timeAverage} | Space: {content.complexity.space}
            </span>
          </div>
        )}

        {/* 10. Advantages & Limitations (if present) */}
        {((content.advantages && content.advantages.length > 0) || (content.limitations && content.limitations.length > 0)) && (
          <div className="my-5 grid grid-cols-1 sm:grid-cols-2 gap-4">
            {content.advantages && content.advantages.length > 0 && (
              <div className="space-y-1">
                <span className="font-bold text-base sm:text-lg text-[#172554]">
                  Key Advantages:
                </span>
                <ul className="space-y-1 pl-2">
                  {content.advantages.map((adv, ai) => (
                    <li
                      key={ai}
                      className="text-base sm:text-lg text-[#1e3a8a] flex items-start gap-2"
                      style={getHandwritingStyle(`${seedPrefix}_adv_${ai}`, 'subtle')}
                    >
                      <span className="text-[#172554] font-bold">✓</span>
                      <span>{adv}</span>
                    </li>
                  ))}
                </ul>
              </div>
            )}

            {content.limitations && content.limitations.length > 0 && (
              <div className="space-y-1">
                <span className="font-bold text-base sm:text-lg text-[#172554]">
                  Limitations / Trade-offs:
                </span>
                <ul className="space-y-1 pl-2">
                  {content.limitations.map((lim, li) => (
                    <li
                      key={li}
                      className="text-base sm:text-lg text-[#1e3a8a] flex items-start gap-2"
                      style={getHandwritingStyle(`${seedPrefix}_lim_${li}`, 'subtle')}
                    >
                      <span className="text-[#172554] font-bold">✗</span>
                      <span>{lim}</span>
                    </li>
                  ))}
                </ul>
              </div>
            )}
          </div>
        )}
      </div>

      {/* Footer: Hand-Drawn Functional Note Box for Key Takeaways / Exam Tips */}
      <div className="w-full mt-6 pt-3 border-t border-[#1e3a8a]/20">
        {(content.keyPoints?.length || content.examTips?.length) ? (
          <HandwrittenBox
            seed={`${seedPrefix}_footer_box`}
            className="my-1"
            padding="p-3.5 sm:p-4"
          >
            <div className="space-y-2">
              {content.keyPoints && content.keyPoints.length > 0 && (
                <div>
                  <span className="font-bold text-base sm:text-lg text-[#172554]">
                    Key Takeaways:
                  </span>
                  <ul className="space-y-1 pl-2 mt-1">
                    {content.keyPoints.slice(0, 3).map((kp, kpi) => (
                      <li
                        key={kpi}
                        className="text-base sm:text-lg text-[#1e3a8a] flex items-start gap-2"
                        style={getHandwritingStyle(`${seedPrefix}_kp_${kpi}`, 'subtle')}
                      >
                        <span className="text-[#172554] font-bold">•</span>
                        <span>{kp.point}</span>
                      </li>
                    ))}
                  </ul>
                </div>
              )}

              {content.examTips && content.examTips.length > 0 && content.examTips[0].tip && (
                <div className="pt-1.5 border-t border-dashed border-[#1e3a8a]/30 text-base sm:text-lg text-[#1e3a8a]">
                  <span className="font-bold text-[#172554]">Exam Tip: </span>
                  <span style={getHandwritingStyle(`${seedPrefix}_exam_tip`, 'subtle')}>
                    {content.examTips[0].tip}
                  </span>
                  {content.examTips[0].mnemonic && (
                    <div className="mt-1 text-sm sm:text-base font-bold text-[#172554]">
                      Mnemonic: {content.examTips[0].mnemonic}
                    </div>
                  )}
                </div>
              )}
            </div>
          </HandwrittenBox>
        ) : null}

        {/* Continuation Note */}
        {content.continuesOnNextPage && (
          <div
            className="text-right text-base font-bold text-[#172554] mt-2 italic"
            style={getHandwritingStyle(`${seedPrefix}_cont_ftr`, 'subtle')}
          >
            Notes continue on next page →
          </div>
        )}
      </div>
    </div>
  );
};
