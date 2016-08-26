package com.runtai.gesturelock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.runtai.gesturelock.R;
import com.runtai.gesturelock.base.BaseActivity;


/**
 * @作者：陈振
 * @时间：2016-1-6下午2:41:24
 * @类描述：登录activity
 * @版本号：
 * @修改人：
 * @修改地址
 */
public class DengLuActivity extends BaseActivity {

	private Button denglu;

	@Override
	protected void beforeSetContent() {
		// TODO Auto-generated method stub

	}

	@Override
	protected int getView() {
		// TODO Auto-generated method stub
		return R.layout.me_denglu;
	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.initView(savedInstanceState);
		denglu = getView(R.id.denglu);
		denglu.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		super.onClick(view);
		switch (view.getId()) {
		case R.id.denglu:
			Intent intent = new Intent(DengLuActivity.this, MainActivity.class);
			startActivity(intent);
			// 因MainActivity的launchMode模式是singleTask，所以登陆成功跳转后MyDengLu.this不能finish
			DengLuActivity.this.finish();
			break;

		default:
			break;
		}
	}

	/**
	 * // * 菜单、返回键响应 //
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			System.exit(0);
			android.os.Process.killProcess(android.os.Process.myPid());
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
