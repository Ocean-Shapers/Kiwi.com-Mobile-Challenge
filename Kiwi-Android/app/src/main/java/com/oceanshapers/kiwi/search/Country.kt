package com.oceanshapers.kiwi.search

import android.os.Parcelable
import java.io.Serializable

data class Country(val name: String, val countryCode: String) : Serializable{
    override fun toString(): String {
        return name
    }
}
