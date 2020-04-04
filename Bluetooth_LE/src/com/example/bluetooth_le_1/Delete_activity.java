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
        //mActionBar.setHomeButtonEnabled(true);			//ͼ���Ƿ�ɵ����4.0�Ժ�Ĭ��false
        mActionBar.setDisplayHomeAsUpEnabled(true);			//��ӷ���ͼ��
        mActionBar.setDisplayShowHomeEnabled(true);   		//ʹ���Ͻ�����ͼ���Ƿ���ʾ
        mActionBar.setTitle("ɾ��");
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
    	//�Զ���layout���
    	TableLayout mylayout = new TableLayout(this);	//������񲼾�
    	mylayout.setBackground(drawable);
//        mylayout.setBackgroundColor(Color.CYAN);		//���ñ���ɫ
        mylayout.setStretchAllColumns(true);			//������Ļ
    	int number=mLeDevices.size();
    	Button Btn[] = new Button[number];				//��̬������ť
    	int i=0;
    	int j=0;
    	for(i=0;i<number;i++)
    	{
    		CDevice mLeDevice=mLeDevices.get(i);
    		Btn[i]=new Button(this);
    		Btn[i].setId(i);
    		Btn[i].setTextColor(Color.BLUE);		//����������ɫ
    		Btn[i].setText(mLeDevice.Getname());
    		Btn[i].setTypeface(Typeface.SERIF,Typeface.BOLD_ITALIC);
    		Btn[i].setTextSize(25);
    		Btn[i].setHeight(200);
    		//���õ�����Ӧ����
    		Btn[i].setOnClickListener(new OnClickListener() {
    			public void onClick(View v) {
    				// TODO Auto-generated method stub
    				id=v.getId();
    				CDevice mLeDevice=mLeDevices.get(id);
    				AlertDialog mydialoginstance = new AlertDialog.Builder(Delete_activity.this) //����Ĳ���ShowDialog_ActivityΪ����
                    //.setIcon(R.drawable.icon)//ͼ�꣬��ʾ�ڶԻ���������
                    .setTitle("ɾ������"+mLeDevice.Getname())  //�Ի������
                    .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() { //��ȷ������ť��Ӧ�Ĺ���
                    			public void onClick(DialogInterface dialog, int whichButton) {//�����Ƿ��е������ȷ����ť
                    				mLeDevices.remove(id);
                    				f.write(mLeDevices,filename);
                    				dialog.cancel();
                    				Delete_activity.this.finish();
                    				//Toast.makeText(getApplicationContext(), "��л����������Ϣ���ټ�", Toast.LENGTH_LONG).show();
                    			}})
                    .setNegativeButton("ȡ��", new DialogInterface.OnClickListener() { //����������ť��Ӧ�Ĺ���
                    			public void onClick(DialogInterface dialog, int whichButton) {  //�����Ƿ��е������ȡ����ť
                    				dialog.cancel();
                    			}})
                    		.create(); 
                    mydialoginstance.show(); //��ʾ�Ի��� 
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
    			if(i>=number)						//����
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
			AlertDialog mydialoginstance = new AlertDialog.Builder(Delete_activity.this) //����Ĳ���ShowDialog_ActivityΪ����
            //.setIcon(R.drawable.icon)//ͼ�꣬��ʾ�ڶԻ���������
            .setTitle("ɾ������"+mLeDevice.Getname())  //�Ի������
            .setCancelable(false)
            .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() { //��ȷ������ť��Ӧ�Ĺ���
            			public void onClick(DialogInterface dialog, int whichButton) {//�����Ƿ��е������ȷ����ť
            				mLeDevices.remove(id);
            				f.write(mLeDevices,filename);
            				dialog.cancel();
            				Delete_activity.this.finish();
            			}})
            .setNegativeButton("ȡ��", new DialogInterface.OnClickListener() { //����������ť��Ӧ�Ĺ���
            			public void onClick(DialogInterface dialog, int whichButton) {  //�����Ƿ��е������ȡ����ť
            				dialog.cancel();
            				Delete_activity.this.finish();
            			}})
            		.create(); 
            mydialoginstance.show(); //��ʾ�Ի���
    	}
    	setContentView(mylayout);//��ʾ�趨�Ĳ���mylayout
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
