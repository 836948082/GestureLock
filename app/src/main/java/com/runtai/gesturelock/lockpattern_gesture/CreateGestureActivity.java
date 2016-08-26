package com.runtai.gesturelock.lockpattern_gesture;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.runtai.gesturelock.R;
import com.runtai.gesturelock.base.BaseFragmentActivity;
import com.runtai.gesturelock.lockpattern_utils.ACache;
import com.runtai.gesturelock.lockpattern_utils.Constant;
import com.runtai.gesturelock.lockpattern_view.LockPatternUtil;
import com.runtai.gesturelock.lockpattern_view.view.LockPatternIndicator;
import com.runtai.gesturelock.lockpattern_view.view.LockPatternView;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建手势密码
 */
public class CreateGestureActivity extends BaseFragmentActivity implements OnClickListener{

	LockPatternIndicator lockPatternIndicator;
	LockPatternView lockPatternView;
	Button resetBtn;
	TextView messageTv;
	Intent intent;

	private List<LockPatternView.Cell> mChosenPattern = null;
	private ACache aCache;
	private static final long DELAYTIME = 600L;
	@SuppressWarnings("unused")
	private static final String TAG = "CreateGestureActivity";
	
	@Override
    protected void doBeforeSetContent() {
    	// TODO Auto-generated method stub
    	super.doBeforeSetContent();
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
    
	@Override
	protected int getContentViewID() {
		// TODO Auto-generated method stub
		return R.layout.activity_create_gesture;
	}
	
	@Override
	protected void initView(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.initView(savedInstanceState);
		lockPatternIndicator = (LockPatternIndicator) findViewById(R.id.lockPatterIndicator);
		lockPatternView = (LockPatternView) findViewById(R.id.lockPatternView);
		resetBtn = (Button) findViewById(R.id.resetBtn);
		resetBtn.setOnClickListener(this);
		messageTv = (TextView) findViewById(R.id.messageTv);
		init();
	}

	private void init() {
		aCache = ACache.get(CreateGestureActivity.this);
		lockPatternView.setOnPatternListener(patternListener);
	}

	/**
	 * 手势监听
	 */
	private LockPatternView.OnPatternListener patternListener = new LockPatternView.OnPatternListener() {

		@Override
		public void onPatternStart() {
			lockPatternView.removePostClearPatternRunnable();
			//updateStatus(Status.DEFAULT, null);
			lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
		}

		@Override
		public void onPatternComplete(List<LockPatternView.Cell> pattern) {
			//Log.e(TAG, "--onPatternDetected--");
			if(mChosenPattern == null && pattern.size() >= 4) {
				mChosenPattern = new ArrayList<LockPatternView.Cell>(pattern);
				updateStatus(Status.CORRECT, pattern);
			} else if (mChosenPattern == null && pattern.size() < 4) {
				updateStatus(Status.LESSERROR, pattern);
			} else if (mChosenPattern != null) {
				if (mChosenPattern.equals(pattern)) {
					updateStatus(Status.CONFIRMCORRECT, pattern);
				} else {
					updateStatus(Status.CONFIRMERROR, pattern);
				}
			}
		}
	};

	/**
	 * 更新状态
	 * @param status
	 * @param pattern
     */
	private void updateStatus(Status status, List<LockPatternView.Cell> pattern) {
		messageTv.setTextColor(getResources().getColor(status.colorId));
		messageTv.setText(status.strId);
		switch (status) {
			case DEFAULT://重新绘制
				lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
				break;
			case CORRECT://第一次绘制
				updateLockPatternIndicator();
				lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
				break;
			case LESSERROR://第一次绘制(长度小于4)
				lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
				break;
			case CONFIRMERROR://第二次绘制与第一次不一致
				lockPatternView.setPattern(LockPatternView.DisplayMode.ERROR);
				// 延迟清除九宫格绘制图案
				lockPatternView.postClearPatternRunnable(DELAYTIME);
				break;
			case CONFIRMCORRECT://第二次绘制与第一次一致
				saveChosenPattern(pattern);
				lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
				setLockPatternSuccess();
				break;
		}
	}

	/**
	 * 更新 Indicator (略缩图)
	 */
	private void updateLockPatternIndicator() {
		if (mChosenPattern == null) {
			return;
		}
		lockPatternIndicator.setIndicator(mChosenPattern);
	}

	/**
	 * 重新设置手势
	 */
	private void resetLockPattern() {
		mChosenPattern = null;
		lockPatternIndicator.setDefaultIndicator();
		updateStatus(Status.DEFAULT, null);
	}

	/**
	 * 成功设置了手势密码(跳到首页)
     */
	private void setLockPatternSuccess() {
		Toast.makeText(this, "手势密码创建成功", Toast.LENGTH_SHORT).show();
    	intent = new Intent();
		setResult(2, intent);
		finish();
	}

	/**
	 * 保存手势密码
	 */
	private void saveChosenPattern(List<LockPatternView.Cell> cells) {
		byte[] bytes = LockPatternUtil.patternToHash(cells);
		aCache.put(Constant.GESTURE_PASSWORD, bytes);
	}

	private enum Status {
		//默认的状态，刚开始的时候（初始化状态）
		DEFAULT(R.string.create_gesture_default, R.color.grey_a5a5a5),
		//第一次记录成功
		CORRECT(R.string.create_gesture_correct, R.color.grey_a5a5a5),
		//连接的点数小于4（二次确认的时候就不再提示连接的点数小于4，而是提示确认错误）
		LESSERROR(R.string.create_gesture_less_error, R.color.red_f4333c),
		//二次确认错误
		CONFIRMERROR(R.string.create_gesture_confirm_error, R.color.red_f4333c),
		//二次确认正确
		CONFIRMCORRECT(R.string.create_gesture_confirm_correct, R.color.grey_a5a5a5);

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
		case R.id.resetBtn:
			resetLockPattern();
			break;
		default:
			break;
		}
	}

}
