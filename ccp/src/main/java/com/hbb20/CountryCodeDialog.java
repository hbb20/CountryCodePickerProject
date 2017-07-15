package com.hbb20;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.futuremind.recyclerviewfastscroll.FastScroller;

import java.util.List;

/**
 * Created by hbb20 on 11/1/16.
 */
class CountryCodeDialog {
    public static void openCountryCodeDialog(CountryCodePicker codePicker) {
        Context context=codePicker.getContext();
        final Dialog dialog = new Dialog(context);
        codePicker.refreshCustomMasterList();
        codePicker.refreshPreferredCountries();
        List<Country> masterCountries = Country.getCustomMasterCountryList(context, codePicker);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setContentView(R.layout.layout_picker_dialog);

        //keyboard
        if (codePicker.isSearchAllowed() && codePicker.isDialogKeyboardAutoPopup()) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        //dialog views
        RecyclerView recyclerView_countryDialog = (RecyclerView) dialog.findViewById(R.id.recycler_countryDialog);
        final TextView textViewTitle=(TextView) dialog.findViewById(R.id.textView_title);
        RelativeLayout rlQueryHolder = (RelativeLayout) dialog.findViewById(R.id.rl_query_holder);
        ImageView imgClearQuery = (ImageView) dialog.findViewById(R.id.img_clear_query);
        final EditText editText_search = (EditText) dialog.findViewById(R.id.editText_search);
        TextView textView_noResult = (TextView) dialog.findViewById(R.id.textView_noresult);

        //add messages to views
        textViewTitle.setText(codePicker.getDialogTitle());
        editText_search.setHint(codePicker.getSearchHintText());
        textView_noResult.setText(codePicker.getNoResultFoundText());

        //this will make dialog compact
        if (!codePicker.isSearchAllowed()) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) recyclerView_countryDialog.getLayoutParams();
            params.height = RecyclerView.LayoutParams.WRAP_CONTENT;
            recyclerView_countryDialog.setLayoutParams(params);
        }

        final CountryCodeAdapter cca = new CountryCodeAdapter(context, masterCountries, codePicker, rlQueryHolder, editText_search, textView_noResult, dialog, imgClearQuery);
        recyclerView_countryDialog.setLayoutManager(new LinearLayoutManager(context));
        recyclerView_countryDialog.setAdapter(cca);

        //fast scroller
        FastScroller fastScroller = (FastScroller) dialog.findViewById(R.id.fastscroll);
        fastScroller.setRecyclerView(recyclerView_countryDialog);
        if (codePicker.isShowFastScroller()) {
            if (codePicker.getFastScrollerBubbleColor() != -1) {
                fastScroller.setBubbleColor(codePicker.getFastScrollerBubbleColor());
            }

            if (codePicker.getFastScrollerHandleColor() != -1) {
                fastScroller.setHandleColor(codePicker.getFastScrollerHandleColor());
            }

            if (codePicker.getFastScrollerBubbleTextAppearance() != -1) {
                try {
                    fastScroller.setBubbleTextAppearance(codePicker.getFastScrollerBubbleTextAppearance());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else {
            fastScroller.setVisibility(View.GONE);
        }


        dialog.show();
    }
}
