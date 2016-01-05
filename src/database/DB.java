package database;


import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import database.DBResult;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DB extends SQLiteOpenHelper{
	public String DBName;
	DBTables tables = new DBTables(this);	
	DBConverter converter = new DBConverter(this);
	
	private static final String DATABASE_NAME = "database.db";
	private static final int DATABASE_VERSION = 20;

	
	public DB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		System.out.println("Started");
	}

	public void onCreate(SQLiteDatabase db) {
		ArrayList<String> tableSqlCommands = tables.tableSqlCommands;
		for(int i = 0 ; i < tableSqlCommands.size(); i++){
			db.execSQL(tableSqlCommands.get(i));
		}
		
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		Hashtable<String, Hashtable<String, Hashtable<String, String>>> tableStructure = tables.tableStructure;
		Enumeration<String> keys = tableStructure.keys();
		while(keys.hasMoreElements()){
			db.execSQL("DROP TABLE IF EXISTS " + keys.nextElement());
		}
		
		onCreate(db);
		
	}

	
	
	/**
	 * SQL Command
	 * <p>
	 * This function should only be used if the sql command will return a row.
	 * See sqlUpdate for functions that just update the database
	 * 
	 * @param sql - A sql command to execute
	 * @param escapeValues - The escape values, optional
	 * @return A DBResult object
	 * 
	 */
	public DBResult sqlCommand(String sql, String[] excapeValues){
		return new DBResult(this, sql, excapeValues);
	}
	public DBResult sqlCommand(String sql){
		DBResult dbrs = null;
		//Class<DBResult> a = DBResult.class;
		dbrs = new DBResult(this, sql, null);
		return dbrs;
	}
	
	/**
	 * SQL Update
	 * <p>
	 * This function should only be used if the sql command will not return a row.
	 * 
	 * @param sql - A sql command to execute
	 * @param escapeValues - The escape values, optional
	 * @throws DBException - If there was an error with the sql command or the database
	 */
	public void sqlUpdate(String sql, String[] escapeValues){
		if(escapeValues.length == 0){
			sqlUpdate(sql);
			return;
		}
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(sql, escapeValues);
		db.close();
	}
	public void sqlUpdate(String sql){
		SQLiteDatabase db = this.getWritableDatabase();
		boolean b = db.isOpen();
		db.execSQL(sql);
		db.close();
	}
	
	/**
	 * Get Next Master Val
	 * <p>
	 * Used to get the next unique id for create new objects
	 * 
	 * @return String with the new unique id
	 * @throws DBException - If there was an error with the sql command or the database
	 */
	
	public String getNextMasterVal(){
		String sql = "INSERT INTO master_sequence (nothing) VALUES ('value')";
		sqlUpdate(sql);
		
		sql = "SELECT * FROM master_sequence ORDER BY id DESC";
		DBResult value = sqlCommand(sql);
		
		Hashtable<String, String> table = value.next();
		String id = table.get("id");
		
		return id;
	}
	
	/**
	 * Select From Table
	 * <p>
	 * This is used to select an entry from the database. The key-value combination from
	 * identifierFieldValuePair is used to find all rows that match the key-value combination.
	 * 
	 * @param table - The name of the table
	 * @param identifierFieldValuePair - A hash table that has the column names mapped out to values. Used to find the rows.
	 * @return A DBResult object
	 */
	public DBResult selectFromTable(String table, Hashtable<String, String> identifierFieldValuePair){
		DBResult ret = converter.selectFromTable(table,identifierFieldValuePair);
		return ret;
	}
	
	/**
	 * Delete From Table
	 * <p>
	 * This is used to delete an entry from the database. The key-value combination from
	 * identifierFieldValuePair is used to find all rows that match the key-value combination.
	 * 
	 * Warning: This will permanently delete all rows that match identifierFieldValuePair
	 * 
	 * @param table - The name of the table
	 * @param identifierFieldValuePair - A hash table that has the column names mapped out to values. Used to remove the rows.
	 */
	public void deleteFromTable(String table, Hashtable<String, String> identifierFieldValuePair){
		converter.deleteFromTable(table, identifierFieldValuePair);
		
	}
	
	/**
	 * Update Into Table
	 * <p>
	 * This is used to update an entry from the database. The key-value combination from
	 * identifierFieldValuePair is used to find all rows that match the key-value combination.
	 * Then any value key-value combination from updateFieldValuePair will be put into all of
	 * the rows that were found.
	 * 
	 * Warning: This will permanently change all rows that match identifierFieldValuePair
	 * 
	 * @param table - The name of the table
	 * @param identifierFieldValuePair - A hash table that has the column names mapped out to values. Used to find the rows.
	 * @param updateFieldValuePair -  A hash table that has the column names mapped out to values. Used to update the values.
	 * 
	 */
	public void updateIntoTable(String table, Hashtable<String, String> updateFieldValuePair, Hashtable<String, String> identifierFieldValuePair){
		converter.updateIntoTable(table, updateFieldValuePair, identifierFieldValuePair);
	}
	
	/**
	 * Insert Into Table
	 * <p>
	 * This is used to insert an entry from the database with the key value pairs from fieldValuePair.
	 * 
	 * @param table - The name of the table
	 * @param fieldValuePair - A hash table that has the column names mapped out to values. Used to insert the values.
	 */
	public void insertIntoTable(String table, Hashtable<String, String> fieldValuePair){
		converter.insertIntoTable(table, fieldValuePair);
	}
	
	/**
	 * Get Table Keys
	 * <p>
	 * This will return back an array list of keys from a given table
	 * 
	 * @param table - The name of the table
	 * @return - An array list of keys
	 */
	public ArrayList<String> getTableKeys(String table){
		Enumeration<String> keys = this.tables.tableStructure.get(table).get("values").keys();
		ArrayList<String> tableKeys = new ArrayList<String>();
		
		while(keys.hasMoreElements()){
			tableKeys.add(keys.nextElement());
		}
		
		return tableKeys;
		
	}
	/**
	 * 
	 * @return
	 */
	public Hashtable<String,Hashtable<String,Hashtable<String,String>>> getDBTableInfo(){
		return tables.tableStructure;
	}
}
