# Decisiones Arquitectónicas y Staging

Durante la Week 5 del proyecto **PupaApp (Carrito de Compras MVP)**, el enfoque principal estuvo en consolidar decisiones arquitectónicas y estabilizar el entorno de staging.  

Se priorizó dejar claridad técnica en el equipo y preparar la base para las siguientes iteraciones del sistema.

---

## 🧩 Historias de Usuario (HU)

En esta semana no se agregaron nuevas historias de usuario prioritarias.  
El enfoque estuvo en:

- Documentación de arquitectura (ADR)
- Y se trabajo en el MPV(como enfoque principal)
- Estabilidad de despliegues
- Validación del entorno

---

## 🎯 Objetivo

Documentar las decisiones arquitectónicas clave del sistema y validar su impacto en el entorno de staging, garantizando:

- Trazabilidad de decisiones
- Claridad técnica para el equipo
- Base sólida para futuras implementaciones

---

## ✅ ¿Qué se hizo?

- Identificación y documentación de **ADRs (Architecture Decision Records)** relevantes para el proyecto.
- Análisis del impacto de las decisiones en los microservicios (especialmente `ms-products` y servicios relacionados).
- Validación del funcionamiento en entorno de **staging (Docker + PostgreSQL)**.
- Verificación de despliegues y comunicación entre microservicios.
- Preparación inicial para monitoreo básico del sistema.

---

## ⚠️ ¿Qué no se logró?

- No quedaron tareas críticas pendientes.
- Sin embargo, algunas decisiones documentadas en los ADR aún **no han sido implementadas completamente en el código**, quedando para próximas semanas.

---

## 🚀 ¿Qué se va a hacer?

- Aplicar los ADR documentados directamente en los repositorios del proyecto.
- Mejorar la estabilidad de los microservicios (ej: solución completa de errores como `ERR_EMPTY_RESPONSE` en `ms-products`).
- Extender la configuración de monitoreo y logging.
- Continuar validaciones en staging antes de pasar a producción.
- Realizar despliegues controlados asegurando funcionamiento end-to-end del sistema.

---

## 🧠 Notas

Esta semana fue clave para **ordenar la arquitectura del proyecto**, evitando decisiones improvisadas y asegurando una evolución más estructurada del sistema.

