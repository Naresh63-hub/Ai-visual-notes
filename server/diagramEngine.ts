export interface DiagramResult {
  type: string;
  title: string;
  caption: string;
  rawSvg: string;
  data?: Record<string, any>;
  labels?: string[];
  annotations?: string[];
}

export function generateDiagram(topic: string, specificType?: string, context?: string): DiagramResult {
  const combined = `${topic} ${specificType || ''} ${context || ''}`.toLowerCase();

  let type = specificType || 'concept-map';
  let title = `${topic} - Core Mechanism & Flow`;
  let caption = `Visual representation illustrating the functional workflow and key states of ${topic}.`;
  let rawSvg = '';

  if (combined.includes('binary search') || combined.includes('binary-search')) {
    type = 'binary-search-array';
    title = 'Binary Search Step-by-Step Array Trace (Target = 15)';
    caption = 'Detailed trace over Array [2, 5, 7, 11, 15, 18, 21]. Pointers (Low, Mid, High) narrow search in 3 iterations.';
    rawSvg = `<svg viewBox="0 0 620 180" xmlns="http://www.w3.org/2000/svg" class="w-full h-auto">
      <rect width="620" height="180" rx="10" fill="#fefce8" stroke="#fef08a" stroke-width="2"/>
      <g transform="translate(20, 12)">
        <rect x="0" y="0" width="580" height="24" rx="4" fill="#fef08a" stroke="#ca8a04"/>
        <text x="290" y="16" font-family="'Patrick Hand', 'Caveat', cursive, sans-serif" font-size="14" font-weight="bold" fill="#854d0e" text-anchor="middle">Goal: Search for Key = 15 in Sorted Array A[0...6]</text>
      </g>
      <g transform="translate(30, 48)">
        <text x="-10" y="18" font-family="sans-serif" font-size="11" font-weight="bold" fill="#b91c1c">Step 1:</text>
        <rect x="45" y="0" width="40" height="28" fill="#fee2e2" stroke="#ef4444" stroke-width="1.5" rx="3"/><text x="65" y="18" font-size="13" font-weight="bold" fill="#991b1b" text-anchor="middle">2</text>
        <rect x="90" y="0" width="40" height="28" fill="#fee2e2" stroke="#ef4444" stroke-width="1.5" rx="3"/><text x="110" y="18" font-size="13" font-weight="bold" fill="#991b1b" text-anchor="middle">5</text>
        <rect x="135" y="0" width="40" height="28" fill="#fee2e2" stroke="#ef4444" stroke-width="1.5" rx="3"/><text x="155" y="18" font-size="13" font-weight="bold" fill="#991b1b" text-anchor="middle">7</text>
        <rect x="180" y="0" width="40" height="28" fill="#fee2e2" stroke="#b91c1c" stroke-width="2.5" rx="3"/><text x="200" y="18" font-size="13" font-weight="bold" fill="#b91c1c" text-anchor="middle">11</text>
        <rect x="225" y="0" width="40" height="28" fill="#eff6ff" stroke="#3b82f6" stroke-width="1.5" rx="3"/><text x="245" y="18" font-size="13" font-weight="bold" fill="#1e40af" text-anchor="middle">15</text>
        <rect x="270" y="0" width="40" height="28" fill="#eff6ff" stroke="#3b82f6" stroke-width="1.5" rx="3"/><text x="290" y="18" font-size="13" font-weight="bold" fill="#1e40af" text-anchor="middle">18</text>
        <rect x="315" y="0" width="40" height="28" fill="#eff6ff" stroke="#3b82f6" stroke-width="1.5" rx="3"/><text x="335" y="18" font-size="13" font-weight="bold" fill="#1e40af" text-anchor="middle">21</text>
        <text x="65" y="-4" font-size="10" font-family="monospace" font-weight="bold" fill="#047857" text-anchor="middle">L=0</text>
        <text x="200" y="-4" font-size="10" font-family="monospace" font-weight="bold" fill="#b91c1c" text-anchor="middle">M=3</text>
        <text x="335" y="-4" font-size="10" font-family="monospace" font-weight="bold" fill="#047857" text-anchor="middle">H=6</text>
        <text x="375" y="18" font-family="'Patrick Hand', cursive, sans-serif" font-size="13" fill="#1e293b">A[3]=11 &lt; 15 ⟹ Search Right (Low = 4)</text>
      </g>
      <g transform="translate(30, 95)">
        <text x="-10" y="18" font-family="sans-serif" font-size="11" font-weight="bold" fill="#b91c1c">Step 2:</text>
        <rect x="45" y="0" width="175" height="28" fill="#f1f5f9" stroke="#cbd5e1" stroke-dasharray="3 2" rx="3"/><text x="132" y="18" font-size="11" fill="#94a3b8" text-anchor="middle">[Discarded Left Half]</text>
        <rect x="225" y="0" width="40" height="28" fill="#eff6ff" stroke="#3b82f6" stroke-width="1.5" rx="3"/><text x="245" y="18" font-size="13" font-weight="bold" fill="#1e40af" text-anchor="middle">15</text>
        <rect x="270" y="0" width="40" height="28" fill="#fee2e2" stroke="#b91c1c" stroke-width="2.5" rx="3"/><text x="290" y="18" font-size="13" font-weight="bold" fill="#b91c1c" text-anchor="middle">18</text>
        <rect x="315" y="0" width="40" height="28" fill="#eff6ff" stroke="#3b82f6" stroke-width="1.5" rx="3"/><text x="335" y="18" font-size="13" font-weight="bold" fill="#1e40af" text-anchor="middle">21</text>
        <text x="245" y="-4" font-size="10" font-family="monospace" font-weight="bold" fill="#047857" text-anchor="middle">L=4</text>
        <text x="290" y="-4" font-size="10" font-family="monospace" font-weight="bold" fill="#b91c1c" text-anchor="middle">M=5</text>
        <text x="335" y="-4" font-size="10" font-family="monospace" font-weight="bold" fill="#047857" text-anchor="middle">H=6</text>
        <text x="375" y="18" font-family="'Patrick Hand', cursive, sans-serif" font-size="13" fill="#1e293b">A[5]=18 &gt; 15 ⟹ Search Left (High = 4)</text>
      </g>
      <g transform="translate(30, 142)">
        <text x="-10" y="18" font-family="sans-serif" font-size="11" font-weight="bold" fill="#15803d">Step 3:</text>
        <rect x="45" y="0" width="175" height="28" fill="#f1f5f9" stroke="#cbd5e1" stroke-dasharray="3 2" rx="3"/>
        <rect x="225" y="0" width="40" height="28" fill="#dcfce7" stroke="#16a34a" stroke-width="3" rx="3"/><text x="245" y="19" font-size="14" font-weight="bold" fill="#15803d" text-anchor="middle">15</text>
        <rect x="270" y="0" width="85" height="28" fill="#f1f5f9" stroke="#cbd5e1" stroke-dasharray="3 2" rx="3"/>
        <text x="245" y="-4" font-size="10" font-family="monospace" font-weight="bold" fill="#15803d" text-anchor="middle">L=M=H=4</text>
        <text x="375" y="18" font-family="'Patrick Hand', cursive, sans-serif" font-size="14" font-weight="bold" fill="#15803d">★ MATCH FOUND at Index 4 (Found in 3 steps!)</text>
      </g>
    </svg>`;
  } else if (combined.includes('quick sort') || combined.includes('quick-sort')) {
    type = 'sorting-partition';
    title = 'Quick Sort Pivot Partitioning & Pointer Movement';
    caption = 'Partitioning array [40, 20, 60, 10, 30] with Pivot = 30. Left subarray < 30, Right subarray > 30.';
    rawSvg = `<svg viewBox="0 0 600 160" xmlns="http://www.w3.org/2000/svg" class="w-full h-auto">
      <rect width="600" height="160" rx="8" fill="#f8fafc" stroke="#e2e8f0" stroke-width="2"/>
      <g transform="translate(40, 25)">
        <text x="0" y="24" font-family="monospace" font-size="12" font-weight="bold" fill="#334155">Initial:</text>
        <rect x="70" y="0" width="45" height="35" rx="4" fill="#e0e7ff" stroke="#6366f1" stroke-width="2"/><text x="92" y="22" font-weight="bold" fill="#312e81" text-anchor="middle">40</text>
        <rect x="125" y="0" width="45" height="35" rx="4" fill="#e0e7ff" stroke="#6366f1" stroke-width="2"/><text x="147" y="22" font-weight="bold" fill="#312e81" text-anchor="middle">20</text>
        <rect x="180" y="0" width="45" height="35" rx="4" fill="#e0e7ff" stroke="#6366f1" stroke-width="2"/><text x="202" y="22" font-weight="bold" fill="#312e81" text-anchor="middle">60</text>
        <rect x="235" y="0" width="45" height="35" rx="4" fill="#e0e7ff" stroke="#6366f1" stroke-width="2"/><text x="257" y="22" font-weight="bold" fill="#312e81" text-anchor="middle">10</text>
        <rect x="290" y="0" width="45" height="35" rx="4" fill="#fef08a" stroke="#ca8a04" stroke-width="2.5"/><text x="312" y="22" font-weight="bold" fill="#854d0e" text-anchor="middle">30</text>
        <text x="312" y="48" font-size="10" font-weight="bold" fill="#ca8a04" text-anchor="middle">PIVOT</text>
      </g>
      <g transform="translate(40, 85)">
        <text x="0" y="24" font-family="monospace" font-size="12" font-weight="bold" fill="#047857">Partitioned:</text>
        <rect x="70" y="0" width="45" height="35" rx="4" fill="#dcfce7" stroke="#16a34a" stroke-width="2"/><text x="92" y="22" font-weight="bold" fill="#15803d" text-anchor="middle">20</text>
        <rect x="125" y="0" width="45" height="35" rx="4" fill="#dcfce7" stroke="#16a34a" stroke-width="2"/><text x="147" y="22" font-weight="bold" fill="#15803d" text-anchor="middle">10</text>
        <rect x="180" y="0" width="45" height="35" rx="4" fill="#fef08a" stroke="#ca8a04" stroke-width="3"/><text x="202" y="22" font-weight="bold" fill="#854d0e" text-anchor="middle">30</text>
        <rect x="235" y="0" width="45" height="35" rx="4" fill="#fee2e2" stroke="#dc2626" stroke-width="2"/><text x="257" y="22" font-weight="bold" fill="#991b1b" text-anchor="middle">60</text>
        <rect x="290" y="0" width="45" height="35" rx="4" fill="#fee2e2" stroke="#dc2626" stroke-width="2"/><text x="312" y="22" font-weight="bold" fill="#991b1b" text-anchor="middle">40</text>
        <text x="120" y="48" font-size="10" font-weight="bold" fill="#16a34a" text-anchor="middle">&lt; Pivot (Left)</text>
        <text x="202" y="48" font-size="10" font-weight="bold" fill="#ca8a04" text-anchor="middle">Placed Pivot</text>
        <text x="285" y="48" font-size="10" font-weight="bold" fill="#dc2626" text-anchor="middle">&gt; Pivot (Right)</text>
      </g>
    </svg>`;
  } else if (combined.includes('osi') || combined.includes('network') || combined.includes('tcp/ip')) {
    type = 'layer-stack';
    title = 'OSI 7-Layer Model: Encapsulation & Protocol Data Units';
    caption = 'Data flows down from Layer 7 (Application) to Layer 1 (Physical) with protocol header encapsulation.';
    rawSvg = `<svg viewBox="0 0 600 200" xmlns="http://www.w3.org/2000/svg" class="w-full h-auto">
      <rect width="600" height="200" rx="8" fill="#f8fafc" stroke="#e2e8f0" stroke-width="2"/>
      <g transform="translate(30, 15)">
        <rect x="0" y="0" width="540" height="22" rx="4" fill="#dbeafe" stroke="#3b82f6"/><text x="270" y="15" font-size="11" font-weight="bold" fill="#1e40af" text-anchor="middle">L7: Application (HTTP, DNS, SSH, SMTP) — User Data</text>
        <rect x="0" y="25" width="540" height="22" rx="4" fill="#e0e7ff" stroke="#6366f1"/><text x="270" y="40" font-size="11" font-weight="bold" fill="#3730a3" text-anchor="middle">L6: Presentation (SSL/TLS, JPEG, ASCII) — Encryption & Format</text>
        <rect x="0" y="50" width="540" height="22" rx="4" fill="#ede9fe" stroke="#8b5cf6"/><text x="270" y="65" font-size="11" font-weight="bold" fill="#5b21b6" text-anchor="middle">L5: Session (RPC, NetBIOS, Sockets) — Session Management</text>
        <rect x="0" y="75" width="540" height="22" rx="4" fill="#fae8ff" stroke="#d946ef"/><text x="270" y="90" font-size="11" font-weight="bold" fill="#86198f" text-anchor="middle">L4: Transport (TCP, UDP, Ports) — Segments</text>
        <rect x="0" y="100" width="540" height="22" rx="4" fill="#fef3c7" stroke="#f59e0b"/><text x="270" y="115" font-size="11" font-weight="bold" fill="#92400e" text-anchor="middle">L3: Network (IP, ICMP, Routers) — Packets</text>
        <rect x="0" y="125" width="540" height="22" rx="4" fill="#dcfce7" stroke="#22c55e"/><text x="270" y="140" font-size="11" font-weight="bold" fill="#166534" text-anchor="middle">L2: Data Link (Ethernet, MAC, Switch) — Frames</text>
        <rect x="0" y="150" width="540" height="22" rx="4" fill="#ffedd5" stroke="#f97316"/><text x="270" y="165" font-size="11" font-weight="bold" fill="#9a3412" text-anchor="middle">L1: Physical (Cables, Fiber, Radio Bits) — Raw Bits</text>
      </g>
    </svg>`;
  } else if (combined.includes('dijkstra') || combined.includes('graph') || combined.includes('tree') || combined.includes('bfs') || combined.includes('dfs')) {
    type = 'graph-network';
    title = `${topic} - Graph Traversal & Topology`;
    caption = `Node states, edge weights, and path relaxation traversal pipeline.`;
    rawSvg = `<svg viewBox="0 0 600 180" xmlns="http://www.w3.org/2000/svg" class="w-full h-auto">
      <rect width="600" height="180" rx="8" fill="#f8fafc" stroke="#e2e8f0" stroke-width="2"/>
      <!-- Edges -->
      <line x1="100" y1="90" x2="220" y2="40" stroke="#94a3b8" stroke-width="2.5"/>
      <line x1="100" y1="90" x2="220" y2="140" stroke="#94a3b8" stroke-width="2.5"/>
      <line x1="220" y1="40" x2="360" y2="40" stroke="#22c55e" stroke-width="3"/>
      <line x1="220" y1="40" x2="220" y2="140" stroke="#94a3b8" stroke-width="2"/>
      <line x1="220" y1="140" x2="360" y2="140" stroke="#94a3b8" stroke-width="2"/>
      <line x1="360" y1="40" x2="480" y2="90" stroke="#22c55e" stroke-width="3"/>
      <line x1="360" y1="140" x2="480" y2="90" stroke="#94a3b8" stroke-width="2"/>
      <!-- Nodes -->
      <circle cx="100" cy="90" r="22" fill="#dbeafe" stroke="#2563eb" stroke-width="3"/>
      <text x="100" y="95" font-weight="bold" fill="#1e40af" text-anchor="middle">S</text>
      <circle cx="220" cy="40" r="22" fill="#dcfce7" stroke="#16a34a" stroke-width="3"/>
      <text x="220" y="45" font-weight="bold" fill="#166534" text-anchor="middle">A</text>
      <circle cx="220" cy="140" r="22" fill="#f1f5f9" stroke="#64748b" stroke-width="2.5"/>
      <text x="220" y="145" font-weight="bold" fill="#334155" text-anchor="middle">B</text>
      <circle cx="360" cy="40" r="22" fill="#dcfce7" stroke="#16a34a" stroke-width="3"/>
      <text x="360" y="45" font-weight="bold" fill="#166534" text-anchor="middle">C</text>
      <circle cx="360" cy="140" r="22" fill="#f1f5f9" stroke="#64748b" stroke-width="2.5"/>
      <text x="360" y="145" font-weight="bold" fill="#334155" text-anchor="middle">D</text>
      <circle cx="480" cy="90" r="22" fill="#fef08a" stroke="#ca8a04" stroke-width="3"/>
      <text x="480" y="95" font-weight="bold" fill="#854d0e" text-anchor="middle">E</text>
    </svg>`;
  } else {
    // High-quality Generic Concept Map & Architecture
    type = 'concept-map';
    title = `${topic} - Concept Architecture & Core Mechanics`;
    caption = `Structural breakdown, input/output flow, and primary components of ${topic}.`;
    rawSvg = `<svg viewBox="0 0 600 170" xmlns="http://www.w3.org/2000/svg" class="w-full h-auto">
      <rect width="600" height="170" rx="8" fill="#f8fafc" stroke="#e2e8f0" stroke-width="2"/>
      <!-- Input Box -->
      <g transform="translate(30, 50)">
        <rect x="0" y="0" width="130" height="60" rx="6" fill="#eff6ff" stroke="#3b82f6" stroke-width="2"/>
        <text x="65" y="26" font-family="'Patrick Hand', cursive, sans-serif" font-size="14" font-weight="bold" fill="#1e40af" text-anchor="middle">Input / Pre-state</text>
        <text x="65" y="44" font-size="11" fill="#60a5fa" text-anchor="middle">Raw Data / Conditions</text>
      </g>
      <!-- Arrow 1 -->
      <line x1="165" y1="80" x2="215" y2="80" stroke="#3b82f6" stroke-width="2.5" marker-end="url(#arrow)"/>
      <polygon points="215,75 225,80 215,85" fill="#3b82f6"/>
      <!-- Core Mechanism -->
      <g transform="translate(230, 35)">
        <rect x="0" y="0" width="145" height="90" rx="8" fill="#fefce8" stroke="#ca8a04" stroke-width="2.5"/>
        <text x="72" y="28" font-family="'Patrick Hand', cursive, sans-serif" font-size="15" font-weight="bold" fill="#854d0e" text-anchor="middle">Core Process</text>
        <text x="72" y="50" font-size="11" font-weight="bold" fill="#a16207" text-anchor="middle">${topic.slice(0, 18)}</text>
        <text x="72" y="70" font-size="10" fill="#713f12" text-anchor="middle">Logic & Transformation</text>
      </g>
      <!-- Arrow 2 -->
      <line x1="380" y1="80" x2="430" y2="80" stroke="#16a34a" stroke-width="2.5"/>
      <polygon points="430,75 440,80 430,85" fill="#16a34a"/>
      <!-- Output Box -->
      <g transform="translate(445, 50)">
        <rect x="0" y="0" width="125" height="60" rx="6" fill="#dcfce7" stroke="#16a34a" stroke-width="2"/>
        <text x="62" y="26" font-family="'Patrick Hand', cursive, sans-serif" font-size="14" font-weight="bold" fill="#15803d" text-anchor="middle">Output / Result</text>
        <text x="62" y="44" font-size="11" fill="#16a34a" text-anchor="middle">Guaranteed Invariant</text>
      </g>
    </svg>`;
  }

  return { type, title, caption, rawSvg };
}
