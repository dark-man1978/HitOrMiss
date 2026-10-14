# HitOrMiss

**AI-generated Android game: the code and all 40 comic portraits were created with ChatGPT.**
The project was developed from a human-provided game specification and has received automated build, rules and metadata checks. The maintainer reports no errors so far during Android device play.

An offline Android reaction game. Native Java, Android 6.0 (API 23) or newer.
Current version: **1.0.2** (`versionCode 3`). The interface is in German.

## Gameplay

- Tap visibly armed adults before the countdown expires.
- For harmless people, do not tap: let the countdown expire.
- Every correct decision earns one point.
- A wrong tap or missed armed person ends the round.
- Easy starts at 5 seconds, medium at 3 seconds, hard at 1 second.
- Every three correct decisions reduce the time by 10%, to a minimum of 0.3 seconds.
- A 0.35-second feedback interval separates pictures.
- A shuffled deck contains 20 harmless and 20 armed comic portraits.
- Each difficulty has its own locally saved top-three leaderboard.
- Leaving the app aborts the active round without saving a score.

No ads, accounts, analytics, purchases or network permission. Children only
appear on harmless cards. Armed cards contain fictional adults with visible
guns or combat knives, without blood or injuries. Images were AI-generated.

## Build

Requirements: OpenJDK 17, Android SDK Platform 35, Android Build Tools 35.0.0.
The wrapper downloads Gradle 8.9. Android Gradle Plugin is pinned to 8.7.3.

```bash
export ANDROID_HOME="$HOME/Android/Sdk"
./gradlew rulesTest :app:assembleRelease :app:lintRelease
```

The release APK is unsigned at:
`app/build/outputs/apk/release/app-release-unsigned.apk`.

F-Droid builds and signs its own release; do not add a private signing key to
this repository. A normal developer debug build uses the local Android debug
keystore automatically:

```bash
./gradlew :app:assembleDebug
```

Android Studio can open this folder as a Gradle project.

### Alternative build without Gradle

With `javac`, `keytool`, `zip`, Android SDK Platform 35 and Build Tools 35.0.0:

```bash
export ANDROID_HOME="$HOME/Android/Sdk"
./build-apk.sh
```

This creates `build/HitOrMiss.apk` with a local development key in
`dev-signing/debug.keystore`. This directory is excluded from publication.
Development APKs and F-Droid APKs normally have different signers. Android
cannot install one as an update over the other; uninstalling clears local
scores unless backed up separately.

## Validation

`./gradlew rulesTest` checks time progression, the minimum countdown, all four
decision cases, top-three ordering and ties, and 100 balanced shuffled decks.
`./gradlew :app:lintRelease` runs Android lint. The maintainer has reported no errors so far during Android device play;
see `VALIDATION.md` for the scope of that feedback.

## Licenses

- Original code, documentation and scripts: **MIT**, see `LICENSE`.
- Original comic cards and icons: **CC0-1.0**, see `ASSET-LICENSE.md`.
- Gradle wrapper: upstream **Apache-2.0**, see `NOTICE` and `LICENSES/Apache-2.0.txt`.

## F-Droid publication

See `FDROID.md`. `fdroid/metadata/de.steph.hitormiss.yml` is prepared for
submission to fdroiddata, with release tag `v1.0.2`. The public GitHub repository is
https://github.com/dark-man1978/HitOrMiss. F-Droid submission is still pending.
Store descriptions and icons are in `fastlane/metadata/android/`. Authentic
app screenshots are included with system bars removed by an exact pixel crop.
See `SCREENSHOTS.md`.
