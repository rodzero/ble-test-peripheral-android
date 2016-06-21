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

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


public class Peripherals extends ListActivity {

    // TODO(g-ortuno): Implement heart rate monitor peripheral
    private static final String[] PERIPHERALS_NAMES = new String[]{"Temperature"};
    public final static String EXTRA_PERIPHERAL_INDEX = "PERIPHERAL_INDEX";
    public static final String ADVERTISE_MAC_ADDRESS_PREF = "AdvertiseMacAddress";

    public static final String DEFAULT_MACADDRESS = "11:22:33:44:55:66";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peripherals_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
        /* layout for the list item */ android.R.layout.simple_list_item_1,
        /* id of the TextView to use */ android.R.id.text1,
        /* values for the list */ PERIPHERALS_NAMES);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent intent = new Intent(this, Peripheral.class);
        intent.putExtra(EXTRA_PERIPHERAL_INDEX, position);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Change MAC Address");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

		final String[] macAddressItems = new String[] {
				"11:11:11:11:11:11",
				"22:22:22:22:22:22",
				"33:33:33:33:33:33",
				"44:44:44:44:44:44",
				"55:55:55:55:55:55",
				"66:66:66:66:66:66",
				"77:77:77:77:77:77",
				"88:88:88:88:88:88",
				"99:99:99:99:99:99",
				"00:00:00:00:00:00",
				"30:0e:db:48:b4:b0"
		};

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Choose MAC Address");
//        alertBuilder.setMessage("Inform a Mac Address that will be sent on Advertisement Date");

        alertBuilder.setItems(macAddressItems,
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
				String newMacAddress = macAddressItems[i];

				SharedPreferences.Editor editor = getSharedPreferences(ADVERTISE_MAC_ADDRESS_PREF, MODE_PRIVATE).edit();
				editor.putString(ADVERTISE_MAC_ADDRESS_PREF, newMacAddress);
				editor.commit();
            }
        });

        alertBuilder.create().show();

        return super.onOptionsItemSelected(item);
    }
}
