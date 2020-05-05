@file:Suppress("DEPRECATION")

package com.example.echo.fragments

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.echo.R
import com.example.echo.Songs
import com.example.echo.adapters.FavoriteAdapter
import com.example.echo.databases.EchoDatabase
//import com.example.echo.fragments.SongPlayingFragment.Staticated.fetchSongs
import com.example.echo.CurrentSongHelper
import com.example.echo.fragments.Favourites.Statified.playPauseButton
//import com.example.echo.fragments.MainScreenFragment.Staticated.nowPlayingBottomBar
import com.example.echo.fragments.SongPlayingFragment.Statified.onSongComplete
import com.example.echo.fragments.SongPlayingFragment.Staticated.playpauseImageButton
//import com.example.echo.fragments.SongPlayingFragment1.Staticated.fetchSongs1
import com.example.echo.fragments.SongPlayingFragment.Staticated.fetchSongs
import kotlinx.android.synthetic.main.fragment_favourites.*
import kotlinx.android.synthetic.main.fragment_favourites.view.*
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class Favourites : Fragment() {
    var myActivity: Activity? = null

    var noFavorites: TextView? = null
    var nowPlayingBottomBar1: RelativeLayout? = null
    var songTitle: TextView? = null

    var recyclerView: RecyclerView? = null
    var trackPosition: Int = 0
    var favoriteContent: EchoDatabase? = null
    var refreshList: ArrayList<Songs>? = null
    var getListfromDatabase: ArrayList<Songs>? = null
    var NextButton: ImageButton? = null
    var PreviousButton: ImageButton? = null

    object Statified {
        var mediaPlayer: MediaPlayer? = null
        var playPauseButton: ImageButton? = null
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater. inflate(R.layout.fragment_favourites, container, false)
        activity?.title = "Favourites"
        noFavorites = view?.findViewById(R.id.noFavorites)
        nowPlayingBottomBar1 = view?.findViewById(R.id.hiddenBarFavScreen)
        songTitle = view?.findViewById(R.id.songTitleFavScreen)
        Statified.playPauseButton = view?.findViewById(R.id.playPauseButton)
        recyclerView = view?.findViewById(R.id.favoriteRecycler) as RecyclerView
        NextButton = view?.findViewById(R.id.NextButton)
        PreviousButton = view?.findViewById(R.id.PreviousButton)
        favoriteContent = EchoDatabase(myActivity)
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        display_favorites_by_searching()
        bottomBarSetup()

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
    }

    private fun getSongsfromPhone(): ArrayList<Songs>? {
        val arrayList = ArrayList<Songs>()
        val contentResolver = activity?.contentResolver
        val songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val songCursor = contentResolver?.query(songUri, null, null, null, null)
        if (songCursor != null && songCursor.moveToFirst()) {
            val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            while (songCursor.moveToNext()) {
                val currentId = songCursor.getLong(songId)
                val currentTitle = songCursor.getString(songTitle)
                val currentArtist = songCursor.getString(songArtist)
                val currentData = songCursor.getString(songData)
                val currentDate = songCursor.getLong(dateIndex)
                arrayList.add(Songs(currentId, currentTitle, currentArtist, currentData, currentDate))
            }
        }else {return null
        }
        return arrayList
    }

    fun bottomBarSetup() {
        try {
            bottomBarClickHandler()
            songTitle?.setText(SongPlayingFragment1.Staticated.currentSongHelper1?.songTitle)
            SongPlayingFragment1.Staticated.mediaplayer1?.setOnCompletionListener( {
                songTitle?.setText(SongPlayingFragment1.Staticated.currentSongHelper1?.songTitle)
                SongPlayingFragment1.Statified.onSongComplete()
            })
            if (SongPlayingFragment1.Staticated.mediaplayer1?.isPlaying as Boolean) {
                nowPlayingBottomBar1?.visibility = View.VISIBLE
            } else {
                nowPlayingBottomBar1?.visibility = View.INVISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //try {
            //bottomBarClickHandler()
            //songTitle?.setText(SongPlayingFragment.Staticated.currentSongHelper?.songTitle)
            //SongPlayingFragment.Staticated.mediaplayer?.setOnCompletionListener( {
                //songTitle?.setText(SongPlayingFragment.Staticated.currentSongHelper?.songTitle)
                //SongPlayingFragment.Statified.onSongComplete()
            //})
            //if (SongPlayingFragment.Staticated.mediaplayer?.isPlaying as Boolean) {
                //nowPlayingBottomBar1?.visibility = View.VISIBLE
            //} else {
                //nowPlayingBottomBar1?.visibility = View.INVISIBLE
            //}
        //} catch (e: Exception) {
            //e.printStackTrace()
        //}
        //onSongComplete()
    }

    fun bottomBarClickHandler() {
        nowPlayingBottomBar1?.setOnClickListener({
            Statified.mediaPlayer = SongPlayingFragment1.Staticated.mediaplayer1
            val songPlayingFragment2 = SongPlayingFragment1()
            val args = Bundle()
            args?.putString("songArtist", SongPlayingFragment1.Staticated.currentSongHelper1?.songArtist)
            args?.putString("path", SongPlayingFragment1.Staticated.currentSongHelper1?.songPath)
            args?.putString("songTitle", SongPlayingFragment1.Staticated.currentSongHelper1?.songTitle)
            args?.putInt("songId", SongPlayingFragment1.Staticated.currentSongHelper1?.songId?.toInt() as Int)
            args?.putInt("songPosition", SongPlayingFragment1.Staticated.currentSongHelper1?.currentPosition?.toInt() as Int)
            args?.putParcelableArrayList("songData", SongPlayingFragment1.Staticated.fetchSongs1)
            args?.putString("FavBottomBar", "success")
            songPlayingFragment2?.arguments = args
            fragmentManager?.beginTransaction()
                ?.replace(R.id.details_fragment, songPlayingFragment2)
                ?.addToBackStack("songPlayingFragment2")
                ?.commit()

        })
        Statified.playPauseButton?.setOnClickListener({
            if (SongPlayingFragment1.Staticated.mediaplayer1?.isPlaying as Boolean) {
                SongPlayingFragment1.Staticated.mediaplayer1?.pause()
                trackPosition = SongPlayingFragment1.Staticated.mediaplayer1?.getCurrentPosition() as Int
                SongPlayingFragment1.Staticated.playpauseImageButton1?.setBackgroundResource(R.drawable.play_icon)
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
                //
                //playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                SongPlayingFragment1.Staticated.mediaplayer1?.seekTo(trackPosition)
                SongPlayingFragment1.Staticated.mediaplayer1?.start()
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
                //SongPlayingFragment.Staticated.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                //playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })
        NextButton?.setOnClickListener({
            playNext()
            playPauseButton?.setBackgroundResource(R.drawable.pause_icon)

        })
        PreviousButton?.setOnClickListener({
            playPrevious()
            playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
        })


    }



    fun display_favorites_by_searching() {
        if (favoriteContent?.checkSize() as Int > 0) {
            refreshList = ArrayList<Songs>()
            getListfromDatabase = favoriteContent?.queryDBList()
            var fetchListfromDevice = getSongsfromPhone()
            if (fetchListfromDevice != null) {
                for (i in 0..fetchListfromDevice?.size - 1) {
                    for (j in 0..getListfromDatabase?.size as Int - 1) {
                        if (getListfromDatabase?.get(j)?.songID === fetchListfromDevice?.get(i)?.songID) {
                            refreshList?.add((getListfromDatabase as ArrayList<Songs>)[j])
                        }
                    }
                }

            } else {

            }
            if (refreshList == null) {
                recyclerView?.visibility = View.INVISIBLE
                noFavorites?.visibility = View.VISIBLE
            } else {
                var favoriteAdapter = FavoriteAdapter(refreshList as ArrayList<Songs>, myActivity as Context)
                val mLayoutManager = LinearLayoutManager(activity)
                recyclerView?.layoutManager = mLayoutManager
                recyclerView?.itemAnimator = DefaultItemAnimator()
                recyclerView?.adapter = favoriteAdapter
                recyclerView?.setHasFixedSize(true)
            }

        } else {
            recyclerView?.visibility = View.INVISIBLE
            noFavorites?.visibility = View.VISIBLE
        }


    }
    fun playPrevious() {
        SongPlayingFragment1.Staticated.currentPosition1 = SongPlayingFragment1.Staticated.currentPosition1 - 1
        if (SongPlayingFragment1.Staticated.currentPosition1 == -1) {
            SongPlayingFragment1.Staticated.currentPosition1 = 0
        }
        if (SongPlayingFragment1.Staticated.currentSongHelper1?.isPlaying as Boolean) {
            SongPlayingFragment1.Staticated.playpauseImageButton1?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            SongPlayingFragment1.Staticated.playpauseImageButton1?.setBackgroundResource(R.drawable.play_icon)
        }
        SongPlayingFragment1.Staticated.currentSongHelper1?.isLoop = false
        val nextSong = SongPlayingFragment1.Staticated.fetchSongs1?.get(SongPlayingFragment1.Staticated.currentPosition1)
        SongPlayingFragment1.Staticated.currentSongHelper1?.songPath = nextSong?.songData
        SongPlayingFragment1.Staticated.currentSongHelper1?.songTitle = nextSong?.songTitle
        SongPlayingFragment1.Staticated.currentSongHelper1?.currentPosition =
            SongPlayingFragment1.Staticated.currentPosition1
        SongPlayingFragment1.Staticated.currentSongHelper1?.songId = nextSong?.songID as Long
        SongPlayingFragment1.Statified.updateTextViews(

            SongPlayingFragment1.Staticated.currentSongHelper1?.songTitle as String,
            SongPlayingFragment1.Staticated.currentSongHelper1?.songArtist as String)
        SongPlayingFragment1.Staticated.mediaplayer1?.reset()
        try {
            SongPlayingFragment1.Staticated.mediaplayer1?.setDataSource(
                SongPlayingFragment1.Staticated.myActivity1, Uri.parse(
                    SongPlayingFragment1.Staticated.currentSongHelper1?.songPath))
            SongPlayingFragment1.Staticated.mediaplayer1?.prepare()
            SongPlayingFragment1.Staticated.mediaplayer1?.start()
            SongPlayingFragment1.Statified.processInformation(SongPlayingFragment1.Staticated.mediaplayer1 as MediaPlayer)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun playNext() {
        SongPlayingFragment1.Staticated.currentPosition1 = SongPlayingFragment1.Staticated.currentPosition1 + 1
        if (SongPlayingFragment1.Staticated.currentPosition1 == fetchSongs?.size) {
            SongPlayingFragment1.Staticated.currentPosition1 = 0
        }
        if (SongPlayingFragment1.Staticated.currentSongHelper1?.isPlaying as Boolean) {
            SongPlayingFragment1.Staticated.playpauseImageButton1?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            SongPlayingFragment1.Staticated.playpauseImageButton1?.setBackgroundResource(R.drawable.play_icon)
        }
        SongPlayingFragment1.Staticated.currentSongHelper1?.isLoop = false
        val nextSong = SongPlayingFragment1.Staticated.fetchSongs1?.get(SongPlayingFragment1.Staticated.currentPosition1)
        SongPlayingFragment1.Staticated.currentSongHelper1?.songPath = nextSong?.songData
        SongPlayingFragment1.Staticated.currentSongHelper1?.songTitle = nextSong?.songTitle
        SongPlayingFragment1.Staticated.currentSongHelper1?.currentPosition =
            SongPlayingFragment1.Staticated.currentPosition1
        SongPlayingFragment1.Staticated.currentSongHelper1?.songId = nextSong?.songID as Long
        SongPlayingFragment1.Statified.updateTextViews(

            SongPlayingFragment1.Staticated.currentSongHelper1?.songTitle as String,
            SongPlayingFragment1.Staticated.currentSongHelper1?.songArtist as String)
        SongPlayingFragment1.Staticated.mediaplayer1?.reset()
        try {
            SongPlayingFragment1.Staticated.mediaplayer1?.setDataSource(
                SongPlayingFragment1.Staticated.myActivity1, Uri.parse(
                    SongPlayingFragment1.Staticated.currentSongHelper1?.songPath))
            SongPlayingFragment1.Staticated.mediaplayer1?.prepare()
            SongPlayingFragment1.Staticated.mediaplayer1?.start()
            SongPlayingFragment1.Statified.processInformation(SongPlayingFragment1.Staticated.mediaplayer1 as MediaPlayer)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}