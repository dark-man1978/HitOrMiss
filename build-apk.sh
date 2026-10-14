#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
: "${ANDROID_HOME:?Set ANDROID_HOME to an Android SDK with platform 35 and build-tools 35.0.0}"
bt="$ANDROID_HOME/build-tools/35.0.0"
platform="$ANDROID_HOME/platforms/android-35/android.jar"
mkdir -p build/classes build/dex dev-signing
version_code=$(awk '/versionCode / {print $2; exit}' app/build.gradle)
version_name=$(awk -F"'" '/versionName / {print $2; exit}' app/build.gradle)
javac -source 8 -target 8 -classpath "$platform" -d build/classes app/src/main/java/de/steph/hitormiss/*.java
"$bt/aapt2" compile --dir app/src/main/res -o build/resources.zip
sed 's/<manifest /<manifest package="de.steph.hitormiss" /' app/src/main/AndroidManifest.xml > build/AndroidManifest.xml
"$bt/aapt2" link -o build/base.apk -I "$platform" --manifest build/AndroidManifest.xml --min-sdk-version 23 --target-sdk-version 35 --version-code "$version_code" --version-name "$version_name" -A app/src/main/assets build/resources.zip
"$bt/d8" --lib "$platform" --min-api 23 --output build/dex build/classes/de/steph/hitormiss/*.class
cp build/base.apk build/unsigned.apk
(cd build/dex && zip -q -j ../unsigned.apk classes*.dex)
"$bt/zipalign" -f -P 16 4 build/unsigned.apk build/aligned.apk
if [ ! -f dev-signing/debug.keystore ]; then
    keytool -genkeypair -keystore dev-signing/debug.keystore -storepass android -keypass android -alias androiddebugkey -dname 'CN=HitOrMiss Development' -keyalg RSA -keysize 2048 -validity 10000
fi
"$bt/apksigner" sign --ks dev-signing/debug.keystore --ks-pass pass:android --key-pass pass:android --out build/HitOrMiss.apk build/aligned.apk
"$bt/apksigner" verify --verbose build/HitOrMiss.apk
printf 'Built: %s/build/HitOrMiss.apk\n' "$PWD"
