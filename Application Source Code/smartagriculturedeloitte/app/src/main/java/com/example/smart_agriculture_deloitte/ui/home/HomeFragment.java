package com.example.smart_agriculture_deloitte.ui.home;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.smart_agriculture_deloitte.FieldActionActivity;
import com.example.smart_agriculture_deloitte.LogInActivity;
import com.example.smart_agriculture_deloitte.UserDetailsActivity;
import com.example.smart_agriculture_deloitte.ui.slideshow.SlideshowFragment;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import com.example.smart_agriculture_deloitte.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;



public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener  {



    private HomeViewModel homeViewModel;

    FirebaseDatabase rootReference;
    DatabaseReference reference;

    DatabaseReference dateTimeReference, desirableCropsReference;                                                                         //string
    DatabaseReference airQualityReference, humidityReference, soilMoistureReference, soilphReference, temperatureReference;               //int
    DatabaseReference cameraDataAnalysisReference;
    DatabaseReference diseaseDetectionReference, weedpestDetectionReference, soilTextureReference, yieldPredictionReference;              //int

    public static Spinner spinner;
    public BarChart barChart;
    public LineChart lineChart;
    public static int number_of_data = 10;
    public static boolean graph_type_bar = true;
    String selectedItemText;
    static TextView bar_status;
    SwipeRefreshLayout livedatarefresh;



    ArrayList<String> date_time_data = new ArrayList<String>();
    ArrayList air_quality_data = new ArrayList();
    ArrayList humidity_data = new ArrayList();
    ArrayList soil_moisture_data = new ArrayList();
    ArrayList soil_ph_data = new ArrayList();
    ArrayList temperature_data = new ArrayList();

    ArrayList disease_detection_data = new ArrayList();
    ArrayList weedpest_detection_data = new ArrayList();
    ArrayList soil_texture_data = new ArrayList();
    ArrayList yield_prediction_data = new ArrayList();




    @SuppressLint("ResourceAsColor")
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);



        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });




        spinner = root.findViewById(R.id.spinner);
        bar_status = root.findViewById(R.id.bar_status);

        livedatarefresh = root.findViewById(R.id.livedatarefresh);

        barChart = root.findViewById(R.id.barchart);
        lineChart = root.findViewById(R.id.lineChart);

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







        livedatarefresh.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) this);
        livedatarefresh.setColorSchemeColors(
                (R.color.colorPrimary),
                (R.color.colorPrimaryDark),
                (R.color.colorAccent));

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.field_data_names));

        myAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinner.setAdapter(myAdapter);






        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedItemText = (String) parentView.getItemAtPosition(position);

                getDateTimeData();

                //air quality
                if(selectedItemText.equals("Air Quality")){
                    checkInternet();
                    getAirQualityData(graph_type_bar);
                }
                else if(selectedItemText.equals("Humidity")){
                    checkInternet();
                    getHumidityData(graph_type_bar);
                }
                else if(selectedItemText.equals("Soil Moisture")){
                    checkInternet();
                    getSoilMoistureData(graph_type_bar);
                }
                else if(selectedItemText.equals("Soil pH")){
                    checkInternet();
                    getSoilphData(graph_type_bar);
                }
                else if(selectedItemText.equals("Temperature")){
                    checkInternet();
                    getTemperatureData(graph_type_bar);
                }
                else if(selectedItemText.equals("Prediction Models")){
                    checkInternet();
                    getPredictionData();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });



        return root;
    }




 public void setLineChart(ArrayList array_name){

        bar_status.setText("");

        LineDataSet set1 = new LineDataSet(array_name, selectedItemText+" Index");

        set1.setFillAlpha(110);
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(true);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1);

        LineData data = new LineData(date_time_data, dataSets);

        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                bar_status.setText( selectedItemText + " is " + e.getVal() + " at " + date_time_data.get(e.getXIndex()) + "." );
            }

            @Override
            public void onNothingSelected() {

            }
        });

        lineChart.setData(data);
        lineChart.invalidate();
        Legend l = lineChart.getLegend();

        l.setForm(Legend.LegendForm.LINE);
        lineChart.setVisibility(View.VISIBLE);

    }







    public void setBarChart(ArrayList array_name){

        bar_status.setText("");

        BarDataSet bardataset = new BarDataSet(array_name, selectedItemText+" Index");
        barChart.animateY(1000);
        BarData data = new BarData(date_time_data, bardataset);

        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        barChart.setDrawBarShadow(true);

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                bar_status.setText( selectedItemText + " is " + e.getVal() + " at " + date_time_data.get(e.getXIndex()) + "." );
            }

            @Override
            public void onNothingSelected() {

            }
        });

        barChart.setData(data);
        barChart.setVisibility(View.VISIBLE);
    }






    public void getSoilMoistureData(final boolean setgraph){

        Query query = soilMoistureReference.limitToLast(number_of_data);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                soil_moisture_data.removeAll(soil_moisture_data);

                int i=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Float temp = snapshot.getValue(Float.class);

                    if( graph_type_bar ) {
                        soil_moisture_data.add(new BarEntry( temp, i));
                    }
                    else{
                        soil_moisture_data.add( new Entry( temp , i));
                    }

                    i++;
                }

                if(date_time_data.size()==0){
                    Toast.makeText(getActivity(), "Error loading data, Check Internet Connection!", Toast.LENGTH_LONG).show();
                }
                else if(setgraph){
                    lineChart.setVisibility(View.INVISIBLE);
                    setBarChart(soil_moisture_data);
                }
                else{
                    barChart.setVisibility(View.INVISIBLE);
                    setLineChart(soil_moisture_data);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error loading data, Check Internet Connection!", Toast.LENGTH_LONG).show();
            }
        });

    }









    public void getTemperatureData(final boolean setgraph){

        Query query = temperatureReference.limitToLast(number_of_data);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                temperature_data.removeAll(temperature_data);

                int i=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Float temp = snapshot.getValue(Float.class);

                    if( graph_type_bar ) {
                        temperature_data.add( new BarEntry(temp, i) );
                    }
                    else{
                        temperature_data.add( new Entry(temp, i) );
                    }

                    i++;
                }

                if(date_time_data.size()==0){
                    Toast.makeText(getActivity(), "Error loading data, Check Internet Connection!", Toast.LENGTH_LONG).show();
                }
                else if(setgraph){
                    lineChart.setVisibility(View.INVISIBLE);
                    setBarChart(temperature_data);
                }
                else{
                    barChart.setVisibility(View.INVISIBLE);
                    setLineChart(temperature_data);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error loading data, Check Internet Connection!", Toast.LENGTH_LONG).show();
            }
        });

    }






    public void getSoilphData(final boolean setgraph){

        Query query = soilphReference.limitToLast(number_of_data);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                soil_ph_data.removeAll(soil_ph_data);

                int i=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Float temp = snapshot.getValue(Float.class);

                    if( graph_type_bar ) {
                        soil_ph_data.add( new BarEntry(temp, i) );
                    }
                    else{
                        soil_ph_data.add( new Entry(temp, i) );
                    }

                    i++;
                }

                if(date_time_data.size()==0){
                    Toast.makeText(getActivity(), "Error loading data, Check Internet Connection!", Toast.LENGTH_LONG).show();
                }
                else if(setgraph){
                    lineChart.setVisibility(View.INVISIBLE);
                    setBarChart(soil_ph_data);
                }
                else{
                    barChart.setVisibility(View.INVISIBLE);
                    setLineChart(soil_ph_data);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error loading data, Check Internet Connection!", Toast.LENGTH_LONG).show();
            }
        });

    }






    public void getHumidityData(final boolean setgraph){

        Query query = humidityReference.limitToLast(number_of_data);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                humidity_data.removeAll(humidity_data);

                int i=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Float temp = snapshot.getValue(Float.class);

                    if( graph_type_bar ) {
                        humidity_data.add( new BarEntry(temp, i) );
                    }
                    else{
                        humidity_data.add( new Entry(temp, i ));
                    }


                    i++;
                }

                if(date_time_data.size()==0){
                    Toast.makeText(getActivity(), "Error loading data, Check Internet Connection!", Toast.LENGTH_LONG).show();
                }
                else if(setgraph){
                    lineChart.setVisibility(View.INVISIBLE);
                    setBarChart(humidity_data);
                }
                else{
                    barChart.setVisibility(View.INVISIBLE);
                    setLineChart(humidity_data);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error loading data, Check Internet Connection!", Toast.LENGTH_LONG).show();
            }
        });

    }






    public void getAirQualityData(final boolean setgraph){

        Query query = airQualityReference.limitToLast(number_of_data);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                air_quality_data.removeAll(air_quality_data);

                int i=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Float temp = snapshot.getValue(Float.class);

                    if( graph_type_bar ) {
                        air_quality_data.add( new BarEntry(temp, i) );
                    }
                    else{
                        air_quality_data.add( new Entry( temp, i ) );
                    }

                    i++;
                }

                if(date_time_data.size()==0){
                    Toast.makeText(getActivity(), "Error loading data, Check Internet Connection!", Toast.LENGTH_LONG).show();
                }
                else if(setgraph){
                    lineChart.setVisibility(View.INVISIBLE);
                    setBarChart(air_quality_data);
                }
                else{
                    barChart.setVisibility(View.INVISIBLE);
                    setLineChart(air_quality_data);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error loading data, Check Internet Connection!", Toast.LENGTH_LONG).show();
            }
        });

    }







    public void getDateTimeData(){

        Query query = dateTimeReference.limitToLast(number_of_data);
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

    }




    public void getPredictionData(){


        barChart.setVisibility(View.INVISIBLE);
        lineChart.setVisibility(View.VISIBLE);

        Query query = diseaseDetectionReference.limitToLast(number_of_data);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                disease_detection_data.removeAll(disease_detection_data);

                int i=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Float temp = snapshot.getValue(Float.class);

                    disease_detection_data.add( new Entry( temp, i ) );

                    i++;
                }

                if(date_time_data.size()==0){
                    Toast.makeText(getActivity(), "Error loading data, Check Internet Connection!", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error loading data, Check Internet Connection!", Toast.LENGTH_LONG).show();
            }
        });



        Query query1 = weedpestDetectionReference.limitToLast(number_of_data);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                weedpest_detection_data.removeAll(weedpest_detection_data);

                int i=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Float temp = snapshot.getValue(Float.class);

                    weedpest_detection_data.add( new Entry( temp, i ) );

                    i++;
                }

                if(date_time_data.size()==0){
                    Toast.makeText(getActivity(), "Error loading data, Check Internet Connection!", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error loading data, Check Internet Connection!", Toast.LENGTH_LONG).show();
            }
        });



        Query query2 = soilTextureReference.limitToLast(number_of_data);
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                soil_texture_data.removeAll(soil_texture_data);

                int i=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Float temp = snapshot.getValue(Float.class);

                    soil_texture_data.add( new Entry( temp, i ) );

                    i++;
                }

                if(date_time_data.size()==0){
                    Toast.makeText(getActivity(), "Error loading data, Check Internet Connection!", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error loading data, Check Internet Connection!", Toast.LENGTH_LONG).show();
            }
        });




        Query query3 = yieldPredictionReference.limitToLast(number_of_data);
        query3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                yield_prediction_data.removeAll(yield_prediction_data);

                int i=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Float temp = snapshot.getValue(Float.class);

                    yield_prediction_data.add( new Entry( temp, i ) );

                    i++;
                }

                if(date_time_data.size()==0){
                    Toast.makeText(getActivity(), "Error loading data, Check Internet Connection!", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error loading data, Check Internet Connection!", Toast.LENGTH_LONG).show();
            }
        });


        setPredictionLineChart();

    }



    public void setPredictionLineChart(){



        LineDataSet set1 = new LineDataSet(disease_detection_data, "Disease Detection");
        LineDataSet set2 = new LineDataSet(weedpest_detection_data, "Weed & Pests Detection");
        LineDataSet set3 = new LineDataSet(soil_texture_data, "Soil Texture");
        LineDataSet set4 = new LineDataSet(yield_prediction_data, "Yield Prediction");

        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);

        set2.setColor(Color.RED);
        set2.setCircleColor(Color.RED);
        set2.setLineWidth(1f);
        set2.setCircleRadius(3f);
        set2.setDrawCircleHole(false);
        set2.setValueTextSize(9f);

        set3.setColor(Color.GREEN);
        set3.setCircleColor(Color.GREEN);
        set3.setLineWidth(1f);
        set3.setCircleRadius(3f);
        set3.setDrawCircleHole(false);
        set3.setValueTextSize(9f);

        set4.setColor(Color.BLUE);
        set4.setCircleColor(Color.BLUE);
        set4.setLineWidth(1f);
        set4.setCircleRadius(3f);
        set4.setDrawCircleHole(false);
        set4.setValueTextSize(9f);


        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1);
        dataSets.add(set2);
        dataSets.add(set3);
        dataSets.add(set4);

        LineData data = new LineData(date_time_data, dataSets);

        lineChart.setDescription("Prediction Models data");

        lineChart.setData(data);
        lineChart.invalidate();
        Legend l = lineChart.getLegend();

        l.setForm(Legend.LegendForm.LINE);
        lineChart.setVisibility(View.VISIBLE);


    }


    @Override
    public void onRefresh() {


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedItemText = (String) parentView.getItemAtPosition(position);

                getDateTimeData();

                //air quality
                if(selectedItemText.equals("Air Quality")){
                    checkInternet();
                    getAirQualityData(graph_type_bar);
                }
                else if(selectedItemText.equals("Humidity")){
                    checkInternet();
                    getHumidityData(graph_type_bar);
                }
                else if(selectedItemText.equals("Soil Moisture")){
                    checkInternet();
                    getSoilMoistureData(graph_type_bar);
                }
                else if(selectedItemText.equals("Soil pH")){
                    checkInternet();
                    getSoilphData(graph_type_bar);
                }
                else if(selectedItemText.equals("Temperature")){
                    checkInternet();
                    getTemperatureData(graph_type_bar);
                }
                else if(selectedItemText.equals("Prediction Models")){
                    checkInternet();
                    getPredictionData();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        livedatarefresh.setRefreshing(false);

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




    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }




    public void checkInternet(){

        boolean temp = isInternetAvailable();

        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true);
            mobileDataEnabled = (Boolean)method.invoke(cm);
        } catch (Exception e) {
            // TODO do whatever error handling you want here
        }

        if(temp==true || mobileDataEnabled==true ){
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    selectedItemText = (String) parentView.getItemAtPosition(position);

                    getDateTimeData();

                    //air quality
                    if(selectedItemText.equals("Air Quality")){
                        getAirQualityData(graph_type_bar);
                    }
                    else if(selectedItemText.equals("Humidity")){
                        getHumidityData(graph_type_bar);
                    }
                    else if(selectedItemText.equals("Soil Moisture")){
                        getSoilMoistureData(graph_type_bar);
                    }
                    else if(selectedItemText.equals("Soil pH")){
                        getSoilphData(graph_type_bar);
                    }
                    else if(selectedItemText.equals("Temperature")){
                        getTemperatureData(graph_type_bar);
                    }
                    else if(selectedItemText.equals("Prediction Models")){
                        getPredictionData();
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }
            });
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );

            builder.setTitle("Internet is off!");
            builder.setIcon(R.drawable.ic_alert);
            builder.setMessage( "You need to turn ON internet to see the data." +"\n"+"\n" + "Go to settings?" );

            builder.setNegativeButton("NO",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getActivity(), "You won't be able to see data.", Toast.LENGTH_LONG).show();

                        }
                    });

            builder.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                        }
                    });

            builder.show();
        }

    }





}