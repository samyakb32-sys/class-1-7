# Class 1 to 7 Learning Course — foundation build

Kotlin + Jetpack Compose, package `com.gumthala.learningapp`. Offline-first, trilingual
(English / मराठी / हिंदी), three roles (Student, Teacher, Admin).

**Status: data layer complete; 15 screens converted to Compose; nothing wired to the data
layer yet.** A second mockup supplied the login/admin/teacher/slides/leaderboard screens —
those 8 are now built and gated behind a Role Select → Login flow in `ui/nav/RootNavHost.kt`.
See "Mockup vs spec" below: the first 7 screens still disagree with the written spec in
several places that haven't been resolved.

---

## What's in here

| Area | Files | Notes |
|---|---|---|
| Build config | `gradle/libs.versions.toml`, `app/build.gradle.kts` | AGP 8.5, Kotlin 2.0, Compose BOM, Room + KSP, Hilt, DataStore, Firestore |
| Core model | `core/Model.kt` | `AppLanguage`, `UserRole`, `LocalizedText`, class levels, support email |
| Credentials | `core/Security.kt` | PBKDF2 salted hashing, constant-time compare, name normalisation |
| Database | `data/local/` | 11 entities, 6 DAOs, relations, leaderboard + progress queries |
| Session | `data/session/SessionManager.kt` | DataStore-backed, survives reboot, cleared only on Logout |
| Repositories | `data/repo/` | Auth, Content, Quiz, Slides |
| Quiz engine | `domain/QuizEngine.kt` | Per-question option shuffling with anti-pattern guard |
| Rewards | `domain/Rewards.kt` | Star thresholds, badge catalogue, Candy Burst intensity tiers |
| Slides | `data/seed/DefaultSlides.kt` | A–Z, tables 1–10, Marathi + English barakhadi — generated, not hand-written |
| Seeding | `data/seed/SeedLoader.kt` | Loads `assets/seed/*.json` on first run; creates founder admin |
| Sync | `data/remote/` | Firestore mirror, cloud-first pull with local fallback, no-op when unconfigured |
| DI | `di/AppModule.kt` | Hilt wiring |
| Theme | `ui/theme/` | Colours, Baloo 2 + Nunito, radii and type scale taken from the mockup |
| Components | `ui/components/` | Tab bar, section header, emoji tile, option grid, bleed header |
| Student screens | `ui/screens/` | Home, Subjects, Lesson, Quiz, Practice, Progress, Profile |
| Auth screens | `ui/screens/auth/` | Role Select, Student Login, Teacher/Admin Login — **wired to AuthRepository** |
| Auth ViewModel | `ui/viewmodel/AuthViewModel.kt` | Real sign-in/sign-out, restores a persisted session on launch |
| Admin screens | `ui/screens/admin/` | Admin Dashboard, Manage Students (also used by Teacher) |
| Teacher screens | `ui/screens/teacher/` | Teacher Dashboard, Teaching Slides |
| Rewards | `ui/screens/rewards/` | Leaderboard (built, not yet linked into a tab) |
| Navigation | `ui/nav/` | `RootNavHost` gates Role Select → Login → Student/Teacher/Admin shell; `AppNavHost` is the student shell (5 tabs + Lesson/Quiz pushed) |

## Key decisions (change any of these if you disagree)

- **Auth is local, not Firebase Auth.** Teacher/admin passwords are PBKDF2-hashed in Room so
  sign-in works with no network. Firestore only mirrors the hash. This is what makes
  "offline-first" actually true for login, not just for content.
- **Students cannot self-signup.** `signInStudent` matches an existing active row on
  normalised name + class. No row, no entry.
- **Teacher scope is enforced in the repository,** not the UI: `registerStudent` rejects a
  class not in the teacher's `assignedClasses`.
- **Option order is never the stored order.** `QuizEngine` reshuffles per question and rejects
  a shuffle that puts the correct answer in the same slot as the previous question.
- **Firebase is optional.** No `google-services.json` → `NoOpRemoteDataSource` → app runs
  fully offline with zero crashes. Add the file and uncomment the plugin line in
  `app/build.gradle.kts` to switch sync on.
- **Founder admin** seeds as `educationfreedigital@gmail.com` / `ChangeMe@123`. The UI must
  force a password change on first sign-in — that guard doesn't exist yet.

## UI conversion notes

- Every colour, radius and font comes from the mockup's stylesheet — `ui/theme/Color.kt`
  mirrors its `:root` block one-to-one.
- **Sizes are scaled 1.25×.** The mockup is drawn inside a 290px phone frame; a real handset
  is 360–412dp wide. Taken literally, an 8px label becomes an unreadable 8sp. Proportions are
  untouched — only the base unit. `MockupScale` in `ui/theme/Dimens.kt` documents this; say
  the word and I'll switch to literal 1.0× values.
- The three bitmaps in the mockup (avatar, "Keep Learning" banner, lesson illustration) were
  extracted to `res/drawable/`. The banner has its headline and CTA baked into the artwork, so
  it can't be translated into Marathi or Hindi as-is — it needs replacing with real layout or
  three localised images.
- **Fixed:** Baloo 2 / Nunito originally loaded via Google's downloadable-fonts provider,
  which referenced `R.array.com_google_android_gms_fonts_certs` — a resource that didn't exist
  anywhere in the project, so the app would not have compiled. It also meant a live network
  call to Play Services on first launch, which fights the offline-first goal outright.
  `theme/Type.kt` now uses `FontFamily.Default` at the same weights/sizes. To get the exact
  typefaces back, bundle the `.ttf` files under `res/font/` and reference them directly —
  don't restore the downloadable-font path.
- Emoji are used as icons exactly as the mockup does. They render identically in all three
  languages, which is a genuine advantage here, but they do look different across OEM skins.

## Mockup vs spec — conflicts to resolve

The mockup is a **student-only** flow and disagrees with the written spec in a few places.
Nothing has been invented to paper over the gaps:

| Mockup shows | Spec says | Currently |
|---|---|---|
| 6 subjects incl. Science + EVS | 4 subjects: Maths, English, Marathi, Hindi | Screens render all 6; seed data has 4 |
| XP points, Coins, Streak | Stars, badges, per-class leaderboard | Screens show XP/Coins/Streak; DB stores stars/badges |
| 6 practice modes (drag & drop, match, blanks, voice) | MCQs with pictures | Screens list all 6; engine supports MCQ only |
| English only | Trilingual | Screens are hard-coded English strings |
| No Candy Burst celebration screen | Required on quiz completion | Not built — no mockup for it yet |
| No student registration form, Manage Teachers, Manage Content / question editor, forced first-login password change | All required | Not built — dashboards link to them but the screens don't exist |

## Login is now real

`ui/viewmodel/AuthViewModel.kt` backs both login screens with `AuthRepository`:

- Student sign-in calls `signInStudent(name, classLevel)` — fails with an on-screen message
  if no matching active student row exists (no self-signup, per spec).
- Staff sign-in calls `signInStaff(email, password)` against the PBKDF2 hash in Room.
- On launch, `AuthViewModel` reads `AuthRepository.currentSession` and skips straight to the
  signed-in user's shell if a session is already persisted — a brief spinner covers that
  check. Logout calls `AuthRepository.logout()` for real.
- Not yet done: the founder-admin forced password change on first login, and the "Not
  registered? Ask your teacher" helper text doesn't yet distinguish "wrong class" from
  "never registered" (both currently show the same message).

## What still needs doing

1. **The still-missing screens.** Candy Burst celebration, student registration form, Manage
   Teachers, Manage Content / question editor, forced first-login password change, Help &
   Support detail, achievements/certificates detail. Need either a mockup or your go-ahead to
   extend the existing design language.
2. **Wiring the remaining screens to the repositories.** Login is now real (see below) —
   Home, Subjects, Lesson, Practice, Quiz, Progress, the dashboards, Manage Students, Teaching
   Slides and Leaderboard still take default/demo state (`RootNavHost.DemoData` and each
   screen's default parameters). Each needs a ViewModel over `ContentRepository` /
   `QuizRepository` / `SlideRepository`.
3. **Trilingual strings.** Screen copy is hard-coded English; it needs to move to
   `strings.xml` with `values-mr` and `values-hi`.
4. **The content corpus.** This is the big one: 4 subjects × 7 classes × 5–7 chapters ×
   10–15 questions ≈ **2,000 questions, each in three languages plus a picture reference**.
   `assets/seed/maths.json` shows the exact format with one worked chapter. This is a
   separate workstream from the app itself — worth deciding whether to generate it subject by
   subject, and who reviews the Marathi/Hindi before it ships to children.
5. **Question images.** `imageRef` paths are wired through but no assets exist yet.
6. **Room migrations.** Version 1 only; add migrations before the first real deployment.
7. **Firestore security rules.** Not written. Students must not be able to read the
   `users` collection (it holds password hashes).

## Build

**Easiest: download a ready-built APK, no local setup needed.**
Every push to `main` triggers GitHub Actions to build a debug APK automatically.
Go to the repo's **Actions** tab -> click the latest **Build debug APK** run ->
scroll to **Artifacts** -> download `class-1-7-debug-apk`. It's a zip containing
the .apk — unzip and install on an Android device (enable "install from unknown
sources" for debug APKs).

You can also trigger a build manually anytime from Actions -> Build debug APK ->
Run workflow, without needing a new commit.

**Locally, via Android Studio or command line:**

```
./gradlew :app:assembleDebug
```

Gradle wrapper files (`gradlew`, `gradlew.bat`, `gradle/wrapper/`) aren't
committed to this repo. The CI workflow generates them on the fly (the ubuntu
runner has a system Gradle install). Locally, run `gradle wrapper --gradle-version 8.9`
once, or just open the folder in Android Studio and let it generate them
automatically on first sync.

