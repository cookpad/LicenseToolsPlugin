package com.cookpad.android.plugin.license.extension

import com.cookpad.android.plugin.license.data.LibraryInfo

internal fun LibraryInfo.generateLibraryInfoText(): String {
    val text = StringBuffer()
    text.append("- artifact: ${artifactId.withWildcardVersion()}\n")
    text.append("  name: ${name ?: "#NAME#"}\n")
    text.append("  copyrightHolder: ${copyrightHolder ?: "#COPYRIGHT_HOLDER#"}\n")
    text.append("  license: ${license ?: "#LICENSE#"}\n")
    if (licenseUrl?.isNotBlank() == true) {
        text.append("  licenseUrl: ${licenseUrl}\n")
    }
    if (url?.isNotBlank() == true) {
        text.append("  url: ${url}\n")
    }
    return text.toString().trim()
}
