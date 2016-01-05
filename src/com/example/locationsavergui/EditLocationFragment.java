package com.example.locationsavergui;

import java.util.ArrayList;
import java.util.Calendar;

import exception.NotFoundException;


import Framework.LocationGroup;
import Framework.LocationRelation;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;



public class EditLocationFragment extends Fragment implements OnClickListener {
	
	AppData appData;
	
	private EditText locationName;
	private EditText locationDescription;
	private Spinner groupSpinner;
	private EditText time;
	private Button saveButton;
	private ArrayAdapter<CharSequence> adapter;
	boolean existing = false;
	private final String ALL_GROUP = "All";
	private final String ADD_GROUP = "New group";
	
	ArrayList<String> menuList= new ArrayList<String>();
	ArrayList<String> idList= new ArrayList<String>();
	
	@Override
 	public View onCreateView(LayoutInflater inflater, ViewGroup parent, 
			Bundle savedInstanceState) {
		

		View v = inflater.inflate(R.layout.edit_location_fragment, parent, false);
		appData = ((AppData) getActivity().getApplicationContext());
		Calendar calendar = Calendar.getInstance();
		
		locationName = (EditText) v.findViewById(R.id.location_name_entry);
		locationDescription = (EditText) v.findViewById(R.id.description_entry);
		groupSpinner = (Spinner) v.findViewById(R.id.group_spinner);
		time = (EditText) v.findViewById(R.id.time_entry);
		saveButton = (Button) v.findViewById(R.id.save_changes_button);
		
		adapter = new ArrayAdapter<CharSequence> (getActivity(), android.R.layout.simple_spinner_item );
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		//This is the list of groups that we will build
		menuList= new ArrayList<String>();
		idList= new ArrayList<String>();
		
		//This is the all group, it will be the first one and can not be changed
		//the id is not needed but we need to fill in the index in the id list so add some junk to the list
		menuList.add(ALL_GROUP);
		idList.add("BAD ID");
		
        ArrayList<LocationGroup> bla = appData.thisUser.getLocationGroups().getKids();
        for (LocationGroup it : bla){
        	String name;
			try {
				name = it.get("groupname");
				menuList.add(name);
				idList.add(it.getPrimaryKey());
				System.out.println(name);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
        }
		
        //This is the new group button
        //the id is not needed but we need to fill in the index in the id list so add some junk to the list
        menuList.add(ADD_GROUP);
		idList.add("BAD ID");
        
        for(int i = 0; i < menuList.size(); i++){
        	adapter.add(menuList.get(i));
        	
        }
		
        
		groupSpinner.setAdapter(adapter);
		
		groupSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String selectedItem = groupSpinner.getSelectedItem().toString();
				System.out.println("CALLED " + selectedItem);
				
				
				if(selectedItem.equals(ADD_GROUP)){
					AlertDialog.Builder  alert = new AlertDialog.Builder(getActivity());
			    	alert.setTitle("Add new group");
			    	alert.setMessage("Enter group name");
			    	final EditText input = new EditText(getActivity());
			    	alert.setView(input);
			    	alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//AppData appData = ((AppData) getApplicationContext());
							String newGroupName = input.getText().toString();
				        	if(newGroupName == null || newGroupName.equals(ADD_GROUP) || newGroupName.equals(ALL_GROUP) || newGroupName.equals("")){
				        		//Cant have the name be the same as the add group button
				        		CharSequence text = "Not valid name, try again";
				        		Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
				        		groupSpinner.setSelection(0);
				        		return;
				        	}
				        
				        	try {
								LocationGroup newGroup = LocationGroup.createNew(appData.db);
								newGroup.set("groupname", newGroupName);
								newGroup.set("userid", appData.thisUser.get("userid"));
								appData.thisUser.getLocationGroups().add(newGroup);
								appData.currentGroup = newGroup;
								
								
						    	adapter.clear();
						    	
						    	//Rebuild the spinner list because we need to add something in the middle
						    	
						    	menuList= new ArrayList<String>();
								idList= new ArrayList<String>();
								
								//This is the all group, it will be the first one and can not be changed
								//the id is not needed but we need to fill in the index in the id list so add some junk to the list
								menuList.add(ALL_GROUP);
								idList.add("BAD ID");
								
						        ArrayList<LocationGroup> bla = appData.thisUser.getLocationGroups().getKids();
						        for (LocationGroup it : bla){
						        	String name;
									try {
										name = it.get("groupname");
										menuList.add(name);
										idList.add(it.getPrimaryKey());
										//System.out.println(name);
									} catch (NotFoundException e) {
										e.printStackTrace();
									}
						        }
								
						        //This is the new group button
						        //the id is not needed but we need to fill in the index in the id list so add some junk to the list
						        menuList.add(ADD_GROUP);
								idList.add("BAD ID");
						        
						        for(int i = 0; i < menuList.size(); i++){
						        	adapter.add(menuList.get(i));
						        	
						        }
								
								
							} catch (Exception e) {
								e.printStackTrace();
							}
							
						}
					});
				
			    	alert.show();
			    
			    	
			    	
				}
				
				
				
			}

			public void onNothingSelected(AdapterView<?> parent) {
				return;
			}});
	 
		
		time.setText(calendar.getTime().toString());
		saveButton.setOnClickListener(this);
		
		if (appData.currentLocation != null){
			try {
				locationName.setText(appData.currentLocation.get("name"));
				locationDescription.setText(appData.currentLocation.get("description"));
				appData.currentLatitude = Double.parseDouble(appData.currentLocation.get("latitude"));
				appData.currentLongitude = Double.parseDouble(appData.currentLocation.get("longitude"));
				existing = true;
			}catch (Exception e){
				System.out.println("Error");
				System.err.println(e);
			}
		}

		return v;
	}
	
	@Override
	public void onClick(View v) {
		try {
			AppData appData = (AppData)getActivity().getApplicationContext();
	        
	        String groupName = groupSpinner.getSelectedItem().toString();
	        
	        Location location = appData.getBestLocation();
	        
	        if(location != null){
	        	appData.currentLatitude = location.getLatitude();
	        	appData.currentLongitude = location.getLongitude();
	        }
	        
			String tempLat = String.valueOf(appData.currentLatitude);
	        String tempLong = String.valueOf(appData.currentLongitude);
	        
	        appData.currentLocation.set("userid", appData.thisUser.get("userid"));
			appData.currentLocation.set("name", locationName.getText().toString());
			appData.currentLocation.set("description", locationDescription.getText().toString());
			appData.currentLocation.set("latitude", tempLat);
			appData.currentLocation.set("longitude", tempLong);
			
			appData.currentLocation.sync();
			if(!groupName.equals(ALL_GROUP)){
		        ArrayList<LocationGroup> bla = appData.thisUser.getLocationGroups().getKids();
		        for (LocationGroup it : bla){
		        	String name;
		        		
					name = it.get("groupname");
					if(groupName.equals(name)){
						
						LocationRelation newRelation = LocationRelation.createNew(appData.db);
						newRelation.setLocation(appData.currentLocation);
						newRelation.setGroup(it);
						it.getLocations().add(newRelation);
					}
		        }
			}
			
			
		} catch (Exception e){
			System.err.println(e);
		}
		try {
			appData.currentLocation.sync();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			if (!existing){
				System.out.println("truing to add");
			appData.thisUser.getLocations().add(appData.currentLocation);
			}
		} catch (Exception e) {
			locationName.setText(e.toString());
		}
		appData.currentLocation = null;
		Intent intent = new Intent(getActivity(), ListActivity.class);
		startActivity(intent);


		
	}
}

