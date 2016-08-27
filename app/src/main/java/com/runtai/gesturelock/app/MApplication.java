package com.runtai.gesturelock.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.runtai.gesturelock.lockpattern_gesture.GestureLoginActivity;
import com.runtai.gesturelock.lockpattern_utils.ACache;
import com.runtai.gesturelock.lockpattern_utils.Constant;
import com.runtai.gesturelock.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Application
 * // 过滤掉本activity 这个界面不用监听
 * // setVerify(false);
 */
public class MApplication extends Application {
	
	private ACache aCache;
	public static boolean hasPwd;
	/**自动锁屏时长*/
	private static int TIME = 1000 * 30;
	/**进入程序就开始倒计时锁屏(没有触碰屏幕情况下)*/
	private static boolean startIsLock = true;
	
	@Override
	public void onCreate() {
		super.onCreate();
		getDensity();
		register();
		init();
		if (startIsLock) {
			startVerify();
		}
	}
	
	private void init() {
		aCache = ACache.get(getApplicationContext());
		String password = aCache.getAsString(Constant.GESTURE_PASSWORD);
        if(password == null || "".equals(password)) {
        	//没有密码
        	hasPwd = false;
        } else {
        	hasPwd = true;
        }
	}

	public static  int width;
	public static  int height;
	public static  float density;
	public static  int densityDpi;
	
	/**
	 * 根据构造函数获得当前手机的屏幕系数
	 */
	public void getDensity() {
		// 获取当前屏幕
		DisplayMetrics dm = new DisplayMetrics();
		dm = getApplicationContext().getResources().getDisplayMetrics();

		width = dm.widthPixels;
		height = dm.heightPixels;
		density = dm.density;
		densityDpi = dm.densityDpi;
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
	
	// 上一次触碰时间
	long lastTimeMillis = 0;
	public void setLastTouchTime(){
		// 最新的触碰时间
		long currentTimeMillis = System.currentTimeMillis();
		if(lastTimeMillis == 0){
			// 第一次触碰
			lastTimeMillis = currentTimeMillis;
			// 开启监听
			startVerify();
		} else {
			//时间差
			long temp = currentTimeMillis - lastTimeMillis;
			// 如果时间差小于TIME，就先停掉前一次的监听，再重新开启
			if(temp < TIME){
				stopVerify();
				startVerify();
				lastTimeMillis = 0;/** 这一句的意思是两次触摸时间差小于TIME则重新开始计时 */
			} else { // else 如果大于,那么上一次的监听在运行着，5分钟之后自然会锁定
				Log.e("锁定时间", ""+System.currentTimeMillis()+"触摸之后大于" + (TIME / 1000) + "秒");
				lastTimeMillis = 0;
				stopVerify();
				verify();
			}
		}
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Log.e("锁定时间", ""+System.currentTimeMillis()+"解锁后" + (TIME / 1000) + "秒自然锁定");
			lastTimeMillis = 0;
			stopVerify();
			verify();
		}
	};
	
	/**
	 * 开启 验证手势密码界面
	 */
	public void verify() {
		//更新手势密码状态
		init();
		boolean isTopRunning = isRunningForeground(getApplicationContext());
		// 是否在前台运行
		if(isTopRunning){ 
			// 是否设置了密码
			if (hasPwd) {
				new SharedPreferencesUtils(getApplicationContext()).put("ISLOCK", true);//这里与忘记密码页面相呼应(判断是否是从自然锁屏情况下进入忘记密码页的)
				// 判断检测界面是否已经运行
				if(!GestureLoginActivity.IS_SHOW){
					Intent intent = new Intent();
					intent.setClass(getApplicationContext(), GestureLoginActivity.class);
					intent.putExtra(GestureLoginActivity.INTENT_MODE, GestureLoginActivity.GESTURE_MODE_VERIFY);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}
			}
		}
	}
	
	private static Timer timer ;
	private static TimerTask timerTask;
	private static boolean isRunning;
	
	/**
	 * TIME一次退出
	 */
	public void startVerify(){
		if(timer == null){
			timer = new Timer();
		}
		if(timerTask == null){
			timerTask = new TimerTask() {
				@Override
				public void run() {
					mHandler.sendEmptyMessage(0);
				}
			};
		}
		if(!isRunning){
			timer.schedule(timerTask, TIME, TIME);
			isRunning = true;
		}
	}
	
	/**
	 * 停止检测
	 */
	public static void stopVerify(){
		if(timer != null){
			timer.cancel();
			timer = null;
		}
		if(timerTask != null){
			timerTask.cancel();
			timerTask = null;
		}
		isRunning = false;
	}
	
	/**
	 * 是否在前台运行
	 */
	public boolean isRunningForeground(Context context) {
	    ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
	    ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
	    String currentPackageName = cn.getPackageName();
//	    ComponentInfo{com.android.systemui/com.android.systemui.recent.RecentsActivity}
//	    ComponentInfo{com.android.systemui/com.android.systemui.recent.RecentAppFxActivity}
		if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(getPackageName())) {
			return true;
		}
	    return false ;
	}
	
	/*************************************** Activity管理 *************************************/
	/**
	 * 正在运行的Activity
	 */
	public static List<Activity> runingActivities = new ArrayList<Activity>();

	/**
	 * 添加Activity
	 */
	public static void addActivity(Activity activity) {
		runingActivities.add(activity);
	}

	/**
	 * 移除Activity
	 */
	public static void removeActivity(Activity activity) {
		runingActivities.remove(activity);
	}

	/**
	 * 退出应用
	 */
	public static void exitAllActivity(Context context) {
		if (runingActivities != null) {
			for (int i = 0; i < runingActivities.size(); i++) {
				Activity item = runingActivities.get(i);
				item.finish();
				runingActivities.remove(item);
				i--;
			}
		}
		stopVerify();
		System.exit(0);
	}

	/**
	 * 清空activity栈,出了当前activity
	 */
	public static void clearAllActivity(Activity context) {
		for (int i = 0; i < runingActivities.size(); i++) {
			Activity item = runingActivities.get(i);
			if (context.getClass().getSimpleName()
					.equals(item.getClass().getSimpleName())) {
				continue;
			}
			item.finish();
			runingActivities.remove(item);
		}
	}
	
	
	
	
	public int count = 0;
	public void register(){
		registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityStopped(Activity activity) {
                Log.e("viclee", activity + "onActivityStopped");
                count--;
                if (count == 0) {
                    Log.e("viclee", ">>>>>>>>>>>>>>>>>>>切到后台  lifecycle");
                    new SharedPreferencesUtils(getApplicationContext()).put("GROUND", false);
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Log.e("viclee", activity + "onActivityStarted");
                if (count == 0) {
                    Log.e("viclee", ">>>>>>>>>>>>>>>>>>>切到前台  lifecycle");
                    new SharedPreferencesUtils(getApplicationContext()).put("GROUND", true);
                }
                count++;
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                Log.e("viclee", activity + "onActivitySaveInstanceState");
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.e("viclee", activity + "onActivityResumed");
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.e("viclee", activity + "onActivityPaused");
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.e("viclee", activity + "onActivityDestroyed");
                if ((""+activity).contains("MainActivity")) {
					Log.i("程序不正常退出", "程序不正常退出");
					new Thread(){
						@Override
						public void run() {
							// TODO Auto-generated method stub
							super.run();
							handler.sendEmptyMessage(0);
						}
					}.start();
					Log.i("onActivityDestroyed", "程序不正常退出");
					/** 这里竟然不起作用 */
					new SharedPreferencesUtils(getApplicationContext()).put("ISFRIST", false);
				}
            }

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Log.e("viclee", activity + "onActivityCreated");
            }
        });
	}

	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			new SharedPreferencesUtils(getApplicationContext()).put("123456", "123456");
			String ss = new SharedPreferencesUtils(getApplicationContext()).get("123456");
			Log.i("程序不正常退出执行", ss);
		}
	};

}
