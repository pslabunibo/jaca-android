package it.unibo.pslab.jaca_android.core;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;

import cartago.ArtifactId;
import cartago.GUARD;
import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import cartago.ObsProperty;
import cartago.OperationException;
import it.unibo.pslab.jaca_android.MasService;

public class ActivityArtifact extends JaCaArtifact {
	public static final String UI_READY = "ui_ready";
    public static final String STATE = "ui_state";

	private static final String FETCH_GUI_EVENTS_OP = "fetchGUIEvents";

	private HashMap<Object, HashMap<String, String>> mEvToOpLinks;
	private boolean mStopped;
	private JaCaBaseActivity wrappedActivity;
	private GenericGUIEventFetcher eventFetcher;

    private HashMap<ArtifactId, String> registeredArtifactsForResultsMap = new HashMap<>();

	private int activity_layout_resource;
    private int activity_menu_resource;
	private boolean activity_fullscreen;
	private boolean activity_portrait_fixed;

	public void init(Class<?> activity, int layout_resource, int menu_resource, boolean portrait, boolean fullscreen) {
		this.activity_layout_resource = layout_resource;
        this.activity_menu_resource = menu_resource;
		this.activity_fullscreen = fullscreen;
		this.activity_portrait_fixed = portrait;
		
		doBaseInit(activity.getName(), new Intent());

		execInternalOp("internalSetup");
	}

    public void init(Class<?> activity, int layout_resource, boolean portrait){
        init(activity, layout_resource, 0, portrait, false);
    }

    public void init(Class<?> activity, int layout_resource, int menu_resource, boolean portrait){
        init(activity, layout_resource, menu_resource, portrait, false);
    }

    public void init(Class<?> activity, int layout_resource, int menu_resource){
        init(activity, layout_resource, menu_resource, false, false);
    }

    public void init(Class<?> activity, int layout_resource){
        init(activity, layout_resource, 0, false, false);
    }

	protected void doBaseInit(String activityName, Intent launchIntent) {
		mEvToOpLinks = new HashMap<>();
		mStopped = false;

        launchIntent.setClassName(this.getApplicationContext(), activityName);
		
		launchIntent.putExtra(JaCaActivity.ARTIFACT_NAME, getId().getName());
		launchIntent.putExtra(JaCaActivity.WSP_NAME, getId().getWorkspaceId().getName());
		launchIntent.putExtra(JaCaActivity.LAYOUT_RESOURCE, activity_layout_resource);
        launchIntent.putExtra(JaCaActivity.MENU_RESOURCE, activity_menu_resource);
		launchIntent.putExtra(JaCaActivity.LAYOUT_PORTRAIT_FIXED, activity_portrait_fixed);
		launchIntent.putExtra(JaCaActivity.LAYOUT_FULLSCREEN, activity_fullscreen);

		launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		launchIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

		defineObsProperty(STATE, (Object) null);

		getApplicationContext().startActivity(launchIntent);
	}

	@OPERATION
	public void setJaCaActivity(String name, JaCaActivity<JaCaBaseActivity> activity) {
		wrappedActivity = activity.getWrappedActivity();
		MasService.getInstance().registerActivity(name, wrappedActivity);
		eventFetcher = activity.getEventsFetcher();

		execInternalOp(FETCH_GUI_EVENTS_OP);

        defineObsProperty(UI_READY);
	}

	@INTERNAL_OPERATION
	protected void fetchGUIEvents() {

		while (!mStopped) {

			await(eventFetcher);

			EventOpInfo eventOp = eventFetcher.getCurrentEventFetched();

			if (eventOp != null) {
				String listener = eventOp.getMethodName();
				Object source = eventOp.getSource();
				HashMap<String, String> map = mEvToOpLinks.get(eventOp
						.getSource());

				/*
				 * Update of the state observable properties, if its the case
				 */
				if (source.equals(wrappedActivity)) {
					ObsProperty prop = getObsProperty(STATE);

                    switch (listener) {
                        case GenericGUIEventFetcher.ON_START:
                            prop.updateValue(ActivityState.STARTED);
                            break;
                        case GenericGUIEventFetcher.ON_RESUME:
                            prop.updateValue(ActivityState.RUNNING);
                            break;
                        case GenericGUIEventFetcher.ON_PAUSE:
                            prop.updateValue(ActivityState.PAUSED);
                            break;
                        case GenericGUIEventFetcher.ON_STOP:
                            prop.updateValue(ActivityState.STOPPED);
                            break;
                        case GenericGUIEventFetcher.ON_DESTROY:
                            prop.updateValue(ActivityState.DESTROYED);
                            break;
                        case GenericGUIEventFetcher.ON_RESTART:
                            prop.updateValue(ActivityState.RESTARTED);
                            break;
                    }
				}

				// Invocation of the operation linked to the event
				if (map != null) {
					String opName = map.get(listener);

					if (opName != null)
						execInternalOp(opName, eventOp.getParam());
				}
			} else {
				mStopped = true;
			}
		}
	}

	protected void bindOnReStartEventToOp(String opName) {
		bindEventToOp(wrappedActivity, GenericGUIEventFetcher.ON_RESTART, opName);
	}

	protected void bindOnStartEventToOp(String opName) {
		bindEventToOp(wrappedActivity, GenericGUIEventFetcher.ON_START, opName);
	}

	protected void bindOnResumeEventToOp(String opName) {
		bindEventToOp(wrappedActivity, GenericGUIEventFetcher.ON_RESUME, opName);
	}

	protected void bindOnPauseEventToOp(String opName) {
		bindEventToOp(wrappedActivity, GenericGUIEventFetcher.ON_PAUSE, opName);
	}

	protected void bindOnStopEventToOp(String opName) {
		bindEventToOp(wrappedActivity, GenericGUIEventFetcher.ON_STOP, opName);
	}

	protected void bindOnDestroyEventToOp(String opName) {
		bindEventToOp(wrappedActivity, GenericGUIEventFetcher.ON_DESTROY, opName);
	}

    protected void bindOnBackPressedToOp(String opName) {
        bindEventToOp(wrappedActivity, GenericGUIEventFetcher.ON_BACK_PRESSED, opName);
    }

	protected void bindOnActivityResultEventToOp(String opName) {
		bindEventToOp(wrappedActivity, GenericGUIEventFetcher.ON_ACTIVITY_RESULT, opName);
	}

	protected void bindOnNewIntentEventToOp(String opName) {
		bindEventToOp(wrappedActivity, GenericGUIEventFetcher.ON_NEW_INTENT, opName);
	}

    protected void bindOnCreateOptionsMenu(String opName) {
        bindEventToOp(wrappedActivity, GenericGUIEventFetcher.ON_CREATE_OPTIONS_MENU, opName);
    }

	protected void bindOnOptionsItemSelectedToOp(String opName) {
		bindEventToOp(wrappedActivity, GenericGUIEventFetcher.ON_OPTIONS_ITEMS_SELECTED, opName);
	}

	protected void bindOnTouchEventToOp(String opName) {
		bindEventToOp(wrappedActivity, GenericGUIEventFetcher.ON_TOUCH_EVENT, opName);
	}

	protected void bindOnDoubleTapToOp(String opName) {
		bindEventToOp(wrappedActivity, GenericGUIEventFetcher.ON_DOUBLE_TAP, opName);
	}

	protected void bindOnDoubleTapEventToOp(String opName) {
		bindEventToOp(wrappedActivity, GenericGUIEventFetcher.ON_DOUBLE_TAP_EVENT, opName);
	}

	protected void bindOnDownToOp(String opName) {
		bindEventToOp(wrappedActivity, GenericGUIEventFetcher.ON_DOWN, opName);
	}

	protected void bindOnFlingToOp(String opName) {
		bindEventToOp(wrappedActivity, GenericGUIEventFetcher.ON_FLING, opName);
	}

	protected void bindOnLongPressToOp(String opName) {
		bindEventToOp(wrappedActivity, GenericGUIEventFetcher.ON_LONG_PRESS, opName);
	}

	protected void bindOnScrollToOp(String opName) {
		bindEventToOp(wrappedActivity, GenericGUIEventFetcher.ON_SCROLL, opName);
	}

	protected void bindOnShowPressToOp(String opName) {
		bindEventToOp(wrappedActivity, GenericGUIEventFetcher.ON_SHOW_PRESS, opName);
	}
	
	protected void bindOnSingleTapConfirmedToOp(String opName) {
		bindEventToOp(wrappedActivity, GenericGUIEventFetcher.ON_SINGLE_TAP_CONFIRMED, opName);
	}
	
	protected void bindOnSingleTapUpToOp(String opName) {
		bindEventToOp(wrappedActivity, GenericGUIEventFetcher.ON_SINGLE_TAP_UP, opName);
	}

	protected void bindOnClickEventToOp(int resID, String opName) {
		View source = wrappedActivity.findViewById(resID);
		bindEventToOp(source, GenericGUIEventFetcher.ON_CLICK, opName);
		source.setOnClickListener(eventFetcher);
	}

	protected void bindOnLongClickEventToOp(int resID, String opName) {
		View source = wrappedActivity.findViewById(resID);
		bindEventToOp(source, GenericGUIEventFetcher.ON_LONG_CLICK, opName);
		source.setOnClickListener(eventFetcher);
	}

	protected void bindOnKeyEventToOp(int resID, String opName) {
		View source = wrappedActivity.findViewById(resID);
		bindEventToOp(source, GenericGUIEventFetcher.ON_KEY, opName);
		source.setOnKeyListener(eventFetcher);
	}

	protected void bindOnTouchEventToOp(int resID, String opName) {
		View source = wrappedActivity.findViewById(resID);
		bindEventToOp(source, GenericGUIEventFetcher.ON_TOUCH, opName);
		source.setOnTouchListener(eventFetcher);
	}

	protected void bindOnFocusChangeEventToOp(int resID, String opName) {
		View source = wrappedActivity.findViewById(resID);
		bindEventToOp(source, GenericGUIEventFetcher.ON_FOCUS_CHANGE, opName);
		source.setOnFocusChangeListener(eventFetcher);
	}

	protected void bindOnEditorActionToOp(TextView source, String opName) {
		bindEventToOp(source, GenericGUIEventFetcher.ON_EDITOR_ACTION, opName);
		source.setOnEditorActionListener(eventFetcher);
	}

	protected void bindEventToOp(Object source, String eventListener, String opName) {
		HashMap<String, String> map = mEvToOpLinks.get(source);

		if (map == null) {
			map = new HashMap<>();
			mEvToOpLinks.put(source, map);
		}

		map.put(eventListener, opName);
	}

	protected void bindOnListItemClickEventToOp(ListView source, String opName) {
		bindEventToOp(source, GenericGUIEventFetcher.ON_LIST_ITEM_CLICK, opName);
		source.setOnItemClickListener(eventFetcher);
	}

	@OPERATION
	public void startActivity(Intent intent) {
		wrappedActivity.startActivity(intent);
	}

	@OPERATION
	public void startImplicitActivity(String action) {
		Intent intent = new Intent(action);
		wrappedActivity.startActivity(intent);
	}

	@OPERATION
	public void startExplicitActivity(String className) {
		Class<?> classTemplate;
		try {
			classTemplate = Class.forName(className);
			Intent intent = new Intent(
					wrappedActivity.getApplicationContext(), classTemplate);
			wrappedActivity.startActivity(intent);
		} catch (ClassNotFoundException e) {
			failed(e.getLocalizedMessage());
		}
	}

	@OPERATION
	public void startActivityForResult(Intent intent, int requestCode) {
		wrappedActivity.startActivityForResult(intent, requestCode);
	}

	@OPERATION
	public void startImplicitActivityForResult(String action, int requestCode) {
		Intent intent = new Intent(action);
		wrappedActivity.startActivityForResult(intent, requestCode);
	}

	@OPERATION
	public void startExplicitActivityForResult(String className, int requestCode) {
		Class<?> classTemplate;
		try {
			classTemplate = Class.forName(className);
			Intent intent = new Intent(
					wrappedActivity.getApplicationContext(), classTemplate);
			wrappedActivity.startActivityForResult(intent, requestCode);
		} catch (ClassNotFoundException e) {
			failed(e.getLocalizedMessage());
		}
	}

	@OPERATION
	public void startService(Intent service) {
		wrappedActivity.startService(service);
	}

	@OPERATION
	public void startImplicitService(String action) {
		Intent intent = new Intent(action);
		wrappedActivity.startService(intent);
	}

	@OPERATION
	public void startExplicitService(String className) {
		Class<?> classTemplate;
		try {
			classTemplate = Class.forName(className);
			Intent intent = new Intent(
					wrappedActivity.getApplicationContext(), classTemplate);
			wrappedActivity.startService(intent);
		} catch (ClassNotFoundException e) {
			failed(e.getLocalizedMessage());
		}
	}

	@OPERATION
	public void stopService(Intent service) {
		wrappedActivity.stopService(service);
	}

	@OPERATION
	public void stopImplicitService(String action) {
		Intent intent = new Intent(action);
		wrappedActivity.stopService(intent);
	}

	@OPERATION
	public void stopExplicitService(String className) {
		Class<?> classTemplate;
		try {
			classTemplate = Class.forName(className);
			Intent intent = new Intent(
					wrappedActivity.getApplicationContext(), classTemplate);
			wrappedActivity.stopService(intent);
		} catch (ClassNotFoundException e) {
			failed(e.getLocalizedMessage());
		}
	}

	@Override
	protected void dispose() {
        if(wrappedActivity != null)
            wrappedActivity.finish();

        super.dispose();
	}

	// ------------------------------------------------------------------------------------

    @GUARD
	boolean activityReady() {
		return wrappedActivity != null;
	}

	@INTERNAL_OPERATION
	void internalSetup() {
		await("activityReady");

		try {
            bindOnActivityResultEventToOp("onActivityResult");
			execInternalOp("setup");
		} catch (Exception ex) {
			failed("Unable to perform internal setup for the artifact.");
		}
	}

	@INTERNAL_OPERATION
	protected void setup() { }

    @OPERATION
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        for(ArtifactId key : registeredArtifactsForResultsMap.keySet()){
            try {
                execLinkedOp(key, registeredArtifactsForResultsMap.get(key), requestCode, resultCode, data);
            } catch (OperationException e) {
                e.printStackTrace();
            }
        }
    }

	@OPERATION
	public void registerArtifactForActivityResults(ArtifactId artifactName, String op){
        if(!registeredArtifactsForResultsMap.containsKey(artifactName)){
            registeredArtifactsForResultsMap.put(artifactName, op);
        }
	}

    @OPERATION
    public void unregisterArtifactForActivityResults(ArtifactId artifactName){
        registeredArtifactsForResultsMap.remove(artifactName);
    }

	protected View findUIElement(int id) {
		return wrappedActivity.findViewById(id);
	}

	protected void execute(Runnable task) {
		wrappedActivity.runOnUiThread(task);
	}
	
	protected Context getActivityContext(){
		return wrappedActivity;
	}

    protected void setUIText(int resID, final String text, final boolean append) {
		final TextView component = wrappedActivity
				.findViewById(resID);

		wrappedActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (append){
					component.append("\n" + text);

					int scrollAmount = component.getLayout().getLineTop(
							component.getLineCount())
							- component.getHeight();

					if (scrollAmount > 0)
						component.scrollTo(0, scrollAmount);

				} else {
					component.setText(text);
				}
			}
		});

	}

    protected String getStringFromResource(int res){
        return wrappedActivity.getString(res);
    }

    protected void disableBackButton(){
        wrappedActivity.disableBackButtonClickEffect();
    }

    protected void enableBackButton() {
        wrappedActivity.enableBackButtonClickEffect();
    }

    protected void keepScreenOn(){
        wrappedActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                wrappedActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        });
	}
}
