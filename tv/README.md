# Maths Adventure TV

A separate Android TV edition of the learning app, for children who are just starting out.
**No login, no accounts, no internet, no permissions.** It opens straight onto the home screen,
works with a normal TV remote (arrows + OK + Back), and Sunny the sun reads every lesson aloud.

The phone app in `app/` is untouched. This is its own module (`:tv`) that builds its own APK,
`MathsAdventureTV-debug.apk`, with its own package name (`com.gumthala.learningapp.tv`), so
both can be installed on the same device.

## What a child sees

1. **Home**: one big "Continue" button that always picks the next lesson, plus Maths Map,
   Quick Practice, My Stars and More Subjects.
2. **Maths Map**: nine worlds, from counting to Class 7.
3. **Each lesson**: Sunny **teaches it step by step** with pictures that appear as they are
   spoken (counters, number lines, blocks, balance scales, bar graphs, clocks, shapes...).
   Then **practice**: eight questions, four big answer buttons, no timer, no penalty.
   - First wrong answer: a hint. Second wrong answer (or "Show me how"): the worked solution plays.
   - Stars are generous: 3 stars at 75% or more, 2 at 50% or more, and finishing always earns a star.
   - Practice is endless: every round is freshly generated, never repeating a question inside a round.

### The pathway (127 lessons)

| # | World | Level | Lessons |
|---|---|---|---|
| 1 | Counting Garden | Start here | 10 |
| 2 | Add & Take-away Forest | Class 1 | 12 |
| 3 | Big Numbers Town | Class 2 | 12 |
| 4 | Times-Table Mountain | Class 2 to 4 | 22 |
| 5 | Shape & Measure Bay | Class 1 to 4 | 12 |
| 6 | Fraction Bakery | Class 3 to 6 | 19 |
| 7 | Number Castle | Class 4 to 7 | 15 |
| 8 | Geometry Galaxy | Class 4 to 7 | 12 |
| 9 | Algebra & Data Planet | Class 6 to 7 | 13 |

*More Subjects* has English, Hindi and Marathi quizzes (plus the phone app's Maths bank) for **Classes 1 to 7**.
Every class has its **own five chapters**: for example Hindi Class 2 is groups, plurals, gender and numbers, Class 5 is
proverbs, one-word-for-many and punctuation, and Class 7 is alankar, harder idioms and tatsam/tadbhav words. Each
question can be shown in English, Marathi or Hindi. The banks are generated from hand-written word lists by
`content-tools/tvseed/` (see below) and bundled in `src/main/assets/seed/`.

## Installing on a TV

The TV needs to run **Android TV or Google TV** (Sony, TCL, Xiaomi Mi TV, Philips, Hisense,
Nvidia Shield, Mi Box, Chromecast with Google TV...) or **Fire TV** (Fire Stick, Fire TV Cube).
Android 5.0 or newer.

> **Not supported:** Samsung (Tizen), LG (webOS), Roku and Apple TV run different operating
> systems and cannot install an Android APK. A phone or tablet with an HDMI cable, or a cheap
> Android TV box, works for those TVs.

### Get the APK

The APK is built by GitHub Actions (no Android Studio needed):

1. Open the repository's **Actions** tab, choose **Build debug APKs**, press **Run workflow**
   and pick the branch. (It also runs on every push to `main`.)
2. When the run is green, open it and download the artifact **`maths-adventure-tv-apk`**
   (a zip). Unzip it: inside is **`MathsAdventureTV-debug.apk`**.

The same run also runs the content test-suite (see below), so a green run means the lessons
were checked.

### Option A: USB stick (no computer needed at the TV)

1. Copy `MathsAdventureTV-debug.apk` onto a USB stick (FAT32 or exFAT, in the top folder).
2. Plug the stick into the TV's USB port.
3. On the TV, install a file manager if it doesn't have one (for example **File Commander**,
   **X-plore** or **Solid Explorer** from the TV's app store; Fire TV: **Downloader** or **ES File Explorer**).
4. Open the file manager, browse to the USB drive and select the `.apk`.
5. The first time, the TV will ask to allow installing from this source: choose **Settings** and
   switch **Allow from this source** on, press Back, then **Install**.
6. Open **Maths Adventure TV** from the app row (or **Apps** if it isn't on the home row).

On **Fire TV** the path to allow this is *Settings > My Fire TV > Developer options > Install
unknown apps* (enable the file manager / Downloader).

### Option B: adb (from a computer on the same network, or over USB for boxes that support it)

```sh
adb connect <tv-ip-address>:5555      # TV: Settings > Developer options > Network/USB debugging
adb install -r MathsAdventureTV-debug.apk
adb shell monkey -p com.gumthala.learningapp.tv 1     # optional: launch it
```

On a device connected by a USB cable (some TV boxes and tablets), skip the `adb connect` line.

### Trying it without a TV

The same APK installs on any Android phone or tablet (it asks for no touch screen and shows up
in the normal launcher too), so you can check it on a tablet first. Use a keyboard's arrow keys
+ Enter for the remote.

## Good to know

- **Works fully offline.** The app has no internet permission at all and never makes a network call; every lesson,
  picture, sound and question bank is inside the APK.
- **Voice:** uses the TV's own text-to-speech, and prefers the best *offline* voice installed (Indian English first)
  so it never waits for a connection. For the most natural sound, install the offline voice data once:
  *TV Settings > Device Preferences > Accessibility (or Language) > Text-to-speech > Google Text-to-speech >
  Install voice data*. If the TV has no voice installed, the app still works; it just shows the words and paces the
  animation itself. *Settings* lets a parent turn
  the voice, the slow voice and the sound effects off.
- **Progress** (stars) is saved on the TV in a tiny private file; "Erase all stars" in Settings
  clears it.
- **Language:** the maths pathway is English for now. The narration is written in short, plain
  sentences so it is easy to translate; Hindi and Marathi narration should be reviewed by a native
  speaker before it is added.
- **Screen sizes:** the layout is drawn once on a 960dp-wide canvas and scaled, so it looks the same
  on a 720p, 1080p or 4K TV, and text is sized to be readable from a sofa.
- **Debug build:** CI produces a debug-signed APK, which is fine for sideloading. A store release
  would need a proper signing key.

## For developers

```
tv/src/main/java/com/gumthala/learningapp/tv/
  content/    the whole curriculum as pure Kotlin data + question generators (no Android)
  core/       small interfaces (narrator, sounds, storage) + the seed-question parser
  platform/   Android implementations (TextToSpeech, synthesised sounds, SharedPreferences)
  ui/         Compose: theme, focus-ring components, Sunny, visual renderers, screens
```

Because the curriculum is code, `tv/src/test/.../ContentTest.kt` runs **every lesson and every
question generator with 60 random seeds** and checks: a valid answer key, distinct answer buttons,
no spoilers in the question picture, lengths that fit the screen, no duplicate question in a round,
no zero or negative measurements, counting steps that really speak a number, and no emoji newer
than Unicode 8 (older TV boxes draw those as empty squares).

To rebuild the English / Hindi / Marathi quiz banks after editing `content-tools/tvseed/*.py`:

```sh
python3 content-tools/tvseed/build.py   # writes tv/src/main/assets/seed/*.json and fails on repeated topics or bad answer keys
```

`SeedTest` also fails the build if a chapter title or a question ever appears in two classes.

```sh
./gradlew :tv:testDebugUnitTest      # check the lessons
./gradlew :tv:assembleDebug          # build tv/build/outputs/apk/debug/MathsAdventureTV-debug.apk
```

To add a lesson: write a `lesson(id, title, blurb, icon, teach = [...]) { rnd, i -> question }` in the
matching `content/W*.kt` file, add it to that world's `lessons` list, and run the tests. The `i`
argument (0-7) lets questions start easy and get harder (`band(i)`).
