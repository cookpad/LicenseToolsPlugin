# DEPRECATED
"LicenseToolsPlugin" is no longer used internally at Cookpad. No more development will be taking place.  As an alternative, we recommend [OSS Licenses Gradle Plugin](https://github.com/google/play-services-plugins/tree/master/oss-licenses-plugin).

Thanks for all your support!


# License Tools Plugin for Android

[![Actions Status](https://github.com/cookpad/LicenseToolsPlugin/workflows/Android%20CI/badge.svg)](https://github.com/cookpad/LicenseToolsPlugin/actions)
[![License](https://img.shields.io/badge/License-Apache%202.0-orange.svg)](https://opensource.org/licenses/Apache-2.0)
[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/com/cookpad/android/plugin/license-tools/com.cookpad.android.plugin.license-tools.gradle.plugin/maven-metadata.xml.svg?colorB=007ec6&label=gradle%20potal)](https://plugins.gradle.org/plugin/com.cookpad.android.plugin.license-tools)

This repository is a replacement project of [cookpad/license-tools-plugin](https://github.com/cookpad/license-tools-plugin) written with kotlin

## License List(sample)

<img src="https://user-images.githubusercontent.com/1908396/103847983-bc7ce780-50e4-11eb-804e-1837113fd61b.png" width=200 />

## Setup

**Recommend**

```gradle
plugins {
  id "com.cookpad.android.plugin.license-tools" version "${latest_version}"
}
```

If you use legacy gradle project

```gradle
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "gradle.plugin.com.cookpad.android.plugin:plugin:${latest_version}"
  }
}

apply plugin: "com.cookpad.android.plugin.license-tools"
```

[Learn how to apply plugins to subprojects](https://docs.gradle.org/current/userguide/plugins.html#sec:subprojects_plugins_dsl)

## Gradle tasks

This Plugin provite to check library licenses and generate license pages.

- `./gradlew checkLicenses` to check licenses in dependencies
- `./gradlew updateLicenses` to update library information file `licenses.yml`
- `./gradlew generateLicensePage` to generate a license page `licenses.html`
- `./gradlew generateLicenseJson` to generate a license json file `licenses.json`

## How To Use

### Run the `checkLicenses` task

You will see the following messages by `./gradlew checkLicenses`:

```yaml
# Libraries not listed:
- artifact: androidx.annotation:annotation:+
  name: Android Support Library Annotations
  copyrightHolder: #COPYRIGHT HOLDER#
  license: The Apache Software License, Version 2.0
  url: http://developer.android.com/tools/extras/support-library.html
  licenseUrl: http://www.apache.org/licenses/LICENSE-2.0.txt
- artifact: io.reactivex.rxjava2:rxjava:+
  name: rxjava
  copyrightHolder: #COPYRIGHT HOLDER#
  license: The Apache Software License, Version 2.0
  url: https://github.com/ReactiveX/RxJava2
```

### Add library licenses to `app/licenses.yml`

Then, Create `app/licenses.yml`, and add libraries listed the above with required fields:

```yaml
- artifact: androidx.annotation:annotation:+
  name: Android Support Library Annotations
  copyrightHolder: Google Inc.
  license: The Apache Software License, Version 2.0
  url: http://developer.android.com/tools/extras/support-library.html
  licenseUrl: http://www.apache.org/licenses/LICENSE-2.0.txt
- artifact: io.reactivex.rxjava2:rxjava:+
  name: rxjava
  copyrightHolder: Netflix, Inc.
  license: The Apache Software License, Version 2.0
  url: https://github.com/ReactiveX/RxJava2
```

You can use wildcards in artifact names and versions.
You'll know the Android support libraries are grouped in `androidx.annotation:annotation` so you use `androidx.annotation:annotation:+:+` here.

Then, `./gradlew checkLicenses` will passes.

### Generate `licenses.html` by the `generateLicensePage` task

`./gradlew generateLicensePage` generates `app/src/main/assets/licenses.html`.

This plugin does not provide `Activity` nor `Fragment` to show `licenses.html`. You should add it by yourself.

`example/MainActivity` is an example.

### Configuring the plugin

Use `licenseTools` in your build.gradle to add some optional configuration.

For example:

```
licenseTools {
    outputHtml = "licenses_output.html"
}
```

Available configuration fields:

| Field name        | Default value      | Description                                                                                                |
| ----------------- | ------------------ | ---------------------------------------------------------------------------------------------------------- |
| `licensesYaml`    | `"licenses.yml"`   | The name of the licenses yml file                                                                          |
| `outputHtml`      | `"licenses.html"`  | The file name of the output of the `generateLicensePage` task                                              |
| `outputJson`      | `"licenses.json"`  | The file name of the output of the `generateLicenseJson` task                                              |
| `ignoredGroups`   | `[]` (empty array) | An array of group names the plugin will ignore (useful for internal dependencies with missing .pom files)  |
| `ignoredProjects` | `[]` (empty array) | An array of project names the plugin will ignore (To ignore particular internal projects like custom lint) |

## DataSet Format

### Required Fields

- `artifact`
- `name`
- Either `copyrightHolder`, `author`, `authors` or `notice`

### Optional Fields

- `year` to indicate copyright years
- `skip` to skip generating license entries (for proprietary libraries)
- `forceGenerate` to force generate the output with arbitrary items. (Read [this issue](Feature Request: feature for adding/changing licenses by hand #78) for more details.)
  - If some `pom` data is wrong, you can override some of them using this flag.

## Sample

```yaml
- artifact: androidx.annotation:annotation:+
  name: Android Support Library Annotations
  copyrightHolder: Google Inc.
  license: The Apache Software License, Version 2.0
  url: http://developer.android.com/tools/extras/support-library.html
  licenseUrl: http://www.apache.org/licenses/LICENSE-2.0.txt
- artifact: androidx.appcompat:appcompat:+
  name: Android AppCompat Library v7
  copyrightHolder: Google Inc.
  license: The Apache Software License, Version 2.0
  url: http://developer.android.com/tools/extras/support-library.html
  licenseUrl: http://www.apache.org/licenses/LICENSE-2.0.txt
- artifact: com.github.bumptech.glide:glide:+
  name: Glide
  copyrightHolder: Sam Judd
  license: Simplified BSD License
  url: https://github.com/bumptech/glide
  licenseUrl: http://www.opensource.org/licenses/bsd-license
```

## Release Engineering

1. Update CHANGELOG.md & Commit
1. `git tag $next_version` (i.g. git tag 1.2.0)
1. `git push orgin master --tags`
1. Fire [publish action](https://github.com/cookpad/LicenseToolsPlugin/actions?query=workflow%3APublish) (automatically)

After that check https://plugins.gradle.org/plugin/com.cookpad.android.plugin.license-tools
