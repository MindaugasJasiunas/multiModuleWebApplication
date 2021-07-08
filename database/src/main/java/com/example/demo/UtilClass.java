package com.example.demo;

import java.util.ArrayList;
import java.util.List;

public class UtilClass {
    //TODO: make countries with states load from file

    public static List<String> getCountries(){
        List<String> countries= new ArrayList<>();
        countries.add("Lithuania");
        countries.add("Latvia");
        countries.add("Estonia");
        countries.add("UK");
        return countries;
    }

    public static List<String> getStatesByCountry(String country){
        List<String> states=new ArrayList<>();
        if(country.equals("Lithuania")){
            states.add("Vilnius");
            states.add("Kaunas");
            states.add("Klaipeda");
        }else if(country.equals("Latvia")){
            states.add("Riga");
            states.add("Liepaja");
            states.add("Jurmala");
        }else if(country.equals("Estonia")){
            states.add("Tallinn");
            states.add("Tartu");
            states.add("Parnu");
        }else if(country.equals("UK")){
            states.add("London");
            states.add("Birmingham");
            states.add("Manchester");
        }
        return states;
    }
}
