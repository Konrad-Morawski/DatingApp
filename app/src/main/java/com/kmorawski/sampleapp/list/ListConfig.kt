package com.kmorawski.sampleapp.list

/**
 * @property pageSize the number of items to be retrieved from the API per single request
 * @property maxItems maximum number of items to be loaded onto the list
 */
data class ListConfig(val pageSize: Int, val maxItems: Int)