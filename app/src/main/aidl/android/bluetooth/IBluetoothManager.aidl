package android.bluetooth;

interface IBluetoothManager {
    boolean isEnabled();
    boolean enable();
    boolean disable(boolean persist);
}