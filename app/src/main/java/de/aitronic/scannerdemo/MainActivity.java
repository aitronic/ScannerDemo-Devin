/*
  aitronic GmbH - www.aitronic.de
  <p>
  ScannerDemo-Devin
  <p>
  Licensed under GPLv3
 */

package de.aitronic.scannerdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import de.aitronic.scannerdemo.utils.SoundManager;

/**
 * Created by T.Reike on 05.12.2016.
 */

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.codes_lv)
    ListView lvCodes;
    @BindView(R.id.rbBarcode)
    RadioButton rbBarcode;
    @BindView(R.id.rbRFID)
    RadioButton rbRFID;
    @BindView(R.id.swEnable2o5)
    Switch swEnable2o5;


    private static HashMap<String, String> MAPPING = new HashMap<>();

    static {
        MAPPING.put("A", "EAN/UPC");
        MAPPING.put("B", "Code 39/32");
        MAPPING.put("C", "Codabar");
        MAPPING.put("D", "Code 128");
        MAPPING.put("E", "Code 93");
        MAPPING.put("F", "Int. 2/5");
        MAPPING.put("G", "Dis. 2/5");
        MAPPING.put("H", "Code 11");
        MAPPING.put("K", "GS1-128");
        MAPPING.put("L", "Bookland EAN");
        MAPPING.put("M", "Triop. Code 39");
        MAPPING.put("N", "Coupon Code");
        MAPPING.put("R", "GS1 Databar");
        MAPPING.put("S", "Matrix 2/5");
        MAPPING.put("T", "UCC/TLC 39");
        MAPPING.put("U", "Chinese 2/5");
        MAPPING.put("V", "Korean 2/5");
        MAPPING.put("X", "ISSN EAN/PDF 417");
        MAPPING.put("z", "Aztec");

        MAPPING.put("P00", "Data Matrix");
        MAPPING.put("P01", "QR Code");
        MAPPING.put("P02", "Maxicode");
        MAPPING.put("P03", "US Postnet");
        MAPPING.put("P04", "US Planet");
        MAPPING.put("P05", "Japan Postal");
        MAPPING.put("P06", "UK Postal");
        MAPPING.put("P08", "KIX Code");
        MAPPING.put("P09", "Australia Post");
        MAPPING.put("P0A", "USBPS 4CB");
        MAPPING.put("P0B", "UPU FICS");
        MAPPING.put("P0H", "Han Xin");
        MAPPING.put("P0X", "Signature");

    }


    ArrayList<String[]> codes = new ArrayList();
    MyArrayAdapter lvAdapter;

    SoundManager soundManager;

    BroadcastReceiver scanBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("de.aitronic.SCAN_DATA".equals(intent.getAction())) {
                String result = intent.getStringExtra("data");
                String type = intent.getStringExtra("type");
                Log.e("Scanner", "Scan Completed: Data: " + result + " Type: " + type);


                if (MAPPING.containsKey(type)) {
                    type = MAPPING.get(type);
                }

                // display last 10 results
                codes.add(0, new String[]{type, result});
                if (codes.size() > 10)
                    codes.subList(10, codes.size()).clear();
                lvAdapter.notifyDataSetChanged();
                lvCodes.post(new Runnable() {
                    public void run() {
                        lvCodes.setSelection(lvCodes.getCount() - 1);
                    }
                });

                soundManager.playSound(1);


                // SIMPLE COMMAND EXAMPLES


                // EXAMPLE on how to use SIMPLE_COMMANDS to read more data from a tag
                if (type.equals("#80")) { // MIFARE
                    // Example of Mifare Read Blocks
                    // Mifare Read
                    Log.e("MainActivity", "MIFARE FOUND, STARTING GET VERSION");
                    Intent intent_login = new Intent("de.aitronic.scanner.SIMPLE_COMMAND");
                    intent_login.putExtra("simpleCommand", "0004FF");
                    sendBroadcast(intent_login);

                } else if (type.equals("#82")) { // ISO15693

                    Intent intent_simple_command = new Intent("de.aitronic.scanner.SIMPLE_COMMAND");

                    // METHOD 1
                    // EXAMPLE OF READ BLOCK for ISO15693
                    intent_simple_command.putExtra("simpleCommand", "0D050000FF");


                    // SIMPLE COMMAND TO WRITE BLOCK 0500
                    // intent_simple_command.putExtra("simpleCommand", "0D0705000411223344");


                    // METHOD 2
                    // SIMPLE COMMAND ARRAY
                    // WRITE BLOCK 0300 AND READ BLOCKS 0000 - 0500
                    /* intent_simple_command.putExtra("simpleCommandArray", new String[]{
                            "0D070300081122334455667788",
                            "0D050000FF",
                            "0D050100FF",
                            "0D050200FF",
                            "0D050300FF",
                            "0D050400FF",
                            "0D050500FF"
                    })*/

                    sendBroadcast(intent_simple_command);


                    //} else if (type.equals("#F0"))  {
                    // Available from Devin >= 1.8.0

                    // EXAMPLE TO READ AND WRITE BLOCKS ON UHF

                    /*
                     RESERVED(0),
                    UII((1),
                    TID(2),
                    USER(3),
                    DIGITAL_SIGNATURE(4);
                     */


                    /*
                    // Write UHF
                    // Writes data to the selected location and reads the segment that should have been
                    // written. It sends back the read data as a SIMPLE_COMMAND_RESULT
                    Intent intent_simple_command = new Intent("de.aitronic.uhfscanner.WRITE_DATA");
                    intent_simple_command.putExtra("bank", 1);
                    intent_simple_command.putExtra("data", "2423"); // Hex-Data, length has to be multiples of 4 (=Word = 2bytes)
                    intent_simple_command.putExtra("offset", 2);
                    intent_simple_command.putExtra("accessPassword", "00000000"); // default often is 00000000
                    sendBroadcast(intent_simple_command);
                    */


                    // Read UHF
                    // Reads data from the selected location
                    // It sends back the read data as a SIMPLE_COMMAND_RESULT


                    // Set Power to Lowest possible value, to prevent reading other tags
                    /*
                    Intent power = new Intent("de.aitronic.uhfscanner.SET_POWER");
                    power.putExtra("power", "5");
                    sendBroadcast(power);

                    Intent intent_simple_command = new Intent("de.aitronic.uhfscanner.READ_DATA");
                    intent_simple_command.putExtra("bank", 1);
                    intent_simple_command.putExtra("count", 6); // reads 6 "words = 2bytes"
                    intent_simple_command.putExtra("offset", 2);
                    intent_simple_command.putExtra("accessPassword", "00000000"); // default often is 00000000
                    sendBroadcast(intent_simple_command);
                    */
                }
            } else {
                // HANDLE RESULTS

                // RESULT FROM Single SimpleCommand
                String result = intent.getStringExtra("result");
                // RESULT-ARRAY from Multi SimpleCommand
                String[] resultArray = intent.getStringArrayExtra("resultArray");
                if (result != null)
                    Log.e("ScannerDemo", "RESULT: " + result);

                if (resultArray != null)
                    Log.e("ScannerDemo", "RESULT ARRAY: " + join(resultArray));

                Toast.makeText(MainActivity.this, "RESULT: " + result, Toast.LENGTH_LONG).show();
            }

        }
    };

    private String join(String[] array) {
        StringBuilder b = new StringBuilder();
        for (String s :
                array) {
            b.append(s).append(", ");
        }
        return b.toString();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    @Override
    protected void onPause() {
        shutdown();
        super.onPause();
    }


    // Use the defined scanner buttons in Devin instead and the SimpleCommand
    // de.aitronic.scanner.SCANNER_TYPES_SET to define which Module to start
    @Deprecated
    @OnClick({R.id.startScan, R.id.stopScan})
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.startScan:
                Intent i = new Intent();
                if (rbBarcode.isChecked()) {
                    i.setAction("de.aitronic.scanner.START_BARCODE");
                } else {
                    i.setAction("de.aitronic.scanner.START_RFID");
                }
                this.sendBroadcast(i);
                break;
            case R.id.stopScan:
                Intent i2 = new Intent();
                if (rbBarcode.isChecked()) {
                    i2.setAction("de.aitronic.scanner.STOP_BARCODE");
                } else {
                    i2.setAction("de.aitronic.scanner.STOP_RFID");
                }
                this.sendBroadcast(i2);
                break;
        }
    }

    private void init() {
        soundManager = SoundManager.getInstance(MainActivity.this);

        lvAdapter = new MyArrayAdapter(this, R.layout.list_item, codes);
        lvCodes.setAdapter(lvAdapter);

        IntentFilter filters = new IntentFilter();
        filters.addAction("de.aitronic.SCAN_DATA");
        filters.addAction("de.aitronic.scanner.SIMPLE_COMMAND_RESULT");
        registerReceiver(scanBroadcastReceiver, filters);
        if (rbBarcode.isChecked()) {
            setTypesToScan(true);
        } else {
            setTypesToScan(false);
        }
    }


    private void shutdown() {
        unregisterReceiver(scanBroadcastReceiver);
        resetTypesToScan();
    }


    @OnClick({R.id.rbBarcode, R.id.rbRFID})
    public void checkRadioButton(View v) {
        if (v.getId() == R.id.rbBarcode)
            setTypesToScan(true);
        else
            setTypesToScan(false);
    }

    @OnCheckedChanged(R.id.swEnable2o5)
    public void enable2o5() {
        Intent i = new Intent("de.aitronic.scanner.CONFIG_TWOD");
        String[] config = new String[6];

        if (swEnable2o5.isChecked()) {
            config[0] = "6;1;bool"; // Interleaved active
            config[3] = "5;1;bool"; // Discreet active
        } else {
            config[0] = "6;0;bool"; // Interleaved inactive
            config[3] = "5;0;bool"; // Discreet inactive
        }

        config[1] = "22;0;int"; // min length interleaved
        config[2] = "23;30;int"; // max length interleaved
        config[4] = "20;0;int"; // min length discreet
        config[5] = "21;30;int"; // max length discreet

        i.putExtra("config", config);
        sendBroadcast(i);
    }


    /**
     *
     * Definiere die Action bei Drücken der Scantaste
     * So können z.B. mehrere Module auf die gleiche Scantaste gelegt werden, jedoch nur das
     * aktuell aktivierte Modul wird dann gestartet
     *
     *
     * Beispiel Konfiguration in Devin:
     * Barcode aktiv - Keycode 131
     * RFID aktiv - Keycode 131
     *
     *
     * In Devin muss die Option "Anderen Apps erlauben Einstellungen zu ändern" aktiv sein
     *
     *
     * @param barcode - enable barcode or rfid
     *
     *                00001 = Barcode
     *                00010 = RFID
     *                00100 = LF (veraltet)
     *                01000 = UHF
     *                10000 = UHF Continous
     *
     *
     */
    private void setTypesToScan(boolean barcode) {
        Intent i = new Intent("de.aitronic.scanner.SCANNER_TYPES_SET");
        if (barcode)
            i.putExtra("scanner_types", 1);
        else
            i.putExtra("scanner_types", 0b01000);
        sendBroadcast(i);
    }


    /**
     *  Zurücksetzen der Modul-Aktivierungen auf "nichts" aktiviert
     *  Drücken der Scan-Tasten startet jetzt kein Modul mehr
     */
    private void resetTypesToScan() {
        Intent i = new Intent("de.aitronic.scanner.SCANNER_TYPES_SET");
        i.putExtra("scanner_types", 0);
        sendBroadcast(i);
    }

}
