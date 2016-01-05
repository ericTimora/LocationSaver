package Framework;

import java.util.ArrayList;
import java.util.Hashtable;

import database.DB;
import database.DBResult;



public class ImageKidList extends KidList<Image, OurLocation> {
	
	public ImageKidList(OurLocation Parent, DB db) throws Exception{
		kids = new ArrayList<Image>();
		this.setParent(Parent);
		Db = db;
		this.loadKids();
	}
	
	private void loadKids() throws Exception{

		String locationid = this.getParent().get("locationid");
		
		//String id = "22222"; // This is temporary
		
		String sql = "select imageid from images where locationid = " + locationid;
		
		DBResult rs = Db.sqlCommand(sql);
	
		Hashtable<String, String> row;
		
		while ((row = rs.next()) != null){
			Image newImage = Image.loadExisting(row.get("imageid"), Db);
			this.add(newImage);
		}
		
		
		// The KidList is loaded and ready to use
	}
	
	public void add(Image kid) throws Exception{
		kid.set("locationid", this.parent.get(this.parent.getPrimaryKey()));
		super.add(kid);
	}
}
