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
placeholders (clearly commented `TEMPORARY` in code) purely so the app is runnable —
please share mockups for these and they'll be redone to match:

- Role select / Student login / Teacher & Admin login (`ui/auth/`)
- Teacher dashboard (`ui/teacher/TeacherHomeScreen.kt`)
- Admin dashboard (`ui/admin/AdminHomeScreen.kt`)
- Leaderboard, Achievements, Help & Support, Language settings, Teaching Slides
  (Teaching Slides isn't built yet at all — data model exists in `SlideDeckEntity`/
  `SlideEntity` and default decks are seeded, but there's no viewer screen)

## Setup

1. **Firebase (optional)**: `app/google-services.json` is a placeholder. Replace it with your
   real Firebase project's file to enable Firestore sync — the app works fully offline without
   it, sync is just skipped silently.
2. **First Admin login**: bootstrapped on first launch — `admin@classapp.local` /
   `Admin@123`. Change it immediately (no "change password" UI yet — update directly via the
   `admins` table or add that screen).
3. Build: `./gradlew assembleDebug` (requires the Android SDK; this container didn't have one
   available to verify the build compiles end-to-end — please run a build locally / in CI
   before relying on it).

## Known gaps

- Practice screen's 5 non-"Multiple Choice" modes (Drag & Drop, Match the Pair, Fill in the
  Blanks, Image Questions, Voice Questions) are shown per the mockup but not implemented —
  they show a "coming soon" toast.
- Teaching Slides viewer UI (arrow navigation through a deck) isn't built yet.
- No password-change / forgot-password flow yet beyond the Help & Support contact email.
