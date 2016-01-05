package Framework;

import java.util.ArrayList;
import java.util.Hashtable;

import database.DB;
import database.DBResult;


public class LocationRelationsKidList extends KidList<LocationRelation, LocationGroup> {
	
	public LocationRelationsKidList(LocationGroup Parent, DB db) throws Exception{
		kids = new ArrayList<LocationRelation>();
		this.setParent(Parent);
		Db = db;
		this.loadKids();
	}
	
	private void loadKids() throws Exception{

		String groupid = this.getParent().get("groupid");
		
		//String id = "22222"; // This is temporary
		
		String sql = "select relationid from location_relations where groupid = " + groupid;
		
		DBResult rs = Db.sqlCommand(sql);
	
		Hashtable<String, String> row;
		
		while ((row = rs.next()) != null){
			LocationRelation newLocation = LocationRelation.loadExisting(row.get("relationid"), Db);
			this.add(newLocation);
		}
		
		
		// The KidList is loaded and ready to use
	}
	
	public void add(LocationRelation kid) throws Exception{
		kid.set("groupid", this.parent.get(this.parent.getPrimaryKey()));
		super.add(kid);
	}
}
