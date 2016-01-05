package Framework;

import java.util.ArrayList;
import java.util.Hashtable;

import database.DB;
import database.DBResult;


public class Image extends DataModel{
	
	private Image(DB db) throws Exception{
		super(db);
		this.baseTable = "images";
		this.modelType = "ImageModel";
		this.primaryKeyValue = "imageid";
	}
	
	private Image(String id, DB db) throws Exception{
		super(db);
		this.baseTable = "images";
		this.modelType = "ImageModel";
		this.primaryKeyValue = "imageid";			
	}
	
	public static Image loadExisting(String id, DB db) throws Exception{
		Image model = new Image(db);
		
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
	
	public static Image createNew(DB db) throws Exception{
		Image model = new Image(db);
		
		String id = db.getNextMasterVal();
		model.set(model.getPrimaryKey(), id);
		model.is_Dirty = true;
		
		return model;
	}
}
