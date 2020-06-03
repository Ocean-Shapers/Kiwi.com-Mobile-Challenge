package com.oceanshapers.kiwi.fragment

import android.annotation.SuppressLint
import android.content.Context
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
import kotlinx.android.synthetic.main.fragment_options.*

/**
 * A simple [Fragment] subclass.
 */
class OptionsFragment : Fragment() {
    var lastSessionScore = 0
    var allTimeHighScore = 0
    lateinit var destinationSelected: String

    //val sharedPreference =  activity!!.applicationContext.getSharedPreferences("scores",Context.MODE_PRIVATE)
    /*
    List of todos for this page :
    TODO("Add kiwi api result to get list of cities")
     */
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
        val destinations = resources.getStringArray(R.array.city_list)
        val adapter = ArrayAdapter(
            activity!!.applicationContext, R.layout.custom_spinner_item, destinations
        )
        val fragmentManager = activity!!.supportFragmentManager
        val fragmentUtil = FragmentUtil()
        val sharedPreference = activity!!.getPreferences(Context.MODE_PRIVATE)
        if (sharedPreference != null) {
            allTimeHighScore = sharedPreference.getInt(resources.getString(R.string.SPHighScore), 0)
            lastSessionScore = sharedPreference.getInt(resources.getString(R.string.SPLastScore), 0)
        }
        adapter.setDropDownViewResource(R.layout.custom_spinner_item)
        destinationsSpinner.adapter = adapter
        destinationsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                destinationSelected = destinationsSpinner.selectedItem.toString()
            }
        }
        session_score.text = resources.getString(R.string.last_played) + " " + lastSessionScore
        high_score.text = resources.getString(R.string.all_time) + " " + allTimeHighScore
        start_game.setOnClickListener {
            fragmentUtil.replaceFragmentWith(
                GameFragment(),
                fragmentManager,
                arguments = destinationSelected
            )
        }

        //TESTING ONLY

        scores_view.setOnClickListener {
            fragmentUtil.replaceFragmentWith(
                ScoresFragment(),
                fragmentManager
            )
        }

        //Testing block complete
    }
}

