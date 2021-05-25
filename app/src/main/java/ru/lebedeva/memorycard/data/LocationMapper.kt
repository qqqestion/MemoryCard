package ru.lebedeva.memorycard.data

import android.content.Context
import android.location.Geocoder
import java.io.IOException
import java.util.*


class LocationMapper(
    private val context: Context
) {

    fun toAddress(lat: Double, lon: Double): String? {
        val geocoder =
            Geocoder(context, Locale.getDefault())
        return try {
            val address = geocoder.getFromLocation(lat, lon, 1)?.firstOrNull() ?: return null
            val country = address.countryName
            val city = address.locality
            "$country, $city"
        } catch (e: IOException) {
            null
        }
    }
}