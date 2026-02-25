/**
 * Environments.js - Sistema de tabs para la sección de Ambientes
 */

const EnvironmentTabs = {
    /**
     * Inicializa el sistema de tabs
     */
    init() {
        const buttons = document.querySelectorAll('.env-tabs__btn');
        if (!buttons.length) return;

        buttons.forEach(btn => {
            btn.addEventListener('click', () => this.activate(btn.dataset.tab));
        });

        // Soporte de teclado (← →)
        const nav = document.querySelector('.env-tabs__nav');
        if (nav) {
            nav.addEventListener('keydown', (e) => this.handleKeyNav(e));
        }
    },

    /**
     * Activa un tab por nombre de ambiente
     * @param {string} tabName - nombre del tab (develop | qa | release | main)
     */
    activate(tabName) {
        // Botones
        document.querySelectorAll('.env-tabs__btn').forEach(btn => {
            const isActive = btn.dataset.tab === tabName;
            btn.classList.toggle('active', isActive);
            btn.setAttribute('aria-selected', isActive);
        });

        // Panels
        document.querySelectorAll('.env-panel').forEach(panel => {
            panel.classList.toggle('active', panel.id === `tab-${tabName}`);
        });
    },

    /**
     * Navegación con flechas del teclado
     * @param {KeyboardEvent} e
     */
    handleKeyNav(e) {
        const buttons = [...document.querySelectorAll('.env-tabs__btn')];
        const current = document.querySelector('.env-tabs__btn.active');
        const idx = buttons.indexOf(current);

        let next = -1;
        if (e.key === 'ArrowRight') next = (idx + 1) % buttons.length;
        if (e.key === 'ArrowLeft')  next = (idx - 1 + buttons.length) % buttons.length;

        if (next >= 0) {
            e.preventDefault();
            this.activate(buttons[next].dataset.tab);
            buttons[next].focus();
        }
    }
};

document.addEventListener('DOMContentLoaded', () => EnvironmentTabs.init());
