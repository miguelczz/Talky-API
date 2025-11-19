package com.talky.backend.service;

import com.talky.backend.dto.UpdateProfileRequest;
import com.talky.backend.dto.UserSyncRequest;
import com.talky.backend.model.Course;
import com.talky.backend.model.User;
import com.talky.backend.repository.CourseRepository;
import com.talky.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public UserService(UserRepository userRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    /**
     * Sincroniza un usuario en la base de datos con la información recibida de Cognito.
     * - Si el usuario ya existe, se actualizan sus datos.
     * - Si no existe, se crea un nuevo usuario.
     */
    @Transactional
    public User syncUser(UserSyncRequest req) {
        Optional<User> existingUserOpt = userRepository.findByCognitoSub(req.getSub());

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            if (req.getEmail() != null) existingUser.setEmail(req.getEmail());
            if (req.getName() != null) existingUser.setName(req.getName());
            if (req.getPhoneNumber() != null) existingUser.setPhoneNumber(req.getPhoneNumber());
            if (req.getBirthdate() != null) existingUser.setBirthdate(req.getBirthdate());
            if (req.getGender() != null) existingUser.setGender(req.getGender());
            if (req.getRole() != null) existingUser.setRole(req.getRole());
            return userRepository.save(existingUser);
        } else {
            User newUser = new User();
            newUser.setCognitoSub(req.getSub());
            newUser.setEmail(req.getEmail());
            newUser.setName(req.getName());
            newUser.setPhoneNumber(req.getPhoneNumber());
            newUser.setBirthdate(req.getBirthdate());
            newUser.setGender(req.getGender());
            newUser.setRole(req.getRole() != null ? req.getRole() : User.Role.STUDENT);
            return userRepository.save(newUser);
        }
    }

    /**
     * Busca un usuario por su Cognito Sub.
     */
    public Optional<User> getByCognitoSub(String sub) {
        return userRepository.findByCognitoSub(sub);
    }

    /**
     * Busca un usuario por su correo electrónico.
     */
    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Obtiene todos los usuarios registrados en la base de datos.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Obtiene un usuario por su identificador único (UUID).
     * Lanza una excepción si el usuario no existe.
     */
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    /**
     * Guarda o actualiza un usuario en la base de datos.
     */
    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * Elimina un usuario por su identificador único.
     */
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        userRepository.deleteById(id);
    }

    /**
     * Actualiza el rol de un usuario.
     * Valida que el cambio de rol sea válido.
     */
    @Transactional
    public User updateUserRole(UUID id, User.Role newRole) {
        User user = getUserById(id);
        
        // Validaciones de cambio de rol
        if (user.getRole() == newRole) {
            throw new RuntimeException("El usuario ya tiene el rol " + newRole);
        }
        
        // Si cambia de estudiante a otro rol, quitar el curso asignado
        if (user.getRole() == User.Role.STUDENT && user.getCourseAsStudent() != null) {
            user.setCourseAsStudent(null);
        }
        
        // Si cambia a estudiante y tiene cursos como profesor, no permitir
        if (newRole == User.Role.STUDENT && user.getCoursesAsTeacher() != null && !user.getCoursesAsTeacher().isEmpty()) {
            throw new RuntimeException("No se puede cambiar a estudiante un usuario que tiene cursos asignados como profesor");
        }
        
        user.setRole(newRole);
        return userRepository.save(user);
    }

    /**
     * Obtiene todos los usuarios con un rol específico.
     */
    public List<User> getUsersByRole(User.Role role) {
        return userRepository.findByRole(role);
    }

    /**
     * Obtiene usuarios que no tienen un curso asignado (estudiantes sin curso).
     */
    public List<User> getStudentsWithoutCourse() {
        return userRepository.findByRoleAndCourseAsStudentIsNull(User.Role.STUDENT);
    }

    /**
     * Asigna un curso a un estudiante.
     */
    @Transactional
    public User assignCourseToStudent(UUID userId, UUID courseId) {
        User user = getUserById(userId);
        if (user.getRole() != User.Role.STUDENT) {
            throw new RuntimeException("Solo los estudiantes pueden ser asignados a un curso");
        }
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
        user.setCourseAsStudent(course);
        return userRepository.save(user);
    }

    /**
     * Quita el curso asignado a un estudiante.
     */
    @Transactional
    public User removeCourseFromStudent(UUID userId) {
        User user = getUserById(userId);
        if (user.getRole() != User.Role.STUDENT) {
            throw new RuntimeException("Solo los estudiantes pueden ser desvinculados de un curso");
        }
        user.setCourseAsStudent(null);
        return userRepository.save(user);
    }

    /**
     * Actualiza el perfil de un usuario.
     * Solo actualiza los campos proporcionados, no modifica email ni rol.
     */
    @Transactional
    public User updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = getUserById(userId);

        // Validar que al menos un campo esté presente
        if (!request.hasAtLeastOneField()) {
            throw new RuntimeException("Debe proporcionar al menos un campo para actualizar");
        }

        // Actualizar solo los campos proporcionados
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            user.setName(request.getName().trim());
        }

        if (request.getPhoneNumber() != null) {
            if (request.getPhoneNumber().trim().isEmpty()) {
                user.setPhoneNumber(null);
            } else {
                user.setPhoneNumber(request.getPhoneNumber().trim());
            }
        }

        if (request.getBirthdate() != null) {
            if (request.getBirthdate().trim().isEmpty()) {
                user.setBirthdate(null);
            } else {
                user.setBirthdate(request.getBirthdate().trim());
            }
        }

        if (request.getGender() != null) {
            String gender = request.getNormalizedGender();
            user.setGender(gender);
        }

        return userRepository.save(user);
    }
}
