package com.example.smart_agriculture_deloitte.ui.slideshow;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.smart_agriculture_deloitte.FieldActionActivity;
import com.example.smart_agriculture_deloitte.LogInActivity;
import com.example.smart_agriculture_deloitte.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class SlideshowFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {





    private SlideshowViewModel slideshowViewModel;
    ListView alert_list;
    SwipeRefreshLayout swipeLayout;
    public static ArrayList<String> alertlist_content = new ArrayList<String>();
    ArrayList<String> date_time_data = new ArrayList<String>();
    public static int data_number = 20;

    FirebaseDatabase rootReference;
    DatabaseReference reference;

    DatabaseReference dateTimeReference, desirableCropsReference;                                                                         //string
    DatabaseReference airQualityReference, humidityReference, soilMoistureReference, soilphReference, temperatureReference;               //int
    DatabaseReference cameraDataAnalysisReference;
    DatabaseReference diseaseDetectionReference, weedpestDetectionReference, soilTextureReference, yieldPredictionReference;              //int

    public static float threshold_air = 80,    //less than
            threshold_ph = 7,                    //greater than
            threshold_moisture = 14,                //greater than
            threshold_temperature = 30,             //greater than
            threshold_humidity = 88;                //greater than





    @SuppressLint("ResourceAsColor")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });





        alert_list = root.findViewById(R.id.alert_list);

        rootReference = FirebaseDatabase.getInstance();
        reference = rootReference.getReference();

        dateTimeReference = reference.child("Date-Time");
        desirableCropsReference = reference.child("Desirable-Crop(s)");

        airQualityReference = reference.child("Air-Quality");
        humidityReference = reference.child("Humidity");
        soilMoistureReference = reference.child("Soil-Moisture");
        soilphReference = reference.child("Soil-pH");
        temperatureReference = reference.child("Temperature");

        cameraDataAnalysisReference = reference.child("Camera-Data-Analysis");

        diseaseDetectionReference = cameraDataAnalysisReference.child("Disease-Detection");
        weedpestDetectionReference = cameraDataAnalysisReference.child("Weed&PestsDetection");
        soilTextureReference = cameraDataAnalysisReference.child("Soil-Texture");
        yieldPredictionReference = cameraDataAnalysisReference.child("Yield-Prediction");





        swipeLayout = root.findViewById(R.id.swipeLayout);






        swipeLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) this);
        swipeLayout.setColorSchemeColors(
                (R.color.colorPrimary),
                (R.color.colorPrimaryDark),
                (R.color.colorAccent));

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_list_item_1, alertlist_content );
        alert_list.setAdapter(myAdapter);




        refreshData();
        alert_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );

                builder.setTitle("Take Action!");
                builder.setIcon(R.drawable.ic_alert);
                builder.setMessage( alertlist_content.get(position) +"\n"+"\n"+ "Take necessary action?" );

                builder.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                //log file
                                addNotification("ACTION DENIED!", alertlist_content.get(position), "Take necessary action");

                            }
                        });

                builder.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Intent intent = new Intent(getContext(), FieldActionActivity.class);
                                startActivity(intent);

                            }
                        });

                builder.setNeutralButton("CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //simply return
                            }
                        });


                builder.show();

            }
        });








        return root;
    }




    public void addNotification( String title, String body, String shortText ){


        NotificationManager mNotificationManager;

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext().getApplicationContext(), "notify_001");
        Intent ii = new Intent(getContext().getApplicationContext(), LogInActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText( body );
        bigText.setBigContentTitle( title );
        bigText.setSummaryText( shortText );

        mBuilder.setContentIntent( pendingIntent );
        mBuilder.setSmallIcon(R.drawable.ic_alert);
        mBuilder.setContentTitle( title );
        mBuilder.setContentText( body );
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        mNotificationManager =
                (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "notify_001";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    title,
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(0, mBuilder.build());


    }




    public void refreshData(){

        alertlist_content.removeAll(alertlist_content);
        date_time_data.removeAll(date_time_data);

        Query query = dateTimeReference.limitToLast(data_number);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                date_time_data.removeAll(date_time_data);

                int i=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    String temp = snapshot.getValue(String.class);
                    date_time_data.add(temp);
                    i++;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error loading data, Check Internet Connection!", Toast.LENGTH_LONG).show();
            }
        });



        Query air_query = airQualityReference.limitToLast(data_number);
        air_query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int i=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Float temp = snapshot.getValue(Float.class);

                    if(temp < (float)threshold_air){
                        alertlist_content.add( "Air Quality (" +temp+ ") less than " +threshold_air+ " at "+date_time_data.get(i)+ "." );
                    }

                    i++;
                }

                alert_list.invalidateViews();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error loading data, Check Internet Connection!", Toast.LENGTH_LONG).show();
            }
        });




        Query ph_query = soilphReference.limitToLast(data_number);
        ph_query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int i=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Float temp = snapshot.getValue(Float.class);

                    if(temp > (float)threshold_ph){
                        alertlist_content.add( "Soil pH (" +temp+ ") greater than " +threshold_ph+ " at "+date_time_data.get(i)+ "." );
                    }

                    i++;
                }

                alert_list.invalidateViews();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error loading data, Check Internet Connection!", Toast.LENGTH_LONG).show();
            }
        });





        Query moisture_query = soilMoistureReference.limitToLast(data_number);
        moisture_query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int i=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Float temp = snapshot.getValue(Float.class);

                    if(temp > (float)threshold_moisture){
                        alertlist_content.add( "Soil Moisture (" +temp+ ") greater than " +threshold_moisture+ " at "+date_time_data.get(i)+ "." );
                    }

                    i++;
                }

                alert_list.invalidateViews();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error loading data, Check Internet Connection!", Toast.LENGTH_LONG).show();
            }
        });




        Query temperature_query = temperatureReference.limitToLast(data_number);
        temperature_query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int i=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Float temp = snapshot.getValue(Float.class);

                    if(temp > (float)threshold_temperature){
                        alertlist_content.add( "Temperature (" +temp+ ") greater than " +threshold_temperature+ " at "+date_time_data.get(i)+ "." );
                    }

                    i++;
                }

                alert_list.invalidateViews();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error loading data, Check Internet Connection!", Toast.LENGTH_LONG).show();
            }
        });




        Query humidity_query = humidityReference.limitToLast(data_number);
        humidity_query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int i=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Float temp = snapshot.getValue(Float.class);

                    if(temp > (float)threshold_humidity){
                        alertlist_content.add( "Humidity (" +temp+ ") greater than " +threshold_humidity+ " at "+date_time_data.get(i)+ "." );
                    }

                    i++;
                }

                alert_list.invalidateViews();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error loading data, Check Internet Connection!", Toast.LENGTH_LONG).show();
            }
        });





    }






    @Override
    public void onRefresh() {


        refreshData();

        alert_list.invalidateViews();
        swipeLayout.setRefreshing(false);

    }






}