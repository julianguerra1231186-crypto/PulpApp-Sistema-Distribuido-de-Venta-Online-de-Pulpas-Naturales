/**
 * Utils.js - Funciones utilitarias
 */

const Utils = {
    /**
     * Debounce - Limita la frecuencia de ejecución de una función
     * @param {Function} func - Función a ejecutar
     * @param {number} wait - Tiempo de espera en ms
     * @returns {Function}
     */
    debounce(func, wait = 100) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    },

    /**
     * Throttle - Limita la ejecución a una vez por intervalo
     * @param {Function} func - Función a ejecutar
     * @param {number} limit - Intervalo en ms
     * @returns {Function}
     */
    throttle(func, limit = 100) {
        let inThrottle;
        return function(...args) {
            if (!inThrottle) {
                func.apply(this, args);
                inThrottle = true;
                setTimeout(() => inThrottle = false, limit);
            }
        };
    },

    /**
     * Copia texto al portapapeles
     * @param {string} text - Texto a copiar
     * @returns {Promise<boolean>}
     */
    async copyToClipboard(text) {
        try {
            await navigator.clipboard.writeText(text);
            return true;
        } catch (err) {
            // Fallback para navegadores antiguos
            const textArea = document.createElement('textarea');
            textArea.value = text;
            textArea.style.position = 'fixed';
            textArea.style.left = '-9999px';
            document.body.appendChild(textArea);
            textArea.select();
            try {
                document.execCommand('copy');
                return true;
            } catch (e) {
                console.error('Error al copiar:', e);
                return false;
            } finally {
                document.body.removeChild(textArea);
            }
        }
    },

    /**
     * Muestra una notificación toast
     * @param {string} message - Mensaje a mostrar
     * @param {number} duration - Duración en ms
     */
    showToast(message, duration = 2000) {
        const toast = document.getElementById('toast');
        if (!toast) return;

        const span = toast.querySelector('span');
        if (span) span.textContent = message;

        toast.classList.add('show');
        
        setTimeout(() => {
            toast.classList.remove('show');
        }, duration);
    },

    /**
     * Obtiene la posición de scroll
     * @returns {number}
     */
    getScrollPosition() {
        return window.pageYOffset || document.documentElement.scrollTop;
    },

    /**
     * Scroll suave a un elemento
     * @param {string} selector - Selector del elemento
     * @param {number} offset - Offset adicional
     */
    scrollTo(selector, offset = 0) {
        const element = document.querySelector(selector);
        if (!element) return;

        const headerHeight = document.querySelector('.header')?.offsetHeight || 0;
        const elementPosition = element.getBoundingClientRect().top;
        const offsetPosition = elementPosition + window.pageYOffset - headerHeight - offset;

        window.scrollTo({
            top: offsetPosition,
            behavior: 'smooth'
        });
    },

    /**
     * Verifica si un elemento está en el viewport
     * @param {HTMLElement} element - Elemento a verificar
     * @param {number} threshold - Umbral de visibilidad (0-1)
     * @returns {boolean}
     */
    isInViewport(element, threshold = 0) {
        const rect = element.getBoundingClientRect();
        const windowHeight = window.innerHeight || document.documentElement.clientHeight;
        
        const visibleHeight = Math.min(rect.bottom, windowHeight) - Math.max(rect.top, 0);
        const elementHeight = rect.height;
        
        return visibleHeight / elementHeight > threshold;
    },

    /**
     * Genera un ID único
     * @returns {string}
     */
    generateId() {
        return '_' + Math.random().toString(36).substr(2, 9);
    },

    /**
     * Parsea query params de la URL
     * @returns {Object}
     */
    getQueryParams() {
        const params = {};
        const searchParams = new URLSearchParams(window.location.search);
        for (const [key, value] of searchParams) {
            params[key] = value;
        }
        return params;
    }
};

// Exponer globalmente
window.Utils = Utils;
