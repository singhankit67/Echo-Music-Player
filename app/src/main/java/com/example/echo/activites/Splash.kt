package com.example.echo.activites


import android.os.Bundle
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.echo.R

class Splash : AppCompatActivity() {
    var permissionsString = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE, // array of those permissions this is basically done to read the external storage of the device to storethe songs on the device
        Manifest.permission.MODIFY_AUDIO_SETTINGS,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.PROCESS_OUTGOING_CALLS,
        Manifest.permission.RECORD_AUDIO)
    //the permissions that the user has to allow when the app is launched


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (!hasPermissions(this@Splash, *permissionsString)) {
            //if permissions are not alloted then we again provide the list to allot all permissions for this we use  *with the string that caaries the permissions
            ActivityCompat.requestPermissions(this@Splash, permissionsString, 131)

        } else {
            postDelayed()
            //if the conditions are satisfied then we would show the screen for 1 second and start the main activityh
            //Handler().postDelayed({
                //val startAct = Intent(this@Splash, MainActivity::class.java)
                //startActivity(startAct) // to initialize th start act function
                //this.finish()

            //}, 1000)
        }
        return
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            131 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
                    && grantResults[3] == PackageManager.PERMISSION_GRANTED
                    && grantResults[4] == PackageManager.PERMISSION_GRANTED
                ) {
                    postDelayed()
                    //Handler().postDelayed({
                    //val startAct = Intent(this@Splash, MainActivity::class.java)
                    //startActivity(startAct) // to initialize th start act function
                    //this.finish()
                    //}, 1000)
                }else{
                    Toast.makeText(this@Splash, "Please grant all the permissions to continue",Toast.LENGTH_SHORT).show()
                    this.finish()
                }
                return
            }
            else -> {
                Toast.makeText(this@Splash, "Something went wrong", Toast.LENGTH_SHORT).show()
                this.finish()
                return
            }

        }
    }

    fun hasPermissions(
        context: Context,
        vararg permissions: String): Boolean //vararg is basically used to convert a array into a simple array boolean is the return type if all the permissions are granted then return true if one permission is also not returned then return false
    {
        var hasAllPermissions = true // stting a variable name to true
        for (permission in permissions) {
            val res = context.checkCallingOrSelfPermission(permission)
            if (res != PackageManager.PERMISSION_GRANTED) {
                hasAllPermissions = false
            }
        }
        return hasAllPermissions

    }
    fun postDelayed() {
        Handler().postDelayed({
            val startAct = Intent(this@Splash, MainActivity::class.java)
            startActivity(startAct) // to initialize th start act function
            this.finish()
        }, 2000)
    }
}



