package com.lovrhatr.socialize;

import java.util.List;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;


public class MainActivity extends Activity {

	private LocationManager locationManager;
	private String provider;
	private TextView title;
	private TextView date;
	private TextView time;
	private TextView creator;
	private TextView going;
	private CardLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Parse.initialize(this, "ZNUGpdAW35nGYe5hvleBl3IndIphZPbZjVfn8Vcn", "JGTTBGebCrZXx2J2nc6TVh3is6bwUBq5hROkFCSI");
		setContentView(R.layout.main_lists);

		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean enabled = service
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		// check if enabled and if not send user to the GSP settings
		// Better solution would be to display a dialog and suggesting to 
		// go to the settings
		if (!enabled) {
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
		} 

		// Get the location manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// Define the criteria how to select the locatioin provider -> use
		// default
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);

		if (location != null) {
			System.out.println("Provider " + provider + " has been selected.");
			onLocationChanged(location);
		} else {

		}

	}

	public void onLocationChanged(Location location) {
		int lat = (int) (location.getLatitude());
		int lng = (int) (location.getLongitude());
		ParseGeoPoint point = new ParseGeoPoint(lat, lng);

		layout = (CardLayout) findViewById(R.id.theLayout);
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);    
		final View cardView = inflater.inflate(R.layout.main_list_card, null);
		title = (TextView)cardView.findViewById(R.id.list_title);
		creator = (TextView)cardView.findViewById(R.id.checkBox1);
		date = (TextView)cardView.findViewById(R.id.checkBox2);
		time = (TextView)cardView.findViewById(R.id.checkBox3);
		going = (TextView)cardView.findViewById(R.id.more_items);

		ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
		query.whereNear("location", point);
		query.setLimit(10);
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> arg0, ParseException arg1) {
				for (int i = 0; i < arg0.size(); i++){
					title.setText(arg0.get(i).getString("name"));
					creator.setText(" - " + arg0.get(i).getString("creator"));
					date.setText(" - " + arg0.get(i).getString("date"));
					time.setText(" - " + arg0.get(i).getString("time"));
					going.setText(arg0.get(i).getInt("looking_for_people") + " people attending");
					going.setPadding(0, 0, 0, 10);
					layout.addView(cardView);
				}
			}
		});
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
