package com.oceanshapers.kiwi.fragment

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.oceanshapers.kiwi.util.FragmentUtil
import com.oceanshapers.kiwi.R
import com.oceanshapers.kiwi.search.CheapestFlight
import com.oceanshapers.kiwi.search.Country
import kotlinx.android.synthetic.main.fragment_about_city.*

/**
 * A simple [Fragment] subclass.
 */
class AboutCityFragment : Fragment() {
    lateinit var mediaPlayer: MediaPlayer
    lateinit var cheapestFlightsMap: HashMap<Country, CheapestFlight?>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.getSerializable("cheapestFlights")?.let {
            cheapestFlightsMap = it as HashMap<Country, CheapestFlight?>
        }

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
        var source = Country(resources.getString(R.string.budapest_city_name), "HU")
        arguments?.getSerializable("source")?.let {
            source = it as Country
        }
        var lastVisited = resources.getString(R.string.budapest_city_name)
        arguments?.getString("lastVisited")?.let {
            lastVisited = it
        }
        back.setOnClickListener {
            stopPreviousAudio()
            mediaPlayer = MediaPlayer.create(
                context,
                resources.getIdentifier("go_back", "raw", activity!!.packageName)
            )
            mediaPlayer.start()
            fragmentUtil.replaceFragmentWith(
                ScoresFragment(),
                fragmentManager,
                destination = destinationCity,
                source = source,
                lastVisited = lastVisited,
                cheapestFlightsMap = cheapestFlightsMap
            )
        }
        val imageResourceName = destinationCity.toString().toLowerCase() + "_city"
        val id = resources.getIdentifier(imageResourceName, "drawable", activity!!.packageName)
        city_image.setImageResource(id)
        city_title.text = destinationCity + " " + resources.getString(R.string.city_title)
        city_desc.text = resources.getString(
            resources.getIdentifier(
                destinationCity + "_tourism",
                "string",
                activity!!.packageName
            )
        )
        city_env_desc.text = resources.getString(
            resources.getIdentifier(
                destinationCity + "_env",
                "string",
                activity!!.packageName
            )
        )
        city_collect_text.text = resources.getString(
            resources.getIdentifier(
                destinationCity + "_collect",
                "string",
                activity!!.packageName
            )
        )
        visit_text.text =
            resources.getString(R.string.visit) + " " + destinationCity + " " + resources.getString(
                R.string.from
            ) + " " + source
        val destinationCountryCode = resources.getString(
            resources.getIdentifier(
                destinationCity + "_country",
                "string",
                activity!!.packageName
            )
        )
        val cheapestFlight = cheapestFlightsMap.entries.filter { mutableEntry ->
            mutableEntry.key.name == destinationCountryCode
        }
            .map { mutableEntry -> mutableEntry.value }
            .firstOrNull()

        activity!!.runOnUiThread {
            if (fares_text != null && cheapestFlight?.price != null) {
                visit_text.visibility = View.VISIBLE
                fares_text.visibility = View.VISIBLE
                fares_text.text =
                    resources.getString(R.string.fares_from) + "\n" + cheapestFlight?.price.toString() + "\u20ac"
            }
        }
    }

    private fun stopPreviousAudio() {
        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
        }
    }

}