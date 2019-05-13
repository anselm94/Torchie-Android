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

package in.blogspot.anselmbros.torchie.main.manager.device.output.torch.flashlight;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;

import in.blogspot.anselmbros.torchie.R;
import in.blogspot.anselmbros.torchie.utils.Constants;

/**
 * Created by Merbin J Anselm on 04-Feb-17.
 */

@TargetApi(23)
public class Flashlight2 extends Flashlight {

    public static final String TYPE = Constants.ID_DEVICE_OUTPUT_TORCH_FLASH_NEW;

    private String[] mCameraIDList;
    private boolean flashSupported;

    public Flashlight2(Context context) {
        super(context);
        flashSupported = false;
        this.deviceType = TYPE;
    }

    @Override
    protected void turnOn() {
        if (!this.getStatus()) {
            CameraManager mCameraManager = (CameraManager) this.mContext.getSystemService(Context.CAMERA_SERVICE);
            try {
                this.mCameraIDList = mCameraManager.getCameraIdList();
            } catch (CameraAccessException e) {
                this.updateError(this.mContext.getResources().getString(R.string.camera_error));
                return;
            }
            try {
                CameraCharacteristics mCameraParameters = mCameraManager.getCameraCharacteristics(this.mCameraIDList[0]);
                this.flashSupported = mCameraParameters.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            } catch (Exception e) {
                this.updateError(this.mContext.getResources().getString(R.string.torch_unsupported));
                return;
            }
            if (this.flashSupported) {
                try {
                    mCameraManager.setTorchMode(this.mCameraIDList[0], true);
                    this.updateStatus(true);
                } catch (CameraAccessException e) {
                    this.updateError(this.mContext.getResources().getString(R.string.camera_busy));
                }
            }
        }
    }

    @Override
    protected void turnOff() {
        if (this.getStatus()) {
            if (this.mCameraIDList != null && this.flashSupported) {
                CameraManager mCameraManager = (CameraManager) this.mContext.getSystemService(Context.CAMERA_SERVICE);
                try {
                    mCameraManager.setTorchMode(mCameraIDList[0], false);
                } catch (CameraAccessException e) {
                    this.updateError(this.mContext.getResources().getString(R.string.torch_unsupported));
                    return;
                }
                this.updateStatus(false);
            }
        }
    }
}
