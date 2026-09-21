import React from 'react';
import DOMPurify from 'dompurify';
import { DiagramData, NoteStyle } from '../../types';

interface DiagramRendererProps {
  diagram: DiagramData | undefined;
  style: NoteStyle;
}

const formatDiagramTypeLabel = (type: string): string => {
  const map: Record<string, string> = {
    'physics-diagram': 'Physics Dynamics',
    'science-reaction': 'Biochemical Pathway',
    'dbms-normalization': 'Relational Schema',
    'layer-stack': 'Network Protocol Stack',
    'neural-network': 'Neural Architecture',
    'binary-search-array': 'Array Search Trace',
    'sorting-partition': 'Sorting Partition',
    'tree-traversal': 'Tree Traversal',
    'graph-network': 'Graph Network',
    'process-steps': 'Process Flow',
    'circuit-diagram': 'Circuit Diagram',
    'formula-math': 'Mathematical Formulation',
    'concept-map': 'Concept Architecture'
  };
  return map[type] || type.replace(/-/g, ' ');
};

export const DiagramRenderer: React.FC<DiagramRendererProps> = ({ diagram, style }) => {
  if (!diagram) return null;

  const isHandwritten = style === 'Handwritten';

  // If backend provided raw SVG markup, sanitize with DOMPurify and render
  if (diagram.rawSvg) {
    const sanitizedSvg = DOMPurify.sanitize(diagram.rawSvg, {
      USE_PROFILES: { svg: true, svgFilters: true }
    });

    return (
      <div className="w-full my-3">
        <div className="flex items-center justify-between mb-1.5 px-1">
          <span className={`text-xs font-bold tracking-tight ${
            isHandwritten ? 'font-hand text-base text-slate-800' : 'text-slate-700'
          }`}>
            ✏️ {diagram.title || 'Visual Representation'}
          </span>
          <span className="text-[10px] font-semibold text-indigo-600 uppercase tracking-wider bg-indigo-50 px-2 py-0.5 rounded border border-indigo-100">
            {formatDiagramTypeLabel(diagram.type)}
          </span>
        </div>

        <div
          className={`w-full overflow-x-auto rounded-xl p-3 flex justify-center items-center shadow-2xs ${
            isHandwritten
              ? 'bg-amber-50/40 border-2 border-slate-700 doodle-box'
              : 'bg-slate-50 border border-slate-200'
          }`}
          dangerouslySetInnerHTML={{ __html: sanitizedSvg }}
        />

        {diagram.caption && (
          <p className={`text-[11px] text-slate-500 mt-1.5 text-center italic px-2 ${
            isHandwritten ? 'font-hand text-sm text-slate-600' : ''
          }`}>
            {diagram.caption}
          </p>
        )}
      </div>
    );
  }

  // Built-in vector fallbacks
  return (
    <div className="w-full my-3 p-4 rounded-xl bg-slate-50 border border-slate-200 text-center">
      <h4 className="text-xs font-bold text-slate-700">{diagram.title}</h4>
      <p className="text-xs text-slate-500 mt-1">{diagram.caption}</p>
    </div>
  );
};
