/*
 * Copyright 2015 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.webbluetoothcg.bletestperipheral;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.graphics.Color;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.UUID;

public class TemperatureServiceFragment extends ServiceFragment {

    private static final String TAG = TemperatureServiceFragment.class.getCanonicalName();
    private static final int MIN_UINT = 0;
    private static final int MAX_UINT8 = (int) Math.pow(2, 8) - 1;
    private static final int MAX_UINT16 = (int) Math.pow(2, 16) - 1;

    /**
     * See <a href="https://developer.bluetooth.org/gatt/services/Pages/ServiceViewer.aspx?u=org.bluetooth.service.heart_rate.xml">
     * Heart Rate Service</a>
     */
    private static final UUID TEMPERATURE_SERVICE_UUID = UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb");

    /**
     * See <a href="https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml">
     * Heart Rate Measurement</a>
     */
    private static final UUID TEMPERATURE_MEASUREMENT_UUID = UUID.fromString("00002A37-0000-1000-8000-00805f9b34fb");
    private static final int TEMPERATURE_MEASUREMENT_VALUE_FORMAT = BluetoothGattCharacteristic.FORMAT_UINT8;
    private static final int INITIAL_TEMPERATURE_MEASUREMENT_VALUE = 37;


    private BluetoothGattService mTemperatureService;
    private BluetoothGattCharacteristic mTemperatureMeasurementCharacteristic;

    private ServiceFragmentDelegate mDelegate;

    private SeekBar temperatureBar;
    private TextView txtTemperatureValue;

    private final SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        int currentTemperature = 0;

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            currentTemperature = i;
            txtTemperatureValue.setText(String.valueOf(currentTemperature));

			if(currentTemperature > 37) {
				seekBar.getThumb().setTint(Color.RED);
				seekBar.getProgressDrawable().setTint(Color.RED);
			} else if(currentTemperature > 23) {
				seekBar.getThumb().setTint(Color.BLUE);
				seekBar.getProgressDrawable().setTint(Color.BLUE);
			} else {
				seekBar.getThumb().setTint(Color.CYAN);
				seekBar.getProgressDrawable().setTint(Color.CYAN);
			}
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mTemperatureMeasurementCharacteristic.setValue(currentTemperature, TEMPERATURE_MEASUREMENT_VALUE_FORMAT, 1);
        }
    };

    private final OnClickListener mNotifyButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mDelegate.sendNotificationToDevices(mTemperatureMeasurementCharacteristic);
        }
    };

    public TemperatureServiceFragment() {
        mTemperatureMeasurementCharacteristic = new BluetoothGattCharacteristic(TEMPERATURE_MEASUREMENT_UUID, BluetoothGattCharacteristic.PROPERTY_NOTIFY, 0);
        mTemperatureMeasurementCharacteristic.setValue(INITIAL_TEMPERATURE_MEASUREMENT_VALUE, TEMPERATURE_MEASUREMENT_VALUE_FORMAT, 1);

        mTemperatureMeasurementCharacteristic.addDescriptor(Peripheral.getClientCharacteristicConfigurationDescriptor());

        mTemperatureService = new BluetoothGattService(TEMPERATURE_SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY);
        mTemperatureService.addCharacteristic(mTemperatureMeasurementCharacteristic);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_temperature, container, false);

        temperatureBar = (SeekBar) view.findViewById(R.id.temperatureBar);
        temperatureBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

        txtTemperatureValue = (TextView) view.findViewById(R.id.txtTemperatureValue);

        temperatureBar.setProgress(INITIAL_TEMPERATURE_MEASUREMENT_VALUE);

        Button notifyButton = (Button) view.findViewById(R.id.button_heartRateMeasurementNotify);
        notifyButton.setOnClickListener(mNotifyButtonListener);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mDelegate = (ServiceFragmentDelegate) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ServiceFragmentDelegate");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDelegate = null;
    }

    @Override
    public BluetoothGattService getBluetoothGattService() {
        return mTemperatureService;
    }

    @Override
    public ParcelUuid getServiceUUID() {
        return new ParcelUuid(TEMPERATURE_SERVICE_UUID);
    }


}
