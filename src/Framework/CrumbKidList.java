package Framework;

import java.util.ArrayList;
import java.util.Hashtable;

import database.DB;
import database.DBResult;


public class CrumbKidList extends KidList<Crumb, OurLocation> {
	
	public CrumbKidList(OurLocation Parent, DB db) throws Exception{
		kids = new ArrayList<Crumb>();
		this.setParent(Parent);
		Db = db;
		this.loadKids();
	}
	
	private void loadKids() throws Exception{

		String locationid = this.getParent().get("locationid");
		
		//String id = "22222"; // This is temporary
		
		String sql = "select crumbid from crumbs where locationid = " + locationid;
		
		DBResult rs = Db.sqlCommand(sql);
	
		Hashtable<String, String> row;
		
		while ((row = rs.next()) != null){
			Crumb newCrumb = Crumb.loadExisting(row.get("crumbid"), Db);
			this.add(newCrumb);
		}
		
		
		// The KidList is loaded and ready to use
	}
	
	public void add(Crumb kid) throws Exception{
		kid.set("locationid", this.parent.get(this.parent.getPrimaryKey()));
		super.add(kid);
	}
}
