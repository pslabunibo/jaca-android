package it.unibo.pslab.jaca_android.core;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.HashMap;

import cartago.infrastructure.android.ICartagoNodeRemote;
import it.unibo.pslab.jaca_android.MasService;

/**
 * 
 * @author mguidi, asanti
 *
 */
public class JaCaBindingRole {

	private static final String INFRA_ALREADY_INSTALLED_ERR = "Cartago Infrastructure already installed for address ";
	private static JaCaBindingRole mInstance;
	private HashMap<String, ICartagoNodeRemote.Stub> mMap = new HashMap<String, ICartagoNodeRemote.Stub>();
	private final DefaultBinder mDefaultBinder = new DefaultBinder();
	
	public static JaCaBindingRole getInstance() {
		if (mInstance == null) mInstance = new JaCaBindingRole();
		return mInstance;
	}
	
	/**
	 * Return the Android CartagoNode corresponding to the intent 
	 * provided in input
	 * @param intent
	 * @return
	 */
	public IBinder getCartagoNode(Intent intent) {
		String action = intent.getAction();
		/*
		 * If action != means that is requested a
		 * remote android node where 
		 * node address = action in the intent
		 */
		if (action != null) {
			ICartagoNodeRemote.Stub cartagoNodeRemote = mMap.get(action);
			if (cartagoNodeRemote!=null) {
				return cartagoNodeRemote;
			}
		}	
		return mDefaultBinder;
		
	}
	
	public void removeCartaNodeRemote(String address) {
		mMap.remove(address);
	}
	
	/**
	 * Store the node input parameter as the Android remote CArtAgO node
	 * for the address provided in input
	 * @param address
	 * @param node
	 */
	public void addCartagoNodeRemote(String address, ICartagoNodeRemote.Stub node) {
		ICartagoNodeRemote.Stub value = mMap.get(address);
		if (value == null) {
			mMap.put(address, node);
		} else {
			Log.v(Utils.JCM4ANDROID, INFRA_ALREADY_INSTALLED_ERR + address);
		}
	}
	
	class DefaultBinder extends Binder {
		public MasService getService() {
			return MasService.getInstance();
		}
	}
}