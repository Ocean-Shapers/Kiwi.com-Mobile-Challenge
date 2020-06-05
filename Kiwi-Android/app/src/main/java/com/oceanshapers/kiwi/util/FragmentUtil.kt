package com.autonet.novid20.helper

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.oceanshapers.kiwi.R

public class FragmentUtil {

    fun replaceFragmentWith(newFragment: Fragment, fragmentManager: FragmentManager, destination:String = "", source:String="", lastVisited:String = "" ) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        val bundle = Bundle()
        bundle.putString("destination",destination)
        bundle.putString("source",source)
        bundle.putString("lastVisited", lastVisited)
        newFragment.arguments = bundle
        fragmentTransaction.replace(R.id.activity_main, newFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

}