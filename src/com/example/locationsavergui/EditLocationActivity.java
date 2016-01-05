package com.example.locationsavergui;

import Framework.OurLocation;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

public class EditLocationActivity extends FragmentActivity {

	private LocationManager locationManager;
	private String provider;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_location);  
        AppData appData = (AppData)getApplicationContext();
		
        Location location = appData.getBestLocation();
        
        //get Map fragment from fragment Manager
        GoogleMap map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapFragment)).getMap();
        
        //Set current location to current latitude and longitude
        LatLng currentLoc = new LatLng(0,0);
        
        if (location != null) {
            currentLoc = new LatLng(location.getLatitude(),location.getLongitude());
        }
        
        
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 15));
        
        map.addMarker(new MarkerOptions()
        .title("Marker location")
        .position(currentLoc));
        
        appData.currentGroup = null;
        
        try {
        	if (appData.currentLocation == null){
        		appData.currentLocation = OurLocation.createNew(appData.db);
        	}
        	else {
        		
        	}
        } catch (Exception e) {
        
        }
        
        
    }
}