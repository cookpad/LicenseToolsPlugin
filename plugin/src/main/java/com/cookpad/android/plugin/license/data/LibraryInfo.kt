// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2016 Cookpad Inc.

package com.cookpad.android.plugin.license.data

data class LibraryInfo(
    val artifactId: ArtifactId,
    val name: String? = null,
    val libraryName: String? = null,
    val url: String? = null,
    val fileName: String? = null,
    val license: String? = null,
    val year: String? = null,
    val copyrightHolder: String? = null,
    val notice: String? = null,
    val licenseUrl: String?,
    val skip: Boolean? = null,
    val forceGenerate: Boolean? = null
) {

    fun normalizedLicense(): String {
        return when {
            license.isNullOrEmpty() -> ""
            apache1_0.matches(license) -> "apache1_0"
            apache1_1.matches(license) -> "apache1_1"
            apache2.matches(license) -> "apache2"
            mit.matches(license) -> "mit"
            bsd_2_clauses_a.matches(license)
                    || bsd_2_clauses_b.matches(license) -> "bsd_2_clauses"
            bsd_3_clauses_a.matches(license) ||
                    bsd_3_clauses_b.matches(license)
                    || bsd_3_clauses_c.matches(license) -> "bsd_3_clauses"
            bsd_4_clauses_a.matches(license)
                    || bsd_4_clauses_b.matches(license) -> "bsd_4_clauses"
            bsd_3_clauses_d.matches(license) -> "bsd_3_clauses"
            isc.matches(license) -> "isc"
            mpl1_0_a.matches(license)
                    || mpl1_0_b.matches(license) -> "mpl1_0"
            mpl2_a.matches(license)
                    || mpl2_b.matches(license) -> "mpl2"
            cpl1_a.matches(license)
                    || cpl1_b.matches(license) -> "cpl1"
            epl1_a.matches(license)
                    || epl1_b.matches(license) -> "epl1"
            fpl1_a.matches(license)
                    || fpl1_b.matches(license) -> "fpl1"
            facebook_platform_license.matches(license) -> "facebook_platform_license"
            cc0_a.matches(license)
                    || cc0_b.matches(license) -> "cc0"
            cddl_a.matches(license)
                    || cddl_b.matches(license) -> "cddl1"
            lgpl2_1_a.matches(license)
                    || lgpl2_1_b.matches(license) -> "lgpl2_1"
            lgpl3_a.matches(license) ||
                    lgpl3_b.matches(license) ||
                    lgpl3_c.matches(license)
                    || lgpl3_d.matches(license) -> "lgpl3"
            gpl1_a.matches(license)
                    || gpl1_b.matches(license) -> "gpl1"
            gpl2_a.matches(license)
                    || gpl2_b.matches(license) -> "gpl2"
            gpl3_a.matches(license) ||
                    gpl3_b.matches(license) ||
                    gpl3_c.matches(license)
                    || gpl3_d.matches(license) -> "gpl3"
            mopub_sdk_license_a.matches(license)
                    || mopub_sdk_license_b.matches(license) -> "mopub_sdk_license"
            android_software_license.matches(license) -> "android_software_development_kit_license"
            amazon_software_license.matches(license) -> "amazon_software_license"
            play_core_software_development_kit_terms_of_service.matches(license) -> "play_core_software_development_kit_terms_of_service"
            pushwoosh_license.matches(license) -> "pushwoosh_license"
            else -> license
        }
    }

    fun getCopyrightStatement(): String? {
        return when {
            notice?.isNotBlank() ?: false -> notice
            copyrightHolder.isNullOrEmpty() -> null
            else -> buildCopyrightStatement(copyrightHolder)
        }
    }

    private fun buildCopyrightStatement(copyrightHolder: String): String {
        val dot = if (copyrightHolder.endsWith(".")) "" else "."
        return if (year.isNullOrEmpty()) {
            "Copyright &copy; ${copyrightHolder}$dot All rights reserved."
        } else {
            "Copyright &copy; $year, ${copyrightHolder}$dot All rights reserved."
        }
    }

    companion object {
        val apache1_0 = """(?i).*\bapache.*1\.0.*""".toRegex()
        val apache1_1 = """(?i).*\bapache.*1.*""".toRegex()
        val apache2 = """(?i).*\bapache.*2.*""".toRegex()
        val mit = """(?i).*\bmit\b.*""".toRegex()
        val bsd_2_clauses_a = """(?i).*\bbsd\b.*\b2\b.*""".toRegex()
        val bsd_2_clauses_b = """(?i).*\bsimplified\b.*\bbsd\b.*""".toRegex()
        val bsd_3_clauses_a = """(?i).*\bbsd\b.*\b3\b.*""".toRegex()
        val bsd_3_clauses_b = """(?i).*\brevised\b.*\bbsd\b.*""".toRegex()
        val bsd_3_clauses_c = """(?i).*\bmodified\b.*\bbsd\b.*""".toRegex()
        val bsd_3_clauses_d = """(?i).*\bbsd\b.*""".toRegex()
        val bsd_4_clauses_a = """(?i).*\bbsd\b.*\b4\b.*""".toRegex()
        val bsd_4_clauses_b = """(?i).*\boriginal\b.*\bbsd\b.*""".toRegex()
        val isc = """(?i).*\bisc\b.*""".toRegex()
        val mpl1_0_a = """(?i).*\bmozilla\b.*\bpublic\b.*\b1\.0\b.*""".toRegex()
        val mpl1_0_b = """(?i).*\bmpl\b?.*\b?1\.0\b.*""".toRegex()
        val mpl2_a = """(?i).*\bmozilla\b.*\bpublic\b.*\b2\b.*""".toRegex()
        val mpl2_b = """(?i).*\bmpl\b?.*\b?2\b.*""".toRegex()
        val cpl1_a = """(?i).*\bcommon\b.*\bpublic\b.*""".toRegex()
        val cpl1_b = """(?i).*\bcpl\b.*""".toRegex()
        val epl1_a = """(?i).*\beclipse\b.*\bpublic\b.*\b1\b.*""".toRegex()
        val epl1_b = """(?i).*\bepl\b.*\b1\b.*""".toRegex()
        val fpl1_a = """(?i).*\bfree\b.*\bpublic\b.*""".toRegex()
        val fpl1_b = """(?i).*\bfpl\b.*""".toRegex()
        val facebook_platform_license =
            """(?i).*\bfacebook\b.*\bplatform\b.*\blicense\b.*""".toRegex()
        val cc0_a = """(?i).*\bcc0\b.*""".toRegex()
        val cc0_b = """(?i).*\bcreative\b.commons\b.*\b.*""".toRegex()
        val cddl_a = """(?i).*\bcommon\b.*\bdevelopment\b.*\bdistribution\b.*""".toRegex()
        val cddl_b = """(?i).*\bcddl\b.*""".toRegex()
        val lgpl2_1_a = """(?i).*\bgnu\b.*\blesser\b.*\bgeneral\b.*\bpublic\b.*2.*""".toRegex()
        val lgpl2_1_b = """(?i).*\blgpl\b?.*2.*""".toRegex()
        val lgpl3_a = """(?i).*\bgnu\b.*\blesser\b.*\bgeneral\b.*\bpublic\b.*3.*""".toRegex()
        val lgpl3_b = """(?i).*\bgnu\b.*\blesser\b.*\bgeneral\b.*\bpublic\b.*""".toRegex()
        val lgpl3_c = """(?i).*\blgpl\b?.*3.*""".toRegex()
        val lgpl3_d = """(?i).*\blgpl\b.*""".toRegex()
        val gpl1_a = """(?i).*\bgnu\b.*\bgeneral\b.*\bpublic\b.*1.*""".toRegex()
        val gpl1_b = """(?i).*\bgpl\b?.*1.*""".toRegex()
        val gpl2_a = """(?i).*\bgnu\b.*\bgeneral\b.*\bpublic\b.*2.*""".toRegex()
        val gpl2_b = """(?i).*\bgpl\b?.*2.*""".toRegex()
        val gpl3_a = """(?i).*\bgnu\b.*\bgeneral\b.*\bpublic\b.*3.*""".toRegex()
        val gpl3_b = """(?i).*\bgnu\b.*\bgeneral\b.*\bpublic\b.*""".toRegex()
        val gpl3_c = """(?i).*\bgpl\b?.*3.*""".toRegex()
        val gpl3_d = """(?i).*\bgpl\b.*""".toRegex()
        val mopub_sdk_license_a = """(?i).*\bmopub\b.*""".toRegex()
        val mopub_sdk_license_b = """(?i).*\bmopub\b.*\bsdk\b.*\blicense\b.*""".toRegex()
        val android_software_license = """(?i).*\bAndroid.*\bSoftware.*\bDevelopment.*\bKit.*\bLicense\b.*""".toRegex()
        val amazon_software_license = """(?i).*\bAmazon.*\bSoftware.*\bLicense\b.*""".toRegex()
        val play_core_software_development_kit_terms_of_service = """(?i).*\bPlay.*\bCore.*\bSoftware.*\bDevelopment.*\bKit.*\bTerms.*\bof.*\bService\b.*""".toRegex()
        val pushwoosh_license = """(?i).*\bPushwoosh.*\bLicense\b.*""".toRegex()
    }
}
