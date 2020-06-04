package com.oceanshapers.kiwi.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.autonet.novid20.helper.FragmentUtil
import com.oceanshapers.kiwi.R
import kotlinx.android.synthetic.main.fragment_about_city.*

/**
 * A simple [Fragment] subclass.
 */
class AboutCityFragment : Fragment() {

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
        var city = resources.getString(R.string.budapest_city_name)
        arguments?.getString("arguments")?.let {
            city = it
        }
        back.setOnClickListener{
            fragmentUtil.replaceFragmentWith(
                ScoresFragment(),
                fragmentManager,
                city
            )
        }
        var source : String? = "dummyCountry"
        val sharedPreference = activity!!.getPreferences(Context.MODE_PRIVATE)
        if (sharedPreference != null) {
           source = sharedPreference.getString("source", "UK")
        }
        val imageResourceName = city.toString().toLowerCase() + "_city"
        val id = resources.getIdentifier(imageResourceName,"drawable", activity!!.packageName)
        city_image.setImageResource(id)
        city_title.text = city + " " +resources.getString(R.string.city_title)
        city_desc.text = resources.getString(resources.getIdentifier(city+"_tourism","string",activity!!.packageName))
        city_env_desc.text = resources.getString(resources.getIdentifier(city+"_env","string",activity!!.packageName))
        city_collect_text.text = resources.getString(resources.getIdentifier(city+"_collect","string",activity!!.packageName))
        visit_text.text = resources.getString(R.string.visit) + " " + city + " " + resources.getString(R.string.from) + " " + source
        fares_text.text = resources.getString(R.string.fares_from) + "\n" + "45 EUR"
    }

}