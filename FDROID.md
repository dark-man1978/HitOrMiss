# F-Droid publication status and instructions

## Status

Source prepared for publication to the public GitHub repository; **not yet submitted to F-Droid**.

- Repository: https://github.com/dark-man1978/HitOrMiss
- Application ID: `de.steph.hitormiss`
- Release: `1.0.1`, version code `2`, planned tag `v1.0.1`
- Original code: MIT
- Original artwork: CC0-1.0
- Native Android Java with no third-party runtime libraries
- No network permission, tracking, advertising, purchases or accounts

The public GitHub repository has been created. A GitLab account or
authenticated session is needed to submit a merge request to F-Droid; there is
no connected GitLab publishing capability in this session.

## Before submitting

1. Test an installed APK on a real Android device: all three difficulties,
   harmless timeout, harmless tap, armed tap, missed armed person, stored scores,
   app backgrounding and screen rotation.
2. Add authentic screenshots as described in `SCREENSHOTS.md`.
3. The source is published to the public repository.
4. The GitHub workflow creates the source preview release and tag `v1.0.1` after successful checks. Verify that this release and its checks are complete before submitting.
5. Fork `fdroid/fdroiddata` on GitLab and place the prepared metadata file at
   `metadata/de.steph.hitormiss.yml` in that fork.
6. Run the F-Droid metadata checks and build pipeline; fix any review or build
   failures and submit a merge request titled `New App: HitOrMiss`.

## Prepared metadata

The metadata points at the GitHub URL and proposed release tag. These must
actually exist before submission. If either changes, update all repository
links and the `commit` field before submitting.

The Gradle build produces an unsigned release. The standard F-Droid build
infrastructure signs it; the local development key is not part of the project
and must not be uploaded. The Gradle wrapper JAR is an upstream Apache-2.0
binary and is listed for deletion during F-Droid scanning because F-Droid uses
its own Gradle installation.

## Maintainer note for the merge request

HitOrMiss is a complete offline reaction game with 40 comic portraits, three
difficulties, progressive countdowns and local top-three leaderboards.

The original program code is MIT licensed. Original game artwork is dedicated
under CC0-1.0 to the extent applicable rights exist and are held by the project's
rights holder. AI-generated origin and prompts are documented in
ASSET-LICENSE.md and ARTWORK.md. Children only appear on harmless cards; armed
cards depict adults with guns or combat knives, without blood or injuries.

It uses only the Android platform at runtime and requires neither Google Play
Services nor a proprietary SDK. It has no network permission, ads or tracking.
The interface is in German; German and English store descriptions are provided.

Please review the license documentation, content and standard Gradle release
build. Real-device testing and screenshots will be supplied before submission.

## References

- https://f-droid.org/docs/Inclusion_Policy/
- https://f-droid.org/docs/Submitting_to_F-Droid_Quick_Start_Guide/
- https://gitlab.com/fdroid/fdroiddata
