import React, { useRef, useState, useEffect } from 'react';
import { generateUnderlinePath, generateDoubleUnderlinePaths } from '../../utils/handwritingVariation';

interface HandUnderlineProps {
  seed: string | number;
  isDouble?: boolean;
  strokeColor?: string;
  strokeWidth?: number;
  className?: string;
}

export const HandUnderline: React.FC<HandUnderlineProps> = ({
  seed,
  isDouble = false,
  strokeColor = '#1e3a8a',
  strokeWidth = 1.8,
  className = '',
}) => {
  const containerRef = useRef<HTMLDivElement>(null);
  const [width, setWidth] = useState<number>(200);

  useEffect(() => {
    if (containerRef.current) {
      const observer = new ResizeObserver((entries) => {
        for (const entry of entries) {
          if (entry.contentRect.width > 0) {
            setWidth(Math.round(entry.contentRect.width));
          }
        }
      });
      observer.observe(containerRef.current);
      return () => observer.disconnect();
    }
  }, []);

  const height = isDouble ? 12 : 8;

  if (isDouble) {
    const { path1, path2 } = generateDoubleUnderlinePaths(width, seed);
    return (
      <div ref={containerRef} className={`w-full overflow-hidden ${className}`}>
        <svg
          width="100%"
          height={height}
          viewBox={`0 0 ${Math.max(width, 10)} ${height}`}
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
          className="block"
        >
          <path
            d={path1}
            stroke={strokeColor}
            strokeWidth={strokeWidth}
            strokeLinecap="round"
            strokeLinejoin="round"
            opacity={0.95}
          />
          <path
            d={path2}
            stroke={strokeColor}
            strokeWidth={strokeWidth * 0.9}
            strokeLinecap="round"
            strokeLinejoin="round"
            opacity={0.85}
          />
        </svg>
      </div>
    );
  }

  const singlePath = generateUnderlinePath(width, seed, 4);

  return (
    <div ref={containerRef} className={`w-full overflow-hidden ${className}`}>
      <svg
        width="100%"
        height={height}
        viewBox={`0 0 ${Math.max(width, 10)} ${height}`}
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
        className="block"
      >
        <path
          d={singlePath}
          stroke={strokeColor}
          strokeWidth={strokeWidth}
          strokeLinecap="round"
          strokeLinejoin="round"
          opacity={0.9}
        />
      </svg>
    </div>
  );
};
