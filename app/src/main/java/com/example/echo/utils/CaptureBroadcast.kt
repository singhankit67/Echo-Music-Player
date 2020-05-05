package com.example.echo.utils

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.example.echo.R
import com.example.echo.activites.MainActivity
import com.example.echo.fragments.SongPlayingFragment
import java.lang.Exception

class CaptureBroadcast : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if(p1?.action == Intent.ACTION_NEW_OUTGOING_CALL){
            try{
                MainActivity.Statified.notificationManager?.cancel(1978)
            }catch(e: Exception){
                e.printStackTrace()
            }
            try {
                if (SongPlayingFragment.Staticated.mediaplayer?.isPlaying as Boolean) {
                    SongPlayingFragment.Staticated.mediaplayer?.pause()
                    SongPlayingFragment.Staticated.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                }
            }catch(e: Exception){
                e.printStackTrace()
            }
        }else{
            val tm: TelephonyManager = p0?.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
            when(tm?.callState){
                TelephonyManager.CALL_STATE_RINGING ->{
                    try{
                        MainActivity.Statified.notificationManager?.cancel(1978)
                    }catch(e: Exception){
                        e.printStackTrace()
                    }
                    try {
                        if (SongPlayingFragment.Staticated.mediaplayer?.isPlaying as Boolean) {
                            SongPlayingFragment.Staticated.mediaplayer?.pause()
                            SongPlayingFragment.Staticated.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                        }
                    }catch(e: Exception){
                        e.printStackTrace()
                    }

                }
                else ->{

                }
            }
        }
    }
}