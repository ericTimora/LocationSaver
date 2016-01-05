package Framework;
import java.util.ArrayList;

import database.DB;

import exception.NotFoundException;


abstract class KidList <T extends DataModel, P extends DataModel> {
	
	public ArrayList<T> kids;
	
	protected P parent;
	
	protected DB Db;
	
	protected boolean is_Dirty = false;
	
	boolean is_deleted = false;
	
	//constructor
	public KidList(){
		
	}
	
	public KidList(P theParent){
		parent = theParent;
	}
	
	public P getParent(){
		return parent;
	}
		
	public T getKidById (String id) throws NotFoundException{
		System.out.println(id);
		System.out.println(kids.size());
		for (T item : kids){
			System.out.println(item.getClass());
			if (item.get(item.getPrimaryKey()).equals(id)){
				//item.printFields();
				return item;
			}			
		}
		throw new NotFoundException("No element with this id was found...");				
	}
	
	public void add(T kid) throws Exception{
		kid.is_Dirty = true;
		this.is_Dirty = true;
		kids.add(kid);
		kid.sync();
	}
	
	public ArrayList<T> getKids(){
		return kids;
	}
	
	public void dropKidById(String id) throws NotFoundException{
		for (T item : kids){
			if (item.get(item.getPrimaryKey()) == id){
				kids.remove(item);
				item.is_Deleted = true;
				return;
			}			
		}
		throw new NotFoundException("No element with this id was found...");
	}
	
	public void printKids(){
		for (T item : kids){
			item.printFields();
		}			
		
	}
	
	protected void setParent(P theParent){
		this.parent = theParent;
	}
	
}



