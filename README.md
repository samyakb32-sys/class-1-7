# Class 1 to 7 Learning Course

Trilingual (English / Marathi / Hindi) offline-first learning & quiz app for a Z.P. Primary
School (Classes 1-7), built with Kotlin + Jetpack Compose.

- Package: `com.gumthala.learningapp`
- Min SDK 24, target/compile SDK 34
- No Hilt/Dagger — a small hand-rolled `AppContainer` (see `di/AppContainer.kt`) wires
  everything up, exposed to Compose via `LocalAppContainer`.

## Architecture

- **Data**: Room (`data/local`) is the source of truth. `data/repository` holds one
  repository per concern (auth, content, quiz, progress, slides, roster), all offline-first —
  reads come straight from Room; writes go to Room first, then best-effort push to Firestore
  (`data/remote/firebase/FirestoreSyncManager.kt`) only when `NetworkUtils.isOnline()`.
- **Session**: `data/session/SessionManager.kt` persists the logged-in user in DataStore until
  an explicit Logout, per spec. Teacher/Admin passwords are PBKDF2-hashed locally
  (`PasswordHasher.kt`) — never sent to Firestore.
- **Quiz engine**: `domain/quiz/QuizEngine.kt` shuffles each question's options independently
  per attempt (no predictable correct-answer position) and computes stars/XP.
- **TTS**: `data/remote/tts/PollinationsTtsClient.kt` calls Pollinations AI's
  `text.pollinations.ai` endpoint and caches audio on disk; `QuestionAudioPlayer.kt` plays it.
- **Content**: `data/seed/ContentSeeder.kt` builds the curriculum on first launch — see
  **Content status** below, this is the part most worth your attention before shipping.

## Content status (read this before demoing)

Chapter titles/structure are curated per subject/class (4 subjects × 7 classes × 5 chapters).
Every question's numbers/vocabulary are **procedurally generated** (`data/seed/
MathQuestionGenerator.kt`, `LanguageQuestionGenerator.kt`) rather than hand-typed, so they're
guaranteed mathematically/factually correct, but this is a first content pass, not a
teacher-reviewed curriculum. Treat it as a working skeleton to review and refine via the
Admin/Teacher question editor (`ContentRepository.saveQuestion`), not final classroom content.

## UI status — screens still need mockups

The HTML mockup provided (`learningappuiexact_1.html`) covers **7 student-flow screens only**:
Home, Subjects, Lesson, Quiz, Practice, Progress, Profile. These were rebuilt in Compose to
match it closely (`ui/student/**`, tokens in `ui/theme/`).

The following screens have **no mockup yet** and were built as plain, unstyled Material3
placeholders (clearly commented `TEMPORARY`/"placeholder pending a mockup" in code) purely so
the app is fully usable end-to-end — please share mockups for these and they'll be redone to
match:

- Role select / Student login / Teacher & Admin login (`ui/auth/`)
- Teacher dashboard (`ui/teacher/TeacherHomeScreen.kt`) and Admin dashboard
  (`ui/admin/AdminHomeScreen.kt`)
- Student/Teacher registration (`ui/roster/`) — without this the student login flow was
  unreachable (nobody could ever be registered to log in as), so it was built even though
  unstyled, rather than left missing
- Teaching Slides: deck browser + arrow-navigation viewer + "add a custom deck" for teachers
  (`ui/slides/`) — custom decks are entered in one language only for now (same text copied into
  all three language fields) until a real translation-authoring flow is designed
- Teacher/Admin quiz question authoring (`ui/content/`): pick class → subject → chapter → add or
  edit a trilingual MCQ
- Leaderboard, Achievements, Help & Support, Language settings

## Testing

`app/src/test/java/com/gumthala/learningapp/` has a JUnit 5 unit test suite covering the
quiz engine, password hashing, and every content generator (`data/seed/`) — run it with:

```
./gradlew testDebugUnitTest
```

For each Maths question template it independently re-derives the correct answer from the
generated question text (regex-parses the numbers back out and recomputes) rather than trusting
the generator's own claimed answer, and every generator is run hundreds of times with different
seeds to shake out edge cases. This isn't just aspirational — this exact suite (run against the
same source files in a standalone Kotlin/JVM harness, since this sandbox can't run the Android
build; see **Setup** below) caught two real bugs before they shipped:

- `comparison()` only ever produced 2 options instead of 4 like every other question type.
- `fractionShaded()` could infinite-loop when it picked a denominator of 2, since "1/2" is the
  only representable fraction at that denominator and no distinct wrong answer could ever be
  found.

Both are fixed; `numericOptions()` (the shared distractor-generation helper) also now enforces a
minimum spread so the same *class* of bug can't recur for a caller not yet imagined.

## Setup

1. **Firebase (optional)**: `app/google-services.json` is a placeholder. Replace it with your
   real Firebase project's file to enable Firestore sync — the app works fully offline without
   it, sync is just skipped silently.
2. **First Admin login**: bootstrapped on first launch — `admin@classapp.local` /
   `Admin@123`. Change it immediately (no "change password" UI yet — update directly via the
   `admins` table or add that screen).
3. Build: `./gradlew assembleDebug` (requires the Android SDK; this container's network policy
   blocks `dl.google.com`, so the Android SDK/AGP/Compose toolchain couldn't be fetched here to
   verify the full build end-to-end — please build locally or in CI, where Google's servers
   aren't blocked, before relying on it). The pure-Kotlin business logic (quiz engine, content
   generators, password hashing) *was* verified — see **Testing** above.

## Known gaps

- Practice screen's 5 non-"Multiple Choice" modes (Drag & Drop, Match the Pair, Fill in the
  Blanks, Image Questions, Voice Questions) are shown per the mockup but not implemented —
  they show a "coming soon" toast.
- No password-change / forgot-password flow yet beyond the Help & Support contact email.
- Admin can't yet delete/deactivate a student or teacher, or edit a teacher's assigned classes
  after registration — only create is wired up.
