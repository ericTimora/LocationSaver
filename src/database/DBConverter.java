package database;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;

//import database.DBException_old;
//import database.DBResult_old;

public class DBConverter {
	DB db;
	public DBConverter(DB db){
		this.db = db;
	}

	/**
	 * Select From Table
	 * <p>
	 * This builds and executes a sql select command based off of the information from identifierFieldValuePair
	 * 
	 * @param table - The name of the table
	 * @param identifierFieldValuePair - The field value pair to used to find a row
	 * @return A DBResult
	 */
	public DBResult selectFromTable(String table, Hashtable<String, String> identifierFieldValuePair){
		String[][] selectBuildValues = buildSQLEqualsListCommand(table, identifierFieldValuePair);
		String sqlSelectValues = selectBuildValues[0][0];
		String[] excapeValues = selectBuildValues[1];
		
		String sql = "SELECT * FROM " + table + " WHERE " + sqlSelectValues;
			
		
		return db.sqlCommand(sql, excapeValues);
	}
	
	/**
	 * Delete From Table
	 * <p>
	 * This builds and executes a sql delete command based off of the information from identifierFieldValuePair
	 * 
	 * @param table - The table name
	 * @param identifierFieldValuePair - The field value pair to find the row Ex: The where name="XX" and age="YY"
	 * @throws DBException_old 
	 */
	public void deleteFromTable(String table, Hashtable<String, String> identifierFieldValuePair){
		String[][] deleteValues = buildSQLEqualsListCommand(table, identifierFieldValuePair);
		String sqlDeleteValues = deleteValues[0][0];
		String[] excapeValues = deleteValues[1];
		
		String sql = "DELETE FROM " + table + " WHERE " + sqlDeleteValues;
			
		db.sqlUpdate(sql, excapeValues);
	}
	
	/**
	 * Update Into Table
	 * <p>
	 * This builds and executes a sql update command based off of the information from updateFieldValuePair and identifierFieldValuePair
	 * 
	 * @param table - The name of the table
	 * @param updateFieldValuePair - The field value pair of values to update
	 * @param identifierFieldValuePair - The field value pair to find the row Ex: The where name="XX" and age="YY"
	 * @throws DBException_old 
	 */
	public void updateIntoTable(String table, Hashtable<String, String> updateFieldValuePair, Hashtable<String, String> identifierFieldValuePair){
		String[][] updateBuildValeus = buildSQLUpdateEqualsListCommand(table, updateFieldValuePair);
		String[][] selectBuildValues = buildSQLEqualsListCommand(table, identifierFieldValuePair);
		
		String sqlUpdateValues = updateBuildValeus[0][0];
		String sqlSelectValues = selectBuildValues[0][0];
		
		//Combine the two escape arrays
		String[] sqlUpdateValuesEscapeValues = updateBuildValeus[1];
		String[] selectBuildValuesEscapeValues = selectBuildValues[1];
		
		int size = sqlUpdateValuesEscapeValues.length + selectBuildValuesEscapeValues.length;
		String[] escapeValues = new String[size];
		
		for(int i = 0; i < sqlUpdateValuesEscapeValues.length; i++){
			escapeValues[i] = sqlUpdateValuesEscapeValues[i];
			
		}
		for(int i = 0; i < selectBuildValuesEscapeValues.length; i++){
			escapeValues[i + sqlUpdateValuesEscapeValues.length] = selectBuildValuesEscapeValues[i];
		}
		//combined
		
		
		String sql = "UPDATE " + table + " SET " + sqlUpdateValues + " WHERE " + sqlSelectValues;

		
		db.sqlUpdate(sql,escapeValues);
	}
	
	/**
	 * Insert Into Table
	 * <p>
	 * This builds and executes a sql insert command based off of the information from fieldValuePair
	 * 
	 * @param table - The name of the table to insert values into
	 * @param fieldValuePair - A hash table of fields mapping to values
	 * @throws DBException_old 
	 */
	public void insertIntoTable(String table, Hashtable<String, String> fieldValuePair){
		String[][] sqlBuildValues = buildSQLInsertCommand(table, fieldValuePair);
		String fieldList = sqlBuildValues[0][0];
		String valueList = sqlBuildValues[1][0];
		String[] excapeValues = sqlBuildValues[2];
		
		String sql = "INSERT INTO " + table + " " + fieldList + " VALUES " + valueList;
		
		db.sqlUpdate(sql, excapeValues);
	}
	
	
	//=============================Private Helper Functions=========================\\
	
	/**
	 * Build SQL Equals List Command
	 * @param table - The name of the table
	 * @param fieldValuePair - The field value pair
	 * @return A 2D string array. 
	 * [0][0] is the sql command.
	 * [1] is the escape arguments.
	 */
	private String[][] buildSQLUpdateEqualsListCommand(String table, Hashtable<String, String> fieldValuePair){
		Hashtable<String,String> fieldTypePair = getFieldTypeTable(table);
		Iterator<Entry<String, String>> it = fieldValuePair.entrySet().iterator();
		
		ArrayList<String> escapeValues = new ArrayList<String>();
		
		String sql = "";
		while(it.hasNext()){
			Entry<String, String> entry = it.next();
			String field = entry.getKey().toLowerCase();
			String value = entry.getValue();
			String type = fieldTypePair.get(field);
			
			String[] values = convertValueToSQLFriendly(value, type);
			
			if(values != null){
				if(values[0] != values[1]){
					//Escape Value
					escapeValues.add(values[1]);
				}
				
				
				sql = sql + field + " = " + values[0] + " , ";
			}
			
		}
		
		if(sql.equals("")){
			//Nothing added
			return null;
		}
		
		//Chop off the last '= '
		sql = sql.substring(0, sql.length() - 2);
		
		//Convert to string array
		Object[] a = escapeValues.toArray();
		String[] ret = new String[a.length];
		for(int i = 0 ; i < a.length; i++){
			ret[i] = (String) a[i];
		}
		
		String[] excapeValuesArray = ret;
		return new String[][]{ new String[]{sql}, excapeValuesArray};
	}
	
	/**
	 * Build SQL Equals List Command
	 * @param table - The name of the table
	 * @param fieldValuePair - The field value pair
	 * @return A 2D string array. 
	 * [0][0] is the sql command.
	 * [1] is the escape arguments.
	 */
	String[][] buildSQLEqualsListCommand(String table, Hashtable<String, String> fieldValuePair){
		Hashtable<String,String> fieldTypePair = getFieldTypeTable(table);
		Iterator<Entry<String, String>> it = fieldValuePair.entrySet().iterator();
		
		ArrayList<String> escapeValues = new ArrayList<String>();
		
		String sql = "";
		while(it.hasNext()){
			Entry<String, String> entry = it.next();
			String field = entry.getKey().toLowerCase();
			String value = entry.getValue();
			String type = fieldTypePair.get(field);
			
			String[] values = convertValueToSQLFriendly(value, type);
			
			if(values != null){
				if(values[0] != values[1]){
					//Escape Value
					escapeValues.add(values[1]);
				}
				sql = sql + field + " = " + values[0] + " AND ";
			}
		}
		
		if(sql.equals("")){
			//Nothing added
			return null;
		}
		
		//Chop off the last and
		sql = sql.substring(0, sql.length() - 4);
		
		//Convert to string array
		Object[] a = escapeValues.toArray();
		String[] ret = new String[a.length];
		for(int i = 0 ; i < a.length; i++){
			ret[i] = (String) a[i];
		}
		
		String[] excapeValuesArray = ret;
		return new String[][]{ new String[]{sql}, excapeValuesArray};
	}
	
	/**
	 * Build SQL Insert Command
	 * @param table - The name of the table
	 * @param fieldValuePair - The field value pair
	 * @return A 2D string array. 
	 * [0][0] is the sql field list.
	 * [1][0] is the sql field value.
	 * [2] is the escape arguments.
	 */
	private String[][] buildSQLInsertCommand(String table, Hashtable<String, String> fieldValuePair){
		Hashtable<String,String> fieldTypePair = getFieldTypeTable(table);
		Iterator<Entry<String, String>> it = fieldValuePair.entrySet().iterator();
		
		ArrayList<String> escapeValues = new ArrayList<String>();
		
		String fieldList = "(";
		String valueList = "(";
		
		while(it.hasNext()){
			Entry<String, String> entry = it.next();
			String field = entry.getKey().toLowerCase();
			String value = entry.getValue();
			String type = fieldTypePair.get(field);
			String[] values = convertValueToSQLFriendly(value, type);
			if(values != null){
				if(values[0] != values[1]){
					//Escape Value
					escapeValues.add(values[1]);
				}
				
				fieldList += field + ",";
				valueList += values[0] + ",";
			}
		}
		
		fieldList = fieldList.substring(0, fieldList.length() - 1);
		valueList = valueList.substring(0, valueList.length() - 1);
		
		fieldList += ")";
		valueList += ")";
		
		if(fieldList.equals("()")){
			//No values added
			return null;
		}
		
		//Convert to string array
		Object[] a = escapeValues.toArray();
		String[] ret = new String[a.length];
		for(int i = 0 ; i < a.length; i++){
			ret[i] = (String) a[i];
		}
		
		String[] excapeValuesArray = ret;
		return new String[][]{ new String[]{fieldList}, new String[]{valueList}, excapeValuesArray};
	}
	
	/**
	 * Convert To SQL Friendly Value
	 * @param value - The value to conver/check
	 * @param type - The sql type of value
	 * @return A string array
	 * if [0] == [1] then its safe to insert directly into the sql statement
	 * if [0] != [1] then it is not 100% safe. Insert [0] which should be '?' and put [1] into the escape values
	 * if Null then there was an error and the value should be excluded.
	 */
	private String[] convertValueToSQLFriendly(String value, String type){
		if(value == null){
			value =  " ";
			System.err.println("Tried to insert a null value into the database, changed to space");
		}
		if (type == null) {
			System.err.println("The value ["+value+"] with type null tried to be inserted into database. Excluding" );
			return null;
        }
		
        if(type.toLowerCase().equals("text")){
			return new String[]{"?",value};
		}
     
        if(type.toLowerCase().equals("integer")){
        	char[] chars = value.toCharArray();
        	for(int i = 0; i < chars.length; i++){
        		if(!Character.isDigit(chars[0])){
        			return null;
        		}
        	}
        	
        	return new String[]{value,value};
        	
        }
        
        //Do not allow other types unless checked
        System.err.println("Unknown type: " + type);
        return null;
	}
	
	/**
	 * Get Field Type Table
	 * @param table
	 * @return The field type value hash table for the sql table
	 */
	private Hashtable<String, String> getFieldTypeTable(String table){
		return db.tables.tableStructure.get(table).get("values");
	}
	
	
}
