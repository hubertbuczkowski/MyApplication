package com.example.h_buc.activitytracker;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import java.lang.reflect.Array;
import java.util.Set;
import com.example.h_buc.activitytracker.Helpers.CustomBluetoothProfile;

/**
 * Created by h_buc on 13/11/2017.
 */

//This controller is gathering heart rate from database and sends back results to Record class

public class bandController {

    volatile String bandAddress = null;
    volatile String lastHeartRate;
    public volatile int readingStatus;
    public volatile int isConnected = 0;
    public volatile boolean isDescriptior = false;

    volatile BluetoothAdapter bluetoothAdapter;
    volatile BluetoothGatt bluetoothGatt;
    volatile BluetoothDevice bluetoothDevice;
    volatile BluetoothGattService mBluetoothGattService = null;
    volatile Context appContext = null;

    volatile Boolean isListeningHeartRate = false;

    //find address of mi band 2
    public void getBoundedDevice(Context ctx) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> boundedDevice = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice bd : boundedDevice) {
            if (bd.getName().contains("MI Band 2")) {
                this.bandAddress = bd.getAddress();
                this.appContext = ctx;
            }
        }
    }

    //connects to mi band
    public void startConnecting(){
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(this.bandAddress);
        bluetoothGatt = bluetoothDevice.connectGatt(this.appContext, true, bluetoothGattCallback);
    }

    //Start heart rate
    public String startScanHeartRate(Context ctx) {
        isListeningHeartRate = false;
        isConnected = 0;
        isDescriptior = false;
        while(this.bandAddress == null){}
        startConnecting();
        while(isListeningHeartRate == false || isConnected == 0 || isDescriptior == false){
            if(isConnected == 2)
            {
                return "-1";
            }
        }
        BluetoothGattCharacteristic bchar = mBluetoothGattService.getCharacteristic(CustomBluetoothProfile.HeartRate.controlCharacteristic);
        bchar.setValue(new byte[]{21, 2, 1});
        bandController.this.readingStatus = 0;
        bluetoothGatt.writeCharacteristic(bchar);

        while(bandController.this.readingStatus == 0)
        {
        }
        this.lastHeartRate = this.lastHeartRate.replace("-", "");
        return this.lastHeartRate;
    }

//    public String startScanHeartRate() {
//        while(isListeningHeartRate == false){}
//        BluetoothGattCharacteristic bchar = mBluetoothGattService.getCharacteristic(CustomBluetoothProfile.HeartRate.controlCharacteristic);
//        bchar.setValue(new byte[]{21, 2, 1});
//        bandController.this.readingStatus = 0;
//        bluetoothGatt.writeCharacteristic(bchar);
//
//        System.out.println("beforeLoop");
//        while(bandController.this.readingStatus == 0)
//        {
//        }
//        System.out.println("afterLoop");
//        return this.lastHeartRate;
//    }

    //send back last heart rate
    public void updateHR(String hr){
        this.lastHeartRate = hr;
        bandController.this.readingStatus = 1;
    }

    //Write characteristics for reading heart rate data
    public void listenHeartRate(BluetoothGattService serv) {
        mBluetoothGattService = serv;
        BluetoothGattCharacteristic bchar = serv.getCharacteristic(CustomBluetoothProfile.HeartRate.measurementCharacteristic);
        bluetoothGatt.setCharacteristicNotification(bchar, true);
        BluetoothGattDescriptor descriptor = bchar.getDescriptor(CustomBluetoothProfile.HeartRate.descriptor);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        bluetoothGatt.writeDescriptor(descriptor);
        isListeningHeartRate = true;
    }

    public void stateConnected() {
        bluetoothGatt.discoverServices();
    }

    public void stateDisconnected() {
        bluetoothGatt.disconnect();
    }

    public final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);


            if (newState == BluetoothProfile.STATE_CONNECTED) {
                stateConnected();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                stateDisconnected();
            }

        }

        //Sygnalise if smart band is connected
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                listenHeartRate(bluetoothGatt.getService(CustomBluetoothProfile.HeartRate.service));
                isConnected = 1;
            }
            else
            {
                isConnected = 2;
            }
        }

        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            byte[] data = characteristic.getValue();
            updateHR(Array.get(data, 1).toString());
        }

        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            byte[] data = characteristic.getValue();
        }

        //send back heart rate read
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            byte[] data = characteristic.getValue();
            updateHR(Array.get(data, 1).toString());
        }

        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            isDescriptior = true;
        }

        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }

    };
}
