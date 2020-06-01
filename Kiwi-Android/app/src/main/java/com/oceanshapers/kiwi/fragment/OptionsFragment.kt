package com.oceanshapers.kiwi.fragment

import android.annotation.SuppressLint
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
    /*
    List of todos for this page :
    TODO("Drop down of cities need to be drop down instead of modal") --> done
    TODO("Add kiwi api result to get list of cities")
    TODO("Set city selected as from city for fare calculation on next page")
    TODO("Show kiwi logo and ocean shaper logo on this page")
    TODO("Add gradient to scores text bx as per design") --> done
    TODO("Cities drop down needs to have gradient") --> done
    TODO("Session to save last played score and high score of user to display here")
    TODO("MY SCORES text to be bold rest two lines to be regular")
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
        adapter.setDropDownViewResource(R.layout.custom_spinner_item)
        destinationsSpinner.adapter = adapter
        destinationsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                getValues()
            }

        }
        scores_view.text =
           "\n" + getString(R.string.my_scores) + "\n" + getString(R.string.all_time) + " 105" + "\n" + getString(
                R.string.last_played
            ) + " 40"
        start_game.setOnClickListener {
//            val intent = Intent (getActivity(), GameActivity::class.java)
//            activity!!.startActivity(intent)
            fragmentUtil.replaceFragmentWith(GameFragment(), fragmentManager)
        }
        scores_view.setOnClickListener {
            fragmentUtil.replaceFragmentWith(ScoresFragment(), fragmentManager)
        }

    }

    fun getValues() {
        Toast.makeText(
            activity!!.applicationContext,
            "Destination selected : " + destinationsSpinner.selectedItem.toString(),
            Toast.LENGTH_LONG
        ).show()

    }
}

