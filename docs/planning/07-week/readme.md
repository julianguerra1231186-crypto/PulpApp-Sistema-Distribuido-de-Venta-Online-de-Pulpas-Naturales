# Carrito de Compras MVP
# Descripción del Proyecto

Este repositorio contiene el desarrollo de un MVP (Minimum Viable Product) de un carrito de compras, enfocado en validar la lógica básica del sistema mediante la implementación de historias de usuario y control de versiones con Git.

El proyecto se desarrolló siguiendo un enfoque práctico basado en flujo de trabajo por ramas y desarrollo incremental.

# Enfoque de Desarrollo

El desarrollo del MVP se organizó a partir de:

✔️ Historias de usuario (definición funcional)
✔️ Ramas por funcionalidad (feature branches)
✔️ Integración progresiva al proyecto principal

Esto permitió trabajar de forma ordenada, simulando un entorno real de desarrollo colaborativo.

# Historias de Usuario Implementadas

Durante el desarrollo se definieron e implementaron las siguientes historias de usuario:

🧾 HU-001 — Agregar productos al carrito

Como usuario, quiero agregar productos al carrito,
para poder seleccionarlos antes de realizar una compra.

🧾 HU-002 — Eliminar productos del carrito

Como usuario, quiero eliminar productos del carrito,
para modificar mi selección de compra.

🧾 HU-003 — Listar productos del carrito

Como usuario, quiero ver los productos agregados al carrito,
para conocer mi selección actual.

🧾 HU-004 — Calcular total de compra

Como usuario, quiero ver el total de mi carrito,
para saber cuánto debo pagar.

# Estrategia de Ramas (Git)

El proyecto se desarrolló utilizando una estrategia basada en ramas para cada funcionalidad:

# Estructura utilizada
- main → Rama principal (versión estable del proyecto)
- develop → Rama de integración
- feature/* → Ramas por cada historia de usuario

# Flujo de trabajo
### Se crea una rama desde develop para cada historia de usuario:

 - feature/agregar-producto
 - feature/eliminar-producto
 - feature/listar-carrito
 - feature/calcular-total
 - Se desarrolla la funcionalidad de forma aislada
 - Se realizan commits claros y organizados
 - Se integra la funcionalidad a develop
 - Finalmente, se consolida en main

### Beneficios de este enfoque: 
- Desarrollo organizado
- Aislamiento de errores
- Mejor control de versiones
- Simulación de trabajo en equipo
- Escalabilidad del proyecto

### Tecnologías Utilizadas
 - Java 17
 - Spring Boot
 - Spring Data JPA
 - Maven
🚀 Estado del Proyecto

El proyecto se encuentra en fase MVP, con funcionalidades básicas implementadas y validadas mediante historias de usuario.

# Repositorio
- Puedes ver el desarrollo completo del carrito de compras en el siguiente enlace:
- https://github.com/julianguerra1231186-crypto/carrito-compras-mvp
