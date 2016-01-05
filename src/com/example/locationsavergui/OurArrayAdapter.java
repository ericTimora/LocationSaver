package com.example.locationsavergui;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

public class OurArrayAdapter extends ArrayAdapter{

	public OurArrayAdapter(Context context, int resource) {
		super(context, resource);
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	public <T> OurArrayAdapter(Context context, int resource, ArrayList<T> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
	}
}
