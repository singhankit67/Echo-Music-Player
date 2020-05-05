package com.example.echo.fragments


import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.echo.R
import com.example.echo.Songs
import com.example.echo.adapters.MainScreenAdapter
import com.example.echo.fragments.MainScreenFragment.Staticated.nowPlayingBottomBar
import com.example.echo.fragments.SongPlayingFragment.Statified.playNext
import kotlinx.android.synthetic.main.fragment_favourites.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class MainScreenFragment : Fragment() {
    var getSongsList: ArrayList<Songs>? = null

    var playPauseButton: ImageButton? = null
    var Nextbutton: ImageButton? = null
    var Previousbutton: ImageButton? = null
    var songTitle: TextView? = null
    var visibleLayout: RelativeLayout? = null
    var noSongs: RelativeLayout? = null
    var recyclerView: RecyclerView? = null
    var trackPosition: Int = 0
    var myActivity: Activity? = null
    var _mainScreenAdapter: MainScreenAdapter? = null
    object Staticated{
        var nowPlayingBottomBar: RelativeLayout? = null
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater!!.inflate(R.layout.fragment_main_screen, container, false)
        setHasOptionsMenu(true)
        activity?.title = "All songs"
        visibleLayout = (view?.findViewById<RelativeLayout>(R.id.visibleLayout)as RelativeLayout)
        noSongs = view?.findViewById<RelativeLayout>(R.id.noSongs) as RelativeLayout
        nowPlayingBottomBar = view?.findViewById<RelativeLayout>(R.id.hiddenBarMainScreen)as RelativeLayout
        songTitle = view?.findViewById<TextView>(R.id.songTitleMainScreen) as TextView
        playPauseButton = view?.findViewById<ImageButton>(R.id.playPauseButton) as ImageButton
        Nextbutton = view?.findViewById<ImageButton>(R.id.nextbuttons)
        Previousbutton = view?.findViewById<ImageButton>(R.id.previousbuttons)
        (nowPlayingBottomBar as RelativeLayout).isClickable = false
        recyclerView = view?.findViewById<RecyclerView>(R.id.contentMain)as RecyclerView
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getSongsList = getSongsfromPhone() //this is how we get list of songs frm phone
        val prefs = activity?.getSharedPreferences("action_sort",Context.MODE_PRIVATE)
        val action_sort_ascending = prefs?.getString("action_sort_ascending","true")
        val action_sort_recent = prefs?.getString("action_sort_recent","false")

        if(getSongsList == null){
            visibleLayout?.visibility = View.INVISIBLE
            noSongs?.visibility = View.VISIBLE
        }else {
            _mainScreenAdapter = MainScreenAdapter(getSongsList as ArrayList<Songs>, myActivity as Context)
            val mLayoutManager = LinearLayoutManager(myActivity)
            recyclerView?.layoutManager = mLayoutManager
            recyclerView?.itemAnimator = DefaultItemAnimator()
            recyclerView?.adapter = _mainScreenAdapter
        }

        if(getSongsList!=null){
            if(action_sort_ascending!!.equals("true",ignoreCase = true)){
                Collections.sort(getSongsList,Songs.Statified.nameComparator)
                _mainScreenAdapter?.notifyDataSetChanged()
            }else if(action_sort_recent!!.equals("true", ignoreCase = true)){
                Collections.sort(getSongsList,Songs.Statified.dateComparator)
                _mainScreenAdapter?.notifyDataSetChanged()
            }
        }
        bottomBarSetup()

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.main,menu)
        return
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val switcher = item?.itemId
        if(switcher == R.id.action_sort_ascending){
            val editor = myActivity?.getSharedPreferences("action_sort",Context.MODE_PRIVATE)?.edit()
            editor?.putString("action_sort_ascending","true")
            editor?.putString("action-sort_recent","false")
            editor?.apply()
            if(getSongsList!=null){
                Collections.sort(getSongsList,Songs.Statified.nameComparator)
            }
            _mainScreenAdapter?.notifyDataSetChanged()
            return false

        }else if(switcher == R.id.action_sort_recent){
            val editortwo = myActivity?.getSharedPreferences("action_sort",Context.MODE_PRIVATE)?.edit()
            editortwo?.putString("action_sort_ascending","false")
            editortwo?.putString("action-sort_recent","true")
            editortwo?.apply()
            if(getSongsList!=null){
                Collections.sort(getSongsList,Songs.Statified.dateComparator)
            }
            _mainScreenAdapter?.notifyDataSetChanged()
            return false

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    /*fun getSongsFromPhone(): ArrayList<Songs> {
        var arrayList = ArrayList<Songs>()  //here we created a object of arraylist
        var contentResolver = myActivity?.contentResolver
        var songUri =
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI //this is done toidentify the media files in our system and identify them as songs created a content resolver to access the database
        var songCursor = contentResolver?.query(songUri, null, null, null, null) //this is done to explore the database
        if (songCursor != null && songCursor.moveToFirst()) { //this specifies that to fetch data if the songcursor hs some value and to start from the first data always
            val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            //this is us uering the database or returning t hat particular columnfrom the database
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            //now to move to next song till the last song is not reached
            while (songCursor.moveToNext()) {
                var currentId = songCursor.getLong(songId)
                var currentTitle = songCursor.getString(songTitle)
                var currentArtist = songCursor.getString(songArtist)
                var currentData = songCursor.getString(songData)
                var currentDate = songCursor.getLong(dateIndex)
                arrayList.add(Songs(currentId, currentTitle, currentArtist, currentData, currentDate))


            }


        }
        return arrayList
    }*/
    private fun getSongsfromPhone(): ArrayList<Songs> {
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
        }
        return arrayList
    }
    fun bottomBarSetup() {
        try {
            bottomBarClickHandler()
            songTitle?.setText(SongPlayingFragment.Staticated.currentSongHelper?.songTitle)
            SongPlayingFragment.Staticated.mediaplayer?.setOnCompletionListener({
                songTitle?.setText(SongPlayingFragment.Staticated.currentSongHelper?.songTitle)
                SongPlayingFragment.Statified.onSongComplete()
            })

            if (SongPlayingFragment.Staticated.mediaplayer?.isPlaying as Boolean) {
                nowPlayingBottomBar?.visibility = View.VISIBLE
            } else {
                nowPlayingBottomBar?.visibility = View.INVISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //try {
            //bottomBarClickHandler()
            //songTitle?.setText(SongPlayingFragment1.Staticated.currentSongHelper1?.songTitle)
            //SongPlayingFragment1.Staticated.mediaplayer1?.setOnCompletionListener({
                //songTitle?.setText(SongPlayingFragment1.Staticated.currentSongHelper1?.songTitle)
                //SongPlayingFragment1.Statified.onSongComplete()
            //})

            //if (SongPlayingFragment1.Staticated.mediaplayer1?.isPlaying as Boolean) {
                //nowPlayingBottomBar?.visibility = View.VISIBLE
            //} else {
                //nowPlayingBottomBar?.visibility = View.INVISIBLE
            //}} catch (e: Exception) {
            //e.printStackTrace()
        //}
        //onSongComplete()
    }
    fun bottomBarClickHandler() {
        nowPlayingBottomBar?.setOnClickListener({
            Favourites.Statified.mediaPlayer = SongPlayingFragment.Staticated.mediaplayer
            val songPlayingFragment = SongPlayingFragment()
            val args = Bundle()
            args.putString("songArtist", SongPlayingFragment.Staticated.currentSongHelper?.songArtist)
            args.putString("path", SongPlayingFragment.Staticated.currentSongHelper?.songPath)
            args.putString("songTitle", SongPlayingFragment.Staticated.currentSongHelper?.songTitle)
            args.putInt("songId", SongPlayingFragment.Staticated.currentSongHelper?.songId?.toInt() as Int)
            args.putInt(
                "songPosition",
                SongPlayingFragment.Staticated.currentSongHelper?.currentPosition?.toInt() as Int
            )
            args.putParcelableArrayList("songData", SongPlayingFragment.Staticated.fetchSongs)
            args.putString("FavBottomBar", "success")
            songPlayingFragment.arguments = args
            fragmentManager?.beginTransaction()
                ?.replace(R.id.details_fragment, songPlayingFragment)
                ?.addToBackStack("songPlayingFragmentFavorite")
                ?.commit()

        })
        playPauseButton?.setOnClickListener({
            if (SongPlayingFragment.Staticated.mediaplayer?.isPlaying as Boolean) {
                SongPlayingFragment.Staticated.mediaplayer?.pause()
                trackPosition = SongPlayingFragment.Staticated.mediaplayer?.getCurrentPosition() as Int
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
                //playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                SongPlayingFragment.Staticated.mediaplayer?.seekTo(trackPosition)
                SongPlayingFragment.Staticated.mediaplayer?.start()
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
                //playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })
        Nextbutton?.setOnClickListener({
            playNext()
            playPauseButton?.setBackgroundResource(R.drawable.pause_icon)

        })
        Previousbutton?.setOnClickListener({
            playPrevious()
            playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
        })


    }
    fun playPrevious() {
        SongPlayingFragment.Staticated.currentPosition = SongPlayingFragment.Staticated.currentPosition - 1
        if (SongPlayingFragment.Staticated.currentPosition == -1) {
            SongPlayingFragment.Staticated.currentPosition = 0
        }
        if (SongPlayingFragment.Staticated.currentSongHelper?.isPlaying as Boolean) {
            SongPlayingFragment.Staticated.playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            SongPlayingFragment.Staticated.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }
        SongPlayingFragment.Staticated.currentSongHelper?.isLoop = false
        val nextSong = SongPlayingFragment.Staticated.fetchSongs?.get(SongPlayingFragment.Staticated.currentPosition)
        SongPlayingFragment.Staticated.currentSongHelper?.songPath = nextSong?.songData
        SongPlayingFragment.Staticated.currentSongHelper?.songTitle = nextSong?.songTitle
        SongPlayingFragment.Staticated.currentSongHelper?.currentPosition =
            SongPlayingFragment.Staticated.currentPosition
        SongPlayingFragment.Staticated.currentSongHelper?.songId = nextSong?.songID as Long
        SongPlayingFragment.Statified.updateTextViews(

            SongPlayingFragment.Staticated.currentSongHelper?.songTitle as String,
            SongPlayingFragment.Staticated.currentSongHelper?.songArtist as String)
        SongPlayingFragment.Staticated.mediaplayer?.reset()
        try {
            SongPlayingFragment.Staticated.mediaplayer?.setDataSource(
                SongPlayingFragment.Staticated.myActivity, Uri.parse(
                    SongPlayingFragment.Staticated.currentSongHelper?.songPath))
            SongPlayingFragment.Staticated.mediaplayer?.prepare()
            SongPlayingFragment.Staticated.mediaplayer?.start()
            SongPlayingFragment.Statified.processInformation(SongPlayingFragment.Staticated.mediaplayer as MediaPlayer)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun playNext() {
        SongPlayingFragment.Staticated.currentPosition = SongPlayingFragment.Staticated.currentPosition + 1
        if (SongPlayingFragment.Staticated.currentPosition == SongPlayingFragment.Staticated.fetchSongs?.size) {
            SongPlayingFragment.Staticated.currentPosition = 0
        }
        if (SongPlayingFragment.Staticated.currentSongHelper?.isPlaying as Boolean) {
            SongPlayingFragment.Staticated.playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            SongPlayingFragment.Staticated.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }
        SongPlayingFragment.Staticated.currentSongHelper?.isLoop = false
        val nextSong = SongPlayingFragment.Staticated.fetchSongs?.get(SongPlayingFragment.Staticated.currentPosition)
        SongPlayingFragment.Staticated.currentSongHelper?.songPath = nextSong?.songData
        SongPlayingFragment.Staticated.currentSongHelper?.songTitle = nextSong?.songTitle
        SongPlayingFragment.Staticated.currentSongHelper?.currentPosition =
            SongPlayingFragment.Staticated.currentPosition
        SongPlayingFragment.Staticated.currentSongHelper?.songId = nextSong?.songID as Long
        SongPlayingFragment.Statified.updateTextViews(

            SongPlayingFragment.Staticated.currentSongHelper?.songTitle as String,
            SongPlayingFragment.Staticated.currentSongHelper?.songArtist as String)
        SongPlayingFragment.Staticated.mediaplayer?.reset()
        try {
            SongPlayingFragment.Staticated.mediaplayer?.setDataSource(
                SongPlayingFragment.Staticated.myActivity, Uri.parse(
                    SongPlayingFragment.Staticated.currentSongHelper?.songPath))
            SongPlayingFragment.Staticated.mediaplayer?.prepare()
            SongPlayingFragment.Staticated.mediaplayer?.start()
            SongPlayingFragment.Statified.processInformation(SongPlayingFragment.Staticated.mediaplayer as MediaPlayer)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
