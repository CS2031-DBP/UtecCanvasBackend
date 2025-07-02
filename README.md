# 19
# UTEC++

## Curso
**CS 2031 - Desarrollo Basado en Plataforma**

## Integrantes
- Santiago Aldebaran Cama Ardiles

---

## Índice
1. [Introducción](#introducción)
2. [Identificación del Problema o Necesidad](#identificación-del-problema-o-necesidad)
3. [Descripción de la Solución](#descripción-de-la-solución)
    - [Funcionalidades Implementadas](#funcionalidades-implementadas)
    - [Tecnologías Utilizadas](#tecnologías-utilizadas)
4. [Modelo de Entidades](#modelo-de-entidades)
    - [Descripción de Entidades](#entidades-principales)
5. [Testing y Manejo de Errores](#testing-y-manejo-de-errores)
6. [Medidas de Seguridad Implementadas](#medidas-de-seguridad-implementadas)
    - [Seguridad de Datos](#seguridad-de-datos)
    - [Prevención de Vulnerabilidades](#prevención-de-vulnerabilidades)
7. [Eventos y Asincronía](#eventos-y-asincronía)
    - [Eventos Utilizados](#eventos-utilizados)
    - [Asincronía](#asincronía)
    - [Servicio de Email](#servicio-de-email)
8. [Conclusión](#conclusión)
    - [Logros del Proyecto](#logros-del-proyecto)
    - [Aprendizajes Clave](#aprendizajes-clave)
    - [Trabajo Futuro](#trabajo-futuro)
9. [Colección de Postman para Pruebas](#colección-de-postman-para-pruebas)
10. [Apéndices](#apéndices)

---

## Introducción

### Contexto
En el contexto académico actual, existe una creciente necesidad de plataformas educativas personalizadas que faciliten la creación, organización y seguimiento de cursos, así como la interacción entre estudiantes e instructores. UTEC++ surge como una solución a esta necesidad, facilitando la gestión académica a través de una plataforma web robusta.

### Objetivos del Proyecto
- Permitir a instructores crear y administrar cursos, lecciones y anuncios.
- Facilitar a los estudiantes su inscripción, visualización de materiales y realización de quizzes.
- Gestionar evaluaciones, calificaciones y envíos de forma automática.
- Fomentar la comunicación efectiva entre participantes del curso.

---

## Identificación del Problema o Necesidad

### Descripción del Problema
Los sistemas tradicionales de gestión académica carecen de flexibilidad y personalización. Muchos docentes tienen dificultades para distribuir materiales, aplicar evaluaciones automatizadas y comunicarse con sus estudiantes eficientemente.

### Justificación
UTEC++ permite centralizar todas estas funciones en una plataforma moderna y adaptable, reduciendo la carga operativa del docente y mejorando la experiencia del estudiante.

---

## Descripción de la Solución

### Funcionalidades Implementadas

- **Gestión de Usuarios**: Registro y autenticación de estudiantes e instructores, con roles diferenciados.
- **Cursos y Lecciones**: Creación de cursos, subida de materiales, y organización de lecciones por parte de instructores.
- **Inscripción y Seguimiento**: Estudiantes pueden inscribirse a cursos y hacer seguimiento de su progreso.
- **Evaluaciones y Quizzes**: Creación de quizzes con preguntas de opción múltiple, autoevaluación, y almacenamiento de intentos.
- **Anuncios**: Los instructores pueden publicar anuncios visibles para los estudiantes inscritos, los cuales también se envían por correo.
- **Notificaciones por Correo**: Se integra un sistema de notificación automática para anuncios nuevos.

### Tecnologías Utilizadas

- **Backend**: Java + Spring Boot
- **Base de Datos**: PostgreSQL
- **Frontend (futuro)**: React (planificado)
- **Seguridad**: Spring Security con JWT
- **Envío de correos**: Spring Mail + Gmail SMTP
- **Mapeo de Objetos**: ModelMapper
- **Gestión de eventos**: ApplicationEventPublisher de Spring

---

## Modelo de Entidades

> 📌 A continuación se incluye el modelo de entidades en formato textual. Puede ser acompañado por un diagrama visual.

### Entidades Principales

#### User _(Clase base)_
- `id`, `email`, `password`, `name`, `lastname`, `role`

#### Student _(hereda de User)_
- `enrollments`: Lista de cursos en los que está inscrito
- `quizSubmissions`: Historial de respuestas en quizzes

#### Instructor _(hereda de User)_
- `courses`: Cursos creados

#### Course
- `id`, `title`, `description`, `createdAt`, `instructor`
- Relaciones:
    - `lessons`, `materials`, `announcements`, `enrollments`, `evaluations`

#### Lesson
- `id`, `title`, `content`, `course`

#### Material
- `id`, `title`, `url`, `course`

#### Announcement
- `id`, `title`, `message`, `instructorName`, `createdAt`, `course`

#### Enrollment
- `id`, `student`, `course`, `createdAt`

#### Evaluation _(clase base para quizzes)_
- `id`, `title`, `course`, `questions`

#### Quiz _(hereda de Evaluation)_

#### Question
- `id`, `questionText`, `options`, `correctAnswer`, `evaluation`

#### QuizSubmission
- `id`, `student`, `quiz`, `answers`, `score`, `submittedAt`

---

## Testing y Manejo de Errores

### Niveles de Testing Realizados

Para garantizar la calidad y estabilidad del sistema, se implementaron diferentes niveles de pruebas automatizadas:

- **Pruebas Unitarias:** Validación de la lógica de negocio en servicios y componentes individuales usando JUnit 5 y Mockito. Se simulan dependencias para aislar y verificar comportamientos específicos.

- **Pruebas de Integración:** Verificación de la interacción entre componentes, principalmente controladores REST y servicios con base de datos real, usando Testcontainers para PostgreSQL.

- **Pruebas Funcionales / de Controlador:** Simulación de peticiones HTTP con MockMvc para validar rutas, permisos y formatos de datos en los endpoints REST.

- **Pruebas de Seguridad:** Evaluación de roles y permisos con Spring Security usando `@WithMockUser`, asegurando el acceso controlado a recursos.

### Resultados

- Cobertura adecuada de casos CRUD, validaciones de acceso y manejo de errores.
- Corrección de errores en validaciones y manejo de excepciones.
- Optimización del mapeo JPA y relaciones en la base de datos para evitar inconsistencias.
- Tests exitosos que garantizan la funcionalidad y seguridad del sistema.

### Manejo de Errores

- Uso de excepciones personalizadas (`ResourceNotFoundException`, `AccessDeniedException`, etc.) para clasificar errores comunes.
- Implementación de controladores globales de excepción con `@ControllerAdvice` para responder con mensajes claros y códigos HTTP apropiados.
- Manejo centralizado que evita duplicación de código y mejora la experiencia del usuario.
- Protección del sistema al no exponer detalles internos en respuestas de error.

## Medidas de Seguridad Implementadas

### Seguridad de Datos

- **Autenticación con JWT (JSON Web Tokens):**  
  Se utiliza JWT para la autenticación segura de usuarios. Los tokens se generan con un secreto HMAC256, incluyen información codificada del usuario (email, rol), y tienen un tiempo de expiración configurado para limitar la vigencia.  
  El `JwtAuthenticationFilter` intercepta cada petición, valida el token, y establece el contexto de seguridad para asegurar que sólo usuarios autenticados accedan a los recursos protegidos.

- **Control de Acceso basado en Roles:**  
  Spring Security se configura con una jerarquía de roles (`ADMIN > INSTRUCTOR > STUDENT`), lo que permite definir permisos precisos en cada endpoint mediante anotaciones `@PreAuthorize`.  
  La seguridad de método se habilita para validar roles a nivel de servicios y controladores.

- **Cifrado de Contraseñas:**  
  Se emplea `BCryptPasswordEncoder` para almacenar las contraseñas de forma segura, evitando guardar contraseñas en texto plano.

- **Configuración Stateless:**  
  La aplicación está configurada para ser stateless, deshabilitando sesiones HTTP tradicionales (`SessionCreationPolicy.STATELESS`), lo que reduce la superficie de ataque.

### Prevención de Vulnerabilidades

- **Desactivación de CSRF para APIs REST:**  
  Dado que el sistema usa tokens JWT para autenticación y no cookies, CSRF está deshabilitado (`http.csrf().disable()`) para evitar conflictos, pero con protección activa en el filtro JWT.

- **CORS configurado estrictamente:**  
  Se permiten solicitudes sólo de orígenes definidos con métodos específicos (`GET`, `POST`, `PUT`, `DELETE`, etc.), reduciendo el riesgo de ataques desde dominios no autorizados.

- **Validación y Manejo de Tokens:**  
  El filtro JWT maneja excepciones de verificación para tokens inválidos o expirados, devolviendo códigos HTTP 401 sin exponer detalles internos.

- **Roles y permisos gestionados centralizadamente:**  
  La jerarquía y permisos se manejan en una configuración central, evitando inconsistencias o puntos vulnerables.

- **Protección ante Inyección SQL y XSS:**  
  Aunque no visible en esta capa, al usar JPA con parámetros enlazados y validaciones en DTOs se mitigan riesgos de inyección SQL. Además, la respuesta JSON es generada de manera segura para evitar vulnerabilidades XSS.

---

Estas medidas combinadas aseguran que el sistema controle estrictamente el acceso, mantenga la confidencialidad e integridad de los datos, y prevenga ataques comunes en aplicaciones web modernas.

## Eventos y Asincronía

### Eventos Utilizados

En el proyecto se implementaron eventos personalizados que permiten desacoplar procesos y mejorar la arquitectura de la aplicación. Los eventos principales son:

- **UserRegisterEvent:**  
  Se dispara cuando un nuevo usuario se registra. Contiene datos relevantes como nombre, apellido, email y contraseña. Este evento es escuchado por `UserRegisterEventListener` para enviar un correo de bienvenida al usuario registrado.

- **AnnouncementCreatedEvent:**  
  Se dispara cuando se crea un nuevo anuncio en un curso. Incluye información sobre el instructor, título del curso, destinatarios y el mensaje del anuncio. El `AnnouncementEventListener` escucha este evento y se encarga de enviar emails a todos los destinatarios notificados.

### Importancia de la Implementación de Eventos

- **Desacoplamiento:**  
  Los eventos permiten separar la lógica principal del negocio (por ejemplo, creación de usuario o anuncio) de las tareas adicionales que deben ejecutarse a partir de estos hechos (como enviar emails). Esto facilita mantenimiento, pruebas y escalabilidad.

- **Extensibilidad:**  
  Nuevas funcionalidades que reaccionen a eventos pueden agregarse sin modificar el flujo principal, simplemente creando nuevos listeners.

### Asincronía

- Los listeners de eventos están marcados con la anotación `@Async`, lo que indica que su ejecución es asincrónica, es decir, se procesan en hilos separados sin bloquear la ejecución principal.

- **Beneficios de la Asincronía en el Proyecto:**

    - **Mejora de la experiencia de usuario:**  
      Acciones que consumen tiempo, como el envío de correos electrónicos, no bloquean la respuesta al usuario, haciendo que la aplicación sea más rápida y reactiva.

    - **Escalabilidad:**  
      Permite manejar múltiples eventos y tareas simultáneamente, aprovechando mejor los recursos del sistema.

    - **Robustez:**  
      Fallos en la ejecución de un evento (por ejemplo, error en el envío de un correo) no afectan el flujo principal, y pueden manejarse de manera aislada (por ejemplo, lanzando excepciones específicas).

### Servicio de Email

- El envío de emails está gestionado por el `EmailService`, que también utiliza `@Async` para enviar correos en segundo plano.

- Se utilizan plantillas Thymeleaf para personalizar el contenido de los correos, mejorando la comunicación con los usuarios.

---

Este diseño basado en eventos y procesamiento asincrónico mejora la arquitectura, rendimiento y mantenibilidad del sistema, alineándose con buenas prácticas para aplicaciones modernas.

## Conclusión

### Logros del Proyecto
El proyecto UTEC++ ha logrado desarrollar una plataforma educativa integral que facilita la gestión académica personalizada, cubriendo desde la creación y administración de cursos y lecciones hasta la inscripción y evaluación de estudiantes. Se ha implementado un sistema robusto de roles y seguridad que asegura el acceso adecuado a los recursos, y se ha integrado un mecanismo eficiente de notificaciones mediante eventos y procesamiento asincrónico. Esto contribuye a mejorar la experiencia tanto de estudiantes como de instructores, resolviendo las principales dificultades identificadas en sistemas tradicionales.

### Aprendizajes Clave
Durante el desarrollo de UTEC++, se profundizó en conceptos esenciales como la arquitectura basada en eventos, seguridad con JWT, manejo de roles y permisos, y la integración de pruebas automatizadas para garantizar la calidad. Se comprendió la importancia del desacoplamiento y la asincronía para construir aplicaciones escalables y mantenibles. Además, se consolidó el uso de herramientas modernas de desarrollo como Spring Boot, JPA, Testcontainers, y ModelMapper.

### Trabajo Futuro
Para continuar mejorando el proyecto, se sugiere:
- Implementar un frontend interactivo con React para complementar el backend.
- Añadir funcionalidades avanzadas de análisis de desempeño y reportes.
- Optimizar la gestión de materiales multimedia y evaluaciones más complejas.
- Extender las pruebas automatizadas para incluir pruebas de rendimiento y seguridad.
- Integrar servicios externos para mejorar la entrega de notificaciones y la experiencia del usuario.

---
## 🧪 Cómo usar la colección Postman

1. Abre Postman.
2. Haz clic en el botón **Import** (arriba a la izquierda).
3. Selecciona el archivo [utec-api-collection.json](./postman/UTEC++.postman_collection.json).
4. Una vez importado, abre una petición y presiona **Send** para probarla.

Asegúrate de que el backend esté corriendo en `http://localhost:8080` o ajusta la URL si es necesario.


## Apéndices

### Licencia
Este proyecto está licenciado bajo la **Licencia MIT**.  
Puedes consultar los términos completos de la licencia en el archivo [LICENSE](LICENSE).

### Referencias
- Documentación oficial de [Spring Boot](https://spring.io/projects/spring-boot)
- [JWT (JSON Web Tokens)](https://jwt.io/)
- [Testcontainers](https://www.testcontainers.org/)
- [Thymeleaf](https://www.thymeleaf.org/)
- [ModelMapper](http://modelmapper.org/)
- Material de curso CS 2031 - Desarrollo Basado en Plataforma, Universidad Tecnológica del Perú (UTEC)
