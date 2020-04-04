package com.example.bluetooth_le_1;

import java.util.HashMap;

public class Define {
	//-------------------------receive data--------------------------------//
	public static String str_ok="1";
	public static String str_error="0";
	public static String str_on="2";
	public static String str_off="3";
	//-------------------------sent data--------------------------------//
	public static String str_setpassword="0";
	public static String str_changepassword="1";
	public static String str_seton="2";
	public static String str_setoff="3";
	public static String str_getstate="4";
	public static String str_erase="5";
	public static String str_freedata="000000";
	
	private static HashMap<String, String> attributes = new HashMap();
    public static String DEVICE_SERVICE = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static String DEVICE_CHARACTERISTIC_SERVICE = "0000ffe1-0000-1000-8000-00805f9b34fb";
    static {
        attributes.put("0000ffe0-0000-1000-8000-00805f9b34fb", "Device Service");
        attributes.put("0000ffe1-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
    }
    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
