package taxilive.taxt.africa;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import util.UrlConstant;

public class PickupArriving extends AppCompatActivity implements OnMapReadyCallback {
    Intent getdata;
    String pickup_lat, pickup_long, drop_lat, drop_long,selected_car_value,car_reg_number,book_otp;
    String distance, duration, pickup, drop,id,driver_id,booking_id,mobile,profile_image;
    TextView txt_distance, tv_dropoff, tv_pickup,txt_price,cartype,carno,otp;
    ImageView img_driver,img_cartype;
    private GoogleMap mMap;
    private Button btn_request;
    private String TAG = "so47492459";
    String travel_price,tavel_disance;
    LinearLayout cancel_booking,call_driver,share_location;
    String tag_json_obj = "booked_ride";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pickup_page);
        getdata = getIntent();
        pickup_lat = getdata.getStringExtra("pickup_lat");
        pickup_long = getdata.getStringExtra("pickup_long");
        drop_lat = getdata.getStringExtra("drop_lat");
        drop_long = getdata.getStringExtra("drop_long");
        pickup = getdata.getStringExtra("pickup");
        drop = getdata.getStringExtra("drop");
        selected_car_value=getdata.getStringExtra("selected_car_value");
        travel_price = getdata.getStringExtra("travel_price");
        tavel_disance=getdata.getStringExtra("tavel_disance");
        car_reg_number=getdata.getStringExtra("car_reg_number");
        id=getdata.getStringExtra("id");
        driver_id=getdata.getStringExtra("driver_id");
        booking_id=getdata.getStringExtra("booking_id");
        mobile=getdata.getStringExtra("mobile");
        profile_image=getdata.getStringExtra("profile_image");
        book_otp=getdata.getStringExtra("book_otp");
        cancel_booking=(LinearLayout)findViewById(R.id.cancel_booking);
        call_driver=(LinearLayout)findViewById(R.id.call_driver);
        share_location=(LinearLayout)findViewById(R.id.share_location);
        id=getdata.getStringExtra("id");

        Log.e("pickup_lat",pickup_lat);
        Log.e("pickup_long",pickup_long);
        Log.e("drop_lat",drop_lat);
        Log.e("drop_long",drop_long);
        Log.e("pickup",pickup);

        Log.e("profile_image",profile_image);
        Log.e("mobile",mobile);
        Log.e("driver_id",driver_id);
        Log.e("booking_id",booking_id);
        Log.e("id",id);



        call_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", mobile, null));
                startActivity(intent);
            }
        });

        share_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My Location");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, pickup);
                startActivity(Intent.createChooser(sharingIntent,"Share"));
            }
        });

        cancel_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel_Booking();
            }
        });



        txt_distance = (TextView) findViewById(R.id.txt_distance);
        tv_dropoff = (TextView) findViewById(R.id.tv_dropoff);
        tv_pickup = (TextView) findViewById(R.id.tv_pickup);
        txt_price = (TextView) findViewById(R.id.txt_price);
        cartype = (TextView) findViewById(R.id.cartype);
        carno = (TextView) findViewById(R.id.carno);
        otp = (TextView) findViewById(R.id.otp);
        img_driver = (ImageView) findViewById(R.id.img_driver);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.drawable.profile);
        Glide.with(this).load(profile_image).apply(options).into(img_driver);

        cartype.setText(selected_car_value.toUpperCase());

//        int randomPIN = (int)(Math.random()*9000)+1000;
//        String val = ""+randomPIN;
        otp.setText("OTP:"+book_otp);

        tv_dropoff.setText(drop);
        tv_pickup.setText(pickup);

        txt_distance.setText(tavel_disance);
        txt_price.setText("ZAR "+travel_price);
        cartype.setText(selected_car_value);
        carno.setText(car_reg_number);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng pickup = new LatLng(Double.valueOf(pickup_lat), Double.valueOf(pickup_long));
        LatLng drop = new LatLng(Double.valueOf(drop_lat), Double.valueOf(drop_long));
        mMap.addMarker(new MarkerOptions().position(pickup).title("pickup"));
        mMap.addMarker(new MarkerOptions().position(drop).title("drop"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(drop, 12f));

    }


    public void cancel_Booking(){

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("booking_id", booking_id);
            jsonBody.put("customer_id", id);


            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    UrlConstant.POST_CANCEL_BOOKING, jsonBody,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e(tag_json_obj, response.toString());
                            try {
                                String Status = response.getString("result").toString();
                                Log.e("result", Status);
                                final String message = response.getString("message").toString();
                                Log.e("message", message);

                                if(Status.equals("success")){
                                    Toast.makeText(PickupArriving.this,"You had canceled your ride",Toast.LENGTH_LONG).show();
                                    finish();
                                }

                            } catch (Exception e) {

                            }


                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.e(tag_json_obj, "Error: " + error.getMessage());

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

}



