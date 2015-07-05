package com.djs.lightStrandClient.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.ParcelUuid;
import android.widget.Toast;

import com.djs.lightStrandClient.IClient;
import com.djs.lightStrandClient.IClient_CommandListener;
import com.djs.lightStrandClient.IClient_ConnectionListener;
import com.djs.lightStrandClient.LightCode;
import com.djs.lightStrandClient.SLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Communicates with the Light strand server over Bluetooth Low energy
 *
 * Created by David Schmidlin on 7/4/2015.
 */
public class LSClient_BLE implements IClient
{
    protected Activity          mActivity;
    protected BluetoothAdapter  mBluetoothAdapter;
    protected boolean           mScanning;
    protected boolean           mIsConnected;
    protected Handler           mHandler;
    protected BluetoothDevice   mRemoteBlutToothDevice;
    protected BluetoothGatt     mBlueToothServerGATT;
    protected boolean           mShouldStartDiscovery;
    private static final long   SCAN_PERIOD = 10000;
    // These were taken from Adafruits site here: https://learn.adafruit.com/getting-started-with-the-nrf8001-bluefruit-le-breakout/adding-app-support
    private static ParcelUuid   ServiceUUID             = ParcelUuid.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    private static ParcelUuid   TXCharacteristicUUID    = ParcelUuid.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    private static ParcelUuid   RXCharacteristicUUID = ParcelUuid.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");

    public LSClient_BLE(Activity activity)
    {
        mActivity       = activity;
        mScanning       = false;
        mIsConnected    = false;
        mHandler        = new Handler();
    }

    @Override
    public boolean isConnected()
    {
        return mIsConnected;
    }

    @Override
    public void connect(final IClient_ConnectionListener connectListner)
    {
        if (mScanning || mIsConnected)
        {
            return;
        }
        mScanning = true;

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager = (BluetoothManager) mActivity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(enableBtIntent, 0);
        }
        else
        {
            final BluetoothLeScanner leScanner = mBluetoothAdapter.getBluetoothLeScanner();

            List<ScanFilter> filters = new ArrayList<ScanFilter>();
            filters.add(new ScanFilter.Builder().setServiceUuid(ServiceUUID).build());
            filters.add(new ScanFilter.Builder().setServiceUuid(TXCharacteristicUUID).build());
            filters.add(new ScanFilter.Builder().setServiceUuid(RXCharacteristicUUID).build());

            ScanSettings.Builder settBuilder = new ScanSettings.Builder();
            settBuilder.setScanMode(ScanSettings.SCAN_MODE_BALANCED);

            leScanner.startScan(filters, settBuilder.build(), new ScanCallback()
            {
                @Override
                public void onScanResult(int callbackType, ScanResult result)
                {
                    mScanning = false;
                    leScanner.stopScan(null);

                    mShouldStartDiscovery = false;
                    mRemoteBlutToothDevice = result.getDevice();
                    mBlueToothServerGATT = mRemoteBlutToothDevice.connectGatt(mActivity, true, new BluetoothGattCallback()
                    {
                        @Override
                        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
                        {
                            switch (newState)
                            {
                                case BluetoothProfile.STATE_CONNECTED:
                                    connectListner.OnConnectionSuccess();

                                    if (mBlueToothServerGATT == null)
                                    {
                                        mShouldStartDiscovery = true;
                                    }
                                    else
                                    {
                                        mBlueToothServerGATT.discoverServices();
                                    }
                                    mIsConnected = true;
                                    break;
                                case BluetoothProfile.STATE_DISCONNECTED:
                                    connectListner.OnConnectionLost();
                                    mIsConnected = false;
                                    break;
                                default:
                                    break;
                            }
                        }

                        @Override
                        public void onServicesDiscovered(BluetoothGatt gatt, int status)
                        {
                            if (status == BluetoothGatt.GATT_SUCCESS)
                            {
                                SLog.Debug("onServicesDiscovered received: GATT_SUCCESS");
                            } else {
                                SLog.Warn("onServicesDiscovered received: " + status);
                            }
                        }

                        @Override
                        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
                        {
                            if (status == BluetoothGatt.GATT_SUCCESS)
                            {
                                SLog.Debug("onCharacteristicRead received: GATT_SUCCESS");
                            }
                            else
                            {
                                SLog.Warn("onCharacteristicRead received: " + status);
                            }
                        }
                    });

                    if (mShouldStartDiscovery)
                    {
                        mShouldStartDiscovery = false;
                        mBlueToothServerGATT.discoverServices();
                    }
                }



                @Override
                public void onScanFailed(int errorCode)
                {
                    mIsConnected = false;
                    mScanning = false;
                    connectListner.OnConnectionFailed("Scann failed with code " + errorCode);
                }
            });


            //This will cancel the scan after a short time
            mHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    mScanning = false;
                    leScanner.stopScan(null);
                    connectListner.OnConnectionFailed("Failed to connect within " + SCAN_PERIOD + "ms");
                }
            }, SCAN_PERIOD);
        }

    }

    @Override
    public void disconnect()
    {
        mIsConnected = false;
        mScanning = false;

        if (mBlueToothServerGATT != null)
        {
            mBlueToothServerGATT.close();
            mBlueToothServerGATT = null;
        }
    }

    @Override
    public void sendCode(LightCode code, IClient_CommandListener cmdListener)
    {
        if (mIsConnected == false)
        {
            cmdListener.OnCommandFailed("Device is NOT connected, unable to send command.");
            return;
        }

        if (mScanning == true)
        {
            cmdListener.OnCommandFailed("Scanning is still in progress, unable to send command");
            return;
        }

        BluetoothGattService gattService = mBlueToothServerGATT.getService(ServiceUUID.getUuid());
        if (gattService == null)
        {
            cmdListener.OnCommandFailed("Unable to find Remote Bluetooth Service : \"" + ServiceUUID.toString() + "\"");
            return;
        }

        try
        {

            BluetoothGattCharacteristic TxCharacteristic = gattService.getCharacteristic(TXCharacteristicUUID.getUuid());
            TxCharacteristic.setValue(new byte[]{(byte) code.ordinal()});
            mBlueToothServerGATT.writeCharacteristic(TxCharacteristic);
            mBlueToothServerGATT.setCharacteristicNotification(TxCharacteristic, true);

            cmdListener.OnCommandSuccess(code.toString() + " sent.");
        }
        catch (Exception ex)
        {
            cmdListener.OnCommandFailed(ex.getMessage());
        }
    }

}
