package app.hellotractor.googlemaps;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import com.kinvey.android.Client;
import com.kinvey.java.model.KinveyMetaData;

public class CreatingFarmer  extends GenericJson {
	
	
	 	@Key("_id")
	    private String id; 
	    @Key
	    private String name;
	    @Key
	    private Double lat;
	    @Key
	    private Double lng;
	    @Key
	    private String farmsize;
	    @Key
	    private String phonenumber;
	    @Key("_kmd")
	    private KinveyMetaData meta; // Kinvey metadata, OPTIONAL
	    @Key("_acl")
	    private KinveyMetaData.AccessControlList acl; //Kinvey access control, OPTIONAL
	    public CreatingFarmer(){}  //GenericJson classes must have a public empty constructor
		
	    
	    
	    public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}



		public Double getLat() {
			return lat;
		}



		public void setLat(Double lat) {
			this.lat = lat;
		}



		public Double getLng() {
			return lng;
		}



		public void setLng(Double lng) {
			this.lng = lng;
		}



		public String getFarmsize() {
			return farmsize;
		}



		public void setFarmsize(String farmsize) {
			this.farmsize = farmsize;
		}



		public String getPhonenumber() {
			return phonenumber;
		}



		public void setPhonenumber(String phonenumber) {
			this.phonenumber = phonenumber;
		}
	
	

}
