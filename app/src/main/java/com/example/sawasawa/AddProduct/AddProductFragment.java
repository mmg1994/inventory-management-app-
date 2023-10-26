package com.example.sawasawa.AddProduct;


import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.example.sawasawa.MyConstants;
import com.example.sawasawa.R;
import com.example.sawasawa.ScanningActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddProductFragment extends Fragment {
    private static final int REQUEST_CODE_SCAN = 1;
    private EditText etQRCode, etName, etPrice, etExpDate;
    private Button btnScan, btnSave;
    private String qrcodeValue;


    private Spinner articleSpinner;
    private ArrayAdapter<String> articleAdapter;
    private ArrayList<String> articleList = new ArrayList<>();

    private TextView mUserIdTextView;

    private SharedPreferences mSharedPreferences;
    public AddProductFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_product, container, false);

        etQRCode = rootView.findViewById(R.id.et_qrcode);
        etName = rootView.findViewById(R.id.et_name);
        etPrice = rootView.findViewById(R.id.et_price);
        etExpDate = rootView.findViewById(R.id.et_exp_date);
        btnScan = rootView.findViewById(R.id.btn_scan);
        btnSave = rootView.findViewById(R.id.btn_save);

        // Initialize the Spinner
        articleSpinner = rootView.findViewById(R.id.articleSpinner);
        articleAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, articleList);
        articleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        articleSpinner.setAdapter(articleAdapter);

            //recupperation du username (inputter)

        mSharedPreferences = requireActivity().getSharedPreferences("my_app", Context.MODE_PRIVATE);

        mUserIdTextView = rootView.findViewById(R.id.user_id_text_view);

        // Retrieve the user ID from SharedPreferences
        String userId = mSharedPreferences.getString("user_id", "");

        // Display the user ID in the TextView
        mUserIdTextView.setText(userId);



        // Make a network request to retrieve the products
       // retrieveProducts();





        // Request article options from server using Volley
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        String url = MyConstants.API_BASE_URL + "get_articles_names.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String articleName = jsonObject.getString("name");
                        articleList.add(articleName);
                    }
                    articleAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(stringRequest);

        // Set an OnClickListener on the expiry date EditText field
        etExpDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the date picker dialog
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), expDateSetListener, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });




        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Launch the ScanningActivity to scan the QR code
                Intent intent = new Intent(getActivity(), ScanningActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SCAN);


            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             /*   // Get the user ID from SharedPreferences
                SharedPreferences prefs = getActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                int userId = prefs.getInt("user_id", -1);
                if (userId == -1) {
                    // User ID not found in SharedPreferences
                    Toast.makeText(getActivity(), "User ID not found", Toast.LENGTH_SHORT).show();
                    return;
                }       */


                String name = etName.getText().toString();
                String price = etPrice.getText().toString();
                String expDate = etExpDate.getText().toString();
                String articleName = articleSpinner.getSelectedItem().toString().trim();
                String inputter = mUserIdTextView.getText().toString();



                // Get the current date and time
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                Date currentDate = new Date();
                String dateString = dateFormat.format(currentDate);
                String time = timeFormat.format(currentDate);


                HashMap<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("price", price);
                params.put("exp_date", expDate);
                params.put("date", dateString);
                params.put("time", time);
                params.put("article_name", articleName);
                params.put("barcode", etQRCode.getText().toString());
                params.put("inputter", inputter);


                String url = MyConstants.API_BASE_URL +"add_product.php";
                //String url = "http://your-server-url.com/add_product.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(getActivity(), "Product added successfully", Toast.LENGTH_SHORT).show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getActivity(), "Error adding product: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return params;
                    }
                };
                Volley.newRequestQueue(getActivity()).add(stringRequest);
            }
        });

        return rootView;
    }

    // Set a listener for the expiry date DatePickerDialog
    private DatePickerDialog.OnDateSetListener expDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            Date date = calendar.getTime();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            etExpDate.setText(dateFormat.format(date));
        }
    };

    // Handle the result of the QR code scan
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ScanningActivity.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            qrcodeValue = data.getStringExtra(ScanningActivity.EXTRA_RESULT);
            etQRCode.setText(qrcodeValue);
        }
    }

}