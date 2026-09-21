# 📝 AI Visual Notes — Intelligent Visual Exam Notes Generator

[![Java](https://img.shields.io/badge/Java-17%2B-orange.svg?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg?logo=springboot)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.x-61DAFB.svg?logo=react)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.x-3178C6.svg?logo=typescript)](https://www.typescriptlang.org/)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind%20CSS-3.x-38B2AC.svg?logo=tailwind-css)](https://tailwindcss.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

An intelligent, full-stack visual study-notes application that transforms natural language study requests and multi-unit syllabi into **structured, handwritten-style A4 exam notes** with dynamic SVG diagrams, mathematical formulations, algorithms, and high-yield revision tips.

---

## 🌟 Core Highlights

* **🧠 Deep Domain Intelligence**: Automatically identifies subject domains (Physics, Algorithms, DBMS, Computer Networks, Biology, AI/ML, Electronics, Mathematics) and structures notes with domain-specific pedagogical components.
* **📑 Multi-Page Syllabus Decomposition**: Large topics and multi-unit engineering syllabi (e.g., *DAA Unit 1*, *Database Normalization 1NF to BCNF*) are split into clear, logical sub-topics across discrete A4 pages.
* **🎨 5 Visual Rendering Themes**: Switch instantly between *Handwritten Notebook Paper*, *Clean Digital*, *Exam Notes*, *Minimal Monochrome*, and *Colorful Study Notes*.
* **📐 Dynamic Vector SVG Diagrams**: Generates algorithmic trees, data structures, physics free-body diagrams, and biological cycles, sanitized in real-time with **DOMPurify** against XSS.
* **🖨️ High-Precision Multi-Page PDF Export**: Dual-engine export featuring client-side vector A4 capture and server-side **Apache PDFBox** rendering with Unicode and Greek symbol transliteration ($\Omega, \Theta, \Sigma, \le, \ge, \rightarrow$).
* **✏️ Interactive Customization**: Edit note sections in-place, regenerate individual pages with custom instructions, and share directly via Web Share API.
* **🔒 Enterprise-Grade Security**: 256-bit HMAC-SHA JWT authentication, IDOR-safe document ownership validation, and strict CORS lockdown.

---

## 🏛️ Architecture Overview

```
ai-visual-notes/
├── backend/                      # Spring Boot 3 Java Application
│   ├── src/main/java/com/visualnotes/
│   │   ├── ai/                   # AI Domain Strategies & Semantic Synthesis Engines
│   │   │   ├── domain/           # Prompt Understanding & Topic Decomposition
│   │   │   └── strategy/         # Domain-Specific Strategies (Physics, DBMS, Algos...)
│   │   ├── config/               # SecurityFilterChain, CORS & Exception Handlers
│   │   ├── controller/           # REST APIs (Auth, Notes, Planning, Export)
│   │   ├── diagram/              # Dynamic SVG Diagram Generator
│   │   ├── dto/                  # Strongly Typed Request/Response DTOs
│   │   ├── entity/               # JPA Entities (NoteDocument, NotePage, User)
│   │   ├── repository/           # Spring Data JPA Repositories
│   │   ├── security/             # JWT Authentication Filter & Token Utilities
│   │   └── service/              # NoteService, AuthService, ExportService
│   ├── src/main/resources/       # application.properties & database configs
│   └── pom.xml                   # Maven Dependencies & Build Configuration
│
├── frontend/                     # React 18 + Vite + Tailwind CSS Application
│   ├── src/
│   │   ├── components/
│   │   │   ├── auth/             # Login, Register & Guest Session Modals
│   │   │   ├── common/           # Navbar, Footer, Modal, Toast, Badges
│   │   │   ├── history/          # Searchable Document History Drawer
│   │   │   ├── home/             # Hero Prompt Input & Recent Cards
│   │   │   ├── planner/          # Topic Count & Page Plan Editor Modals
│   │   │   └── viewer/           # A4 VisualNotePage, DiagramRenderer & Toolbar
│   │   ├── services/             # Axios/Fetch API Clients & Note Services
│   │   ├── styles/               # Notebook Paper Grid & Handwriting CSS
│   │   └── types/                # TypeScript Interfaces & Models
│   ├── package.json              # NPM Dependencies & Scripts
│   ├── tailwind.config.js        # Typography & Color Palettes
│   ├── vercel.json               # Vercel SPA Routing Configuration
│   └── vite.config.ts            # Vite Dev Server & API Proxy
└── README.md
```

---

## 🚀 Quickstart & Local Setup

### Prerequisites
* **Java**: JDK 17 or higher
* **Node.js**: v18 or higher (with npm)
* **Maven**: 3.8+ (or use the included Maven wrapper)
* **Git**

---

### 1. Clone the Repository
```bash
git clone https://github.com/Naresh63-hub/Ai-visual-notes.git
cd Ai-visual-notes
```

---

### 2. Start the Backend (Spring Boot)
```bash
cd backend
mvn spring-boot:run
```
* The backend will start on **`http://localhost:8080`**.
* In-memory H2 Console available at **`http://localhost:8080/h2-console`** (`jdbc:h2:mem:visualnotes`).

---

### 3. Start the Frontend (React + Vite)
In a new terminal:
```bash
cd frontend
npm install
npm run dev
```
* The web app will launch at **`http://localhost:3000`** (or `http://localhost:3001`).
* API requests are proxied automatically to `http://localhost:8080`.

---

## 📡 REST API Reference

| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :---: |
| `POST` | `/api/auth/register` | Register new user account | No |
| `POST` | `/api/auth/login` | Login and receive JWT bearer token | No |
| `GET` | `/api/auth/me` | Fetch authenticated user profile | Yes |
| `POST` | `/api/notes/analyze` | Classify prompt domain & suggest page plan | No |
| `POST` | `/api/notes/plan` | Generate structured multi-page outline | No |
| `POST` | `/api/notes/generate` | Synthesize complete visual study note document | No / Guest |
| `GET` | `/api/notes` | Get recent study notes for authenticated user | Yes |
| `GET` | `/api/notes/{id}` | Retrieve document by ID (with ownership check) | Public / Owner |
| `PATCH` | `/api/notes/{id}/rename` | Rename study document title | Yes (Owner) |
| `DELETE` | `/api/notes/{id}` | Delete document | Yes (Owner) |
| `POST` | `/api/notes/{id}/regenerate-page` | Re-synthesize single page with custom prompt | Yes (Owner) |
| `PUT` | `/api/notes/{id}/update-page` | In-place update of page text/diagram content | Yes (Owner) |
| `GET` | `/api/notes/{id}/download/pdf` | Download server-generated multi-page A4 PDF | Yes (Owner) |

---

## 🌐 Production Deployment

### Option A: Frontend on Vercel + Backend on Render (Free & Fast)

1. **Deploy Backend on [Render.com](https://render.com/)**:
   * Create a **Web Service** from the GitHub repository.
   * Root Directory: `backend`
   * Build Command: `mvn clean package -DskipTests`
   * Start Command: `java -jar target/ai-visual-notes-1.0.0.jar`
   * Copy the backend URL (e.g. `https://my-notes-api.onrender.com`).

2. **Deploy Frontend on [Vercel](https://vercel.com/)**:
   * Import your GitHub repository on Vercel.
   * Root Directory: `frontend`
   * Framework Preset: `Vite`
   * Add Environment Variable: `VITE_API_URL` = `https://my-notes-api.onrender.com/api`
   * Deploy!

---

### Option B: 1-Click Deployment on Railway.app

1. Go to **[Railway.app](https://railway.app/)** and create a project from `Naresh63-hub/Ai-visual-notes`.
2. Add service with Root Directory `backend` (Java).
3. Add service with Root Directory `frontend` (Node/Vite).

---

## 🧪 Running Automated Tests

### Backend Unit & Security Tests
```bash
cd backend
mvn test
```
* **15/15 tests covering**:
  * `SecurityAccessTest`: JWT token generation, signature validation, secret strength checks.
  * `NoteServiceTest`: Ownership authorization, IDOR prevention, page count clamping.
  * `PromptUnderstandingEngineTest`: Multi-domain classification & syllabus decomposition.
  * `ExportServiceTest`: Full section PDF rendering and Unicode symbol transliteration.

### Frontend Typecheck & Build
```bash
cd frontend
npm run build
```

---

## 📄 License
This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.
