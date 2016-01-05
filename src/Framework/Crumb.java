package Framework;
import java.util.ArrayList;
import java.util.Hashtable;

import database.DB;
import database.DBResult;



public class Crumb extends DataModel{
	
	String imageFile;
	
	private Crumb(DB db) throws Exception{
		super(db);
		this.baseTable = "crumbs";
		this.modelType = "CrumbModel";
		this.primaryKeyValue = "crumbid";
	}
	
	private Crumb(String id, DB db) throws Exception{
		super(db);
		this.baseTable = "crumbs";
		this.modelType = "CrumbModel";
		this.primaryKeyValue = "crumbid";			
	}
	
	public static Crumb loadExisting(String id, DB db) throws Exception{
		Crumb model = new Crumb(db);
		
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
	
	public static Crumb createNew(DB db) throws Exception{
		Crumb model = new Crumb(db);
		
		String id = db.getNextMasterVal();
		model.set(model.getPrimaryKey(), id);
		model.is_Dirty = true;
		
		return model;
	}
}
