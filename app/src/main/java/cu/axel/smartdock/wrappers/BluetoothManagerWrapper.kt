package cu.axel.smartdock.wrappers

import android.annotation.SuppressLint
import android.bluetooth.IBluetoothManager
import android.os.IBinder
import android.util.Log
import cu.axel.smartdock.utils.Utils
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.SystemServiceHelper

class BluetoothManagerWrapper {

    private var binder: ShizukuBinderWrapper? = null
    private var bluetoothManager: IBluetoothManager? = null

    private val deathRecipient = IBinder.DeathRecipient {
        synchronized(this) {
            bluetoothManager = null
            binder = null
        }
    }

    init {
        init()
    }

    @SuppressLint("BlockedPrivateApi", "PrivateApi")
    fun init() {
        synchronized(this) {
            if (bluetoothManager != null)
                return
            binder = ShizukuBinderWrapper(SystemServiceHelper.getSystemService("bluetooth_manager"))
            binder?.linkToDeath(deathRecipient, 0)
            bluetoothManager = IBluetoothManager.Stub.asInterface(binder)
            Log.e("dock", Utils.getClassInfo(bluetoothManager!!.javaClass))
        }
    }

    fun isAlive() = binder?.isBinderAlive == true && bluetoothManager != null

    fun setBluetoothEnabled(enabled: Boolean): Boolean {
        return true
    }
}