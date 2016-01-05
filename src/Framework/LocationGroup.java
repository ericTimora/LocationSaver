package Framework;

import java.util.ArrayList;
import java.util.Hashtable;

import database.DB;
import database.DBResult;



public class LocationGroup extends DataModel {
	
	protected LocationRelationsKidList locationList;
	
	private LocationGroup(DB db) throws Exception{
		super(db);
		this.baseTable = "location_groups";
		this.modelType = "LocationGroupModel";
		this.primaryKeyValue = "groupid";
		this.toStringField = "groupname";
		this.locationList = new LocationRelationsKidList(this, db);
	}
	
	private LocationGroup(String id, DB db) throws Exception{
		super(db);
		this.baseTable = "location_groups";
		this.modelType = "LocationGroupModel";
		this.primaryKeyValue = "groupid";
		this.toStringField = "groupname";
		this.set(this.getPrimaryKey(), id);
		this.locationList = new LocationRelationsKidList(this, db);			
	}
	
	public static LocationGroup loadExisting(String id, DB db) throws Exception{
		int a = 2;
		LocationGroup model = new LocationGroup(id, db);
		
		String sql = "select * from " + model.getBaseTable() + " where " + model.getPrimaryKey() + " = " + id;
		DBResult rs = db.sqlCommand(sql);
		
		System.out.println("#group: " + id);
		
		Hashtable<String, String> row;
		
		while ((row = rs.next()) != null){
			ArrayList<String> keys = db.getTableKeys(model.getBaseTable());
			for (String s : keys){
				if (row.get(s) != null){
					model.dataFields.put(s, row.get(s));
				}
			}
		}		
		model.set(model.getPrimaryKey(), id);
		
		return model;
	}
	
	public static LocationGroup createNew(DB db) throws Exception{
		String id = db.getNextMasterVal();
		
		LocationGroup model = new LocationGroup(id, db);

		model.is_Dirty = true;
		
		System.out.println("new group " + id);
		
		return model;
	}
	
	public LocationRelationsKidList getLocations(){
		return this.locationList;
	}

}
