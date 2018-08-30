package util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;

/**
 * Created by riya on 17/2/18.
 */

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage =
                            SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();
                    try {
                        if (senderNum.equals("HP-TRKUSS")) {
                            Intent smsIntent = new Intent("otp");
                            smsIntent.putExtra("message", message.substring(message.length() - 6));
                            LocalBroadcastManager.getInstance(context).sendBroadcast(smsIntent);
                        }
                    } catch (Exception e) {
                    }

                }
            }

        } catch (Exception e) {

        }
    }

}
