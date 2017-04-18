package com.example.lfswe.smsfetch;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * An activity that presents the user with a way to compose and send text messages. Will always be
 * passed as an intent from MainActivity.
 * Created by MacGyver on 3/26/2017.
 */

public class ComposeActivity extends Activity {

    //passed contact info
    private String receiverName;
    private String receiverNumber;

    //display objects
    Button sendButton;
    Button recordButton;
    EditText textField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        sendButton = (Button) findViewById(R.id.sendButton);
        recordButton = (Button) findViewById(R.id.recordButton);
        textField = (EditText) findViewById(R.id.messageBody);

        receiverName = getIntent().getStringExtra("RECIPIENT_NAME");
        receiverNumber = getIntent().getStringExtra("RECIPIENT_NUMBER");
        final String SMS_SENT = "SMS_SENT";
        final String SMS_DELIVERED = "SMS_DELIVERED";

        final PendingIntent sentPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_SENT), 0);
        final PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_DELIVERED), 0);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        switch (getResultCode()) {
                            case Activity.RESULT_OK:
                                Toast.makeText(context, "SMS sent successfully", Toast.LENGTH_SHORT).show();
                                break;
                            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                Toast.makeText(context, "Generic failure cause", Toast.LENGTH_SHORT).show();
                                break;
                            case SmsManager.RESULT_ERROR_NO_SERVICE:
                                Toast.makeText(context, "Service is currently unavailable", Toast.LENGTH_SHORT).show();
                                break;
                            case SmsManager.RESULT_ERROR_NULL_PDU:
                                Toast.makeText(context, "No pdu provided", Toast.LENGTH_SHORT).show();
                                break;
                            case SmsManager.RESULT_ERROR_RADIO_OFF:
                                Toast.makeText(context, "Radio was explicitly turned off", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }, new IntentFilter(SMS_SENT));

                registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        switch (getResultCode()) {
                            case Activity.RESULT_OK:
                                Toast.makeText(context, "SMS sent successfully", Toast.LENGTH_SHORT).show();
                                break;
                            case Activity.RESULT_CANCELED:
                                Toast.makeText(context, "SMS not delivered", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }, new IntentFilter(SMS_DELIVERED));
                SmsManager smsManager = SmsManager.getDefault();
                try {
                    smsManager.sendTextMessage(receiverNumber, null, textField.getText().toString(), sentPendingIntent, deliveredPendingIntent);
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        });

        /**
         * TODO: Implement voice record button, may not be necessary depending on whether daydream keyboard is viable
         */

    }
}
