package com.gumthala.learningapp.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.gumthala.learningapp.core.LocalizedText
import com.gumthala.learningapp.core.UserRole

/**
 * One table for all three roles.
 *
 * Students: [fullName] + [classLevel], no credentials — they can only sign in if a
 * teacher or admin created their row first (no self-signup).
 * Teachers/Admins: [email] + salted hash. Auth is local so it works with no network;
 * Firestore is only a mirror.
 */
@Entity(
    tableName = "users",
    indices = [
        Index(value = ["email"], unique = true),
        Index(value = ["fullNameNormalized", "classLevel"]),
        Index(value = ["role"])
    ]
)
data class UserEntity(
    @PrimaryKey val id: String,
    val role: UserRole,
    val fullName: String,
    /** lowercased + whitespace-collapsed, used for tolerant student name matching */
    val fullNameNormalized: String,
    val email: String? = null,
    val passwordHash: String? = null,
    val passwordSalt: String? = null,
    /** true right after seeding/reset — forces a change screen before the staff member reaches their dashboard */
    val mustChangePassword: Boolean = false,
    /** students only */
    val classLevel: Int? = null,
    val rollNo: String? = null,
    /** teachers only: comma-separated class levels they are assigned to, e.g. "1,2,5" */
    val assignedClasses: String? = null,
    val avatarKey: String? = null,
    val isActive: Boolean = true,
    val createdBy: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)

@Entity(tableName = "subjects", indices = [Index(value = ["code"], unique = true)])
data class SubjectEntity(
    @PrimaryKey val id: String,
    val code: String,
    @Embedded(prefix = "name_") val name: LocalizedText,
    val iconKey: String,
    val colorHex: String,
    val orderIndex: Int
)

@Entity(
    tableName = "chapters",
    foreignKeys = [ForeignKey(
        entity = SubjectEntity::class,
        parentColumns = ["id"],
        childColumns = ["subjectId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["subjectId", "classLevel", "orderIndex"])]
)
data class ChapterEntity(
    @PrimaryKey val id: String,
    val subjectId: String,
    val classLevel: Int,
    val orderIndex: Int,
    @Embedded(prefix = "title_") val title: LocalizedText,
    @Embedded(prefix = "blurb_") val blurb: LocalizedText = LocalizedText(""),
    val iconKey: String? = null,
    val isPublished: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)

@Entity(
    tableName = "questions",
    foreignKeys = [ForeignKey(
        entity = ChapterEntity::class,
        parentColumns = ["id"],
        childColumns = ["chapterId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["chapterId", "orderIndex"]), Index(value = ["authorUserId"])]
)
data class QuestionEntity(
    @PrimaryKey val id: String,
    val chapterId: String,
    val orderIndex: Int,
    @Embedded(prefix = "prompt_") val prompt: LocalizedText,
    /** asset path or remote URL for the question picture, e.g. "images/maths/apples_3.webp" */
    val imageRef: String? = null,
    @Embedded(prefix = "hint_") val hint: LocalizedText = LocalizedText(""),
    /** 1 = easy .. 5 = stretch. Content is authored ~50% above the traditional grade level. */
    val difficulty: Int = 3,
    /** null for built-in content; set when a teacher authors their own question */
    val authorUserId: String? = null,
    val isActive: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)

@Entity(
    tableName = "options",
    foreignKeys = [ForeignKey(
        entity = QuestionEntity::class,
        parentColumns = ["id"],
        childColumns = ["questionId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["questionId"])]
)
data class OptionEntity(
    @PrimaryKey val id: String,
    val questionId: String,
    @Embedded(prefix = "text_") val text: LocalizedText,
    val imageRef: String? = null,
    val isCorrect: Boolean,
    /** authoring order only — never the display order; see QuizEngine */
    val orderIndex: Int
)

@Entity(
    tableName = "quiz_attempts",
    indices = [Index(value = ["userId", "chapterId"]), Index(value = ["isSynced"])]
)
data class QuizAttemptEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val chapterId: String,
    val startedAt: Long,
    val finishedAt: Long? = null,
    val correctCount: Int = 0,
    val totalCount: Int = 0,
    val starsEarned: Int = 0,
    val isSynced: Boolean = false
)

@Entity(
    tableName = "attempt_answers",
    foreignKeys = [ForeignKey(
        entity = QuizAttemptEntity::class,
        parentColumns = ["id"],
        childColumns = ["attemptId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["attemptId"])]
)
data class AttemptAnswerEntity(
    @PrimaryKey val id: String,
    val attemptId: String,
    val questionId: String,
    val selectedOptionId: String?,
    val isCorrect: Boolean,
    val answeredAt: Long = System.currentTimeMillis()
)

/** Rolled-up best result per student per chapter — what the dashboards read. */
@Entity(tableName = "progress", primaryKeys = ["userId", "chapterId"], indices = [Index(value = ["userId"])])
data class ProgressEntity(
    val userId: String,
    val chapterId: String,
    val bestCorrect: Int = 0,
    val bestTotal: Int = 0,
    val stars: Int = 0,
    val attemptCount: Int = 0,
    val lastAttemptAt: Long = 0L,
    val isSynced: Boolean = false
)

@Entity(tableName = "badges", indices = [Index(value = ["userId", "code"], unique = true)])
data class BadgeEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val code: String,
    val earnedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)

/** A teaching slide deck. Default decks ship with the app; teachers can add their own. */
@Entity(tableName = "slide_decks", indices = [Index(value = ["ownerUserId"]), Index(value = ["category"])])
data class SlideDeckEntity(
    @PrimaryKey val id: String,
    @Embedded(prefix = "title_") val title: LocalizedText,
    /** alphabet | tables | barakhadi | custom */
    val category: String,
    val ownerUserId: String? = null,
    val isDefault: Boolean = false,
    val classLevel: Int? = null,
    val orderIndex: Int = 0,
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)

@Entity(
    tableName = "slides",
    foreignKeys = [ForeignKey(
        entity = SlideDeckEntity::class,
        parentColumns = ["id"],
        childColumns = ["deckId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["deckId", "orderIndex"])]
)
data class SlideEntity(
    @PrimaryKey val id: String,
    val deckId: String,
    val orderIndex: Int,
    /** big centred glyph: "A", "क", "7 × 3" */
    val headline: String,
    /** supporting line: "Apple", "= 21" */
    @Embedded(prefix = "caption_") val caption: LocalizedText = LocalizedText(""),
    val imageRef: String? = null
)
