# Talky - Backend

**Español** · [English](README.en.md)

Este repositorio contiene la parte backend de **Talky**, construida con **Java**, **Spring Boot** y **PostgreSQL**.
Su función principal es manejar la lógica de negocio del asistente, incluida la persistencia de usuarios, conversaciones
y contenido académico, además de centralizar la seguridad (Cognito + Spring Security) y las integraciones externas (n8n, OpenAI).
También ofrece mecanismos avanzados como el control de abuso y las migraciones versionadas.

---

## Arquitectura y tecnologías

- **Java 17+**
- **Spring Boot** (API REST)
- **Spring Security con OAuth2 Resource Server** (validación de JWT de Cognito)
- **Spring Data JPA** (gestión de entidades y repositorios)
- **PostgreSQL** como base de datos principal
- **Flyway** para el control de versiones y las migraciones
- **Bucket4j** para limitar la tasa de peticiones (prevención de abuso)
- **WireMock** en las pruebas para simular n8n/OpenAI

---

## Entidades principales

### Usuarios y sesiones
- **User** → información básica del usuario (sub de Cognito, correo, nombre, rol)
- **Conversation** → hilo de conversación asociado a un usuario
- **Message** → mensajes dentro de una conversación, con soporte para metadatos (JSONB)

### Contenido académico
- **GlossaryTerm** → términos del glosario y sus definiciones
- **Lesson** → lecciones organizadas por nivel y orden
- **Exam** → exámenes asociados a una lección
- **Question** → preguntas del examen (las opciones se guardan como JSONB)
- **UserExamResult** → resultados de los exámenes de un usuario

---

## Endpoints principales

- `POST /api/v1/conversations` → crea una conversación
- `GET /api/v1/conversations` → lista las conversaciones del usuario
- `GET /api/v1/conversations/{id}` → detalle de una conversación
- `POST /api/v1/conversations/{id}/messages` → envía un mensaje y recibe una respuesta
- `GET /api/v1/glossary` → lista los términos del glosario
- `GET /api/v1/lessons` → lista las lecciones
- `GET /api/v1/exams/{id}` → detalle de un examen con sus preguntas
- `POST /api/v1/exams/{id}/submit` → registra el resultado de un examen

---

## Integraciones externas

- **AWS Cognito**: gestión de usuarios y validación de tokens JWT en el backend.
- **n8n**: orquestación de flujos; la información se recibe y se envía mediante webhooks.
- **OpenAI** (planeado): posible conexión directa para habilitar streaming token por token.

---

## Ejecución del proyecto

Clona el repositorio:

```bash
git clone https://github.com/miguelczz/talky-API.git
cd talky-API
```

Compila y ejecuta con Maven:

```bash
mvn clean install
mvn spring-boot:run
```

El backend arrancará en:

```
http://localhost:8080
```

---

## Notas de seguridad

- La validación de usuarios se hace con **JWT de Cognito** a través de Spring Security.
- Configura las credenciales de la base de datos y de los servicios externos mediante **variables de entorno** o un archivo de configuración seguro.
- Usa un `.gitignore` adecuado para no subir información sensible (llaves, contraseñas, etc.).
