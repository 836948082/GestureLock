package com.runtai.gesturelock.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.runtai.gesturelock.utils.SharedPreferencesUtils;

/**
 * @作者: 高炎鹏
 * @时间: 2016-8-26 上午10:59:44
 * 
 * @描述: 携带手势密码监听
 * 		  BaseActivity extends BaseFragmentActivity(所有继承BaseActivity的页面都将携带手势密码监听)
 * 		   不携带手势密码监听
 * 		  BaseActivity extends FragmentActivity(所有继承BaseActivity的页面都不携带手势密码监听)
 * 		  << 需要把getContentViewID()方法注释掉 >>
 */
public abstract class BaseActivity extends BaseFragmentActivity implements OnClickListener {
	
	/**
	 * 继承 BaseFragmentActivity 携带密码时  isHasPwd = true
	 * 继承 FragmentActivity   不带密码时  isHasPwd = false
	 */
	private static boolean isHasPwd = true;
	protected BaseActivity CTX = BaseActivity.this;
	
	@Override
	protected void doBeforeSetContent() {
		// TODO Auto-generated method stub
		super.doBeforeSetContent();
		beforeSetContent();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	/**
	 * 继承 BaseFragmentActivity 需要重写的方法 (设置所有页面携带密码监听)
	 */
	@Override
	protected int getContentViewID() {
		// TODO Auto-generated method stub
		return getView();
	}
	
	
	
	
	/**------------------------------------------------------------------------------------------------------------*/
	/**------------------------------------------------------------------------------------------------------------*/
	/* 如果继承FragmentActivity不携带手势密码，注释掉以上部分代码，并修改isHadPwd的值  */
	/**------------------------------------------------------------------------------------------------------------*/
	/**------------------------------------------------------------------------------------------------------------*/
	
	
	
	/**
	 * 继承 FragmentActivity 设置所有页面(继承BaseActivity)不携带密码监听
	 */
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		if (!isHasPwd) {
			beforeSetContent();
		}
		setContentView(getView());
		init(arg0);
	}

	private void init(Bundle savedInstanceState) {
		getIntentInfo();
		initView(savedInstanceState);
		setListener();
		getDatas();
	}

	protected abstract int getView();
	protected abstract void beforeSetContent();

	/**
	 * 初始化所有控件
	 */
	protected void initView(Bundle savedInstanceState) {
	}

	/**
	 * 获取 意图 intent中的数据，如果不需要获取，则可以不做处理
	 */
	protected void getIntentInfo() {
	}

	/**
	 * 设置监听事件，如果不用设置，则不用设置
	 */
	protected void setListener() {
	}

	/**
	 * 获取数据,根据实际项目，放在onResume或者onPause中
	 */
	protected void getDatas() {
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
	}

	/**
	 * 封装Intent跳转
	 *
	 * @param clazz
	 *            要跳向的界面的class
	 * @param isCloseSelf
	 *            是否关闭本界面
	 */
	protected void skip(Class<?> clazz, boolean isCloseSelf) {
		Intent intent = new Intent(CTX, clazz);
		startActivity(intent);
		if (isCloseSelf) {
			CTX.finish();
		}
		new SharedPreferencesUtils(getApplicationContext()).put("GROUND", false);
	}

	/**
	 * 根据资源id 获取View ，不用强制转换
	 *
	 * @param id
	 *            资源id
	 * @return 返回id所指向的View
	 */
	protected <A extends View> A getView(int id) {
		return (A) findViewById(id);
	}

}
