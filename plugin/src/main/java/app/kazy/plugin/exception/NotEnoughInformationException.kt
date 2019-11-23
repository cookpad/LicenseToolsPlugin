package app.kazy.plugin.exception

import app.kazy.plugin.data.LibraryInfo

class NotEnoughInformationException(val libraryInfo: LibraryInfo) : RuntimeException()
