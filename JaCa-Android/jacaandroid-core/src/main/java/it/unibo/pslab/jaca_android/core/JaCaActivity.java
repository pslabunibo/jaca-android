package it.unibo.pslab.jaca_android.core;

import android.app.Activity;

public interface JaCaActivity<T extends Activity>{
	
	String ARTIFACT_NAME = "ARTIFACT_NAME";
	String WSP_NAME = "WSP_NAME";
	String LAYOUT_RESOURCE = "LAYOUT_RESOURCE";
	String MENU_RESOURCE = "MENU_RESOURCE";
	String LAYOUT_PORTRAIT_FIXED = "LAYOUT_PORTRAIT_FIXED";
	String LAYOUT_FULLSCREEN = "LAYOUT_FULLSCREEN";

	GenericGUIEventFetcher getEventsFetcher();
	
	T getWrappedActivity();
}