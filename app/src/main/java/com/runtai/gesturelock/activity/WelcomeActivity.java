package com.runtai.gesturelock.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.runtai.gesturelock.R;
import com.runtai.gesturelock.lockpattern_utils.ACache;
import com.runtai.gesturelock.lockpattern_utils.Constant;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @作者：高炎鹏
 * @类描述：欢迎界面
 */
public class WelcomeActivity extends Activity {

	private ACache aCache;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_welcome);
		aCache = ACache.get(getApplicationContext());
		init();
	}

	public void init() {
		// 闪屏页面2秒后跳转
		final String password = aCache.getAsString(Constant.GESTURE_PASSWORD);
		final Intent it = new Intent(WelcomeActivity.this, DengLuActivity.class); // 你要转向的Activity
		final Intent intent = new Intent(WelcomeActivity.this, MainActivity.class); // 你要转向的Activity
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if (password == null || "".equals(password)) {
					// 没有密码
					startActivity(it);
				} else {
					startActivity(intent);
				}
				WelcomeActivity.this.finish();
			}
		};
		timer.schedule(task, 1000 * 2); // 2秒后
	}
}