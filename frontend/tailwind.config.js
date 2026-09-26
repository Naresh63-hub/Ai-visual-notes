/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      fontFamily: {
        hand: ['Kalam', 'Patrick Hand', 'Caveat', 'cursive'],
        sans: ['Plus Jakarta Sans', 'Inter', 'system-ui', 'sans-serif'],
        mono: ['Fira Code', 'monospace']
      },
      colors: {
        note: {
          bg: '#fbf9f4',
          lines: '#e2e8f0',
          margin: '#f87171',
          ink: '#1e293b',
          pencil: '#475569',
          highlighter: '#fef08a',
          blueink: '#1d4ed8'
        }
      },
      boxShadow: {
        'page': '0 10px 25px -5px rgba(0, 0, 0, 0.08), 0 8px 10px -6px rgba(0, 0, 0, 0.04)',
        'page-hover': '0 20px 30px -10px rgba(0, 0, 0, 0.12), 0 10px 15px -8px rgba(0, 0, 0, 0.06)'
      }
    },
  },
  plugins: [],
}
