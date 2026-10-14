# Validation of HitOrMiss 1.0.1

Checked on 2026-10-14.

- Standard Gradle 8.9 / Android Gradle Plugin 8.7.3 / OpenJDK 17 release build: passed.
- `rulesTest`: passed (countdown progression and minimum, all decisions, ranking and ties, 100 shuffled deck cycles).
- Android release lint: 0 errors; 6 warnings (German text concatenation and a newer manifest attribute).
- F-Droid metadata lint: passed using fdroidserver with the Games category configured.
- F-Droid metadata rewrite: passed; metadata uses canonical formatting.
- APK signature verification: passed, using the existing local development signer for device testing.
- APK contains exactly 20 harmless and 20 armed comic cards.
- Public source package excludes signing keys, local SDK paths, build outputs and caches.

Device feedback received on 2026-10-14:

- The maintainer reports no errors so far during Android device play.
- Authentic gameplay, leaderboard and result screenshots were supplied privately.
- Public screenshot upload is awaiting explicit maintainer permission; no images
  are included in the public repository yet.
- The installed APK version, device model/OS and complete test matrix were not
  explicitly reported. Restart persistence, backgrounding and rotation have
  not been specifically confirmed.

Not yet verified:

- F-Droid's own build pipeline and maintainer review.

Publication status: source published publicly; preview release `v1.0.1` created.
GitHub build, rule tests, Android lint and source-release jobs passed:
https://github.com/dark-man1978/HitOrMiss/actions/runs/38539669806
F-Droid submission is pending.
The test APK is signed for direct device testing. The repository's Gradle
release remains unsigned for F-Droid to build and sign.
