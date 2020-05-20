package it.unibo.pslab.jaca_android;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

import cartago.CartagoService;
import it.unibo.pslab.jaca_android.core.JaCaBindingRole;
import it.unibo.pslab.jaca_android.core.RunCentralisedMAS;
import it.unibo.pslab.jaca_android.core.Utils;

/**
 * Android Service providing functioning as a container
 * for running JaCa application on Android
 */
public class MasService extends Service {

	private static MasService mInstance;
	
	protected boolean mRunning;
	protected int mBoundClients;

    protected ConcurrentHashMap<String,Activity> registeredActivities;
	
	public MasService(){
		registeredActivities = new ConcurrentHashMap<>();
	}

	public static MasService getInstance(){
		return mInstance;
	}

	public void registerActivity(String activityName, Activity act){
		registeredActivities.put(activityName, act);
	}

	public Activity getRegisteredActivity(String activityName){
		return registeredActivities.get(activityName);
	}
	
	public void runOnMainLooper(final Runnable task){
		new Handler(Looper.getMainLooper()).post(task);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();

		mInstance = this;
		mRunning = false;

        CartagoService.setDefaultInfrastructureLayer("android");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (!mRunning) {
			runMAS(intent);
		}

        return START_REDELIVER_INTENT;
	}

    @Override
    public void onDestroy() {
        super.onDestroy();

        terminateMAS();
    }

	@Override
	public IBinder onBind(Intent intent) {
		mBoundClients++;

		if (!mRunning) {
			runMAS(intent);
		}

		return JaCaBindingRole.getInstance().getCartagoNode(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		mBoundClients--;

		if(mBoundClients == 0){
			stopSelf();
		}

		return super.onUnbind(intent);
	}

	private void runMAS(Intent intent){
		final String mas2j = intent.getStringExtra(Utils.MAS2J);
		
		if(mas2j != null){
			URL url = MasService.class.getResource(mas2j);

			if(url != null){
				Log.i(Utils.JCM4ANDROID, "=== MAS ================================================================");
				Log.i(Utils.JCM4ANDROID, "=== running mas2j " + url.toString());
				Log.i(Utils.JCM4ANDROID, "========================================================================");

				try {
					RunCentralisedMAS.main(new String[]{url.toString()});
				} catch (Exception ex){
					ex.printStackTrace();
				}

				mRunning = true;
			}
		} else{
			throw new RuntimeException(ErrorMessage.INVALID_MAS2J_PARAMETER);
		}
	}

	private void terminateMAS(){
        RunCentralisedMAS.getRunner().finish();

        mInstance = null;
        mRunning = false;
    }
}