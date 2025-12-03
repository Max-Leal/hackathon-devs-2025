/** @type {import('tailwindcss').Config} */
module.exports = {
  // Indica onde o Tailwind deve procurar por classes CSS
  content: [
    "./src/**/*.{html,ts}",
  ],
  // Ativa o modo escuro manual (adicionando a classe 'dark' no HTML/Body)
  darkMode: 'class', 
  theme: {
    extend: {
      // 🎨 Cores da Fintech (Baseadas no Logo)
      colors: {
        'fintech-primary': '#319F86',   // <--- NOVA COR DA PARTE DE CIMA
        'fintech-secondary': '#356859', // <--- NOVA COR DO FOOTER
        'dark-background': '#000000',
        'light-background': '#FFFFFF',
      },
      // 🔄 Animação Personalizada (Giro Lento)
      keyframes: {
        'spin-slow': {
          '0%, 100%': { transform: 'rotate(0deg)' },
          '100%': { transform: 'rotate(360deg)' },
        }
      },
      animation: {
        'spin-slow': 'spin-slow 3s linear infinite', // Gira em 3 segundos
      }
    },
  },
  plugins: [],
}