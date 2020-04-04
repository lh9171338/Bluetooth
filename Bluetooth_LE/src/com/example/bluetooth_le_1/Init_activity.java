package com.example.bluetooth_le_1;

import java.util.ArrayList;
import java.util.UUID;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Init_activity extends Activity{
	private final static String TAG = Init_activity.class.getSimpleName();
	private ArrayList<CDevice> mLeDevices;
	private CDevice mLeDevice;
	private String filename ="file.txt";
	private file f;
	private Button add;
	private TextView tv1,tv11,tv2,tv22,tv3,tv4;
	private EditText ed1,ed2;
	private String name;
	private String address;
	private String sendmessage;
	private String receivemessage;
	private ActionBar mActionBar;
	
    //读数据的服务和characteristic
    private BluetoothGattService rd_wr_GattService;
    private BluetoothGattCharacteristic rd_wr_Characteristic; 
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init);
        mActionBar=getActionBar();
        //mActionBar.setHomeButtonEnabled(true);			//图标是否可点击，4.0以后默认false
        mActionBar.setDisplayHomeAsUpEnabled(true);			//添加返回图标
        mActionBar.setDisplayShowHomeEnabled(true);   		//使左上角重新图标是否显示
        final Intent intent = getIntent();
        name=intent.getStringExtra("name");
        address=intent.getStringExtra("address");
        mActionBar.setTitle(name);
        Devices_Init();
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }
    public void Devices_Init()
    {
    	mLeDevices= new ArrayList<CDevice>();
    	mLeDevice=new CDevice(name,address,false);
    	f= new file(Init_activity.this);
    	mLeDevices=f.read(filename);
    	UI_Init();
    }
    public void UI_Init()
    {
    	tv1=(TextView)findViewById(R.id.s_devicename1);
    	tv11=(TextView)findViewById(R.id.s_devicename2);
    	tv2=(TextView)findViewById(R.id.s_deviceaddress1);
    	tv22=(TextView)findViewById(R.id.s_deviceaddress2);
    	tv3=(TextView)findViewById(R.id.s_name);
    	tv4=(TextView)findViewById(R.id.s_password);
    	ed1=(EditText)findViewById(R.id.s_e_name);
    	ed2=(EditText)findViewById(R.id.s_e_password);
    	add=(Button)findViewById(R.id.s_add);
    	
    	tv11.setTextColor(Color.BLUE);
    	tv11.setText(name);
    	tv22.setTextColor(Color.BLUE);
    	tv22.setText(address);
    	ed1.setTextColor(Color.BLUE);
    	ed1.setText("");
    	ed2.setTextColor(Color.BLUE);
    	ed2.setText("");
    	
    	tv1.setTypeface(Typeface.SERIF,Typeface.BOLD_ITALIC);
    	tv11.setTypeface(Typeface.SERIF,Typeface.BOLD_ITALIC);
    	tv2.setTypeface(Typeface.SERIF,Typeface.BOLD_ITALIC);
    	tv22.setTypeface(Typeface.SERIF,Typeface.BOLD_ITALIC);
    	tv3.setTypeface(Typeface.SERIF,Typeface.BOLD_ITALIC);
    	tv4.setTypeface(Typeface.SERIF,Typeface.BOLD_ITALIC);
    	ed1.setTypeface(Typeface.SERIF,Typeface.BOLD_ITALIC);
    	ed2.setTypeface(Typeface.SERIF,Typeface.BOLD_ITALIC);
    	add.setTypeface(Typeface.SERIF,Typeface.BOLD_ITALIC);
    	
    	add.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(rd_wr_Characteristic==null)
            	{
					Toast. makeText (Init_activity.this, "未连接上蓝牙设备",Toast. LENGTH_SHORT ).show();
            		Log.i(TAG, "add:rd_wr_Characteristic is null");
            		return;
            	}
				else
				{
					String str1=tv11.getText().toString();
					String str3=ed1.getText().toString();
					String str4=ed2.getText().toString();
					if(str3.isEmpty())
						str3=str1;
					if(str4.isEmpty())
					{
						mActionBar.setTitle(mLeDevice.Getname()+"     "+"密码不能为空");
//						Toast. makeText (Init_activity.this, "密码不能为空",Toast. LENGTH_SHORT ).show();
					}
					else
					{
						str4=str4+"000000";
						str4=str4.substring(0, 6);		//长度扩充为6位
						mLeDevice.Setname(str3);
						mLeDevice.Setpassword(str4);
						sendmessage=Define.str_setpassword+str4+Define.str_freedata;
						rd_wr_Characteristic.setValue(sendmessage);
						mBluetoothLeService.writeCharacteristic(rd_wr_Characteristic);
						mActionBar.setTitle(mLeDevice.Getname()+"     "+"正在添加，请稍候...");
						Log.i(TAG, "sendmessage:"+sendmessage);
					}
				}
			}
		});
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            	Init_activity.this.finish();
            	//onBackPressed();
            	break;
        }
        return true;
    }
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(address);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                Log.i(TAG, "mConnected:"+mConnected);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                Log.i(TAG, "mConnected:"+mConnected);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            	rd_wr_GattService= mBluetoothLeService.getSupportedGattServices(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
            	if(rd_wr_GattService==null)
            		Log.i(TAG, "rd_wr_GattService:null");
            	else
            	{
            		rd_wr_Characteristic=rd_wr_GattService.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
            	    if(rd_wr_Characteristic==null)
            	    	Log.i(TAG, "rd_wr_Characteristic:null");
            	    else
            	    {
            	    	final int charaProp = rd_wr_Characteristic.getProperties();
            	        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            	            if (mNotifyCharacteristic != null) {
            	                mBluetoothLeService.setCharacteristicNotification(
            	                        mNotifyCharacteristic, false);
            	                mNotifyCharacteristic = null;
            	            }
            	            mBluetoothLeService.readCharacteristic(rd_wr_Characteristic);
            	        }
            	        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            	            mNotifyCharacteristic = rd_wr_Characteristic;
            	            mBluetoothLeService.setCharacteristicNotification(
            	            		rd_wr_Characteristic, true);
            	        } 
            	    }
            	}
            } else if (BluetoothLeService.ACTION_DATA_CHANGED.equals(action)) {
            	receivemessage=intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
            	if(receivemessage.equals(Define.str_ok))
            	{
            		mLeDevices.add(mLeDevice);
					f.write(mLeDevices,filename);
                	Log.e("a", "读到数据"+receivemessage);
//                	mActionBar.setTitle(mLeDevice.Getname()+"     "+"添加成功");
                	Toast. makeText (Init_activity.this, "添加成功",Toast. LENGTH_SHORT ).show();
                	Init_activity.this.finish();
            	}
            	else
            	{
            		mActionBar.setTitle(mLeDevice.Getname()+"     "+"添加失败");
//            		Toast. makeText (Init_activity.this, "添加失败"+receivemessage,Toast. LENGTH_SHORT ).show();
            	}
            }
            else if (BluetoothLeService.ACTION_DATA_READ.equals(action)) {
            	String str=intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
            	Log.i(TAG, "ACTION_DATA_READ+"+str);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(address);
            Log.w(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_READ);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_CHANGED);
        return intentFilter;
    }
}
