package it.unibo.pslab.jaca_android.core;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import java.util.UUID;

import cartago.ArtifactId;
import cartago.CartagoException;
import cartago.Op;
import cartago.util.agent.CartagoBasicContext;
import it.unibo.pslab.jaca_android.MasService;

/**
 * Utility class containing the implementation of common lifecycle methods for activities
 * @author asanti
 */
public class JaCaActivityBehaviorHandler {

	public static final String SET_JACA_ACTIVITY_OP = "setJaCaActivity";
	public static final String QUIT_WORKSPACE = "quitWorkspace";

	protected Activity mWrappedActivity;
	
	protected ArtifactId mArtifactId;
	protected String mArtifactName;
	protected String mWspName;
	
	protected boolean mBound;
	
	protected GenericGUIEventFetcher mActivityEventFetcher;
	protected CartagoBasicContext mCtx;

	protected ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) { 
			mBound = false;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			/* setting the activity to the GUI artifact */
			notifyActivityCreationToGUIArtifact();
			mBound = true;
		}
	};

	public JaCaActivityBehaviorHandler(Activity activity, String artName, String wspName){
		mWrappedActivity = activity;
		mArtifactName = artName;
		mWspName = wspName;
	}
	
	public void onCreate(){
		mBound = false;
		mActivityEventFetcher = new GenericGUIEventFetcher(mWrappedActivity);
		mCtx = new CartagoBasicContext(UUID.randomUUID().toString(), mWspName);
		/* Registration to the JaCaService */
		try {
			mWrappedActivity.bindService(new Intent(mWrappedActivity, MasService.class), mConnection, 0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void onStart() {
		mActivityEventFetcher.putEvent(new EventOpInfo(mWrappedActivity, GenericGUIEventFetcher.ON_START));
	}

	public void onResume() {
		mActivityEventFetcher.putEvent(new EventOpInfo(mWrappedActivity, GenericGUIEventFetcher.ON_RESUME));
	}

	public void onPause() {
		mActivityEventFetcher.putEvent(new EventOpInfo(mWrappedActivity, GenericGUIEventFetcher.ON_PAUSE));
	}

	public void onRestart() {
		mActivityEventFetcher.putEvent(new EventOpInfo(mWrappedActivity, GenericGUIEventFetcher.ON_RESTART));
	}

	public void onStop() {
		mActivityEventFetcher.putEvent(new EventOpInfo(mWrappedActivity, GenericGUIEventFetcher.ON_STOP));
	}
	
	public void onDestroy() {
		mActivityEventFetcher.putEvent(new EventOpInfo(this, GenericGUIEventFetcher.ON_DESTROY));
		try{
			if (mBound){
				/* De-registration to the JaCaService */
				mWrappedActivity.unbindService(mConnection);
			}
			if (mCtx!=null) {
				if (mArtifactId!=null) {
					mCtx.disposeArtifact(mArtifactId);
				}
				Op op = new Op(QUIT_WORKSPACE);
				mCtx.doAction(op);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public GenericGUIEventFetcher getActivityEventsFetcher(){
		return mActivityEventFetcher;
	}
	
	public Activity getActivity() {
		return mWrappedActivity;
	}
	
	public CartagoBasicContext getCartagoContext(){
		return mCtx;
	}

    public void onBackPressed() {
        mActivityEventFetcher.putEvent(new EventOpInfo(mWrappedActivity, GenericGUIEventFetcher.ON_BACK_PRESSED));
    }

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		mActivityEventFetcher.putEvent(new EventOpInfo(mWrappedActivity, GenericGUIEventFetcher.ON_ACTIVITY_RESULT, requestCode, resultCode, data));
	}
	
	public void onNewIntent(Intent intent) {
		mActivityEventFetcher.putEvent(new EventOpInfo(mWrappedActivity, GenericGUIEventFetcher.ON_NEW_INTENT, intent));
	}

	public void onCreateOptionsMenu(final Menu menu){
		mActivityEventFetcher.putEvent(new EventOpInfo(mWrappedActivity, GenericGUIEventFetcher.ON_CREATE_OPTIONS_MENU, menu) );
	}
	
	public void onOptionsItemSelected(final MenuItem item) {
		mActivityEventFetcher.putEvent(new EventOpInfo(mWrappedActivity, GenericGUIEventFetcher.ON_OPTIONS_ITEMS_SELECTED, item));
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		if(mActivityEventFetcher.isGestureDetectorInstalled()){
			if(!mActivityEventFetcher.getGestureDetector().onTouchEvent(event)){
				mActivityEventFetcher.putEvent(new EventOpInfo(mWrappedActivity, GenericGUIEventFetcher.ON_TOUCH_EVENT, event));
			}
		} else {
			mActivityEventFetcher.putEvent(new EventOpInfo(mWrappedActivity, GenericGUIEventFetcher.ON_TOUCH_EVENT, event));
		}
		return true;
	}

	/**
	 * Notify the creation of the JaCactivity to the artifact wrapping it
	 */
	private void notifyActivityCreationToGUIArtifact(){
		try {
            mArtifactId = mCtx.lookupArtifact(mArtifactName);
			mCtx.doAction(mArtifactId, new Op(SET_JACA_ACTIVITY_OP, mArtifactName, mWrappedActivity));
		} catch (CartagoException e) {
            Log.e("JaCa","Unable to lookup artifact " + mArtifactName);
        }
	}
}