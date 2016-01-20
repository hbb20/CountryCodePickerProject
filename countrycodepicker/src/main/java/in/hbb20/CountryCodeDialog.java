package in.hbb20;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

/**
 * Created by hbb20 on 11/1/16.
 */
class CountryCodeDialog {
    public static void openCountryCodeDialog(CountryCodePicker codePicker) {
        Context context=codePicker.getContext();
        final Dialog dialog = new Dialog(context);
        List<Country> filteredList = Country.getAllCountries(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setContentView(R.layout.layout_picker_dialog);
        RecyclerView recyclerView_countryDialog = (RecyclerView) dialog.findViewById(R.id.recycler_countryDialog);
        final EditText editText_search = (EditText) dialog.findViewById(R.id.editText_search);
        TextView textView_noResult = (TextView) dialog.findViewById(R.id.textView_noresult);
        final CountryCodeAdapter cca = new CountryCodeAdapter(context, filteredList, codePicker, editText_search, textView_noResult, dialog);
        recyclerView_countryDialog.setLayoutManager(new LinearLayoutManager(context));
        recyclerView_countryDialog.setAdapter(cca);
        dialog.show();
    }
}
