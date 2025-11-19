# Guía de Implementación de Roles - Frontend

## 📋 Resumen del Sistema de Roles

El backend ahora implementa un sistema completo de separación de roles con tres tipos de usuarios:
- **STUDENT** (Estudiante)
- **TEACHER** (Profesor)
- **ADMIN** (Administrador)

## 🔑 Endpoint Principal: Obtener Usuario Actual

### `GET /api/auth/me`

**Descripción:** Devuelve la información del usuario autenticado incluyendo su rol.

**Respuesta:**
```json
{
  "id": "uuid",
  "email": "usuario@example.com",
  "name": "Nombre Usuario",
  "role": "STUDENT",  // "STUDENT" | "TEACHER" | "ADMIN"
  "phoneNumber": "123456789",
  "birthdate": "2000-01-01",
  "gender": "M",
  "createdAt": "2024-01-01T00:00:00Z",
  "updatedAt": "2024-01-01T00:00:00Z",
  "courseId": "uuid",        // Solo para estudiantes
  "courseTitle": "Curso 1",  // Solo para estudiantes
  "coursesCount": 3          // Solo para profesores
}
```

**Uso en Frontend:**
```typescript
// Ejemplo con fetch
const response = await fetch('/api/auth/me', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});
const user = await response.json();

// Verificar rol
if (user.role === 'STUDENT') {
  // Mostrar vista de estudiante
} else if (user.role === 'TEACHER') {
  // Mostrar vista de profesor
} else if (user.role === 'ADMIN') {
  // Mostrar vista de administrador
}
```

## 🎯 Endpoints Específicos por Rol

### Endpoints para Estudiantes (`/api/student/**`)

Requieren rol `STUDENT` en el token.

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/student/profile` | Obtiene el perfil del estudiante |
| GET | `/api/student/course` | Obtiene el curso asignado |
| GET | `/api/student/exams` | Obtiene todos los exámenes del curso |
| GET | `/api/student/exam-results` | Obtiene todos los resultados del estudiante |
| GET | `/api/student/exam-results/{examId}` | Obtiene resultado de un examen específico |

### Endpoints para Profesores (`/api/teacher/**`)

Requieren rol `TEACHER` en el token.

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/teacher/profile` | Obtiene el perfil del profesor |
| GET | `/api/teacher/courses` | Obtiene los cursos que dicta |
| GET | `/api/teacher/exams` | Obtiene exámenes de sus cursos |
| GET | `/api/teacher/exams/{examId}/results` | Obtiene resultados de un examen |
| GET | `/api/teacher/students` | Obtiene estudiantes de sus cursos |

### Endpoints para Administradores (`/api/admin/**`)

Requieren rol `ADMIN` en el token.

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/admin/profile` | Obtiene el perfil del admin |
| GET | `/api/admin/users` | Obtiene todos los usuarios |
| GET | `/api/admin/users/{id}` | Obtiene un usuario por ID |
| PUT | `/api/admin/users/{id}/role?role=TEACHER` | Actualiza el rol de un usuario |
| DELETE | `/api/admin/users/{id}` | Elimina un usuario |
| GET | `/api/admin/courses` | Obtiene todos los cursos |
| PUT | `/api/admin/users/{userId}/assign-course/{courseId}` | Asigna curso a estudiante |
| PUT | `/api/admin/users/{userId}/remove-course` | Quita curso de estudiante |

## 🔄 Endpoints Generales (Filtrados por Rol)

Estos endpoints filtran automáticamente los datos según el rol del usuario:

### Cursos (`/api/courses`)
- **Estudiantes:** Solo ven su curso asignado
- **Profesores:** Solo ven los cursos que dictan
- **Administradores:** Ven todos los cursos

### Exámenes (`/api/exams`)
- **Estudiantes:** Solo ven exámenes de su curso
- **Profesores:** Solo ven exámenes de sus cursos
- **Administradores:** Ven todos los exámenes

### Resultados de Exámenes (`/api/exams/{examId}/results`)
- **Profesores:** Solo ven resultados de exámenes de sus cursos
- **Administradores:** Ven todos los resultados

## 💡 Implementación Recomendada en Frontend

### 1. Hook/Context para Manejo de Usuario

```typescript
// useAuth.ts o AuthContext.tsx
import { useState, useEffect } from 'react';

interface User {
  id: string;
  email: string;
  name: string;
  role: 'STUDENT' | 'TEACHER' | 'ADMIN';
  courseId?: string;
  courseTitle?: string;
  coursesCount?: number;
}

export function useAuth() {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchUser();
  }, []);

  const fetchUser = async () => {
    try {
      const response = await fetch('/api/auth/me', {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });
      const userData = await response.json();
      setUser(userData);
    } catch (error) {
      console.error('Error fetching user:', error);
    } finally {
      setLoading(false);
    }
  };

  return {
    user,
    loading,
    isStudent: user?.role === 'STUDENT',
    isTeacher: user?.role === 'TEACHER',
    isAdmin: user?.role === 'ADMIN',
    refetch: fetchUser
  };
}
```

### 2. Componente de Rutas Protegidas

```typescript
// ProtectedRoute.tsx
import { Navigate } from 'react-router-dom';
import { useAuth } from './useAuth';

interface ProtectedRouteProps {
  children: React.ReactNode;
  allowedRoles: ('STUDENT' | 'TEACHER' | 'ADMIN')[];
}

export function ProtectedRoute({ children, allowedRoles }: ProtectedRouteProps) {
  const { user, loading } = useAuth();

  if (loading) {
    return <div>Cargando...</div>;
  }

  if (!user) {
    return <Navigate to="/login" />;
  }

  if (!allowedRoles.includes(user.role)) {
    return <Navigate to="/unauthorized" />;
  }

  return <>{children}</>;
}
```

### 3. Uso en Rutas

```typescript
// App.tsx o Router.tsx
import { Routes, Route } from 'react-router-dom';
import { ProtectedRoute } from './ProtectedRoute';
import { StudentDashboard } from './StudentDashboard';
import { TeacherDashboard } from './TeacherDashboard';
import { AdminDashboard } from './AdminDashboard';

function App() {
  return (
    <Routes>
      <Route
        path="/student/*"
        element={
          <ProtectedRoute allowedRoles={['STUDENT']}>
            <StudentDashboard />
          </ProtectedRoute>
        }
      />
      <Route
        path="/teacher/*"
        element={
          <ProtectedRoute allowedRoles={['TEACHER']}>
            <TeacherDashboard />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/*"
        element={
          <ProtectedRoute allowedRoles={['ADMIN']}>
            <AdminDashboard />
          </ProtectedRoute>
        }
      />
    </Routes>
  );
}
```

### 4. Componente de Navegación Condicional

```typescript
// Navigation.tsx
import { useAuth } from './useAuth';

export function Navigation() {
  const { user, isStudent, isTeacher, isAdmin } = useAuth();

  return (
    <nav>
      {isStudent && (
        <>
          <Link to="/student/course">Mi Curso</Link>
          <Link to="/student/exams">Exámenes</Link>
          <Link to="/student/results">Mis Resultados</Link>
        </>
      )}
      {isTeacher && (
        <>
          <Link to="/teacher/courses">Mis Cursos</Link>
          <Link to="/teacher/exams">Exámenes</Link>
          <Link to="/teacher/students">Estudiantes</Link>
        </>
      )}
      {isAdmin && (
        <>
          <Link to="/admin/users">Usuarios</Link>
          <Link to="/admin/courses">Cursos</Link>
          <Link to="/admin/settings">Configuración</Link>
        </>
      )}
    </nav>
  );
}
```

## 🚨 Manejo de Errores

El backend devuelve:
- **401 Unauthorized:** Token inválido o expirado
- **403 Forbidden:** Usuario no tiene permisos para acceder al recurso
- **404 Not Found:** Recurso no encontrado

```typescript
async function fetchWithAuth(url: string) {
  const response = await fetch(url, {
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    }
  });

  if (response.status === 401) {
    // Token expirado, redirigir a login
    window.location.href = '/login';
    return;
  }

  if (response.status === 403) {
    // Sin permisos
    throw new Error('No tienes permisos para acceder a este recurso');
  }

  if (!response.ok) {
    throw new Error('Error al obtener datos');
  }

  return response.json();
}
```

## 📝 Notas Importantes

1. **Todos los endpoints requieren autenticación** excepto `/api/auth/ping`
2. **El rol viene en el token JWT** y se valida en cada request
3. **Los datos se filtran automáticamente** según el rol del usuario
4. **Los endpoints específicos por rol** (`/api/student/*`, `/api/teacher/*`, `/api/admin/*`) requieren el rol correspondiente
5. **El endpoint `/api/auth/me`** es el punto de entrada para obtener el rol del usuario

## 🔐 Seguridad

- El backend valida el rol en cada request
- Los datos se filtran en el servidor, no confíes solo en el frontend
- Siempre verifica el rol antes de mostrar opciones sensibles
- Usa los endpoints específicos por rol cuando sea posible

---

## 👥 Gestión de Roles (Solo Administradores)

### Asignar/Actualizar Rol de un Usuario

#### Opción 1: Con DTO (Recomendado)
```typescript
// PUT /api/admin/users/{userId}/role
const updateUserRole = async (userId: string, newRole: 'STUDENT' | 'TEACHER' | 'ADMIN') => {
  const response = await fetch(`/api/admin/users/${userId}/role`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({
      role: newRole
    })
  });
  
  if (!response.ok) {
    throw new Error('Error al actualizar el rol');
  }
  
  return response.json();
};

// Uso
await updateUserRole('user-uuid', 'TEACHER');
```

#### Opción 2: Con Query Parameter (Alternativa)
```typescript
// PUT /api/admin/users/{userId}/role-simple?role=TEACHER
const updateUserRoleSimple = async (userId: string, role: string) => {
  const response = await fetch(`/api/admin/users/${userId}/role-simple?role=${role}`, {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  return response.json();
};
```

### Obtener Usuarios por Rol

```typescript
// GET /api/admin/users/by-role?role=STUDENT
const getUsersByRole = async (role: 'STUDENT' | 'TEACHER' | 'ADMIN') => {
  const response = await fetch(`/api/admin/users/by-role?role=${role}`, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  return response.json();
};

// Ejemplos
const students = await getUsersByRole('STUDENT');
const teachers = await getUsersByRole('TEACHER');
const admins = await getUsersByRole('ADMIN');
```

### Obtener Estudiantes Sin Curso

```typescript
// GET /api/admin/users/students-without-course
const getStudentsWithoutCourse = async () => {
  const response = await fetch('/api/admin/users/students-without-course', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  return response.json();
};
```

### Validaciones del Sistema

El backend valida automáticamente:
- ✅ No se puede asignar el mismo rol que ya tiene
- ✅ Si un estudiante cambia de rol, se quita su curso asignado
- ✅ No se puede cambiar a estudiante a un profesor que tiene cursos asignados
- ✅ Solo administradores pueden cambiar roles

### Ejemplo Completo: Panel de Administración de Roles

```typescript
// AdminRoleManagement.tsx
import { useState, useEffect } from 'react';
import { useAuth } from './useAuth';

interface User {
  id: string;
  email: string;
  name: string;
  role: 'STUDENT' | 'TEACHER' | 'ADMIN';
}

export function AdminRoleManagement() {
  const { isAdmin } = useAuth();
  const [users, setUsers] = useState<User[]>([]);
  const [filterRole, setFilterRole] = useState<'ALL' | 'STUDENT' | 'TEACHER' | 'ADMIN'>('ALL');

  useEffect(() => {
    if (isAdmin) {
      loadUsers();
    }
  }, [isAdmin, filterRole]);

  const loadUsers = async () => {
    try {
      let url = '/api/admin/users';
      if (filterRole !== 'ALL') {
        url = `/api/admin/users/by-role?role=${filterRole}`;
      }
      
      const response = await fetch(url, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });
      const data = await response.json();
      setUsers(data);
    } catch (error) {
      console.error('Error loading users:', error);
    }
  };

  const handleRoleChange = async (userId: string, newRole: 'STUDENT' | 'TEACHER' | 'ADMIN') => {
    try {
      const response = await fetch(`/api/admin/users/${userId}/role`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify({ role: newRole })
      });

      if (!response.ok) {
        const error = await response.json();
        alert(`Error: ${error.message || 'No se pudo actualizar el rol'}`);
        return;
      }

      // Recargar usuarios
      loadUsers();
      alert('Rol actualizado correctamente');
    } catch (error) {
      console.error('Error updating role:', error);
      alert('Error al actualizar el rol');
    }
  };

  if (!isAdmin) {
    return <div>No tienes permisos para acceder a esta sección</div>;
  }

  return (
    <div>
      <h1>Gestión de Roles</h1>
      
      <div>
        <label>Filtrar por rol:</label>
        <select value={filterRole} onChange={(e) => setFilterRole(e.target.value as any)}>
          <option value="ALL">Todos</option>
          <option value="STUDENT">Estudiantes</option>
          <option value="TEACHER">Profesores</option>
          <option value="ADMIN">Administradores</option>
        </select>
      </div>

      <table>
        <thead>
          <tr>
            <th>Nombre</th>
            <th>Email</th>
            <th>Rol Actual</th>
            <th>Cambiar Rol</th>
          </tr>
        </thead>
        <tbody>
          {users.map((user) => (
            <tr key={user.id}>
              <td>{user.name}</td>
              <td>{user.email}</td>
              <td>{user.role}</td>
              <td>
                <select
                  value={user.role}
                  onChange={(e) => handleRoleChange(user.id, e.target.value as any)}
                >
                  <option value="STUDENT">Estudiante</option>
                  <option value="TEACHER">Profesor</option>
                  <option value="ADMIN">Administrador</option>
                </select>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
```

## 📋 Resumen de Endpoints de Gestión de Roles

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/admin/users` | Todos los usuarios |
| GET | `/api/admin/users/by-role?role=STUDENT` | Usuarios por rol |
| GET | `/api/admin/users/students-without-course` | Estudiantes sin curso |
| GET | `/api/admin/users/{id}` | Usuario específico |
| PUT | `/api/admin/users/{id}/role` | Actualizar rol (con DTO) |
| PUT | `/api/admin/users/{id}/role-simple?role=TEACHER` | Actualizar rol (query param) |

