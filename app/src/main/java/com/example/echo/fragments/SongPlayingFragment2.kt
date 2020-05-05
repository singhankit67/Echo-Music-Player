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
import com.example.echo.fragments.SongPlayingFragment1.Staticated.myActivity1
import com.example.echo.fragments.SongPlayingFragment1.Staticated.MY_PREFS_NAME1

import com.example.echo.fragments.SongPlayingFragment1.Staticated.currentPosition1
import com.example.echo.fragments.SongPlayingFragment1.Staticated.currentSongHelper1
import com.example.echo.fragments.SongPlayingFragment1.Staticated.endTimeText1
import com.example.echo.fragments.SongPlayingFragment1.Staticated.fab1
import com.example.echo.fragments.SongPlayingFragment1.Staticated.favoriteContent
import com.example.echo.fragments.SongPlayingFragment1.Staticated.fetchSongs1
import com.example.echo.fragments.SongPlayingFragment1.Staticated.glView1
import com.example.echo.fragments.SongPlayingFragment1.Staticated.loopImageButton1
import com.example.echo.fragments.SongPlayingFragment1.Staticated.mSensorListener1
import com.example.echo.fragments.SongPlayingFragment1.Staticated.mSensorManager
//import com.example.echo.fragments.SongPlayingFragment1.Staticated.mediaplayer
import com.example.echo.fragments.SongPlayingFragment1.Staticated.mediaplayer1
import com.example.echo.fragments.SongPlayingFragment1.Staticated.myActivity1
import com.example.echo.fragments.SongPlayingFragment1.Staticated.nextImageButton1
import com.example.echo.fragments.SongPlayingFragment1.Staticated.playpauseImageButton1
import com.example.echo.fragments.SongPlayingFragment1.Staticated.previousImageButton1
import com.example.echo.fragments.SongPlayingFragment1.Staticated.seekBar1
import com.example.echo.fragments.SongPlayingFragment1.Staticated.shuffleImageButton1
import com.example.echo.fragments.SongPlayingFragment1.Staticated.songArtistView1
import com.example.echo.fragments.SongPlayingFragment1.Staticated.songPlaying1
import com.example.echo.fragments.SongPlayingFragment1.Staticated.songTitleView1
import com.example.echo.fragments.SongPlayingFragment1.Staticated.startTimeText
import com.example.echo.fragments.SongPlayingFragment1.Staticated.updateSongTime1
//import com.example.echo.fragments.SongPlayingFragment.Staticated.audioVisualization
import com.example.echo.fragments.SongPlayingFragment1.Staticated.audioVisualization1
import com.example.echo.fragments.SongPlayingFragment1.Staticated.glView1
import kotlinx.android.synthetic.main.fragment_favourites.*
import kotlinx.android.synthetic.main.fragment_song_playing.*
import kotlinx.android.synthetic.main.fragment_song_playing.playPauseButton
import org.w3c.dom.Text
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.max
//import com.example.echo.fragments.SongPlayingFragment1.Staticated.audioVisualization as audio_Visualization


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class SongPlayingFragment1 : Fragment() {


    object Staticated {
        var myActivity1: Activity? = null
        var mediaplayer1: MediaPlayer? = null
        var startTimeText: TextView? = null
        var endTimeText1: TextView? = null
        var playpauseImageButton1: ImageButton? = null
        var previousImageButton1: ImageButton? = null
        var nextImageButton1: ImageButton? = null
        var loopImageButton1: ImageButton? = null
        var seekBar1: SeekBar? = null
        var songArtistView1: TextView? = null
        var songTitleView1: TextView? = null
        var shuffleImageButton1: ImageButton? = null
        var currentPosition1: Int = 0
        var fetchSongs1: ArrayList<Songs>? = null
        var currentSongHelper1: CurrentSongHelper? = null
        var audioVisualization1: AudioVisualization? = null
        var glView1: GLAudioVisualizationView? = null
        var songPlaying1: TextView? = null
        var fab1: ImageButton? = null
        var currentTrackPosition1: String? = null

        var favoriteContent: EchoDatabase? = null
        var mSensorManager: SensorManager? = null
        var mSensorListener1: SensorEventListener? = null
        var MY_PREFS_NAME1 = "Shake Feature"
        var updateSongTime1 = object : Runnable {
            override fun run() {
                val getcurrent = mediaplayer1?.currentPosition
                startTimeText?.setText(
                    String.format(
                        "%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(getcurrent?.toLong() as Long),
                        TimeUnit.MILLISECONDS.toSeconds(getcurrent?.toLong() as Long) -
                                TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getcurrent?.toLong() as Long))
                    )
                )
                seekBar1?.setProgress(getcurrent?.toInt() as Int)
                Handler().postDelayed(this, 1000)
            }
        }


    }


    object Statified {

        var MY_PREFS_SHUFFLE1 = "Shuffle feature"
        var My_PREFS_LOOP1 = "Loop feature"

        fun onSongComplete() {
            if (!(currentSongHelper1?.isShuffle)!!) {
                if (currentSongHelper1!!.isLoop) {
                    currentSongHelper1?.isPlaying = true
                    var nextSong = fetchSongs1?.get(currentPosition1)
                    SongPlayingFragment1.Staticated.currentTrackPosition1 = nextSong?.songTitle

                    if (nextSong?.artist.equals("<unknown>", true)) {
                        currentSongHelper1?.songArtist = "unknown"
                    } else {
                        currentSongHelper1?.songArtist = nextSong?.artist
                    }

                    currentSongHelper1?.songTitle = nextSong?.songTitle
                    currentSongHelper1?.songPath = nextSong?.songData
                    currentSongHelper1?.currentPosition = currentPosition1
                    currentSongHelper1?.songId = nextSong?.songID as Long
                    if (currentSongHelper1?.songId?.toInt()?.let { favoriteContent?.checkifIdExists(it) } as Boolean) {
                        fab1?.setImageDrawable(ContextCompat.getDrawable(Staticated.myActivity1 as Context, R.drawable.favorite_on))
                    }
                    mediaplayer1?.reset()
                    try {
                        mediaplayer1?.setDataSource(Staticated.myActivity1, Uri.parse(nextSong.songData))
                        mediaplayer1?.prepare()
                        mediaplayer1?.start()
                        songArtistView1?.text = nextSong.artist
                        songTitleView1?.text = nextSong.songTitle
                        processInformation(mediaplayer1 as MediaPlayer)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    currentSongHelper1?.isPlaying = true
                    playNex("PlayNextNormal")
                }
            } else {
                currentSongHelper1?.isPlaying = true
                playNex("playNextLikeNormalShuffle")
            }
        }

        //if (currentSongHelper?.isShuffle as Boolean) {
        //
        fun playNex(check: String) {
            if (check.equals("PlayNextNormal", true)) {
                currentPosition1 = currentPosition1 + 1
            } else if (check.equals("PlayNextLikeNormalShuffle", true)) {
                var randomObject = Random()
                var randomPosition = randomObject.nextInt(fetchSongs1?.size?.plus(1) as Int)
                currentPosition1 = randomPosition


            }
            if (currentPosition1 == fetchSongs1?.size) {
                currentPosition1 = 0
            }
            currentSongHelper1?.isLoop = false
            val nextSong = fetchSongs1?.get(currentPosition1)
            currentSongHelper1?.songPath = nextSong?.songData
            currentSongHelper1?.songTitle = nextSong?.songTitle
            currentSongHelper1?.currentPosition = currentPosition1
            currentSongHelper1?.songId = nextSong?.songID as Long
            updateTextViews(currentSongHelper1?.songTitle as String, currentSongHelper1?.songArtist as String)
            mediaplayer1?.reset()
            try {
                mediaplayer1?.setDataSource(Staticated.myActivity1, Uri.parse(currentSongHelper1?.songPath))
                mediaplayer1?.prepare()
                mediaplayer1?.start()
                processInformation(mediaplayer1 as MediaPlayer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (favoriteContent?.checkifIdExists(currentSongHelper1?.songId?.toInt() as Int) as Boolean) {
                fab1?.setImageDrawable(ContextCompat.getDrawable(Staticated.myActivity1!!, R.drawable.favorite_on))
            } else {
                fab1?.setImageDrawable(ContextCompat.getDrawable(Staticated.myActivity1!!, R.drawable.favorite_off))
            }

        }

        fun updateTextViews(songtitle: String, songartist: String) {

            songTitleView1?.setText(songtitle)
            songArtistView1?.setText(songartist)
        }

        fun processInformation(mediaPlayer: MediaPlayer) {
            val finalTime = mediaPlayer.duration
            val startTime = mediaPlayer.currentPosition
            seekBar1?.max = finalTime
            startTimeText?.setText(
                String.format(
                    "%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(startTime?.toLong() as Long),
                    TimeUnit.MILLISECONDS.toSeconds(startTime?.toLong() as Long) -
                            TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime?.toLong() as Long))
                )
            )
            endTimeText1?.setText(
                String.format(
                    "%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(finalTime?.toLong() as Long),
                    TimeUnit.MILLISECONDS.toSeconds(finalTime?.toLong() as Long) -
                            TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime?.toLong() as Long))
                )
            )
            seekBar1?.setProgress(startTime)
            Handler().postDelayed(updateSongTime1, 1000)
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
        val view = inflater!!.inflate(R.layout.fragment_song_playing2, container, false)
        setHasOptionsMenu(true)
        Staticated.seekBar1 = view?.findViewById(R.id.seekBar1)
        activity?.title = "Now Playing"
        startTimeText = view?.findViewById(R.id.startTime1)
        endTimeText1 = view?.findViewById(R.id.endTime1)
        playpauseImageButton1 = view?.findViewById(R.id.playPauseButton1)
        nextImageButton1 = view?.findViewById(R.id.nextButton1)
        previousImageButton1 = view?.findViewById(R.id.previousButton1)
        loopImageButton1 = view?.findViewById(R.id.loopButton1)
        shuffleImageButton1 = view?.findViewById(R.id.shuffleButton1)
        songArtistView1 = view?.findViewById(R.id.songArtist1)
        songTitleView1 = view?.findViewById(R.id.songTitle1)
        Staticated.glView1 = view?.findViewById(R.id.visualizer_view1)
        fab1 = view?.findViewById(R.id.favoriteIcon1)
        //fab?.alpha = 0.8f //to dim the effect
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        audioVisualization1 = Staticated.glView1 as AudioVisualization
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Staticated.myActivity1 = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        Staticated.myActivity1 = activity
    }

    override fun onResume() {
        super.onResume()
        audioVisualization1?.onResume()
        mSensorManager?.registerListener(mSensorListener1, mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL)

    }

    override fun onPause() {
        audioVisualization1?.onPause()
        super.onPause()
        mSensorManager?.unregisterListener(mSensorListener1)
    }

    override fun onDestroyView() {
        audioVisualization1?.release()
        super.onDestroyView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSensorManager = Staticated.myActivity1?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
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
                Staticated.myActivity1?.onBackPressed() // to return back to the page from which user just came
                return false
            }
        }
        return false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        favoriteContent = EchoDatabase(Staticated.myActivity1)

        currentSongHelper1 = CurrentSongHelper()
        currentSongHelper1?.isPlaying = true
        currentSongHelper1?.isLoop = false
        currentSongHelper1?.isShuffle = false
        var path: String? = null
        var _songTitle: String? = null
        var _songArtist: String? = null
        var songId: Long = 0
        try {
            path = arguments?.getString("path")
            _songTitle = arguments?.getString("songTitle")
            _songArtist = arguments?.getString("songArtist")
            songId = arguments?.getInt("songId")!!.toLong()
            currentPosition1 = arguments?.getInt("songPosition")!!
            fetchSongs1 = arguments?.getParcelableArrayList("songData")
            currentSongHelper1?.songPath = path
            currentSongHelper1?.songTitle = _songTitle
            currentSongHelper1?.songArtist = _songArtist
            currentSongHelper1?.songId = songId
            Statified.updateTextViews(currentSongHelper1?.songTitle as String, currentSongHelper1?.songArtist as String)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        var fromFavBottomBar = arguments?.get("FavBottomBar") as? String
        if(fromFavBottomBar != null){
            Staticated.mediaplayer1 = Favourites.Statified.mediaPlayer
        }else {
            mediaplayer1 = MediaPlayer()
            mediaplayer1?.setAudioStreamType(AudioManager.STREAM_MUSIC) // this tells that from audio manager stream only music
            try {
                mediaplayer1?.setDataSource(Staticated.myActivity1, Uri.parse(path))
                mediaplayer1?.prepare()

            } catch (e: Exception) {
                e.printStackTrace()
            }
            mediaplayer1?.start()
        }
        Statified.processInformation(mediaplayer1 as MediaPlayer)
        if (currentSongHelper1?.isPlaying as Boolean) {
            playpauseImageButton1?.setBackgroundResource(R.drawable.pause_icon)
            //playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            playpauseImageButton1?.setBackgroundResource(R.drawable.play_icon)
            //playPauseButton?.setBackgroundResource(R.drawable.play_icon)
        }
        mediaplayer1?.setOnCompletionListener {
            Statified.onSongComplete()
        }
        clickHandler()
        var visualizationHandler = DbmHandler.Factory.newVisualizerHandler(Staticated.myActivity1 as Context, 0)
        audioVisualization1?.linkTo(visualizationHandler)

        var prefsForShuffle = Staticated.myActivity1?.getSharedPreferences(Statified.MY_PREFS_SHUFFLE1, Context.MODE_PRIVATE)
        var isShuffleAllowed = prefsForShuffle?.getBoolean("feature", false)
        if (isShuffleAllowed as Boolean) {
            currentSongHelper1?.isShuffle = true
            currentSongHelper1?.isLoop = false
            shuffleImageButton1?.setBackgroundResource(R.drawable.shuffle_icon)
            loopImageButton1?.setBackgroundResource(R.drawable.loop_white_icon)
        } else {
            currentSongHelper1?.isShuffle = false
            shuffleImageButton1?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }
        var prefsForLoop = Staticated.myActivity1?.getSharedPreferences(Statified.MY_PREFS_SHUFFLE1, Context.MODE_PRIVATE)
        var isLoopAllowed = prefsForLoop?.getBoolean("feature", false)
        if (isLoopAllowed as Boolean) {
            currentSongHelper1?.isShuffle = false
            currentSongHelper1?.isLoop = true
            shuffleImageButton1?.setBackgroundResource(R.drawable.shuffle_white_icon)
            loopImageButton1?.setBackgroundResource(R.drawable.loop_icon)
        } else {
            currentSongHelper1?.isLoop = false
            shuffleImageButton1?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }
        if (favoriteContent?.checkifIdExists(currentSongHelper1?.songId?.toInt() as Int) as Boolean) {
            fab1?.setImageDrawable(ContextCompat.getDrawable(Staticated.myActivity1!!, R.drawable.favorite_on))
        } else {
            fab1?.setImageDrawable(ContextCompat.getDrawable(Staticated.myActivity1!!, R.drawable.favorite_off))
        }
    }

    fun clickHandler() {
        fab1?.setOnClickListener({
            if (favoriteContent?.checkifIdExists(currentSongHelper1?.songId?.toInt() as Int) as Boolean) {
                fab1?.setImageDrawable(ContextCompat.getDrawable(Staticated.myActivity1!!, R.drawable.favorite_off))
                favoriteContent?.deleteFavourite(currentSongHelper1?.songId?.toInt() as Int)
                Toast.makeText(Staticated.myActivity1,"Removed from favorites", Toast.LENGTH_SHORT).show()
            } else {
                fab1?.setImageDrawable(ContextCompat.getDrawable(Staticated.myActivity1!!, R.drawable.favorite_on))
                favoriteContent?.storeAsFavorite(currentSongHelper1?.songId?.toInt(),currentSongHelper1?.songArtist,currentSongHelper1?.songTitle,currentSongHelper1?.songPath)
                Toast.makeText(Staticated.myActivity1,"Added to favorites",Toast.LENGTH_SHORT).show()
            }
        })

        songPlaying1?.setOnClickListener({
            isSongPlaying()

        })

        shuffleImageButton1?.setOnClickListener({
            var editorShuffle =
                Staticated.myActivity1?.getSharedPreferences(Statified.MY_PREFS_SHUFFLE1, Context.MODE_PRIVATE)?.edit()
            var editorLoop = Staticated.myActivity1?.getSharedPreferences(Statified.My_PREFS_LOOP1, Context.MODE_PRIVATE)?.edit()
            if (currentSongHelper1?.isShuffle as Boolean) {
                shuffleImageButton1?.setBackgroundResource(R.drawable.shuffle_white_icon)
                currentSongHelper1?.isShuffle = false
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
            } else {
                currentSongHelper1?.isShuffle = true
                currentSongHelper1?.isLoop = false
                shuffleImageButton1?.setBackgroundResource(R.drawable.shuffle_icon)
                loopImageButton1?.setBackgroundResource(R.drawable.loop_white_icon)
                editorShuffle?.putBoolean("feature", true)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            }
        })
        nextImageButton1?.setOnClickListener({
            currentSongHelper1?.isPlaying = true
            playpauseImageButton1?.setBackgroundResource(R.drawable.pause_icon)
            if (currentSongHelper1?.isShuffle as Boolean) {
                Statified.playNex("PlayNextLikeNormalShuffle")
                loopImageButton1?.setBackgroundResource(R.drawable.loop_white_icon)

            } else {
                Statified.playNex("PlayNextNormal")
                loopImageButton1?.setBackgroundResource(R.drawable.loop_white_icon)
            }
        })
        previousImageButton1?.setOnClickListener({
            currentSongHelper1?.isPlaying = true
            if (currentSongHelper1?.isLoop as Boolean) {
                loopImageButton1?.setBackgroundResource(R.drawable.loop_white_icon)
            }
            playPrevious()
        })
        loopImageButton1?.setOnClickListener({
            var editorShuffle =
                Staticated.myActivity1?.getSharedPreferences(Statified.MY_PREFS_SHUFFLE1, Context.MODE_PRIVATE)?.edit()
            var editorLoop = Staticated.myActivity1?.getSharedPreferences(Statified.My_PREFS_LOOP1, Context.MODE_PRIVATE)?.edit()
            if (currentSongHelper1?.isLoop as Boolean) {
                currentSongHelper1?.isLoop = false
                loopImageButton1?.setBackgroundResource(R.drawable.loop_white_icon)
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
                //this is to turn of the loop
            } else {
                currentSongHelper1?.isLoop = true
                currentSongHelper1?.isShuffle = false
                false // when the loop feature is turned on the shuffle feature gets closed
                loopImageButton1?.setBackgroundResource(R.drawable.loop_icon)
                shuffleImageButton1?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature", true)
                editorLoop?.apply()
            }
        })
        playpauseImageButton1?.setOnClickListener({
            if (mediaplayer1?.isPlaying as Boolean) {
                mediaplayer1?.pause()
                currentSongHelper1?.isPlaying = false
                playpauseImageButton1?.setBackgroundResource(R.drawable.play_icon)
                Favourites.Statified.playPauseButton?.setBackgroundResource(R.drawable.play_icon)

            } else {
                mediaplayer1?.start()
                currentSongHelper1?.isPlaying = true
                playpauseImageButton1?.setBackgroundResource(R.drawable.pause_icon)
                Favourites.Statified.playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })
    }



    fun playPrevious() {
        currentPosition1 = currentPosition1 - 1
        if (currentPosition1 == -1) {
            currentPosition1 = 0
        }
        if (currentSongHelper1?.isPlaying as Boolean) {
            playpauseImageButton1?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            playpauseImageButton1?.setBackgroundResource(R.drawable.play_icon)
        }
        currentSongHelper1?.isLoop = false
        val nextSong = fetchSongs1?.get(currentPosition1)
        currentSongHelper1?.songPath = nextSong?.songData
        currentSongHelper1?.songTitle = nextSong?.songTitle
        currentSongHelper1?.currentPosition = currentPosition1
        currentSongHelper1?.songId = nextSong?.songID as Long
        Statified.updateTextViews(currentSongHelper1?.songTitle as String, currentSongHelper1?.songArtist as String)
        mediaplayer1?.reset()
        try {
            mediaplayer1?.setDataSource(Staticated.myActivity1, Uri.parse(currentSongHelper1?.songPath))
            mediaplayer1?.prepare()
            mediaplayer1?.start()
            Statified.processInformation(mediaplayer1 as MediaPlayer)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (favoriteContent?.checkifIdExists(currentSongHelper1?.songId?.toInt() as Int) as Boolean) {
            fab1?.setImageDrawable(ContextCompat.getDrawable(Staticated.myActivity1!!, R.drawable.favorite_on))
        } else {
            fab1?.setImageDrawable(ContextCompat.getDrawable(Staticated.myActivity1!!, R.drawable.favorite_off))
        }
    }
    fun isSongPlaying() {
        if (currentSongHelper1?.isPlaying as Boolean) {
            currentSongHelper1?.isPlaying = true
            mediaplayer1?.stop()

        } else {
            currentSongHelper1?.isPlaying
        }
    }
    fun bindShakeListener(){
        mSensorListener1 = object: SensorEventListener{
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
                    val prefs = Staticated.myActivity1?.getSharedPreferences(MY_PREFS_NAME1, Context.MODE_PRIVATE)
                    val isAllowed = prefs?.getBoolean("feature",false)
                    Statified.playNex("PlayNextNormal")
                }
            }


        }
    }
}
