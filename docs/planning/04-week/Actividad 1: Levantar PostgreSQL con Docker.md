# Levantar PostgreSQL con Docker

### Creamos el archivo docker-compose.yml 
![](https://github.com/julianguerra1231186-crypto/PulpApp-Sistema-Distribuido-de-Venta-Online-de-Pulpas-Naturales/blob/main/docs/planning/04-week/Captura%20de%20pantalla%20(115).png)

### Emepamos a subir datos al contenedor de base de datos para el microservicio ms-users utilizando Docker Compose. 
![](https://github.com/julianguerra1231186-crypto/PulpApp-Sistema-Distribuido-de-Venta-Online-de-Pulpas-Naturales/blob/main/docs/planning/04-week/Captura%20de%20pantalla%20(117).png)
![](https://github.com/julianguerra1231186-crypto/PulpApp-Sistema-Distribuido-de-Venta-Online-de-Pulpas-Naturales/blob/main/docs/planning/04-week/Captura%20de%20pantalla%20(118).png)
### Se nos presento  un error ya que no nos presenta las tablas qeu se crean en hibernate en el servidor de pg4admin
![](https://github.com/julianguerra1231186-crypto/PulpApp-Sistema-Distribuido-de-Venta-Online-de-Pulpas-Naturales/blob/main/docs/planning/04-week/Captura%20de%20pantalla%202026-02-28%20161711.png)
![](https://github.com/julianguerra1231186-crypto/PulpApp-Sistema-Distribuido-de-Venta-Online-de-Pulpas-Naturales/blob/main/docs/planning/04-week/Captura%20de%20pantalla%20(129).png)
### Durante la configuración del entorno con Docker surgió un problema con el puerto 5432. En un inicio, el microservicio estaba apuntando a la base de datos con la URL jdbc:postgresql://localhost:5432/pulpapp_users. El detalle fue que en la computadora ya había una instalación local de PostgreSQL funcionando en ese mismo puerto, lo que provocó un conflicto.Por esta razón, la aplicación Spring Boot no se estaba conectando al contenedor de Docker, sino a la base de datos instalada en el sistema operativo. Esto explicaba por qué Hibernate creaba la tabla sin inconvenientes y esta aparecía en el pgAdmin local. Sin embargo, al revisar directamente el contenedor con el comando docker exec -it pulpapp_users_db psql -U postgres -d pulpapp_users y ejecutar \dt, no se mostraba ninguna tabla. Eso dejaba claro que el contenedor realmente no estaba siendo utilizado.La confirmación llegó al cerrar Docker Desktop y notar que el microservicio seguía iniciando sin errores. Con eso quedó evidente que la conexión era hacia el PostgreSQL local.Para resolver el conflicto sin necesidad de desactivar la base de datos local, se modificó el archivo docker-compose.yml para exponer el contenedor en el puerto 5433 (5433:5432). Después, se actualizó el application.yml del microservicio para que se conectara a localhost:5433.Finalmente, se eliminaron y recrearon los contenedores con docker compose down -v y docker compose up -d. Con esta configuración, el PostgreSQL local continúa utilizando el puerto 5432 y el contenedor Docker trabaja en el 5433, asegurando que el microservicio se conecte correctamente a la base de datos del contenedor.
### Al verificar nuevamente, la tabla ya aparecía dentro de Docker, cumpliendo así con lo solicitado en la actividad.
![](https://github.com/julianguerra1231186-crypto/PulpApp-Sistema-Distribuido-de-Venta-Online-de-Pulpas-Naturales/blob/main/docs/planning/04-week/Captura%20de%20pantalla%202026-02-28%20164452.png)
![](https://github.com/julianguerra1231186-crypto/PulpApp-Sistema-Distribuido-de-Venta-Online-de-Pulpas-Naturales/blob/main/docs/planning/04-week/Captura%20de%20pantalla%20(138).png)

#### Primero se crea el User, luego se crea el Pedido enviando el userId. Hibernate genera automáticamente la foreign key user_id en la tabla pedidos.
![](https://github.com/julianguerra1231186-crypto/PulpApp-Sistema-Distribuido-de-Venta-Online-de-Pulpas-Naturales/blob/main/docs/planning/04-week/Captura%20de%20pantalla%20(152).png)
# Realizamos las pueblas con Postman Para verificar que nuestro sofware este funcionando y guardando
## creamos un usuario 
![](https://github.com/julianguerra1231186-crypto/PulpApp-Sistema-Distribuido-de-Venta-Online-de-Pulpas-Naturales/blob/main/docs/planning/04-week/Captura%20de%20pantalla%20(145).png)
## creamos un pedido 
![](https://github.com/julianguerra1231186-crypto/PulpApp-Sistema-Distribuido-de-Venta-Online-de-Pulpas-Naturales/blob/main/docs/planning/04-week/Captura%20de%20pantalla%20(148).png)
## Consultar pedidos
![](https://github.com/julianguerra1231186-crypto/PulpApp-Sistema-Distribuido-de-Venta-Online-de-Pulpas-Naturales/blob/main/docs/planning/04-week/Captura%20de%20pantalla%20(150).png)
## buscar usuarios
![](https://github.com/julianguerra1231186-crypto/PulpApp-Sistema-Distribuido-de-Venta-Online-de-Pulpas-Naturales/blob/main/docs/planning/04-week/Captura%20de%20pantalla%20(151).png)
## Organizamos nuestro Postman  
![](https://github.com/julianguerra1231186-crypto/PulpApp-Sistema-Distribuido-de-Venta-Online-de-Pulpas-Naturales/blob/main/docs/planning/04-week/Captura%20de%20pantalla%20(153).png)
# Actividad 3: Manejo de Errores y Validación
## (1) Agregar el GlobalExceptionHandler. (2) Implementar @NotBlank, @Email, @Size en los DTOs de request. (3) Probar que al enviar datos inválidos se retorna un error 400 con mensaje claro.
### Iniciamos añadiendo las validaciones
![](https://github.com/julianguerra1231186-crypto/PulpApp-Sistema-Distribuido-de-Venta-Online-de-Pulpas-Naturales/blob/main/docs/planning/04-week/Captura%20de%20pantalla%20(154).png)
![](https://github.com/julianguerra1231186-crypto/PulpApp-Sistema-Distribuido-de-Venta-Online-de-Pulpas-Naturales/blob/main/docs/planning/04-week/Captura%20de%20pantalla%20(156).png)
# realziamos las pluebas en postman
### Creamos un usuario que no cumple con las validaciones que establecimos, por eso lo rechaza
![](https://github.com/julianguerra1231186-crypto/PulpApp-Sistema-Distribuido-de-Venta-Online-de-Pulpas-Naturales/blob/main/docs/planning/04-week/Captura%20de%20pantalla%20(157).png)
### Pedido Sin ID 
![](https://github.com/julianguerra1231186-crypto/PulpApp-Sistema-Distribuido-de-Venta-Online-de-Pulpas-Naturales/blob/main/docs/planning/04-week/Captura%20de%20pantalla%20(159).png)

## Estamos usando anotaciones de validación de  en los DTOs para asegurarnos de que la información que llega sea correcta. Además, tenemos un GlobalExceptionHandler con @RestControllerAdvice que se encarga de atrapar la excepción MethodArgumentNotValidException y devolver un 400 con mensajes personalizados cuando algo no cumple con las validaciones.


# Actividad 4: Commit, PR y Merge(1) Commit con convención: feat(service-one): add PostgreSQL persistence. (2) Push a feature branch y crear PR hacia develop. (3) Review cruzado con otro equipo. (4) Merge aprobado — verificar que develop funciona con la BD.
## Yo lo hare de la sigueinte manera no quiero utilizar mi develop entonces creare una rama que se llama develop-actvididad y haremos merge con ella y pobraremos las funcionalidades sin tocar mi carperta develop 
### Iniciamos creando un commit 
![](https://github.com/julianguerra1231186-crypto/PulpApp-Sistema-Distribuido-de-Venta-Online-de-Pulpas-Naturales/blob/main/docs/planning/04-week/Captura%20de%20pantalla%20(161).png)
### creamos la rama Develop-Actividad desde la rama Actvidad-2 y la subimos Verificamos que estamos en la rama develop-actividad 
![](https://github.com/julianguerra1231186-crypto/PulpApp-Sistema-Distribuido-de-Venta-Online-de-Pulpas-Naturales/blob/main/docs/planning/04-week/Captura%20de%20pantalla%20(163).png)
### realizamos el merge 
![](https://github.com/julianguerra1231186-crypto/PulpApp-Sistema-Distribuido-de-Venta-Online-de-Pulpas-Naturales/blob/main/docs/planning/04-week/Captura%20de%20pantalla%20(161).png)
### desplegamos y verificamos que todo funcione bien 
![](https://github.com/julianguerra1231186-crypto/PulpApp-Sistema-Distribuido-de-Venta-Online-de-Pulpas-Naturales/blob/main/docs/planning/04-week/Captura%20de%20pantalla%20(175).png)
### pluebas con Postman 
## Crear Usuario
![](https://github.com/julianguerra1231186-crypto/PulpApp-Sistema-Distribuido-de-Venta-Online-de-Pulpas-Naturales/blob/main/docs/planning/04-week/Captura%20de%20pantalla%20(171).png)
## Probamos Validaciones 
![](https://github.com/julianguerra1231186-crypto/PulpApp-Sistema-Distribuido-de-Venta-Online-de-Pulpas-Naturales/blob/main/docs/planning/04-week/Captura%20de%20pantalla%20(172).png)
## Crear Pedidos 
![](https://github.com/julianguerra1231186-crypto/PulpApp-Sistema-Distribuido-de-Venta-Online-de-Pulpas-Naturales/blob/main/docs/planning/04-week/Captura%20de%20pantalla%20(173).png)
## Consultar Pedidos 
![](https://github.com/julianguerra1231186-crypto/PulpApp-Sistema-Distribuido-de-Venta-Online-de-Pulpas-Naturales/blob/main/docs/planning/04-week/Captura%20de%20pantalla%20(174).png)

### Para esta actividad trabajé sobre la rama feature actividad-2, donde implementé la persistencia con PostgreSQL en Docker, la relación entre entidades User y Pedido, el uso de DTOs, validaciones con Jakarta Validation y el manejo global de excepciones.
### Posteriormente realicé un Pull Request hacia la rama develop-actividad, donde se hizo el merge y verifiqué que la aplicación funcionara correctamente después de la integración.
### Decidí no hacer el merge directamente hacia develop para mantener esa rama principal intacta y evitar afectar posibles integraciones del equipo. Sin embargo, en cualquier momento puedo realizar el merge hacia develop si el docente o el equipo lo requieren, ya que la rama está completamente estable y validada.















