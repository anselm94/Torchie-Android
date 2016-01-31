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

package in.blogspot.anselmbros.torchie.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

import in.blogspot.anselmbros.torchie.donate.IabHelper;
import in.blogspot.anselmbros.torchie.donate.IabResult;
import in.blogspot.anselmbros.torchie.donate.Inventory;
import in.blogspot.anselmbros.torchie.donate.Purchase;
import in.blogspot.anselmbros.torchie.misc.TorchieConstants;
import in.blogspot.anselmbros.torchie.misc.key;

/**
 * Created by anselm94 on 8/12/15.
 */
public class DonationAPI implements IabHelper.OnIabSetupFinishedListener, IabHelper.QueryInventoryFinishedListener, IabHelper.OnIabPurchaseFinishedListener, IabHelper.OnConsumeFinishedListener {

    public static String SKU_DONATION_1 = "donate_1";
    public static String SKU_DONATION_2 = "donate_2";
    public static String SKU_DONATION_3 = "donate_3";
    public static String SKU_DONATION_4 = "donate_4";
    public static String SKU_DONATION_5 = "donate_5";
    public static String SKU_DONATION_6 = "donate_6";
    public String TAG = TorchieConstants.INFO;
    private IabHelper mHelper;
    private DonationAPICallback mListener;
    private String base64EncodedPublicKey;
    private Activity mActivity;

    private boolean isServiceReady;
    private boolean isPurchaseFlowing = false;

    public DonationAPI(Activity activity) {
        this.mActivity = activity;
        TAG = getClass().getName();
        base64EncodedPublicKey = getBase64EncodedPublicKey();
        mHelper = new IabHelper(mActivity.getApplicationContext(), base64EncodedPublicKey);
//        mHelper.enableDebugLogging(true,"Donation");
    }

    public void setupConnection() {
        mHelper.startSetup(this);
    }

    public void tearConnection() {
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
        Log.d(TAG, "Tear down connection!");
    }

    public void setDonationAPICallback(DonationAPICallback listener) {
        this.mListener = listener;
    }

    public void queryDonationList() {
        ArrayList<String> additionalSkuList = new ArrayList<>();
        additionalSkuList.add(SKU_DONATION_1);
        additionalSkuList.add(SKU_DONATION_2);
        additionalSkuList.add(SKU_DONATION_3);
        additionalSkuList.add(SKU_DONATION_4);
        additionalSkuList.add(SKU_DONATION_5);
        additionalSkuList.add(SKU_DONATION_6);
        mHelper.queryInventoryAsync(true, additionalSkuList, this);
    }

    public void initDonationAmount(String skuProductID) {
        if (mHelper != null && !isPurchaseFlowing) {
            mHelper.launchPurchaseFlow(mActivity, skuProductID, 10001, this, "ajfowvlxkep34bndpf");
            isPurchaseFlowing = true;
        }
    }

    public boolean handleActivityIntent(int requestCode, int resultCode, Intent data) {
        if (mHelper == null)
            return false;
        else
            return mHelper.handleActivityResult(requestCode, resultCode, data);

    }

    public boolean isServiceReady() {
        return isServiceReady;
    }

    private String getBase64EncodedPublicKey() {
        key mKey = new key();
        String key = mKey.sh + mKey.an + mKey.th + mKey.im + mKey.ed + mKey.on + mKey.ac + mKey.ha + mKey.n + mKey.dr + mKey.ab + mKey.os + mKey.e + mKey.me + mKey.rb + mKey.in + mKey.re + mKey.bi + mKey.na + mKey.ns;
        return key;
    }

    @Override
    public void onIabSetupFinished(IabResult result) {
        if (!result.isSuccess()) {
            Log.d(TAG, "Problem setting up In-app Billing: " + result);
            isServiceReady = false;
            if (mListener != null) mListener.onAvailabilityCheck(false);
        } else {
            Log.d(TAG, "Success setting up In-app Billing: " + result);
            isServiceReady = true;
            if (mListener != null) mListener.onAvailabilityCheck(true);
        }
    }

    @Override
    public void onQueryInventoryFinished(IabResult result, Inventory inv) {
        if (result.isFailure()) {
            // handle error
            Log.d(TAG, "Querying inventory is a failure : " + result);
            if (mListener != null) mListener.onInventoryAvailability(false, null);
            return;
        }
        if (mListener != null) mListener.onInventoryAvailability(true, inv);
    }

    @Override
    public void onIabPurchaseFinished(IabResult result, Purchase info) {
        isPurchaseFlowing = false;
        if (result.isFailure()) {
            Log.d(TAG, "Error purchasing: " + result);
            if (mListener != null) mListener.onPurchaseComplete(false, null);
            return;
        }
        mHelper.consumeAsync(info, this);
    }

    @Override
    public void onConsumeFinished(Purchase purchase, IabResult result) {
        if (mListener != null) mListener.onPurchaseComplete(true, null);
    }

    public interface DonationAPICallback {
        void onAvailabilityCheck(boolean isAvailable);

        void onInventoryAvailability(boolean isAvailable, Inventory inventory);

        void onPurchaseComplete(boolean success, Purchase purchaseInfo);
    }
}
