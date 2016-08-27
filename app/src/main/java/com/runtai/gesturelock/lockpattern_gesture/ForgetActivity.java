package com.runtai.gesturelock.lockpattern_gesture;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.runtai.gesturelock.R;
import com.runtai.gesturelock.base.BaseFragmentActivity;
import com.runtai.gesturelock.lockpattern_utils.ACache;
import com.runtai.gesturelock.lockpattern_utils.Constant;
import com.runtai.gesturelock.utils.SharedPreferencesUtils;

public class ForgetActivity extends BaseFragmentActivity {
	
	private EditText edittext;
	private Button proving;
	private String psd;
	
    private ACache aCache;
    
    @Override
    protected void doBeforeSetContent() {
    	// TODO Auto-generated method stub
    	super.doBeforeSetContent();
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
    
	@Override
	protected int getContentViewID() {
		// TODO Auto-generated method stub
		return R.layout.activity_forget;
	}
	
	@Override
	protected void initView(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.initView(savedInstanceState);
		aCache = ACache.get(ForgetActivity.this);
		edittext = (EditText) findViewById(R.id.edittext);
		proving = (Button) findViewById(R.id.proving);
		proving.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		super.onClick(view);
		switch (view.getId()) {
		case R.id.proving:
			proving();
			break;

		default:
			break;
		}
	}
	
	private void proving() {
		psd = edittext.getText().toString();
		if ("123456".equals(psd)) {
			/** 清除手势密码 */
			aCache.put(Constant.GESTURE_PASSWORD, "");
			Toast.makeText(this, "验证成功，清除手势密码！", Toast.LENGTH_SHORT).show();
			
			/** 判断是否是从后台切换到前台后忘记密码-->经过验证成功-->跳转到哪里 */
			boolean ground = new SharedPreferencesUtils(getApplicationContext()).get("GROUND", false);
	    	Log.e("ground", ""+ground);
	    	if (ground) { //是从后台切换到前台后忘记密码---一系列操作
	    		/** 返回到登陆主页面 */
				intent = new Intent();
				setResult(999, intent);
			} else {
				/** 返回到主页面 */
				intent = new Intent();
				setResult(888, intent);
			}
	    	finish();
		} else {
			Toast.makeText(this, "您输入的密码错误，请重新输入", Toast.LENGTH_SHORT).show();
		}
	}
	Intent intent;
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		boolean isbg = new SharedPreferencesUtils(getApplicationContext()).get("isbg", true);
    	Log.e("isbg", ""+isbg);
    	/** 忘记手势密码页面--->返回键返回事件 (根据在验证手势密码页面(判断是否是从后台切换到前台)跳转过来携带参数判断) */
    	if (isbg) {
    		Log.i("验证手势密码页面(是从后台切换到前台(不在程序中))跳转过来携带参数---->", "true");
    		Log.i("返回键返回", "跳转到验证手势密码界面");
    		Intent intent = new Intent();
    		intent.setClass(getApplicationContext(), GestureLoginActivity.class);
    		startActivity(intent);
		} else {
			Log.i("验证手势密码页面(不是从后台切换到前台(在程序中))跳转过来携带参数---->", "false");
    		Log.i("返回键返回", "正常返回");

			//这里增加判断(是否是从自然锁定状态下的手势密码跳转过来的)
			boolean lock = new SharedPreferencesUtils(getApplicationContext()).get("ISLOCK", false);
			if (lock) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), GestureLoginActivity.class);
				startActivity(intent);
				new SharedPreferencesUtils(getApplicationContext()).put("ISLOCK", false);
			}
		}
    	finish();
	}

}
