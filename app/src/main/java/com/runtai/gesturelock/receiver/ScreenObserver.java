package com.runtai.gesturelock.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.util.Log;

import com.runtai.gesturelock.utils.SharedPreferencesUtils;

import java.lang.reflect.Method;

/**
 * 屏幕锁屏或变暗
 */
public class ScreenObserver {
	private static String TAG = "ScreenObserver";
	private Context mContext;
	private ScreenBroadcastReceiver mScreenReceiver;
	private ScreenStateListener mScreenStateListener;
	private static Method mReflectScreenState;

	public ScreenObserver(Context context) {
		mContext = context;
		mScreenReceiver = new ScreenBroadcastReceiver();
		try {
			mReflectScreenState = PowerManager.class.getMethod("isScreenOn",
					new Class[] {});
		} catch (NoSuchMethodException nsme) {
			Log.d(TAG, "API < 7," + nsme);
		}
	}

	/**
	 * screen状态广播接收者
	 */
	private class ScreenBroadcastReceiver extends BroadcastReceiver {
		private String action = null;

		@Override
		public void onReceive(Context context, Intent intent) {
			action = intent.getAction();
			if (Intent.ACTION_SCREEN_ON.equals(action)) {
				mScreenStateListener.onScreenStateChange(true);
			} else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				mScreenStateListener.onScreenStateChange(false);
			}
		}
	}

	/**
	 * 请求screen状态更新
	 */
	public void requestScreenStateUpdate(ScreenStateListener listener, Context context) {
		mScreenStateListener = listener;
		startScreenBroadcastReceiver();
		boolean isfirst = new SharedPreferencesUtils(context).get("ISFRIST", true);//不正常退出
		if (!isfirst) {
			Log.i("不正常退出后再次启动程序", "执行这里ISFRIST");
			firstGetScreenState();
			new SharedPreferencesUtils(context).put("ISFRIST", true);
		}
		boolean exit = new SharedPreferencesUtils(context).get("EXIT", false);//正常退出
		if (exit) {
			Log.i("正常退出后再次启动程序", "执行这里EXIT");
			firstGetScreenState();
			new SharedPreferencesUtils(context).put("EXIT", false);
		}
	}
	
	/**
	 * 第一次请求screen状态
	 */
	private void firstGetScreenState() {
		PowerManager manager = (PowerManager) mContext
				.getSystemService(Activity.POWER_SERVICE);
		if (isScreenOn(manager)) {
			if (mScreenStateListener != null) {
				mScreenStateListener.onScreenStateChange(true);
			}
		} else {
			if (mScreenStateListener != null) {
				mScreenStateListener.onScreenStateChange(false);
			}
		}
	}

	/**
	 * 停止screen状态更新
	 */
	public void stopScreenStateUpdate() {
		mContext.unregisterReceiver(mScreenReceiver);
	}

	/**
	 * 启动screen状态广播接收器
	 */
	private void startScreenBroadcastReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		mContext.registerReceiver(mScreenReceiver, filter);
	}

	/**
	 * screen是否打开状态
	 */
	private static boolean isScreenOn(PowerManager pm) {
		boolean screenState;
		try {
			screenState = (Boolean) mReflectScreenState.invoke(pm);
		} catch (Exception e) {
			screenState = false;
		}
		return screenState;
	}

	public interface ScreenStateListener {
		public void onScreenStateChange(boolean isScreenOn);
	}
}

//// 另外一种判断屏幕关闭的方法：
//public final static boolean isScreenLocked(Context c) {
//	android.app.KeyguardManager mKeyguardManager = (KeyguardManager) c
//			.getSystemService(c.KEYGUARD_SERVICE);
//	return !mKeyguardManager.inKeyguardRestrictedInputMode();
//}