package com.example.echo.fragments


import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.example.echo.CurrentSongHelper
import com.example.echo.R
import com.example.echo.Songs
import com.example.echo.databases.EchoDatabase
import com.example.echo.fragments.SongPlayingFragment.Staticated.MY_PREFS_NAME

import com.example.echo.fragments.SongPlayingFragment.Staticated.audioVisualization
import com.example.echo.fragments.SongPlayingFragment.Staticated.currentPosition
import com.example.echo.fragments.SongPlayingFragment.Staticated.currentSongHelper
import com.example.echo.fragments.SongPlayingFragment.Staticated.endTimeText
import com.example.echo.fragments.SongPlayingFragment.Staticated.fab
import com.example.echo.fragments.SongPlayingFragment.Staticated.favoriteContent
import com.example.echo.fragments.SongPlayingFragment.Staticated.fetchSongs
import com.example.echo.fragments.SongPlayingFragment.Staticated.glView
import com.example.echo.fragments.SongPlayingFragment.Staticated.loopImageButton
import com.example.echo.fragments.SongPlayingFragment.Staticated.mSensorListener
import com.example.echo.fragments.SongPlayingFragment.Staticated.mSensorManager
import com.example.echo.fragments.SongPlayingFragment.Staticated.mediaplayer
import com.example.echo.fragments.SongPlayingFragment.Staticated.myActivity
import com.example.echo.fragments.SongPlayingFragment.Staticated.nextImageButton
import com.example.echo.fragments.SongPlayingFragment.Staticated.playpauseImageButton
import com.example.echo.fragments.SongPlayingFragment.Staticated.previousImageButton
import com.example.echo.fragments.SongPlayingFragment.Staticated.seekBar
import com.example.echo.fragments.SongPlayingFragment.Staticated.shuffleImageButton
import com.example.echo.fragments.SongPlayingFragment.Staticated.songArtistView
import com.example.echo.fragments.SongPlayingFragment.Staticated.songPlaying
import com.example.echo.fragments.SongPlayingFragment.Staticated.songTitleView
import com.example.echo.fragments.SongPlayingFragment.Staticated.startTimeText
import com.example.echo.fragments.SongPlayingFragment.Staticated.updateSongTime
import kotlinx.android.synthetic.main.fragment_favourites.*
import kotlinx.android.synthetic.main.fragment_song_playing.*
import kotlinx.android.synthetic.main.fragment_song_playing.playPauseButton
import org.w3c.dom.Text
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.max


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class SongPlayingFragment : Fragment() {


    object Staticated {
        var myActivity: Activity? = null
        var mediaplayer: MediaPlayer? = null
        var startTimeText: TextView? = null
        var endTimeText: TextView? = null
        var playpauseImageButton: ImageButton? = null
        var previousImageButton: ImageButton? = null
        var nextImageButton: ImageButton? = null
        var loopImageButton: ImageButton? = null
        var seekBar: SeekBar? = null
        var songArtistView: TextView? = null
        var songTitleView: TextView? = null
        var shuffleImageButton: ImageButton? = null
        var currentPosition: Int = 0
        var fetchSongs: ArrayList<Songs>? = null
        var currentSongHelper: CurrentSongHelper? = null
        var audioVisualization: AudioVisualization? = null
        var glView: GLAudioVisualizationView? = null
        var songPlaying: TextView? = null
        var fab: ImageButton? = null
        var currentTrackPosition: String? = null

        var favoriteContent: EchoDatabase? = null
        var mSensorManager: SensorManager? = null
        var mSensorListener: SensorEventListener? = null
        var MY_PREFS_NAME = "Shake Feature"
        var updateSongTime = object : Runnable {
            override fun run() {
                val getcurrent = mediaplayer?.currentPosition
                startTimeText?.setText(
                    String.format(
                        "%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(getcurrent?.toLong() as Long),
                        TimeUnit.MILLISECONDS.toSeconds(getcurrent?.toLong() as Long) -
                                TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getcurrent?.toLong() as Long))
                    )
                )
                seekBar?.setProgress(getcurrent?.toInt() as Int)
                Handler().postDelayed(this, 1000)
            }
        }


    }


    object Statified {

        var MY_PREFS_SHUFFLE = "Shuffle feature"
        var My_PREFS_LOOP = "Loop feature"

        fun onSongComplete() {
            if (!(currentSongHelper?.isShuffle)!!) {
                if (currentSongHelper!!.isLoop) {
                    currentSongHelper?.isPlaying = true
                    var nextSong = fetchSongs?.get(currentPosition)
                    SongPlayingFragment.Staticated.currentTrackPosition = nextSong?.songTitle

                    if (nextSong?.artist.equals("<unknown>", true)) {
                        currentSongHelper?.songArtist = "unknown"
                    } else {
                        currentSongHelper?.songArtist = nextSong?.artist
                    }

                    currentSongHelper?.songTitle = nextSong?.songTitle
                    currentSongHelper?.songPath = nextSong?.songData
                    currentSongHelper?.currentPosition = currentPosition
                    currentSongHelper?.songId = nextSong?.songID as Long
                    if (currentSongHelper?.songId?.toInt()?.let { favoriteContent?.checkifIdExists(it) } as Boolean) {
                        fab?.setImageDrawable(ContextCompat.getDrawable(myActivity as Context, R.drawable.favorite_on))
                    }
                    mediaplayer?.reset()
                    try {
                        mediaplayer?.setDataSource(myActivity, Uri.parse(nextSong.songData))
                        mediaplayer?.prepare()
                        mediaplayer?.start()
                        songArtistView?.text = nextSong.artist
                        songTitleView?.text = nextSong.songTitle
                        processInformation(mediaplayer as MediaPlayer)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    currentSongHelper?.isPlaying = true
                    playNext("PlayNextNormal")
                }
            } else {
                currentSongHelper?.isPlaying = true
                playNext("playNextLikeNormalShuffle")
            }
        }

        //if (currentSongHelper?.isShuffle as Boolean) {
        //
        fun playNext(check: String) {
            if (check.equals("PlayNextNormal", true)) {
                currentPosition = currentPosition + 1
            } else if (check.equals("PlayNextLikeNormalShuffle", true)) {
                var randomObject = Random()
                var randomPosition = randomObject.nextInt(fetchSongs?.size?.plus(1) as Int)
                currentPosition = randomPosition


            }
            if (currentPosition == fetchSongs?.size) {
                currentPosition = 0
            }
            currentSongHelper?.isLoop = false
            val nextSong = fetchSongs?.get(currentPosition)
            currentSongHelper?.songPath = nextSong?.songData
            currentSongHelper?.songTitle = nextSong?.songTitle
            currentSongHelper?.currentPosition = currentPosition
            currentSongHelper?.songId = nextSong?.songID as Long
            updateTextViews(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)
            mediaplayer?.reset()
            try {
                mediaplayer?.setDataSource(myActivity, Uri.parse(currentSongHelper?.songPath))
                mediaplayer?.prepare()
                mediaplayer?.start()
                processInformation(mediaplayer as MediaPlayer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (favoriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                fab?.setImageDrawable(ContextCompat.getDrawable(myActivity!!, R.drawable.favorite_on))
            } else {
                fab?.setImageDrawable(ContextCompat.getDrawable(myActivity!!, R.drawable.favorite_off))
            }

        }

        fun updateTextViews(songtitle: String, songartist: String) {

            songTitleView?.setText(songtitle)
            songArtistView?.setText(songartist)
        }

        fun processInformation(mediaPlayer: MediaPlayer) {
            val finalTime = mediaPlayer.duration
            val startTime = mediaPlayer.currentPosition
            seekBar?.max = finalTime
            startTimeText?.setText(
                String.format(
                    "%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(startTime?.toLong() as Long),
                    TimeUnit.MILLISECONDS.toSeconds(startTime?.toLong() as Long) -
                            TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime?.toLong() as Long))
                )
            )
            endTimeText?.setText(
                String.format(
                    "%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(finalTime?.toLong() as Long),
                    TimeUnit.MILLISECONDS.toSeconds(finalTime?.toLong() as Long) -
                            TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime?.toLong() as Long))
                )
            )
            seekBar?.setProgress(startTime)
            Handler().postDelayed(updateSongTime, 1000)
        }
    }

    var mAcceleration: Float = 0f
    var mAccelerationCurrent: Float = 0f
    var mAccelerationLast: Float = 0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_song_playing, container, false)
        setHasOptionsMenu(true)
        Staticated.seekBar = view?.findViewById(R.id.seekBar)
        activity?.title = "Now Playing"
        startTimeText = view?.findViewById(R.id.startTime)
        endTimeText = view?.findViewById(R.id.endTime)
        playpauseImageButton = view?.findViewById(R.id.playPauseButton)
        nextImageButton = view?.findViewById(R.id.nextButton)
        previousImageButton = view?.findViewById(R.id.previousButton)
        loopImageButton = view?.findViewById(R.id.loopButton)
        shuffleImageButton = view?.findViewById(R.id.shuffleButton)
        songArtistView = view?.findViewById(R.id.songArtist)
        songTitleView = view?.findViewById(R.id.songTitle)
        glView = view?.findViewById(R.id.visualizer_view)
        fab = view?.findViewById(R.id.favoriteIcon)
        //fab?.alpha = 0.8f //to dim the effect
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        audioVisualization = glView as AudioVisualization
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onResume() {
        super.onResume()
        audioVisualization?.onResume()
            mSensorManager?.registerListener(mSensorListener, mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL)

    }

    override fun onPause() {
        audioVisualization?.onPause()
        super.onPause()
        mSensorManager?.unregisterListener(mSensorListener)
    }

    override fun onDestroyView() {
        audioVisualization?.release()
        super.onDestroyView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSensorManager = myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAcceleration = 0.0f
        mAccelerationCurrent = SensorManager.GRAVITY_EARTH
        mAccelerationLast = SensorManager.GRAVITY_EARTH
        bindShakeListener()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater!!.inflate(R.menu.song_playing_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)


    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item: MenuItem? = menu?.findItem(R.id.action_redirect)
        item?.isVisible = true
        val item2:MenuItem? = menu?.findItem(R.id.action_sort)
        item2?.isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.action_redirect ->{
                myActivity?.onBackPressed() // to return back to the page from which user just came
                return false
            }
        }
        return false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        favoriteContent = EchoDatabase(myActivity)

        currentSongHelper = CurrentSongHelper()
        currentSongHelper?.isPlaying = true
        currentSongHelper?.isLoop = false
        currentSongHelper?.isShuffle = false
        var path: String? = null
        var _songTitle: String? = null
        var _songArtist: String? = null
        var songId: Long = 0
        try {
            path = arguments?.getString("path")
            _songTitle = arguments?.getString("songTitle")
            _songArtist = arguments?.getString("songArtist")
            songId = arguments?.getInt("songId")!!.toLong()
            currentPosition = arguments?.getInt("songPosition")!!
            fetchSongs = arguments?.getParcelableArrayList("songData")
            currentSongHelper?.songPath = path
            currentSongHelper?.songTitle = _songTitle
            currentSongHelper?.songArtist = _songArtist
            currentSongHelper?.songId = songId
            Statified.updateTextViews(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        var fromFavBottomBar = arguments?.get("FavBottomBar") as? String
        if(fromFavBottomBar != null){
            Staticated.mediaplayer = Favourites.Statified.mediaPlayer
        }else {
            mediaplayer = MediaPlayer()
            mediaplayer?.setAudioStreamType(AudioManager.STREAM_MUSIC) // this tells that from audio manager stream only music
            try {
                mediaplayer?.setDataSource(myActivity, Uri.parse(path))
                mediaplayer?.prepare()

            } catch (e: Exception) {
                e.printStackTrace()
            }
            mediaplayer?.start()
        }
        Statified.processInformation(mediaplayer as MediaPlayer)
        if (currentSongHelper?.isPlaying as Boolean) {
            playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            //playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            //playPauseButton?.setBackgroundResource(R.drawable.play_icon)
        }
        mediaplayer?.setOnCompletionListener {
            Statified.onSongComplete()
        }
        clickHandler()
        var visualizationHandler = DbmHandler.Factory.newVisualizerHandler(myActivity as Context, 0)
        audioVisualization?.linkTo(visualizationHandler)

        var prefsForShuffle = myActivity?.getSharedPreferences(Statified.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)
        var isShuffleAllowed = prefsForShuffle?.getBoolean("feature", false)
        if (isShuffleAllowed as Boolean) {
            currentSongHelper?.isShuffle = true
            currentSongHelper?.isLoop = false
            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
            loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        } else {
            currentSongHelper?.isShuffle = false
            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }
        var prefsForLoop = myActivity?.getSharedPreferences(Statified.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)
        var isLoopAllowed = prefsForLoop?.getBoolean("feature", false)
        if (isLoopAllowed as Boolean) {
            currentSongHelper?.isShuffle = false
            currentSongHelper?.isLoop = true
            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
        } else {
            currentSongHelper?.isLoop = false
            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }
        if (favoriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean) {
            fab?.setImageDrawable(ContextCompat.getDrawable(myActivity!!, R.drawable.favorite_on))
        } else {
            fab?.setImageDrawable(ContextCompat.getDrawable(myActivity!!, R.drawable.favorite_off))
        }
    }

    fun clickHandler() {
        fab?.setOnClickListener({
            if (favoriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                fab?.setImageDrawable(ContextCompat.getDrawable(myActivity!!, R.drawable.favorite_off))
                favoriteContent?.deleteFavourite(currentSongHelper?.songId?.toInt() as Int)
                Toast.makeText(myActivity,"Removed from favorites", Toast.LENGTH_SHORT).show()
            } else {
                fab?.setImageDrawable(ContextCompat.getDrawable(myActivity!!, R.drawable.favorite_on))
                favoriteContent?.storeAsFavorite(currentSongHelper?.songId?.toInt(),currentSongHelper?.songArtist,currentSongHelper?.songTitle,currentSongHelper?.songPath)
                Toast.makeText(myActivity,"Added to favorites",Toast.LENGTH_SHORT).show()
            }
        })

        songPlaying?.setOnClickListener({
            isSongPlaying()

        })

        shuffleImageButton?.setOnClickListener({
            var editorShuffle =
                myActivity?.getSharedPreferences(Statified.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editorLoop = myActivity?.getSharedPreferences(Statified.My_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()
            if (currentSongHelper?.isShuffle as Boolean) {
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                currentSongHelper?.isShuffle = false
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
            } else {
                currentSongHelper?.isShuffle = true
                currentSongHelper?.isLoop = false
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorShuffle?.putBoolean("feature", true)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            }
        })
        nextImageButton?.setOnClickListener({
            currentSongHelper?.isPlaying = true
            playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            if (currentSongHelper?.isShuffle as Boolean) {
                Statified.playNext("PlayNextLikeNormalShuffle")
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)

            } else {
                Statified.playNext("PlayNextNormal")
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }
        })
        previousImageButton?.setOnClickListener({
            currentSongHelper?.isPlaying = true
            if (currentSongHelper?.isLoop as Boolean) {
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }
            playPrevious()
        })
        loopImageButton?.setOnClickListener({
            var editorShuffle =
                myActivity?.getSharedPreferences(Statified.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editorLoop = myActivity?.getSharedPreferences(Statified.My_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()
            if (currentSongHelper?.isLoop as Boolean) {
                currentSongHelper?.isLoop = false
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
                //this is to turn of the loop
            } else {
                currentSongHelper?.isLoop = true
                currentSongHelper?.isShuffle = false
                    false // when the loop feature is turned on the shuffle feature gets closed
                loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature", true)
                editorLoop?.apply()
            }
        })
        playpauseImageButton?.setOnClickListener({
            if (mediaplayer?.isPlaying as Boolean) {
                mediaplayer?.pause()
                currentSongHelper?.isPlaying = false
                playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                Favourites.Statified.playPauseButton?.setBackgroundResource(R.drawable.play_icon)

            } else {
                mediaplayer?.start()
                currentSongHelper?.isPlaying = true
                playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
                Favourites.Statified.playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })
    }



    fun playPrevious() {
        currentPosition = currentPosition - 1
        if (currentPosition == -1) {
            currentPosition = 0
        }
        if (currentSongHelper?.isPlaying as Boolean) {
            playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }
        currentSongHelper?.isLoop = false
        val nextSong = fetchSongs?.get(currentPosition)
        currentSongHelper?.songPath = nextSong?.songData
        currentSongHelper?.songTitle = nextSong?.songTitle
        currentSongHelper?.currentPosition = currentPosition
        currentSongHelper?.songId = nextSong?.songID as Long
        Statified.updateTextViews(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)
        mediaplayer?.reset()
        try {
            mediaplayer?.setDataSource(myActivity, Uri.parse(currentSongHelper?.songPath))
            mediaplayer?.prepare()
            mediaplayer?.start()
            Statified.processInformation(mediaplayer as MediaPlayer)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (favoriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean) {
            fab?.setImageDrawable(ContextCompat.getDrawable(myActivity!!, R.drawable.favorite_on))
        } else {
            fab?.setImageDrawable(ContextCompat.getDrawable(myActivity!!, R.drawable.favorite_off))
        }
    }
    fun isSongPlaying() {
        if (currentSongHelper?.isPlaying as Boolean) {
            currentSongHelper?.isPlaying = true
            mediaplayer?.stop()

        } else {
            currentSongHelper?.isPlaying
        }
    }
    fun bindShakeListener(){
        mSensorListener = object: SensorEventListener{
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            }
            // override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            //}

            override fun onSensorChanged(p0: SensorEvent?) {
                val x = p0!!.values[0]
                val y = p0!!.values[1]
                val z = p0!!.values[2]

                mAccelerationLast = mAccelerationCurrent
                mAccelerationCurrent = Math.sqrt(((x*x+y*y+z*z).toDouble())).toFloat()
                val delta = mAccelerationCurrent - mAccelerationLast
                mAcceleration = mAcceleration * 0.9f + delta

                if(mAcceleration> 12){
                    val prefs = myActivity?.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE)
                    val isAllowed = prefs?.getBoolean("feature",false)
                    Statified.playNext("PlayNextNormal")
                }
            }


        }
    }
}
