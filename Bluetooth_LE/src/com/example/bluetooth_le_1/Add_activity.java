package com.example.bluetooth_le_1;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Add_activity extends Activity {
	private final static String TAG = Add_activity.class.getSimpleName();
	private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<BluetoothDevice> devices;
    private boolean mScanning;
    private Handler mHandler;
    private ArrayList<CDevice> mLeDevices;
    private String filename ="file.txt";
	private file f;
    private ListView deviceListview;
    private List<String> deviceList=new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private ActionBar mActionBar;

    // Stops scanning after 2 seconds.
    private static final long SCAN_PERIOD = 2000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan);
        mActionBar=getActionBar();
        //mActionBar.setHomeButtonEnabled(true);			//图标是否可点击，4.0以后默认false
        mActionBar.setDisplayHomeAsUpEnabled(true);			//添加返回图标
        mActionBar.setDisplayShowHomeEnabled(true);   		//使左上角重新图标是否显示
        mActionBar.setTitle(R.string.title_devices);
       
        mHandler = new Handler();
        BluetoothLE_Init();
        Devices_Init();
    }
    public void BluetoothLE_Init()
    {
    	mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        //uuid[0]=UUID.fromString(SampleGattAttributes.DEVICE_SERVICE);
        Log.w(TAG, "BluetoothLE_Init");
    }
    public void Devices_Init()
    {
    	mLeDevices= new ArrayList<CDevice>();
    	devices= new ArrayList<BluetoothDevice>();
    	f= new file(Add_activity.this);
    	mLeDevices=f.read(filename);
    	Log.w(TAG, "Devices_Init");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan_menu, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
            	deviceList.clear();
            	adapter.notifyDataSetChanged();
            	devices.clear();
            	mLeDevices=f.read(filename);
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
            case android.R.id.home:
            	Add_activity.this.finish();
            	//onBackPressed();
            	break;
        }
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        deviceListview=(ListView)findViewById(R.id.listview);  
        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceList);  
        deviceListview.setAdapter(adapter);  
        deviceListview.setOnItemClickListener(listener); 
        scanLeDevice(true);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(mScanning)
        	scanLeDevice(false);
        deviceList.clear();
    	mLeDevices.clear();
    	devices.clear();
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
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
        invalidateOptionsMenu();
    }
 // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                	if(!devices.contains(device))
                	{
                		Log.w(TAG, "device");
                		devices.add(device);
                		int index=IsContain(mLeDevices,device);
                    	if(index==-1)				//新设备
                    	{
                    		deviceList.add(device.getName()+'\n'+device.getAddress());  
                            adapter.notifyDataSetChanged();
                    	}
                    	else
                    	{
                    		mLeDevices.remove(index);	//移除，节省时间
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
    private OnItemClickListener listener=new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			BluetoothDevice device=	(BluetoothDevice) devices.get(position);
			if (device == null) 
				return;
            final Intent intent = new Intent(Add_activity.this, Init_activity.class);
            intent.putExtra("name", device.getName());
            intent.putExtra("address", device.getAddress());
            startActivity(intent);
            Add_activity.this.finish();
		}
	};
}