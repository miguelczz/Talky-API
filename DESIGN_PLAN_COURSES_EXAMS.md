# Plan de Diseño: Componentes de Cursos, Lecciones, Exámenes y Calificaciones

## 📊 Estado Actual

### ✅ Lo que ya existe:
- **Modelos (Entities)**: Course, Lesson, Exam, Question, UserExamResult, UserLesson
- **Repositorios**: Todos creados y funcionando
- **Servicios básicos**: CRUD básico implementado
- **Controladores**: Con lógica básica de roles
- **Migración**: V1__init_schema.sql completa con todas las tablas

### ⚠️ Lo que necesita mejoras:
1. **DTOs estructurados** para requests y responses
2. **Validaciones más robustas** en controladores
3. **Endpoints adicionales** para calificaciones
4. **Mejoras en autorización** por roles
5. **Endpoints de actualización** (PUT) faltantes
6. **Estadísticas** para profesores

---

## 🎯 Diseño Propuesto

### 1. **DTOs (Data Transfer Objects)**

#### **Course DTOs**
- `CourseRequestDto`: Para crear/actualizar cursos
  - `title` (String, requerido, max 255)
  - `description` (String, opcional)
  - `teacherId` (UUID, opcional - solo para ADMIN)

- `CourseResponseDto`: Para respuestas
  - Todos los campos de Course
  - `teacherName` (String)
  - `studentsCount` (Integer)
  - `lessonsCount` (Integer)

#### **Lesson DTOs**
- `LessonRequestDto`: Para crear/actualizar lecciones
  - `title` (String, requerido, max 255)
  - `description` (String, opcional)
  - `content` (String, opcional)
  - `courseId` (UUID, requerido)

- `LessonResponseDto`: Para respuestas
  - Todos los campos de Lesson
  - `courseTitle` (String)
  - `examsCount` (Integer)

#### **Exam DTOs**
- `ExamRequestDto`: Para crear/actualizar exámenes
  - `title` (String, requerido, max 255)
  - `description` (String, opcional)
  - `lessonId` (UUID, requerido)

- `ExamResponseDto`: Para respuestas
  - Todos los campos de Exam
  - `lessonTitle` (String)
  - `questionsCount` (Integer)
  - `averageScore` (Double, opcional)

#### **Question DTOs**
- `QuestionRequestDto`: Para crear/actualizar preguntas
  - `text` (String, requerido)
  - `options` (Map<String, String>, requerido)
  - `correctAnswer` (String, requerido)

- `QuestionResponseDto`: Para respuestas
  - Todos los campos de Question
  - `examTitle` (String)

#### **Grade DTOs**
- `GradeResponseDto`: Resultado de examen con información completa
  - `id` (UUID)
  - `studentId` (UUID)
  - `studentName` (String)
  - `studentEmail` (String)
  - `examId` (UUID)
  - `examTitle` (String)
  - `score` (Double)
  - `answers` (Map<String, String>)
  - `submittedAt` (Instant)

- `StudentGradesDto`: Todas las calificaciones de un estudiante
  - `studentId` (UUID)
  - `studentName` (String)
  - `grades` (List<GradeResponseDto>)
  - `averageScore` (Double)

- `CourseStatisticsDto`: Estadísticas de un curso para profesores
  - `courseId` (UUID)
  - `courseTitle` (String)
  - `totalStudents` (Integer)
  - `totalExams` (Integer)
  - `averageScore` (Double)
  - `examStatistics` (List<ExamStatisticsDto>)

- `ExamStatisticsDto`: Estadísticas de un examen
  - `examId` (UUID)
  - `examTitle` (String)
  - `totalSubmissions` (Integer)
  - `averageScore` (Double)
  - `passRate` (Double) // % de aprobados (>= 70)

---

### 2. **Endpoints Propuestos**

#### **CourseController** (`/api/courses`)
- ✅ `POST /` - Crear curso (TEACHER, ADMIN)
- ✅ `GET /` - Listar cursos (filtrado por rol)
- ✅ `GET /{id}` - Obtener curso
- ✅ `DELETE /{id}` - Eliminar curso
- ➕ `PUT /{id}` - **NUEVO**: Actualizar curso
- ➕ `GET /{id}/statistics` - **NUEVO**: Estadísticas del curso (TEACHER, ADMIN)

#### **LessonController** (`/api/lessons`)
- ✅ `POST /` - Crear lección (TEACHER, ADMIN)
- ✅ `GET /` - Listar lecciones
- ✅ `GET /{id}` - Obtener lección
- ✅ `GET /course/{courseId}` - Lecciones por curso
- ✅ `PUT /{id}` - Actualizar lección
- ✅ `DELETE /{id}` - Eliminar lección
- ✅ `POST /{lessonId}/progress` - Actualizar progreso (STUDENT)
- ✅ `GET /{lessonId}/progress` - Obtener progreso (STUDENT)
- ➕ `GET /{id}/statistics` - **NUEVO**: Estadísticas de la lección (TEACHER, ADMIN)

#### **ExamController** (`/api/exams`)
- ✅ `POST /lesson/{lessonId}` - Crear examen
- ✅ `GET /` - Listar exámenes (filtrado por rol)
- ✅ `GET /{id}` - Obtener examen
- ✅ `GET /lesson/{lessonId}` - Exámenes por lección
- ✅ `DELETE /{id}` - Eliminar examen
- ➕ `PUT /{id}` - **NUEVO**: Actualizar examen
- ✅ `POST /{examId}/questions` - Agregar pregunta
- ✅ `GET /{examId}/questions` - Obtener preguntas
- ✅ `GET /questions/{id}` - Obtener pregunta
- ✅ `DELETE /questions/{id}` - Eliminar pregunta
- ➕ `PUT /questions/{id}` - **NUEVO**: Actualizar pregunta
- ✅ `POST /{examId}/submit` - Enviar examen (STUDENT)
- ✅ `GET /{examId}/results` - Resultados del examen (TEACHER, ADMIN)
- ✅ `GET /{examId}/results/{userId}` - Resultado de un estudiante
- ➕ `GET /{examId}/statistics` - **NUEVO**: Estadísticas del examen (TEACHER, ADMIN)

#### **GradeController** (`/api/grades`) - **NUEVO**
- ➕ `GET /student/{studentId}` - Calificaciones de un estudiante (STUDENT, TEACHER, ADMIN)
- ➕ `GET /course/{courseId}` - Calificaciones de un curso (TEACHER, ADMIN)
- ➕ `GET /exam/{examId}` - Calificaciones de un examen (TEACHER, ADMIN)
- ➕ `GET /student/{studentId}/course/{courseId}` - Calificaciones de un estudiante en un curso

---

### 3. **Lógica de Autorización por Roles**

#### **STUDENT (Estudiante)**
- ✅ Ver su curso asignado
- ✅ Ver lecciones de su curso
- ✅ Ver exámenes de su curso
- ✅ Presentar exámenes
- ✅ Ver sus propias calificaciones
- ✅ Actualizar progreso en lecciones
- ❌ No puede crear/editar/eliminar cursos, lecciones o exámenes

#### **TEACHER (Profesor)**
- ✅ Crear/editar/eliminar sus propios cursos
- ✅ Crear/editar/eliminar lecciones de sus cursos
- ✅ Crear/editar/eliminar exámenes de sus lecciones
- ✅ Ver todos los estudiantes de sus cursos
- ✅ Ver calificaciones de sus cursos
- ✅ Ver estadísticas de sus cursos
- ❌ No puede modificar cursos de otros profesores
- ❌ No puede ver calificaciones de otros cursos

#### **ADMIN (Administrador)**
- ✅ Acceso completo a todo
- ✅ Crear/editar/eliminar cualquier curso
- ✅ Asignar profesores a cursos
- ✅ Ver todas las calificaciones
- ✅ Ver estadísticas globales

---

### 4. **Validaciones Propuestas**

#### **Course**
- `title`: Requerido, máximo 255 caracteres
- `teacherId`: Debe existir y ser un TEACHER
- No se puede eliminar un curso con estudiantes asignados (opcional)

#### **Lesson**
- `title`: Requerido, máximo 255 caracteres
- `courseId`: Debe existir
- Profesor solo puede crear lecciones en sus cursos

#### **Exam**
- `title`: Requerido, máximo 255 caracteres
- `lessonId`: Debe existir
- Profesor solo puede crear exámenes en sus lecciones

#### **Question**
- `text`: Requerido
- `options`: Debe tener al menos 2 opciones
- `correctAnswer`: Debe estar en las opciones

#### **UserExamResult**
- Un estudiante solo puede presentar un examen una vez (o permitir reintentos)
- `score`: Calculado automáticamente
- Validar que el estudiante pertenece al curso del examen

---

### 5. **Mejoras en Servicios**

#### **CourseService**
- ➕ `updateCourse(UUID id, CourseRequestDto dto)`: Actualizar curso
- ➕ `getCourseStatistics(UUID courseId)`: Estadísticas del curso
- ➕ `validateTeacherOwnership(UUID courseId, UUID teacherId)`: Validar propiedad

#### **LessonService**
- ➕ `updateLesson(UUID id, LessonRequestDto dto)`: Actualizar lección
- ➕ `validateCourseOwnership(UUID lessonId, UUID teacherId)`: Validar propiedad
- ➕ `getLessonStatistics(UUID lessonId)`: Estadísticas de la lección

#### **ExamService**
- ➕ `updateExam(UUID id, ExamRequestDto dto)`: Actualizar examen
- ➕ `validateLessonOwnership(UUID examId, UUID teacherId)`: Validar propiedad
- ➕ `getExamStatistics(UUID examId)`: Estadísticas del examen

#### **UserExamResultService**
- ➕ `getStudentGrades(UUID studentId)`: Todas las calificaciones de un estudiante
- ➕ `getCourseGrades(UUID courseId)`: Calificaciones de un curso
- ➕ `calculateAverageScore(UUID studentId, UUID courseId)`: Promedio del estudiante
- ➕ `getPassRate(UUID examId)`: Tasa de aprobación

---

### 6. **Estructura de Carpetas para DTOs**

```
dto/
├── course/
│   ├── CourseRequestDto.java
│   └── CourseResponseDto.java
├── lesson/
│   ├── LessonRequestDto.java
│   └── LessonResponseDto.java
├── exam/
│   ├── ExamRequestDto.java
│   ├── ExamResponseDto.java
│   └── ExamStatisticsDto.java
├── question/
│   ├── QuestionRequestDto.java
│   └── QuestionResponseDto.java
└── grade/
    ├── GradeResponseDto.java
    ├── StudentGradesDto.java
    └── CourseStatisticsDto.java
```

---

## 🚀 Plan de Implementación

1. **Fase 1**: Crear todos los DTOs
2. **Fase 2**: Mejorar servicios con validaciones y métodos adicionales
3. **Fase 3**: Actualizar controladores con DTOs y nuevos endpoints
4. **Fase 4**: Crear GradeController
5. **Fase 5**: Agregar endpoints de estadísticas
6. **Fase 6**: Mejorar validaciones y manejo de errores

---

## ✅ Verificación de Migraciones

La migración `V1__init_schema.sql` ya incluye:
- ✅ Tabla `courses`
- ✅ Tabla `lessons`
- ✅ Tabla `exams`
- ✅ Tabla `questions`
- ✅ Tabla `user_exam_results`
- ✅ Tabla `user_lessons`
- ✅ Todas las relaciones y constraints

**No se necesitan migraciones adicionales** para esta implementación.

