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
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Edit_activity extends Activity{
		private final static String TAG = Edit_activity.class.getSimpleName();;
		private ArrayList<CDevice> mLeDevices;
		private CDevice mLeDevice;
		private String filename ="file.txt";
		private file f;
		private Button edit;
		private TextView tv1,tv2,tv11,tv22,tv3,tv4,tv5,tv55;
		private EditText ed1,ed2,et;
		private int id;
		private String address;
		private String sendmessage;
		private String receivemessage;
		private ActionBar mActionBar;
		
		//�����ݵķ����characteristic
	    private BluetoothGattService rd_wr_GattService;
	    private BluetoothGattCharacteristic rd_wr_Characteristic; 
	    private BluetoothLeService mBluetoothLeService;
	    private boolean mConnected = false;
	    private BluetoothGattCharacteristic mNotifyCharacteristic;
	    
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.edit);
	        mActionBar=getActionBar();
	        //mActionBar.setHomeButtonEnabled(true);			//ͼ���Ƿ�ɵ����4.0�Ժ�Ĭ��false
	        mActionBar.setDisplayHomeAsUpEnabled(true);			//��ӷ���ͼ��
	        mActionBar.setDisplayShowHomeEnabled(true);   		//ʹ���Ͻ�����ͼ���Ƿ���ʾ
	        final Intent intent = getIntent();
	        id=intent.getIntExtra("id", 0);
	        Devices_Init();
	        et=new EditText(this);
	        et.setTransformationMethod(PasswordTransformationMethod.getInstance());
//	        et.setEms(6);
	        et.setMaxEms(6);
	        AlertDialog mydialoginstance = new AlertDialog.Builder(Edit_activity.this) //����Ĳ���ShowDialog_ActivityΪ����
	                //.setIcon(R.drawable.icon)//ͼ�꣬��ʾ�ڶԻ���������
	                .setTitle("����������")  //�Ի������
	                .setCancelable(false)
	                .setView(et)
	                .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() { //��ȷ������ť��Ӧ�Ĺ���
	                			public void onClick(DialogInterface dialog, int whichButton) {//�����Ƿ��е������ȷ����ť
	                				String str=(et.getText()).toString();
	                				str=str+"000000";
	            					str=str.substring(0, 6);		//��������Ϊ6λ
	                				if(str.equals(mLeDevice.Getpassword()))		//����������ȷ
	                				{
	                					Log.i(TAG, "password��ȷ:"+str+mLeDevice.Getpassword());
	                					dialog.cancel();
	                				}
	                				else										//�������벻��ȷ
	                				{
	                					Log.i(TAG, "password����"+str+mLeDevice.Getpassword());
	                					dialog.cancel();
	                					Toast. makeText (Edit_activity.this, "�������",Toast. LENGTH_SHORT ).show();
		                				Edit_activity.this.finish();
	                				}	
	                			}})
	                .setNegativeButton("ȡ��", new DialogInterface.OnClickListener() { //����������ť��Ӧ�Ĺ���
	                			public void onClick(DialogInterface dialog, int whichButton) {  //�����Ƿ��е������ȡ����ť
	                				dialog.cancel();
	                				Edit_activity.this.finish();
	                			}})
	                		.create(); 
	                mydialoginstance.show(); //��ʾ�Ի���
	        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
	        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	    }
	    public void Devices_Init()
	    {
	    	mLeDevices= new ArrayList<CDevice>();
	    	f= new file(Edit_activity.this);
	    	mLeDevices=f.read(filename);
	    	mLeDevice=mLeDevices.get(id);
	    	mActionBar.setTitle(mLeDevice.Getname());
	    	address=mLeDevice.Getdeviceaddress();
	    	UI_Init();
	    }
	    public void UI_Init()
	    {
	    	tv1=(TextView)findViewById(R.id.e_devicename1);
	    	tv11=(TextView)findViewById(R.id.e_devicename2);
	    	tv2=(TextView)findViewById(R.id.e_deviceaddress1);
	    	tv22=(TextView)findViewById(R.id.e_deviceaddress2);
	    	tv3=(TextView)findViewById(R.id.e_name);
	    	tv4=(TextView)findViewById(R.id.e_password);
	    	tv5=(TextView)findViewById(R.id.e_state1);
	    	tv55=(TextView)findViewById(R.id.e_state2);
	    	ed1=(EditText)findViewById(R.id.e_e_name);
	    	ed2=(EditText)findViewById(R.id.e_e_password);
	    	edit=(Button)findViewById(R.id.bt_edit);
	    	
	    	tv11.setTextColor(Color.BLUE);
	    	tv11.setText(mLeDevice.Getdevicename());
	    	tv22.setTextColor(Color.BLUE);
	    	tv22.setText(mLeDevice.Getdeviceaddress());
	    	tv55.setTextColor(Color.BLUE);
	    	String str;
	    	if(mLeDevice.GetState())
//	    		str="on";
	    		str="��";
	    	else
//	    		str="off";
	    		str="��";
	    	tv55.setText(str);
	    	ed1.setTextColor(Color.BLUE);
	    	ed1.setText(mLeDevice.Getname());
	    	ed2.setTextColor(Color.BLUE);
	    	ed2.setText(mLeDevice.Getpassword());
	    	
	    	tv1.setTypeface(Typeface.SERIF,Typeface.BOLD_ITALIC);
	    	tv11.setTypeface(Typeface.SERIF,Typeface.BOLD_ITALIC);
	    	tv2.setTypeface(Typeface.SERIF,Typeface.BOLD_ITALIC);
	    	tv22.setTypeface(Typeface.SERIF,Typeface.BOLD_ITALIC);
	    	tv3.setTypeface(Typeface.SERIF,Typeface.BOLD_ITALIC);
	    	tv4.setTypeface(Typeface.SERIF,Typeface.BOLD_ITALIC);
	    	tv5.setTypeface(Typeface.SERIF,Typeface.BOLD_ITALIC);
	    	tv55.setTypeface(Typeface.SERIF,Typeface.BOLD_ITALIC);
	    	ed1.setTypeface(Typeface.SERIF,Typeface.BOLD_ITALIC);
	    	ed2.setTypeface(Typeface.SERIF,Typeface.BOLD_ITALIC);
	    	edit.setTypeface(Typeface.SERIF,Typeface.BOLD_ITALIC);
	    	
	    	edit.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(rd_wr_Characteristic==null)
	            	{
						Toast. makeText (Edit_activity.this, "δ�����������豸",Toast. LENGTH_SHORT ).show();
	            		Log.i(TAG, "edit:rd_wr_Characteristic is null");
	            		return;
	            	}
					else
					{
						String str1=tv1.getText().toString();
						String str3=ed1.getText().toString();
						String str4=ed2.getText().toString();
						Log.i(TAG, "str4:"+str4);
						if(str4.isEmpty())
							str3=str1;
						if(str4.isEmpty())
						{
							mActionBar.setTitle(mLeDevice.Getname()+"     "+"���벻��Ϊ��");
//							Toast. makeText (Edit_activity.this, "���벻��Ϊ��",Toast. LENGTH_SHORT ).show();
						}
						else
						{
							mLeDevice.Setname(str3);
							if(str4.equals(mLeDevice.Getpassword()))		//����û��
							{
								mLeDevices.set(id, mLeDevice);
								f.write(mLeDevices,filename);   
//								mActionBar.setTitle(mLeDevice.Getname()+"     "+"�޸ĳɹ�");
			                	Toast. makeText (Edit_activity.this, "�޸ĳɹ�",Toast. LENGTH_SHORT ).show();
			                	Edit_activity.this.finish();
							}
							else
							{
								str4=str4+"000000";
								str4=str4.substring(0, 6);		//��������Ϊ6λ
								sendmessage=Define.str_changepassword+mLeDevice.Getpassword()+str4;
								mLeDevice.Setpassword(str4);
								rd_wr_Characteristic.setValue(sendmessage);
								mBluetoothLeService.writeCharacteristic(rd_wr_Characteristic);
								Log.i(TAG, "sendmessage:"+sendmessage);
								mActionBar.setTitle(mLeDevice.Getname()+"     "+"�����޸����룬���Ժ�...");
							}
						}
					}
				}
			});
	    }
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	            case android.R.id.home:
	            	Edit_activity.this.finish();
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
	            		mLeDevices.set(id, mLeDevice);
						f.write(mLeDevices,filename);   
	                	Log.i("a", "��������"+receivemessage);
//	                	mActionBar.setTitle(mLeDevice.Getname()+"     "+"�޸ĳɹ�");
	                	Toast. makeText (Edit_activity.this, "�޸ĳɹ�",Toast. LENGTH_SHORT ).show();
	                	mBluetoothLeService.close();
	                	Edit_activity.this.finish();
	            	}
	            	else
	            	{
	            		mActionBar.setTitle(mLeDevice.Getname()+"     "+"�޸�ʧ��");
//	            		Toast. makeText (Edit_activity.this, "�޸�ʧ��"+receivemessage,Toast. LENGTH_SHORT ).show();
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
	        unbindService(mServiceConnection);
	        mBluetoothLeService = null;
	        unregisterReceiver(mGattUpdateReceiver);
	    }

	    @Override
	    protected void onDestroy() {
	        super.onDestroy();
	        //unbindService(mServiceConnection);
	        //mBluetoothLeService = null;
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

