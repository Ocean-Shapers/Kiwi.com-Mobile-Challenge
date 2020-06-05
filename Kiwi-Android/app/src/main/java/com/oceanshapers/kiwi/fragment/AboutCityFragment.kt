package com.oceanshapers.kiwi.fragment

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.autonet.novid20.helper.FragmentUtil
import com.oceanshapers.kiwi.R
import com.oceanshapers.kiwi.search.CheapestFlightSearchService
import com.oceanshapers.kiwi.search.CountrySearchService
import kotlinx.android.synthetic.main.fragment_about_city.*
import kotlinx.android.synthetic.main.fragment_game.*

/**
 * A simple [Fragment] subclass.
 */
class AboutCityFragment : Fragment() {
    lateinit var mediaPlayer: MediaPlayer
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about_city, container, false)
    }

    override fun onStart() {
        super.onStart()
        val fragmentManager = activity!!.supportFragmentManager
        val fragmentUtil = FragmentUtil()
        var destinationCity = resources.getString(R.string.budapest_city_name)
        arguments?.getString("destination")?.let {
            destinationCity = it
        }
        var source = resources.getString(R.string.budapest_city_name)
        arguments?.getString("source")?.let {
            source = it
        }
        var lastVisited = resources.getString(R.string.budapest_city_name)
        arguments?.getString("lastVisited")?.let {
            lastVisited = it
        }
       back.setOnClickListener{
           stopPreviousAudio()
           mediaPlayer = MediaPlayer.create(context, resources.getIdentifier("go_back","raw",activity!!.packageName))
           mediaPlayer.start()
            fragmentUtil.replaceFragmentWith(
                ScoresFragment(),
                fragmentManager,
                destination = destinationCity,
                source = source,
                lastVisited = lastVisited
            )
        }
        val imageResourceName = destinationCity.toString().toLowerCase() + "_city"
        val id = resources.getIdentifier(imageResourceName,"drawable", activity!!.packageName)
        city_image.setImageResource(id)
        city_title.text = destinationCity + " " +resources.getString(R.string.city_title)
        city_desc.text = resources.getString(resources.getIdentifier(destinationCity+"_tourism","string",activity!!.packageName))
        city_env_desc.text = resources.getString(resources.getIdentifier(destinationCity+"_env","string",activity!!.packageName))
        city_collect_text.text = resources.getString(resources.getIdentifier(destinationCity+"_collect","string",activity!!.packageName))
        visit_text.text = resources.getString(R.string.visit) + " " + destinationCity + " " + resources.getString(R.string.from) + " " + source
        Thread {
            val sourceCountry =
                CountrySearchService().searchByString(source).get(0)
            val destinationCountry =
                CountrySearchService().searchByString(resources.getString(resources.getIdentifier(destinationCity+"_country","string",activity!!.packageName))).get(0)
            val cheapestFlight = CheapestFlightSearchService().search(
                sourceCountry, destinationCountry
            )
            activity!!.runOnUiThread {
                if(fares_text!=null)
                {
                    fares_text.visibility = View.VISIBLE
                    fares_text.text = resources.getString(R.string.fares_from) + "\n" + cheapestFlight?.price.toString() + "\u20ac"
                }
            }
        }.start()
    }

    private fun stopPreviousAudio()
    {
        if (this::mediaPlayer.isInitialized)
        {
            mediaPlayer.stop()
        }
    }

}