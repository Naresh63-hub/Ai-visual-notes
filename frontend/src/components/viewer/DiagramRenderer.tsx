import React from 'react';
import DOMPurify from 'dompurify';
import { DiagramData, NoteStyle } from '../../types';
import { HandUnderline } from '../handwritten/HandUnderline';
import { getHandwritingStyle } from '../../utils/handwritingVariation';

interface DiagramRendererProps {
  diagram: DiagramData | undefined;
  style: NoteStyle;
}

export const DiagramRenderer: React.FC<DiagramRendererProps> = ({ diagram }) => {
  if (!diagram) return null;

  const titleSeed = `diag_${diagram.title || 'diagram'}`;

  // If backend provided raw SVG markup, sanitize with DOMPurify and render
  if (diagram.rawSvg) {
    const sanitizedSvg = DOMPurify.sanitize(diagram.rawSvg, {
      USE_PROFILES: { svg: true, svgFilters: true }
    });

    return (
      <div className="w-full my-4">
        {diagram.title && (
          <div className="mb-2">
            <div className="inline-block">
              <h4
                className="font-hand font-bold text-lg sm:text-xl text-[#172554] tracking-wide"
                style={getHandwritingStyle(titleSeed, 'heading')}
              >
                {diagram.title}
              </h4>
              <HandUnderline seed={`${titleSeed}_u`} strokeWidth={1.3} strokeColor="#1e3a8a" />
            </div>
          </div>
        )}

        <div
          className="w-full overflow-x-auto my-2 flex justify-center items-center bg-white"
          dangerouslySetInnerHTML={{ __html: sanitizedSvg }}
        />

        {diagram.caption && (
          <p
            className="font-hand text-sm sm:text-base text-[#1e3a8a]/90 mt-1.5 text-center italic px-4"
            style={getHandwritingStyle(`${titleSeed}_caption`, 'subtle')}
          >
            {diagram.caption}
          </p>
        )}
      </div>
    );
  }

  // Fallback if no SVG
  return null;
};
