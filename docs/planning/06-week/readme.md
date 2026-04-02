# 🧾 PulpApp - Sistema Distribuido de Venta de Pulpas Naturales

## 👨‍💻 Autor

**Julian Guerra**

---

## 📌 Descripción del Proyecto

PulpApp es un sistema distribuido basado en microservicios desarrollado con **Spring Boot**, cuyo objetivo es gestionar la venta online de pulpas naturales de forma eficiente, escalable y mantenible.

Este proyecto no solo implementa funcionalidades backend, sino que también documenta decisiones arquitectónicas clave mediante el uso de **ADR (Architecture Decision Records)**.

---

## 🧠 ¿Qué son los ADR?

Los ADR (Architecture Decision Records) son documentos que permiten registrar decisiones técnicas importantes dentro de un proyecto.

En este repositorio se documentan:

* Problemas identificados
* Soluciones implementadas
* Justificación técnica
* Consecuencias de cada decisión

---

## 📂 Decisiones Arquitectónicas Implementadas

### 🔹 ADR-001 — Refactorización de la Capa de Servicios

Se eliminó la duplicación de código en las operaciones CRUD mediante la creación de una clase base (`BaseServiceImpl`).

**Resultado:**

* Código más limpio
* Mayor reutilización
* Mejor mantenibilidad

---

### 🔹 ADR-002 — Implementación de MapStruct

Se reemplazó el mapeo manual entre entidades y DTOs por **MapStruct**, automatizando este proceso.

**Resultado:**

* Reducción de errores
* Código más limpio
* Mejor rendimiento en tiempo de compilación

---

### 🔹 ADR-003 — Manejo de Excepciones Personalizadas

Se implementaron excepciones de dominio y un manejador global de errores (`@RestControllerAdvice`).

**Resultado:**

* Manejo de errores centralizado
* Respuestas estructuradas en JSON
* Uso correcto de códigos HTTP

---

## 🐳 Infraestructura y Tecnologías

Este proyecto utiliza herramientas modernas de desarrollo backend:

* Java 17
* Spring Boot
* Spring Data JPA
* Spring Security
* PostgreSQL
* Docker
* Maven

Además, se realizaron pruebas funcionales utilizando **Postman**, validando el correcto funcionamiento de los endpoints.

---

## 🚀 Resultados Obtenidos

Gracias a la implementación de los ADR:

* Se redujo significativamente la duplicación de código
* Se mejoró la arquitectura del sistema
* Se automatizó el mapeo de datos
* Se implementó un manejo profesional de errores
* Se logró un sistema más limpio, escalable y mantenible

---

## 📊 Evidencias

El proyecto cuenta con:

* Ejecución correcta en Docker
* Conexión funcional con PostgreSQL
* Pruebas de endpoints en Postman
* Código implementado en microservicios

---

## 🔗 Acceso al Repositorio Completo

Para ver toda la documentación detallada, implementación en código y evidencias completas de los ADR, puedes acceder al repositorio en el siguiente enlace:

👉 **Haz clic aquí para ver todo a fondo:**

[https://github.com/julianguerra1231186-crypto/ADR/blob/main/1-ADR.md](https://github.com/julianguerra1231186-crypto/ADR/blob/main/1-ADR.md)

---

## 🔮 Trabajo Futuro

Como parte de la evolución del proyecto, se plantea:

* Implementar seguridad con JWT
* Mejorar la comunicación entre microservicios
* Desarrollar pruebas automatizadas (unitarias e integración)
* Optimizar la escalabilidad del sistema

---

## 🏆 Conclusión

Este proyecto demuestra la aplicación de buenas prácticas de desarrollo backend, arquitectura limpia y toma de decisiones técnicas estructuradas.

No solo se desarrolló un sistema funcional, sino que también se documentó el proceso de construcción de una arquitectura profesional orientada a entornos reales.

