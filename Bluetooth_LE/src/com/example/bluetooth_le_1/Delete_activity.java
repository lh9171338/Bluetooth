package com.example.bluetooth_le_1;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;


public class Delete_activity extends Activity{
	private ArrayList<CDevice> mLeDevices;
	private String filename ="file.txt";
	private file f;
	private int id;
	private boolean haveid;
	private ActionBar mActionBar;
	private int count=2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar=getActionBar();
        //mActionBar.setHomeButtonEnabled(true);			//图标是否可点击，4.0以后默认false
        mActionBar.setDisplayHomeAsUpEnabled(true);			//添加返回图标
        mActionBar.setDisplayShowHomeEnabled(true);   		//使左上角重新图标是否显示
        mActionBar.setTitle("删除");
        final Intent intent = getIntent();
        haveid=intent.hasExtra("id");
        if(haveid)
        {
        	id=intent.getIntExtra("id", 0);
        }
        Devices_Init();   
    }
    public void Devices_Init()
    {
    	mLeDevices= new ArrayList<CDevice>();
    	f= new file(Delete_activity.this);
    	mLeDevices=f.read(filename);
    	UI_Init();
    }
    public void UI_Init()
    {
    	Resources res = getResources();
    	Drawable drawable = res.getDrawable(R.drawable.pic12);
    	//自定义layout组件
    	TableLayout mylayout = new TableLayout(this);	//创建表格布局
    	mylayout.setBackground(drawable);
//        mylayout.setBackgroundColor(Color.CYAN);		//设置背景色
        mylayout.setStretchAllColumns(true);			//充满屏幕
    	int number=mLeDevices.size();
    	Button Btn[] = new Button[number];				//动态创建按钮
    	int i=0;
    	int j=0;
    	for(i=0;i<number;i++)
    	{
    		CDevice mLeDevice=mLeDevices.get(i);
    		Btn[i]=new Button(this);
    		Btn[i].setId(i);
    		Btn[i].setTextColor(Color.BLUE);		//设置文字颜色
    		Btn[i].setText(mLeDevice.Getname());
    		Btn[i].setTypeface(Typeface.SERIF,Typeface.BOLD_ITALIC);
    		Btn[i].setTextSize(25);
    		Btn[i].setHeight(200);
    		//设置单击相应函数
    		Btn[i].setOnClickListener(new OnClickListener() {
    			public void onClick(View v) {
    				// TODO Auto-generated method stub
    				id=v.getId();
    				CDevice mLeDevice=mLeDevices.get(id);
    				AlertDialog mydialoginstance = new AlertDialog.Builder(Delete_activity.this) //这里的参数ShowDialog_Activity为类名
                    //.setIcon(R.drawable.icon)//图标，显示在对话框标题左侧
                    .setTitle("删除车辆"+mLeDevice.Getname())  //对话框标题
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() { //“确定”按钮对应的功能
                    			public void onClick(DialogInterface dialog, int whichButton) {//侦听是否有单击这个确定按钮
                    				mLeDevices.remove(id);
                    				f.write(mLeDevices,filename);
                    				dialog.cancel();
                    				Delete_activity.this.finish();
                    				//Toast.makeText(getApplicationContext(), "感谢您输入了信息，再见", Toast.LENGTH_LONG).show();
                    			}})
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() { //“结束”按钮对应的功能
                    			public void onClick(DialogInterface dialog, int whichButton) {  //侦听是否有单击这个取消按钮
                    				dialog.cancel();
                    			}})
                    		.create(); 
                    mydialoginstance.show(); //显示对话框 
    			}
    		});
    	}
    	int row=((number%count)>0)?(number/count+1):number/count;
    	TableRow tableRow[] = new TableRow[row];  
    	for(j=0;j<row;j++)
    	{
    		tableRow[j] =new TableRow(this);
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
    	if(haveid)
    	{
    		CDevice mLeDevice=mLeDevices.get(id);
			AlertDialog mydialoginstance = new AlertDialog.Builder(Delete_activity.this) //这里的参数ShowDialog_Activity为类名
            //.setIcon(R.drawable.icon)//图标，显示在对话框标题左侧
            .setTitle("删除车辆"+mLeDevice.Getname())  //对话框标题
            .setCancelable(false)
            .setPositiveButton("确定", new DialogInterface.OnClickListener() { //“确定”按钮对应的功能
            			public void onClick(DialogInterface dialog, int whichButton) {//侦听是否有单击这个确定按钮
            				mLeDevices.remove(id);
            				f.write(mLeDevices,filename);
            				dialog.cancel();
            				Delete_activity.this.finish();
            			}})
            .setNegativeButton("取消", new DialogInterface.OnClickListener() { //“结束”按钮对应的功能
            			public void onClick(DialogInterface dialog, int whichButton) {  //侦听是否有单击这个取消按钮
            				dialog.cancel();
            				Delete_activity.this.finish();
            			}})
            		.create(); 
            mydialoginstance.show(); //显示对话框
    	}
    	setContentView(mylayout);//显示设定的布局mylayout
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            	Delete_activity.this.finish();
            	//onBackPressed();
            	break;
        }
        return true;
    }
}
