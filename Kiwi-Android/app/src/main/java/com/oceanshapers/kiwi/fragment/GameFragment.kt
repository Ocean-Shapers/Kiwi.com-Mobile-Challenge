package com.oceanshapers.kiwi.fragment

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.autonet.novid20.helper.FragmentUtil
import com.oceanshapers.kiwi.R
import com.oceanshapers.kiwi.search.CheapestFlightSearchService
import com.oceanshapers.kiwi.search.Country
import com.oceanshapers.kiwi.search.CountrySearchService
import kotlinx.android.synthetic.main.fragment_game.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class GameFragment : Fragment() {
    lateinit var plasticAnimation: ObjectAnimator
    lateinit var collectibleAnimation: ObjectAnimator
    private val displayMetrics = DisplayMetrics()
    var currentSessionScore = 0
    lateinit var budapestVienna: ValueAnimator
    lateinit var viennaAmsterdam: ValueAnimator
    lateinit var amsterdamParis: ValueAnimator
    lateinit var parisLondon: ValueAnimator
    private val gameOverTimer = Timer()
    private val sessionScoreTimer = Timer()
    val handler = Handler()
    lateinit var runnable: Runnable
    lateinit var sourceCountry: Country
    lateinit var destinationCountry: Country
    var destinationUnlocked: String = "none"
    lateinit var sourceCountryString: String
    var gameOver = false
    var turtleUpperLimit = 0.0f
    var turtleLowerLimit = 0.0f
    var turtleLimitSet = false
    lateinit var turtleUpSound: MediaPlayer
    lateinit var turtledownSound: MediaPlayer
    lateinit var turtleCollectsSound: MediaPlayer
    lateinit var gameOverSound: MediaPlayer


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
        turtleUpSound = MediaPlayer.create(
            context,
            resources.getIdentifier("turtle_up", "raw", activity!!.packageName)
        )
        turtledownSound = MediaPlayer.create(
            context,
            resources.getIdentifier("turtle_dives", "raw", activity!!.packageName)
        )
        turtleCollectsSound = MediaPlayer.create(
            context,
            resources.getIdentifier("turtle_collects_item", "raw", activity!!.packageName)
        )
        gameOverSound = MediaPlayer.create(
            context,
            resources.getIdentifier("game_over", "raw", activity!!.packageName)
        )
        arguments?.getString("source")?.let {
            sourceCountryString = it
        }
        hideDashboard()
        showFaresFor(resources.getStringArray(R.array.destination_country_list).get(0))
        gameOver = false
        dashboard_text_header.text = resources.getString(R.string.first_city)
        dashboard_faires_text.text = resources.getString(R.string.fares_from)
        score_text.text = currentSessionScore.toString()
        val collectibleArray = intArrayOf(
            R.drawable.vienna, R.drawable.amsterdam, R.drawable.paris, R.drawable.london
        )
        val destinationTextArray = resources.getStringArray(R.array.destination_list)
        jump_up_button.setOnClickListener {
            animateButton(jump_up_button)
            if (!turtleLimitSet) {
                setTurtleLimits()
            }
            if (turtle.y - 240 >= turtleUpperLimit) {

                turtleUpSound.start()
                turtle.animate().x(turtle.x).y(turtle.y - 240).setDuration(200)
            }
        }
        jump_down_button.setOnClickListener {
            animateButton(jump_down_button)
            if (!turtleLimitSet) {
                setTurtleLimits()
            }
            if (turtle.y + 240 <= turtleLowerLimit) {
                turtledownSound.start()
                turtle.animate().x(turtle.x).y(turtle.y + 240).setDuration(200)
            }
        }
        gameOverTimer()
        sessionScoreTimer()
        runnable = object : Runnable {
            var i = 0
            override fun run() {
                showFaresFor(resources.getStringArray(R.array.destination_country_list).get(i))
                destinationUnlocked = destinationTextArray.get(i)
                collectible.setImageResource(collectibleArray.get(i))
                dashboard_text_header.text = destinationTextArray.get(i + 1)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        turtleLowerLimit = displayMetrics.ydpi
        startAnimation(plastic1, -(displayMetrics.widthPixels * 2).toFloat(), 0, 8000)
        startAnimation(plastic2, -(displayMetrics.widthPixels * 2).toFloat(), 500, 10000)
        startAnimation(plastic3, -(displayMetrics.widthPixels * 2).toFloat(), 10000, 6000)
        startAnimation(plastic4, -(displayMetrics.widthPixels * 2).toFloat(), 15000, 11000)
        startCollectibleAnimation()
    }

    private fun animateButton(buttonToAnimate: Button) {
        val buttonAnimation = AnimationUtils.loadAnimation(context, R.anim.bounce);
        val interpolator = CustomBounceInterpolator(0.2, 20.0)
        buttonAnimation.setInterpolator(interpolator)
        buttonToAnimate.startAnimation(buttonAnimation)
    }

    //Method to limit turtle movement in just three lanes - middle, top and lower.
    private fun setTurtleLimits() {
        turtleLowerLimit = turtle.y + 250.0f
        turtleUpperLimit = turtle.y - 250.0f
        turtleLimitSet = true
    }

    // Method to show fares for all destinations
    private fun showFaresFor(currentDestination: String) {
        Thread {
            sourceCountry =
                CountrySearchService().searchByString(sourceCountryString).get(0)
            destinationCountry =
                CountrySearchService().searchByString(currentDestination).get(0)
            val cheapestFlight = CheapestFlightSearchService().search(
                sourceCountry, destinationCountry
            )
            activity!!.runOnUiThread {
                if (cheapestFlight?.price != null && fare !=null) {
                    fare.text = cheapestFlight?.price.toString() + "\u20ac"
                    showDashboard()
                }
            }
        }.start()
    }

    private fun hideDashboard() {
        dashboard_faires_text.visibility = View.INVISIBLE
        fare.visibility = View.INVISIBLE
    }

    private fun showDashboard() {
        dashboard_faires_text.visibility = View.VISIBLE
        fare.visibility = View.VISIBLE
    }

    private fun setAnimationProperties(
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

    //Method to animate collectible.
    private fun startCollectibleAnimation() {
        collectibleAnimation = ObjectAnimator.ofFloat(
            collectible,
            "translationX",
            -(displayMetrics.widthPixels).toFloat()
        )
        collectibleAnimation.duration = 4000
        collectibleAnimation.repeatCount = ValueAnimator.INFINITE
        collectibleAnimation.startDelay = 0
        collectibleAnimation.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
                val random = (0..2).random()
                if (random == 1 && collectible.y - 220 > turtleUpperLimit) {
                    collectible.y = collectible.y - 220
                }
                if (random == 2 && collectible.y + 220 < turtleLowerLimit) {
                    collectible.y = collectible.y + 220
                }
                collectible.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {}
        })
        collectibleAnimation.start()
    }

    private fun startAnimation(
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

    //Method to detect game over if turtle collides with plastic
    private fun gameOverTimer() {
        gameOverTimer.schedule(object : TimerTask() {
            override fun run() {
                activity!!.runOnUiThread {
                    if (detectCollisionWith(plastic1) || detectCollisionWith(plastic2) || detectCollisionWith(
                            plastic3
                        ) || detectCollisionWith(plastic4)
                    ) {
                        disableButtonsOnGameEnd()
                        gameOverSound.start()
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

    fun disableButtonsOnGameEnd()
    {
        jump_up_button.isEnabled = false
        jump_up_button.isClickable = false
        jump_down_button.isClickable = false
        jump_down_button.isEnabled = false
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
        collectible.visibility = View.GONE
        collectibleAnimation.cancel()
    }

    // Method to keep track of user score when turtle collides with the gems.
    private fun sessionScoreTimer() {
        sessionScoreTimer.schedule(object : TimerTask() {
            override fun run() {
                activity!!.runOnUiThread {
                    if (detectCollisionWith(collectible)) {
                        turtleCollectsSound.start()
                        collectible.visibility = View.INVISIBLE
                        currentSessionScore = currentSessionScore + 5
                        score_text.text = currentSessionScore.toString()
                    }
                }
            }
        }, 0, 300) // Delay of 5 milliseconds for user to get a hang of all game components
    }

    // Need this method to navigate to the scores page 2000 milliseconds after turtle reaches last city - London
    // That means user has completed the game.
    private val gameWinListener: Animator.AnimatorListener = object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {}
        override fun onAnimationEnd(animation: Animator?) {
            //User has won the game so show game win and stop all plastic animations and navigate
            // to next page
            handler.postDelayed(
                {
                    if (!gameOver) {
                        disableButtonsOnGameEnd()
                        destinationUnlocked = resources.getString(R.string.london_city_name)
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

    // Post game over or game win navigate to scores page after 2000milliseconds.
    fun goToLastPage() {
        val fragmentUtil = FragmentUtil()
        val fragmentManager = activity!!.supportFragmentManager
        handler.postDelayed({
            fragmentUtil.replaceFragmentWith(
                ScoresFragment(),
                fragmentManager,
                source = sourceCountryString,
                lastVisited = destinationUnlocked
            )
        }, 2000)
    }

    // method to detect if turtle collides with any object - plastic or gem.
    fun detectCollisionWith(objectToCollideWith: ImageView): Boolean {
        var collisionDetected = false
        //Need to adjust the cordinates since the image doesnot fill up the entire view so collision
        //would happen even when it looks like the turtle is away from the object.
        val actualFishX = turtle.x + 100
        val actualFishY = turtle.y + 200
        val actualFishWidth = turtle.width / 2 - 150
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

    //Logic to store high score and session score in shared preferences post game over.
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

    internal class CustomBounceInterpolator(
        amplitude: Double,
        frequency: Double
    ) :
        Interpolator {
        private var mAmplitude = 1.0
        private var mFrequency = 10.0
        override fun getInterpolation(time: Float): Float {
            return (-1 * Math.pow(Math.E, -time / mAmplitude) *
                    Math.cos(mFrequency * time) + 1).toFloat()
        }

        init {
            mAmplitude = amplitude
            mFrequency = frequency
        }
    }
}





