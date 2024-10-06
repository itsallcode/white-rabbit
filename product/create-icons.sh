#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

# See https://www.codingforentrepreneurs.com/blog/create-icns-icons-for-macos-apps/

base_dir="$( cd "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"
icon="$base_dir/../jfxui/src/main/resources/icon.png"
output_icons="$base_dir/src/main/resources/icon.icns"

iconset_dir="$base_dir/build/icon.iconset"

rm -rf "$iconset_dir"
mkdir -p "$iconset_dir"

sips --resampleHeightWidth 16 16   "$icon" --out "$iconset_dir/icon_16x16.png"
sips --resampleHeightWidth 32 32   "$icon" --out "$iconset_dir/icon_16x16@2x.png"
sips --resampleHeightWidth 32 32   "$icon" --out "$iconset_dir/icon_32x32.png"
sips --resampleHeightWidth 64 64   "$icon" --out "$iconset_dir/icon_32x32@2x.png"
sips --resampleHeightWidth 128 128 "$icon" --out "$iconset_dir/icon_128x128.png"
sips --resampleHeightWidth 256 256 "$icon" --out "$iconset_dir/icon_128x128@2x.png"
sips --resampleHeightWidth 256 256 "$icon" --out "$iconset_dir/icon_256x256.png"
sips --resampleHeightWidth 512 512 "$icon" --out "$iconset_dir/icon_256x256@2x.png"
sips --resampleHeightWidth 512 512 "$icon" --out "$iconset_dir/icon_512x512.png"

iconutil --convert icns --output "$output_icons" "$iconset_dir"
