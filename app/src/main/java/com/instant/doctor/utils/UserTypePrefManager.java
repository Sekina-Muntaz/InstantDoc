package com.instant.doctor.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserTypePrefManager {
    Context context;


    //usertype 0=patient
    //usertype1=doctor
//
    public UserTypePrefManager(Context context){
        this.context=context;

    }
//
//    public void setUserType(int userType){
//        editor.putInt(CHECK_USER_TYPE,userType).apply();
//    }

//    public int getUserType(){
//        return preferences.getInt(CHECK_USER_TYPE,0);
//    }


    public void addUserType(int type){
        //0-patient 1-doctor
        SharedPreferences sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("user_type",type);
        editor.apply();
    }

    public void addUserName(String name){
        //0-patient 1-doctor
        SharedPreferences sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_name",name);
        editor.apply();
    }

    public void saveMerchantID(String id){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("merchant_requestId",id);
        editor.apply();
    }

    public void deleteMerchantID(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("merchant_requestId");
        editor.apply();
    }

    public String getMerchantID(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        return sharedPreferences.getString("merchant_requestId",null);
    }

    public void saveSessionID(String id){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("session_id",id);
        editor.apply();
    }

    public void deleteSessionID(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("session_id");
        editor.apply();
    }

    public String getSessionID(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        return sharedPreferences.getString("session_id",null);
    }


    public String getUserName(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        return sharedPreferences.getString("user_name",null);

    }


    public int getUserType(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("user_type",3);

    }

    public void deleteUserTypePref(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.remove("user_type");
        editor.remove("user_name");
        editor.apply();

    }
}
