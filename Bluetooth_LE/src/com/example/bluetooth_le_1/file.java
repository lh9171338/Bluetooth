package com.example.bluetooth_le_1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

public class file {
	private final static String TAG = "file";
	private int mlength;
	private Context mcontext;
	public file(){}
	public file(Context context)
	{
		mcontext=context;
	}
	public ArrayList<CDevice> read(String filename)
    {
		ArrayList<CDevice> mLeDevices=new ArrayList<CDevice>();
		try{
    		FileInputStream inStream=mcontext.openFileInput(filename);
    		ObjectInputStream objectInputStream= new ObjectInputStream(inStream);
    		mlength=objectInputStream.readInt();
    		Log.w(TAG, "readnumber"+mlength);
    		int i;
    		for(i=0;i<mlength;i++)
    		{
    			CDevice mLeDevice =(CDevice)objectInputStream.readObject();
    			mLeDevices.add(mLeDevice);
    			Log.w(TAG, "read"+mLeDevice);
    		}
    		inStream.close();
    	} catch(FileNotFoundException e)
    	{
    		e.printStackTrace();
    	} catch(IOException e)
    	{
    		e.printStackTrace();
    	} catch(ClassNotFoundException e)
    	{
    		e.printStackTrace();
    	}
		return mLeDevices;
    }
	public void write(ArrayList<CDevice> mLeDevices,String filename)
    {
    	try{
    		FileOutputStream outStream=mcontext.openFileOutput(filename,mcontext.MODE_PRIVATE);
    		ObjectOutputStream objectOutputStream= new ObjectOutputStream(outStream);
    		mlength=mLeDevices.size();
    		Log.w(TAG, "writenumber"+mlength);
    		objectOutputStream.writeInt(mlength);
    		for(CDevice mLeDevice:mLeDevices)
    		{
    			objectOutputStream.writeObject(mLeDevice);
        		Log.w(TAG, "write"+mLeDevice);
    		}
    		outStream.close();
    	} catch(FileNotFoundException e)
    	{
    		e.printStackTrace();
    	} catch(IOException e)
    	{
    		e.printStackTrace();
    	}
    }


}

