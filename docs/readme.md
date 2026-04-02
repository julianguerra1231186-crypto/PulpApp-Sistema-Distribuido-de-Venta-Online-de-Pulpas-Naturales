# 📊 PupaApp - Evolución del Proyecto por Semanas

## 🧾 Descripción General

El proyecto **PupaApp** corresponde a un sistema distribuido para la venta online de pulpas naturales, construido bajo una arquitectura de microservicios.  

A lo largo de 6 semanas, el desarrollo ha evolucionado desde la definición conceptual hasta la integración parcial de componentes, alcanzando un **78% de avance**.

---

## 📈 Visualización del Avance

| Semana   | Avance |
|----------|--------|
| Semana 1 | 10%    |
| Semana 2 | 25%    |
| Semana 3 | 40%    |
| Semana 4 | 60%    |
| Semana 5 | 75%    |
| Semana 6 | 78%    |

---

## 🧠 Interpretación de la Métrica

- **10% – 30%** → Fase de planificación y análisis del proyecto  
- **30% – 60%** → Fase de diseño del sistema y arquitectura  
- **60% – 90%** → Fase de desarrollo e integración  
- **90% – 100%** → Pruebas finales, documentación y despliegue  

---

## 📅 Detalle Técnico por Semana

### 🔹 Semana 1 - Definición del Proyecto (10%)
- Definición del alcance del sistema
- Identificación del problema y propuesta de solución
- Conformación del equipo de trabajo
- Asignación de roles

**Entregables:**
- Idea del proyecto
- Propósito del sistema

---

### 🔹 Semana 2 - Análisis de Requerimientos (25%)
- Identificación de necesidades del sistema
- Análisis de usuarios y funcionalidades principales

**Entregables:**
- Documento de necesidades del sistema

---

### 🔹 Semana 3 - Requerimientos RF y RNF (40%)
- Definición de requerimientos funcionales (RF)
- Definición de requerimientos no funcionales (RNF)
- Estructuración inicial del sistema

**Entregables:**
- Documento de RF y RNF
- Base estructural del sistema

---

### 🔹 Semana 4 - Diseño y Arquitectura (60%)
- Diseño de arquitectura basada en microservicios
- Definición de servicios como `ms-products`
- Establecimiento de comunicación mediante APIs REST
- Selección de tecnologías:
  - Spring Boot
  - Docker
  - PostgreSQL

**Entregables:**
- Diagramas de arquitectura
- Diseño del sistema

---

### 🔹 Semana 5 - ADR y Staging (75%)
- Documentación de decisiones arquitectónicas (ADR)
- Validación del sistema en entorno de staging (Docker)
- Pruebas de despliegue y comunicación entre servicios

**Problema detectado:**
- `ms-products` no respondía (`ERR_EMPTY_RESPONSE`)
- Posible fallo interno del servicio o conexión a base de datos

**Acciones realizadas:**
- Revisión de logs de contenedor
- Validación de variables de entorno
- Verificación de conexión a PostgreSQL
- Revisión de puertos y configuración Docker

**Entregables:**
- ADR documentados
- Validaciones en entorno de staging

---

### 🔹 Semana 6 - Desarrollo e Integración (78%)
- Implementación de historias de usuario (frontend y backend)
- Integración parcial entre microservicios
- Desarrollo modular del sistema

**Estado de HU:**
- HU-FE-01, 03-06 → Implementadas  
- HU-FE-02, 07-10 → Pendientes  
- Backend → HU-doc, HU-MCI en progreso  

**Entregables:**
- Interfaces funcionales
- Integración parcial del sistema

---

## ⚙️ Estado Técnico Actual

- Arquitectura de microservicios definida
- Entorno Docker funcional
- Persistencia con PostgreSQL configurada
- Servicios parcialmente integrados
- Problemas puntuales en estabilidad (ej: `ms-products`)

---

## 🚀 Próximos Pasos

- Implementar completamente los ADR en código
- Resolver errores de comunicación entre microservicios
- Mejorar monitoreo y logging
- Completar historias de usuario pendientes
- Preparar despliegue a producción

---

## 🧩 Conclusión

El proyecto ha evolucionado de manera estructurada, pasando de una fase conceptual a una implementación técnica real.  

La base arquitectónica ya está definida, y el enfoque actual está en **estabilización, integración y preparación para producción**.
