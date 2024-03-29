package com.blueberrysolution.pinelib19.activity

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import com.blueberrysolution.pinelib19.R
import com.blueberrysolution.pinelib19.addone.mytimer.MyTimer
import com.blueberrysolution.pinelib19.addone.mytimer.OnTimerListener
import com.blueberrysolution.pinelib19.debug.G
import com.blueberrysolution.pinelib19.addone.broadcast.Broadcast
import java.lang.Math.abs
import android.content.pm.PackageManager
import com.blueberrysolution.pinelib19.activity.t.t
import com.blueberrysolution.pinelib19.addone.broadcast.gps.OnGpsBroadcast
import com.blueberrysolution.pinelib19.addone.language.LocaleUtils
import com.blueberrysolution.pinelib19.debug.window.DebugWindowsController
import com.blueberrysolution.pinelib19.hardware.screen.ScreenUtils
import com.blueberrysolution.pinelib19.addone.language.MyContextWrapper
import java.util.*


open class PineActivity: AppCompatActivity() {
    //是否开启双击退出
    open var enableDoubleReturnExit = false
    //是否开启左右滑动广播
    open var enableSliderNotice = false
    //是否开启左滑返回
    open var enableSliderLeftToReturn = true





    //局部变量
    private var downX = 0;
    private var downY = 0;


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        if (requestCode == 100){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted 授予权限
                //处理授权之后逻辑
                Broadcast.i.send("LocationPermissions", true)
                OnGpsBroadcast.i().showGPSContacts()
            } else {
                // Permission Denied 权限被拒绝
                Broadcast.i.send("LocationPermissions", false)
            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    fun onFinger(position: String){
        G.d(position)
    }



    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!enableSliderNotice && !enableSliderLeftToReturn)
            return super.onTouchEvent(event)

        when(event?.getAction()){
            MotionEvent.ACTION_DOWN -> {
                downX = event?.getRawX().toInt();
                downY = event?.getRawY().toInt();
            }
            MotionEvent.ACTION_UP ->{
                var moveX = event?.getRawX().toInt() - downX
                var moveY = event?.getRawY().toInt() - downY
                var moveXAbs = abs(moveX)
                var moveYAbs = abs(moveY)

                if (enableSliderLeftToReturn) {
                    if ((downX < 50) && (moveYAbs < moveXAbs) && (moveXAbs > 200)) {
                        G.d("On Finger Left -- Unload Me")
                        unloadMe()
                    }
                }

                if (enableSliderNotice){
                    if (moveXAbs > moveYAbs) {
                        if (moveXAbs > 100) {
                            if (moveX > 0)
                                onFinger("Right")
                            else
                                onFinger("Left")
                        }
                    }
                    else{
                        if (moveYAbs > 100) {
                            if (moveY > 0)
                                onFinger("Down")
                            else
                                onFinger("Up")
                        }
                    }
                }


            }

            else -> {}

        }

        return super.onTouchEvent(event)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        A.a(this)
        DebugWindowsController.i();

        if (A.app != null){
            ScreenUtils.setKeepScreenOn(A.app!!.keepScreenOn);
        }


        super.onCreate(savedInstanceState)
    }

    override fun attachBaseContext(newBase: Context?) {
        val newLocale: Locale = LocaleUtils.getUserLocale();

        val context = MyContextWrapper.wrap(newBase!!, newLocale)
        super.attachBaseContext(context)

    }

    override fun onResume() {
        A.a(this)
        super.onResume()
    }

    fun unloadMe(v: View? = null){
        this.finish();
    }

    //是否将事件标记成已经消耗
    open fun onReturnKeyDown(): Boolean{
        return false;
    }

    private var mBackKeyPressed = false
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {


        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                Broadcast.i.send("key_up_down")
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                Broadcast.i.send("key_down_down")
            }
            KeyEvent.KEYCODE_BACK -> {

                if (enableDoubleReturnExit){
                    if(!mBackKeyPressed){
                        t(A.s(R.string.one_more_time_to_exit))
                        mBackKeyPressed = true
                        MyTimer.i().setInterval(2000).setOnTimerListener(object : OnTimerListener {
                            override fun onTimer() {
                                mBackKeyPressed = false
                            }
                        }).startOnce()
                        return false;
                    }
                    else{//退出程序
                        this.finish()
                        //System.exit(0)
                    }
                }
                if (onReturnKeyDown())
                    return false;
            }
            else -> {
            }
        }


        return super.onKeyDown(keyCode, event)
    }

}