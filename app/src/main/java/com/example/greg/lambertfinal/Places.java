package com.example.greg.lambertfinal;

import android.os.Parcel;
import android.os.Parcelable;

public class Places implements Parcelable { //Creates a places class. Implements Parceable so that we can bundle the data and move it between instances
 
    //Declare needed variables
    
    Double lat;
    
    Double lng;

    String name;

    String iconUrl;

    String address;

    String operating;

    public Places(){ //constructor

    }
    
    //methods

    public void setLat(String lat2){
        
        this.lat = Double.parseDouble(lat2);
        
    }


    public Double getLat(){
        
        return this.lat;

    }

    public void setLng(String lng2){
        
        this.lng = Double.parseDouble(lng2);
        
    }


    public Double getLng(){
        
        return this.lng;

    }

    public void setName(String name2){
        
        this.name = name2;
        
    }

    public String getName(){

        return this.name;
       
    }

    public void setIconUrl(String icon){

        this.iconUrl = icon;
        
    }

    public String getIconUrl(){

        return this.iconUrl;
    }

    public void setAddress(String address2){

        this.address = address2;

    }

    public String getAddress(){

        return this.address;
    }

    public void setOperating(String operation){

        this.operating = "unknown";

        if(operation.equals("")){
            
            this.operating = "Unknown";
        }

        if(operation.equals("true")){
            
            this.operating = "true";

        }

        else{

            this.operating = "false";
        }

    }

    public String getOperating(){

        return this.operating;
        
    }

    protected Places(Parcel in) {
        
        lat = in.readByte() == 0x00 ? null : in.readDouble();
        
        lng = in.readByte() == 0x00 ? null : in.readDouble();
        
        name = in.readString();
        
        iconUrl = in.readString();
        
        address = in.readString
            
        operating = in.readString();
        
    }

    @Override
    
    public int describeContents() {
        
        return 0;
        
    }

    @Override
    
    public void writeToParcel(Parcel dest, int flags) { //increment byte if information is not null, write information to Parcel object
        
        if (lat == null) {
            
            dest.writeByte((byte) (0x00));
            
        } else {
            
            dest.writeByte((byte) (0x01));
            
            dest.writeDouble(lat);
            
        }
        if (lng == null) {
            
            dest.writeByte((byte) (0x00));
            
        } else {
            
            dest.writeByte((byte) (0x01));
            
            dest.writeDouble(lng);
            
        }
        
        dest.writeString(name);
        
        dest.writeString(iconUrl);
        
        dest.writeString(address);
        
        dest.writeString(operating);
        
    }

    @SuppressWarnings("unused")
    
    public static final Parcelable.Creator<Places> CREATOR = new Parcelable.Creator<Places>() {
        
        @Override
        
        public Places createFromParcel(Parcel in) {
            
            return new Places(in);
            
        }

        @Override
        
        public Places[] newArray(int size) {
            
            return new Places[size];
            
        }
    };
}






