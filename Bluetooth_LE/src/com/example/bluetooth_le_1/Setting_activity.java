package com.example.bluetooth_le_1;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;



public class Setting_activity extends Activity{
	private final static String TAG = Setting_activity.class.getSimpleName();
	private CheckBox cb1,cb2;
	private TextView tv1,tv2;
	private ActionBar mActionBar;
	private SharedPreferences sp;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stting);
        mActionBar=getActionBar();
        //mActionBar.setHomeButtonEnabled(true);			//图标是否可点击，4.0以后默认false
        mActionBar.setDisplayHomeAsUpEnabled(true);			//添加返回图标
        mActionBar.setDisplayShowHomeEnabled(true);   		//使左上角重新图标是否显示
        mActionBar.setTitle("设置");
        Devices_Init();
    }
    public void Devices_Init()
    {
    	sp = getSharedPreferences("UserInfo", 0);
    	UI_Init();
    }
    public void UI_Init()
    {
    	tv1=(TextView)findViewById(R.id.setting_item1);
    	tv2=(TextView)findViewById(R.id.steting_item2);
    	tv1.setTypeface(Typeface.SERIF,Typeface.BOLD_ITALIC);
    	tv2.setTypeface(Typeface.SERIF,Typeface.BOLD_ITALIC);
    	cb1=(CheckBox)findViewById(R.id.checkBox_item1);
    	cb2=(CheckBox)findViewById(R.id.checkBox_item2);
    	cb1.setChecked(sp.getBoolean("indicate", true));
    	cb2.setChecked(sp.getBoolean("close", true));
    	Log.i(TAG, "sw1:"+sp.getBoolean("indicate", true));
    	cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
               sp = getSharedPreferences("UserInfo", 0);
               sp.edit().putBoolean("indicate", isChecked).commit();
        }});
    	cb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
               sp = getSharedPreferences("UserInfo", 0);
               sp.edit().putBoolean("close", isChecked).commit();
        }});
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            	Setting_activity.this.finish();
            	//onBackPressed();
            	break;
        }
        return true;
    }
}

