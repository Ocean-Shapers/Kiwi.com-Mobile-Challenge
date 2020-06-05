package com.oceanshapers.kiwi.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.autonet.novid20.helper.FragmentUtil
import com.oceanshapers.kiwi.R
import com.oceanshapers.kiwi.search.CountrySearchService
import kotlinx.android.synthetic.main.fragment_options.*

/**
 * A simple [Fragment] subclass.
 */
class OptionsFragment : Fragment() {
    var lastSessionScore = 0
    var allTimeHighScore = 0
    lateinit var sourceSelected: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(
            R.layout.fragment_options, container,
            false
        )
        return view
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var sourceCountries: MutableList<String> = mutableListOf<String>()
        sourceCountries.add(resources.getString(R.string.select_source))
        Thread {
            val countries = CountrySearchService().searchAllCountries()
            for (i in 0..(countries.size - 1)) {
                sourceCountries.add(countries.get(i).name)
            }
        }.start()
        val adapter = ArrayAdapter(
            activity!!.applicationContext, android.R.layout.select_dialog_item, sourceCountries
        )
        val fragmentManager = activity!!.supportFragmentManager
        val fragmentUtil = FragmentUtil()
        val sharedPreference = activity!!.getPreferences(Context.MODE_PRIVATE)
        if (sharedPreference != null) {
            allTimeHighScore = sharedPreference.getInt(resources.getString(R.string.SPHighScore), 0)
            lastSessionScore = sharedPreference.getInt(resources.getString(R.string.SPLastScore), 0)
        }
        adapter.setDropDownViewResource(R.layout.custom_spinner_item)
        sourceSpinner.adapter = adapter
        sourceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                sourceSelected = sourceSpinner.selectedItem.toString()

            }
        }
        session_score.text = resources.getString(R.string.last_played) + " " + lastSessionScore
        high_score.text = resources.getString(R.string.all_time) + " " + allTimeHighScore
        start_game.setOnClickListener {
            if (sourceSelected == resources.getString(R.string.select_source)){
                Toast.makeText(context, "Please select source country from dropdown on the left", Toast.LENGTH_SHORT).show()
            } else {
                var mediaPlayer = MediaPlayer.create(context, resources.getIdentifier("start_game","raw",activity!!.packageName))
                mediaPlayer.start()
                fragmentUtil.replaceFragmentWith(
                    GameFragment(),
                    fragmentManager,
                    source = sourceSelected
                )
            }
        }
    }
}

