package com.gumthala.learningapp.data.repo

import com.gumthala.learningapp.core.PasswordHasher
import com.gumthala.learningapp.core.UserRole
import com.gumthala.learningapp.core.normalizedEmail
import com.gumthala.learningapp.core.normalizedName
import com.gumthala.learningapp.data.local.UserDao
import com.gumthala.learningapp.data.local.UserEntity
import com.gumthala.learningapp.data.session.Session
import com.gumthala.learningapp.data.session.SessionManager
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

sealed interface AuthResult {
    data class Success(val user: UserEntity) : AuthResult
    data object StudentNotRegistered : AuthResult
    data object InvalidCredentials : AuthResult
    data object AccountDisabled : AuthResult
    data class Error(val message: String) : AuthResult
}

sealed interface RegisterResult {
    data class Success(val user: UserEntity) : RegisterResult
    data object EmailAlreadyUsed : RegisterResult
    data object StudentAlreadyExists : RegisterResult
    data object NotPermitted : RegisterResult
    data class Error(val message: String) : RegisterResult
}

@Singleton
class AuthRepository @Inject constructor(
    private val userDao: UserDao,
    private val session: SessionManager
) {

    val currentSession: Flow<Session?> = session.session

    /**
     * Student sign-in: name + class only, no password. Succeeds only if a teacher
     * or admin has already registered this student — there is no self-signup path.
     */
    suspend fun signInStudent(name: String, classLevel: Int): AuthResult {
        val student = userDao.findStudent(name.normalizedName(), classLevel)
            ?: return AuthResult.StudentNotRegistered
        session.save(
            Session(student.id, UserRole.STUDENT, student.fullName, student.classLevel)
        )
        return AuthResult.Success(student)
    }

    /** Teacher/Admin sign-in with email + password, verified locally. */
    suspend fun signInStaff(email: String, password: String): AuthResult {
        val user = userDao.findStaffByEmail(email.normalizedEmail())
            ?: return AuthResult.InvalidCredentials
        if (!user.isActive) return AuthResult.AccountDisabled
        val salt = user.passwordSalt
        val hash = user.passwordHash
        if (salt == null || hash == null) return AuthResult.InvalidCredentials
        if (!PasswordHasher.verify(password, salt, hash)) return AuthResult.InvalidCredentials
        session.save(Session(user.id, user.role, user.fullName, null))
        return AuthResult.Success(user)
    }

    suspend fun logout() = session.clear()

    /** Teachers may only register into classes assigned to them; admins into any class. */
    suspend fun registerStudent(
        actorId: String,
        fullName: String,
        classLevel: Int,
        rollNo: String? = null,
        avatarKey: String? = null
    ): RegisterResult {
        val actor = userDao.findById(actorId) ?: return RegisterResult.NotPermitted
        val allowed = when (actor.role) {
            UserRole.ADMIN -> true
            UserRole.TEACHER -> classLevel in actor.assignedClassLevels()
            UserRole.STUDENT -> false
        }
        if (!allowed) return RegisterResult.NotPermitted

        val normalized = fullName.normalizedName()
        if (userDao.findStudent(normalized, classLevel) != null) {
            return RegisterResult.StudentAlreadyExists
        }
        val student = UserEntity(
            id = UUID.randomUUID().toString(),
            role = UserRole.STUDENT,
            fullName = fullName.trim(),
            fullNameNormalized = normalized,
            classLevel = classLevel,
            rollNo = rollNo?.trim()?.takeIf { it.isNotEmpty() },
            avatarKey = avatarKey,
            createdBy = actorId
        )
        userDao.upsert(student)
        return RegisterResult.Success(student)
    }

    /** Admin-only: create a teacher (sub-admin) or another admin. */
    suspend fun registerStaff(
        actorId: String,
        fullName: String,
        email: String,
        password: String,
        role: UserRole,
        assignedClasses: List<Int> = emptyList()
    ): RegisterResult {
        val actor = userDao.findById(actorId) ?: return RegisterResult.NotPermitted
        if (actor.role != UserRole.ADMIN) return RegisterResult.NotPermitted
        if (role == UserRole.STUDENT) return RegisterResult.NotPermitted

        val cleanEmail = email.normalizedEmail()
        if (userDao.findStaffByEmail(cleanEmail) != null) return RegisterResult.EmailAlreadyUsed

        val salt = PasswordHasher.newSalt()
        val user = UserEntity(
            id = UUID.randomUUID().toString(),
            role = role,
            fullName = fullName.trim(),
            fullNameNormalized = fullName.normalizedName(),
            email = cleanEmail,
            passwordHash = PasswordHasher.hash(password, salt),
            passwordSalt = salt,
            assignedClasses = assignedClasses.sorted().joinToString(","),
            createdBy = actorId
        )
        userDao.upsert(user)
        return RegisterResult.Success(user)
    }

    /** Admin resets a staff password (recovery flow is: email support, admin resets).
     *  Forces the recovering staff member through the change-password screen on
     *  their next login, same as the seeded founder admin. */
    suspend fun resetStaffPassword(actorId: String, targetUserId: String, newPassword: String): Boolean {
        val actor = userDao.findById(actorId) ?: return false
        if (actor.role != UserRole.ADMIN) return false
        val salt = PasswordHasher.newSalt()
        userDao.setPassword(targetUserId, PasswordHasher.hash(newPassword, salt), salt, mustChange = true)
        return true
    }

    /** Self-service: a signed-in staff member picks their own new password. Clears mustChangePassword. */
    suspend fun changeOwnPassword(userId: String, newPassword: String): Boolean {
        val user = userDao.findById(userId) ?: return false
        if (user.role == UserRole.STUDENT) return false
        val salt = PasswordHasher.newSalt()
        userDao.setPassword(userId, PasswordHasher.hash(newPassword, salt), salt, mustChange = false)
        return true
    }

    suspend fun setUserActive(actorId: String, targetUserId: String, active: Boolean): Boolean {
        val actor = userDao.findById(actorId) ?: return false
        if (actor.role != UserRole.ADMIN) return false
        userDao.setActive(targetUserId, active)
        return true
    }

    fun observeStudentsInClass(classLevel: Int) = userDao.observeStudentsInClass(classLevel)
    fun observeByRole(role: UserRole) = userDao.observeByRole(role)
    suspend fun user(id: String) = userDao.findById(id)
    fun observeUser(id: String) = userDao.observeById(id)
}

fun UserEntity.assignedClassLevels(): List<Int> =
    assignedClasses.orEmpty()
        .split(',')
        .mapNotNull { it.trim().toIntOrNull() }
