package com.oceanshapers.kiwi.search

import android.util.Log
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CheapestFlightSearchService {

    private companion object {
        private const val STATUS_CODE_OK = 200
        private const val FLIGHT_URL =
            "https://api.skypicker.com/flights?fly_from=%s&fly_to=%s&date_from=%s&date_to=%s&limit=1&sort=price&asc=1&partner=picky"
        private const val FLIGHT_DATE_FORMAT = "MM/dd/yyyy"
        private const val DATA_JSON_KEY = "data"
        private const val PRICE_JSON_KEY = "price"
    }

    fun search(departureCountry: Country, destinationCountry: Country): CheapestFlight? {
        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern(FLIGHT_DATE_FORMAT));
        val response = khttp.get(
            String.format(
                FLIGHT_URL,
                departureCountry.countryCode,
                destinationCountry.countryCode,
                currentDate,
                currentDate
            )
        )
        var cheapestFlight: CheapestFlight? = null

        if (STATUS_CODE_OK == response.statusCode) {
            try {
                val responseArray = response.jsonObject.getJSONArray(DATA_JSON_KEY)
                if (responseArray.length() > 0) {
                    val dataObject = responseArray.getJSONObject(0)
                    cheapestFlight = CheapestFlight(
                        departureCountry,
                        destinationCountry,
                        BigDecimal(dataObject.getString(PRICE_JSON_KEY))
                    )
                }
            } catch (e: Exception) {
                Log.e("Flight error", e.message ?: "empty error message")
            }
        }
        return cheapestFlight
    }
}
