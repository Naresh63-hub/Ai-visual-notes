import {
  NoteDocument,
  NotePage,
  NoteStyle,
  PageContent,
  PagePlan,
  PromptAnalysisResponse,
} from '../types';
import { authService } from './authService';

function getAuthHeaders(): HeadersInit {
  const token = authService.getToken();
  return {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };
}

export const noteService = {
  async getRecentDocuments(search?: string): Promise<NoteDocument[]> {
    const url = search ? `/api/notes?search=${encodeURIComponent(search)}` : '/api/notes';
    const res = await fetch(url, {
      headers: getAuthHeaders(),
    });
    if (!res.ok) {
      throw new Error('Failed to fetch documents');
    }
    return res.json();
  },

  async getDocument(id: number): Promise<NoteDocument> {
    const res = await fetch(`/api/notes/${id}`, {
      headers: getAuthHeaders(),
    });
    if (!res.ok) {
      throw new Error('Document not found');
    }
    return res.json();
  },

  async analyzePrompt(
    prompt: string,
    preferredStyle: NoteStyle = 'Handwritten',
    requestedPageCount?: number
  ): Promise<PromptAnalysisResponse> {
    const res = await fetch('/api/notes/analyze', {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify({ prompt, preferredStyle, requestedPageCount }),
    });
    if (!res.ok) {
      throw new Error('Failed to analyze prompt');
    }
    return res.json();
  },

  async planPages(
    prompt: string,
    pageCount: number,
    audience: string = 'B.Tech / College',
    style: NoteStyle = 'Handwritten',
    difficulty: string = 'Intermediate'
  ): Promise<PagePlan> {
    const res = await fetch('/api/notes/plan', {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify({ prompt, pageCount, audience, style, difficulty }),
    });
    if (!res.ok) {
      throw new Error('Failed to plan pages');
    }
    return res.json();
  },

  async generateNotes(payload: {
    prompt: string;
    pageCount: number;
    style: NoteStyle;
    audience: string;
    detailLevel: string;
    customPlan?: PagePlan;
  }): Promise<NoteDocument> {
    const res = await fetch('/api/notes/generate', {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(payload),
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({ message: 'Generation failed' }));
      throw new Error(err.message || 'Generation failed');
    }
    return res.json();
  },

  async regeneratePage(
    docId: number,
    pageNumber: number,
    instruction: string,
    customModifier: string,
    style: NoteStyle
  ): Promise<NotePage> {
    const res = await fetch(`/api/notes/${docId}/regenerate-page`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify({ pageNumber, instruction, customModifier, style }),
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({ message: 'Failed to regenerate page' }));
      throw new Error(err.message || 'Failed to regenerate page');
    }
    return res.json();
  },

  async updatePageContent(
    docId: number,
    pageNumber: number,
    topicTitle: string,
    content: PageContent
  ): Promise<NotePage> {
    const res = await fetch(`/api/notes/${docId}/update-page`, {
      method: 'PUT',
      headers: getAuthHeaders(),
      body: JSON.stringify({ pageNumber, topicTitle, content }),
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({ message: 'Failed to update page' }));
      throw new Error(err.message || 'Failed to update page');
    }
    return res.json();
  },

  async deleteDocument(id: number): Promise<void> {
    const res = await fetch(`/api/notes/${id}`, {
      method: 'DELETE',
      headers: getAuthHeaders(),
    });
    if (!res.ok) {
      throw new Error('Failed to delete document');
    }
  },

  async renameDocument(id: number, title: string): Promise<NoteDocument> {
    const res = await fetch(`/api/notes/${id}/rename`, {
      method: 'PATCH',
      headers: getAuthHeaders(),
      body: JSON.stringify({ title }),
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({ message: 'Failed to rename' }));
      throw new Error(err.message || 'Failed to rename');
    }
    return res.json();
  },
};
