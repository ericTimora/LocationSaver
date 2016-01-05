package database;
/**DBResult.java
 * @author Blayze
 * @date 10/31/2014
 * 
 */

import java.util.ArrayList;
import java.util.Hashtable;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBResult {
	private DB db;
	private String baseSQL;
	public ArrayList<String> tableKeys;
	public int location;
	private String[] excapeValues;
	private boolean error;
	
	public DBResult(DB db, String sqlCommand, String[] excapeValues){
		this.db = db;
		this.location = 0;
		this.baseSQL = sqlCommand + " LIMIT 1 ";
		String table = parseForTable(sqlCommand);
		this.error = false;
		if (table != null){
			if (table != ""){
				if(!db.getDBTableInfo().containsKey(table)){
					System.err.println("The table ["+ table +"] does not exist");
					this.error = true;
				}
			}
			else{
				System.err.println("The table was not found while parsing");
				this.error = true;	
			}
		}
		else{
			System.err.println("There was a problem parsing for the table");
			this.error = true;
		}
		
		if(!error){
			this.tableKeys = db.getTableKeys(table);
			this.excapeValues = excapeValues;
		}
	}
	
	/**
	 * next
	 * @return A hashtable with the fields maped to the values
	 *         null if there was no value found or there was an error
	 * @throws DBException 
	 */
	public Hashtable<String, String> next(){
		if(this.error){
			System.err.println("The sql command is broken. Returning null");
			return null;
		}
		
		Hashtable<String,String> results  = new Hashtable<String,String>();
		SQLiteDatabase database = db.getWritableDatabase();
		Cursor cursor = database.rawQuery(baseSQL + getLocation(),this.excapeValues);
		if(cursor.moveToFirst()){
			//There is something here
			String[] columnNames = cursor.getColumnNames();
			for(int i = 0; i < columnNames.length; i++){
				String res = cursor.getString(i);
				if(res != null){
					results.put(columnNames[i], res);
				}
			}
			cursor.close();
			database.close();
			this.location += 1;
			return results;
		}
		else{
			cursor.close();
			database.close();
			return null;
		}
	}
	
	private String getLocation(){
		return " OFFSET " + this.location;
	}
	
	private String parseForTable(String sqlCommand){
		sqlCommand = sqlCommand.toLowerCase();
		int index = sqlCommand.indexOf(" from ");
		
		if(index == -1){
			//" from " was not found
			return "";
		}
		
		index = index + " from ".length();
		
		sqlCommand = sqlCommand.substring(index, sqlCommand.length());
		char[] split_command = sqlCommand.toCharArray();
		
		int tableNameStart = -1;
		int tableNameEnd = -1;
		for(int i = 0; i < split_command.length; i++){
			if(tableNameStart == -1){
				//If we have not found the start of the table name
				if(split_command[i] != ' '){
					//Found the first non space before the table name
					tableNameStart = i;
				}
				
			}
			else{
				//If we have found the start and are looking for the end
				tableNameEnd = i+1; //Case where there is no space after the table name
				if(split_command[i] == ' '){
					//Found the first space after table name
					tableNameEnd = i; //Case where there is a space after the table name
					break;
				}
			}
		}
		if(tableNameEnd < tableNameStart){
			return "";
		}
		String tableName = sqlCommand.substring(tableNameStart, tableNameEnd);
		
		return tableName;
	}
}
