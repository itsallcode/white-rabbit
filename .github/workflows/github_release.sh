#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

base_dir="$( cd "$(dirname "$0")/../.." >/dev/null 2>&1 ; pwd -P )"
readonly base_dir

cd "$base_dir"
echo "Reading project version from Gradle project at ${base_dir}..."
project_version=$(./gradlew properties --console=plain --quiet | grep "^version:" | awk '{print $2}')
readonly project_version
echo "Read project version '$project_version' from Gradle project"

release_artifacts=$(find build/release-artifacts -type f)
readonly release_artifacts
readonly title="Release $project_version"
readonly tag="v$project_version"
echo "Creating release:"
echo "Git tag  : $tag"
echo "Title    : $title"
echo "Artifacts: $release_artifacts"

# shellcheck disable=SC2086
release_url=$(gh release create --draft --latest --title "$title" --target main "$tag" $release_artifacts)
readonly release_url
echo "Release URL: $release_url"
