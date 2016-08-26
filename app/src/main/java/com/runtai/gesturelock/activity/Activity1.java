package com.runtai.gesturelock.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.runtai.gesturelock.R;
import com.runtai.gesturelock.base.BaseActivity;

public class Activity1 extends BaseActivity {

	private TextView textview_1;

	@Override
	protected void beforeSetContent() {
		// TODO Auto-generated method stub

	};

	@Override
	protected int getView() {
		// TODO Auto-generated method stub
		return R.layout.activity_1;
	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.initView(savedInstanceState);
		textview_1 = (TextView) findViewById(R.id.textview_1);
		textview_1.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		super.onClick(view);

		switch (view.getId()) {
		case R.id.textview_1:
			skip(Activity2.class, false);
			break;

		default:
			break;
		}
	}
}
