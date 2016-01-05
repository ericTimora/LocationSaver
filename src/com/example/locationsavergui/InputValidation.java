package com.example.locationsavergui;

public class InputValidation {

	public boolean nameValidation(String text) 
	{
		if(text.length() > 30) 
		{
			return false;
		}else return true;
	}
	
	public boolean descriptionValidation(String text) 
	{
		if(text.length() > 250) 
		{
			return false;
		}else return true;
	}
}
