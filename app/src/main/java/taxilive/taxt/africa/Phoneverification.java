package taxilive.taxt.africa;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import util.UrlConstant;

public class Phoneverification extends AppCompatActivity {

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    String OTP, MobileNumber, otp;
    EditText input_otp;
    TextView counter, resend, mob_text;
    ProgressDialog pDialog;
    Button btn_confirm, btn_previous;
    Intent intent;
    String tag_json_obj = "MessageVerificationActivity_TAG";
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String txt_otp = intent.getStringExtra("message");
                input_otp.setText(txt_otp);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messageverification);

        if (checkAndRequestPermissions()) {
            // carry on the normal flow, as the case of  permissions  granted.
        }
        input_otp = findViewById(R.id.input_otp);
        counter = findViewById(R.id.counter);
        resend = findViewById(R.id.resend);
        mob_text = findViewById(R.id.mob_text);
        btn_previous = findViewById(R.id.btn_previous);
        btn_confirm = findViewById(R.id.btn_confirm);

        intent = getIntent();
        OTP = intent.getStringExtra("otp");
        MobileNumber = intent.getStringExtra("mobile");

        mob_text.setText("You will receive an one time password on your mobile number " + MobileNumber);
        input_otp.setText(OTP);
       /* resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otp = input_otp.getText().toString().trim();

                if (otp.isEmpty() || otp.length() < 6) {

                    Toast.makeText(Phoneverification.this, "Enter correct OTP", Toast.LENGTH_LONG).show();

                } else {
                    verifyUser();
                }
            }
        });
        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        new CountDownTimer(45000, 1000) {

            public void onTick(long millisUntilFinished) {
                counter.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                counter.setText("done!");
            }

        }.start();

    }


    public void verifyUser() {

        pDialog = new ProgressDialog(Phoneverification.this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("mobile", MobileNumber);
            jsonBody.put("otp", OTP);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    UrlConstant.VERIFY_OTP, jsonBody,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(final JSONObject response) {
                            Log.e(tag_json_obj, response.toString());
                            try {
                                String Status = response.getString("result");
                                final String message = response.getString("message").toString();

                                if (Status.equals("success")) {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            try {
                                                Intent sign_up = new Intent(Phoneverification.this, HomeScreen.class);
                                                sign_up.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(sign_up);
                                            } catch (Exception e) {

                                            }

                                            pDialog.hide();
                                        }
                                    });

                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            android.support.v7.app.AlertDialog.Builder dlgAlert = new android.support.v7.app.AlertDialog.Builder(Phoneverification.this);
                                            dlgAlert.setMessage(message);
                                            dlgAlert.setPositiveButton("OK", null);
                                            dlgAlert.setCancelable(true);
                                            dlgAlert.create().show();
                                        }
                                    });

                                }

                            } catch (Exception e) {

                            }

                            pDialog.hide();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.e(tag_json_obj, "Error: " + error.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            android.support.v7.app.AlertDialog.Builder dlgAlert = new android.support.v7.app.AlertDialog.Builder(Phoneverification.this);
                            dlgAlert.setMessage("Error while fetching data, please try again");
                            dlgAlert.setPositiveButton("OK", null);
                            dlgAlert.setCancelable(true);
                            dlgAlert.create().show();
                        }
                    });
                    pDialog.hide();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
            };

            MyAppDefault.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    private boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);

        int receiveSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);

        int readSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS);

        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (receiveSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_MMS);
        }
        if (result != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (readSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

}
