import { request } from './api';
import {
  NoteDocument,
  NotePage,
  PromptAnalysisResponse,
  PagePlan,
  NoteStyle,
  PageContent
} from '../types';

export interface GenerateNotesParams {
  prompt: string;
  pageCount?: number;
  style?: NoteStyle;
  audience?: string;
  difficulty?: string;
  detailLevel?: string;
  includeDiagrams?: boolean;
  customPlan?: PagePlan;
}

export const noteService = {
  async analyzePrompt(prompt: string, preferredStyle?: NoteStyle, requestedPageCount?: number): Promise<PromptAnalysisResponse> {
    return request<PromptAnalysisResponse>('/notes/analyze', {
      method: 'POST',
      body: JSON.stringify({ prompt, preferredStyle, requestedPageCount }),
    });
  },

  async planPages(prompt: string, pageCount: number, audience?: string, style?: string, difficulty?: string): Promise<PagePlan> {
    return request<PagePlan>('/notes/plan', {
      method: 'POST',
      body: JSON.stringify({ prompt, pageCount, audience, style, difficulty }),
    });
  },

  async generateNotes(params: GenerateNotesParams): Promise<NoteDocument> {
    return request<NoteDocument>('/notes/generate', {
      method: 'POST',
      body: JSON.stringify(params),
    });
  },

  async regeneratePage(documentId: number, pageNumber: number, instruction?: string, customModifier?: string, style?: NoteStyle): Promise<NotePage> {
    return request<NotePage>(`/notes/${documentId}/regenerate-page`, {
      method: 'POST',
      body: JSON.stringify({ pageNumber, instruction, customModifier, style }),
    });
  },

  async updatePageContent(documentId: number, pageNumber: number, topicTitle: string, content: PageContent): Promise<NotePage> {
    return request<NotePage>(`/notes/${documentId}/update-page`, {
      method: 'PUT',
      body: JSON.stringify({ pageNumber, topicTitle, content }),
    });
  },

  async getDocument(id: number): Promise<NoteDocument> {
    return request<NoteDocument>(`/notes/${id}`);
  },

  async getRecentDocuments(searchQuery?: string): Promise<NoteDocument[]> {
    const query = searchQuery ? `?search=${encodeURIComponent(searchQuery)}` : '';
    return request<NoteDocument[]>(`/notes${query}`);
  },

  async deleteDocument(id: number): Promise<void> {
    await request(`/notes/${id}`, { method: 'DELETE' });
  },

  async renameDocument(id: number, title: string): Promise<NoteDocument> {
    return request<NoteDocument>(`/notes/${id}/rename`, {
      method: 'PATCH',
      body: JSON.stringify({ title }),
    });
  },

  getPdfDownloadUrl(documentId: number): string {
    return `/api/notes/${documentId}/download/pdf`;
  }
};
