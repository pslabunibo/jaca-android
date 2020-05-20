package it.unibo.pslab.jaca_android.core;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;

import cartago.Artifact;
import cartago.ArtifactId;
import cartago.OperationException;
import it.unibo.pslab.jaca_android.MasService;

/**
 * Base class to extend in order to realize a JaCa-Android artifact.
 * This class provides the getApplicationContext that allow to getCurrentTraumaLeader access to
 * the standard Android application context.
 * Developers are recommended extend this class to develop their Android-based
 * artifacts.
 */
public abstract class JaCaArtifact extends Artifact{
	/**
	 * Return the Android application context
	 * 
	 * @return application context
	 */
	protected Context getApplicationContext(){
		return MasService.getInstance().getApplicationContext();
	}

	protected void executeOnActivity(String activityName, final Runnable task){
		MasService.getInstance().getRegisteredActivity(activityName).runOnUiThread(task);
	}

	protected Activity getActivity(String activityName){
		return MasService.getInstance().getRegisteredActivity(activityName);
	}
	
	protected void runOnMainLooper(final Runnable task){
		MasService.getInstance().runOnMainLooper(task);
	}

	/*
	 *
	 */

	protected void subscribeForActivityResults(String providerName, ArtifactId receiverId, String receiverOp){
        try {
            execLinkedOp(lookupArtifact(providerName),"registerArtifactForActivityResults", receiverId, receiverOp);
        } catch (OperationException e) {
            e.printStackTrace();
            System.out.println("ERROR: subscribeForActivityResults failed! > " + e.getMessage());
        }
    }

    protected void unsubscribeForActivityResults(String providerName, ArtifactId receiverId){
        try {
            execLinkedOp(lookupArtifact(providerName),"unregisterArtifactForActivityResults", receiverId);
        } catch (OperationException e) {
            e.printStackTrace();
        }
    }

	/*
     * SHARED PREFERENCES MANAGEMENT
     */
	protected void writeSharedPreference(String preferenceId, String key, String value){
		SharedPreferences preferences = getApplicationContext().getSharedPreferences(preferenceId, Context.MODE_PRIVATE);
		preferences.edit().putString(key, value).commit();
	}

	protected void writeSharedPreference(String preferenceId, String key, int value){
		SharedPreferences preferences = getApplicationContext().getSharedPreferences(preferenceId, Context.MODE_PRIVATE);
		preferences.edit().putInt(key, value).commit();
	}

	protected void writeSharedPreference(String preferenceId, String key, float value){
		SharedPreferences preferences = getApplicationContext().getSharedPreferences(preferenceId, Context.MODE_PRIVATE);
		preferences.edit().putFloat(key, value).commit();
	}

	protected void writeSharedPreference(String preferenceId, String key, long value){
		SharedPreferences preferences = getApplicationContext().getSharedPreferences(preferenceId, Context.MODE_PRIVATE);
		preferences.edit().putLong(key, value).commit();
	}

	protected void writeSharedPreference(String preferenceId, String key, Boolean value){
		SharedPreferences preferences = getApplicationContext().getSharedPreferences(preferenceId, Context.MODE_PRIVATE);
		preferences.edit().putBoolean(key, value).commit();
	}

	protected void writeSharedPreference(String preferenceId, String key, Set<String> value){
		SharedPreferences preferences = getApplicationContext().getSharedPreferences(preferenceId, Context.MODE_PRIVATE);
		preferences.edit().putStringSet(key, value).commit();
	}

	protected String readSharedPreference(String preferenceId, String key, String default_value){
		SharedPreferences preferences = getApplicationContext().getSharedPreferences(preferenceId, Context.MODE_PRIVATE);
		return preferences.getString(key, default_value);
	}

	protected int readSharedPreference(String preferenceId, String key, int default_value){
		SharedPreferences preferences = getApplicationContext().getSharedPreferences(preferenceId, Context.MODE_PRIVATE);
		return preferences.getInt(key, default_value);
	}

	protected float readSharedPreference(String preferenceId, String key, float default_value){
		SharedPreferences preferences = getApplicationContext().getSharedPreferences(preferenceId, Context.MODE_PRIVATE);
		return preferences.getFloat(key, default_value);
	}

	protected long readSharedPreference(String preferenceId, String key, long default_value){
		SharedPreferences preferences = getApplicationContext().getSharedPreferences(preferenceId, Context.MODE_PRIVATE);
		return preferences.getLong(key, default_value);
	}

	protected boolean readSharedPreference(String preferenceId, String key, boolean default_value){
		SharedPreferences preferences = getApplicationContext().getSharedPreferences(preferenceId, Context.MODE_PRIVATE);
		return preferences.getBoolean(key, default_value);
	}

	protected Set<String> readSharedPreference(String preferenceId, String key, Set<String> default_value){
		SharedPreferences preferences = getApplicationContext().getSharedPreferences(preferenceId, Context.MODE_PRIVATE);
		return preferences.getStringSet(key, default_value);
	}

	protected Map<String, ?> readSharedPreferences(String preferenceId){
		SharedPreferences preferences = getApplicationContext().getSharedPreferences(preferenceId, Context.MODE_PRIVATE);
		return preferences.getAll();
	}
}