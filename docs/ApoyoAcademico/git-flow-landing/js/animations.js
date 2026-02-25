/**
 * Animations.js - Animaciones y efectos visuales
 */

const Animations = {
    /**
     * Configuración del Intersection Observer
     */
    observerOptions: {
        root: null,
        rootMargin: '0px 0px -10% 0px',
        threshold: 0.1
    },

    /**
     * Inicializa las animaciones
     */
    init() {
        this.initScrollAnimations();
        this.initParticles();
        this.initCounters();
    },

    /**
     * Inicializa las animaciones de scroll
     */
    initScrollAnimations() {
        // Elementos a animar
        const animatedElements = document.querySelectorAll(`
            .branch-card,
            .step,
            .env-card,
            .rule,
            .command-group
        `);

        if (!animatedElements.length) return;

        // Agregar clase inicial
        animatedElements.forEach(el => {
            el.style.opacity = '0';
            el.style.transform = 'translateY(30px)';
            el.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
        });

        // Observer
        const observer = new IntersectionObserver((entries) => {
            entries.forEach((entry, index) => {
                if (entry.isIntersecting) {
                    // Delay escalonado
                    setTimeout(() => {
                        entry.target.style.opacity = '1';
                        entry.target.style.transform = 'translateY(0)';
                    }, index * 100);
                    
                    observer.unobserve(entry.target);
                }
            });
        }, this.observerOptions);

        animatedElements.forEach(el => observer.observe(el));
    },

    /**
     * Inicializa el efecto de partículas en el hero
     */
    initParticles() {
        const container = document.getElementById('particles');
        if (!container) return;

        const particleCount = 50;
        
        for (let i = 0; i < particleCount; i++) {
            const particle = document.createElement('div');
            particle.className = 'particle';
            
            // Estilos aleatorios
            const size = Math.random() * 4 + 2;
            const posX = Math.random() * 100;
            const posY = Math.random() * 100;
            const duration = Math.random() * 20 + 10;
            const delay = Math.random() * 5;
            
            particle.style.cssText = `
                position: absolute;
                width: ${size}px;
                height: ${size}px;
                background: linear-gradient(135deg, var(--color-primary-light), var(--color-primary));
                border-radius: 50%;
                left: ${posX}%;
                top: ${posY}%;
                opacity: ${Math.random() * 0.5 + 0.1};
                animation: float ${duration}s ease-in-out ${delay}s infinite;
            `;
            
            container.appendChild(particle);
        }

        // Agregar keyframes si no existen
        if (!document.getElementById('particle-styles')) {
            const style = document.createElement('style');
            style.id = 'particle-styles';
            style.textContent = `
                @keyframes float {
                    0%, 100% {
                        transform: translate(0, 0) rotate(0deg);
                    }
                    25% {
                        transform: translate(10px, -15px) rotate(90deg);
                    }
                    50% {
                        transform: translate(-5px, -25px) rotate(180deg);
                    }
                    75% {
                        transform: translate(-15px, -10px) rotate(270deg);
                    }
                }
            `;
            document.head.appendChild(style);
        }
    },

    /**
     * Inicializa contadores animados
     */
    initCounters() {
        const counters = document.querySelectorAll('[data-counter]');
        if (!counters.length) return;

        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    this.animateCounter(entry.target);
                    observer.unobserve(entry.target);
                }
            });
        }, this.observerOptions);

        counters.forEach(counter => observer.observe(counter));
    },

    /**
     * Anima un contador
     * @param {HTMLElement} element - Elemento contador
     */
    animateCounter(element) {
        const target = parseInt(element.dataset.counter, 10);
        const duration = parseInt(element.dataset.duration, 10) || 2000;
        const start = 0;
        const startTime = performance.now();

        const updateCounter = (currentTime) => {
            const elapsed = currentTime - startTime;
            const progress = Math.min(elapsed / duration, 1);
            
            // Easing
            const easeOutQuart = 1 - Math.pow(1 - progress, 4);
            const current = Math.floor(start + (target - start) * easeOutQuart);
            
            element.textContent = current;
            
            if (progress < 1) {
                requestAnimationFrame(updateCounter);
            } else {
                element.textContent = target;
            }
        };

        requestAnimationFrame(updateCounter);
    },

    /**
     * Efecto de typing para código
     * @param {HTMLElement} element - Elemento con el código
     * @param {number} speed - Velocidad en ms por caracter
     */
    typeCode(element, speed = 50) {
        const text = element.textContent;
        element.textContent = '';
        element.style.visibility = 'visible';
        
        let index = 0;
        
        const type = () => {
            if (index < text.length) {
                element.textContent += text.charAt(index);
                index++;
                setTimeout(type, speed);
            }
        };
        
        type();
    },

    /**
     * Parallax suave
     * @param {HTMLElement} element - Elemento a animar
     * @param {number} speed - Velocidad del parallax (-1 a 1)
     */
    parallax(element, speed = 0.5) {
        const handleScroll = () => {
            const scrolled = window.pageYOffset;
            const rate = scrolled * speed;
            element.style.transform = `translate3d(0, ${rate}px, 0)`;
        };

        window.addEventListener('scroll', Utils.throttle(handleScroll, 16));
    }
};

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', () => Animations.init());
