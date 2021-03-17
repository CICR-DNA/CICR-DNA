package fr.insalyon.mxyns.icrc.dna.data_gathering;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import fr.insalyon.mxyns.icrc.dna.R;

public class DataGatheringOnScreenChangeListener extends ViewPager.SimpleOnPageChangeListener {

    private final View left_swiper, right_swiper;
    private final FormScreenAdapter screenAdapter;
    private Toast currentlyDisplayedToast;

    public DataGatheringOnScreenChangeListener(View left_swiper, View right_swiper, FormScreenAdapter screenAdapter, Context context) {
        this.left_swiper = left_swiper;
        this.right_swiper = right_swiper;
        this.screenAdapter = screenAdapter;
        this.context = context;
    }

    private final Context context;

    @Override
    public void onPageSelected(int position) {
        left_swiper.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
        right_swiper.setVisibility(position == screenAdapter.getCount() - 1 ? View.INVISIBLE : View.VISIBLE);

        if (currentlyDisplayedToast != null && currentlyDisplayedToast.getView().getVisibility() == View.VISIBLE)
            currentlyDisplayedToast.cancel();

        if (screenAdapter.tabs.get(position) instanceof FormScreenFragment
                && ((FormScreenFragment) screenAdapter.tabs.get(position)).doesContainConditional()) {
            currentlyDisplayedToast = Toast.makeText(context, R.string.datagathering_warning_contains_conditional_fields, Toast.LENGTH_LONG);
            currentlyDisplayedToast.show();
        }

        Log.d("conditionally-enabled", "swiped to " + position);
        Fragment frag = screenAdapter.tabs.get(position);
        if (frag instanceof FormScreenFragment) {
            FormScreenFragment fragment = (FormScreenFragment) frag;
            Log.d("conditionally-enabled", "try to launch onStart " + position);
            fragment.onSwippedTo();
        }
    }
}
