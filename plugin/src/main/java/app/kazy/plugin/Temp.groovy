class Temp{

    public String getNormalizedLicense() {
        return normalizeLicense(license ?: "")
    }

    static String normalizeLicense(String name) {
        switch (name) {
            case ~/(?i).*\bapache.*1\.0.*/:
                return "apache1_0"
            case ~/(?i).*\bapache.*1.*/:
                return "apache1_1"
            case ~/(?i).*\bapache.*2.*/:
                return "apache2"
            case ~/(?i).*\bmit\b.*/:
                return "mit"
            case ~/(?i).*\bbsd\b.*\b2\b.*/:
            case ~/(?i).*\bsimplified\b.*\bbsd\b.*/:
                return "bsd_2_clauses"
            case ~/(?i).*\bbsd\b.*\b3\b.*/:
            case ~/(?i).*\brevised\b.*\bbsd\b.*/:
            case ~/(?i).*\bmodified\b.*\bbsd\b.*/:
                return "bsd_3_clauses"
            case ~/(?i).*\bbsd\b.*\b4\b.*/:
            case ~/(?i).*\boriginal\b.*\bbsd\b.*/:
                return "bsd_4_clauses"
            case ~/(?i).*\bbsd\b.*/:
                return "bsd_3_clauses"
            case ~/(?i).*\bisc\b.*/:
                return "isc"
            case ~/(?i).*\bmozilla\b.*\bpublic\b.*\b1\.0\b.*/:
            case ~/(?i).*\bmpl\b?.*\b?1\.0\b.*/:
                return "mpl1_0"
            case ~/(?i).*\bmozilla\b.*\bpublic\b.*\b1\b.*/:
            case ~/(?i).*\bmpl\b?.*\b?1\b.*/:
                return "mpl1_1"
            case ~/(?i).*\bmozilla\b.*\bpublic\b.*\b2\b.*/:
            case ~/(?i).*\bmpl\b?.*\b?2\b.*/:
                return "mpl2"
            case ~/(?i).*\bcommon\b.*\bpublic\b.*/:
            case ~/(?i).*\bcpl\b.*/:
                return "cpl1"
            case ~/(?i).*\beclipse\b.*\bpublic\b.*\b1\b.*/:
            case ~/(?i).*\bepl\b.*\b1\b.*/:
                return "epl1"
            case ~/(?i).*\bfree\b.*\bpublic\b.*/:
            case ~/(?i).*\bfpl\b.*/:
                return "fpl1"
            case ~/(?i).*\bfacebook\b.*\bplatform\b.*\blicense\b.*/:
                return "facebook_platform_license"
            case ~/(?i).*\bcc0\b.*/:
            case ~/(?i).*\bcreative\b.commons\b.*\b.*/:
                return "cc0"
            case ~/(?i).*\bcommon\b.*\bdevelopment\b.*\bdistribution\b.*/:
            case ~/(?i).*\bcddl\b.*/:
                return "cddl1"
            case ~/(?i).*\bgnu\b.*\blesser\b.*\bgeneral\b.*\bpublic\b.*2.*/:
            case ~/(?i).*\blgpl\b?.*2.*/:
                return "lgpl2_1"
            case ~/(?i).*\bgnu\b.*\blesser\b.*\bgeneral\b.*\bpublic\b.*3.*/:
            case ~/(?i).*\bgnu\b.*\blesser\b.*\bgeneral\b.*\bpublic\b.*/:
            case ~/(?i).*\blgpl\b?.*3.*/:
            case ~/(?i).*\blgpl\b.*/:
                return "lgpl3"
            case ~/(?i).*\bgnu\b.*\bgeneral\b.*\bpublic\b.*1.*/:
            case ~/(?i).*\bgpl\b?.*1.*/:
                return "gpl1"
            case ~/(?i).*\bgnu\b.*\bgeneral\b.*\bpublic\b.*2.*/:
            case ~/(?i).*\bgpl\b?.*2.*/:
                return "gpl2"
            case ~/(?i).*\bgnu\b.*\bgeneral\b.*\bpublic\b.*3.*/:
            case ~/(?i).*\bgnu\b.*\bgeneral\b.*\bpublic\b.*/:
            case ~/(?i).*\bgpl\b?.*3.*/:
            case ~/(?i).*\bgpl\b.*/:
                return "gpl3"
            case ~/(?i).*\bmopub\b.*/:
            case ~/(?i).*\bmopub\b.*\bsdk\b.*\blicense\b.*/:
                return "mopub_sdk_license"
            default:
                return name
        }
    }
}