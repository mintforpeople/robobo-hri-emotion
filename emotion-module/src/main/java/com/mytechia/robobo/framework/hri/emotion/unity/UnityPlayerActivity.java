package com.mytechia.robobo.framework.hri.emotion.unity;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.webkit.WebView;
import android.widget.FrameLayout;
import com.unity3d.player.*;


import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.exception.ModuleNotFoundException;
import com.mytechia.robobo.framework.hri.emotion.DefaultEmotionModule;
import com.mytechia.robobo.framework.hri.emotion.Emotion;
import com.mytechia.robobo.framework.hri.emotion.IEmotionListener;
import com.mytechia.robobo.framework.hri.emotion.R;
import com.mytechia.robobo.framework.hri.emotion.webgl.WebGLEmotionDisplayActivity;
import com.mytechia.robobo.framework.remote_control.remotemodule.Command;
import com.mytechia.robobo.framework.remote_control.remotemodule.ICommandExecutor;
import com.mytechia.robobo.framework.remote_control.remotemodule.IRemoteControlModule;
import com.mytechia.robobo.framework.service.RoboboServiceHelper;
import com.unity3d.player.UnityPlayer;

import org.opencv.android.CameraBridgeViewBase;

public class UnityPlayerActivity extends Activity implements IEmotionListener
{
	protected UnityPlayer mUnityPlayer; // don't change the name of this variable; referenced from native code
    protected FrameLayout unitylayout;
    protected FrameLayout flayout;

    private RoboboServiceHelper roboboHelper;




    private DefaultEmotionModule emotionModule;



    private CameraBridgeViewBase cameraBridge;



    // Setup activity layout
	@Override protected void onCreate (Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		getWindow().setFormat(PixelFormat.RGBX_8888); // <--- This makes xperia play happy

		//mUnityPlayer = new UnityPlayer(this);

		setContentView(R.layout.activity_unity_layout);
        flayout = (FrameLayout) findViewById(R.id.flayoutUnity);
        unitylayout = (FrameLayout) findViewById(R.id.unitylayout);
        mUnityPlayer = new UnityPlayer(UnityPlayerActivity.this);
        unitylayout.addView(mUnityPlayer.getView());
		//setContentView(mUnityPlayer);
		mUnityPlayer.requestFocus();

		cameraBridge = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvViewUnity) ;

		this.roboboHelper = new RoboboServiceHelper(this, new RoboboServiceHelper.Listener() {
            @Override
            public void onRoboboManagerStarted(RoboboManager roboboManager) {

                try {
                    emotionModule =
                            roboboManager.getModuleInstance(DefaultEmotionModule.class);
                    emotionModule.subscribe(UnityPlayerActivity.this);
					emotionModule.setCameraBridgeView(cameraBridge);
                } catch(ModuleNotFoundException ex) {
                    Log.e("EMOTION-UNITY", ex.getMessage());
                }


                try {
                    roboboManager.getModuleInstance(IRemoteControlModule.class).registerCommand("CHANGEEMOTION", new ICommandExecutor() {
                        @Override
                        public void executeCommand(Command c, IRemoteControlModule rcmodule) {
                            mUnityPlayer.UnitySendMessage("roboboFace","msgFace",c.getParameters().get("emotion"));

                        }
                    });
                } catch (ModuleNotFoundException e) {

                }
            }

            @Override
            public void onError(String errorMsg) {

            }
        });
        this.roboboHelper.bindRoboboService(new Bundle());
        }

	// Quit Unity
	@Override protected void onDestroy ()
	{
		mUnityPlayer.quit();
		super.onDestroy();
	}

	// Pause Unity
	@Override protected void onPause()
	{
		super.onPause();
		mUnityPlayer.UnitySendMessage("roboboFace","msgFace","angry");
		mUnityPlayer.pause();
	}

	// Resume Unity
	@Override protected void onResume()
	{
		super.onResume();
		mUnityPlayer.resume();
	}

	// This ensures the layout will be correct.
	@Override public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		mUnityPlayer.configurationChanged(newConfig);
	}

	// Notify Unity of the focus change.
	@Override public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
		mUnityPlayer.windowFocusChanged(hasFocus);
	}

	// For some reason the multiple keyevent type is not supported by the ndk.
	// Force event injection by overriding dispatchKeyEvent().
	@Override public boolean dispatchKeyEvent(KeyEvent event)
	{
		if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
			return mUnityPlayer.injectEvent(event);
		return super.dispatchKeyEvent(event);
	}

	// Pass any events not handled by (unfocused) views straight to UnityPlayer
	@Override public boolean onKeyUp(int keyCode, KeyEvent event)     { return mUnityPlayer.injectEvent(event); }
	@Override public boolean onKeyDown(int keyCode, KeyEvent event)   { return mUnityPlayer.injectEvent(event); }
	@Override public boolean onTouchEvent(MotionEvent event)          { return mUnityPlayer.injectEvent(event); }
	/*API12*/ public boolean onGenericMotionEvent(MotionEvent event)  { return mUnityPlayer.injectEvent(event); }

	@Override
	public void newEmotion(Emotion emotion) {
		switch (emotion){
			case HAPPY:
				mUnityPlayer.UnitySendMessage("roboboFace","msgFace","happy");
				break;
			case SAD:
				mUnityPlayer.UnitySendMessage("roboboFace","msgFace","sad");
				break;
			case ANGRY:
				mUnityPlayer.UnitySendMessage("roboboFace","msgFace","angry");
				break;
			case SMYLING:
				mUnityPlayer.UnitySendMessage("roboboFace","msgFace","smiling");
				break;
			case LAUGHING:
				mUnityPlayer.UnitySendMessage("roboboFace","msgFace","laughting");
				break;
			case EMBARRASED:
				mUnityPlayer.UnitySendMessage("roboboFace","msgFace","embarassed");
				break;
			case SURPRISED:
				mUnityPlayer.UnitySendMessage("roboboFace","msgFace","surprised");
				break;
			case IN_LOVE:
				mUnityPlayer.UnitySendMessage("roboboFace","msgFace","love");
				break;
			case NORMAL:
				mUnityPlayer.UnitySendMessage("roboboFace","msgFace","normal");
				break;
		}
	}
}
