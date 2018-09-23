package com.kmorawski.sampleapp.api

data class Profile(
        /* fields aren't expected to be null based on the API specs,
         * but we're protecting ourselves against breaking API changes.
         * malformed data can always be treated as a domain error and handled further down the line.
         * it doesn't have to be a network error, as some of the missing fields aren't really
         * essential - eg. just because the backend removed the surname field doesn't mean the app
         * isn't still usable. */
        val name: String?,
        val surname: String?,
        val photo: String?,
        val age: Int?,
        val region: String?)