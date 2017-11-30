package com.team6.rifflegroup.streamsavvy;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;

/**
 * Created by aaddaamm30 on 11/16/2017.
 */

public class btClass {

    private BluetoothAdapter mBluetoothAdapter;

    public char[] getBTdata(Context init){

        char[] ell = null;
        final BluetoothManager bluetoothManager = (BluetoothManager) init.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        init.startActivity(enableBtIntent);
        }


        return ell;
    }

}
