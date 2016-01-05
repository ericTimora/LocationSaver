package database;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;


public class DBTables {
	public Hashtable<String,Hashtable<String,Hashtable<String,String>>> tableStructure;
	public ArrayList<String> tableSqlCommands;
	DB db;
	public DBTables(DB db){
		this.db = db;
		this.tableStructure = buildDBTableInfo();
		
		Enumeration<String> keys = tableStructure.keys();
		tableSqlCommands = new ArrayList<String>();
		
		while(keys.hasMoreElements()){
			tableSqlCommands.add(buildTable(keys.nextElement()));	
		}
	}
	
	/**
	 * Build Table
	 * <p>
	 * This will build the database table based off of the information set in buildDBTableInfo()
	 * 
	 * @param table - The name of the table
	 * @throws DBException
	 */
	public String buildTable(String table){
		Hashtable<String, Hashtable<String, String>> tableConfig = tableStructure.get(table);
		if(tableConfig == null){
			return null;	
		}
		
		Hashtable<String, String> tableValues = tableConfig.get("values");
		Hashtable<String, String> tableOptions = tableConfig.get("options");
		
		Iterator<Entry<String, String>> tableValuesIT = tableValues.entrySet().iterator();
		
		String sql = "CREATE TABLE " + table + " (";
		
		while(tableValuesIT.hasNext()){
			Entry<String, String> row = tableValuesIT.next();
			
			String key = row.getKey();
			String value = row.getValue();
			String options = tableOptions.get(key);
			
			if(options == null){
				options = "";
			}
			
			sql += key + " " + value + " " + options;
			
			if(tableValuesIT.hasNext()){
				sql += ", ";
			}
		}
		sql += ")";	
		
		return sql;
		
	}
	
	
	/**
	 * configureTable
	 * @param values - The column name and the type
	 * @param options - Any additional sql information
	 * @return A hash table with "values" mapped to the values and "options" mapped out to the options
	 */
	private Hashtable<String,Hashtable<String,String>> configureTable(Hashtable<String, String> values, Hashtable<String, String> options){
		Hashtable<String,Hashtable<String,String>> tableInfo = new Hashtable<String,Hashtable<String,String>>();
		tableInfo.put("values", values);
		tableInfo.put("options", options);
		
		return tableInfo;
	}
	
	/**
	 * Get DB Table Info
	 * <p>
	 * This is where the sql tables are defined.
	 * 
	 * @return A hash table of all of the tables info
	 */
	private Hashtable<String,Hashtable<String,Hashtable<String,String>>> buildDBTableInfo(){
		
		Hashtable<String, String> users_Values = new Hashtable<String, String>();
		Hashtable<String, String> users_Options = new Hashtable<String, String>();
		users_Values.put("userid",   "INTEGER");
		users_Values.put("username", "TEXT");
		users_Values.put("password", "TEXT");
		
		users_Options.put("userid",   "PRIMARY KEY");
		users_Options.put("username", "NOT NULL");
		users_Options.put("password", "NOT NULL");
		
		
		Hashtable<String, String> location_groups_Values = new Hashtable<String, String>();
		Hashtable<String, String> location_groups_Options = new Hashtable<String, String>();
		location_groups_Values.put("groupid",   "INTEGER");
		location_groups_Values.put("groupname", "TEXT");
		location_groups_Values.put("userid", "INTEGER");
		
		location_groups_Options.put("groupid", "PRIMARY KEY");
		
		
		Hashtable<String, String> location_relations_Values = new Hashtable<String, String>();
		Hashtable<String, String> location_relations_Options = new Hashtable<String, String>();
		location_relations_Values.put("relationid", "INTEGER");
		location_relations_Values.put("groupid",    "INTEGER");
		location_relations_Values.put("locationid", "INTEGER");
		
		location_relations_Options.put("relationid", "PRIMARY KEY");
		location_relations_Options.put("groupid",    "NOT NULL");
		location_relations_Options.put("locationid", "NOT NULL");
		
		
		Hashtable<String, String> locations_Values = new Hashtable<String, String>();
		Hashtable<String, String> locations_Options = new Hashtable<String, String>();
		locations_Values.put("locationid",  "INTEGER");
		locations_Values.put("name",        "TEXT");
		locations_Values.put("coordinate",  "TEXT");
		locations_Values.put("description", "TEXT");
		locations_Values.put("notes",       "TEXT");
		locations_Values.put("userid",       "TEXT");
		locations_Values.put("latitude",       "TEXT");
		locations_Values.put("longitude",       "TEXT");
		
		locations_Options.put("locationid", "PRIMARY KEY");
		locations_Options.put("userid", "NOT NULL");
		locations_Options.put("latitude", "NOT NULL");
		locations_Options.put("longitude", "NOT NULL");
		
		
		Hashtable<String, String> crumbs_Values = new Hashtable<String, String>();
		Hashtable<String, String> crumbs_Options = new Hashtable<String, String>();
		crumbs_Values.put("crumbid",     "INTEGER");
		crumbs_Values.put("locationid",  "TEXT");
		crumbs_Values.put("coordinate",  "TEXT");
		crumbs_Values.put("description", "TEXT");
		
		crumbs_Options.put("crumbid", "PRIMARY KEY");
		
		
		Hashtable<String, String> images_Values = new Hashtable<String, String>();
		Hashtable<String, String> images_Options = new Hashtable<String, String>();
		images_Values.put("imageid",    "INTEGER");
		images_Values.put("locationid", "INTEGER");
		images_Values.put("filename",   "TEXT");
		images_Values.put("caption",    "TEXT");
		
		images_Options.put("imageid", "PRIMARY KEY");
		
		Hashtable<String, String> masterSequence_Values = new Hashtable<String, String>();
		Hashtable<String, String> masterSequence_Options = new Hashtable<String, String>();
		masterSequence_Values.put("id",    "INTEGER");
		masterSequence_Values.put("nothing", "TEXT");
		
		masterSequence_Options.put("id", "PRIMARY KEY AUTOINCREMENT");
		
		
		Hashtable<String,Hashtable<String,Hashtable<String,String>>> tables = new Hashtable<String,Hashtable<String,Hashtable<String,String>>>();
		
		tables.put("users",              configureTable(users_Values,users_Options));
		tables.put("location_groups",    configureTable(location_groups_Values,location_groups_Options));
		tables.put("location_relations", configureTable(location_relations_Values,location_relations_Options));
		tables.put("locations",          configureTable(locations_Values,locations_Options));
		tables.put("crumbs",             configureTable(crumbs_Values,crumbs_Options));
		tables.put("images",             configureTable(images_Values,images_Options));
		tables.put("master_sequence",    configureTable(masterSequence_Values,masterSequence_Options));
		
		return tables;
	}
}



