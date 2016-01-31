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

package in.blogspot.anselmbros.torchie.ui.dialog;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import in.blogspot.anselmbros.torchie.R;
import in.blogspot.anselmbros.torchie.donate.Inventory;
import in.blogspot.anselmbros.torchie.donate.Purchase;
import in.blogspot.anselmbros.torchie.misc.TorchieConstants;
import in.blogspot.anselmbros.torchie.ui.activity.MainActivity;
import in.blogspot.anselmbros.torchie.utils.DonationAPI;

/**
 * Created by anselm94 on 9/12/15.
 */
public class DonateDialog extends DialogFragment implements View.OnClickListener, DonationAPI.DonationAPICallback, AdapterView.OnItemSelectedListener {

    View rootView;
    DonationAPI mDonation;

    Button butDonate, butDonatePaypal;
    TextView tvStatus;
    Spinner spinDonateAmount;

    ArrayAdapter<String> donateAmountAdapter;
    ArrayList<String> donationValues;
    ArrayList<String> donationNote;
    ArrayList<String> skuNameList;

    MainActivity mainActivityInstance;
    int prevTextColor;

    public DonateDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_donate, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        mainActivityInstance = (MainActivity) getActivity();

        mDonation = new DonationAPI(getActivity());
        mDonation.setupConnection();
        mDonation.setDonationAPICallback(this);

        butDonate = (Button) rootView.findViewById(R.id.but_donate_now);
        butDonatePaypal = (Button) rootView.findViewById(R.id.but_paypal_donate);
        spinDonateAmount = (Spinner) rootView.findViewById(R.id.spin_donate_values);
        tvStatus = (TextView) rootView.findViewById(R.id.tv_donate_status);

        butDonatePaypal.setVisibility(View.INVISIBLE);

        butDonatePaypal.setOnClickListener(this);
        butDonate.setOnClickListener(this);
        spinDonateAmount.setOnItemSelectedListener(this);

        prevTextColor = butDonate.getCurrentTextColor();
        setDonateEnabled(false, null);

        skuNameList = new ArrayList<>();
        skuNameList.add(DonationAPI.SKU_DONATION_1);
        skuNameList.add(DonationAPI.SKU_DONATION_2);
        skuNameList.add(DonationAPI.SKU_DONATION_3);
        skuNameList.add(DonationAPI.SKU_DONATION_4);
        skuNameList.add(DonationAPI.SKU_DONATION_5);
        skuNameList.add(DonationAPI.SKU_DONATION_6);

        donationNote = new ArrayList<>();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        mDonation.tearConnection();
        mDonation = null;
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        if (v == butDonate) {
            mDonation.initDonationAmount(getSkuNameAtposition(spinDonateAmount.getSelectedItemPosition()));
            setDonateEnabled(false, " ");
        } else if (v == butDonatePaypal) {
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(TorchieConstants.WEB_DONATE_URI));
                    startActivity(intent);
                    dismiss();
                }

            }, 350L);

        }
    }

    private void setDonateEnabled(boolean enable, String msg) {
        butDonate.setEnabled(enable);
        spinDonateAmount.setEnabled(enable);
        if (!enable) {
            butDonate.setTextColor(getResources().getColor(R.color.colorGrey600));
        } else {
            butDonate.setTextColor(prevTextColor);
        }
        if (msg == null) {
            if (enable)
                tvStatus.setText(getActivity().getResources().getString(R.string.select_donation_amount));
            else
                tvStatus.setText(" ");
        } else {
            tvStatus.setText(msg);
        }

    }

    private void loadValuesToAdapter(Inventory inventory) {

        donationValues = new ArrayList<>();
        donationValues.clear();
        donationNote.clear();

        for (int i = 0; i < skuNameList.size(); i++) {
            donationValues.add(inventory.getSkuDetails(getSkuNameAtposition(i)).getPrice());
            donationNote.add(inventory.getSkuDetails(getSkuNameAtposition(i)).getDescription());
        }

        donateAmountAdapter = new ArrayAdapter<>(getActivity(), R.layout.layout_list_item, donationValues);
        spinDonateAmount.setAdapter(donateAmountAdapter);
        spinDonateAmount.setSelection(0, true);
    }

    private String getSkuNameAtposition(int position) {
        return skuNameList.get(position);
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (mDonation != null) {
            mDonation.handleActivityIntent(requestCode, resultCode, data);
        }
    }

    @Override
    public void onAvailabilityCheck(boolean isAvailable) {//If Google Purchasing API Service is available
        if (isAvailable) {
            mDonation.queryDonationList();
            tvStatus.setText(getResources().getString(R.string.loading));
        } else {
            setDonateEnabled(false, "Google Play In-App donation not supported!");
            tvStatus.setText('-');
            butDonatePaypal.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onInventoryAvailability(boolean isAvailable, Inventory inventory) {
        if (isAvailable) {
            setDonateEnabled(true, null);
            loadValuesToAdapter(inventory);
        }
    }

    @Override
    public void onPurchaseComplete(boolean success, Purchase purchaseInfo) {
        if (success) {
            mainActivityInstance.showDialogDonateSuccess();
            this.dismiss();
        } else {
            mainActivityInstance.showDialogDonateFailure();
            this.dismiss();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        tvStatus.setText(donationNote.get(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
