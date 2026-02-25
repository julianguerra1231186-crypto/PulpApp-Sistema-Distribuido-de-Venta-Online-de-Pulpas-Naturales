/**
 * Sistemas Distribuidos - Roadmap Gamificado
 * CORHUILA - Corporación Universitaria del Huila
 * 
 * @description Script principal para la gestión del progreso del curso
 * @version 2.0.0
 */

// ===== DATOS DEL CURSO =====
const COURSE_DATA = {
    name: "Sistemas Distribuidos",
    totalWeeks: 16,
    cortes: [
        { id: 1, name: "Primer Corte", weeks: [1, 2, 3, 4, 5], percentage: 30 },
        { id: 2, name: "Segundo Corte", weeks: [6, 7, 8, 9, 10], percentage: 35 },
        { id: 3, name: "Tercer Corte", weeks: [11, 12, 13, 14, 15, 16], percentage: 35 }
    ],
    weeks: [
        {
            num: 1,
            title: "Fundamentos y Setup",
            topic: "Introducción a Sistemas Distribuidos",
            type: "normal",
            sessions: [
                { type: "weekly", title: "Formación de equipos", desc: "Presentación, equipos de trabajo, lluvia de ideas de proyectos" },
                { type: "planning", title: "Fundamentos SD", desc: "Teorema CAP, arquitecturas distribuidas, patrones básicos" }
            ],
            tags: ["Git", "GitHub"]
        },
        {
            num: 2,
            title: "Arquitectura del Proyecto",
            topic: "Diseño de Microservicios",
            type: "normal",
            sessions: [
                { type: "weekly", title: "Definición del proyecto", desc: "User stories, arquitectura inicial, backlog del producto" },
                { type: "planning", title: "Microservicios", desc: "Patrones de diseño, Domain-Driven Design, bounded contexts" }
            ],
            tags: ["DDD", "Arquitectura"]
        },
        {
            num: 3,
            title: "Repositorio y Ambientes",
            topic: "Control de Versiones Avanzado",
            type: "normal",
            sessions: [
                { type: "weekly", title: "Setup del repositorio", desc: "Estructura monorepo/multirepo, branches develop/qa/main" },
                { type: "planning", title: "API Gateway", desc: "Service Discovery, comunicación síncrona, REST APIs" }
            ],
            tags: ["Git Flow", "GitHub Actions"]
        },
        {
            num: 4,
            title: "Primer Microservicio",
            topic: "Java Spring Boot",
            type: "normal",
            sessions: [
                { type: "weekly", title: "Microservicio Java", desc: "Desarrollo del primer microservicio con Spring Boot" },
                { type: "planning", title: "PostgreSQL", desc: "Bases de datos relacionales en microservicios, migraciones" }
            ],
            tags: ["Java", "Spring Boot", "PostgreSQL"]
        },
        {
            num: 5,
            title: "⭐ ENTREGA 1: MVP",
            topic: "Primera Entrega de Producto",
            type: "entrega",
            sessions: [
                { type: "weekly", title: "Presentación MVP", desc: "Demo del MVP: API funcional básica con al menos un microservicio" },
                { type: "planning", title: "Retroalimentación", desc: "Feedback, métricas de calidad, planificación segundo corte" }
            ],
            tags: ["Demo", "MVP", "Evaluación"]
        },
        {
            num: 6,
            title: "Base de Datos NoSQL",
            topic: "MongoDB en Microservicios",
            type: "normal",
            sessions: [
                { type: "weekly", title: "Avances post-MVP", desc: "Mejoras basadas en feedback, refactoring inicial" },
                { type: "planning", title: "MongoDB", desc: "Modelado NoSQL, agregaciones, índices para microservicios" }
            ],
            tags: ["MongoDB", "NoSQL"]
        },
        {
            num: 7,
            title: "Segundo Microservicio",
            topic: "Integración MongoDB",
            type: "normal",
            sessions: [
                { type: "weekly", title: "Micro con MongoDB", desc: "Desarrollo de microservicio con base de datos NoSQL" },
                { type: "planning", title: "Mensajería Asíncrona", desc: "Patrones de comunicación, eventos, pub/sub" }
            ],
            tags: ["MongoDB", "Eventos"]
        },
        {
            num: 8,
            title: "Comunicación Asíncrona",
            topic: "Message Brokers",
            type: "normal",
            sessions: [
                { type: "weekly", title: "Implementación mensajería", desc: "RabbitMQ o Kafka para comunicación entre servicios" },
                { type: "planning", title: "Docker Básico", desc: "Contenedorización, Dockerfiles, buenas prácticas" }
            ],
            tags: ["RabbitMQ", "Kafka", "Docker"]
        },
        {
            num: 9,
            title: "Contenedorización",
            topic: "Docker y Orquestación",
            type: "normal",
            sessions: [
                { type: "weekly", title: "Dockerización completa", desc: "Todos los servicios en contenedores Docker" },
                { type: "planning", title: "Docker Compose", desc: "Orquestación local, redes, volúmenes, health checks" }
            ],
            tags: ["Docker", "Docker Compose"]
        },
        {
            num: 10,
            title: "⭐ ENTREGA 2: Multi-Servicios",
            topic: "Segunda Entrega de Producto",
            type: "entrega",
            sessions: [
                { type: "weekly", title: "Demo Multi-servicios", desc: "Sistema con múltiples microservicios comunicándose" },
                { type: "planning", title: "Retroalimentación", desc: "Evaluación técnica, deuda técnica, plan tercer corte" }
            ],
            tags: ["Demo", "Evaluación"]
        },
        {
            num: 11,
            title: "CI/CD Pipeline",
            topic: "Integración y Despliegue Continuo",
            type: "normal",
            sessions: [
                { type: "weekly", title: "Avances CI/CD", desc: "Configuración de pipelines de integración continua" },
                { type: "planning", title: "GitHub Actions", desc: "Workflows, jobs, secrets, environments" }
            ],
            tags: ["CI/CD", "GitHub Actions"]
        },
        {
            num: 12,
            title: "Microfrontend Angular",
            topic: "Frontend Distribuido",
            type: "normal",
            sessions: [
                { type: "weekly", title: "Desarrollo MicroFE", desc: "Implementación del microfrontend en Angular" },
                { type: "planning", title: "Estrategias de deploy", desc: "Ambientes develop, qa, main - feature flags" }
            ],
            tags: ["Angular", "Microfrontend"]
        },
        {
            num: 13,
            title: "Integración Full Stack",
            topic: "Frontend + Backend",
            type: "normal",
            sessions: [
                { type: "weekly", title: "Integración FE-BE", desc: "Conexión del frontend con los microservicios" },
                { type: "planning", title: "Monitoreo", desc: "Logging centralizado, métricas, health endpoints" }
            ],
            tags: ["Integración", "Observabilidad"]
        },
        {
            num: 14,
            title: "Resiliencia",
            topic: "Patrones de Tolerancia a Fallos",
            type: "normal",
            sessions: [
                { type: "weekly", title: "Testing y resiliencia", desc: "Pruebas de integración, manejo de errores" },
                { type: "planning", title: "Circuit Breaker", desc: "Retry, timeout, fallback, bulkhead patterns" }
            ],
            tags: ["Testing", "Resilience4j"]
        },
        {
            num: 15,
            title: "⭐ ENTREGA 3: Sistema Completo",
            topic: "Tercera Entrega de Producto",
            type: "entrega",
            sessions: [
                { type: "weekly", title: "Demo Final", desc: "Sistema completo desplegado con todos los componentes" },
                { type: "planning", title: "Documentación", desc: "README, diagramas de arquitectura, API docs" }
            ],
            tags: ["Demo", "Producción", "Evaluación"]
        },
        {
            num: 16,
            title: "🎓 Cierre del Curso",
            topic: "Retrospectiva y Presentación Final",
            type: "cierre",
            sessions: [
                { type: "weekly", title: "Retrospectiva", desc: "Lecciones aprendidas, mejores prácticas identificadas" },
                { type: "planning", title: "Demo Final", desc: "Presentación ejecutiva del proyecto completo" }
            ],
            tags: ["Presentación", "Cierre"]
        }
    ],
    badges: [
        { id: "starter", icon: "🚀", name: "Iniciador", desc: "Completa la semana 1", requirement: 1 },
        { id: "mvp", icon: "💡", name: "MVP Builder", desc: "Primera entrega exitosa", requirement: 5 },
        { id: "architect", icon: "🏗️", name: "Arquitecto", desc: "Semana 10 completada", requirement: 10 },
        { id: "fullstack", icon: "⚡", name: "Full Stack", desc: "Frontend integrado", requirement: 13 },
        { id: "deployer", icon: "🚢", name: "Deployer", desc: "Sistema desplegado", requirement: 15 },
        { id: "master", icon: "🎓", name: "Master SD", desc: "Curso completado", requirement: 16 }
    ]
};

// ===== ESTADO DE LA APLICACIÓN =====
const STORAGE_KEY = 'sd_roadmap_v2';
let state = {
    completedWeeks: new Set(),
    currentFilter: 'all'
};

// ===== FUNCIONES DE PERSISTENCIA =====
function loadState() {
    try {
        const saved = localStorage.getItem(STORAGE_KEY);
        if (saved) {
            const parsed = JSON.parse(saved);
            state.completedWeeks = new Set(parsed.completedWeeks || []);
        }
    } catch (error) {
        console.error('Error loading state:', error);
    }
}

function saveState() {
    try {
        localStorage.setItem(STORAGE_KEY, JSON.stringify({
            completedWeeks: Array.from(state.completedWeeks)
        }));
    } catch (error) {
        console.error('Error saving state:', error);
    }
}

// ===== FUNCIONES DE CÁLCULO =====
function getProgress() {
    return {
        completed: state.completedWeeks.size,
        total: COURSE_DATA.totalWeeks,
        percentage: Math.round((state.completedWeeks.size / COURSE_DATA.totalWeeks) * 100)
    };
}

function getCorteProgress(corte) {
    const completedInCorte = corte.weeks.filter(w => state.completedWeeks.has(w)).length;
    return {
        completed: completedInCorte,
        total: corte.weeks.length,
        percentage: Math.round((completedInCorte / corte.weeks.length) * 100)
    };
}

function getUnlockedBadges() {
    const completed = state.completedWeeks.size;
    return COURSE_DATA.badges.filter(b => completed >= b.requirement);
}

// ===== FUNCIONES DE TOGGLE =====
function toggleWeek(weekNum) {
    if (state.completedWeeks.has(weekNum)) {
        state.completedWeeks.delete(weekNum);
    } else {
        state.completedWeeks.add(weekNum);
    }
    saveState();
    render();
    checkBadgeUnlock(weekNum);
}

function checkBadgeUnlock(weekNum) {
    const badge = COURSE_DATA.badges.find(b => b.requirement === weekNum);
    if (badge && state.completedWeeks.has(weekNum)) {
        showBadgeModal(badge);
    }
}

function resetProgress() {
    state.completedWeeks = new Set();
    saveState();
    render();
    closeModal();
}

// ===== FUNCIONES DE FILTRADO =====
function setFilter(filter) {
    state.currentFilter = filter;
    render();
}

function getFilteredWeeks() {
    switch (state.currentFilter) {
        case 'entrega':
            return COURSE_DATA.weeks.filter(w => w.type === 'entrega');
        case 'pending':
            return COURSE_DATA.weeks.filter(w => !state.completedWeeks.has(w.num));
        case 'completed':
            return COURSE_DATA.weeks.filter(w => state.completedWeeks.has(w.num));
        default:
            return COURSE_DATA.weeks;
    }
}

// ===== RENDERIZADO =====
function renderProgress() {
    const progress = getProgress();
    const progressFill = document.getElementById('progressFill');
    const progressValue = document.getElementById('progressValue');
    const progressText = document.getElementById('progressText');
    
    if (progressFill) progressFill.style.width = `${progress.percentage}%`;
    if (progressValue) progressValue.textContent = `${progress.percentage}%`;
    if (progressText) progressText.textContent = `${progress.completed} de ${progress.total} semanas completadas`;
}

function renderHeroStats() {
    const progress = getProgress();
    const badges = getUnlockedBadges();
    
    document.getElementById('statWeeks').textContent = progress.completed;
    document.getElementById('statBadges').textContent = badges.length;
    document.getElementById('statProgress').textContent = `${progress.percentage}%`;
}

function renderRoadmap() {
    const container = document.getElementById('roadmapContainer');
    if (!container) return;
    
    let html = '';
    
    COURSE_DATA.cortes.forEach(corte => {
        const corteProgress = getCorteProgress(corte);
        const corteWeeks = COURSE_DATA.weeks.filter(w => corte.weeks.includes(w.num));
        const filteredWeeks = corteWeeks.filter(w => {
            if (state.currentFilter === 'all') return true;
            if (state.currentFilter === 'entrega') return w.type === 'entrega';
            if (state.currentFilter === 'pending') return !state.completedWeeks.has(w.num);
            if (state.currentFilter === 'completed') return state.completedWeeks.has(w.num);
            return true;
        });
        
        if (filteredWeeks.length === 0) return;
        
        html += `
            <div class="corte-section">
                <div class="corte-header">
                    <span class="corte-badge corte-badge--${corte.id}">Corte ${corte.id}</span>
                    <div class="corte-info">
                        <h3>${corte.name}</h3>
                        <span>Semanas ${corte.weeks[0]} - ${corte.weeks[corte.weeks.length - 1]} | ${corte.percentage}% del curso</span>
                    </div>
                    <div class="corte-progress">
                        <div class="corte-progress__value">${corteProgress.percentage}%</div>
                        <div class="corte-progress__label">${corteProgress.completed}/${corteProgress.total} semanas</div>
                    </div>
                </div>
                <div class="weeks-grid">
                    ${filteredWeeks.map(week => renderWeekCard(week)).join('')}
                </div>
            </div>
        `;
    });
    
    container.innerHTML = html;
    
    // Añadir event listeners
    document.querySelectorAll('.week-card').forEach(card => {
        card.addEventListener('click', () => {
            const weekNum = parseInt(card.dataset.week);
            toggleWeek(weekNum);
        });
    });
}

function renderWeekCard(week) {
    const isCompleted = state.completedWeeks.has(week.num);
    const typeClass = week.type === 'entrega' ? 'week-card--entrega' : 
                      week.type === 'cierre' ? 'week-card--cierre' : '';
    const completedClass = isCompleted ? 'week-card--completed' : '';
    
    return `
        <div class="week-card ${typeClass} ${completedClass}" data-week="${week.num}">
            <div class="week-card__header">
                <div class="week-card__number">${isCompleted ? '✓' : week.num}</div>
                <div class="week-card__title">
                    <h4>${week.title}</h4>
                    <span>${week.topic}</span>
                </div>
                <div class="week-card__check">
                    ${isCompleted ? '✓' : ''}
                </div>
            </div>
            <div class="week-card__body">
                <div class="week-card__sessions">
                    ${week.sessions.map(session => `
                        <div class="session session--${session.type}">
                            <span class="session__type">${session.type}</span>
                            <div class="session__content">
                                <div class="session__title">${session.title}</div>
                                <div class="session__desc">${session.desc}</div>
                            </div>
                        </div>
                    `).join('')}
                </div>
                <div class="week-card__tags">
                    ${week.tags.map(tag => `<span class="tech-tag">${tag}</span>`).join('')}
                </div>
            </div>
        </div>
    `;
}

function renderBadges() {
    const container = document.getElementById('badgesContainer');
    if (!container) return;
    
    const unlockedIds = getUnlockedBadges().map(b => b.id);
    
    container.innerHTML = COURSE_DATA.badges.map(badge => {
        const isUnlocked = unlockedIds.includes(badge.id);
        return `
            <div class="badge-card ${isUnlocked ? 'badge-card--unlocked' : 'badge-card--locked'}">
                <div class="badge-card__icon">${badge.icon}</div>
                <h4 class="badge-card__title">${badge.name}</h4>
                <p class="badge-card__desc">${badge.desc}</p>
                <span class="badge-card__status">${isUnlocked ? '✓ Desbloqueado' : '🔒 Bloqueado'}</span>
            </div>
        `;
    }).join('');
}

function renderFilters() {
    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.classList.toggle('active', btn.dataset.filter === state.currentFilter);
    });
}

function render() {
    renderProgress();
    renderHeroStats();
    renderRoadmap();
    renderBadges();
    renderFilters();
}

// ===== MODAL =====
function openResetModal() {
    const modal = document.getElementById('resetModal');
    if (modal) modal.classList.add('open');
}

function closeModal() {
    document.querySelectorAll('.modal').forEach(m => m.classList.remove('open'));
}

function showBadgeModal(badge) {
    const modal = document.getElementById('badgeModal');
    if (!modal) return;
    
    document.getElementById('badgeModalIcon').textContent = badge.icon;
    document.getElementById('badgeModalTitle').textContent = `¡${badge.name} Desbloqueado!`;
    document.getElementById('badgeModalDesc').textContent = badge.desc;
    
    modal.classList.add('open');
    
    // Auto-close después de 3 segundos
    setTimeout(() => {
        modal.classList.remove('open');
    }, 3000);
}

// ===== SCROLL TOP =====
function initScrollTop() {
    const btn = document.getElementById('scrollTopBtn');
    if (!btn) return;
    
    window.addEventListener('scroll', () => {
        btn.classList.toggle('visible', window.scrollY > 300);
    });
    
    btn.addEventListener('click', () => {
        window.scrollTo({ top: 0, behavior: 'smooth' });
    });
}

// ===== NAVEGACIÓN ACTIVA =====
function initActiveNav() {
    const navLinks = document.querySelectorAll('.header__nav a');
    const sections = document.querySelectorAll('section[id]');
    
    window.addEventListener('scroll', () => {
        let current = '';
        sections.forEach(section => {
            const sectionTop = section.offsetTop - 100;
            if (window.scrollY >= sectionTop) {
                current = section.getAttribute('id');
            }
        });
        
        navLinks.forEach(link => {
            link.classList.remove('active');
            if (link.getAttribute('href') === `#${current}`) {
                link.classList.add('active');
            }
        });
    });
}

// ===== EVENT LISTENERS =====
function initEventListeners() {
    // Filtros
    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            setFilter(btn.dataset.filter);
        });
    });
    
    // Modal reset
    const resetBtn = document.getElementById('btnReset');
    if (resetBtn) {
        resetBtn.addEventListener('click', openResetModal);
    }
    
    // Botones del modal
    document.getElementById('modalCancel')?.addEventListener('click', closeModal);
    document.getElementById('modalConfirm')?.addEventListener('click', resetProgress);
    document.getElementById('badgeModalClose')?.addEventListener('click', closeModal);
    
    // Cerrar modal al hacer clic fuera
    document.querySelectorAll('.modal').forEach(modal => {
        modal.addEventListener('click', (e) => {
            if (e.target === modal) closeModal();
        });
    });
}

// ===== INICIALIZACIÓN =====
document.addEventListener('DOMContentLoaded', () => {
    loadState();
    render();
    initScrollTop();
    initActiveNav();
    initEventListeners();
    
    console.log('🚀 Sistemas Distribuidos Roadmap v2.0 inicializado');
});
