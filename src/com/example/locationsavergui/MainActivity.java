package com.example.locationsavergui;


import java.util.ArrayList;
import java.util.List;

import Framework.CrumbKidList;
import Framework.LocationGroup;
import Framework.LocationGroupKidList;
import Framework.LocationKidList;
import Framework.LocationRelation;
import Framework.LocationRelationsKidList;
import Framework.OurLocation;
import Framework.User;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import exception.NotFoundException;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;

public class MainActivity extends FragmentActivity {
	private Button saveButton;
	private Button listButton;
	private LocationManager locationManager;
	private String provider;
	private GoogleMap map;
	
	
	private final String ADD_GROUP = "New Group";
	private final String ALL_GROUP = "All";
	
	ArrayList<String> menuList = new ArrayList<String>();
	ArrayList<LocationGroup> idList = new ArrayList<LocationGroup>();
    /**
     * Gets called every time the user presses the menu button.
     * Use if your menu is dynamic.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	AppData appData = ((AppData) getApplicationContext());
    	appData.init();
        menu.clear();
        menuList = new ArrayList<String>();
        idList = new ArrayList<LocationGroup>();
        
        menuList.add(ALL_GROUP);
        idList.add(null);
        
        ArrayList<LocationGroup> bla = appData.thisUser.getLocationGroups().getKids();
        for (LocationGroup it : bla){
        	String name;
			try {
				name = it.get("groupname");
				menuList.add(name);
				
				idList.add(it);
				System.out.println(name);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
        }
        
        menuList.add(ADD_GROUP);
        idList.add(null);
        
        for (int i = 0; i < menuList.size(); i++){
        	System.out.println(i + " -> " + menuList.get(i));
        	menu.add(0, i, Menu.NONE, menuList.get(i)).setIcon(R.drawable.common_signin_btn_text_pressed_dark);
        }
        
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	AppData appData = ((AppData) getApplicationContext());
    	//Start the input box code
    	AlertDialog.Builder  alert = new AlertDialog.Builder(this);
    	alert.setTitle("Add new group");
    	alert.setMessage("Enter group name");
    	final EditText input = new EditText(this);
    	alert.setView(input);
    	alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				AppData appData = ((AppData) getApplicationContext());
				String newGroupName = input.getText().toString();
	        	if(newGroupName == null || newGroupName.equals(ADD_GROUP) || newGroupName.equals(ALL_GROUP) || newGroupName.equals("")){
	        		//Cant have the name be the same as the add group button
	        		Context context = getApplicationContext();
	        		CharSequence text = "Not valid name, try again";
	        		int duration = Toast.LENGTH_SHORT;

	        		Toast toast = Toast.makeText(context, text, duration);
	        		toast.show();
	        		
	        		return;
	        	}
	        	try {
					LocationGroup newGroup = LocationGroup.createNew(appData.db);
					newGroup.set("groupname", newGroupName);
					newGroup.set("userid", appData.thisUser.get("userid"));
					appData.thisUser.getLocationGroups().add(newGroup);
					appData.currentGroup = newGroup;
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
    	//End input box code
    	
        super.onOptionsItemSelected(item);
       
        int itemLoc = item.getItemId();
        
        String selectedName = menuList.get(item.getItemId());
        LocationGroup id = idList.get(item.getItemId());
        
        System.out.println(selectedName);
        
        if(selectedName == ADD_GROUP){
        	alert.show();
        	return false;
        }
        
        map.clear();
        //Clear the map, now add the locations 
        if (selectedName == ALL_GROUP){
        	ArrayList<OurLocation> locs = appData.thisUser.getLocations().getKids();
        	for(OurLocation loc : locs){
        		add_location_to_map(loc);
        	}
        	return false;
        }
        
    	ArrayList<LocationRelation> location = id.getLocations().getKids();
    	for (LocationRelation locRel : location){
    		OurLocation loc;
			try {
				loc = locRel.getLocation();
				add_location_to_map(loc);
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
        return false;
    }
 
    private void add_location_to_map(OurLocation loc){
    	try {
			String lat_str = loc.get("latitude");
			String lgn_str = loc.get("longitude");
			
			double lat_dou = Double.parseDouble(lat_str);
			double lgn_dou = Double.parseDouble(lgn_str);
			
			String location_name = loc.get("name");
			String desc = loc.get("description");
			
	    	LatLng testLoc = new LatLng(lat_dou, lgn_dou);
	        map.addMarker(new MarkerOptions()
	        .title(location_name)
	        .snippet(desc)
	        .position(testLoc));
			
			System.out.println("#@CORDS=" + lat_str + "," + lgn_str);
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (Exception e2){
			e2.printStackTrace();
		}
    }
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	AppData appData = ((AppData) getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appData.init();
        
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment)).getMap();
        map.clear();
        map.setMyLocationEnabled(true);
        
        //Setup location manager
        Location location = appData.getBestLocation();
        
        //retrieve current Latitude/Longitude upon creation
        double lat;
        double lng;
        if(location != null){
	        lat = (location.getLatitude());
	        lng = (location.getLongitude());
	        
	        appData.currentLatitude = lat;
	        appData.currentLongitude = lng;
        }
        else{
        	//TODO: Fix gps
        	System.out.println("ERROR");
        	lat = 0;
        	lng = 0;
        	
	        appData.currentLatitude = lat;
	        appData.currentLongitude = lng;
        }
        
        
    
        LatLng currentLoc = new LatLng(lat, lng);
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 5));
       /* map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 15));
        */

    
        //Add all of the markers to the map
    	ArrayList<OurLocation> locs = appData.thisUser.getLocations().getKids();
    	for(OurLocation loc : locs){
    		add_location_to_map(loc);
    	}
        
        ArrayList<LocationGroup> bla = appData.thisUser.getLocationGroups().getKids();
        for (LocationGroup it : bla){
        	String name;
			try {
				name = it.get("groupname");
				System.out.println(name);
			} catch (NotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	ArrayList<LocationRelation> locRel = it.getLocations().getKids();
        	for (LocationRelation loc : locRel){
        		
				try {
					String alat = loc.get("latitude");
					String alog = loc.get("longitude");
					System.out.println(alat);
				} catch (NotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
    
        	}
        }
         saveButton = (Button) findViewById(R.id.saveButton); 
         listButton = (Button) findViewById(R.id.listButton);
         
         saveButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				Intent intent = new Intent(MainActivity.this, EditLocationActivity.class);
				startActivity(intent);
				
			}
		}); 
         listButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, ListActivity.class);
				startActivity(intent);
	
			}
		});
    }
}
