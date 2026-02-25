/**
 * Main.js - Lógica principal de la aplicación
 */

const App = {
    /**
     * Inicializa la aplicación
     */
    init() {
        this.initCopyButtons();
        this.initPrintButton();
        this.initSmoothScrollLinks();
    },

    /**
     * Inicializa los botones de copiar comandos
     */
    initCopyButtons() {
        const copyButtons = document.querySelectorAll('.command__copy');
        
        copyButtons.forEach(button => {
            button.addEventListener('click', async () => {
                const textToCopy = button.dataset.copy;
                
                if (!textToCopy) return;
                
                const success = await Utils.copyToClipboard(textToCopy);
                
                if (success) {
                    Utils.showToast('¡Comando copiado!');
                    
                    // Feedback visual
                    button.classList.add('copied');
                    setTimeout(() => button.classList.remove('copied'), 1000);
                } else {
                    Utils.showToast('Error al copiar');
                }
            });
        });
    },

    /**
     * Inicializa el botón de imprimir/PDF
     */
    initPrintButton() {
        const printBtn = document.getElementById('btn-print');
        
        if (printBtn) {
            printBtn.addEventListener('click', () => {
                window.print();
            });
        }
    },

    /**
     * Inicializa enlaces con scroll suave
     */
    initSmoothScrollLinks() {
        document.querySelectorAll('a[href^="#"]').forEach(anchor => {
            anchor.addEventListener('click', function(e) {
                const href = this.getAttribute('href');
                
                // Ignorar enlaces vacíos o solo #
                if (!href || href === '#') return;
                
                e.preventDefault();
                Utils.scrollTo(href);
            });
        });
    }
};

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', () => App.init());

// Log de bienvenida para desarrolladores
console.log(
    '%c🌿 GitFlow HU Guide',
    'color: #6366f1; font-size: 20px; font-weight: bold;'
);
console.log(
    '%cFlujo de trabajo profesional con Git',
    'color: #6b7280; font-size: 12px;'
);
