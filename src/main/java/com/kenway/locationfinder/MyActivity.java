package com.kenway.locationfinder;

import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class MyActivity extends FragmentActivity implements LocationListener {

    GoogleMap googleMap;
    protected MapDataSource mapDataSource;
    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        setContentView(R.layout.activity_my);

        SupportMapFragment supportMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        googleMap = supportMapFragment.getMap();
        googleMap.setMyLocationEnabled(true);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);



    }

    @Override
    public void onLocationChanged(Location location) {
        TextView locationTv = (TextView) findViewById(R.id.latlongLocation);
        final double latitude = location.getLatitude();
        final double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        // get current time
        DateFormat format = new SimpleDateFormat(
                "MMM d, yyyy, h:mm a");

        TimeZone cst = TimeZone.getTimeZone("US/Central");
        GregorianCalendar gc = new GregorianCalendar(cst);
        Date now = gc.getTime();

        googleMap.addMarker(new MarkerOptions().position(latLng).title("I'm here").snippet(format.format(now)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        locationTv.setText("Latitude:" + latitude + ", Longitude:" + longitude);
//        //store in db

        mapDataSource = new MapDataSource(MyActivity.this); // create a db
        mapDataSource.openDataBase();

        Cursor cursor = mapDataSource.selectAllLocations();
        LatLng prevLoc;

        while (cursor.moveToNext()) {
            prevLoc = new LatLng(cursor.getExtras().getDouble("Latitude"), cursor.getExtras().getDouble("Longitude"));
            googleMap.addMarker(new MarkerOptions().position(prevLoc));

        }
        cursor.close();

        mapDataSource.insertLocation(latitude, longitude); // add location data
        //mapDataSource.insertLocation(10.0,20.0);
        mapDataSource.close();
    }


    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }




}
