package com.mortonsworld.suggestly.model.foursquare;

import androidx.room.ColumnInfo;

public class Location {
    public String cc;
    public String city;
    public String state;
    public String country;
    public String address;
    public String crossStreet;
    public String postalCode;
    public Double lat;
    public Double lng;

    @ColumnInfo(defaultValue = "0")
    public Double distance;

    public String getFormattedAddress(){
        String formattedAddress = "";
        if(address != null){
            formattedAddress += address + "\n";
        }
        if(city != null){
            formattedAddress += city;
        }

        if(state != null){
            formattedAddress += ", " + state;
        }

        if(postalCode != null){
            formattedAddress += " " + postalCode;
        }
        return formattedAddress;
    }
}
