package com.example.bluetooth_le_1;

import java.util.ArrayList;

import com.example.bluetooth_le_1.R.id;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;
import android.widget.TextView.BufferType;


public class MainActivity extends Activity {
	private final static String TAG = MainActivity.class.getSimpleName();
	private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<BluetoothDevice> devices;
    private boolean mScanning=false;
    private Handler mHandler;
    // Stops scanning after 2 seconds.
    private static final long SCAN_PERIOD = 2000;
    private static final int REQUEST_ENABLE_BT = 1;
	private ArrayList<CDevice> mLeDevices;
	private String filename ="file.txt";
	private file f;
	public static final int ITEM0 = Menu.FIRST;//Menu.FIRST是系统基准值
	public static final int ITEM1 = Menu.FIRST + 1;
	private long mExitTime;
	private TableLayout mylayout;
	private ImageView refresh;
	private ImageView add;
	private ImageView delete;
	private SharedPreferences sp;
	private int count=2;
    @Override
    public void onCreate(Bundle savedInstanceState) {//重写onCreate函数
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.main);
        mHandler = new Handler();
        BluetoothLE_Init();
        UI_Init();
    }
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
    	Devices_Init(); 
    }
    
    public void BluetoothLE_Init()
    {
    	Log.i(TAG, "BluetoothLE_Init");
    	if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
    	mBluetoothManager =(BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
        	sp = getSharedPreferences("UserInfo", 0);
        	Log.w(TAG, "indicate:"+sp.getBoolean("indicate", true));
        	if(sp.getBoolean("indicate", true))		//提示
        	{
        		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        		startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        	}
        	else									//不提示
        	{
        		mBluetoothAdapter.enable();
        	}          
        }
        //uuid[0]=UUID.fromString(SampleGattAttributes.DEVICE_SERVICE);
    }
    public void Devices_Init()
    {
     	Log.w(TAG, "Devices_Init");
    	mLeDevices= new ArrayList<CDevice>();
    	devices= new ArrayList<BluetoothDevice>();
    	f= new file( MainActivity.this);
    	mLeDevices=f.read(filename);
    	UI_Reflesh();
    }
    public void UI_Init()
    {
    	mylayout =(TableLayout)findViewById(id.tl_main);		
        mylayout.setStretchAllColumns(true);
        refresh=(ImageView)findViewById(id.iv_refresh);
        add=(ImageView)findViewById(id.iv_add);
        delete=(ImageView)findViewById(id.iv_delete);
        
        refresh.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mLeDevices=f.read(filename);
	    		Devices_Init(); 
			}
		});
    	add.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final Intent intent = new Intent( MainActivity.this, Add_activity.class);
		        startActivity(intent);
			}
		});
    	delete.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final Intent intent2 = new Intent( MainActivity.this, Delete_activity.class);
		        startActivity(intent2);
			}
		});
    }
    public void UI_Reflesh()
    {
    	mylayout.removeAllViews();	
    	int number=mLeDevices.size();
    	Log.w(TAG, "number:"+number);
    	Button Btn[] = new Button[number];				//动态创建按钮
    	int i=0;
    	int j=0;
    	for(i=0;i<number;i++)
    	{
    		CDevice mLeDevice=mLeDevices.get(i);
    		Btn[i]=new Button(this);
    		Btn[i].setId(i);
    		Btn[i].setTextColor(Color.RED);		//设置文字颜色
    		Btn[i].setText(mLeDevice.Getname());
    		Btn[i].setTypeface(Typeface.SERIF,Typeface.BOLD_ITALIC);
    		Btn[i].setTextSize(25);
    		Btn[i].setHeight(200);
    		Btn[i].setTag(0);					//设置标记，0代表不可用
    		//设置单击相应函数
    		Btn[i].setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					int id=v.getId();
    				Log.w(TAG, "LongClick+id:"+id);
    				final Intent intent = new Intent( MainActivity.this, Delete_activity.class);
    				intent.putExtra("id",id);
    		        startActivity(intent);
					return false;	//震动
					//return false;
				}
			});
    		Btn[i].setOnClickListener(new OnClickListener() {
    			public void onClick(View v) {
    				// TODO Auto-generated method stub
    				if((Integer)v.getTag()==1)		//1表示可用
					{
    					int id=v.getId();
        				Log.w(TAG, "Click+id:"+id);
        				final Intent intent = new Intent( MainActivity.this, Operate_activity.class);
        				intent.putExtra("id",id);
        		        startActivity(intent);
					}
    			}
    		});
    	}
    	int row=((number%count)>0)?(number/count+1):number/count;
    	TableRow tableRow[] = new TableRow[row];  
    	for(j=0;j<row;j++)
    	{
    		tableRow[j] =new TableRow(this);
    		tableRow[j].setWeightSum((float)0.25);
    		for(i=j*count;i<j*count+count;i++)
    		{
    			if(i>=number)						//补满
    			{
    				Button bt=new Button(this);
    				bt.setVisibility(View.INVISIBLE);
    				bt.setEnabled(false);
    				tableRow[j].addView(bt);
    			}
    			else
    			{
    				tableRow[j].addView(Btn[i]);
    			}
    		}
    		mylayout.addView(tableRow[j]);
    	}
    	invalidateOptionsMenu();
    	Log.w(TAG, "isEnabled:"+mBluetoothAdapter.isEnabled());
    	while(!mBluetoothAdapter.isEnabled()&&!sp.getBoolean("indicate", true));
    		scanLeDevice(true);
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                        Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                        mExitTime = System.currentTimeMillis();

                } 
                else {
                	sp = getSharedPreferences("UserInfo", 0);
                	Log.i(TAG, "close:"+sp.getBoolean("close", true));
                	if(sp.getBoolean("close", true))
                	{
                		if(mBluetoothAdapter!=null)			//退出关闭蓝牙
                		{
                			mBluetoothAdapter.disable();
                		}
                	}
                	finish();
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        scanLeDevice(true);
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(mScanning)
        {
        	scanLeDevice(false);
        }
    }
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	devices.clear();
        mLeDevices.clear();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.menu_add);
        if(mLeDevices.size()>0)
        	menu.findItem(R.id.menu_delete).setEnabled(true);
        else
        	menu.findItem(R.id.menu_delete).setEnabled(false);
        menu.findItem(R.id.menu_refresh);
        menu.findItem(R.id.menu_setting);
        return true;
    }
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	// TODO Auto-generated method stub
    	switch (item.getItemId()) {
    	case R.id.menu_add:
    		final Intent intent1 = new Intent( MainActivity.this, Add_activity.class);
	        startActivity(intent1);
	        break;
    	case R.id.menu_delete:
    		final Intent intent2 = new Intent( MainActivity.this, Delete_activity.class);
	        startActivity(intent2);
	        break;
    	case R.id.menu_refresh:
    		mLeDevices=f.read(filename);
    		Devices_Init(); 
    		break;
    	case R.id.menu_setting:
    		final Intent intent3 = new Intent( MainActivity.this, Setting_activity.class);
	        startActivity(intent3);
	        break;
    	}
    	return super.onMenuItemSelected(featureId, item);
    }
	private void scanLeDevice(final boolean enable) {
		if (enable) {
	          // Stops scanning after a pre-defined scan period.
	          mHandler.postDelayed(new Runnable() {
	          @Override
	          public void run() {
	              mScanning = false;
	              mBluetoothAdapter.stopLeScan(mLeScanCallback);
	          	}
	          }, SCAN_PERIOD);
	          mScanning = true;
	          mBluetoothAdapter.startLeScan(mLeScanCallback);
	          //扫描有指定uuid的服务的设备
	          //mBluetoothAdapter.startLeScan(uuid,mLeScanCallback);
	    } else {
	         mScanning = false;
	         mBluetoothAdapter.stopLeScan(mLeScanCallback);
	    }
    }
	private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                	if(!devices.contains(device))
                	{
                		devices.add(device); 
                		int index=IsContain(mLeDevices,device);
                    	if(index!=-1)				//已加设备
                    	{
                    		mLeDevices.remove(index);	//移除，节省时间
                    		Button bt=(Button)findViewById(index);
                    		bt.setTextColor(Color.GREEN);		//设置文字颜色
                    		bt.setTag(1);	
//                    		bt.setEnabled(true);
                    	}
                	}
                }
            });
        }
    };
    private int IsContain(ArrayList<CDevice> mLeDevices,BluetoothDevice device)
    {
    	int index=-1;
    	String name=device.getName();
    	String address=device.getAddress();
    	for(CDevice mLeDevice:mLeDevices)
    	{
    		String mname=mLeDevice.Getdevicename();
    		String maddress=mLeDevice.Getdeviceaddress();
    		if(mname.equals(name)&&maddress.equals(address))
    		{
    			index=mLeDevices.indexOf(mLeDevice);
    			Log.w(TAG, "contain"+index);
    			return index;
    		}		
    	}
    	Log.w(TAG, "notcontain"+index);
		return index;
    }
}

