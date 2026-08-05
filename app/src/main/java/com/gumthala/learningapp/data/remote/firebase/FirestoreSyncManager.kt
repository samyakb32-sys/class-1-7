package com.gumthala.learningapp.data.remote.firebase

import com.gumthala.learningapp.data.local.entity.QuestionEntity
import com.gumthala.learningapp.data.local.entity.QuizAnswerEntity
import com.gumthala.learningapp.data.local.entity.QuizAttemptEntity
import com.gumthala.learningapp.data.local.entity.StudentEntity
import com.gumthala.learningapp.data.local.entity.TeacherEntity
import com.gumthala.learningapp.domain.model.TrilingualText
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Optional cloud sync layer. Room is always the source of truth; every method here
 * is a best-effort push/pull that repositories only call when the device is online,
 * and whose failure never blocks the offline-first flow.
 */
class FirestoreSyncManager(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private object Collections {
        const val STUDENTS = "students"
        const val TEACHERS = "teachers"
        const val SUBJECTS = "subjects"
        const val CHAPTERS = "chapters"
        const val QUESTIONS = "questions"
        const val ATTEMPTS = "quiz_attempts"
        const val ANSWERS = "answers"
    }

    suspend fun pushStudent(student: StudentEntity) {
        firestore.collection(Collections.STUDENTS).document(student.id)
            .set(
                mapOf(
                    "id" to student.id,
                    "name" to student.name,
                    "classLevel" to student.classLevel,
                    "registeredByUserId" to student.registeredByUserId,
                    "registeredByRole" to student.registeredByRole,
                    "avatarEmoji" to student.avatarEmoji,
                    "createdAtMillis" to student.createdAtMillis
                )
            ).await()
    }

    suspend fun pushTeacher(teacher: TeacherEntity) {
        // Password hash/salt intentionally never leave the device.
        firestore.collection(Collections.TEACHERS).document(teacher.id)
            .set(
                mapOf(
                    "id" to teacher.id,
                    "name" to teacher.name,
                    "email" to teacher.email,
                    "assignedClasses" to teacher.assignedClasses,
                    "createdAtMillis" to teacher.createdAtMillis
                )
            ).await()
    }

    suspend fun pushQuestion(question: QuestionEntity) {
        firestore.collection(Collections.QUESTIONS).document(question.id)
            .set(
                mapOf(
                    "id" to question.id,
                    "chapterId" to question.chapterId,
                    "orderIndex" to question.orderIndex,
                    "textEn" to question.text.en,
                    "textMr" to question.text.mr,
                    "textHi" to question.text.hi,
                    "optionsEn" to question.optionsEn,
                    "optionsMr" to question.optionsMr,
                    "optionsHi" to question.optionsHi,
                    "correctIndex" to question.correctIndex,
                    "difficulty" to question.difficulty,
                    "imageUrl" to question.imageUrl,
                    "createdByTeacherId" to question.createdByTeacherId
                )
            ).await()
    }

    /** Pushes a completed quiz attempt and its answers right after a student finishes a quiz. */
    suspend fun pushQuizAttempt(attempt: QuizAttemptEntity, answers: List<QuizAnswerEntity>) {
        val attemptRef = firestore.collection(Collections.ATTEMPTS).document(attempt.id)
        attemptRef.set(
            mapOf(
                "id" to attempt.id,
                "studentId" to attempt.studentId,
                "chapterId" to attempt.chapterId,
                "startedAtMillis" to attempt.startedAtMillis,
                "completedAtMillis" to attempt.completedAtMillis,
                "correctCount" to attempt.correctCount,
                "totalQuestions" to attempt.totalQuestions,
                "starsEarned" to attempt.starsEarned,
                "xpEarned" to attempt.xpEarned
            )
        ).await()

        val batch = firestore.batch()
        answers.forEach { answer ->
            val ref = attemptRef.collection(Collections.ANSWERS).document(answer.id)
            batch.set(
                ref,
                mapOf(
                    "questionId" to answer.questionId,
                    "selectedCanonicalIndex" to answer.selectedCanonicalIndex,
                    "wasCorrect" to answer.wasCorrect,
                    "timeTakenMillis" to answer.timeTakenMillis
                )
            )
        }
        batch.commit().await()
    }

    /** Cloud-first content pull; callers fall back to whatever's already in Room if this throws. */
    suspend fun pullQuestionsForChapter(chapterId: String): List<QuestionEntity> {
        val snapshot = firestore.collection(Collections.QUESTIONS)
            .whereEqualTo("chapterId", chapterId)
            .get()
            .await()
        return snapshot.documents.mapNotNull { doc ->
            val optionsEn = doc.get("optionsEn") as? List<String> ?: return@mapNotNull null
            val optionsMr = doc.get("optionsMr") as? List<String> ?: optionsEn
            val optionsHi = doc.get("optionsHi") as? List<String> ?: optionsEn
            QuestionEntity(
                id = doc.id,
                chapterId = chapterId,
                orderIndex = (doc.getLong("orderIndex") ?: 0).toInt(),
                text = TrilingualText(
                    en = doc.getString("textEn").orEmpty(),
                    mr = doc.getString("textMr").orEmpty(),
                    hi = doc.getString("textHi").orEmpty()
                ),
                optionsEn = optionsEn,
                optionsMr = optionsMr,
                optionsHi = optionsHi,
                correctIndex = (doc.getLong("correctIndex") ?: 0).toInt(),
                difficulty = doc.getString("difficulty") ?: "medium",
                imageUrl = doc.getString("imageUrl"),
                createdByTeacherId = doc.getString("createdByTeacherId"),
                isSynced = true
            )
        }
    }
}
