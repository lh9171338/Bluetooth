package com.example.bluetooth_le_1;


import java.util.ArrayList;
import java.util.UUID;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class Operate_activity extends Activity{
	private final static String TAG = Operate_activity.class.getSimpleName();
	private ArrayList<CDevice> mLeDevices;
	private CDevice mLeDevice;
	private String filename ="file.txt";
	private file f;
	private Switch sw1;
	private TextView tv1;
	private int id;
	private String address;
	private String sendmessage;
	private String receivemessage;
	private ActionBar mActionBar;
	private boolean isearsing=false;
    
    
	//�����ݵķ����characteristic
    private BluetoothGattService rd_wr_GattService;
    private BluetoothGattCharacteristic rd_wr_Characteristic; 
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.operate);
        mActionBar=getActionBar();
        //mActionBar.setHomeButtonEnabled(true);			//ͼ���Ƿ�ɵ����4.0�Ժ�Ĭ��false
        mActionBar.setDisplayHomeAsUpEnabled(true);			//��ӷ���ͼ��
        mActionBar.setDisplayShowHomeEnabled(true);   		//ʹ���Ͻ�����ͼ���Ƿ���ʾ
        final Intent intent = getIntent();
        id=intent.getIntExtra("id", 0);
    }
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
    	Devices_Init(); 
    }
    public void Devices_Init()
    {
    	mLeDevices= new ArrayList<CDevice>();
    	f= new file(Operate_activity.this);
    	mLeDevices=f.read(filename);
    	mLeDevice=mLeDevices.get(id);
    	mActionBar.setTitle(mLeDevice.Getname());
    	address=mLeDevice.Getdeviceaddress();
    	UI_Init();
    }
    public void UI_Init()
    {
    	tv1=(TextView)findViewById(R.id.o_name);
    	tv1.setTypeface(Typeface.SERIF,Typeface.BOLD_ITALIC);
    	tv1.setText("��������: "+mLeDevice.Getname());
    	sw1=(Switch)findViewById(R.id.o_switch);
    	sw1.setTypeface(Typeface.SERIF,Typeface.BOLD_ITALIC);
    	sw1.setChecked(mLeDevice.GetState());
    	sw1.setOnClickListener(new View.OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                boolean checked = ((Switch) v).isChecked();  
//                mLeDevice.SetState(true);
//        		mLeDevices.set(id, mLeDevice);
//                f.write(mLeDevices, filename);
            	if(rd_wr_Characteristic==null)
            	{
            		Toast. makeText (Operate_activity.this, "δ�����������豸",Toast. LENGTH_SHORT ).show();
            		Log.i(TAG, "Switch:rd_wr_Characteristic is null");
            		return;
            	}
            	else
            	{
            		sw1.setChecked(!checked);
                    if (checked) {  							//on
                    	sendmessage=Define.str_seton+mLeDevice.Getpassword()+Define.str_freedata;
                    	rd_wr_Characteristic.setValue(sendmessage);
                		mBluetoothLeService.writeCharacteristic(rd_wr_Characteristic);
     					Log.i(TAG, "sendmessage:"+sendmessage);
     					mActionBar.setTitle(mLeDevice.Getname()+"     "+"���ڿ��������Ժ�...");
                     } else {  									//off
                    	sendmessage=Define.str_setoff+mLeDevice.Getpassword()+Define.str_freedata;
                    	rd_wr_Characteristic.setValue(sendmessage);
                		mBluetoothLeService.writeCharacteristic(rd_wr_Characteristic);
      					Log.i(TAG, "sendmessage:"+sendmessage);
      					mActionBar.setTitle(mLeDevice.Getname()+"     "+"���ڹ��������Ժ�...");
                     }   
            	}
            }  
        });  
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
            	    	GetState();													//��ȡ״̬
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
            	if(receivemessage.equals(Define.str_on))				//state:on
            	{
            		sw1.setChecked(true);
            		mLeDevice.SetState(true);
            		mLeDevices.set(id, mLeDevice);
                    f.write(mLeDevices, filename);
            	}
            	else if(receivemessage.equals(Define.str_off))		//state:off
            	{
            		sw1.setChecked(false);
            		mLeDevice.SetState(false);
            		mLeDevices.set(id, mLeDevice);
                    f.write(mLeDevices, filename);
            	}
            	else if(receivemessage.equals(Define.str_ok))        //state changed or erase successfully
            	{
                	Log.i("a", "��������"+receivemessage);				 
                	if(isearsing==true)								 //erase successfully
                	{
                		mLeDevices.remove(id);
        				f.write(mLeDevices,filename);
        				Operate_activity.this.finish();
        				Toast. makeText (Operate_activity.this, "ɾ���ɹ�",Toast. LENGTH_SHORT ).show();
                	}
                	else											 //state changed successfully
                	{
                		boolean state=sw1.isChecked();
                		sw1.setChecked(!state);
                    	mLeDevice.SetState(!state);
                		mLeDevices.set(id, mLeDevice);
                        f.write(mLeDevices, filename);
                    	mActionBar.setTitle(mLeDevice.Getname()+"     "+"�����ɹ�");
                	}
            	}
            	else
            	{
            		mActionBar.setTitle(mLeDevice.Getname()+"     "+"����ʧ��");
//            		Toast. makeText (Operate_activity.this, "����ʧ��"+receivemessage,Toast. LENGTH_SHORT ).show();
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
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(address);
            Log.d(TAG, "Connect request result=" + result);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.operate_menu, menu);
        menu.findItem(R.id.menu_edit);
        menu.findItem(R.id.menu_erase);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_edit:
            	Log.w(TAG, "Edit+id:"+id);
				final Intent intent = new Intent(Operate_activity.this, Edit_activity.class);
				mBluetoothLeService.close();
				intent.putExtra("id",id);
		        startActivity(intent);
                return true;
            case R.id.menu_erase:
            	if(rd_wr_Characteristic==null)
            	{
            		Toast. makeText (Operate_activity.this, "δ�����������豸",Toast. LENGTH_SHORT ).show();
            		Log.i(TAG, "Switch:rd_wr_Characteristic is null");
            		break;
            	}
            	AlertDialog mydialoginstance = new AlertDialog.Builder(Operate_activity.this) 
                //.setIcon(R.drawable.icon)//ͼ�꣬��ʾ�ڶԻ���������
                .setTitle("ɾ������"+mLeDevice.Getname()+"��������������룡")  //�Ի������
                .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() { //��ȷ������ť��Ӧ�Ĺ���
                			public void onClick(DialogInterface dialog, int whichButton) {//�����Ƿ��е������ȷ����ť
                				sendmessage=Define.str_erase+mLeDevice.Getpassword()+Define.str_freedata;
                            	rd_wr_Characteristic.setValue(sendmessage);
                        		mBluetoothLeService.writeCharacteristic(rd_wr_Characteristic);
             					Log.i(TAG, "sendmessage:"+sendmessage);
             					isearsing=true;
             					mActionBar.setTitle(mLeDevice.Getname()+"     "+"����ɾ�������Ժ�...");
//                				mLeDevices.remove(id);
//                				f.write(mLeDevices,filename);
                				dialog.cancel();
//                				Operate_activity.this.finish();
                			}})
                .setNegativeButton("ȡ��", new DialogInterface.OnClickListener() { //����������ť��Ӧ�Ĺ���
                			public void onClick(DialogInterface dialog, int whichButton) {  //�����Ƿ��е������ȡ����ť
                				dialog.cancel();
                			}})
                		.create(); 
                mydialoginstance.show(); //��ʾ�Ի���
                return true;
            case android.R.id.home:
            	Operate_activity.this.finish();
            	//onBackPressed();
            	break;
        }
        return super.onOptionsItemSelected(item);
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
    private void GetState()
    {
    	sendmessage=Define.str_getstate+mLeDevice.Getpassword()+Define.str_freedata;
    	rd_wr_Characteristic.setValue(sendmessage);
		mBluetoothLeService.writeCharacteristic(rd_wr_Characteristic);
		Log.i(TAG, "sendmessage:"+sendmessage);
    }
}
