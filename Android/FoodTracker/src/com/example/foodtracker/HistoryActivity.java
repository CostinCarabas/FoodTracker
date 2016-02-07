package com.example.foodtracker;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class HistoryActivity extends ListActivity {
    TextView content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_activity);
		content = (TextView)findViewById(R.id.output);
		String historyString;
		if (savedInstanceState == null) {
		    Bundle extras = getIntent().getExtras();
		    if(extras == null) {
		        historyString= null;
		    } else {
		        historyString= extras.getString("history");
		    }
		} else {
		    historyString= (String) savedInstanceState.getSerializable("history");
		}
		System.out.println("!!!!!!!!!HISTORY:"+historyString.toString());
         //listView = (ListView) findViewById(R.id.list);
	    ArrayList<String> values = new ArrayList<String>();
		try {
			JSONArray jsonArray = new JSONArray(historyString);
			for (int i = 0; i < jsonArray.length(); i++) {
		        values.add(jsonArray.getJSONObject(i).get("menu").toString() + 
		        		" - " +
		        		jsonArray.getJSONObject(i).get("data").toString());
		    }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         // Define a new Adapter
         // First parameter - Context
         // Second parameter - Layout for the row
         // Third - the Array of data
 
         ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                 android.R.layout.simple_list_item_1, values);
 
 
         // Assign adapter to List
         setListAdapter(adapter); 
    }
 
     
   protected void onListItemClick(ListView l, View v, int position, long id) {
         
         super.onListItemClick(l, v, position, id);
 
            // ListView Clicked item index
            int itemPosition     = position;
            
            // ListView Clicked item value
            String  itemValue    = (String) l.getItemAtPosition(position);
               
            content.setText("Click : \n  Position :"+itemPosition+"  \n  ListItem : " +itemValue);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
