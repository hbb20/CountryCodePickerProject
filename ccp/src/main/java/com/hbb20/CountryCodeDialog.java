package com.hbb20;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.futuremind.recyclerviewfastscroll.FastScroller;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by hbb20 on 11/1/16.
 */
class CountryCodeDialog {
    private static final Field
            sEditorField,
            sCursorDrawableField,
            sCursorDrawableResourceField;
    static Dialog dialog;
    static Context context;

    static {
        Field editorField = null;
        Field cursorDrawableField = null;
        Field cursorDrawableResourceField = null;
        boolean exceptionThrown = false;
        try {
            cursorDrawableResourceField = TextView.class.getDeclaredField("mCursorDrawableRes");
            cursorDrawableResourceField.setAccessible(true);
            final Class<?> drawableFieldClass;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                drawableFieldClass = TextView.class;
            } else {
                editorField = TextView.class.getDeclaredField("mEditor");
                editorField.setAccessible(true);
                drawableFieldClass = editorField.getType();
            }
            cursorDrawableField = drawableFieldClass.getDeclaredField("mCursorDrawable");
            cursorDrawableField.setAccessible(true);
        } catch (Exception e) {
            exceptionThrown = true;
        }
        if (exceptionThrown) {
            sEditorField = null;
            sCursorDrawableField = null;
            sCursorDrawableResourceField = null;
        } else {
            sEditorField = editorField;
            sCursorDrawableField = cursorDrawableField;
            sCursorDrawableResourceField = cursorDrawableResourceField;
        }
    }

    public static void openCountryCodeDialog(final CountryCodePicker codePicker) {
        openCountryCodeDialog(codePicker, null);
    }

    public static void
    openCountryCodeDialog(final CountryCodePicker codePicker, final String countryNameCode) {
        context = codePicker.getContext();
        dialog = new Dialog(context);
        codePicker.refreshCustomMasterList();
        codePicker.refreshPreferredCountries();
        List<CCPCountry> masterCountries = CCPCountry.getCustomMasterCountryList(context, codePicker);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setContentView(R.layout.layout_picker_dialog);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, android.R.color.transparent));


        //dialog views
        RecyclerView recyclerView_countryDialog = (RecyclerView) dialog.findViewById(R.id.recycler_countryDialog);
        ImageView imgClearQuery = (ImageView) dialog.findViewById(R.id.img_search);
        final EditText editText_search = (EditText) dialog.findViewById(R.id.editText_search);
        TextView textView_noResult = (TextView) dialog.findViewById(R.id.textView_noresult);
        CardView dialogRoot = (CardView) dialog.findViewById(R.id.cardViewRoot);
        ImageView imgDismiss = (ImageView) dialog.findViewById(R.id.img_dismiss);

        //keyboard
        if (codePicker.isSearchAllowed() && codePicker.isDialogKeyboardAutoPopup()) {
            editText_search.requestFocus();
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        } else {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
        // type faces
        //set type faces
        try {
            if (codePicker.getDialogTypeFace() != null) {
                if (codePicker.getDialogTypeFaceStyle() != CountryCodePicker.DEFAULT_UNSET) {
                    textView_noResult.setTypeface(codePicker.getDialogTypeFace(), codePicker.getDialogTypeFaceStyle());
                } else {
                    textView_noResult.setTypeface(codePicker.getDialogTypeFace());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //dialog background color
        if (codePicker.getDialogBackgroundColor() != 0) {
            dialogRoot.setCardBackgroundColor(codePicker.getDialogBackgroundColor());
        }

        if (codePicker.getDialogBackgroundResId() != 0) {
            dialogRoot.setBackgroundResource(codePicker.getDialogBackgroundResId());
        }

        dialogRoot.setRadius(codePicker.getDialogCornerRadius());

        //close button visibility
        if (codePicker.isShowCloseIcon()) {
            imgDismiss.setVisibility(View.VISIBLE);
            imgDismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        } else {
            imgDismiss.setVisibility(View.GONE);
        }

        //title
        if (!codePicker.getCcpDialogShowTitle()) {
            textViewTitle.setVisibility(View.GONE);
        }

        //clear button color and title color
        if (codePicker.getDialogTextColor() != 0) {
            int textColor = codePicker.getDialogTextColor();
            iv_close.setColorFilter(textColor);
            textView_noResult.setTextColor(textColor);
        }

        //add messages to views
        editText_search.setHint(codePicker.searchHint);
        textView_noResult.setText(codePicker.noResultText);

        //this will make dialog compact
        if (!codePicker.isSearchAllowed()) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) recyclerView_countryDialog.getLayoutParams();
            params.height = RecyclerView.LayoutParams.WRAP_CONTENT;
            recyclerView_countryDialog.setLayoutParams(params);
        }

        final CountryCodeAdapter cca = new CountryCodeAdapter(context, masterCountries, codePicker, editText_search, textView_noResult, dialog, imgClearQuery);
        recyclerView_countryDialog.setLayoutManager(new LinearLayoutManager(context));
        recyclerView_countryDialog.setAdapter(cca);

        //fast scroller
        FastScroller fastScroller = (FastScroller) dialog.findViewById(R.id.fastScroll);
        fastScroller.setRecyclerView(recyclerView_countryDialog);
        if (codePicker.isShowFastScroller()) {
            if (codePicker.getFastScrollerBubbleColor() != 0) {
                fastScroller.setBubbleColor(codePicker.getFastScrollerBubbleColor());
            }

            if (codePicker.getFastScrollerHandleColor() != 0) {
                fastScroller.setHandleColor(codePicker.getFastScrollerHandleColor());
            }

            if (codePicker.getFastScrollerBubbleTextAppearance() != 0) {
                try {
                    fastScroller.setBubbleTextAppearance(codePicker.getFastScrollerBubbleTextAppearance());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else {
            fastScroller.setVisibility(View.GONE);
        }

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                hideKeyboard(context);
                if (codePicker.getDialogEventsListener() != null) {
                    codePicker.getDialogEventsListener().onCcpDialogDismiss(dialogInterface);
                }
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                hideKeyboard(context);
                if (codePicker.getDialogEventsListener() != null) {
                    codePicker.getDialogEventsListener().onCcpDialogCancel(dialogInterface);
                }
            }
        });

        //auto scroll to mentioned countryNameCode
        if (countryNameCode != null) {
            boolean isPreferredCountry = false;
            if (codePicker.preferredCountries != null) {
                for (CCPCountry preferredCountry : codePicker.preferredCountries) {
                    if (preferredCountry.nameCode.equalsIgnoreCase(countryNameCode)) {
                        isPreferredCountry = true;
                        break;
                    }
                }
            }

            //if selection is from preferred countries then it should show all (or maximum) preferred countries.
            // don't scroll if it was one of those preferred countries
            if (!isPreferredCountry) {
                int preferredCountriesOffset = 0;
                if (codePicker.preferredCountries != null && codePicker.preferredCountries.size() > 0) {
                    preferredCountriesOffset = codePicker.preferredCountries.size() + 1; //+1 is for divider
                }
                for (int i = 0; i < masterCountries.size(); i++) {
                    if (masterCountries.get(i).nameCode.equalsIgnoreCase(countryNameCode)) {
                        recyclerView_countryDialog.scrollToPosition(i + preferredCountriesOffset);
                        break;
                    }
                }
            }
        }

        dialog.show();
        if (codePicker.getDialogEventsListener() != null) {
            codePicker.getDialogEventsListener().onCcpDialogOpen(dialog);
        }
    }

    private static void hideKeyboard(Context context) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    static void setCursorColor(EditText editText, int color) {
        if (sCursorDrawableField == null) {
            return;
        }
        try {
            final Drawable drawable = getDrawable(editText.getContext(),
                    sCursorDrawableResourceField.getInt(editText));
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            sCursorDrawableField.set(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN
                    ? editText : sEditorField.get(editText), new Drawable[]{drawable, drawable});
        } catch (Exception ignored) {

        }
    }

    static void clear() {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = null;
        context = null;
    }

    private static Drawable getDrawable(Context context, int id) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return context.getResources().getDrawable(id);
        } else {
            return context.getDrawable(id);
        }
    }
}
