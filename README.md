# Talky - Backend

Este repositorio contiene la parte del backend de **Talky**, desarrollado con **Java**, **Spring Boot** y **PostgreSQL**. 
Su función principal es gestionar la lógica de negocio del asistente, incluyendo la persistencia de usuarios, conversaciones 
y contenido académico, además de centralizar la seguridad (Cognito + Spring Security) y las integraciones externas (n8n, OpenAI). 
También provee mecanismos avanzados como control de abusos, migraciones versionadas y streaming de respuestas en tiempo real.

---

## Arquitectura y Tecnologías

- **Java 17+**
- **Spring Boot** (REST API)
- **Spring Security con OAuth2 Resource Server** (validación de JWT de Cognito)
- **Spring Data JPA** (manejo de entidades y repositorios)
- **PostgreSQL** como base de datos principal
- **Flyway** para control de versiones y migraciones
- **Bucket4j** para rate limiting (prevención de abusos)
- **WireMock** en pruebas para simular n8n/OpenAI

---

## Entidades principales

### Usuarios y sesiones
- **User** → información básica del usuario (sub de Cognito, email, nombre, rol)
- **Conversation** → hilo de conversación asociado a un usuario
- **Message** → mensajes dentro de una conversación, con soporte de metadatos (JSONB)

### Contenido académico
- **GlossaryTerm** → términos y definiciones del glosario
- **Lesson** → lecciones organizadas por nivel/orden
- **Exam** → exámenes asociados a una lección
- **Question** → preguntas de un examen (opciones en JSONB)
- **UserExamResult** → resultados de los exámenes de un usuario

---

## Endpoints base

- `POST /api/v1/conversations` → crea una conversación
- `GET /api/v1/conversations` → lista conversaciones del usuario
- `GET /api/v1/conversations/{id}` → detalle de una conversación
- `POST /api/v1/conversations/{id}/messages` → envía mensaje y recibe respuesta
- `GET /api/v1/glossary` → lista términos del glosario
- `GET /api/v1/lessons` → lista lecciones
- `GET /api/v1/exams/{id}` → detalle de un examen con preguntas
- `POST /api/v1/exams/{id}/submit` → registrar resultado de un examen

---

## Integraciones externas

- **AWS Cognito**: gestión de usuarios y validación de tokens JWT en el backend.
- **n8n**: orquestación de flujos, se recibe y envía información a través de webhooks.
- **OpenAI** (planeado): posible conexión directa para habilitar streaming token a token.

---

## Ejecución del proyecto

Clonar el repositorio:

```bash
git clone https://github.com/miguelczz/talky-API.git
cd talky-API
```

Compilar y ejecutar con Maven:

```bash
mvn clean install
mvn spring-boot:run
```

El backend se levantará en:

```
http://localhost:8080
```

---

## Notas de seguridad

- La validación de usuarios se realiza con **JWT de Cognito** a través de Spring Security.  
- Configura las credenciales de base de datos y servicios externos mediante **variables de entorno** o en un archivo de configuración seguro.  
- Utiliza un archivo `.gitignore` adecuado para evitar subir información sensible (claves, contraseñas, etc.).  
