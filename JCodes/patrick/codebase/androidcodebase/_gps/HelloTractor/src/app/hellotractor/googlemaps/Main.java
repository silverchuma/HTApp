package app.hellotractor.googlemaps;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyPingCallback;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class Main extends FragmentActivity {
	
	//temporarily hold data
	SharedPreferences pref;
	
	GoogleMap mMap;
	Marker mMarker;
	LocationManager lm;
	Double lat, lng;
	//LatLng position;
	protected void onCreate(Bundle savedInstanceState) {
		
		try{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mMap = ((SupportMapFragment)getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		
		mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
		      @Override
		      public void onMapClick(final LatLng position) {
		         
		         try {
		        	    Thread.sleep(1000);                 //1000 milliseconds is one second.
		        	    Toast.makeText(getApplicationContext(),position.latitude+" : "+position.longitude,Toast.LENGTH_SHORT).show();
		        	} catch(InterruptedException ex) {
		        	    Thread.currentThread().interrupt();
		        	}
		         
		         
		         AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
		         builder.setMessage("Create a Farmer on this location.")
		            .setCancelable(false)
		            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int id) {
		                    
		                	SharedPreferences shared = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
		        			SharedPreferences.Editor editor = shared.edit();
		        			Double positionLat = position.latitude;
		        			Double positionLng = position.longitude;
		        			
		        			String positionLat2 = positionLat.toString();
		        			String positionLng2 = positionLng.toString();
		        			
		        			editor.putString("latVal", positionLat2); editor.putString("lngVal", positionLng2);
		        			editor.commit();// commit is important here.
		                	openIntent();
		                	
		                	Main.this.finish();
		                     
		                }
		            })
		            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int id) {
		                     dialog.cancel();
		                }
		            });
		         AlertDialog alert = builder.create();
		         alert.show();
		         
		         
		      }
		});

		//initializing kinvey
		final Client mKinveyClient = new Client.Builder("kid_HyEnevkil", "162ad67e75ad4e1b849e93b5e06f2240"
			    , this.getApplicationContext()).build();
		
		
		final String TAG = "KINVEY";
		//ping kinvey backend
		final Client mKinveyClientS = new Client.Builder("kid_HyEnevkil", "162ad67e75ad4e1b849e93b5e06f2240", this.getApplicationContext()).build();
		mKinveyClientS.ping(new KinveyPingCallback() {
		    public void onFailure(Throwable t) {
		        Log.e(TAG, "Kinvey Ping Failed", t);
		    }
		    public void onSuccess(Boolean b) {
		        Log.d(TAG, "Kinvey Ping Success");
		        Toast.makeText(getBaseContext(), "Kinvey Ping Success", Toast.LENGTH_SHORT).show();
		    }
		});
		

		
//		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.putString("latVal", lat22); editor.putString("lngVal", lng22);
//        editor.commit();   
//        Toast.makeText(this, "latitude main>>> " + lat22, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "longitude main>>> " + lng22, Toast.LENGTH_SHORT).show();
		
			
		
		//getActionBar().setDisplayShowTitleEnabled(false); //remove title from the action bar
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	void openIntent(){
		Intent intent1 = new Intent(this, CreateFarmer.class);
        startActivity(intent1);
			//return true;
	}
	LocationListener listener = new LocationListener() {
	    public void onLocationChanged(Location loc) {
	    	LatLng coordinate = new LatLng(loc.getLatitude(), loc.getLongitude());
	    	lat = loc.getLatitude();
	    	lng = loc.getLongitude();
	    	if(lat != null && lat != null){
	    	Double lat2 = 44.4; Double lng2 = 55.5;
			String lat22 = Double.toString(lat);  String lng22 = Double.toString(lng);
		
            SharedPreferences shared = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
			SharedPreferences.Editor editor = shared.edit();
			editor.putString("latVal", lat22); editor.putString("lngVal", lng22);
			editor.commit();// commit is important here.
			
			//Toast.makeText(this, "latitude main>>> " + lat22, Toast.LENGTH_SHORT).show();
   		 	//Toast.makeText(this, "longitude main>>> " + lng22, Toast.LENGTH_SHORT).show();
	    	}
	    	if(mMarker != null)
	    		mMarker.remove();
	    	
	    	mMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)));
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 16));
	    }

	    public void onStatusChanged(String provider, int status, Bundle extras) {}
	    public void onProviderEnabled(String provider) {}
	    public void onProviderDisabled(String provider) {}
	};
	
	public void onResume() {
		super.onResume();
		
		lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

		boolean isNetwork = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		boolean isGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if(isNetwork) {
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, listener);
			Location loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if(loc != null) {
				lat = loc.getLatitude();
			    lng = loc.getLongitude();
			}
		}
		
		if(isGPS) {
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, listener);
			Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if(loc != null) {
				lat = loc.getLatitude();
			    lng = loc.getLongitude();
			}
		}
	}
	
	public void onPause() {
		super.onPause();
		lm.removeUpdates(listener);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int itemId = item.getItemId();
		// added custom menu selection for HelloTractor
		if (item.getItemId() == R.id.create) {
			//Toast.makeText(getBaseContext(), "Tap to create a Farmer", Toast.LENGTH_SHORT).show();
			Intent intent1 = new Intent(this, CreateFarmer.class);
            startActivity(intent1);
			return true;
		}
		return super.onOptionsItemSelected(item);
		}
	
	
	
	
	
	
	
	
	
}
