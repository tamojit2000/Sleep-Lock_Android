package com.ysvg2tafy.sleeplock

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    var mDPM: DevicePolicyManager? = null
    var mAdminName: ComponentName? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        mDPM = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
        mAdminName = ComponentName(this, ScreenOffAdminReceiver::class.java)

        if (!mDPM!!.isAdminActive(mAdminName!!)){
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                    mAdminName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "Additional text explaining why this needs to be added.");
            startActivityForResult(intent, 101);
        }else {
            mDPM!!.lockNow()

        }


        turnScreenOffAndExit();

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (101 == requestCode){
            if (resultCode == Activity.RESULT_OK){
                Show(this,"Has become the device administrator.")
            }else{
                Show(this,"Canceled or failed.")
            }
        }
    }

    private fun turnScreenOffAndExit() {
        // first lock screen
        turnScreenOff(applicationContext)

        // then provide feedback
        (getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(25)

        // schedule end of activity
        val activity: Activity = this
        val t: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(500)
                } catch (e: InterruptedException) {
                    /* ignore this */
                }
                activity.finish()
            }
        }
        t.start()
    }

    fun turnScreenOff(context: Context) {
        val policyManager = context
                .getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val adminReceiver = ComponentName(
                context,
                ScreenOffAdminReceiver::class.java
        )
        val admin = policyManager.isAdminActive(adminReceiver)
        if (admin) {
            Show(context,"Going to sleep now.")
            policyManager.lockNow()
        } else {
            Show(context, "Not an admin")
        }
    }

    fun Show(context: Context,txt:String){
        Toast.makeText(context,txt,Toast.LENGTH_SHORT).show()
    }



}
