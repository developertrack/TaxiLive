package taxilive.taxt.africa;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import taxilive.taxt.africa.directionmodules.DirectionFinder;
import taxilive.taxt.africa.directionmodules.DirectionFinderListener;
import taxilive.taxt.africa.directionmodules.Route;
import util.MaterialProgressBar;
import util.UrlConstant;

public class Home extends AppCompatActivity implements OnMapReadyCallback, DirectionFinderListener {
    Intent getdata;
    String pickup_lat, pickup_long, drop_lat, drop_long,selected_car_value,id,customer_wallet,book_otp;
    String distance, duration, pickup, drop;
    TextView txt_distance, tv_dropoff, tv_pickup,txt_price;
    private GoogleMap mMap;
    private Button btn_request;
    private String TAG = "so47492459";
    String travel_price,tavel_disance;
    Button bt_request;
    String tag_json_obj = "booking";
    DirectionFinder direction_finder;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    int booking_try=0;
    MaterialProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getdata = getIntent();
        pickup_lat = getdata.getStringExtra("pickup_lat");
        pickup_long = getdata.getStringExtra("pickup_long");
        drop_lat = getdata.getStringExtra("drop_lat");
        drop_long = getdata.getStringExtra("drop_long");
        pickup = getdata.getStringExtra("pickup");
        drop = getdata.getStringExtra("drop");
        selected_car_value=getdata.getStringExtra("selected_car_value");
        customer_wallet=getdata.getStringExtra("customer_wallet");
        id=getdata.getStringExtra("id");

        Log.e("pickup_lat",pickup_lat);
        Log.e("pickup_long",pickup_long);
        Log.e("pickup",pickup);
        Log.e("drop",drop);
        Log.e("drop_lat",drop_lat);
        Log.e("drop_long",drop_long);
        Log.e("customer_wallet",customer_wallet);



        txt_distance = (TextView) findViewById(R.id.txt_distance);
        tv_dropoff = (TextView) findViewById(R.id.tv_dropoff);
        tv_pickup = (TextView) findViewById(R.id.tv_pickup);
        txt_price = (TextView) findViewById(R.id.txt_price);
        bt_request=(Button)findViewById(R.id.bt_request);
        progressBar=(MaterialProgressBar)findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);
        bt_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Integer.valueOf(customer_wallet)<Integer.valueOf(travel_price)){
                    Toast.makeText(Home.this,"Please add amount in your wallet first",Toast.LENGTH_LONG).show();
                }else{
                    book_car();
                }


            }
        });

        tv_dropoff.setText(drop);
        tv_pickup.setText(pickup);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sendRequest();

        getDistanceTime();

    }

    private void sendRequest() {

        try {
            direction_finder= new DirectionFinder(this, pickup, drop);
            direction_finder.execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setCompassEnabled(true);
        LatLng pickup = new LatLng(Double.valueOf(pickup_lat), Double.valueOf(pickup_long));
        LatLng drop = new LatLng(Double.valueOf(drop_lat), Double.valueOf(drop_long));
        originMarkers.add( mMap.addMarker(new MarkerOptions().position(pickup).title("pickup")));
        destinationMarkers.add(mMap.addMarker(new MarkerOptions().position(drop).title("drop")));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(drop, 12f));

    }



    public void book_car() {
        progressBar.setVisibility(View.VISIBLE);
        booking_try=booking_try+1;
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("customer_id", id);
            jsonBody.put("pickup_lat", pickup_lat);
            jsonBody.put("pickup_long", pickup_long);
            jsonBody.put("destination_lat", drop_lat);
            jsonBody.put("destination_long", drop_long);
            jsonBody.put("car_type", selected_car_value);
            jsonBody.put("pickupaddress", pickup);
            jsonBody.put("destinationaddress", drop);
            jsonBody.put("price", travel_price);

            Log.e(tag_json_obj, jsonBody.toString());

//            {"latitute":"25.3529","longitute":"82.9972"}

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    UrlConstant.Book_CAR_URL, jsonBody,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e(tag_json_obj, response.toString());
                            try {
                                String Status = response.getString("result").toString();
                                Log.e("result", Status);
                                final String message = response.getString("message").toString();
                                Log.e("message", message);

//                                book_detail

                                if (Status.equals("success")) {
                                    final JSONObject car_type = response.getJSONObject("driver_car");
                                    final JSONObject driver_profile = response.getJSONObject("driver_profile");
                                    final JSONObject book_detail = response.getJSONObject("book_detail");
                                    Intent home = new Intent(Home.this, PickupArriving.class);
                                    home.putExtra("pickup_lat", pickup_lat);
                                    home.putExtra("pickup_long", pickup_long);
                                    home.putExtra("drop_lat", drop_lat);
                                    home.putExtra("drop_long", drop_long);
                                    home.putExtra("drop", drop);
                                    home.putExtra("pickup", pickup);
                                    home.putExtra("car_reg_number",car_type.getString("car_reg_number"));
                                    home.putExtra("selected_car_value",selected_car_value);
                                    home.putExtra("travel_price", travel_price);
                                    home.putExtra("id",id);
                                    home.putExtra("tavel_disance",tavel_disance);
                                    home.putExtra("profile_image",driver_profile.getString("profile_image"));
                                    home.putExtra("driver_id",driver_profile.getString("id"));
                                    home.putExtra("booking_id",book_detail.getString("booking_id"));
                                    home.putExtra("mobile",driver_profile.getString("mobile"));
                                    home.putExtra("book_otp",response.getString("book_otp"));
                                   startActivity(home);
                                   finish();
                                }else if(booking_try<5){
                                    book_car();
                                }else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(Home.this);
                                            dlgAlert.setMessage("Car Not available, please try again");
                                            dlgAlert.setPositiveButton("OK", null);
                                            dlgAlert.setCancelable(true);
                                            dlgAlert.create().show();
                                        }
                                    });
                                }

                            } catch (Exception e) {

                            }
                            progressBar.setVisibility(View.GONE);

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.e(tag_json_obj, "Error: " + error.getMessage());

                    book_car();
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


    @Override
    public void onDirectionFinderStart() {
        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {

        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 12));
//            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
//            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.add_marker))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.add_marker))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    visible(true).
                    width(10);


            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

//            distance=route.distance.text;
            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
//        String dist=String.valueOf(distance(Double.valueOf(pickup_lat),Double.valueOf(pickup_long),Double.valueOf(drop_lat),Double.valueOf(drop_long)," Kilometers"));
//        txt_distance.setText(String.format("%.2f", Double.valueOf(dist))+"km");

        distance=String.valueOf(distance(Double.valueOf(pickup_lat),Double.valueOf(pickup_long),Double.valueOf(drop_lat),Double.valueOf(drop_long)," Kilometers"));

    }

    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        } else if (unit == "N") {
            dist = dist * 0.8684;
        }

        DecimalFormat f = new DecimalFormat("##.00");
        System.out.println(f.format(dist));

        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


    public void getDistanceTime() {


        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("pickup_lat", pickup_lat);
            jsonBody.put("pickup_long", pickup_long);
            jsonBody.put("destination_lat", drop_lat);
            jsonBody.put("destination_long", drop_long);


            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    UrlConstant.GET_DISTANCE_TIME, jsonBody,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e(tag_json_obj, response.toString());
                            try {
                               distance = response.getString("distance").toString();
                                Log.e("distance", distance);
                               final String time = response.getString("time").toString();
                                Log.e("time", time);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        long l = Math.round(Double.valueOf(distance.substring(0, distance.length() - 2)));

                                        int i = (int) l+1;
                                        int price=20+i*20;
                                        travel_price=String.valueOf(price);
                                        tavel_disance=distance +"KM "+ " in " + time+"MIN";
                                        txt_distance.setText(tavel_disance);
                                        txt_price.setText("ZAR "+String.valueOf(price));
                                    }
                                });

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
