package com.oceanshapers.kiwi.search

import java.io.Serializable
import java.math.BigDecimal

class CheapestFlight(val departureCountry: Country, val destinationCountry: Country, val price : BigDecimal) : Serializable
