package com.oceanshapers.kiwi.fragment

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator

import com.oceanshapers.kiwi.R
import kotlinx.android.synthetic.main.fragment_game.*
import kotlinx.android.synthetic.main.fragment_scores.*
import kotlinx.android.synthetic.main.fragment_scores.background1ImageView
import kotlinx.android.synthetic.main.fragment_scores.background2ImageView

/**
 * A simple [Fragment] subclass.
 */
class ScoresFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scores, container, false)
    }

    override fun onStart() {
        super.onStart()
        val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
// % 18 because my tile size is 18px
// and to avoid partial crop when both image are joined together
//        val width = displayMetrics.widthPixels
//        background1ImageView.layoutParams.width = width
//        background2ImageView.layoutParams.width = width

// start animation
        val animator = ValueAnimator.ofFloat(1.0f, 0.0f)
        animator.repeatCount = 0
        animator.interpolator = LinearInterpolator()
        animator.duration = 20000L // this will control speed of scrolling
        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            val width = budapest_map.getWidth()
            val translationX = width * progress
            background1ImageView.setTranslationX(translationX)
            background2ImageView.setTranslationX(translationX - width)
            //background3ImageView.setTranslationX(translationX-width)
        }
       // animator.addUpdateListener(animatorListener)
        animator.start()
    }

//    private val animatorListener : ValueAnimator.AnimatorUpdateListener = object : ValueAnimator.AnimatorUpdateListener
//    {
//        override fun onAnimationUpdate(animation: ValueAnimator?) {
//            TODO("Not yet implemented")
//            val progress = animation.animatedValue as Float
//            val width = background1ImageView.getWidth()
//            val translationX = width * progress
//            background1ImageView.setTranslationX(translationX)
//            background2ImageView.setTranslationX(translationX - width)
//        }
//
//    }

}
