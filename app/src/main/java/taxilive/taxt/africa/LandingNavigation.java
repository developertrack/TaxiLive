package taxilive.taxt.africa;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import taxilive.taxt.africa.ridehistorydata.Ridehistory;
import util.AppUtils;
import util.FetchAddressIntentService;
import util.UrlConstant;

public class LandingNavigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private static String TAG = "MAP LOCATION";
    protected String mAddressOutput;
    protected String mAreaOutput;
    protected String mCityOutput;
    protected String mStateOutput;
    Context mContext;
    TextView mLocationAddress, tv_dropoff;
    LinearLayout micro, mini, sedan, rental, luxury;
    Toolbar mToolbar;
    View mapView;
    int selector_add = 1;
    String selected_car_value = "none";
    Button bt_request;
    String pickup_lat, pickup_long, drop_lat = "0", drop_long = "0",id;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    //    TextView mLocationMarkerText;
    private LatLng mCenterLatLong;
    private AddressResultReceiver mResultReceiver;
    Toolbar toolbar;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    Intent getdata;
    String name,email;
    TextView navUsername,useremail,txt_micro,txt_mini,txt_sedan,txt_rental,txt_luxury;
    Random r = new Random();
    int Low = 2;
    int High = 14;
    int result=1;
    String tag_json_obj = "loc_update_landing";
    String customer_id,customer_wallet;
    SupportMapFragment mapFragment;
    String update_lat,update_long;
    @Override
    protected void onResume() {
        tv_dropoff.setText("Drop off Address");
        mapFragment.getMapAsync(this);
        super.onResume();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_navigation);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Book Rides");

        getdata=getIntent();
        name=getdata.getStringExtra("first_name")+" "+getdata.getStringExtra("last_name");
        email=getdata.getStringExtra("email");
        id=getdata.getStringExtra("customer_id");
        customer_id=getdata.getStringExtra("customer_id");
        customer_wallet=getdata.getStringExtra("customer_wallet");
        Log.e("customer_id",id);

        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                LandingNavigation.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        navUsername = headerView.findViewById(R.id.name);
        useremail = headerView.findViewById(R.id.email);

        navUsername.setText(name);
        useremail.setText(email);

        mContext = this;


        mLocationAddress = (TextView) findViewById(R.id.Address);
        tv_dropoff = (TextView) findViewById(R.id.tv_dropoff);
        micro = (LinearLayout) findViewById(R.id.micro);
        mini = (LinearLayout) findViewById(R.id.mini);
        sedan = (LinearLayout) findViewById(R.id.sedan);
        rental = (LinearLayout) findViewById(R.id.rental);
        luxury = (LinearLayout) findViewById(R.id.luxury);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        txt_micro = (TextView) findViewById(R.id.txt_micro);
        txt_mini = (TextView) findViewById(R.id.txt_mini);
        txt_sedan = (TextView) findViewById(R.id.txt_sedan);
        txt_rental = (TextView) findViewById(R.id.txt_rental);
        txt_luxury = (TextView) findViewById(R.id.txt_luxury);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);


        bt_request = (Button) findViewById(R.id.bt_request);

        bt_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (drop_lat.equals("0")) {
                    Toast.makeText(LandingNavigation.this, "Please select drop location", Toast.LENGTH_LONG).show();
                    return;
                }
                if (selected_car_value.equalsIgnoreCase("none")) {
                    Toast.makeText(LandingNavigation.this, "Please select car type", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent home = new Intent(LandingNavigation.this, Home.class);
                home.putExtra("pickup_lat", pickup_lat);
                home.putExtra("pickup_long", pickup_long);
                home.putExtra("drop_lat", drop_lat);
                home.putExtra("drop_long", drop_long);
                home.putExtra("drop", tv_dropoff.getText().toString());
                home.putExtra("pickup", mLocationAddress.getText().toString());
                home.putExtra("selected_car_value",selected_car_value);
                home.putExtra("id",id);
                home.putExtra("customer_wallet",customer_wallet);
                startActivity(home);
            }
        });



        mLocationAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openAutocompleteActivity();
                selector_add = 1;

            }

        });

        tv_dropoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openAutocompleteActivity();
                selector_add = 2;

            }


        });

        micro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int color = Color.parseColor("#FFCB09");
                int color1 = Color.parseColor("#FFFFFF");
                ((GradientDrawable) micro.getBackground()).setColor(color);
                ((GradientDrawable) mini.getBackground()).setColor(color1);
                ((GradientDrawable) sedan.getBackground()).setColor(color1);
                ((GradientDrawable) rental.getBackground()).setColor(color1);
                ((GradientDrawable) luxury.getBackground()).setColor(color1);
                selected_car_value = "Micro";

            }
        });

        mini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int color = Color.parseColor("#FFCB09");
                int color1 = Color.parseColor("#FFFFFF");
                ((GradientDrawable) micro.getBackground()).setColor(color1);
                ((GradientDrawable) mini.getBackground()).setColor(color);
                ((GradientDrawable) sedan.getBackground()).setColor(color1);
                ((GradientDrawable) rental.getBackground()).setColor(color1);
                ((GradientDrawable) luxury.getBackground()).setColor(color1);
                selected_car_value = "Mini";

            }
        });

        sedan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int color = Color.parseColor("#FFCB09");
                int color1 = Color.parseColor("#FFFFFF");
                ((GradientDrawable) micro.getBackground()).setColor(color1);
                ((GradientDrawable) mini.getBackground()).setColor(color1);
                ((GradientDrawable) sedan.getBackground()).setColor(color);
                ((GradientDrawable) rental.getBackground()).setColor(color1);
                ((GradientDrawable) luxury.getBackground()).setColor(color1);
                selected_car_value = "Sedan";

            }
        });

        rental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int color = Color.parseColor("#FFCB09");
                int color1 = Color.parseColor("#FFFFFF");
                ((GradientDrawable) micro.getBackground()).setColor(color1);
                ((GradientDrawable) mini.getBackground()).setColor(color1);
                ((GradientDrawable) sedan.getBackground()).setColor(color1);
                ((GradientDrawable) rental.getBackground()).setColor(color);
                ((GradientDrawable) luxury.getBackground()).setColor(color1);
                selected_car_value = "Rental";

            }
        });

        luxury.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int color = Color.parseColor("#FFCB09");
                int color1 = Color.parseColor("#FFFFFF");
                ((GradientDrawable) micro.getBackground()).setColor(color1);
                ((GradientDrawable) mini.getBackground()).setColor(color1);
                ((GradientDrawable) sedan.getBackground()).setColor(color1);
                ((GradientDrawable) rental.getBackground()).setColor(color1);
                ((GradientDrawable) luxury.getBackground()).setColor(color);
                selected_car_value = "Luxury";

            }
        });

        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);
        mResultReceiver = new AddressResultReceiver(new Handler());
        closeKeyboard();
        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            if (!AppUtils.isLocationEnabled(mContext)) {
                // notify user
                final AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setMessage("Location not enabled!");
                dialog.setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub

                    }
                });

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.show();
                    }
                });


            }
            buildGoogleApiClient();
        } else {
            Toast.makeText(mContext, "Location not supported in this device", Toast.LENGTH_SHORT).show();
        }


            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    updateLocation(update_lat,update_long);
                }
            }, 0, 15000);


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
// Sync the toggle state after onRestoreInstanceState has occurred.
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "OnMapReady");
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.d("Camera postion change" + "", cameraPosition + "");
                mCenterLatLong = cameraPosition.target;


                mMap.clear();

                try {

                    Location mLocation = new Location("");
                    mLocation.setLatitude(mCenterLatLong.latitude);
                    mLocation.setLongitude(mCenterLatLong.longitude);

                    startIntentService(mLocation);
                    final String addressdata = getAddress(mCenterLatLong.latitude, mCenterLatLong.longitude);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (selector_add == 2) {
                                tv_dropoff.setText(addressdata);
                                drop_lat = String.valueOf(mCenterLatLong.latitude);
                                drop_long = String.valueOf(mCenterLatLong.longitude);
                            }

                            if (selector_add == 1) {
                                mLocationAddress.setText(addressdata);
                                pickup_lat = String.valueOf(mCenterLatLong.latitude);
                                pickup_long = String.valueOf(mCenterLatLong.longitude);
                                update_lat= String.valueOf(mCenterLatLong.latitude);
                                update_long=String.valueOf(mCenterLatLong.longitude);

//                                updateLocation(String.valueOf(mCenterLatLong.latitude), String.valueOf(mCenterLatLong.latitude));
                            }
//                            mLocationAddress.setText(addressdata);
                        }
                    });

//                    mLocationMarkerText.setText("Lat : " + mCenterLatLong.latitude + "," + "Long : " + mCenterLatLong.longitude);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        mMap.setMyLocationEnabled(true);
//        mMap.getUiSettings().setMyLocationButtonEnabled(true);
//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            changeMap(mLastLocation);
            Log.d(TAG, "ON connected");

        } else
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, this);

            } catch (Exception e) {
                e.printStackTrace();
            }
        try {
            @SuppressLint("RestrictedApi") LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location != null)
                changeMap(location);
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            mGoogleApiClient.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //finish();
            }
            return false;
        }
        return true;
    }

    private void changeMap(final Location location) {

        Log.d(TAG, "Reaching map" + mMap);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        // check if map is created successfully or not
        if (mMap != null) {
            mMap.getUiSettings().setZoomControlsEnabled(false);
            LatLng latLong;


            latLong = new LatLng(location.getLatitude(), location.getLongitude());

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLong).zoom(14f).tilt(70).build();
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);

            rlp.addRule(RelativeLayout.ALIGN_PARENT_END, 0);
            rlp.addRule(RelativeLayout.ALIGN_END, 0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rlp.setMargins(30, 0, 0, 100);
            mMap.setMyLocationEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLocationAddress.setText(getAddress(location.getLatitude(), location.getLongitude()));

                    update_lat= String.valueOf(location.getLatitude());
                    update_long=String.valueOf(location.getLongitude());
                    updateLocation(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));


                        }
            });
//            mLocationMarkerText.setText("Lat : " + location.getLatitude() + "," + "Long : " + location.getLongitude());
            startIntentService(location);


        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    protected void displayAddressOutput() {
//          mLocationAddress.setText(mAddressOutput);
        try {
//            if (mAreaOutput != null)
//                // mLocationText.setText(mAreaOutput+ "");
//
//                mLocationAddress.setText(mAddressOutput);
            //mLocationText.setText(mAreaOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService(Location mLocation) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, mLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }

    private void openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(mContext, data);

                // TODO call location based filter


                LatLng latLong;


                latLong = place.getLatLng();
                if (selector_add == 2) {
                    tv_dropoff.setText(place.getName() + "");
                }

                if (selector_add == 1) {
                    mLocationAddress.setText(place.getName() + "");
                    update_lat= String.valueOf(latLong.latitude);
                    update_long=String.valueOf(latLong.longitude);
                    updateLocation(String.valueOf(latLong.latitude), String.valueOf(latLong.longitude));

                }

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLong).zoom(14f).tilt(70).build();

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.setMyLocationEnabled(true);
                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));


            }


        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(mContext, data);
        } else if (resultCode == RESULT_CANCELED) {
            // Indicates that the activity closed before a selection was made. For example if
            // the user pressed the back button.
        }
    }

    private void closeKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {

                Address address = addresses.get(0);
                result.append(address.getAddressLine(0));
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return result.toString();
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(AppUtils.LocationConstants.RESULT_DATA_KEY);

            mAreaOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_AREA);

            mCityOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_CITY);
            mStateOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_STREET);

            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == AppUtils.LocationConstants.SUCCESS_RESULT) {
                //  showToast(getString(R.string.address_found));


            }


        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.landing_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_payment) {
        Log.e("id on call",customer_id);
        Intent add_wallet=new Intent(LandingNavigation.this,PaymentPage.class);
        add_wallet.putExtra("id",customer_id);
        startActivity(add_wallet);

        } else if (id == R.id.nav_trips) {
            Intent add_wallet=new Intent(LandingNavigation.this,Ridehistory.class);
            add_wallet.putExtra("id",customer_id);
            startActivity(add_wallet);

        } else if (id == R.id.nav_free_rides) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateLocation(final String lat, final String lon) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("latitute", lat);
            jsonBody.put("longitute", lon);

            Log.e(tag_json_obj, jsonBody.toString());

//            {"latitute":"25.3529","longitute":"82.9972"}

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    UrlConstant.FETCH_CAR_URL, jsonBody,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e(tag_json_obj, response.toString());
                            try {
                                String Status = response.getString("result").toString();
                                Log.e("result", Status);
//                                final String message = response.getString("message").toString();
//                                Log.e("message", message);

                                final JSONObject car_type = response.getJSONObject("car_type");
                                Log.e("car_type", car_type.toString());
                                if (Status.equals("success")) {
//                                    "car_type":{"":0,"Luxury":0,"Micro":"1","Mini":"3","Rental":"1","Sedan":"1","Shuttle":"1"}

                                    if(car_type.getString("Mini").equals("0")){
                                        mini.setClickable(false);
                                        txt_mini.setText("No Cabs");
                                    }else{
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mini.setClickable(true);
                                                result = r.nextInt(High - Low) + Low;
                                                txt_mini.setText(String.valueOf(result) + " " + "min.");
                                            }
                                        });
                                    }
                                    if(car_type.getString("Luxury").equals("0")){
                                        luxury.setClickable(false);
                                        txt_luxury.setText("No Cabs");
                                    }else{
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                luxury.setClickable(true);
                                                result = r.nextInt(High - Low) + Low;
                                                txt_luxury.setText(String.valueOf(result) + " " + "min.");
                                            }
                                        });
                                    }

                                    if(car_type.getString("Micro").equals("0")){
                                        micro.setClickable(false);
                                        txt_micro.setText("No Cabs");
                                    }else{
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                micro.setClickable(true);
                                                result = r.nextInt(High - Low) + Low;
                                                txt_micro.setText(String.valueOf(result) + " " + "min.");
                                            }
                                        });
                                    }


                                    if(car_type.getString("Rental").equals("0")){
                                        rental.setClickable(false);
                                        txt_rental.setText("No Cabs");
                                    }else{
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                rental.setClickable(true);
                                                result = r.nextInt(High - Low) + Low;
                                                txt_rental.setText(String.valueOf(result) + " " + "min.");
                                            }});
                                    }

                                    if(car_type.getString("Sedan").equals("0")){
                                        sedan.setClickable(false);
                                        txt_sedan.setText("No Cabs");
                                    }else{
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                sedan.setClickable(true);
                                                result = r.nextInt(High - Low) + Low;
                                                txt_sedan.setText(String.valueOf(result) + " " + "min.");
                                            }});
                                    }


                                } else {
                               /*     runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(LandingNavigation.this);
                                            dlgAlert.setMessage(message);

                                            dlgAlert.setPositiveButton(message, null);

                                            dlgAlert.setCancelable(true);
                                            dlgAlert.create().show();
                                        }
                                    });
*/
                                }

                            } catch (Exception e) {

                            }


                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.e(tag_json_obj, "Error: " + error.getMessage());
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(LandingNavigation.this);
//                            dlgAlert.setMessage("Error while fetching data, please try again");
//                            dlgAlert.setPositiveButton("OK", null);
//                            dlgAlert.setCancelable(true);
//                            dlgAlert.create().show();
//                        }
//                    });

                    updateLocation(lat,lon);

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
