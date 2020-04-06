package com.example.smart_agriculture_deloitte.ui.gallery;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.smart_agriculture_deloitte.R;

import java.util.ArrayList;

import static androidx.annotation.InspectableProperty.ValueType.GRAVITY;


public class GalleryFragment extends Fragment {



    private GalleryViewModel galleryViewModel;
    static LinearLayout notification_layout;
    Button add_noti_button;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });




        notification_layout = root.findViewById(R.id.notifocation_layout);
        add_noti_button = root.findViewById(R.id.add_noti_button);




        add_noti_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final TextView temp = new TextView(getContext());
                Resources r = getContext().getResources();
                int height = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        65,
                        r.getDisplayMetrics()
                );
                final LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height );
                int margin_top = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        30,
                        r.getDisplayMetrics()
                );
                layoutParams.setMargins(0,margin_top,0,0);
                temp.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.red_notificationtextview));
                temp.setLayoutParams(layoutParams);
                temp.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                temp.setText("Sample Notification");
                temp.setTextColor(ContextCompat.getColor(getContext(), R.color.deloitteBlack));
                notification_layout.addView(temp, layoutParams);

                temp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        temp.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.green_notificationtextview));
                        temp.setLayoutParams(layoutParams);
                    }
                });

            }
        });




        return root;
    }


}