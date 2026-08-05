package com.gumthala.learningapp.data.repository

import com.gumthala.learningapp.data.local.dao.AdminDao
import com.gumthala.learningapp.data.local.dao.StudentDao
import com.gumthala.learningapp.data.local.dao.TeacherDao
import com.gumthala.learningapp.data.local.entity.AdminEntity
import com.gumthala.learningapp.data.local.entity.StudentEntity
import com.gumthala.learningapp.data.local.entity.TeacherEntity
import com.gumthala.learningapp.data.session.PasswordHasher
import com.gumthala.learningapp.domain.model.Role
import java.util.UUID

sealed interface LoginResult {
    data class Success(val userId: String, val displayName: String, val classLevel: Int? = null) : LoginResult
    data object InvalidCredentials : LoginResult
    data object NotRegistered : LoginResult
}

/**
 * Handles all three login flows. Student sign-in is name+class only and only succeeds
 * for students a Teacher/Admin has already registered — there is no student self-signup.
 */
class AuthRepository(
    private val studentDao: StudentDao,
    private val teacherDao: TeacherDao,
    private val adminDao: AdminDao
) {
    suspend fun studentLogin(name: String, classLevel: Int): LoginResult {
        val student = studentDao.findByNameAndClass(name.trim(), classLevel)
            ?: return LoginResult.NotRegistered
        return LoginResult.Success(student.id, student.name, student.classLevel)
    }

    suspend fun teacherLogin(email: String, password: String): LoginResult {
        val teacher = teacherDao.findByEmail(email.trim().lowercase()) ?: return LoginResult.NotRegistered
        val valid = PasswordHasher.verify(password, teacher.passwordSalt, teacher.passwordHash)
        return if (valid) LoginResult.Success(teacher.id, teacher.name) else LoginResult.InvalidCredentials
    }

    suspend fun adminLogin(email: String, password: String): LoginResult {
        val admin = adminDao.findByEmail(email.trim().lowercase()) ?: return LoginResult.NotRegistered
        val valid = PasswordHasher.verify(password, admin.passwordSalt, admin.passwordHash)
        return if (valid) LoginResult.Success(admin.id, admin.name) else LoginResult.InvalidCredentials
    }

    /** Called by a Teacher (their own classes only, enforced by the calling ViewModel) or an Admin (any class). */
    suspend fun registerStudent(name: String, classLevel: Int, registeredBy: String, registeredByRole: Role): StudentEntity {
        val student = StudentEntity(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            classLevel = classLevel,
            registeredByUserId = registeredBy,
            registeredByRole = registeredByRole.name,
            createdAtMillis = System.currentTimeMillis()
        )
        studentDao.upsert(student)
        return student
    }

    /** Admin-only: registers a new Teacher account. */
    suspend fun registerTeacher(name: String, email: String, password: String, assignedClasses: List<Int>): TeacherEntity {
        val hashed = PasswordHasher.hash(password)
        val teacher = TeacherEntity(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            email = email.trim().lowercase(),
            passwordHash = hashed.hash,
            passwordSalt = hashed.salt,
            assignedClasses = assignedClasses,
            createdAtMillis = System.currentTimeMillis()
        )
        teacherDao.upsert(teacher)
        return teacher
    }

    suspend fun ensureDefaultAdminExists(defaultEmail: String, defaultPassword: String) {
        if (adminDao.count() > 0) return
        val hashed = PasswordHasher.hash(defaultPassword)
        adminDao.upsert(
            AdminEntity(
                id = UUID.randomUUID().toString(),
                name = "School Admin",
                email = defaultEmail.lowercase(),
                passwordHash = hashed.hash,
                passwordSalt = hashed.salt,
                createdAtMillis = System.currentTimeMillis()
            )
        )
    }
}
