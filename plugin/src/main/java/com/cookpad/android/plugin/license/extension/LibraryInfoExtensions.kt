package com.cookpad.android.plugin.license.extension

import com.cookpad.android.plugin.license.data.LibraryInfo

internal fun LibraryInfo.generateLibraryInfoText(updatedInfo: LibraryInfo? = null): String {
    val text = StringBuffer()
    text.append("- artifact: ${artifactId.withWildcardVersion()}\n")

    var currentName = name ?: "#NAME#"
    currentName = if (updatedInfo?.name != null && currentName == "#NAME#") {
        updatedInfo.name
    } else currentName
    var currentCopyrightHolder = copyrightHolder ?: "#COPYRIGHT_HOLDER#"
    currentCopyrightHolder = if (updatedInfo?.copyrightHolder != null && currentCopyrightHolder == "#COPYRIGHT_HOLDER#") {
        updatedInfo.copyrightHolder
    } else currentCopyrightHolder
    var currentLicense = license ?: "#LICENSE#"
    currentLicense = if (updatedInfo?.license != null && currentLicense == "#LICENSE#") {
        updatedInfo.license
    } else currentLicense



    if (currentName != "#NAME#" || skip != true) {
        text.append("  name: ${currentName}\n")
    }
    if (notice != null) {
        if (notice.lines().size > 1) {
            text.append("  notice: |\n    ${notice.replace("\n", "\n    ")}\n")
        } else {
            text.append("  notice: ${notice}\n")
        }
    } else if (currentCopyrightHolder != "#COPYRIGHT_HOLDER#" || skip != true) {
        text.append("  copyrightHolder: ${currentCopyrightHolder}\n")
    }
    if (currentLicense != "#LICENSE#" || skip != true) {
        text.append("  license: ${currentLicense}\n")
    }
    if (licenseUrl?.isNotBlank() == true) {
        text.append("  licenseUrl: ${licenseUrl}\n")
    } else if (updatedInfo?.licenseUrl?.isNotBlank() == true) {
        text.append("  licenseUrl: ${updatedInfo.licenseUrl}\n")
    }
    if (url?.isNotBlank() == true) {
        text.append("  url: ${url}\n")
    } else if (updatedInfo?.url?.isNotBlank() == true) {
        text.append("  url: ${updatedInfo.url}\n")
    }

    if (customLicenseName?.isNotBlank() == true) {
        text.append("  customLicenseName: ${customLicenseName}\n")
    }
    if (customLicenseContent?.isNotBlank() == true) {
        text.append("  customLicenseContent: |\n    ${customLicenseContent.replace("\n", "\n    ")}\n")
    }

    if (skip == true) {
        text.append("  skip: true\n")
    }
    return text.toString().trim()
}
