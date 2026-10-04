import express from 'express';
import cors from 'cors';
import path from 'path';
import { fileURLToPath } from 'url';
import { createServer as createViteServer } from 'vite';
import {
  analyzePrompt,
  planPages,
  generatePageContent,
  NoteDocument,
  NotePage,
  PageContent,
  PagePlan
} from './server/notesEngine';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const app = express();
const PORT = parseInt(process.env.PORT || '3000', 10);
const HOST = '0.0.0.0';

app.use(cors());
app.use(express.json({ limit: '15mb' }));

// In-memory data structures
interface StoredUser {
  id: number;
  name: string;
  email: string;
  password?: string;
  role: string;
  createdAt: string;
}

const users: StoredUser[] = [
  {
    id: 1,
    name: 'Study Scholar',
    email: 'scholar@visualnotes.edu',
    role: 'STUDENT',
    createdAt: new Date().toISOString()
  }
];

const documents = new Map<number, NoteDocument>();
let docIdCounter = 1;
let pageIdCounter = 1;

// Helper to seed sample document
async function seedInitialDoc() {
  const sampleTopic = 'Binary Search Algorithm';
  const plan = planPages(sampleTopic, 2, 'B.Tech / College', 'Handwritten', 'Intermediate');
  const pages: NotePage[] = [];

  for (let i = 1; i <= plan.totalPages; i++) {
    const content = await generatePageContent(sampleTopic, i, plan.totalPages, sampleTopic, 'Handwritten');
    pages.push({
      id: pageIdCounter++,
      pageNumber: i,
      topicTitle: content.topicTitle,
      content,
      layoutType: 'classic-handwritten',
      diagramType: content.diagram?.type || 'binary-search-array',
      createdAt: new Date().toISOString()
    });
  }

  const initialDoc: NoteDocument = {
    id: docIdCounter++,
    userId: 1,
    title: 'Binary Search Algorithm Study Notes',
    originalPrompt: 'Binary Search Algorithm in C++ with detailed step-by-step array diagram and exam tips',
    pageCount: pages.length,
    style: 'Handwritten',
    audience: 'B.Tech / College',
    difficulty: 'Undergraduate Intermediate',
    status: 'COMPLETED',
    pages,
    createdAt: new Date().toISOString()
  };

  documents.set(initialDoc.id, initialDoc);
}

seedInitialDoc().catch(err => console.error('Initial seeding error:', err));

// ==================== AUTH ROUTES ====================

app.post('/api/auth/register', (req, res) => {
  const { name, email, password } = req.body;
  if (!email || !name) {
    return res.status(400).json({ message: 'Name and email are required' });
  }

  const existing = users.find(u => u.email.toLowerCase() === email.toLowerCase());
  if (existing) {
    const token = `token_${existing.id}_${Date.now()}`;
    return res.json({
      token,
      tokenType: 'Bearer',
      user: {
        id: existing.id,
        name: existing.name,
        email: existing.email,
        role: existing.role,
        createdAt: existing.createdAt
      }
    });
  }

  const newUser: StoredUser = {
    id: users.length + 1,
    name: name.trim(),
    email: email.trim(),
    password: password || 'password123',
    role: 'STUDENT',
    createdAt: new Date().toISOString()
  };
  users.push(newUser);

  const token = `token_${newUser.id}_${Date.now()}`;
  return res.json({
    token,
    tokenType: 'Bearer',
    user: {
      id: newUser.id,
      name: newUser.name,
      email: newUser.email,
      role: newUser.role,
      createdAt: newUser.createdAt
    }
  });
});

app.post('/api/auth/login', (req, res) => {
  const { email } = req.body;
  const user = users.find(u => u.email.toLowerCase() === (email || '').toLowerCase()) || users[0];
  const token = `token_${user.id}_${Date.now()}`;

  return res.json({
    token,
    tokenType: 'Bearer',
    user: {
      id: user.id,
      name: user.name,
      email: user.email,
      role: user.role,
      createdAt: user.createdAt
    }
  });
});

app.get('/api/auth/me', (req, res) => {
  const authHeader = req.headers.authorization;
  if (!authHeader) {
    return res.json(users[0]);
  }
  return res.json(users[0]);
});

// ==================== NOTES ROUTES ====================

app.post('/api/notes/analyze', (req, res) => {
  const { prompt, preferredStyle, requestedPageCount } = req.body;
  const analysis = analyzePrompt(prompt || '', preferredStyle, requestedPageCount);
  return res.json(analysis);
});

app.post('/api/notes/plan', (req, res) => {
  const { prompt, pageCount, audience, style, difficulty } = req.body;
  const plan = planPages(prompt || '', pageCount || 2, audience, style, difficulty);
  return res.json(plan);
});

app.post('/api/notes/generate', async (req, res) => {
  try {
    const { prompt, pageCount, style, audience, difficulty, customPlan } = req.body;
    const plan: PagePlan = customPlan && customPlan.pages && customPlan.pages.length > 0
      ? customPlan
      : planPages(prompt || 'Engineering Study Notes', pageCount || 2, audience, style, difficulty);

    const pages: NotePage[] = [];

    for (let i = 0; i < plan.pages.length; i++) {
      const planItem = plan.pages[i];
      const pageContent = await generatePageContent(
        planItem.pageTitle || plan.documentTitle,
        planItem.pageNumber,
        plan.totalPages,
        prompt,
        style || plan.style || 'Handwritten'
      );

      pages.push({
        id: pageIdCounter++,
        pageNumber: planItem.pageNumber,
        topicTitle: pageContent.topicTitle,
        content: pageContent,
        layoutType: 'classic-handwritten',
        diagramType: pageContent.diagram?.type || planItem.plannedDiagramType || 'concept-map',
        createdAt: new Date().toISOString()
      });
    }

    const newDoc: NoteDocument = {
      id: docIdCounter++,
      userId: 1,
      title: plan.documentTitle || `${prompt} Notes`,
      originalPrompt: prompt,
      pageCount: pages.length,
      style: (style as any) || 'Handwritten',
      audience: audience || 'B.Tech / College',
      difficulty: difficulty || 'Intermediate',
      status: 'COMPLETED',
      pages,
      createdAt: new Date().toISOString()
    };

    documents.set(newDoc.id, newDoc);
    return res.json(newDoc);
  } catch (err: any) {
    console.error('Error generating notes:', err);
    return res.status(500).json({ message: err?.message || 'Failed to generate notes' });
  }
});

app.post('/api/notes/:id/regenerate-page', async (req, res) => {
  try {
    const docId = parseInt(req.params.id, 10);
    const { pageNumber, instruction, customModifier, style } = req.body;
    const doc = documents.get(docId);
    if (!doc) {
      return res.status(404).json({ message: 'Document not found' });
    }

    const pageIndex = doc.pages.findIndex(p => p.pageNumber === pageNumber);
    if (pageIndex === -1) {
      return res.status(404).json({ message: 'Page not found' });
    }

    const existingPage = doc.pages[pageIndex];
    const newContent = await generatePageContent(
      existingPage.topicTitle,
      pageNumber,
      doc.pages.length,
      `${doc.originalPrompt} - Instruction: ${instruction || ''} ${customModifier || ''}`,
      style || doc.style
    );

    const updatedPage: NotePage = {
      ...existingPage,
      content: newContent,
      updatedAt: new Date().toISOString()
    };

    doc.pages[pageIndex] = updatedPage;
    doc.updatedAt = new Date().toISOString();
    documents.set(docId, doc);

    return res.json(updatedPage);
  } catch (err: any) {
    console.error('Error regenerating page:', err);
    return res.status(500).json({ message: err?.message || 'Failed to regenerate page' });
  }
});

app.put('/api/notes/:id/update-page', (req, res) => {
  const docId = parseInt(req.params.id, 10);
  const { pageNumber, topicTitle, content } = req.body;
  const doc = documents.get(docId);
  if (!doc) {
    return res.status(404).json({ message: 'Document not found' });
  }

  const pageIndex = doc.pages.findIndex(p => p.pageNumber === pageNumber);
  if (pageIndex === -1) {
    return res.status(404).json({ message: 'Page not found' });
  }

  const updatedPage: NotePage = {
    ...doc.pages[pageIndex],
    topicTitle: topicTitle || doc.pages[pageIndex].topicTitle,
    content: content || doc.pages[pageIndex].content,
    updatedAt: new Date().toISOString()
  };

  doc.pages[pageIndex] = updatedPage;
  doc.updatedAt = new Date().toISOString();
  documents.set(docId, doc);

  return res.json(updatedPage);
});

app.get('/api/notes/:id', (req, res) => {
  const docId = parseInt(req.params.id, 10);
  const doc = documents.get(docId);
  if (!doc) {
    return res.status(404).json({ message: 'Document not found' });
  }
  return res.json(doc);
});

app.get('/api/notes', (req, res) => {
  const search = ((req.query.search as string) || '').toLowerCase().trim();
  const allDocs = Array.from(documents.values()).sort(
    (a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
  );

  if (!search) {
    return res.json(allDocs);
  }

  const filtered = allDocs.filter(
    d => d.title.toLowerCase().includes(search) || d.originalPrompt.toLowerCase().includes(search)
  );
  return res.json(filtered);
});

app.delete('/api/notes/:id', (req, res) => {
  const docId = parseInt(req.params.id, 10);
  if (!documents.has(docId)) {
    return res.status(404).json({ message: 'Document not found' });
  }
  documents.delete(docId);
  return res.json({ message: 'Document deleted successfully', id: docId });
});

app.patch('/api/notes/:id/rename', (req, res) => {
  const docId = parseInt(req.params.id, 10);
  const { title } = req.body;
  const doc = documents.get(docId);
  if (!doc) {
    return res.status(404).json({ message: 'Document not found' });
  }
  if (!title || !title.trim()) {
    return res.status(400).json({ message: 'Title cannot be blank' });
  }

  doc.title = title.trim();
  doc.updatedAt = new Date().toISOString();
  documents.set(docId, doc);
  return res.json(doc);
});

app.get('/api/notes/:id/download/pdf', (req, res) => {
  res.setHeader('Content-Type', 'text/plain');
  return res.send('PDF generation is processed directly in-browser using html2canvas & jsPDF.');
});

// ==================== VITE SPA INTEGRATION ====================

async function startServer() {
  if (process.env.NODE_ENV === 'production') {
    app.use(express.static(path.join(__dirname, 'dist')));
    app.get('*', (_req, res) => {
      res.sendFile(path.join(__dirname, 'dist', 'index.html'));
    });
  } else {
    const vite = await createViteServer({
      server: { middlewareMode: true },
      appType: 'spa'
    });
    app.use(vite.middlewares);
  }

  app.listen(PORT, HOST, () => {
    console.log(`AI Visual Notes server running at http://${HOST}:${PORT}`);
  });
}

startServer().catch(err => {
  console.error('Failed to start server:', err);
  process.exit(1);
});
