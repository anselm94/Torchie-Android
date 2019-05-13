/*
 *     Copyright (C) 2016  Merbin J Anselm <merbinjanselm@gmail.com>
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package in.blogspot.anselmbros.torchie.main.manager.device.input.event;

import android.view.KeyEvent;

/**
 * Created by Merbin J Anselm on 05-Feb-17.
 */

public class VolumeKeyEvent extends KeyEvent {

    public static final int VOLUME_KEY_EVENT_NATIVE = 0;
    public static final int VOLUME_KEY_EVENT_ROCKER = 1;

    private final int mVolumeKeyEventType;
    private final int[] mPrevCurrentValue;

    public VolumeKeyEvent(int action, int code) {
        super(action, code);
        this.mVolumeKeyEventType = VOLUME_KEY_EVENT_NATIVE;
        this.mPrevCurrentValue = null;
    }

    public VolumeKeyEvent(long downTime, long eventTime, int action, int code, int repeat) {
        super(downTime, eventTime, action, code, repeat);
        this.mVolumeKeyEventType = VOLUME_KEY_EVENT_NATIVE;
        this.mPrevCurrentValue = null;
    }

    public VolumeKeyEvent(long downTime, long eventTime, int action, int code, int repeat, int metaState) {
        super(downTime, eventTime, action, code, repeat, metaState);
        this.mVolumeKeyEventType = VOLUME_KEY_EVENT_NATIVE;
        this.mPrevCurrentValue = null;
    }

    public VolumeKeyEvent(long downTime, long eventTime, int action, int code, int repeat, int metaState, int deviceId, int scancode) {
        super(downTime, eventTime, action, code, repeat, metaState, deviceId, scancode);
        this.mVolumeKeyEventType = VOLUME_KEY_EVENT_NATIVE;
        this.mPrevCurrentValue = null;
    }

    public VolumeKeyEvent(long downTime, long eventTime, int action, int code, int repeat, int metaState, int deviceId, int scancode, int flags) {
        super(downTime, eventTime, action, code, repeat, metaState, deviceId, scancode, flags);
        this.mVolumeKeyEventType = VOLUME_KEY_EVENT_NATIVE;
        this.mPrevCurrentValue = null;
    }

    public VolumeKeyEvent(long downTime, long eventTime, int action, int code, int repeat, int metaState, int deviceId, int scancode, int flags, int source) {
        super(downTime, eventTime, action, code, repeat, metaState, deviceId, scancode, flags, source);
        this.mVolumeKeyEventType = VOLUME_KEY_EVENT_NATIVE;
        this.mPrevCurrentValue = null;
    }

    public VolumeKeyEvent(long time, String characters, int deviceId, int flags) {
        super(time, characters, deviceId, flags);
        this.mVolumeKeyEventType = VOLUME_KEY_EVENT_NATIVE;
        this.mPrevCurrentValue = null;
    }

    public VolumeKeyEvent(KeyEvent origEvent) {
        super(origEvent);
        this.mVolumeKeyEventType = VOLUME_KEY_EVENT_NATIVE;
        this.mPrevCurrentValue = null;
    }

    @Deprecated
    public VolumeKeyEvent(KeyEvent origEvent, long eventTime, int newRepeat) {
        super(origEvent, eventTime, newRepeat);
        this.mVolumeKeyEventType = VOLUME_KEY_EVENT_NATIVE;
        this.mPrevCurrentValue = null;
    }

    public VolumeKeyEvent(int[] prevCurrentValues) {
        super(ACTION_DOWN, KEYCODE_VOLUME_DOWN);
        this.mVolumeKeyEventType = VOLUME_KEY_EVENT_ROCKER;
        this.mPrevCurrentValue = prevCurrentValues;
    }

    public int getVolumeKeyEventType() {
        return this.mVolumeKeyEventType;
    }

    public boolean isVolumeKeyEvent() {
        return (this.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) || (this.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP);
    }

    public int[] getPrevCurrentValue() {
        return this.mPrevCurrentValue;
    }
}
