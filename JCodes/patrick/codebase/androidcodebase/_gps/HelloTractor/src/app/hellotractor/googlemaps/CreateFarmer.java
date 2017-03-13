package app.hellotractor.googlemaps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.android.gms.maps.SupportMapFragment;
import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.android.callback.KinveyPingCallback;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.Query;
import com.kinvey.java.User;
import com.kinvey.java.core.KinveyClientCallback;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CreateFarmer extends Activity implements OnClickListener {
	
		
	

	Button next, previous;
	
	String name,farmsize,phonenumber, latVal, lngVal;
	Double latitude, longitude;
	//data variable declarations
	EditText nameL;
	EditText latitudeL;
	EditText longitudeL;
	EditText farmsizeL;
	EditText phonenumberL;
	
	
	
	
    
    //initialize a global url
    private static final String REGISTER_URL = "http://10.0.2.2:88/SocNetApi/web/app_dev.php/influ/";
    
    //JSON element ids from repsonse of symfony script:
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
	
	final ArrayList<String> state_options=new ArrayList<String>();
    final ArrayList<String> city_options=new ArrayList<String>();
	
	private String array_spinner[];
	Client mKinveyClient;
	
	public static final String mypreference = "mypref";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.createfarmer);
		 
//		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//		//sharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
//        String latitude=sharedPreferences.getString("latVal", "");
//        String longitude =sharedPreferences.getString("lngVal", "");
//        Toast.makeText(this, "latitude>>> " + latitude, Toast.LENGTH_SHORT).show();//returns nothing
//        Toast.makeText(this, "longitude>>> " + longitude, Toast.LENGTH_SHORT).show();//returns nothing
		
		 
		//initializing kinvey
		  mKinveyClient = new Client.Builder("kid_HyEnevkil", "162ad67e75ad4e1b849e93b5e06f2240"
			    , this.getApplicationContext()).build();
		  
		  mKinveyClient.user().login(new KinveyUserCallback() {
			    @Override
			    public void onFailure(Throwable error) {
			        Log.e("KINVEY LOGIN", "Login Failure", error);
			//Toast.makeText(getBaseContext(), "Login Failure " , Toast.LENGTH_SHORT).show();
					   
			    }
			    @Override
			    public void onSuccess(User result) {
			        Log.i("KINVEY LOGIN","Logged in a new implicit user with id: " + result.getId());
			//        Toast.makeText(getBaseContext(), "Logged in a new implicit user with id: " + result.getId(), Toast.LENGTH_SHORT).show();
			    } 
			});
		  
		  final String TAG = "KINVEY";
			//ping kinvey backend
			final Client mKinveyClientS = new Client.Builder("kid_HyEnevkil", "162ad67e75ad4e1b849e93b5e06f2240", this.getApplicationContext()).build();
			mKinveyClientS.ping(new KinveyPingCallback() {
			    public void onFailure(Throwable t) {
			        Log.e(TAG, "Kinvey Ping Failed", t);
			    }
			    public void onSuccess(Boolean b) {
			        Log.d(TAG, "Kinvey Ping Success");
			    //    Toast.makeText(getBaseContext(), "Kinvey Ping Success2", Toast.LENGTH_SHORT).show();
			    }
			});
		
		
        next = (Button) findViewById(R.id.next);
        
        next.setOnClickListener(this);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setHomeAsUpIndicator(R.drawable.back);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(false);
        //getActionBar().setTitle("Back");
        
         SharedPreferences shareds = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
	     latVal = (shareds.getString("latVal", ""));
	     lngVal =(shareds.getString("lngVal", ""));
		 
		 //Toast.makeText(this, "latitude>>> " + latVal, Toast.LENGTH_SHORT).show();
		 //Toast.makeText(this, "longitude>>> " + lngVal, Toast.LENGTH_SHORT).show();
		 
		 try{
			 latitudeL = (EditText) findViewById(R.id.latitudes); //latitude logic point
			 longitudeL = (EditText) findViewById(R.id.longitude); //longitude logic point
			 
			 latitudeL.setText(latVal);
			 longitudeL.setText(lngVal);
			 
			 }catch(Exception ex){
				 ex.printStackTrace();
			 }
		 retrieveEntity();
	}
	
	void saveEntity(){
		
		CreatingFarmer createFarmer = new CreatingFarmer();
		createFarmer.setName(name);
		createFarmer.setLat(latitude);
		createFarmer.setLng(longitude);
		createFarmer.setFarmsize(farmsize);
		createFarmer.setPhonenumber(phonenumber);
		
		AsyncAppData<CreatingFarmer> myevents = mKinveyClient.appData("farmersCollections", CreatingFarmer.class);
		myevents.save(createFarmer, new KinveyClientCallback<CreatingFarmer>() {
		    @Override
		    public void onFailure(Throwable e) {
		        Log.e("TAG", "failed to save event data", e); 
		     //   Toast.makeText(getBaseContext(), "failed to save event data " + e, Toast.LENGTH_SHORT).show();
		    }
		    @Override
		    public void onSuccess(CreatingFarmer r) {
		        Log.d("TAG", "saved data for entity "+ r.getName()); 
		        Toast.makeText(getBaseContext(), "Farmer data saved successfully ", Toast.LENGTH_SHORT).show();
		    }
		});
		
		
	}
	
	

	
	void retrieveEntity(){
		

		//The CreatingFarmer class is defined above
//		CreatingFarmer events = new CreatingFarmer();
//		Query myQuery = mKinveyClient.query();
//		myQuery.equals("name","pat");
//		AsyncAppData<CreatingFarmer> myEvents = mKinveyClient.appData("farmersCollections", CreatingFarmer.class);
//		myEvents.get(myQuery, new KinveyListCallback<CreatingFarmer>() {
//		    @Override
//		    public void onSuccess(CreatingFarmer[] results) { 
//		        Log.v("TAG", "received "+ results.length + " events");
//		        Toast.makeText(getBaseContext(), "Retrieved event objects "+ results.length, Toast.LENGTH_SHORT).show();
//		    }
//		    @Override
//		    public void onFailure(Throwable error) { 
//		        Log.e("TAG", "failed to fetchByFilterCriteria", error);
//		        Toast.makeText(getBaseContext(), "Failed retrieving event objects ", Toast.LENGTH_SHORT).show();
//		    }
//		});
//		int ID = 5;
		//The EventEntity class is defined above
//		CreatingFarmer event = new CreatingFarmer();
//		AsyncAppData<CreatingFarmer> myEvents2 = mKinveyClient.appData("farmersCollections", CreatingFarmer.class);
//		myEvents2.getEntity("pat", new KinveyClientCallback<CreatingFarmer>() {
//		    @Override
//		    public void onSuccess(CreatingFarmer result) { 
//		        Log.v("TAG", "received "+ result.getName() );
//		        Toast.makeText(getBaseContext(), "Retrieved event objects "+ result.getName(), Toast.LENGTH_SHORT).show();
//		    }
//		    @Override
//		    public void onFailure(Throwable error) { 
//		        Log.e("TAG", "failed to fetchByFilterCriteria", error);
//		        Toast.makeText(getBaseContext(), "Failed retrieving event objects ", Toast.LENGTH_SHORT).show();
//		    }
//		});
		
		//EditText searchBar = (EditText) findViewById(R.id.search_bar);
		Query query = new Query();
		query.equals("name", "pat");
		AsyncAppData<CreatingFarmer> searchedEvents = mKinveyClient.appData("farmersCollections", CreatingFarmer.class);
		searchedEvents.get(query, new KinveyListCallback<CreatingFarmer>() {
		    @Override
		    public void onSuccess(CreatingFarmer[] event) {
		        Log.v("TAG", "received "+ event.length);
		        
		        Toast.makeText(getBaseContext(), "Retrieved event objects "+ event.length, Toast.LENGTH_SHORT).show();
		         
//		        for(int i = 0; i < event.length; i++){
//		            CreatingFarmer value = event[i];
//		            Toast.makeText(getBaseContext(), "Retrieved event objects "+ value.getName().toString(), Toast.LENGTH_SHORT).show();
//		         
//		        }
		        
		    }
		    @Override
		    public void onFailure(Throwable error) {
		        Log.e("TAG", "failed to query ", error);
		        Toast.makeText(getBaseContext(), "Failed retrieving event objects ", Toast.LENGTH_SHORT).show();
		    }      
		});
		
		
		
		
		
	}
	
	
	
    
    void validateFarmer(){
    	
    	InputFilter[] filterArray = new InputFilter[1];
		filterArray[0] = new InputFilter.LengthFilter(11);
		final EditText phone = (EditText)findViewById(R.id.phonenumber);
		phone.setFilters(filterArray);
    	
    	 nameL  = (EditText) findViewById(R.id.name); //name logic point
    	name = nameL.getText().toString();
    	latitudeL = (EditText) findViewById(R.id.latitudes); //latitude logic point
    	 latVal = latitudeL.getText().toString();
    	latitude = Double.parseDouble(latVal);
    	longitudeL = (EditText) findViewById(R.id.longitude); //longitude logic point
    	 lngVal = longitudeL.getText().toString();
    	longitude = Double.parseDouble(lngVal);
    	farmsizeL = (EditText) findViewById(R.id.farmsize); //farmsize logic point
    	farmsize = farmsizeL.getText().toString();
    	phonenumberL = (EditText) findViewById(R.id.phonenumber); //retype password logic point
    	phonenumber = phonenumberL.getText().toString();
    	
    	if (name.matches("")) {
    	   Toast.makeText(this, "Please enter name to proceed", Toast.LENGTH_SHORT).show();
    		
    	    return;
    	}else if (latVal.matches("")) {
    	    Toast.makeText(this, "Latitude is required to proceed", Toast.LENGTH_SHORT).show();
    	    return;
    	}else if (lngVal.matches("")) {
    	    Toast.makeText(this, "Longitude is required to proceed", Toast.LENGTH_SHORT).show();
    	    return;
    	}
    	else if (farmsize.matches("")) {
    	    Toast.makeText(this, "Please enter farmsize to proceed", Toast.LENGTH_SHORT).show();
    	    return;
    	}
    	 else if (phonenumber.matches("")) {
    	    Toast.makeText(this, "Please enter phonenumber to proceed", Toast.LENGTH_SHORT).show();
    	    return;
    	}
    	 else if(phone.getText().length() != 11)
         {
    		 phone.setError("phone number must be eleven digits");
    		 //Toast.makeText(this, "Please enter phonenumber to proceed", Toast.LENGTH_SHORT).show();
         }
    	else{
    	
    		saveEntity();
    		Intent intent1 = new Intent(this, Main.class);
    		startActivity(intent1);
    	}
    	
    	
    }
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		
//		
//switch(item.getItemId()){
//		
//		case R.id.action_settings:
//			return true;
//	case android.R.id.home:
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//        return true;
//		
//    default:
//        return super.onOptionsItemSelected(item);
//	}
//	}
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // app icon in action bar clicked; goto parent activity.
	            this.finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	    overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
	}

	public void onClick(View view) {
		
		switch (view.getId()){
        case R.id.next:
        	//saveEntity();
        	validateFarmer();
//        	Intent intent1 = new Intent(this, Main.class);
//            startActivity(intent1);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            break;
        default:
            break;
    }
		
	}
	
	
	
	
}
