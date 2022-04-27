package com.example.networkmanager.utils

object Util {

    /**
     * Return last part of the url as [String]
     * */
    fun getTheLastPartOfTheUrl(relativeUrl: String): String {
        return relativeUrl.substring(relativeUrl.lastIndexOf("/") + 1)
    }
}