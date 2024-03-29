package com.blueberrysolution.pinelib19.permission

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.blueberrysolution.pinelib19.activity.A
import com.blueberrysolution.pinelib19.activity.I
import com.blueberrysolution.pinelib19.permission.src.PermissionRequireActivity


//RequirePermission()
//.require(Manifest.permission.READ_EXTERNAL_STORAGE)
//.require(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//.requestPermission({
//    useCamera()
//},{
//    this.finish()
//})


class RequirePermission  {
    var permissionList: Array<String> = arrayOf();


    constructor(){
        lastInstance = this;
    }

    fun require(permission: String): RequirePermission{
        if (!hasPermission(permission)){
            permissionList = permissionList.plus(permission);
        }
        return this;
    }

    fun requestPermission(onGranted0: () -> Unit, onCancel0: (() -> Unit)? = null){

        RequirePermission.onGranted = onGranted0;
        RequirePermission.onCancel = onCancel0;

        if (permissionList.count() == 0){
            RequirePermission.onGranted!!();
            return;
        }
        else{
            I.i(PermissionRequireActivity::class).show();
        }

    }



    fun hasPermission(permissionName: String): Boolean{
        val hasCameraPermission = ContextCompat.checkSelfPermission(
            A.app(),
            permissionName
        )
        return hasCameraPermission == PackageManager.PERMISSION_GRANTED
    }

    fun hasPermissions(): Boolean{

        permissionList.map {
            if (!hasPermission(it))
                return false;
        }
        return true;
    }

    fun onCancel(){
        if (RequirePermission.onCancel != null){
            RequirePermission.onCancel!!();
        }

    }

    companion object{
        var lastInstance: RequirePermission? = null;
        val PERMISSION_REQUEST_CODE = 0x00000299
        var onGranted: (() -> Unit)? = null;
        var onCancel: (() -> Unit)? = null;
    }


}