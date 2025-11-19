# 📚 Documentación de API - Talky Backend

## 🔐 Autenticación

Todos los endpoints requieren autenticación mediante JWT. Incluye el token en el header:

```
Authorization: Bearer <tu_token_jwt>
```

---

## 📋 Índice

1. [Cursos (Courses)](#cursos-courses)
2. [Lecciones (Lessons)](#lecciones-lessons)
3. [Exámenes (Exams)](#exámenes-exams)
4. [Preguntas (Questions)](#preguntas-questions)
5. [Calificaciones (Grades)](#calificaciones-grades)
6. [Progreso de Lecciones](#progreso-de-lecciones)
7. [DTOs](#dtos-data-transfer-objects)

---

## 🎓 Cursos (Courses)

**Base URL:** `/api/courses`

### Crear Curso
**POST** `/api/courses`

**Roles:** `TEACHER`, `ADMIN`

**Request Body:**
```json
{
  "title": "Inglés Básico",
  "description": "Curso de inglés para principiantes",
  "teacherId": "uuid-del-profesor" // Opcional, solo para ADMIN
}
```

**Response (200 OK):**
```json
{
  "id": "uuid",
  "title": "Inglés Básico",
  "description": "Curso de inglés para principiantes",
  "teacherId": "uuid",
  "teacherName": "Profesor Ejemplo",
  "teacherEmail": "profesor@example.com",
  "studentsCount": 0,
  "lessonsCount": 0,
  "createdAt": "2024-01-01T00:00:00Z",
  "updatedAt": "2024-01-01T00:00:00Z"
}
```

### Listar Cursos
**GET** `/api/courses`

**Roles:** `STUDENT`, `TEACHER`, `ADMIN`

**Comportamiento por rol:**
- **STUDENT**: Solo ve su curso asignado
- **TEACHER**: Ve solo los cursos que dicta
- **ADMIN**: Ve todos los cursos

**Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "title": "Inglés Básico",
    "description": "Curso de inglés para principiantes",
    "teacherId": "uuid",
    "teacherName": "Profesor Ejemplo",
    "teacherEmail": "profesor@example.com",
    "studentsCount": 5,
    "lessonsCount": 10,
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z"
  }
]
```

### Obtener Curso por ID
**GET** `/api/courses/{id}`

**Roles:** `STUDENT`, `TEACHER`, `ADMIN`

**Response (200 OK):** Mismo formato que crear curso

### Actualizar Curso
**PUT** `/api/courses/{id}`

**Roles:** `TEACHER`, `ADMIN`

**Request Body:**
```json
{
  "title": "Inglés Básico Actualizado",
  "description": "Nueva descripción"
}
```

**Nota:** Los profesores solo pueden actualizar sus propios cursos.

### Eliminar Curso
**DELETE** `/api/courses/{id}`

**Roles:** `TEACHER`, `ADMIN`

**Nota:** No se puede eliminar un curso con estudiantes asignados.

---

## 📖 Lecciones (Lessons)

**Base URL:** `/api/lessons`

### Crear Lección
**POST** `/api/lessons`

**Roles:** `TEACHER`, `ADMIN`

**Request Body:**
```json
{
  "title": "Presente Simple",
  "description": "Aprende a usar el presente simple",
  "courseId": "uuid-del-curso"
}
```

**Response (200 OK):**
```json
{
  "id": "uuid",
  "title": "Presente Simple",
  "description": "Aprende a usar el presente simple",
  "courseId": "uuid",
  "courseTitle": "Inglés Básico",
  "examsCount": 0,
  "createdAt": "2024-01-01T00:00:00Z",
  "updatedAt": "2024-01-01T00:00:00Z"
}
```

### Listar Lecciones
**GET** `/api/lessons`

**Roles:** `STUDENT`, `TEACHER`, `ADMIN`

**Response (200 OK):** Array de lecciones

### Obtener Lección por ID
**GET** `/api/lessons/{id}`

**Roles:** `STUDENT`, `TEACHER`, `ADMIN`

### Lecciones por Curso
**GET** `/api/lessons/course/{courseId}`

**Roles:** `STUDENT`, `TEACHER`, `ADMIN`

**Response (200 OK):** Array de lecciones del curso

### Actualizar Lección
**PUT** `/api/lessons/{id}`

**Roles:** `TEACHER`, `ADMIN`

**Request Body:**
```json
{
  "title": "Presente Simple Actualizado",
  "description": "Nueva descripción"
}
```

### Eliminar Lección
**DELETE** `/api/lessons/{id}`

**Roles:** `TEACHER`, `ADMIN`

---

## 📝 Exámenes (Exams)

**Base URL:** `/api/exams`

### Crear Examen
**POST** `/api/exams/lesson/{lessonId}`

**Roles:** `TEACHER`, `ADMIN`

**Request Body:**
```json
{
  "title": "Examen de Presente Simple",
  "description": "Evalúa tu conocimiento del presente simple"
}
```

**Response (200 OK):**
```json
{
  "id": "uuid",
  "title": "Examen de Presente Simple",
  "description": "Evalúa tu conocimiento del presente simple",
  "lessonId": "uuid",
  "lessonTitle": "Presente Simple",
  "courseId": "uuid",
  "courseTitle": "Inglés Básico",
  "questionsCount": 0,
  "averageScore": null,
  "createdAt": "2024-01-01T00:00:00Z",
  "updatedAt": "2024-01-01T00:00:00Z"
}
```

### Listar Exámenes
**GET** `/api/exams`

**Roles:** `STUDENT`, `TEACHER`, `ADMIN`

**Comportamiento por rol:**
- **STUDENT**: Solo ve exámenes de su curso
- **TEACHER**: Ve exámenes de sus cursos
- **ADMIN**: Ve todos los exámenes

### Obtener Examen por ID
**GET** `/api/exams/{id}`

**Roles:** `STUDENT`, `TEACHER`, `ADMIN`

### Exámenes por Lección
**GET** `/api/exams/lesson/{lessonId}`

**Roles:** `STUDENT`, `TEACHER`, `ADMIN`

### Actualizar Examen
**PUT** `/api/exams/{id}`

**Roles:** `TEACHER`, `ADMIN`

**Request Body:**
```json
{
  "title": "Examen Actualizado",
  "description": "Nueva descripción"
}
```

### Eliminar Examen
**DELETE** `/api/exams/{id}`

**Roles:** `TEACHER`, `ADMIN`

---

## ❓ Preguntas (Questions)

**Base URL:** `/api/exams/{examId}/questions`

### Agregar Pregunta
**POST** `/api/exams/{examId}/questions`

**Roles:** `TEACHER`, `ADMIN`

**Request Body:**
```json
{
  "text": "¿Cuál es la forma correcta del presente simple para 'he/she/it'?",
  "options": {
    "a": "go",
    "b": "goes",
    "c": "going",
    "d": "went"
  },
  "correctAnswer": "b"
}
```

**Response (200 OK):**
```json
{
  "id": "uuid",
  "text": "¿Cuál es la forma correcta del presente simple para 'he/she/it'?",
  "options": {
    "a": "go",
    "b": "goes",
    "c": "going",
    "d": "went"
  },
  "correctAnswer": "b",
  "examId": "uuid",
  "examTitle": "Examen de Presente Simple"
}
```

### Obtener Preguntas de un Examen
**GET** `/api/exams/{examId}/questions`

**Roles:** `STUDENT`, `TEACHER`, `ADMIN`

**Nota:** Los estudiantes pueden ver las preguntas, pero no las respuestas correctas (esto debe manejarse en el frontend).

**Response (200 OK):** Array de preguntas

### Obtener Pregunta por ID
**GET** `/api/exams/questions/{id}`

**Roles:** `STUDENT`, `TEACHER`, `ADMIN`

### Actualizar Pregunta
**PUT** `/api/exams/questions/{id}`

**Roles:** `TEACHER`, `ADMIN`

**Request Body:**
```json
{
  "text": "Pregunta actualizada",
  "options": {
    "a": "Opción 1",
    "b": "Opción 2"
  },
  "correctAnswer": "a"
}
```

### Eliminar Pregunta
**DELETE** `/api/exams/questions/{id}`

**Roles:** `TEACHER`, `ADMIN`

---

## 📊 Calificaciones (Grades)

**Base URL:** `/api/exams/{examId}`

### Enviar Examen (Submit)
**POST** `/api/exams/{examId}/submit`

**Roles:** `STUDENT`

**Request Body:**
```json
{
  "answers": {
    "uuid-pregunta-1": "a",
    "uuid-pregunta-2": "b",
    "uuid-pregunta-3": "c"
  }
}
```

**Nota:** La clave del objeto `answers` es el ID de la pregunta (UUID como string), y el valor es la opción seleccionada.

**Response (200 OK):**
```json
{
  "id": "uuid",
  "user": {
    "id": "uuid-estudiante",
    "name": "Estudiante Ejemplo"
  },
  "exam": {
    "id": "uuid-examen",
    "title": "Examen de Presente Simple"
  },
  "score": 85.5,
  "answers": "{\"uuid-pregunta-1\":\"a\",\"uuid-pregunta-2\":\"b\"}",
  "submittedAt": "2024-01-15T10:30:00Z"
}
```

### Obtener Resultados de un Examen
**GET** `/api/exams/{examId}/results`

**Roles:** `TEACHER`, `ADMIN`

**Comportamiento:**
- **TEACHER**: Solo ve resultados de exámenes de sus cursos
- **ADMIN**: Ve todos los resultados

**Response (200 OK):** Array de resultados

### Obtener Resultado de un Estudiante
**GET** `/api/exams/{examId}/results/{userId}`

**Roles:** `TEACHER`, `ADMIN`

**Response (200 OK):** Un resultado

---

## 📈 Progreso de Lecciones

**Base URL:** `/api/lessons/{lessonId}`

### Actualizar Progreso
**POST** `/api/lessons/{lessonId}/progress?progress=50`

**Roles:** `STUDENT`

**Query Parameters:**
- `progress`: Integer (0-100)

**Response (200 OK):**
```json
{
  "id": "uuid",
  "user": {
    "id": "uuid-estudiante",
    "name": "Estudiante Ejemplo"
  },
  "lesson": {
    "id": "uuid-leccion",
    "title": "Presente Simple"
  },
  "progress": 50,
  "completed": false,
  "completedAt": null
}
```

**Nota:** Cuando `progress` llega a 100, `completed` se establece en `true` y `completedAt` se guarda automáticamente.

### Obtener Progreso
**GET** `/api/lessons/{lessonId}/progress`

**Roles:** `STUDENT`

**Response (200 OK):** Mismo formato que actualizar progreso

---

## 📋 DTOs (Data Transfer Objects)

### CourseRequestDto
```typescript
interface CourseRequestDto {
  title: string; // Requerido, max 255 caracteres
  description?: string; // Opcional, max 2000 caracteres
  teacherId?: string; // Opcional, solo para ADMIN (UUID)
}
```

### CourseResponseDto
```typescript
interface CourseResponseDto {
  id: string; // UUID
  title: string;
  description?: string;
  teacherId: string; // UUID
  teacherName: string;
  teacherEmail: string;
  studentsCount: number;
  lessonsCount: number;
  createdAt: string; // ISO 8601
  updatedAt: string; // ISO 8601
}
```

### LessonRequestDto
```typescript
interface LessonRequestDto {
  title: string; // Requerido, max 255 caracteres
  description?: string; // Opcional, max 2000 caracteres
  courseId: string; // Requerido (UUID)
}
```

### LessonResponseDto
```typescript
interface LessonResponseDto {
  id: string; // UUID
  title: string;
  description?: string;
  courseId: string; // UUID
  courseTitle: string;
  examsCount: number;
  createdAt: string; // ISO 8601
  updatedAt: string; // ISO 8601
}
```

### ExamRequestDto
```typescript
interface ExamRequestDto {
  title: string; // Requerido, max 255 caracteres
  description?: string; // Opcional, max 2000 caracteres
  lessonId: string; // Requerido (UUID)
}
```

### ExamResponseDto
```typescript
interface ExamResponseDto {
  id: string; // UUID
  title: string;
  description?: string;
  lessonId: string; // UUID
  lessonTitle: string;
  courseId: string; // UUID
  courseTitle: string;
  questionsCount: number;
  averageScore?: number; // Opcional
  createdAt: string; // ISO 8601
  updatedAt: string; // ISO 8601
}
```

### QuestionRequestDto
```typescript
interface QuestionRequestDto {
  text: string; // Requerido, max 2000 caracteres
  options: { // Requerido, mínimo 2 opciones
    [key: string]: string; // Ej: { "a": "Opción 1", "b": "Opción 2" }
  };
  correctAnswer: string; // Requerido, debe estar en las opciones
}
```

### QuestionResponseDto
```typescript
interface QuestionResponseDto {
  id: string; // UUID
  text: string;
  options: {
    [key: string]: string;
  };
  correctAnswer: string;
  examId: string; // UUID
  examTitle: string;
}
```

### ExamResultDto (Para enviar examen)
```typescript
interface ExamResultDto {
  answers: {
    [questionId: string]: string; // questionId es UUID como string, valor es la opción (ej: "a", "b")
  };
}
```

### UserExamResult (Respuesta del examen)
```typescript
interface UserExamResult {
  id: string; // UUID
  user: {
    id: string; // UUID
    name: string;
  };
  exam: {
    id: string; // UUID
    title: string;
  };
  score: number; // 0.0 - 100.0
  answers: string; // JSON string con las respuestas
  submittedAt: string; // ISO 8601
}
```

### UserLesson (Progreso)
```typescript
interface UserLesson {
  id: string; // UUID
  user: {
    id: string; // UUID
    name: string;
  };
  lesson: {
    id: string; // UUID
    title: string;
  };
  progress: number; // 0-100
  completed: boolean;
  completedAt?: string; // ISO 8601, null si no está completado
}
```

---

## ⚠️ Códigos de Estado HTTP

- **200 OK**: Operación exitosa
- **201 Created**: Recurso creado exitosamente
- **400 Bad Request**: Datos inválidos o faltantes
- **401 Unauthorized**: Token inválido o ausente
- **403 Forbidden**: No tienes permisos para esta operación
- **404 Not Found**: Recurso no encontrado
- **500 Internal Server Error**: Error del servidor

---

## 🔒 Reglas de Autorización

### STUDENT (Estudiante)
- ✅ Ver su curso asignado
- ✅ Ver lecciones de su curso
- ✅ Ver exámenes de su curso
- ✅ Ver preguntas de exámenes (sin respuestas correctas)
- ✅ Presentar exámenes
- ✅ Ver sus propias calificaciones
- ✅ Actualizar progreso en lecciones
- ❌ No puede crear/editar/eliminar cursos, lecciones o exámenes

### TEACHER (Profesor)
- ✅ Crear/editar/eliminar sus propios cursos
- ✅ Crear/editar/eliminar lecciones de sus cursos
- ✅ Crear/editar/eliminar exámenes de sus lecciones
- ✅ Ver todos los estudiantes de sus cursos
- ✅ Ver calificaciones de sus cursos
- ❌ No puede modificar cursos de otros profesores
- ❌ No puede ver calificaciones de otros cursos

### ADMIN (Administrador)
- ✅ Acceso completo a todo
- ✅ Crear/editar/eliminar cualquier curso
- ✅ Asignar profesores a cursos
- ✅ Ver todas las calificaciones
- ✅ Ver estadísticas globales

---

## 📝 Notas Importantes

1. **UUIDs**: Todos los IDs son UUIDs (strings de 36 caracteres)

2. **Fechas**: Todas las fechas están en formato ISO 8601 (ej: `2024-01-15T10:30:00Z`)

3. **Calificaciones**: El score va de 0.0 a 100.0. Un score >= 70.0 se considera aprobado.

4. **Progreso**: El progreso de lecciones va de 0 a 100. Cuando llega a 100, se marca como completado automáticamente.

5. **Respuestas de Exámenes**: Las respuestas se envían como un objeto donde:
   - La clave es el ID de la pregunta (UUID como string)
   - El valor es la opción seleccionada (ej: "a", "b", "c", "d")

6. **Validaciones**:
   - Los campos requeridos deben estar presentes
   - Los campos opcionales pueden ser `null` o no estar presentes
   - Los límites de caracteres se validan en el backend

7. **Errores**: Los errores se devuelven en el siguiente formato:
   ```json
   {
     "message": "Mensaje de error descriptivo"
   }
   ```

   Para errores de validación:
   ```json
   {
     "message": "Error de validación",
     "errors": {
       "campo": "Mensaje de error del campo"
     }
   }
   ```

---

## 🚀 Ejemplos de Uso

### Ejemplo: Flujo completo de un estudiante

1. **Obtener su curso:**
   ```http
   GET /api/courses
   Authorization: Bearer <token>
   ```

2. **Ver lecciones del curso:**
   ```http
   GET /api/lessons/course/{courseId}
   Authorization: Bearer <token>
   ```

3. **Ver una lección:**
   ```http
   GET /api/lessons/{lessonId}
   Authorization: Bearer <token>
   ```

4. **Actualizar progreso:**
   ```http
   POST /api/lessons/{lessonId}/progress?progress=50
   Authorization: Bearer <token>
   ```

5. **Ver exámenes de la lección:**
   ```http
   GET /api/exams/lesson/{lessonId}
   Authorization: Bearer <token>
   ```

6. **Ver preguntas del examen:**
   ```http
   GET /api/exams/{examId}/questions
   Authorization: Bearer <token>
   ```

7. **Enviar examen:**
   ```http
   POST /api/exams/{examId}/submit
   Authorization: Bearer <token>
   Content-Type: application/json

   {
     "answers": {
       "uuid-pregunta-1": "a",
       "uuid-pregunta-2": "b"
     }
   }
   ```

### Ejemplo: Flujo de un profesor

1. **Crear curso:**
   ```http
   POST /api/courses
   Authorization: Bearer <token>
   Content-Type: application/json

   {
     "title": "Inglés Básico",
     "description": "Curso para principiantes"
   }
   ```

2. **Crear lección:**
   ```http
   POST /api/lessons
   Authorization: Bearer <token>
   Content-Type: application/json

   {
     "title": "Presente Simple",
     "description": "Aprende el presente simple",
     "courseId": "uuid-del-curso"
   }
   ```

3. **Crear examen:**
   ```http
   POST /api/exams/lesson/{lessonId}
   Authorization: Bearer <token>
   Content-Type: application/json

   {
     "title": "Examen de Presente Simple",
     "description": "Evalúa tu conocimiento"
   }
   ```

4. **Agregar preguntas:**
   ```http
   POST /api/exams/{examId}/questions
   Authorization: Bearer <token>
   Content-Type: application/json

   {
     "text": "¿Cuál es correcto?",
     "options": {
       "a": "I go",
       "b": "I goes"
     },
     "correctAnswer": "a"
   }
   ```

5. **Ver resultados:**
   ```http
   GET /api/exams/{examId}/results
   Authorization: Bearer <token>
   ```

---

## 📞 Soporte

Para más información o problemas, contacta al equipo de backend.

