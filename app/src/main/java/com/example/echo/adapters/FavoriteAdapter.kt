package com.example.echo.adapters

/*import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.echo.R
import com.example.echo.Songs
import com.example.echo.fragments.SongPlayingFragment

class FavoriteAdapter(_songDetails: ArrayList<Songs>, _context: Context)
    : RecyclerView.Adapter<FavoriteAdapter.MyViewHolder>() {

    var songDetails: ArrayList<Songs>? = null
    var mContext: Context? = null

    init {
        this.songDetails = _songDetails
        this.mContext = _context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val songObject = songDetails?.get(position)
        if(songObject?.songTitle!=null)
            holder.trackTitle?.setText(songObject?.songTitle)
        if(songObject?.artist!=null)
            holder.trackArtist?.setText(songObject?.artist)
        holder.contentHolder?.setOnClickListener({

            val songPlayingFragment= SongPlayingFragment()
            val args = Bundle()
            args?.putString("songArtist",songObject?.artist)
            args?.putString("path",songObject?.songData)
            args?.putString("songTitle",songObject?.songTitle)
            args?.putInt("songId",songObject?.songID?.toInt() as Int)
            args?.putInt("songPosition",position)
            args?.putParcelableArrayList("songData",songDetails)
            songPlayingFragment.arguments=args
            (mContext as FragmentActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.details_fragment,songPlayingFragment)
                .addToBackStack("SongPlayingFragment")
                .commit()


        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            MyViewHolder {

        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_custom_mainscreen_adapter, parent, false)
        return MyViewHolder(itemView)

    }

    override fun getItemCount(): Int {
        if (songDetails == null) {
            return 0
        } else {
            return (songDetails as ArrayList<Songs>).size
        }
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var trackTitle: TextView? = null
        var trackArtist: TextView? = null
        var contentHolder: RelativeLayout? = null

        init {
            trackTitle = view.findViewById<TextView>(R.id.trackTitle)
            trackArtist = view.findViewById<TextView>(R.id.trackArtist)
            contentHolder = view.findViewById<RelativeLayout>(R.id.contentRow)

        }


    }
}*/
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.contentValuesOf
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.echo.R
import com.example.echo.Songs
import com.example.echo.activites.MainActivity
import com.example.echo.fragments.MainScreenFragment
import com.example.echo.fragments.SongPlayingFragment
//import com.example.echo.fragments.SongPlayingFragment
import com.example.echo.fragments.SongPlayingFragment1

class FavoriteAdapter (_songDetails: ArrayList<Songs>, _context: Context)
    : RecyclerView.Adapter<FavoriteAdapter.MyViewHolder>() {

    var songDetails: ArrayList<Songs>? = null
    var mContext: Context? = null

    init {
        this.songDetails = _songDetails
        this.mContext = _context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val songObject = songDetails?.get(position)
        if(songObject?.songTitle!=null)
            holder.trackTitle?.setText(songObject?.songTitle)
        if(songObject?.artist!=null)
            holder.trackArtist?.setText(songObject?.artist)
        holder.contentHolder?.setOnClickListener({

            val songPlayingFragment1=SongPlayingFragment1()
            val args = Bundle()
            args?.putString("songArtist",songObject?.artist)
            args?.putString("path",songObject?.songData)
            args?.putString("songTitle",songObject?.songTitle)
            args?.putInt("songId",songObject?.songID?.toInt() as Int)
            args?.putInt("songPosition",position)
            args?.putParcelableArrayList("songData",songDetails)
            songPlayingFragment1.arguments = args
            (mContext as FragmentActivity).supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.details_fragment,songPlayingFragment1)
                ?.addToBackStack("SongPlayingFragment1")
                ?.commit()
            try {

                if (SongPlayingFragment1.Staticated.mediaplayer1?.isPlaying as Boolean ) {

                    SongPlayingFragment1.Staticated.mediaplayer1?.stop()

                }

            } catch (e: Exception) {

                e.printStackTrace()
            }
            try {

                if (SongPlayingFragment.Staticated?.mediaplayer?.isPlaying as Boolean ) {

                    SongPlayingFragment.Staticated.mediaplayer?.stop()

                }

            } catch (e: Exception) {

                e.printStackTrace()
            }


        })







    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            MyViewHolder {

        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_custom_mainscreen_adapter, parent, false)
        return MyViewHolder(itemView)

    }

    override fun getItemCount(): Int {
        if (songDetails == null) {
            return 0
        } else {
            return (songDetails as ArrayList<Songs>).size
        }
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var trackTitle: TextView? = null
        var trackArtist: TextView? = null
        var contentHolder: RelativeLayout? = null

        init {
            trackTitle = view.findViewById<TextView>(R.id.trackTitle)
            trackArtist = view.findViewById<TextView>(R.id.trackArtist)
            contentHolder = view.findViewById<RelativeLayout>(R.id.contentRow)

        }


    }
}