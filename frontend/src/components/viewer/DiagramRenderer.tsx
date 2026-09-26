import React from 'react';
import DOMPurify from 'dompurify';
import { DiagramData, NoteStyle } from '../../types';

interface DiagramRendererProps {
  diagram: DiagramData | undefined;
  style: NoteStyle;
}

const formatDiagramTypeLabel = (type: string): string => {
  const map: Record<string, string> = {
    'automata-state-transition': 'State Transition Diagram',
    'sql-join-venn': 'Relational Set Operations',
    'os-gantt-chart': 'CPU Scheduling Gantt Chart',
    'circuit-schematic': 'Circuit Schematic',
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

  // If backend provided raw SVG markup, sanitize with DOMPurify and render
  if (diagram.rawSvg) {
    const sanitizedSvg = DOMPurify.sanitize(diagram.rawSvg, {
      USE_PROFILES: { svg: true, svgFilters: true }
    });

    return (
      <div className="w-full my-3">
        <div className="flex items-center justify-between mb-1.5 px-1">
          <span className="font-hand font-bold text-base text-[#172554]">
            ✏️ {diagram.title || 'Visual Representation'}
          </span>
          <span className="text-xs font-semibold text-[#1e3a8a] uppercase tracking-wider bg-blue-50 px-2 py-0.5 rounded border border-blue-200">
            {formatDiagramTypeLabel(diagram.type)}
          </span>
        </div>

        <div
          className="w-full overflow-x-auto rounded-lg p-3 flex justify-center items-center bg-white border border-slate-200 shadow-2xs"
          dangerouslySetInnerHTML={{ __html: sanitizedSvg }}
        />

        {diagram.caption && (
          <p className="font-hand text-sm text-[#1e3a8a]/90 mt-1.5 text-center italic px-2">
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
