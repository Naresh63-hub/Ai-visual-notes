import React from 'react';
import { ComparisonTable } from '../../types';
import { HandUnderline } from './HandUnderline';
import { getHandwritingStyle } from '../../utils/handwritingVariation';

interface HandwrittenTableProps {
  table: ComparisonTable;
  seed: string | number;
}

export const HandwrittenTable: React.FC<HandwrittenTableProps> = ({ table, seed }) => {
  return (
    <div className="my-5 w-full">
      {/* Table Title with Hand Underline */}
      <div className="mb-2">
        <div className="inline-block">
          <h4
            className="text-lg sm:text-xl font-bold text-[#172554] tracking-wide"
            style={getHandwritingStyle(`${seed}_tbl_title`, 'heading')}
          >
            {table.title || 'Comparison / Analysis Table'}
          </h4>
          <HandUnderline seed={`${seed}_tbl_u`} strokeWidth={1.4} strokeColor="#1e3a8a" />
        </div>
      </div>

      {/* Hand-Ruled Table Structure */}
      <div className="overflow-x-auto my-2">
        <table className="w-full border-collapse font-hand text-left">
          <thead>
            <tr className="border-b-2 border-[#1e3a8a]">
              {table.headers.map((header, hidx) => (
                <th
                  key={hidx}
                  className="py-2.5 px-3.5 text-base sm:text-lg font-bold text-[#172554] bg-[#f8fafc]/50"
                  style={getHandwritingStyle(`${seed}_th_${hidx}`, 'heading')}
                >
                  {header}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {table.rows.map((row, ridx) => (
              <tr
                key={ridx}
                className="border-b border-dashed border-[#1e3a8a]/40 hover:bg-slate-50/50 transition-colors"
              >
                {row.map((cell, cidx) => (
                  <td
                    key={cidx}
                    className={`py-2 px-3.5 text-base sm:text-lg ${
                      cidx === 0
                        ? 'font-bold text-[#172554] border-r border-[#1e3a8a]/30'
                        : 'text-[#1e3a8a]'
                    }`}
                    style={getHandwritingStyle(`${seed}_td_${ridx}_${cidx}`, 'subtle')}
                  >
                    {cell}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Table Conclusion / Note */}
      {table.conclusion && (
        <div
          className="mt-2 text-sm sm:text-base text-[#1e3a8a] italic"
          style={getHandwritingStyle(`${seed}_tbl_conc`, 'subtle')}
        >
          <span className="font-bold text-[#172554] not-italic">Note: </span>
          {table.conclusion}
        </div>
      )}
    </div>
  );
};
