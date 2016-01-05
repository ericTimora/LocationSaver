package com.example.locationsavergui;

import java.util.ArrayList;

import exception.NotFoundException;

import Framework.LocationGroup;
import Framework.LocationKidList;
import Framework.LocationRelation;
import Framework.OurLocation;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ListActivity extends Activity {
	
    private Button newLocation;
    private Button mapView;

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
       
        
        String selectedName = menuList.get(item.getItemId());
        LocationGroup id = idList.get(item.getItemId());
        
        System.out.println(selectedName);
        
        if(selectedName == ADD_GROUP){
        	alert.show();
        	return false;
        }
        else if (selectedName == ALL_GROUP){
        	//TODO: Make the all group work 
        }
        openListView(selectedName, id);
        //Show all of the pins on the map
        return false;
    }
    
    
    private void openListView(String selectedName, LocationGroup id){
    	AppData appData = ((AppData) getApplicationContext());
    	
		appData.currentGroup = id;
    	
    	Intent intent = new Intent(ListActivity.this, ListActivity.class);
		startActivity(intent);
    }
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	final AppData appData = (AppData) getApplicationContext();
    	System.out.println("CURRENT GROUP NAME = " + appData.currentGroupName);
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        final ArrayList<OurLocation> allKids;
        


        newLocation = (Button)findViewById(R.id.saveButton);
        mapView = (Button)findViewById(R.id.mapButton);
        
        
        try { 
        System.out.println(appData.thisUser.get("username"));
        System.out.println(appData.thisUser.get("password"));
        appData.thisUser.getLocations().printKids();
        } catch (Exception e){
        
        }
        if (appData.currentGroup != null) {
        	allKids = new ArrayList<OurLocation>();
        	for (LocationRelation it : appData.currentGroup.getLocations().getKids()){
        		try {
					allKids.add(it.getLocation());
				} catch (Exception e) {
					e.printStackTrace();
				}
        	}
        }
        else if (appData.currentGroup == null){
        	allKids = appData.thisUser.getLocations().getKids();
        } else {
        	ArrayList<LocationGroup> a = appData.thisUser.getLocationGroups().getKids();
        	allKids = new ArrayList<OurLocation>();
        	for (LocationRelation it : appData.currentGroup.getLocations().getKids()){
        		try {
        			allKids.add(it.getLocation());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }
        
        newLocation.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(ListActivity.this, EditLocationActivity.class);
				startActivity(intent);
			}
		});
       mapView.setOnClickListener(new View.OnClickListener() {
		
	        @Override
		    public void onClick(View v) {
			    // TODO Auto-generated method stub
	        	Intent intent = new Intent(ListActivity.this, MainActivity.class);
	        	startActivity(intent);
		}
       });
       
  /*      
        String[] testList = new String[] {"11/02/2014 3:14pm", "10/29/2014 8:36am",
        		"10/27/2014 5:56pm", "10/27/2014 12:00pm", "10/26/2014 10:43pm",
        		"10/18/2014 10:30pm"
        };

        ArrayList<String> locations = new ArrayList<String>();
        for (int i = 0; i < testList.length; i++){
        	locations.add(testList[i]);
        }*/

        ArrayAdapter <OurLocation> listAdapter = new ArrayAdapter <OurLocation>(this, android.R.layout.simple_list_item_1, allKids);
        ListView listView = (ListView) findViewById(R.id.locationsList);
        listView.setAdapter(listAdapter);
        
        listView.setOnItemClickListener(new OnItemClickListener(){        
        
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			appData.currentLocation = (OurLocation) allKids.get(position);
			Intent intent = new Intent(ListActivity.this, EditLocationActivity.class);
			startActivity(intent);
			
		}
    });
    }
}