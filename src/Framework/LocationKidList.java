package Framework;

import Framework.User;
import Framework.OurLocation;

import java.util.*;

import database.DB;
import database.DBResult;


public class LocationKidList extends KidList<OurLocation, User> {
	
	public LocationKidList(User Parent, DB db) throws Exception{
		kids = new ArrayList<OurLocation>();
		this.setParent(Parent);
		Db = db;
		this.loadKids();
	}
	
	private void loadKids() throws Exception{
		
		System.out.println(this.getParent().getPrimaryKey());
		String userid = this.getParent().get("userid");
		
		//String id = "22222"; // This is temporary
		
		String sql = "select locationid from locations where userid = " + userid;
		
		DBResult rs = Db.sqlCommand(sql);
		
		System.out.println("trying to load kids");
	
		Hashtable<String, String> row;
		
		while ((row = rs.next()) != null){
			OurLocation newLocation = OurLocation.loadExisting(row.get("locationid"), Db);
			this.add(newLocation);
		}
		
		this.printKids();
		
		// The KidList is loaded and ready to use
	}
	
	public void add(OurLocation kid) throws Exception{
		kid.set("userid", this.parent.get(this.parent.getPrimaryKey()));
		super.add(kid);
	}

}
