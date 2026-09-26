import React, { useRef, useState, useEffect } from 'react';
import { generateHandBoxPath } from '../../utils/handwritingVariation';

interface HandwrittenBoxProps {
  seed: string | number;
  children: React.ReactNode;
  strokeColor?: string;
  strokeWidth?: number;
  backgroundColor?: string;
  className?: string;
  padding?: string;
}

export const HandwrittenBox: React.FC<HandwrittenBoxProps> = ({
  seed,
  children,
  strokeColor = '#1e3a8a',
  strokeWidth = 1.6,
  backgroundColor = '#ffffff',
  className = '',
  padding = 'p-4 sm:p-5',
}) => {
  const containerRef = useRef<HTMLDivElement>(null);
  const [dimensions, setDimensions] = useState<{ width: number; height: number }>({ width: 300, height: 100 });

  useEffect(() => {
    if (containerRef.current) {
      const observer = new ResizeObserver((entries) => {
        for (const entry of entries) {
          const { width, height } = entry.contentRect;
          if (width > 0 && height > 0) {
            setDimensions({
              width: Math.round(width),
              height: Math.round(height),
            });
          }
        }
      });
      observer.observe(containerRef.current);
      return () => observer.disconnect();
    }
  }, []);

  const path = generateHandBoxPath(dimensions.width, dimensions.height, seed);

  return (
    <div ref={containerRef} className={`relative my-4 ${className}`}>
      {/* Hand-drawn SVG border background */}
      <svg
        className="absolute inset-0 w-full h-full pointer-events-none"
        viewBox={`-4 -4 ${dimensions.width + 8} ${dimensions.height + 8}`}
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
        style={{ overflow: 'visible' }}
      >
        <rect
          x="0"
          y="0"
          width={dimensions.width}
          height={dimensions.height}
          fill={backgroundColor}
          rx="2"
        />
        <path
          d={path}
          stroke={strokeColor}
          strokeWidth={strokeWidth}
          strokeLinecap="round"
          strokeLinejoin="round"
          fill="none"
          opacity={0.9}
        />
      </svg>

      {/* Box Content */}
      <div className={`relative z-10 ${padding}`}>
        {children}
      </div>
    </div>
  );
};
