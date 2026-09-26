/**
 * Deterministic Natural Handwriting Variation Engine
 * 
 * Provides subtle, controlled organic variations for text lines, baseline offsets,
 * hand-drawn SVG underlines, and hand-drawn functional boxes.
 * Fully deterministic based on seed string/index.
 */

// Simple seeded PRNG (Mulberry32)
export function createPRNG(seedStr: string | number): () => number {
  let h = 0;
  const str = String(seedStr);
  for (let i = 0; i < str.length; i++) {
    h = Math.imul(31, h) + str.charCodeAt(i) | 0;
  }
  let s = h >>> 0;
  return function() {
    s |= 0;
    s = s + 0x6D2B79F5 | 0;
    let t = Math.imul(s ^ s >>> 15, 1 | s);
    t = t + Math.imul(t ^ t >>> 7, 61 | t) ^ t;
    return ((t ^ t >>> 14) >>> 0) / 4294967296;
  };
}

/**
 * Returns subtle CSS transform and spacing styles to simulate natural handwriting drift.
 */
export function getHandwritingStyle(seed: string | number, intensity: 'subtle' | 'normal' | 'heading' = 'normal'): React.CSSProperties {
  const rng = createPRNG(seed);
  
  const rotRange = intensity === 'heading' ? 0.35 : intensity === 'subtle' ? 0.15 : 0.25;
  const yRange = intensity === 'heading' ? 0.5 : intensity === 'subtle' ? 0.3 : 0.4;
  
  const rot = (rng() - 0.5) * 2 * rotRange;
  const y = (rng() - 0.5) * 2 * yRange;
  const letterSpacing = ((rng() - 0.5) * 0.015).toFixed(3) + 'em';
  const wordSpacing = (0.01 + rng() * 0.03).toFixed(3) + 'em';

  return {
    transform: `rotate(${rot.toFixed(2)}deg) translateY(${y.toFixed(1)}px)`,
    letterSpacing,
    wordSpacing,
    transformOrigin: 'left center',
    display: 'inline-block',
  };
}

/**
 * Generates an SVG path for a natural hand-drawn horizontal underline with slight curve & wobble.
 */
export function generateUnderlinePath(width: number, seed: string | number, yOffset = 4): string {
  const rng = createPRNG(seed);
  const segments = Math.max(3, Math.floor(width / 40));
  const segWidth = width / segments;
  
  let d = `M 0,${yOffset + (rng() - 0.5) * 1.5}`;
  
  for (let i = 1; i <= segments; i++) {
    const prevX = (i - 1) * segWidth;
    const curX = i * segWidth;
    const midX = prevX + segWidth * (0.45 + rng() * 0.1);
    const midY = yOffset + (rng() - 0.5) * 2.2;
    const curY = yOffset + (rng() - 0.5) * 1.8;
    
    d += ` Q ${midX.toFixed(1)},${midY.toFixed(1)} ${curX.toFixed(1)},${curY.toFixed(1)}`;
  }
  
  return d;
}

/**
 * Generates a double hand-drawn underline for main page titles.
 */
export function generateDoubleUnderlinePaths(width: number, seed: string | number): { path1: string, path2: string } {
  const path1 = generateUnderlinePath(width, `${seed}_top`, 2);
  const path2 = generateUnderlinePath(width * 0.96, `${seed}_bottom`, 6);
  return { path1, path2 };
}

/**
 * Generates a hand-drawn SVG rectangular box path with organic edge wobble and corner overshoots.
 */
export function generateHandBoxPath(
  width: number,
  height: number,
  seed: string | number,
  overshoot = 3
): string {
  const rng = createPRNG(seed);
  
  const o = overshoot;
  // Top left start with slight overshoot
  const tlX = 0;
  const tlY = 0;
  
  // Top edge: (0,0) to (width, 0)
  const tMidX = width * (0.48 + rng() * 0.04);
  const tMidY = (rng() - 0.5) * 2;
  const trX = width + (rng() * o);
  const trY = (rng() - 0.5) * 1.5;
  
  // Right edge: (width, 0) to (width, height)
  const rMidX = width + (rng() - 0.5) * 2;
  const rMidY = height * (0.48 + rng() * 0.04);
  const brX = width + (rng() - 0.5) * 1.5;
  const brY = height + (rng() * o);
  
  // Bottom edge: (width, height) to (0, height)
  const bMidX = width * (0.48 + rng() * 0.04);
  const bMidY = height + (rng() - 0.5) * 2;
  const blX = -(rng() * o);
  const blY = height + (rng() - 0.5) * 1.5;
  
  // Left edge: (0, height) to (0, 0)
  const lMidX = (rng() - 0.5) * 2;
  const lMidY = height * (0.48 + rng() * 0.04);
  const tlEndX = (rng() - 0.5) * 1.5;
  const tlEndY = -(rng() * o);

  return `M ${tlX},${tlY} ` +
         `Q ${tMidX.toFixed(1)},${tMidY.toFixed(1)} ${trX.toFixed(1)},${trY.toFixed(1)} ` +
         `M ${width},0 ` +
         `Q ${rMidX.toFixed(1)},${rMidY.toFixed(1)} ${brX.toFixed(1)},${brY.toFixed(1)} ` +
         `M ${width},${height} ` +
         `Q ${bMidX.toFixed(1)},${bMidY.toFixed(1)} ${blX.toFixed(1)},${blY.toFixed(1)} ` +
         `M 0,${height} ` +
         `Q ${lMidX.toFixed(1)},${lMidY.toFixed(1)} ${tlEndX.toFixed(1)},${tlEndY.toFixed(1)}`;
}
