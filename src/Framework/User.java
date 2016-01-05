package Framework;


import java.util.*;

import database.DB;
import database.DBResult;



public class User extends DataModel {
	
	protected LocationGroupKidList locationGroupKidList;
	protected LocationKidList locationKidList;
	
	public String username;
	
	private User(DB db) throws Exception{
		super(db);
		this.baseTable = "users";
		this.modelType = "UserModel";
		this.primaryKeyValue = "userid";
		this.locationGroupKidList = new LocationGroupKidList(this, db);
	}
	
	private User(String id, DB db) throws Exception{
		super(db);
		this.baseTable = "users";
		this.modelType = "UserModel";
		this.primaryKeyValue = "userid";
		this.set(this.getPrimaryKey(), id);
		this.locationGroupKidList = new LocationGroupKidList(this, db);
		this.locationKidList = new LocationKidList(this, db);
	}
	
	public LocationGroupKidList getLocationGroups(){
		return this.locationGroupKidList;
	}
	
	public LocationKidList getLocations(){
		return this.locationKidList;
	}
	
	public static User loadExisting(String id, DB db) throws Exception{
		User model = new User(id, db);
		
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
	
	public static User createNew( DB db) throws Exception{
		
		String id = db.getNextMasterVal();
		
		User model = new User(id, db);

		model.is_Dirty = true;
		

		
		return model;
	}
	
	@SuppressWarnings("null")
	public static User getUser(DB db) throws Exception{
		User model = null;
		try {
			
			
			String sql = "select userid from users";
			DBResult rs = db.sqlCommand(sql);
			System.out.println(sql);
			Hashtable<String, String> row;
			
			int cnt = 0;
						
			while ((row = rs.next()) != null){
				System.out.println("nothing");
				String a = row.get("userid");
				model = User.loadExisting(row.get("userid"), db);
				cnt++;
				ArrayList<String> keys = db.getTableKeys(model.getBaseTable());
				for (String s : keys){
					if (row.get(s) != null){
						model.dataFields.put(s, row.get(s));
					}
				}
			}
			
			if (cnt ==0) throw new Exception();
			
			return model;
		} catch (Exception e) {
			String id = db.getNextMasterVal();
			
			System.out.println("exception");
			
			model = new User(id, db);
			
			model.set("username", "Eric");
			model.set("password", "12345");

			model.is_Dirty = true;

			return model; 
		}
	}

}
