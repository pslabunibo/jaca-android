package it.unibo.pslab.jaca_android.core;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class JaCaBaseActivity extends FragmentActivity implements JaCaActivity<Activity> {
	
	protected JaCaActivityBehaviorHandler mJaCaBehaviorHandler;

    protected volatile boolean backButtonEnabled;

	@SuppressLint("SourceLockedOrientationActivity")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if(getIntent().getExtras().getBoolean(JaCaActivity.LAYOUT_PORTRAIT_FIXED)){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

        if(getIntent().getExtras().getBoolean(JaCaActivity.LAYOUT_FULLSCREEN)){
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		
		super.onCreate(savedInstanceState);
		
		setContentView(getIntent().getExtras().getInt(JaCaActivity.LAYOUT_RESOURCE));

		mJaCaBehaviorHandler = new JaCaActivityBehaviorHandler(this,
                getIntent().getExtras().getString(ARTIFACT_NAME),
                getIntent().getExtras().getString(WSP_NAME));

		mJaCaBehaviorHandler.onCreate();

        backButtonEnabled = true;
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        int menu_resource = getIntent().getExtras().getInt(JaCaActivity.MENU_RESOURCE);

        if(menu_resource != 0) {
			getMenuInflater().inflate(menu_resource, menu);
		}

        mJaCaBehaviorHandler.onCreateOptionsMenu(menu);

        return true;
    }

	@Override
	protected void onStart() {
		mJaCaBehaviorHandler.onStart();
		super.onStart();
	}

	@Override
	protected void onResume() {
		mJaCaBehaviorHandler.onResume();
		super.onResume();
	}

	@Override
	protected void onPause() {
		mJaCaBehaviorHandler.onPause();
		super.onPause();
	}

	@Override
	protected void onRestart() {
		mJaCaBehaviorHandler.onRestart();
		super.onRestart();
	}

	@Override
	protected void onStop() {
		mJaCaBehaviorHandler.onStop();
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		mJaCaBehaviorHandler.onDestroy();
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		mJaCaBehaviorHandler.onBackPressed();

        if(backButtonEnabled) {
			super.onBackPressed();
		}
	}

	@Override
	public GenericGUIEventFetcher getEventsFetcher(){
		return mJaCaBehaviorHandler.getActivityEventsFetcher();
	}
	
	@Override
	public Activity getWrappedActivity() {
		return this;
	}
	
	/** Methods for handling the activity-related events */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mJaCaBehaviorHandler.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

    @Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		mJaCaBehaviorHandler.onOptionsItemSelected(item);
		return true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mJaCaBehaviorHandler.onTouchEvent(event);
		return true;
	}
	
	@Override
	public void onNewIntent(Intent intent) { 
		mJaCaBehaviorHandler.onNewIntent(intent);
		super.onNewIntent(intent);
	}

    public void disableBackButtonClickEffect() {
        backButtonEnabled = false;
    }

	public void enableBackButtonClickEffect() {
		backButtonEnabled = true;
	}
}