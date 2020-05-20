package it.unibo.pslab.jaca_android.core;

import android.content.Context;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * Class used for managing the dispatch of Activity events
 * to CArtAgO Artifacts
 *
 * @author asanti
 *
 */
public class GenericGUIEventFetcher extends BaseEventFetcher implements OnClickListener, OnLongClickListener, OnKeyListener, OnTouchListener, OnFocusChangeListener, OnEditorActionListener, ListView.OnItemClickListener{

	/* Pure activity events */
	public static final String ON_CREATE = "onCreate";
	public static final String ON_RESTART = "onReStart";
	public static final String ON_START = "onStart";
	public static final String ON_RESUME = "onResume";
	public static final String ON_PAUSE = "onPause";
	public static final String ON_STOP = "onStop";
	public static final String ON_DESTROY = "onDestroy";
	public static final String ON_ACTIVITY_RESULT = "onActivityResult";
	public static final String ON_LIST_ITEM_CLICK = "onListItemClick";
	public static final String ON_CREATE_OPTIONS_MENU = "onCreateOptionsMenu";
	public static final String ON_OPTIONS_ITEMS_SELECTED = "onOptionsItemSelected";
	public static final String ON_TOUCH_EVENT = "onTouchEvent";
	public static final String ON_NEW_INTENT = "onNewIntent";

	/* View events */
	public static final String ON_CLICK = "onClick";
	public static final String ON_LONG_CLICK = "onLongClick";
	public static final String ON_KEY = "onKey";
	public static final String ON_TOUCH = "onTouch";
	public static final String ON_FOCUS_CHANGE = "onFocusChange";
	public static final String ON_EDITOR_ACTION = "onEditorAction";
	public static final String ON_DOUBLE_TAP = "onDoubleTap";
	public static final String ON_DOUBLE_TAP_EVENT = "onDoubleTapEvent";
	public static final String ON_DOWN = "onDown";
	public static final String ON_FLING = "onFling";
	public static final String ON_LONG_PRESS = "onLongPress";
	public static final String ON_SCROLL = "onScroll";
	public static final String ON_SHOW_PRESS = "onShowPress";
	public static final String ON_SINGLE_TAP_CONFIRMED = "onSingleTapConfirmed";
	public static final String ON_SINGLE_TAP_UP = "onSingleTapUp";
	public static final String ON_BACK_PRESSED = "onBackPressed";
	
	private GestureDetector mGestureDetector;
	private boolean isListenGesture;
	
	/**
	 * Constructor to use for managing activity and View events
	 * without considering gesture
	 */
	public GenericGUIEventFetcher() {
		isListenGesture = false;
	}

	/**
	 * Constructor to use for managing activity and View events
	 * also considering gesture
	 */
	public GenericGUIEventFetcher(Context ctx) {
		isListenGesture = true;
		mGestureDetector = new GestureDetector(ctx, new MyGestureDetector());
	}
	
	public void onClick(View view) {
		putEvent(new EventOpInfo(view, ON_CLICK, view));
	}
	
	public boolean onLongClick(View view) {
		putEvent(new EventOpInfo(view, ON_LONG_CLICK, view));
		return true;
	}

	public boolean onKey(View view, int keyCode, KeyEvent event) {
		putEvent(new EventOpInfo(view, ON_KEY, view, keyCode, event));
		return true;
	}

	public boolean onTouch(View view, MotionEvent event) {
		/*
		 * Call the GestureDetector first. If the gesture
		 * is not recognised then a simple onTouch event is
		 * inserted 
		 */
		if(isListenGesture && !mGestureDetector.onTouchEvent(event))
			putEvent(new EventOpInfo(view, ON_TOUCH, view, event));
		return true;
	}
	
	public void onFocusChange(View view, boolean arg1) {
		putEvent(new EventOpInfo(view, ON_FOCUS_CHANGE, view, arg1));
	}
	
	public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
		putEvent(new EventOpInfo(view, ON_EDITOR_ACTION, view, actionId, event));
		return true;
	}
		
	public GestureDetector getGestureDetector() {
		return mGestureDetector;
	}

	public boolean isGestureDetectorInstalled(){
		return mGestureDetector != null;
	}
	
	/**
	 * Specialization of GestureDetector.SimpleOnGestureListene that allow
	 * to detect Android gesture events
	 * @author asanti
	 *
	 */
	class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
		public MyGestureDetector(){
			super();
		}
		
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			putEvent(new EventOpInfo(this, ON_DOUBLE_TAP, e));
			return true;
		}
		
		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			putEvent(new EventOpInfo(this, ON_DOUBLE_TAP_EVENT, e));
			return true;
		}
		
		@Override
		public boolean onDown(MotionEvent e) {
			putEvent(new EventOpInfo(this, ON_DOWN, e));
			return true;
		}
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			putEvent(new EventOpInfo(this, ON_FLING, e1, e2, velocityX, velocityY));
			return true;
		}
		
		@Override
		public void onLongPress(MotionEvent e) {
			putEvent(new EventOpInfo(this, ON_LONG_PRESS, e));
		}
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			putEvent(new EventOpInfo(this, ON_SCROLL, e1, e2, distanceX, distanceY));
			return true;
		}
		
		@Override
		public void onShowPress(MotionEvent e) {
			putEvent(new EventOpInfo(this, ON_SHOW_PRESS, e));
		}
		
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			putEvent(new EventOpInfo(this, ON_SINGLE_TAP_CONFIRMED, e));
			return true;
		}
		
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			putEvent(new EventOpInfo(this, ON_SINGLE_TAP_UP, e));
			return true;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
		putEvent(new EventOpInfo(l, GenericGUIEventFetcher.ON_LIST_ITEM_CLICK, l, v, position, id));
	}
}