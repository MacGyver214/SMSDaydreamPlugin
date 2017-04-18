package com.example.lfswe.smsfetch;

/**
 * The main activity class for my android application.
 * initiates loading of the messages into the view described in layout file
 * Created by MacGyver on 2/27/2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.Manifest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends Activity implements AdapterView.OnItemClickListener{
    //integer control codes for permissions
    public static final int PERMISSION_REQUESTS = 24;
    public static final int SMS_REQUEST = 23;
    public static final int CONTACTS_REQUEST = 22;
    //constants & resources
    private static final String address = Telephony.TextBasedSmsColumns.ADDRESS;
    private static final String body = Telephony.TextBasedSmsColumns.BODY;
    private static final String displayName = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY;
    private static final String contactNumber = ContactsContract.PhoneLookup.NUMBER;
    private static final String[] permissions = {Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS};
    private static final Uri uriSms = Uri.parse("content://sms");
    private static final int permissionGranted = PackageManager.PERMISSION_GRANTED;

    //base URI for contact lookup
    private Uri uriContact = ContactsContract.PhoneLookup.CONTENT_FILTER_URI;


    //First attempt, using a default ArrayList for a default ArrayAdapter
    //ArrayList<String> messages = new ArrayList<>();
    //ArrayAdapter adapter;
    //Second attempt, using a custom ArrayList for a Custom MessageAdapter
    ArrayList<Message> mMessages = new ArrayList<>();
    ConversationAdapter mAdapter;

    ListView messageScroller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageScroller = (ListView) findViewById(R.id.conversation_list_view);
        messageScroller.setOnItemClickListener(this);
        //First Attempt
        //adapter = new ArrayAdapter(this, R.layout.message_widget, R.id.message_preview, messages);
        //Second Attempt
        mAdapter = new ConversationAdapter(this, mMessages);
        //messageScroller.setAdapter(adapter);
        messageScroller.setAdapter(mAdapter);
        if (ContextCompat.checkSelfPermission(this, permissions[0]) != permissionGranted
                && ContextCompat.checkSelfPermission(this, permissions[1]) != permissionGranted) {
            handlePermissions(permissions, PERMISSION_REQUESTS);
        } else if (ContextCompat.checkSelfPermission(this, permissions[0]) != permissionGranted){
            handlePermissions(new String[]{permissions[0]}, SMS_REQUEST);
        } else if (ContextCompat.checkSelfPermission(this, permissions[1]) != permissionGranted) {
            handlePermissions(new String[]{permissions[1]}, CONTACTS_REQUEST);
        } else {
            fetchConversations();
        }
    }

    @Override
    /**
     * Describes the action to be performed when a message object in the list is clicked
     */
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Message toBeRecipient = (Message) parent.getItemAtPosition(position);
        Intent intent = new Intent(getBaseContext(), ComposeActivity.class);
        intent.putExtra("RECIPIENT_NAME", toBeRecipient.getName());
        intent.putExtra("RECIPIENT_NUMBER", toBeRecipient.getNumber());
        startActivity(intent);
    }

    /**
     * A helper method to look up and load sms messages into local data structures
     */
    public void fetchConversations() {

        Cursor smsCursor = getContentResolver().query(uriSms, null, null, null, null);
        int count = 0;
        int indexBody = smsCursor.getColumnIndex(body);
        int indexAddress = smsCursor.getColumnIndex(address);
        if (indexBody < 0 || !smsCursor.moveToFirst()) return;
        mAdapter.clear();
        do {
            String address = smsCursor.getString(indexAddress);
            String name = lookupContact(address);
            String body = smsCursor.getString(indexBody);
            Message sms = new Message(name, body, address);

            mMessages.add(sms);
            //just don't fetch the entire sms database, it takes too long.
            count++;
        } while (smsCursor.moveToNext() && count < 300);
    }

    /**
     * A method to query the contacts provider database to convert a phone number into a contact name
     * @param phoneNumber The phone number drawn from fetching user SMS messages
     * @return A string of the primary contact name associated with the number
     */
    public String lookupContact(String phoneNumber) {
        Uri lookupUri = Uri.withAppendedPath(uriContact, Uri.encode(phoneNumber));
        String name = null;

        Cursor contactsCursor = getContentResolver().query(lookupUri, new String[]{displayName, contactNumber},null, null, null);

        if (contactsCursor.moveToFirst()) {
            name = contactsCursor.getString(contactsCursor.getColumnIndex(displayName));
        }

        return name;
    }

    /**
     * Modeled after the permissions handler I learned from an example in class
     */
    public void handlePermissions(String[] permissions, int requestCode){
            ActivityCompat.requestPermissions(this, permissions, requestCode);
    }

    @Override
    /**
     * An overridden method that describes the actions to take when returning from asking the user
     * for device permissions.
     * TODO: Does not completely work, still behaves funky when people deny requests
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //I had this when I thought there were multiple permissions to check for
        //boolean pass = true;
        //for (int i : grantResults) {pass = (grantResults[i] == PackageManager.PERMISSION_GRANTED && pass != false) ? true : false;}

        switch(requestCode) {
            case PERMISSION_REQUESTS: {
                Map<String, Integer> perms = new HashMap<>();
                //initial setting
                for (String s : permissions) {perms.put(s, permissionGranted);}
                //fill with results
                for (int i = 0; i < permissions.length; i++) {perms.put(permissions[i], grantResults[i]);}

                if (perms.get(permissions[0]) == permissionGranted
                        && perms.get(permissions[1]) == permissionGranted) {
                    fetchConversations();
                }
                break;
            }

            case SMS_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == permissionGranted)
                    fetchConversations();
                if (checkSelfPermission(this.permissions[1]) == permissionGranted)
                    break;
            }

            case CONTACTS_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == permissionGranted)
                    fetchConversations();
                if (checkSelfPermission(this.permissions[0]) == permissionGranted)
                    break;
            }

            //if we get here, then both of the permissions were denied and we need to keep asking for them
            default:
                handlePermissions(permissions, requestCode);
        }
    }
}
