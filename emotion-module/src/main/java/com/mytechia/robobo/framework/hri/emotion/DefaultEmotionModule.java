/*******************************************************************************
 *
 *   Copyright 2016 Mytech Ingenieria Aplicada <http://www.mytechia.com>
 *   Copyright 2016 Gervasio Varela <gervasio.varela@mytechia.com>
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
package com.mytechia.robobo.framework.hri.emotion;

import android.view.MotionEvent;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.exception.ModuleNotFoundException;
import com.mytechia.robobo.framework.power.IPowerModeListener;
import com.mytechia.robobo.framework.power.PowerMode;
import com.mytechia.robobo.framework.remote_control.remotemodule.Command;
import com.mytechia.robobo.framework.remote_control.remotemodule.ICommandExecutor;
import com.mytechia.robobo.framework.remote_control.remotemodule.IRemoteControlModule;
import com.mytechia.robobo.framework.remote_control.remotemodule.Status;

import org.opencv.android.CameraBridgeViewBase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

/** A naive implementation of the emotion module that manages the changes of the
 * emotion and the notification to the listeners.
 */
public class DefaultEmotionModule implements IEmotionModule, IPowerModeListener {

    private ArrayList<IEmotionListener> listeners = new ArrayList<>();
    private ArrayList<ITouchEventListener> touchlisteners = new ArrayList<>();
    private Emotion currentEmotion;
    private CameraBridgeViewBase cameraBridge;
    protected RoboboManager m;
    private IRemoteControlModule rcmodule;


    public DefaultEmotionModule() { }


    /** Change the emotion of the robot when the robot goes
     * to low power mode (sleep) or to normal (normal face)
     *
     * @param newMode new power mode
     */
    @Override
    public void onPowerModeChange(PowerMode newMode) {
        if (newMode == PowerMode.LOWPOWER) {
            setCurrentEmotion(Emotion.SLEEPING);
        }
        else {
            setCurrentEmotion(Emotion.NORMAL);
        }
    }


    @Override
    public void startup(RoboboManager manager) throws InternalErrorException {
        m= manager;

        manager.subscribeToPowerModeChanges(this);

        rcmodule = m.getModuleInstance(IRemoteControlModule.class);

        //register a new REMOTE COMMAND to allow chaging the emotion remotelly
        rcmodule.registerCommand("SET-EMOTION", new ICommandExecutor() {
            @Override
            public void executeCommand(Command c, IRemoteControlModule rcmodule) {

                setCurrentEmotion(Emotion.fromString(c.getParameters().get("emotion")));
                Status s = new Status("EMOTION");
                s.putContents("emotion", c.getParameters().get("emotion"));
                rcmodule.postStatus(s);

            }
        });

    }

    @Override
    public void shutdown() throws InternalErrorException {

    }

    @Override
    public String getModuleInfo() {
        return "Default Emotion Module";
    }

    @Override
    public String getModuleVersion() {
        return "1.0.0";
    }


    private void notifyEmotion(Emotion emotion) {
        Iterator<IEmotionListener> iter = this.listeners.iterator();
        while(iter.hasNext()) {
            iter.next().newEmotion(emotion);
        }


    }

    public void subscribe(IEmotionListener listener) {
        this.listeners.add(listener);
    }

    public void ubsubscribe(IEmotionListener listener) {
        this.listeners.remove(listener);
    }

    public void subscribeTouchListener(ITouchEventListener listener) {
        this.touchlisteners.add(listener);
    }

    public void unsubscribeTouchListener(ITouchEventListener listener) {
        this.touchlisteners.remove(listener);
    }

    @Override
    public CameraBridgeViewBase getCameraBridgeView() {
        return cameraBridge;
    }

    @Override
    public void setCameraBridgeView(CameraBridgeViewBase viewBase) {
        cameraBridge = viewBase;
    }


    @Override
    public void setCurrentEmotion(Emotion emotion) {

        this.currentEmotion = emotion;
        notifyEmotion(emotion);

    }

    @Override
    public Emotion getCurrentEmotion() {
        return this.currentEmotion;
    }

    @Override
    public void setTemporalEmotion(Emotion temporalEmotion, long duration, Emotion nextEmotion) {
        setCurrentEmotion(temporalEmotion);
        Timer timer = new Timer();
        timer.schedule(new TemporalEmotionChangeTask(nextEmotion), duration);
    }


    public void notifyTouchEvent(MotionEvent event){
        Iterator<ITouchEventListener> iter = this.touchlisteners.iterator();
        while(iter.hasNext()) {
            iter.next().onScreenTouchEvent(event);
        }
    }

    private class TemporalEmotionChangeTask extends TimerTask {

        private Emotion next;

        public TemporalEmotionChangeTask(Emotion next) {
            this.next = next;
        }


        @Override
        public void run() {

            setCurrentEmotion(next);
            Status s = new Status("EMOTION");

            s.putContents("emotion", next.toString());

            rcmodule.postStatus(s);

        }

    }

}
