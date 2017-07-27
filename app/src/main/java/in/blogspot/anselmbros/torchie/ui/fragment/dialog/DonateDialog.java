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

package in.blogspot.anselmbros.torchie.ui.fragment.dialog;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import java.util.Arrays;

import in.blogspot.anselmbros.torchie.BuildConfig;
import in.blogspot.anselmbros.torchie.R;
import in.blogspot.anselmbros.torchie.ui.helper.DonateDialogListener;
import in.blogspot.anselmbros.torchie.utils.Constants;
import in.blogspot.anselmbros.torchie.utils.IabUtils.IabHelper;
import in.blogspot.anselmbros.torchie.utils.IabUtils.IabResult;
import in.blogspot.anselmbros.torchie.utils.IabUtils.Inventory;
import in.blogspot.anselmbros.torchie.utils.IabUtils.Purchase;

/**
 * Created by anselm94 on 9/12/15.
 */
public class DonateDialog extends DialogFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, IabHelper.OnIabSetupFinishedListener, IabHelper.QueryInventoryFinishedListener, IabHelper.OnIabPurchaseFinishedListener {

    Button butDonate, butDonatePaypal;
    TextView tvStatus;
    Spinner spinDonateAmount;
    IabHelper mIabHelper;
    DonateDialogListener mListener;
    ArrayList<String> skuPrices, skuDescriptions;
    boolean isPurchaseFlowing = false;
    private String[] SKU_DONATION = {"donate_1", "donate_2", "donate_3", "donate_4", "donate_5", "donate_6"};

    public DonateDialog() {
    }

    public void setListener(DonateDialogListener listener) {
        this.mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_donate, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        butDonate = (Button) rootView.findViewById(R.id.but_donate_now);
        butDonatePaypal = (Button) rootView.findViewById(R.id.but_paypal_donate);
        spinDonateAmount = (Spinner) rootView.findViewById(R.id.spin_donate_values);
        tvStatus = (TextView) rootView.findViewById(R.id.tv_donate_status);

        butDonatePaypal.setVisibility(View.INVISIBLE);

        butDonatePaypal.setOnClickListener(this);
        butDonate.setOnClickListener(this);
        spinDonateAmount.setOnItemSelectedListener(this);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.initIabSetup();
    }

    @Override
    public void onDestroyView() {
        if (this.mIabHelper != null) {
            try {
                this.mIabHelper.dispose();
            } catch (Exception e) {

            }
            this.mIabHelper = null;
        }
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        if (v == butDonate) {
            this.startTransaction();
        } else if (v == butDonatePaypal) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(Constants.WEB_DONATE_URI));
            startActivity(intent);
            dismiss();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        tvStatus.setText(skuDescriptions.get(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void initIabSetup() {
        this.mIabHelper = new IabHelper(this.getActivity().getApplicationContext(), BuildConfig.GOOGLE_PLAY_API_KEY);
        this.mIabHelper.startSetup(this);
    }

    @Override
    public void onIabSetupFinished(IabResult result) {
        if (!result.isSuccess()) {
            butDonatePaypal.setVisibility(View.VISIBLE);
            butDonate.setVisibility(View.GONE);
        } else {
            butDonate.setEnabled(true);
            ArrayList<String> additionalSkuList = new ArrayList<>(Arrays.asList(this.SKU_DONATION));
            this.mIabHelper.queryInventoryAsync(true, additionalSkuList, this);
        }
    }

    @Override
    public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
        if (result.isFailure()) {
            return;
        }

        skuPrices = new ArrayList<>();
        skuDescriptions = new ArrayList<>();

        for (String skuDonation : this.SKU_DONATION) {
            skuPrices.add(inventory.getSkuDetails(skuDonation).getPrice());
            skuDescriptions.add(inventory.getSkuDetails(skuDonation).getDescription());
        }

        ArrayAdapter<String> donateAmountAdapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_list_item, skuPrices);
        spinDonateAmount.setAdapter(donateAmountAdapter);
        spinDonateAmount.setSelection(0, true);
    }

    @Override
    public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
        isPurchaseFlowing = false;
        if (this.mListener != null) {
            this.mListener.onDonateDialogResult(result.isSuccess());
        }
        this.dismiss();
    }

    public void startTransaction() {
        if (this.mIabHelper != null && !isPurchaseFlowing) {
            this.mIabHelper.launchPurchaseFlow(getActivity(), SKU_DONATION[spinDonateAmount.getSelectedItemPosition()], 10001, this, "donation_amount");
            isPurchaseFlowing = true;
        }
    }

    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        return this.mIabHelper.handleActivityResult(requestCode, resultCode, data);
    }
}
