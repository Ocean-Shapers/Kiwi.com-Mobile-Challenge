package com.oceanshapers.kiwi.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
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
    var currentTurtleY = 0f
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
        super.onStart()
        currentTurtleY = fish_image.y
        jump_up_button.setOnClickListener {
            fish_image.animate().x(fish_image.x).y(fish_image.y - 200)
        }
        jump_down_button.setOnClickListener {
            fish_image.animate().x(fish_image.x).y(fish_image.y + 200)
        }
        startLooseLifeTimer()
        //gemImage.setImageDrawable(resources.getDrawable(R.drawable.gem_blink))
        budapest_map.visibility = View.VISIBLE
        vienna_map.visibility = View.VISIBLE
        val budapestVienna = ValueAnimator.ofFloat(1.0f, 0.0f)
        setAnimationProperties(budapestVienna, vienna_map,budapest_map)
        budapestVienna.start()
        val viennaAmsterdam = ValueAnimator.ofFloat(1.0f, 0.0f)
        setAnimationProperties(viennaAmsterdam,amsterdam_map,vienna_map)
        val amsterdamParis = ValueAnimator.ofFloat(1.0f, 0.0f)
        setAnimationProperties(amsterdamParis,paris_map,amsterdam_map)
        val parisLondon = ValueAnimator.ofFloat(1.0f, 0.0f)
        setAnimationProperties(parisLondon,london_map,paris_map)
        AnimatorSet().apply {
            play(viennaAmsterdam).after(budapestVienna)
            play(amsterdamParis).after(viennaAmsterdam)
            play(parisLondon).after(amsterdamParis)
            start()
        }

    }

    fun setAnimationProperties(animation: ValueAnimator, firstImageView: ImageView,followingImageView: ImageView)
    {
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
        startPlasticAnimation(plastic1, -(displayMetrics.widthPixels * 2).toFloat(), 0, 10000)
        startPlasticAnimation(plastic2, -(displayMetrics.widthPixels * 2).toFloat(), 500, 15000)
        startPlasticAnimation(plastic3, -(displayMetrics.widthPixels * 2).toFloat(), 1000, 20000)
        startPlasticAnimation(plastic4, -(displayMetrics.widthPixels * 2).toFloat(), 1500, 25000)
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

//    private val plasticAnimationListener : Animator.AnimatorListener = object :
//        Animator.AnimatorListener {
//        override fun onAnimationRepeat(animation: Animator?) {
//            plastic1.y = plastic1.y + 50
//        }
//
//        override fun onAnimationEnd(animation: Animator?) {
//            TODO("Not yet implemented")
//        }
//
//        override fun onAnimationCancel(animation: Animator?) {
//            TODO("Not yet implemented")
//        }
//
//        override fun onAnimationStart(animation: Animator?) {
//            System.out.println("Animate plastic")
//        }
//
//    }

    fun startLooseLifeTimer() {
        val looseLifeTimer = Timer()
        looseLifeTimer.schedule(object : TimerTask() {
            override fun run() {
                activity!!.runOnUiThread {
                    if (((plastic1.x < fish_image.x) && (fish_image.x < plastic1.x + plastic1.width)) && (plastic1.y < fish_image.y && fish_image.y < (plastic1.y + plastic1.height))) {
                        System.out.println("GAME OVER")
                        // scrollingBackImageView.stop()
                    }
                }
            }
        }, 10, 10) // Delay of 10 milliseconds for user to get a hang of all game components
    }

    override fun onDestroy() {
        super.onDestroy()
        // finalScore = 0
        //startScoring = false
        isGameOver = false
    }


}
