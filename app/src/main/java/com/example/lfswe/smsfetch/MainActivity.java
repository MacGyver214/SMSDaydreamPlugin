package com.example.lfswe.smsfetch;

/**
 * The main activity class for my android application.
 * initiates loading of the messages into the view described in layout file
 * Created by MacGyver on 2/27/2017.
 */

import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.Manifest;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity{

    public static final int PERMISSION_REQUESTS = 24;
    private static final String address = Telephony.TextBasedSmsColumns.ADDRESS;
    private static final String body = Telephony.TextBasedSmsColumns.BODY;



    boolean permissionGranted = false;
    ArrayList<String> messages = new ArrayList<>();
    ListView messageScroller;
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageScroller = (ListView) findViewById(R.id.message_list_view);
        adapter = new ArrayAdapter(this, R.layout.message_widget, R.id.message_text, messages);
        messageScroller.setAdapter(adapter);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            handlePermissions();
        } else {
            fetchInbox();
        }
    }

    public void fetchInbox() {

        Uri uriSms = Uri.parse("content://sms");
        Cursor cursor = getContentResolver().query(uriSms, null, null, null, null);
        int indexBody = cursor.getColumnIndex(body);
        int indexAddress = cursor.getColumnIndex(address);
        if (indexBody < 0 || !cursor.moveToFirst()) return;
        adapter.clear();
        do {
            String sms = "SMS From: " + cursor.getString(indexAddress) +
                    "\n" + cursor.getString(indexBody) + "\n";

            messages.add(sms);
        } while (cursor.moveToNext());
    }

    /**
     * Modeled after the permissions handler I learned from an example in class
     */
    public void handlePermissions(){
        if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                /**&& shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS)**/) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS}, PERMISSION_REQUESTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //I had this when I thought there were multiple permissions to check for
        //boolean pass = true;
        //for (int i : grantResults) {pass = (grantResults[i] == PackageManager.PERMISSION_GRANTED && pass != false) ? true : false;}

        if (requestCode == PERMISSION_REQUESTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionGranted = true;
                fetchInbox();
            } else {
                permissionGranted = false;
                Log.e("PERMISSION:", "Access to SMS read/write is denied");
                Toast.makeText(this, "Access to SMS read/write is denied", Toast.LENGTH_LONG);
            }
        }
    }
}
