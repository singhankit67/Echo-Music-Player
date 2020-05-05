package com.example.echo.activites

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.echo.R

import com.example.echo.adapters.NavigationDrawerAdapter
import com.example.echo.fragments.MainScreenFragment
import com.example.echo.fragments.SongPlayingFragment
import kotlinx.android.synthetic.main.fragment_song_playing.*
import kotlinx.android.synthetic.main.row_custom_mainscreen_adapter.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    var navigationDrawerIconsList: ArrayList<String> = arrayListOf()
    var images_for_navdrawer = intArrayOf(
        R.drawable.navigation_allsongs,
        R.drawable.navigation_favorites,
        R.drawable.navigation_settings,
        R.drawable.navigation_aboutus
    )
    var trackNotificationBuilder: Notification.Builder? = null
    var notificationChannel: NotificationChannel? = null
    //private val channelid = "com.example.echo.activites"
    //private val description = "Test Notification"

    object Statified {
        var drawerLayout: DrawerLayout? = null
        var notificationManager: NotificationManager? = null
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?/*, persistentState: PersistableBundle?*/) {
        super.onCreate(savedInstanceState/*, persistentState*/)
        setContentView(R.layout.activity_main) //this way we link the main activity tothe oncreate function
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)
        MainActivity.Statified.drawerLayout = findViewById(R.id.drawer_layout)
        navigationDrawerIconsList.add("All Songs")
        navigationDrawerIconsList.add("Favorites")
        navigationDrawerIconsList.add("Settings")
        navigationDrawerIconsList.add("About us")
        val toggle = ActionBarDrawerToggle(
            this@MainActivity, MainActivity.Statified.drawerLayout, toolbar,
            R.string.navigation_drawer_0,
            R.string.navigation_drawer_c
        )
        MainActivity.Statified.drawerLayout?.setDrawerListener(toggle)
        toggle.syncState()

        val mainScreenFragement = MainScreenFragment() //to create a new class
        this.supportFragmentManager
            .beginTransaction()
            .add(R.id.details_fragment, mainScreenFragement, "MainScreenFragment")
            .commit()//this is bascially done to intialize a fragment and to manage all the fragments and all the functions associated with the fragments
        //to apply the changes we made in our class we use commit

        var _navigationAdapter = NavigationDrawerAdapter(navigationDrawerIconsList, images_for_navdrawer, this)
        _navigationAdapter.notifyDataSetChanged()

        var navigation_recycler_view = findViewById<RecyclerView>(R.id.navigation_recycler_view)
        navigation_recycler_view.layoutManager = LinearLayoutManager(this)
        navigation_recycler_view.itemAnimator = DefaultItemAnimator()
        navigation_recycler_view.adapter = _navigationAdapter
        navigation_recycler_view.setHasFixedSize(true)
        //Statified.notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        /*contentRow.setOnHoverListener({

            val intent = Intent(this,LauncherActivity::class.java)
            val pintent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationChannel = NotificationChannel(channelid,description,NotificationManager.IMPORTANCE_HIGH)
                notificationChannel!!.enableLights(true)
                notificationChannel!!.lightColor = Color.GREEN
                notificationChannel!!.enableVibration(false)
                Statified.notificationManager!!.createNotificationChannel(notificationChannel)
                trackNotificationBuilder = Notification.Builder(this,channelid)
                    .setContentTitle("Code Android")
                    .setContentText("Echo App")
                    .setSmallIcon(R.drawable.echo_icon)
                    .setLargeIcon(BitmapFactory.decodeResource(this.resources,R.drawable.echo_icon))
                    .setContentIntent(pintent)


            }else{
                trackNotificationBuilder = Notification.Builder(this)
                    .setContentTitle("Code Android")
                    .setContentText("Echo App")
                    .setSmallIcon(R.drawable.echo_icon)
                    .setLargeIcon(BitmapFactory.decodeResource(this.resources,R.drawable.echo_icon))
                    .setContentIntent(pintent)

            }
            Statified.notificationManager!!.notify(1234,trackNotificationBuilder!!.build())

        })
    }*/


        //override fun onStart() {
        //super.onStart()
        //try{
        //Statified.notificationManager?.cancel(1978)
        //}catch(e: Exception){
        //e.printStackTrace()
        //}
    }
}

    //override fun onStop() {
        //super.onStop()
        //try{
            //if(SongPlayingFragment.Staticated.mediaplayer?.isPlaying as Boolean){
                //Statified.notificationManager?.notify(1978)

            //}
        //}catch(e:Exception){
            //e.printStackTrace()
        //}
    //}

    //override fun onResume() {
        //super.onResume()
        //try{
            //Statified.notificationManager?.cancel(1978)
        //}catch(e: Exception){
            //e.printStackTrace()
        //}
    //}
    //}
