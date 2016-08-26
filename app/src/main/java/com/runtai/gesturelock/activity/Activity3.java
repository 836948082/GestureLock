package com.runtai.gesturelock.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.runtai.gesturelock.R;
import com.runtai.gesturelock.base.BaseActivity;

public class Activity3 extends BaseActivity {

	private TextView textview_3;
	@Override
	protected int getView() {
		// TODO Auto-generated method stub
		return R.layout.activity_3;
	}
	
	@Override
	protected void initView(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.initView(savedInstanceState);
		textview_3 = (TextView) findViewById(R.id.textview_3);
		textview_3.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		super.onClick(view);
		textview_3.setText("变身");
	}

	@Override
	protected void beforeSetContent() {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

}
