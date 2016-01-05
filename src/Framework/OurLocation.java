package Framework;
import java.util.ArrayList;
import java.util.Hashtable;

import database.DB;
import database.DBResult;
import exception.NotFoundException;



public class OurLocation extends DataModel {
	
	protected CrumbKidList crumbList;
	protected ImageKidList imageList;

	
	private OurLocation(DB db) throws Exception{
		super(db);
		this.baseTable = "locations";
		this.modelType = "LocationModel";
		this.primaryKeyValue = "locationid";
		this.toStringField = "name";
		this.crumbList = new CrumbKidList(this, db);
		this.imageList = new ImageKidList(this, db);
	}
	
	private OurLocation(String id, DB db) throws Exception{
		super(db);
		this.baseTable = "locations";
		this.modelType = "LocationModel";
		this.primaryKeyValue = "locationid";
		this.toStringField = "name";
		this.set(this.getPrimaryKey(), id);
		this.crumbList = new CrumbKidList(this, db);
		this.imageList = new ImageKidList(this, db);			
	}
	
	public static OurLocation loadExisting(String id, DB db) throws Exception{
		OurLocation model = new OurLocation(id, db);
		
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
	
	public static OurLocation createNew(DB db) throws Exception{
		String id = db.getNextMasterVal();
		
		OurLocation model = new OurLocation(id, db);
		
		model.is_Dirty = true;
		
		return model;
	}
	
	public CrumbKidList getCrumbs(){
		return this.crumbList;
	}
	
	public ImageKidList getImages(){
		return this.imageList;
	}

	public String toString() {
		try {
			String lat = this.get("latitude");
			String lng = this.get("longitude");
			lat = lat.substring(0,Math.min(lat.length(), 6));
			lng = lng.substring(0,Math.min(lng.length(), 6));
			
			String ret ="Name: "        + this.get("name") + "\n" +
						"Description: " + this.get("description") + "\n" +
						"Cords: "       + lat + "," + lng;
			return ret;
		} catch (Exception e){
			return null;
		}
	}
}
