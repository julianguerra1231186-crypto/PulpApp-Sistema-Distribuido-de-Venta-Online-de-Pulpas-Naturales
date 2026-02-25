/**
 * Navigation.js - Manejo de la navegación
 */

const Navigation = {
    header: null,
    navToggle: null,
    navMenu: null,
    navLinks: null,
    lastScrollPosition: 0,

    /**
     * Inicializa la navegación
     */
    init() {
        this.header = document.getElementById('header');
        this.navToggle = document.getElementById('nav-toggle');
        this.navMenu = document.getElementById('nav-menu');
        this.navLinks = document.querySelectorAll('.nav__link');

        if (!this.header) return;

        this.bindEvents();
        this.handleScroll();
    },

    /**
     * Bindea los eventos
     */
    bindEvents() {
        // Toggle del menú móvil
        if (this.navToggle) {
            this.navToggle.addEventListener('click', () => this.toggleMenu());
        }

        // Cerrar menú al hacer clic en un enlace
        this.navLinks.forEach(link => {
            link.addEventListener('click', (e) => this.handleNavClick(e));
        });

        // Scroll event con throttle
        window.addEventListener('scroll', Utils.throttle(() => this.handleScroll(), 100));

        // Cerrar menú al hacer clic fuera
        document.addEventListener('click', (e) => this.handleOutsideClick(e));

        // Cerrar menú con Escape
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && this.navMenu?.classList.contains('active')) {
                this.closeMenu();
            }
        });
    },

    /**
     * Toggle del menú móvil
     */
    toggleMenu() {
        const isOpen = this.navMenu?.classList.toggle('active');
        this.navToggle?.classList.toggle('active');
        
        // Prevenir scroll del body cuando el menú está abierto
        document.body.style.overflow = isOpen ? 'hidden' : '';
        
        // Accesibilidad
        this.navToggle?.setAttribute('aria-expanded', isOpen);
    },

    /**
     * Cierra el menú móvil
     */
    closeMenu() {
        this.navMenu?.classList.remove('active');
        this.navToggle?.classList.remove('active');
        document.body.style.overflow = '';
        this.navToggle?.setAttribute('aria-expanded', 'false');
    },

    /**
     * Maneja el clic en enlaces de navegación
     * @param {Event} e - Evento de clic
     */
    handleNavClick(e) {
        const href = e.currentTarget.getAttribute('href');
        
        if (href && href.startsWith('#')) {
            e.preventDefault();
            this.closeMenu();
            
            // Scroll suave al elemento
            Utils.scrollTo(href);
            
            // Actualizar URL sin recargar
            history.pushState(null, '', href);
            
            // Actualizar estado activo
            this.updateActiveLink(href);
        }
    },

    /**
     * Maneja el clic fuera del menú
     * @param {Event} e - Evento de clic
     */
    handleOutsideClick(e) {
        if (!this.navMenu?.classList.contains('active')) return;
        
        const isClickInside = this.navMenu.contains(e.target) || 
                              this.navToggle?.contains(e.target);
        
        if (!isClickInside) {
            this.closeMenu();
        }
    },

    /**
     * Maneja el evento de scroll
     */
    handleScroll() {
        const scrollPosition = Utils.getScrollPosition();
        
        // Agregar clase cuando hay scroll
        if (scrollPosition > 10) {
            this.header?.classList.add('header--scrolled');
        } else {
            this.header?.classList.remove('header--scrolled');
        }

        // Actualizar enlace activo basado en la sección visible
        this.updateActiveLinkOnScroll();
        
        this.lastScrollPosition = scrollPosition;
    },

    /**
     * Actualiza el enlace activo basado en el scroll
     */
    updateActiveLinkOnScroll() {
        const sections = document.querySelectorAll('section[id]');
        const headerHeight = this.header?.offsetHeight || 0;
        
        let currentSection = '';
        
        sections.forEach(section => {
            const sectionTop = section.offsetTop - headerHeight - 100;
            const sectionBottom = sectionTop + section.offsetHeight;
            const scrollPosition = Utils.getScrollPosition();
            
            if (scrollPosition >= sectionTop && scrollPosition < sectionBottom) {
                currentSection = '#' + section.getAttribute('id');
            }
        });
        
        if (currentSection) {
            this.updateActiveLink(currentSection);
        }
    },

    /**
     * Actualiza el estado activo de los enlaces
     * @param {string} href - Hash del enlace activo
     */
    updateActiveLink(href) {
        this.navLinks.forEach(link => {
            const linkHref = link.getAttribute('href');
            link.classList.toggle('nav__link--active', linkHref === href);
        });
    }
};

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', () => Navigation.init());
