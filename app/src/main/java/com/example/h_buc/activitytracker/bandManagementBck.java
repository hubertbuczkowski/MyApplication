package com.example.h_buc.activitytracker;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Driver;
import java.util.Arrays;
import java.util.Set;

import com.example.h_buc.activitytracker.Helpers.CustomBluetoothProfile;
import com.example.h_buc.activitytracker.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by h_buc on 13/11/2017.
 */

public class bandManagementBck extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;
    BluetoothGatt bluetoothGatt;
    BluetoothDevice bluetoothDevice;

    Boolean isListeningHeartRate = false;
    //TextView txtStatus;
    //TextView txtMac;
    //TextView heartRate;
    FirebaseDatabase db;
    DatabaseReference dRef ;
    ImageButton usrBtn, logout;

    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        //initialie();
        getBoundedDevice();

        db = FirebaseDatabase.getInstance();
        dRef = db.getReference("SimpleMessage");
        dRef.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                //heartRate.setText(value);
                Log.d("DB SUCC", "Value is: " + value);
            }

            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("DB WARN", "Failed to read value.", error.toException());
            }
        });

        usrBtn = (ImageButton) findViewById(R.id.userSettings);
        logout = (ImageButton) findViewById(R.id.logout);

        usrBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                userPref();
            }
        });
        logout.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                Logout();
            }
        });

    }

    /*void initialie(){
        //Button shButton = findViewById(R.id.checkHR);
        //txtStatus = findViewById(R.id.statusReady);
        //txtMac = findViewById(R.id.addressReady);
        //heartRate = findViewById(R.id.heartReady);

        shButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                startScanHeartRate();
            }
        });
    }*/

    void startConnecting(String addressEx) {

        String address = addressEx;
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
        bluetoothGatt = bluetoothDevice.connectGatt(this, true, bluetoothGattCallback);

    }

    void getBoundedDevice() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> boundedDevice = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice bd : boundedDevice) {
            if (bd.getName().contains("MI Band 2")) {
                //txtMac.setText(bd.getAddress());
                startConnecting(bd.getAddress());
            }
        }
    }

    void sendData(String results){
        dRef = db.getReference("SimpleMessage");
        dRef.setValue(results);
    }

    void startScanHeartRate() {
        BluetoothGattCharacteristic bchar = bluetoothGatt.getService(CustomBluetoothProfile.HeartRate.service)
                .getCharacteristic(CustomBluetoothProfile.HeartRate.controlCharacteristic);
        bchar.setValue(new byte[]{21, 2, 1});
        bluetoothGatt.writeCharacteristic(bchar);
    }

    void listenHeartRate() {
        BluetoothGattCharacteristic bchar = bluetoothGatt.getService(CustomBluetoothProfile.HeartRate.service)
                .getCharacteristic(CustomBluetoothProfile.HeartRate.measurementCharacteristic);
        bluetoothGatt.setCharacteristicNotification(bchar, true);
        BluetoothGattDescriptor descriptor = bchar.getDescriptor(CustomBluetoothProfile.HeartRate.descriptor);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        bluetoothGatt.writeDescriptor(descriptor);
        isListeningHeartRate = true;
    }

    void stateConnected() {
        bluetoothGatt.discoverServices();
        //txtStatus.setText("Connected");
    }

    void stateDisconnected() {
        bluetoothGatt.disconnect();
        //txtStatus.setText("Disconnected");
    }

    void userPref()
    {
        Intent intent = new Intent(bandManagementBck.this, userPref.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    void Logout(){
        SaveSharedPreference.clear(getApplicationContext());
        Intent intent = new Intent(bandManagementBck.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }



    final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

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
            super.onServicesDiscovered(gatt, status);
            listenHeartRate();
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            byte[] data = characteristic.getValue();
            //tx.setText(Arrays.toString(data));
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            byte[] data = characteristic.getValue();
            sendData(Arrays.toString(data));
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
