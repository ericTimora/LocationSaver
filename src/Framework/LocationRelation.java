package Framework;
import java.util.ArrayList;
import java.util.Hashtable;

import database.DB;
import database.DBResult;
import exception.NotFoundException;


public class LocationRelation extends DataModel {
	private LocationRelation(DB db) throws Exception{
		super(db);
		this.baseTable = "location_relations";
		this.modelType = "LocationRelationsModel";
		this.primaryKeyValue = "relationid";
	}
	
	private LocationRelation(String id, DB db) throws Exception{
		super(db);
		this.set(this.getPrimaryKey(), id);
		this.baseTable = "location_relations";
		this.modelType = "LocationRelationsModel";
		this.primaryKeyValue = "relationid";
		this.toStringField = "name";
		
	}
	
	public static LocationRelation loadExisting(String id, DB db) throws Exception{
		LocationRelation model = new LocationRelation(db);
		
		String sql = "select * from " + model.getBaseTable() + " where " + model.getPrimaryKey() + " = " + id;
		DBResult rs = db.sqlCommand(sql);
		
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
	
	public static LocationRelation createNew(DB db) throws Exception{
		LocationRelation model = new LocationRelation(db);
		
		String id = db.getNextMasterVal();
		model.set(model.getPrimaryKey(), id);
		model.is_Dirty = true;
		
		return model;
	}
	
	public OurLocation getLocation() throws Exception {
		OurLocation thisLocation = OurLocation.loadExisting(this.get("locationid"), Db);
		
		return thisLocation;
	}
	
	public LocationGroup getGroup() throws Exception {
		LocationGroup thisLocationGroup = LocationGroup.loadExisting(this.get(this.getPrimaryKey()), Db);
		
		return thisLocationGroup;
	}
	
	public void setLocation(OurLocation thisLocation) throws NotFoundException{
		this.set("locationid", thisLocation.get(thisLocation.getPrimaryKey()));
	}
	
	public void setGroup(LocationGroup thisLocationGroup) throws NotFoundException{
		this.set("groupid", thisLocationGroup.get(thisLocationGroup.getPrimaryKey()));
	}
	
	public String toString(){
		try {
			return this.getLocation().get("name");
		} catch (Exception e) {
			return e.toString();
		}
	}
}
