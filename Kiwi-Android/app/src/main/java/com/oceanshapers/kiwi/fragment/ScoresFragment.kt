package com.oceanshapers.kiwi.fragment

import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.oceanshapers.kiwi.util.FragmentUtil
import com.oceanshapers.kiwi.R
import com.oceanshapers.kiwi.search.CheapestFlight
import com.oceanshapers.kiwi.search.Country
import kotlinx.android.synthetic.main.fragment_scores.*

/**
 * A simple [Fragment] subclass.
 */
class ScoresFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_scores, container, false)
    }

    override fun onStart() {
        super.onStart()
        var lastVisited = resources.getString(R.string.budapest_city_name)
        var sourceCountry = Country(resources.getString(R.string.budapest_city_name), "HU")
        arguments?.getString("lastVisited")?.let {
            lastVisited = it
        }
        arguments?.getSerializable("source")?.let {
            sourceCountry = it as Country
        }
        val noOfCitiesVisited =
            resources.getStringArray(R.array.destination_list).indexOf(lastVisited) + 1
        val sharedPreference = activity!!.getPreferences(Context.MODE_PRIVATE)
        val gems = sharedPreference.getInt(resources.getString(R.string.SPLastScore), 0) / 5
        val destinationImages =
            arrayOf(budapest_image, vienna_image, amsterdam_image, paris_image, london_image)
        unlockDestinations(destinationImages, noOfCitiesVisited, sourceCountry, lastVisited)
        main_text_2_3.text =
            resources.getString(R.string.main_text_2) + " " + noOfCitiesVisited + " " + resources.getString(
                R.string.main_text_3
            )
        gems_collected.text = resources.getString(R.string.gems_collected) + " " + gems
    }

    fun unlockDestinations(
        listOfImages: Array<ImageView>,
        noOfCitiesVisited: Int,
        sourceCountry: Country,
        lastVisited:String
    ) {
        val fragmentManager = activity!!.supportFragmentManager
        val fragmentUtil = FragmentUtil()
        //For loop to lock cities not visited
        for (i in noOfCitiesVisited until listOfImages.size) {
            listOfImages.get(i).setImageDrawable(null)
            listOfImages.get(i).setBackgroundColor(Color.parseColor("#5d6f9e"))
            listOfImages.get(i).setOnClickListener{
                stopPreviousAudio()
                mediaPlayer = MediaPlayer.create(context, resources.getIdentifier("city_locked","raw",activity!!.packageName))
                mediaPlayer.start()
            }
        }
        //for loop to unlock cities visited, click on any of these cities should take you to the about city page.
        for (i in 0 until noOfCitiesVisited) {
            listOfImages.get(i).setOnClickListener {
                fragmentUtil.replaceFragmentWith(
                    AboutCityFragment(),
                    fragmentManager,
                    destination = resources.getStringArray(R.array.destination_list).get(i),
                    source = sourceCountry,
                    lastVisited = lastVisited,
                    cheapestFlightsMap = cheapestFlightsMap
                )
            }
        }
        back_to_game.setOnClickListener {
            fragmentUtil.replaceFragmentWith(
                OptionsFragment(),
                fragmentManager
            )
        }
    }
    private fun stopPreviousAudio()
    {
        if (this::mediaPlayer.isInitialized)
        {
            mediaPlayer.stop()
        }
    }



}