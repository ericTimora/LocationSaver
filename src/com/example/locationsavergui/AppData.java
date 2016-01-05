/** ApppData
 * This is where everything is stored
 */
package com.example.locationsavergui;

import android.app.Application;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import Framework.*;
import database.*;

public class AppData extends Application{
	DB db = new DB(this);
	User thisUser;
	boolean isLoaded = false;
	String currentGroupName = "none";
	LocationGroup currentGroup;
	OurLocation currentLocation;
	LocationRelation currentRelation;
	double currentLatitude;
	double currentLongitude;
	
	LocationManager locationManager;
	
	
	public Location getBestLocation(){
        String provider = locationManager.getBestProvider(new Criteria(), false);
        Location location = locationManager.getLastKnownLocation(provider);
        
        return location;
	}
	
	public void init(){	
		if(!isLoaded){
			System.out.println("init");
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			
			
	        try {
	        	if (thisUser == null){
					thisUser = User.getUser(db);
					thisUser.sync();
					System.out.println(thisUser.get("userid"));
	        	}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			isLoaded = true;
		}
	}
}
