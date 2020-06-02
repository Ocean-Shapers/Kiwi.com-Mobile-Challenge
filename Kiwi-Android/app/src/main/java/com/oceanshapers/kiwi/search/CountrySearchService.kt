package com.oceanshapers.kiwi.search

import java.util.stream.Collectors
import java.util.stream.IntStream

class CountrySearchService {

    private companion object {
        private const val COUNTRY_SOURCE_URL = "https://restcountries.eu/rest/v2/region/europe"
        private const val COUNTRY_NAME_KEY = "name"
        private const val COUNTRY_CODE_KEY = "alpha2Code"
        private const val COUNTRY_LIMIT = 3L
        private const val STATUS_CODE_OK = 200
    }

    fun searchByString(searchString: String): List<Country> {
        val response = khttp.get(COUNTRY_SOURCE_URL)
        val countryResultList: List<Country>

        if (STATUS_CODE_OK !== response.statusCode) {
            countryResultList = emptyList()
        } else {
            val countryJsonArray = response.jsonArray
            countryResultList = IntStream.range(0, countryJsonArray.length())
                .mapToObj { index -> countryJsonArray.getJSONObject(index) }
                .map { countryJsonObject ->
                    Country(
                        countryJsonObject.getString(COUNTRY_NAME_KEY),
                        countryJsonObject.getString(COUNTRY_CODE_KEY)
                    )
                }
                .filter { country -> country.name.contains(searchString) }
                .limit(COUNTRY_LIMIT)
                .collect(Collectors.toList())
        }

        return countryResultList
    }
}
