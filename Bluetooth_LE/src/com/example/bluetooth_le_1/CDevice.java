package com.example.bluetooth_le_1;

import java.io.Serializable;

public class CDevice implements Serializable{
	private static final long serialVersionUID= 1L;
	private String mdevicename;
	private String mdeviceaddress;
	private String mname;
	private String mpassword;
	private Boolean misOn;
	public CDevice(){
		
	}
	public CDevice(String str1,String str2,Boolean isOn){
		mdevicename=str1;
		mdeviceaddress=str2;
		misOn=isOn;
	}
	public CDevice(String str1,String str2,String str3,String str4,Boolean isOn){
		mdevicename=str1;
		mdeviceaddress=str2;
		mname=str3;
		mpassword=str4;
		misOn=isOn;
	}
	public void SetState(Boolean ison){
		misOn=ison;
	}
	public Boolean GetState(){
		return misOn;
	}
	public void Setname(String name){
		mname=name;
	}
	public void Setpassword(String password){
		mpassword=password;
	}
	public String Getdevicename(){
		return mdevicename;
	}
	public String Getdeviceaddress(){
		return mdeviceaddress;
	}
	public String Getname(){
		return mname;
	}
	public String Getpassword(){
		return mpassword;
	}
}


