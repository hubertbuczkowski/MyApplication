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
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import com.example.h_buc.activitytracker.Helpers.CustomBluetoothProfile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by h_buc on 13/11/2017.
 */

public class bandController {

    String bandAddress = null;
    String lastHeartRate;
    int readingStatus;

    BluetoothAdapter bluetoothAdapter;
    BluetoothGatt bluetoothGatt;
    BluetoothDevice bluetoothDevice;
    BluetoothGattService mBluetoothGattService = null;

    Boolean isListeningHeartRate = false;

    public void getBoundedDevice(Context ctx) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> boundedDevice = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice bd : boundedDevice) {
            if (bd.getName().contains("MI Band 2")) {
                //txtMac.setText(bd.getAddress());
                this.bandAddress = bd.getAddress();
                String address = bd.getAddress();
                bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
                bluetoothGatt = bluetoothDevice.connectGatt(ctx, true, bluetoothGattCallback);
            }
        }
    }

    public String startScanHeartRate() {
        while(isListeningHeartRate == false){}
        BluetoothGattCharacteristic bchar = mBluetoothGattService.getCharacteristic(CustomBluetoothProfile.HeartRate.controlCharacteristic);
        bchar.setValue(new byte[]{21, 2, 1});
        this.readingStatus = 0;
        bluetoothGatt.writeCharacteristic(bchar);

        System.out.println("beforeLoop");
        while(this.readingStatus == 0)
        {
        }
        System.out.println("afterLoop");
        return this.lastHeartRate;
    }

    public void updateHR(String hr){
        this.lastHeartRate = hr;
        this.readingStatus = 1;
        System.out.println(hr + " updated");
    }

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

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                stateConnected();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                stateDisconnected();
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                listenHeartRate(bluetoothGatt.getService(CustomBluetoothProfile.HeartRate.service));
            }

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            byte[] data = characteristic.getValue();
            System.out.println("Characteristic Read");
            updateHR(Array.get(data, 1).toString());
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            System.out.println("Characteristic Write");
            byte[] data = characteristic.getValue();
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            System.out.println("Characteristic Change");
            byte[] data = characteristic.getValue();
            updateHR(Array.get(data, 1).toString());
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }

    };
}
