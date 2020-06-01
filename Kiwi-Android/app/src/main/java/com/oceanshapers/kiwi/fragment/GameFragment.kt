package com.oceanshapers.kiwi.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.oceanshapers.kiwi.R
import kotlinx.android.synthetic.main.fragment_game.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class GameFragment : Fragment() {
    lateinit var plasticAnimation: ObjectAnimator

    //var currentTurtleY = 0f
    var isGameOver = false
    val displayMetrics = DisplayMetrics()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    // background map order : Budapest -->Budapest - Vienna - Amsterdam - Paris - London
    override fun onStart() {
        dashboard_text.text = "Budapest" + "\n" + "Fares from $300"
        val gemImageArray = intArrayOf(
            R.drawable.vienna, R.drawable.amsterdam, R.drawable.paris, R.drawable.london
        )
        val destinationTextArray = arrayOf("Vienna", "Amsterdam", "Paris", "London")
        fish_image.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        fish_image.buildLayer();
        super.onStart()
        //currentTurtleY = fish_image.y
        jump_up_button.setOnClickListener {
            fish_image.animate().x(fish_image.x).y(fish_image.y - 200).setDuration(200)
        }
        jump_down_button.setOnClickListener {
            fish_image.animate().x(fish_image.x).y(fish_image.y + 200).setDuration(200)
        }
        startLooseLifeTimer()
        val handler = Handler()
        val runnable: Runnable = object : Runnable {
            var i = 0
            override fun run() {
                gemImage.setImageResource(gemImageArray.get(i))
                dashboard_text.text = destinationTextArray.get(i) + "\n" + "Fares from $200"
                i++
                if (i > gemImageArray.size - 1) {
                    i = 0
                }
                handler.postDelayed(this, 20000)
            }
        }
        handler.postDelayed(runnable, 12000)
        budapest_map.visibility = View.VISIBLE
        vienna_map.visibility = View.VISIBLE
        val budapestVienna = ValueAnimator.ofFloat(1.0f, 0.0f)
        setAnimationProperties(budapestVienna, vienna_map, budapest_map)
        budapestVienna.start()
        val viennaAmsterdam = ValueAnimator.ofFloat(1.0f, 0.0f)
        setAnimationProperties(viennaAmsterdam, amsterdam_map, vienna_map)
        val amsterdamParis = ValueAnimator.ofFloat(1.0f, 0.0f)
        setAnimationProperties(amsterdamParis, paris_map, amsterdam_map)
        val parisLondon = ValueAnimator.ofFloat(1.0f, 0.0f)
        setAnimationProperties(parisLondon, london_map, paris_map)
        AnimatorSet().apply {
            play(viennaAmsterdam).after(budapestVienna)
            play(amsterdamParis).after(viennaAmsterdam)
            play(parisLondon).after(amsterdamParis)
            start()
        }
    }

    fun setAnimationProperties(
        animation: ValueAnimator,
        firstImageView: ImageView,
        followingImageView: ImageView
    ) {
        animation.repeatCount = 0
        animation.interpolator = LinearInterpolator()
        animation.duration = 20000L // this will control speed of scrolling
        animation.addUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            val width = firstImageView.width
            var translationX = width * progress
            firstImageView.setTranslationX(translationX)
            followingImageView.setTranslationX(translationX - width)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        startPlasticAnimation(plastic1, -(displayMetrics.widthPixels * 2).toFloat(), 0, 15000)
        startPlasticAnimation(plastic2, -(displayMetrics.widthPixels * 2).toFloat(), 500, 20000)
        startPlasticAnimation(plastic3, -(displayMetrics.widthPixels * 2).toFloat(), 10000, 15000)
        startPlasticAnimation(plastic4, -(displayMetrics.widthPixels * 2).toFloat(), 15000, 18000)
    }

    fun startPlasticAnimation(
        plastic: ImageView,
        transitionTo: Float,
        delay: Long,
        animationDuration: Long
    ) {
        plasticAnimation = ObjectAnimator.ofFloat(plastic, "translationX", transitionTo)
        plasticAnimation.duration = animationDuration
        plasticAnimation.repeatCount = ValueAnimator.INFINITE
        //plasticAnimation.addListener(plasticAnimationListener)
        plasticAnimation.startDelay = delay
        plasticAnimation.start()

    }

    fun startLooseLifeTimer() {
        val looseLifeTimer = Timer()
        looseLifeTimer.schedule(object : TimerTask() {
            override fun run() {
                activity!!.runOnUiThread {
                    if (detectCollisionWith(plastic1) || detectCollisionWith(plastic2) || detectCollisionWith(
                            plastic3
                        ) || detectCollisionWith(plastic4)
                    ) {
                       // gameOver.visibility = View.VISIBLE
                    }
                }
            }
        }, 0, 10) // Delay of 5 milliseconds for user to get a hang of all game components
    }

    fun detectCollisionWith(plasticImageView: ImageView): Boolean {
        var collisionDetected = false
        /*
         (a,b) ==> (fish.x, fish.y)
         (a+fishWidth,b) ==> (fish.x + fish.width, fish.y)
         (a,b+fishHeight) ==> (fish.x, fish.y + fish.length)
        (a+fishWidth,b+fishHeight) ==> (fish.x + fish.width, fish.y + fish.length)

        (x,y) ==> (plastic.x, plastic.y)
         (x+plasticWidth,y) ==> (plastic.x + plastic.width, plastic.y)
         (x,y+plasticHeight) ==> (plastic.x, plastic.y + plastic.length)
        (x+plasticWidth,y+plasticHeight) ==> (plastic.x + plastic.width, plastic.y + plastic.length)

        //Collison if :
        1. (a+h > x && a+h<x+h)&& (b+h >y && b+h <y+h)
        2. (a+w > x && a+w<x+w) && (b> y && b <y+h)

         */
        if ((plasticImageView.x-20 < fish_image.x) && (fish_image.x < plasticImageView.x-20 + plasticImageView.width) && (plasticImageView.y < fish_image.y) && (fish_image.y < (plasticImageView.y + plasticImageView.height)) ||
            (plasticImageView.x-20 < fish_image.x + fish_image.width) && (fish_image.x + fish_image.width < plasticImageView.x-20 + plasticImageView.width)
            &&(plasticImageView.y< fish_image.y+fish_image.height) && (fish_image.y + fish_image.height < plasticImageView.y+plasticImageView.height))
        {
            collisionDetected = true
        }
        return collisionDetected
    }

    override fun onDestroy() {
        super.onDestroy()
        // finalScore = 0
        //startScoring = false
        fish_image.setLayerType(View.LAYER_TYPE_NONE, null)
        isGameOver = false
    }


}
