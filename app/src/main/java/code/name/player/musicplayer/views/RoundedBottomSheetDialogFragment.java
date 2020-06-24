package code.name.player.musicplayer.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatDialogFragment;
import code.name.player.appthemehelper.ThemeStore;
import code.name.player.appthemehelper.util.VersionUtils;
import code.name.player.musicplayer.R;
import code.name.player.musicplayer.ui.activities.base.AbsBaseActivity;
import code.name.player.musicplayer.util.RetroUtil;

/**
 * Created by yu on 2016/11/10.
 */
@SuppressLint("RestrictedApi")
public class RoundedBottomSheetDialogFragment extends AppCompatDialogFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
            FrameLayout bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheet.setBackground(RetroUtil.getTintedDrawable(getContext(), R.drawable.bg_bottom_sheet_dialog_fragment, ThemeStore.Companion.primaryColor(getContext())));
            }
        });
        if (getActivity() != null) {
            if (VersionUtils.INSTANCE.hasNougat()) {
                ((AbsBaseActivity) getActivity()).setNavigationbarColor(ThemeStore.Companion.primaryColor(getContext()));
            } else {
                ((AbsBaseActivity) getActivity()).setNavigationbarColorAuto();
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //noinspection ConstantConditions
        return new CustomWidthBottomSheetDialog(getContext(), getTheme());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setNavigationBarColor(ThemeStore.Companion.primaryColor(getContext()));
            window.findViewById(com.google.android.material.R.id.container).setFitsSystemWindows(true);
        }
    }

    static class CustomWidthBottomSheetDialog extends BottomSheetDialog {
        CustomWidthBottomSheetDialog(@NonNull Context context, @StyleRes int theme) {
            super(context, theme);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            int width = getContext().getResources().getDimensionPixelSize(R.dimen.bottom_sheet_width);
            Objects.requireNonNull(getWindow()).setLayout(width > 0 ? width : ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }
}