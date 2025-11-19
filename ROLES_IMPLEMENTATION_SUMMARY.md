# Resumen de Implementación de Roles

## 📝 Para el Chat del Frontend

Este documento resume todo lo implementado en el backend para la separación de roles. Puedes copiar este contenido completo y pasarlo al chat del proyecto frontend.

---

## 🎯 Sistema de Roles Implementado

### Roles Disponibles
- **STUDENT** (Estudiante)
- **TEACHER** (Profesor)  
- **ADMIN** (Administrador)

### Funcionalidades Principales

1. **Autenticación y Autorización**
   - Los roles se obtienen desde la base de datos (no de Cognito groups)
   - Cada request valida el rol del usuario
   - Los datos se filtran automáticamente según el rol

2. **Endpoints Específicos por Rol**
   - `/api/student/**` - Solo para estudiantes
   - `/api/teacher/**` - Solo para profesores
   - `/api/admin/**` - Solo para administradores

3. **Filtrado Automático de Datos**
   - Estudiantes: solo ven su curso y exámenes relacionados
   - Profesores: solo ven sus cursos y exámenes
   - Administradores: ven todo

4. **Gestión de Roles**
   - Solo administradores pueden cambiar roles
   - Validaciones automáticas al cambiar roles
   - Endpoints para buscar usuarios por rol

---

## 🔑 Endpoint Principal

### `GET /api/auth/me`

**Respuesta:**
```json
{
  "id": "uuid",
  "email": "usuario@example.com",
  "name": "Nombre Usuario",
  "role": "STUDENT",  // "STUDENT" | "TEACHER" | "ADMIN"
  "courseId": "uuid",        // Solo estudiantes
  "courseTitle": "Curso 1",  // Solo estudiantes
  "coursesCount": 3          // Solo profesores
}
```

**Uso:**
```typescript
const response = await fetch('/api/auth/me', {
  headers: { 'Authorization': `Bearer ${token}` }
});
const user = await response.json();
// user.role contiene el rol del usuario
```

---

## 📚 Endpoints por Rol

### Estudiantes (`/api/student/**`)
- `GET /api/student/profile` - Perfil
- `GET /api/student/course` - Curso asignado
- `GET /api/student/exams` - Exámenes del curso
- `GET /api/student/exam-results` - Resultados del estudiante
- `GET /api/student/exam-results/{examId}` - Resultado específico

### Profesores (`/api/teacher/**`)
- `GET /api/teacher/profile` - Perfil
- `GET /api/teacher/courses` - Cursos que dicta
- `GET /api/teacher/exams` - Exámenes de sus cursos
- `GET /api/teacher/exams/{examId}/results` - Resultados de examen
- `GET /api/teacher/students` - Estudiantes de sus cursos

### Administradores (`/api/admin/**`)
- `GET /api/admin/profile` - Perfil
- `GET /api/admin/users` - Todos los usuarios
- `GET /api/admin/users/by-role?role=STUDENT` - Usuarios por rol
- `GET /api/admin/users/students-without-course` - Estudiantes sin curso
- `GET /api/admin/users/{id}` - Usuario específico
- `PUT /api/admin/users/{id}/role` - Actualizar rol
- `DELETE /api/admin/users/{id}` - Eliminar usuario
- `GET /api/admin/courses` - Todos los cursos
- `PUT /api/admin/users/{userId}/assign-course/{courseId}` - Asignar curso
- `PUT /api/admin/users/{userId}/remove-course` - Quitar curso

---

## 🔄 Endpoints Generales (Filtrados)

### Cursos (`/api/courses`)
- Estudiantes: solo su curso
- Profesores: solo sus cursos
- Administradores: todos

### Exámenes (`/api/exams`)
- Estudiantes: solo de su curso
- Profesores: solo de sus cursos
- Administradores: todos

---

## 👥 Asignación de Roles

### Actualizar Rol de Usuario

**Endpoint:** `PUT /api/admin/users/{userId}/role`

**Request Body:**
```json
{
  "role": "TEACHER"  // "STUDENT" | "TEACHER" | "ADMIN"
}
```

**Ejemplo:**
```typescript
const updateRole = async (userId: string, newRole: string) => {
  const response = await fetch(`/api/admin/users/${userId}/role`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({ role: newRole })
  });
  return response.json();
};
```

### Obtener Usuarios por Rol

**Endpoint:** `GET /api/admin/users/by-role?role=STUDENT`

```typescript
const getUsersByRole = async (role: string) => {
  const response = await fetch(`/api/admin/users/by-role?role=${role}`, {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  return response.json();
};
```

### Validaciones Automáticas

El backend valida:
- ✅ No se puede asignar el mismo rol
- ✅ Al cambiar de estudiante, se quita el curso
- ✅ No se puede cambiar a estudiante a un profesor con cursos
- ✅ Solo admins pueden cambiar roles

---

## 💡 Implementación Recomendada

### 1. Hook de Autenticación

```typescript
export function useAuth() {
  const [user, setUser] = useState<User | null>(null);
  
  useEffect(() => {
    fetch('/api/auth/me', {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    .then(res => res.json())
    .then(setUser);
  }, []);

  return {
    user,
    isStudent: user?.role === 'STUDENT',
    isTeacher: user?.role === 'TEACHER',
    isAdmin: user?.role === 'ADMIN'
  };
}
```

### 2. Rutas Protegidas

```typescript
<Route
  path="/student/*"
  element={
    <ProtectedRoute allowedRoles={['STUDENT']}>
      <StudentDashboard />
    </ProtectedRoute>
  }
/>
```

### 3. Navegación Condicional

```typescript
{isStudent && <Link to="/student/course">Mi Curso</Link>}
{isTeacher && <Link to="/teacher/courses">Mis Cursos</Link>}
{isAdmin && <Link to="/admin/users">Usuarios</Link>}
```

---

## 📋 Archivos de Referencia

- **FRONTEND_ROLES_GUIDE.md** - Guía completa con ejemplos de código
- **ROLES_IMPLEMENTATION_SUMMARY.md** - Este documento (resumen)

---

## 🚨 Notas Importantes

1. Todos los endpoints requieren autenticación (excepto `/api/auth/ping`)
2. El rol viene en el token JWT y se valida en cada request
3. Los datos se filtran automáticamente en el servidor
4. Siempre verifica el rol antes de mostrar opciones sensibles
5. Usa los endpoints específicos por rol cuando sea posible

---

## 📞 Soporte

Si necesitas más detalles, consulta:
- `FRONTEND_ROLES_GUIDE.md` - Guía completa con ejemplos
- Código del backend en los controladores:
  - `StudentController.java`
  - `TeacherController.java`
  - `AdminController.java`
  - `AuthController.java`

