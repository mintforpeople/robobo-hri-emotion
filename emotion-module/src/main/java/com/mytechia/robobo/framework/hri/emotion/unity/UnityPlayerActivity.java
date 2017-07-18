/*******************************************************************************
 *
 *   Copyright 2017 Mytech Ingenieria Aplicada <http://www.mytechia.com>
 *   Copyright 2017 Luis Llamas <luis.llamas@mytechia.com>
 *
 *   This file is part of Robobo Emotion Module.
 *
 *   Robobo Emotion Module is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Robobo Emotion Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Robobo Emotion Module.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package com.mytechia.robobo.framework.hri.emotion.unity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.mytechia.robobo.framework.LogLvl;
import com.mytechia.robobo.framework.remote_control.remotemodule.Status;
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

public class UnityPlayerActivity extends Activity implements IEmotionListener {
	@Override
	protected void onNewIntent(Intent intent) {
		if( intent.getBooleanExtra("finish",false) ){
//			finish();
			this.onBackPressed();
		}
		super.onNewIntent(intent);
	}

	protected UnityPlayer mUnityPlayer; // don't change the name of this variable; referenced from native code
    protected FrameLayout unitylayout;
    protected FrameLayout flayout;
	protected LinearLayout touchCapture;



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
		touchCapture = (LinearLayout) findViewById(R.id.mainlayout);
        unitylayout = (FrameLayout) findViewById(R.id.unitylayout);
        mUnityPlayer = new UnityPlayer(UnityPlayerActivity.this);
        unitylayout.addView(mUnityPlayer.getView());
		//setContentView(mUnityPlayer);
		mUnityPlayer.requestFocus();
        mUnityPlayer.getView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d("WebGlActivity","EVENT");
				emotionModule.notifyTouchEvent(motionEvent);
				return false;
            }
        });



		cameraBridge = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvViewUnity) ;

//
//		cameraBridge.setOnTouchListener(new View.OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View view, MotionEvent motionEvent) {
//				Log.d("WebGlActivity","EVENT");
//				emotionModule.notifyTouchEvent(motionEvent);
//				return false;
//			}
//		});


		this.roboboHelper = new RoboboServiceHelper(this, new RoboboServiceHelper.Listener() {
            @Override
            public void onRoboboManagerStarted(RoboboManager roboboManager) {

                try {
                    emotionModule =
                            roboboManager.getModuleInstance(DefaultEmotionModule.class);
                    emotionModule.subscribe(UnityPlayerActivity.this);
					emotionModule.setCameraBridgeView(cameraBridge);
                } catch(ModuleNotFoundException ex) {
                    roboboManager.log(LogLvl.ERROR, "EMOTION-UNITY", ex.getMessage());
                }


                try {
                    roboboManager.getModuleInstance(IRemoteControlModule.class).registerCommand("CHANGEEMOTION", new ICommandExecutor() {
                        @Override
                        public void executeCommand(Command c, IRemoteControlModule rcmodule) {
                            mUnityPlayer.UnitySendMessage("roboboFace","msgFace",c.getParameters().get("emotion"));
							Status s = new Status("EMOTIONSTATUS");
							s.putContents("emotion", c.getParameters().get("emotion"));
							rcmodule.postStatus(s);

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




//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
//        Log.d("UnityActivity","EVENT");
//        emotionModule.notifyTouchEvent(event);
//        return super.onTouchEvent(event);
//    }
//


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
			case SLEEPING:
				mUnityPlayer.UnitySendMessage("roboboFace","msgFace","sleeping");
				break;
		}
	}



}
