package com.runtai.gesturelock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.runtai.gesturelock.R;
import com.runtai.gesturelock.base.BaseActivity;
import com.runtai.gesturelock.lockpattern_gesture.CreateGestureActivity;
import com.runtai.gesturelock.lockpattern_gesture.GestureLoginActivity;
import com.runtai.gesturelock.lockpattern_utils.ACache;
import com.runtai.gesturelock.lockpattern_utils.Constant;
import com.runtai.gesturelock.utils.SharedPreferencesUtils;

/**
 * 
 * @作者: 高炎鹏
 * @时间: 2016-8-9 下午5:06:43
 * @描述: 主菜单
 * 
 * 
 *  * 初始化时设置
 *   setVerify(false);
 *   过滤掉本activity 这个界面不用监听 *
 *   
 *   跳转界面需要用到skip  在这个方法中默认添加了页面跳转时不触发验证界面(而在熄屏、锁定后会默认跳入验证手势密码界面)
 *     @param clazz 要跳向的界面的class
 *     @param isCloseSelf 是否关闭本界面
 *     skip(Class<?> clazz, boolean isCloseSelf)
 * 
 */

public class MainActivity extends BaseActivity {
	
    private ACache aCache;
    private TextView textview;
    private Button button1, button2, button3, jump;
    
    private Intent intent;
	

	@Override
	protected void beforeSetContent() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected int getView() {
		// TODO Auto-generated method stub
		return R.layout.activity_main;
	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		super.initView(savedInstanceState);
		
//		// 过滤掉本activity 这个界面不用监听
//		setVerify(false);
		
		aCache = ACache.get(this);
		
		textview = (TextView) findViewById(R.id.textview);
		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		button3 = (Button) findViewById(R.id.button3);
		jump = getView(R.id.jump);
		init();
	}
	
	private void init() {
        String password = aCache.getAsString(Constant.GESTURE_PASSWORD);
        if(password == null || "".equals(password)) {
        	//没有密码
        	textview.setText("未设置");
        	button1.setVisibility(View.VISIBLE);
        	button2.setVisibility(View.GONE);
        	button3.setVisibility(View.GONE);
        } else {
        	//有密码
        	textview.setText("设置");
        	button1.setVisibility(View.GONE);
        	button2.setVisibility(View.VISIBLE);
        	button3.setVisibility(View.VISIBLE);
        }
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		super.setListener();
		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		button3.setOnClickListener(this);
		jump.setOnClickListener(this);
	}
	
	/** 验证手势密码时的请求码 */
	private int requestCode1 = 1;
	
	/** 创建手势密码时的请求码 */
	private int requestCode2 = 2;
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		super.onClick(view);
		switch (view.getId()) {
		
		/** 设置(创建)密码, 本页面不关闭 */
		case R.id.button1:
//			skip(CreateGestureActivity.class, false);
			intent = new Intent(this, CreateGestureActivity.class);
			startActivityForResult(intent, requestCode2);
			break;
			
			/** 修改密码 *//** 先验证手势密码，再进行相关操作 */
		case R.id.button2:
			new SharedPreferencesUtils(getApplicationContext()).put("GROUND", false);
			intent = new Intent(this, GestureLoginActivity.class);
			intent.putExtra("CODE", "222");
			startActivityForResult(intent, requestCode1);
			break;
			
			/** 清除密码 *//** 先验证手势密码，再进行相关操作 */
		case R.id.button3:
			new SharedPreferencesUtils(getApplicationContext()).put("GROUND", false);
			intent = new Intent(this, GestureLoginActivity.class);
			intent.putExtra("CODE", "111");
			startActivityForResult(intent, requestCode1);
			break;
			
		case R.id.jump:/** 跳转 */
			skip(Activity1.class, false);
			break;
			
		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode1 == requestCode) {
			switch (resultCode) {
			case 111://清除密码
				aCache.put(Constant.GESTURE_PASSWORD, "");
				break;
			case 222://修改密码
				skip(CreateGestureActivity.class, false);
				break;
//			case 333://返回键返回
//				
//				break;
			default:
				break;
			}
		} else if (requestCode2 == requestCode) {
			switch (resultCode) {
			case 2://创建密码
				Log.e("创建密码回跳", "手势密码创建成功");
				break;
			default:
				break;
			}
		}
		init();
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.i("onStart", "MainActivity");
		init();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.i("onStop", "MainActivity");
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.i("onPause", "MainActivity");
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i("onDestroy", "MainActivity");
	}
	
	private long exitTime = 0;
	/**
	 * // * 菜单、返回键响应 //
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 双击退出程序函数
	 */
	public void exit() {
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
		} else {
			new SharedPreferencesUtils(this).put("EXIT", true);
			MainActivity.this.finish();
			System.exit(0);
			//清除Token状态
		}
	}

}
