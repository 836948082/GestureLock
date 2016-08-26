package com.runtai.gesturelock.lockpattern_gesture;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.runtai.gesturelock.R;
import com.runtai.gesturelock.activity.DengLuActivity;
import com.runtai.gesturelock.base.BaseFragmentActivity;
import com.runtai.gesturelock.lockpattern_utils.ACache;
import com.runtai.gesturelock.lockpattern_utils.Constant;
import com.runtai.gesturelock.lockpattern_view.LockPatternUtil;
import com.runtai.gesturelock.lockpattern_view.view.LockPatternView;
import com.runtai.gesturelock.utils.SharedPreferencesUtils;

import java.util.List;

/**
 * 验证手势密码(登陆)
 */
public class GestureLoginActivity extends BaseFragmentActivity {
	
	public final static String INTENT_MODE= "mode";
	public final static int GESTURE_MODE_VERIFY = 3;// 验证
	public static boolean IS_SHOW = false;// 是否运行了
	
    @SuppressWarnings("unused")
	private static final String TAG = "LoginGestureActivity";

    LockPatternView lockPatternView;
    TextView messageTv;
    Button forgetGestureBtn;

    private ACache aCache;
    private static final long DELAYTIME = 600l;
    private byte[] gesturePassword;
    
    @Override
    protected void doBeforeSetContent() {
    	// TODO Auto-generated method stub
    	super.doBeforeSetContent();
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
    
	@Override
	protected int getContentViewID() {
		// TODO Auto-generated method stub
		return R.layout.activity_gesture_login;
	}
	
	@SuppressWarnings("static-access")
	@Override
	protected void initView(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.initView(savedInstanceState);
		lockPatternView = (LockPatternView) findViewById(R.id.lockPatternView);
        messageTv = (TextView) findViewById(R.id.messageTv);
        forgetGestureBtn = (Button) findViewById(R.id.forgetGestureBtn);
        
        IS_SHOW = true;
		setVerify(false);
		mApplication.stopVerify();
        init();
	}
	
	@Override
	protected void onDestroy() {
		IS_SHOW = false;
		mApplication.startVerify();
		super.onDestroy();
	}
	
	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		super.setListener();
		forgetGestureBtn.setOnClickListener(this);
	}
	
	int MODE;
	
	@Override
	protected void getIntentInfo() {
		// TODO Auto-generated method stub
		super.getIntentInfo();
		intent = getIntent();
    	String codecode = intent.getStringExtra("CODE");
    	if (!"".equals(codecode) && null != codecode) {
    		CODE = Integer.parseInt(intent.getStringExtra("CODE"));
		}
    	MODE = getIntent().getIntExtra(INTENT_MODE, -1);
    	Log.e("MODE", ""+MODE);
//    	if(MODE == -1){
//    		throw new RuntimeException("请输入跳转模式");
//    	}
	}

    private Intent intent;
    private int CODE = -1;
    
	private void init() {
        aCache = ACache.get(GestureLoginActivity.this);
        //得到当前用户的手势密码
        gesturePassword = aCache.getAsBinary(Constant.GESTURE_PASSWORD);
        lockPatternView.setOnPatternListener(patternListener);
        updateStatus(Status.DEFAULT);
    }

    private LockPatternView.OnPatternListener patternListener = new LockPatternView.OnPatternListener() {

        @Override
        public void onPatternStart() {
            lockPatternView.removePostClearPatternRunnable();
        }

        @Override
        public void onPatternComplete(List<LockPatternView.Cell> pattern) {
            if(pattern != null){
                if(LockPatternUtil.checkPattern(pattern, gesturePassword)) {
                    updateStatus(Status.CORRECT);
                } else {
                    updateStatus(Status.ERROR);
                }
            }
        }
    };

    /**
     * 更新状态
     * @param status
     */
    private void updateStatus(Status status) {
        messageTv.setText(status.strId);
        messageTv.setTextColor(getResources().getColor(status.colorId));
        switch (status) {
            case DEFAULT:
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                break;
            case ERROR:
                lockPatternView.setPattern(LockPatternView.DisplayMode.ERROR);
                lockPatternView.postClearPatternRunnable(DELAYTIME);
                break;
            case CORRECT:
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                loginGestureSuccess();
                break;
        }
    }

    /**
     * 手势验证成功
     */
    private void loginGestureSuccess() {
    	if (CODE == -1) { //增加判断是否是登陆状态
    		Toast.makeText(GestureLoginActivity.this, "手势密码验证成功，跳转中", Toast.LENGTH_SHORT).show();
//    		Intent intent = new Intent(GestureLoginActivity.this, MainActivity.class);
//    		startActivity(intent);
        } else if (CODE == 111) {//清除密码
        	intent = new Intent();
    		setResult(111, intent);
        } else if (CODE == 222) {//修改密码
        	intent = new Intent();
    		setResult(222, intent);
        }
    	this.finish();
    }

    /**
     * 忘记手势密码
     */
    private void forgetGesturePasswrod() {
    	//**这里添加跳转时携带的数据，进行判断 111、222---->指的是在修改密码或清除密码时的状态
    	boolean ground = new SharedPreferencesUtils(getApplicationContext()).get("GROUND", false);
    	Log.e("ground", ""+ground);
    	if (ground) {
    		isbg = true;
    		Log.i("借助后台boolean", ""+isbg);
		}
    	new SharedPreferencesUtils(getApplicationContext()).put("isbackground", isbackground);
    	new SharedPreferencesUtils(getApplicationContext()).put("isbg", isbg);
        Intent intent = new Intent(GestureLoginActivity.this, ForgetActivity.class);
        startActivityForResult(intent, requestCode);
    }
    
    private int requestCode = 88;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	super.onActivityResult(requestCode, resultCode, data);
    	if (this.requestCode == requestCode) {
    		switch (resultCode) {
			case 888:
				Log.e("忘记手势密码界面", "关闭");
				break;
			case 999:
				intent = new Intent(this, DengLuActivity.class);
		        startActivity(intent);
				Log.e("忘记手势密码界面", "关闭");
				break;
			default:
				break;
			}
    		GestureLoginActivity.this.finish();
    	}
    }

    
    private enum Status {
        //默认的状态
        DEFAULT(R.string.gesture_default, R.color.grey_a5a5a5),
        //密码输入错误
        ERROR(R.string.gesture_error, R.color.red_f4333c),
        //密码输入正确
        CORRECT(R.string.gesture_correct, R.color.grey_a5a5a5);

        private Status(int strId, int colorId) {
            this.strId = strId;
            this.colorId = colorId;
        }
        private int strId;
        private int colorId;
    }

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.forgetGestureBtn:
			forgetGesturePasswrod();
			break;
		default:
			break;
		}
	}
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if(keyCode == KeyEvent.KEYCODE_BACK){
//			if (MODE == GESTURE_MODE_VERIFY) {
//				Log.e("相等了", ""+MODE);
//				System.exit(0);
//			} else {
//				Log.e("不相等", ""+MODE);
//			}
//			GestureLoginActivity.this.finish();
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}
	
	@Override
	public void onBackPressed() {
		if (MODE == GESTURE_MODE_VERIFY) {
			overHome();
			// 是刚进入程序后、等待超时的验证密码状态
			Log.e("相等", "" + MODE);
			isbackground = true;
		} else {
			// 是在程序中主动验证的状态(清除、修改手势密码)
			Log.e("不相等", "" + MODE);
			boolean ground = new SharedPreferencesUtils(getApplicationContext()).get("GROUND", false);
			if (ground) {
				/** 判断是否是从后台切换到前台 */
				if (!GestureLoginActivity.IS_SHOW) {
					Intent intent = new Intent();
					intent.setClass(getApplicationContext(), GestureLoginActivity.class);
					intent.putExtra(GestureLoginActivity.INTENT_MODE, GestureLoginActivity.GESTURE_MODE_VERIFY);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}
				Log.e("主动----是从后台切换到前台", "跳出验证密码");
				overHome();
				isbackground = true;
			} else {
				Log.e("不是从后台切换到前台", "不用跳出验证密码");
				isbackground = false;
				if (isbg) {
					/** 验证密码时，点击忘记密码，再返回到验证密码 */
					overHome();
					Log.e("是从后台切换到前台后(验证密码)--->忘记密码--->返回--->(验证密码)", "验证密码页面--->返回键相应");
					Log.e("相应", "实现Home键功能");
				} else {
					Log.e("不是从后台切换到前台后(验证密码)--->忘记密码--->返回--->(验证密码)", "验证密码页面--->返回键相应");
					Log.e("简单返回", "没有其他操作");
				}
			}
		}
		super.onBackPressed();
	}
	
	private boolean isbackground = false;
	
	/** 实现Home键功能 */
	private void overHome() {
		Intent intent= new Intent(Intent.ACTION_MAIN); 
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		intent.addCategory(Intent.CATEGORY_HOME); 
        startActivity(intent);
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		isbg = new SharedPreferencesUtils(getApplicationContext()).get("isbackground", false);
		Log.i("isbg", ""+isbg);
	}
	
	static boolean isbg;
	
}
