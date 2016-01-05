package Framework;

import java.util.*;

import database.DB;
import database.DBResult;

import exception.NotFoundException;


public class DataModel {
	
	protected String modelType = "DataModel";
	
	protected String baseTable;
	
	protected String primaryKeyValue;
	
	protected String toStringField;
	
	protected boolean is_Dirty = false;
	
	protected boolean is_Deleted = false;
	
	protected Hashtable<String, String> dataFields;
	
	protected DB Db;
	
	
	//Constructor
	public DataModel(DB theDb){
		dataFields = new Hashtable<String, String>();
		Db = theDb;
	}
	
	// load existing
	public void loadExisting(String id) throws Exception{
		
		
		String sql = "select * from " + this.getBaseTable() + " where " + this.getPrimaryKey() + " = " + id;
		DBResult rs = Db.sqlCommand(sql);
		
		Hashtable<String, String> row;
		
		while ((row = rs.next()) != null){
			ArrayList<String> keys = Db.getTableKeys(this.getBaseTable());
			for (String s : keys){
				
				this.dataFields.put(s, row.get(s));
			}
		}		
		this.set(this.getPrimaryKey(), id);		
	}
	
	// Generic function to return the value of the requested datafield
	public String get(String key) throws NotFoundException{
		if (dataFields.get(key) != null){
			 return dataFields.get(key);
		} else {
			throw new NotFoundException("No field with this key was found..." + key);
		}
	}
	
	// Generic function to set the value of the requested datafield
	public String set(String key, String value){		
		dataFields.put(key, value);
		this.is_Dirty = true;
		return ("Key: " + key + " set to: " + value);		
	}
	
	// Generic function to get the db table that this model builds from...
	public String getBaseTable(){
		return this.baseTable;
	}
	
	public String getPrimaryKey(){
		return this.primaryKeyValue;
	}
	
	protected void createNew() throws Exception{
		//get the next master sequence number and put it in the datafields correctly
		
		
		String id = Db.getNextMasterVal();
		this.set(this.getPrimaryKey(), id);
		this.is_Dirty = true;
	}
	
	public void printFields(){
		for (Map.Entry<String, String> key : dataFields.entrySet()){
			System.out.println(key.getKey() + " : " + key.getValue()); /// this will become update
		}
	}
	
	public void sync() throws Exception{
		System.out.println("syncing..." + this.getClass());	
		
		if (!this.is_Deleted) {			
			String sql = "SELECT * FROM " + this.getBaseTable() + " WHERE " + this.getPrimaryKey() + " = " + this.get(this.getPrimaryKey());
			
			DBResult rs = Db.sqlCommand(sql);

			Hashtable<String, String> row;
		
			if ((row = rs.next()) == null){
				Db.insertIntoTable(this.getBaseTable(), this.dataFields);
				
			} else {
				sql = "UPDATE INTO " + this.getBaseTable() + "";
				Hashtable<String,String> ids = new Hashtable<String,String>();
				String a = this.getPrimaryKey();
				String b = this.dataFields.get(this.getPrimaryKey());
				ids.put(this.getPrimaryKey(), this.dataFields.get(this.getPrimaryKey()));
				Db.updateIntoTable(this.getBaseTable(), this.dataFields, ids);
			}
		} else {
			String sql = "DELETE " + this.getPrimaryKey() + " FROM " + this.getBaseTable() + " WHERE " + this.getPrimaryKey() + " = " + this.get(this.getPrimaryKey());

			Db.sqlUpdate(sql);
			
		}		
		this.is_Dirty = false;		
	}
	
	public String toString(){
		try {
			return this.get(this.toStringField);
		} catch (NotFoundException e) {
			return e.toString();
		}
	}
}
