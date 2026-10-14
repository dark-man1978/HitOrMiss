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

Not yet verified:

- Installation and gameplay on real Android hardware.
- Authentic app screenshots.
- F-Droid's own build pipeline and maintainer review.

Publication status: the public repository exists. The GitHub workflow creates the source preview tag after successful CI. F-Droid submission is pending.
The test APK is signed for direct device testing. The repository's Gradle
release remains unsigned for F-Droid to build and sign.
