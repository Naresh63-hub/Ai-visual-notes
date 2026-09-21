export type NoteStyle = 'Handwritten' | 'Clean Digital' | 'Exam Notes' | 'Minimal' | 'Colorful Study Notes';

export interface User {
  id: number;
  name: string;
  email: string;
  role: string;
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  user: User;
}

export interface PromptAnalysisResponse {
  rawPrompt: string;
  mainSubject: string;
  domain?: string;
  subdomain?: string;
  requirements?: string[];
  detectedTopics: string[];
  topicCount: number;
  audience: string;
  difficulty: string;
  detailLevel: string;
  pageMode?: string;
  detectedStyle: NoteStyle;
  requiresDiagram: boolean;
  requiresAlgorithm: boolean;
  requiresExample: boolean;
  isExamOriented: boolean;
  requestedPageCount: number | null;
  suggestedPageCount: number;
  quickPageOptions: number[];
  userPromptFeedback: string;
}

export interface PagePlanItem {
  pageNumber: number;
  pageTitle: string;
  pagePartTitle?: string;
  topics: string[];
  focusArea: string;
  plannedDiagramType: string;
  estimatedDensity: string;
  keyConceptsSummary: string;
  isContinuation?: boolean;
  continuesOnNextPage?: boolean;
}

export interface PagePlan {
  documentTitle: string;
  totalPages: number;
  overallSummary: string;
  style: NoteStyle;
  audience: string;
  difficulty: string;
  detailLevel?: string;
  pageMode?: string;
  pages: PagePlanItem[];
}

export interface Section {
  heading: string;
  content?: string;
  badge?: string;
  bulletPoints?: string[];
  highlights?: string[];
}

export interface AlgorithmStep {
  stepNumber: number;
  instruction: string;
  codeSnippet?: string;
  note?: string;
}

export interface Example {
  title: string;
  scenario?: string;
  input?: string;
  stepByStep?: string[];
  outputOrResult?: string;
  takeaway?: string;
}

export interface FormulaVariable {
  symbol: string;
  meaning: string;
  unit?: string;
}

export interface Formula {
  title: string;
  expression: string;
  explanation?: string;
  variables?: FormulaVariable[];
  applicationExample?: string;
}

export interface Complexity {
  timeBest?: string;
  timeAverage?: string;
  timeWorst?: string;
  space?: string;
  timeComplexitySummary?: string;
  spaceComplexitySummary?: string;
  explanation?: string;
}

export interface KeyPoint {
  point: string;
  starred: boolean;
  category?: string;
}

export interface ExamTip {
  tip: string;
  commonMistake?: string;
  mnemonic?: string;
}

export interface DiagramData {
  type: string;
  title: string;
  caption?: string;
  rawSvg?: string;
  data?: Record<string, any>;
  labels?: string[];
  annotations?: string[];
}

export interface PageContent {
  documentTitle: string;
  pageNumber: number;
  totalPages: number;
  topicTitle: string;
  topicSubtitle?: string;
  categoryBadge?: string;
  difficultyLevel?: string;
  pagePartTitle?: string;
  
  definition?: string;
  purpose?: string;
  mainIdea?: string;
  simpleExplanation?: string;
  sections?: Section[];
  algorithm?: AlgorithmStep[];
  pseudocode?: string;
  example?: Example;
  formula?: Formula;
  complexity?: Complexity;
  diagram?: DiagramData;
  advantages?: string[];
  limitations?: string[];
  keyPoints?: KeyPoint[];
  examTips?: ExamTip[];
  quickTakeaways?: string[];
  continuesOnNextPage?: boolean;
  isContinuation?: boolean;
  layoutHint?: string;
  styleTheme?: NoteStyle;
}

export interface NotePage {
  id: number;
  pageNumber: number;
  topicTitle: string;
  content: PageContent;
  layoutType: string;
  diagramType: string;
  createdAt: string;
  updatedAt?: string;
}

export interface NoteDocument {
  id: number;
  userId?: number;
  title: string;
  originalPrompt: string;
  pageCount: number;
  style: NoteStyle;
  audience: string;
  difficulty: string;
  status: string;
  pages: NotePage[];
  createdAt: string;
  updatedAt?: string;
}
