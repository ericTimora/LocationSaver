package Framework;

import java.util.ArrayList;
import java.util.Hashtable;

import database.DB;
import database.DBResult;

public class LocationGroupKidList extends KidList<LocationGroup, User> {
	public LocationGroupKidList(User Parent, DB db) throws Exception{
		kids = new ArrayList<LocationGroup>();
		this.setParent(Parent);
		Db = db;
		this.loadKids();
	}

	
	private void loadKids() throws Exception{
		

		String userid = this.getParent().get("userid");
		
		String sql = "select groupid from location_groups where userid = " + userid;
		
		try {
			DBResult rs = Db.sqlCommand(sql);
		
			Hashtable<String, String> row;
			
			while ((row = rs.next()) != null){
				String ret = row.get("groupid");
				
				LocationGroup newGroup = LocationGroup.loadExisting(ret, Db);
				this.kids.add(newGroup);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
		// The KidList is loaded and ready to use
	}
	
	

}
