package com.example.smart_agriculture_deloitte.ui.send;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.smart_agriculture_deloitte.R;
import com.example.smart_agriculture_deloitte.ui.home.HomeFragment;
import com.example.smart_agriculture_deloitte.ui.slideshow.SlideshowFragment;


public class SendFragment extends Fragment {


    private SendViewModel sendViewModel;

    HomeFragment homeFragment;

    EditText input_number;
    Button input_number_button;
    static Spinner graph_spinner;

    EditText input_air_number,
    input_ph_number,
    input_moisture_number,
    input_temperature_number,
    input_humidity_number;

    Button input_air_number_button,
    input_ph_number_button,
    input_moisture_number_button,
    input_temperature_number_button,
    input_humidity_number_button;



    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sendViewModel =
                ViewModelProviders.of(this).get(SendViewModel.class);
        View root = inflater.inflate(R.layout.fragment_send, container, false);

        sendViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });




        input_number = root.findViewById(R.id.input_number);
        input_number.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(2)});
        input_number_button = root.findViewById(R.id.input_number_button);

        graph_spinner = root.findViewById(R.id.graph_spinner);

        input_air_number = root.findViewById(R.id.input_air_number);
        input_ph_number = root.findViewById(R.id.input_ph_number);
        input_moisture_number = root.findViewById(R.id.input_moisture_number);
        input_temperature_number = root.findViewById(R.id.input_temperature_number);
        input_humidity_number = root.findViewById(R.id.input_humidity_number);

        input_air_number_button = root.findViewById(R.id.input_air_number_button);
        input_ph_number_button = root.findViewById(R.id.input_ph_number_button);
        input_moisture_number_button = root.findViewById(R.id.input_moisture_number_button);
        input_temperature_number_button = root.findViewById(R.id.input_temperature_number_button);
        input_humidity_number_button = root.findViewById(R.id.input_humidity_number_button);






        input_number_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( input_number.getText().toString().equals("") ){
                    Toast.makeText(getActivity(), "Please Enter A Number!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int temp = Integer.parseInt( input_number.getText().toString() );
                if(temp>10){
                    SlideshowFragment.data_number = temp;
                    homeFragment.number_of_data = temp;
                }
                else{
                    homeFragment.number_of_data = temp;
                }

                Toast.makeText(getActivity(), "Number of Input Data set to "+homeFragment.number_of_data+"!", Toast.LENGTH_SHORT).show();

            }
        });

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.graph_type));

        myAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        graph_spinner.setAdapter(myAdapter);


        graph_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItemText = (String) parentView.getItemAtPosition(position);


                if( selectedItemText.equals("Bar Graph") ){
                    homeFragment.graph_type_bar = true;
                }
                else if( selectedItemText.equals("Line Graph") ){
                    homeFragment.graph_type_bar = false;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        input_air_number_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( input_air_number.getText().toString().equals("") ){
                    Toast.makeText(getActivity(), "Please Enter A Number!", Toast.LENGTH_SHORT).show();
                    return;
                }

                SlideshowFragment.threshold_air = Float.parseFloat( input_air_number.getText().toString() );
                Toast.makeText(getActivity(), "Air Quality threshold set to "+SlideshowFragment.threshold_air+"!", Toast.LENGTH_SHORT).show();
            }
        });

        input_ph_number_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( input_ph_number.getText().toString().equals("") ){
                    Toast.makeText(getActivity(), "Please Enter A Number!", Toast.LENGTH_SHORT).show();
                    return;
                }

                SlideshowFragment.threshold_ph = Float.parseFloat( input_ph_number.getText().toString() );
                Toast.makeText(getActivity(), "Soil pH threshold set to "+SlideshowFragment.threshold_ph+"!", Toast.LENGTH_SHORT).show();
            }
        });

        input_moisture_number_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( input_moisture_number.getText().toString().equals("") ){
                    Toast.makeText(getActivity(), "Please Enter A Number!", Toast.LENGTH_SHORT).show();
                    return;
                }

                SlideshowFragment.threshold_moisture = Float.parseFloat( input_moisture_number.getText().toString() );
                Toast.makeText(getActivity(), "Soil Moisture threshold set to "+SlideshowFragment.threshold_moisture+"!", Toast.LENGTH_SHORT).show();
            }
        });

        input_temperature_number_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( input_temperature_number.getText().toString().equals("") ){
                    Toast.makeText(getActivity(), "Please Enter A Number!", Toast.LENGTH_SHORT).show();
                    return;
                }

                SlideshowFragment.threshold_temperature = Float.parseFloat( input_temperature_number.getText().toString() );
                Toast.makeText(getActivity(), "Temperature threshold set to "+SlideshowFragment.threshold_temperature+"!", Toast.LENGTH_SHORT).show();
            }
        });

        input_humidity_number_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( input_humidity_number.getText().toString().equals("") ){
                    Toast.makeText(getActivity(), "Please Enter A Number!", Toast.LENGTH_SHORT).show();
                    return;
                }

                SlideshowFragment.threshold_humidity = Float.parseFloat( input_humidity_number.getText().toString() );
                Toast.makeText(getActivity(), "Humidity threshold set to "+SlideshowFragment.threshold_humidity+"!", Toast.LENGTH_SHORT).show();
            }
        });






        return root;
    }
}