package cu.axel.smartdock.wrappers

import android.annotation.SuppressLint
import android.net.wifi.IWifiManager
import android.net.wifi.ScanResult
import android.os.IBinder
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.SystemServiceHelper
import java.lang.reflect.Method

class WifiManagerWrapper {


    private var getListMethod: Method? = null
    private var getScanResultsMethod: Method? = null
    private var binder: ShizukuBinderWrapper? = null
    private var wifiManager: IWifiManager? = null

    private val deathRecipient = IBinder.DeathRecipient {
        synchronized(this) {
            wifiManager = null
            binder = null
        }
    }

    init {
        init()
    }

    @SuppressLint("BlockedPrivateApi", "PrivateApi")
    fun init() {
        synchronized(this) {
            if (wifiManager != null)
                return
            binder = ShizukuBinderWrapper(SystemServiceHelper.getSystemService("wifi"))
            binder?.linkToDeath(deathRecipient, 0)
            wifiManager = IWifiManager.Stub.asInterface(binder)
            getScanResultsMethod = wifiManager!!.javaClass.getDeclaredMethod(
                "getScanResults",
                String::class.java, String::class.java
            )
            val parceledListSliceClass =
                Class.forName("com.android.wifi.x.com.android.modules.utils.ParceledListSlice")
            getListMethod = parceledListSliceClass.getDeclaredMethod("getList")
        }
    }

    fun isAlive() = binder?.isBinderAlive == true && wifiManager != null

    fun setWifiEnabled(enabled: Boolean): Boolean {
        return wifiManager?.setWifiEnabled("com.android.shell", enabled) ?: false
    }

    fun getScanResults(): List<ScanResult> {
        val results = getScanResultsMethod!!.invoke(wifiManager, "com.android.shell", "")
        val list = getListMethod!!.invoke(results) as ArrayList<ScanResult>
        return list
    }

    fun startScan() {
        //wifiManager.startScan()
    }
}