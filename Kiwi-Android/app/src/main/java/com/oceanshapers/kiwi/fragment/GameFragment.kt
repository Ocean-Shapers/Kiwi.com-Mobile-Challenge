package com.oceanshapers.kiwi.fragment

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.autonet.novid20.helper.FragmentUtil
import com.oceanshapers.kiwi.R
import com.oceanshapers.kiwi.search.CheapestFlightSearchService
import com.oceanshapers.kiwi.search.Country
import com.oceanshapers.kiwi.search.CountrySearchService
import kotlinx.android.synthetic.main.fragment_game.*
import java.io.IOException
import java.math.BigDecimal
import java.util.*


/*
TODO("Add sounds for all user actions")
TODO("Turtle to only go up or down one step")
TODO("Clean code and commit")
 */

/**
 * A simple [Fragment] subclass.
 */
class GameFragment : Fragment() {
    lateinit var plasticAnimation: ObjectAnimator
    val displayMetrics = DisplayMetrics()
    var currentSessionScore = 0
    lateinit var budapestVienna: ValueAnimator
    lateinit var viennaAmsterdam: ValueAnimator
    lateinit var amsterdamParis: ValueAnimator
    lateinit var parisLondon: ValueAnimator
    val gameOverTimer = Timer()
    val sessionScoreTimer = Timer()
    val handler = Handler()
    lateinit var runnable: Runnable
    var destinationUnlocked: String = "none"
    lateinit var sourceCountryString: String
    var gameOver = false

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
        gameOver = false
        dashboard_text_header.text = resources.getString(R.string.first_city)
        dashboard_faires_text.text = resources.getString(R.string.fares_from)
        fare.text = "80" + "\u20ac"
        score_text.text = currentSessionScore.toString()
        arguments?.getString("arguments")?.let {
            sourceCountryString= it
        }
        val gemImageArray = intArrayOf(
            R.drawable.vienna, R.drawable.amsterdam, R.drawable.paris, R.drawable.london
        )
        val destinationTextArray = resources.getStringArray(R.array.destination_list)
        jump_up_button.setOnClickListener {
            fish_image.animate().x(fish_image.x).y(fish_image.y - 240).setDuration(200)
        }
        jump_down_button.setOnClickListener {
            fish_image.animate().x(fish_image.x).y(fish_image.y + 240).setDuration(200)
        }
        gameOverTimer()
        sessionScoreTimer()

        runnable = object : Runnable {
            var i = 0
            override fun run() {
                if (i > 0) {
                    destinationUnlocked = destinationTextArray.get(i - 1)
                }
                else
                {
                    destinationUnlocked = resources.getString(R.string.budapest_city_name)
                }
                gemImage.setImageResource(gemImageArray.get(i))
                //@norby : this is where the async task will be called to get the fare for this city.
                //findFare(sourceCountryString,gemImageArray.get(i).toString(),fare).execute()
                dashboard_text_header.text = destinationTextArray.get(i)
                i++
                handler.postDelayed(this, 20000)
            }
        }
        handler.postDelayed(runnable, 12000)
        budapest_map.visibility = View.VISIBLE
        vienna_map.visibility = View.VISIBLE
        budapestVienna = ValueAnimator.ofFloat(1.0f, 0.0f)
        setAnimationProperties(budapestVienna, vienna_map, budapest_map)
        budapestVienna.start()
        viennaAmsterdam = ValueAnimator.ofFloat(1.0f, 0.0f)
        setAnimationProperties(viennaAmsterdam, amsterdam_map, vienna_map)
        amsterdamParis = ValueAnimator.ofFloat(1.0f, 0.0f)
        setAnimationProperties(amsterdamParis, paris_map, amsterdam_map)
        parisLondon = ValueAnimator.ofFloat(1.0f, 0.0f)
        setAnimationProperties(parisLondon, london_map, paris_map)
        parisLondon.addListener(gameWinListener)
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
        startAnimation(plastic1, -(displayMetrics.widthPixels * 2).toFloat(), 0, 15000)
        startAnimation(plastic2, -(displayMetrics.widthPixels * 2).toFloat(), 500, 20000)
        startAnimation(plastic3, -(displayMetrics.widthPixels * 2).toFloat(), 10000, 15000)
        startAnimation(plastic4, -(displayMetrics.widthPixels * 2).toFloat(), 15000, 18000)
        startAnimation(gemImage, -(displayMetrics.widthPixels).toFloat(), 0, 3000)
    }
    fun startAnimation(
        plastic: ImageView,
        transitionTo: Float,
        delay: Long,
        animationDuration: Long
    ) {
        plasticAnimation = ObjectAnimator.ofFloat(plastic, "translationX", transitionTo)
        plasticAnimation.duration = animationDuration
        plasticAnimation.repeatCount = ValueAnimator.INFINITE
        plasticAnimation.startDelay = delay
        plasticAnimation.start()
    }
    fun gameOverTimer() {
        gameOverTimer.schedule(object : TimerTask() {
            override fun run() {
                activity!!.runOnUiThread {
                    if (detectCollisionWith(plastic1) || detectCollisionWith(plastic2) || detectCollisionWith(
                            plastic3
                        ) || detectCollisionWith(plastic4)
                    ) {
                        gameOver = true
                        gameStatus.setBackgroundResource(R.drawable.game_over)
                        gameStatus.visibility = View.VISIBLE
                        stopbackgroundAnimations()
                        stopPlasticAnimations()
                        stopTimers()
                        goToLastPage()
                    }
                }
            }
        }, 0, 10) // Delay of 5 milliseconds for user to get a hang of all game components
    }

    fun stopbackgroundAnimations() {
        budapestVienna.cancel()
        viennaAmsterdam.cancel()
        amsterdamParis.cancel()
        parisLondon.cancel()
        plasticAnimation.cancel()
    }
    fun stopTimers() {
        if (gameOverTimer != null) {
            gameOverTimer.cancel()
            gameOverTimer.purge()
        }
        if (sessionScoreTimer != null) {
            sessionScoreTimer.cancel()
            sessionScoreTimer.purge()
        }
        handler.removeCallbacks(runnable)
    }

    fun stopPlasticAnimations() {
        plastic1.visibility = View.GONE
        plastic2.visibility = View.GONE
        plastic3.visibility = View.GONE
        plastic4.visibility = View.GONE
        gemImage.visibility = View.GONE
    }

    fun sessionScoreTimer() {
        sessionScoreTimer.schedule(object : TimerTask() {
            override fun run() {
                activity!!.runOnUiThread {
                    if (detectCollisionWith(gemImage)) {
                        currentSessionScore = currentSessionScore + 5
                        score_text.text = currentSessionScore.toString()
                    }
                }
            }
        }, 0, 300) // Delay of 5 milliseconds for user to get a hang of all game components
    }

    val gameWinListener: Animator.AnimatorListener = object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {}
        override fun onAnimationEnd(animation: Animator?) {
            //User has won the game so show game win and stop all plastic animations and navigate
            // to next page
            handler.postDelayed(
                {
                    //stopPlasticAnimations()
                    if(!gameOver) {
                        stopTimers()
                        gameStatus.setBackgroundResource(R.drawable.win)
                        gameStatus.visibility = View.VISIBLE
                        stopPlasticAnimations()
                        stopTimers()
                        goToLastPage()
                    }
                }, 2000
            )
        }
        override fun onAnimationCancel(animation: Animator?) {}
        override fun onAnimationStart(animation: Animator?) {}
    }

    fun goToLastPage() {
        val fragmentUtil = FragmentUtil()
        val fragmentManager = activity!!.supportFragmentManager
        handler.postDelayed({
            fragmentUtil.replaceFragmentWith(ScoresFragment(), fragmentManager, destinationUnlocked)
        }, 2000)
    }

    fun detectCollisionWith(objectToCollideWith: ImageView): Boolean {
        var collisionDetected = false
        //Need to adjust the cordinates since the image doesnot fill up the entire view so collision
        //would happen even when it looks like the turtle is away from the object.
        val actualFishX = fish_image.x + 100
        val actualFishY = fish_image.y + 200
        val actualFishWidth = fish_image.width / 2 - 150
        val actualObjectHeight = objectToCollideWith.height / 2 - 350
        //Logic to determine if a plastic or gem collided with the fish
        if ((objectToCollideWith.x < actualFishX) && (actualFishX < objectToCollideWith.x + objectToCollideWith.width) &&
            (objectToCollideWith.y < actualFishY) && (actualFishY < (objectToCollideWith.y + objectToCollideWith.height)) ||
            (objectToCollideWith.x < actualFishX + actualFishWidth) &&
            (actualFishX + actualFishWidth < objectToCollideWith.x + objectToCollideWith.width)
            && (objectToCollideWith.y < actualFishY) &&
            (actualFishY < objectToCollideWith.y + actualObjectHeight)
        ) {
            collisionDetected = true
        }
        return collisionDetected
    }

    //Logic to store high score and session score in shared preferences.
    override fun onStop() {
        super.onStop()
        val sharedPreference = activity!!.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPreference.edit()) {
            if (currentSessionScore > sharedPreference.getInt(
                    resources.getString(R.string.SPHighScore),
                    0
                )
            ) {
                putInt(getString(R.string.SPHighScore), currentSessionScore)
            }
            putInt(getString(R.string.SPLastScore), currentSessionScore)
            commit()
        }
    }
private class findFare(sourceCountry: String, destinationCountry: String, textView: TextView) : AsyncTask<Void, Void, Void>() {
    var sourceCountryString = sourceCountry
    var destinationCountryString = destinationCountry
    var cheapestFare : BigDecimal = BigDecimal.ZERO
    var textViewToUpdate = textView

    override fun doInBackground(vararg params: Void?): Void? {
        try {
            //get country object for both source and destination
            val sourceCountryObject = CountrySearchService().searchByString("Germany")
            val destinationCountryObject = CountrySearchService().searchByString("Hungary")
            System.out.println("DEBUG : sourceCountryObject : " +sourceCountryObject)
            System.out.println("DEBUG : destinationCountryObject : " +destinationCountryObject)
            val cheapestFlight = CheapestFlightSearchService().search(sourceCountryObject.get(0),destinationCountryObject.get(0))
           // cheapestFare = cheapestFlight!!.price
            System.out.println("DEBUG : cheapestFare : " +cheapestFlight)
        }
        catch(exception:IOException)
        {
            exception.printStackTrace()
        }
        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        textViewToUpdate.text = cheapestFare.toString()+"\u20ac"
    }
}
}





