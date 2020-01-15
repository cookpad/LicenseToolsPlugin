package com.cookpad.android.plugin.license.exception

import com.cookpad.android.plugin.license.data.LibraryInfo

class NotEnoughInformationException(val libraryInfo: LibraryInfo) : RuntimeException()
