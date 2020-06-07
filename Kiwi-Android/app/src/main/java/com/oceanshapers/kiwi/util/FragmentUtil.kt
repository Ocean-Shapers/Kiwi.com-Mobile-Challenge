package com.oceanshapers.kiwi.util

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.oceanshapers.kiwi.R
import com.oceanshapers.kiwi.search.CheapestFlight
import com.oceanshapers.kiwi.search.Country

public class FragmentUtil {

    fun replaceFragmentWith(newFragment: Fragment, fragmentManager: FragmentManager, destination:String = "", source:Country=Country("Czech", "CZ"), lastVisited:String = "",
                            cheapestFlightsMap : HashMap<Country, CheapestFlight?> = HashMap<Country, CheapestFlight?>() ) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        val bundle = Bundle()
        bundle.putString("destination",destination)
        bundle.putSerializable("source",source)
        bundle.putString("lastVisited", lastVisited)
        bundle.putSerializable("cheapestFlights", cheapestFlightsMap)
        newFragment.arguments = bundle

        fragmentTransaction.replace(R.id.activity_main, newFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

}