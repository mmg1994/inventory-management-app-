package com.example.sawasawa.homeFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.sawasawa.MyConstants;
import com.example.sawasawa.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import android.graphics.Color;


public class HomeFragment extends Fragment {

    private TextView mProductCountTextView;
    private TextView mSaleCountTextView;
    private BarChart mBarChart;

    private ArrayList<BarEntry> mSalesData = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize the TextViews
        mProductCountTextView = view.findViewById(R.id.product_count_textview);
        mSaleCountTextView = view.findViewById(R.id.sale_count_textview);

        // Initialize the BarChart view
        mBarChart = view.findViewById(R.id.bar_chart);
        // Configure the BarChart view
        mBarChart.setDrawGridBackground(false);
        mBarChart.getDescription().setEnabled(false);
        mBarChart.setTouchEnabled(true);
        mBarChart.setDragEnabled(true);
        mBarChart.setScaleEnabled(true);
        mBarChart.setPinchZoom(true);
        mBarChart.setDrawBorders(true);
        mBarChart.setBorderColor(Color.BLACK);
        mBarChart.setBorderWidth(1f);

        // Configure the X axis
        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // intervalles de 1 jour
        xAxis.setLabelCount(7); // 7 jours maximum affichés
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("dd-MM", Locale.FRANCE);

            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                // Convert the X axis value to a Date object
                Date date = new Date((long) value);
                // Format the date as a string
                return mFormat.format(date);
            }
        });

        // Configure the Y axis
        YAxis yAxis = mBarChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.FRANCE, "%.2f Fbu", value);
            }
        });

        // Add a legend
        Legend legend = mBarChart.getLegend();
        legend.setEnabled(false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set up the Volley request queue
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        // Make a Volley request to retrieve the sales data
        String saleUrl = MyConstants.API_BASE_URL + "sales_diagram.php";
        JsonArrayRequest saleRequest = new JsonArrayRequest(Request.Method.GET, saleUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Iterate over the JSON array and add each data point to the mSalesData list
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject sale = response.getJSONObject(i);
                                double revenue = sale.getDouble("total_price");
                                String dateString = sale.getString("date");
                                // Parse the date string into a Date object
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                Date date = dateFormat.parse(dateString);
                                // Get the timestamp of the Date
                                long timestamp = date.getTime();
                                // Add a new BarEntry object to the mSalesData list
                                mSalesData.add(new BarEntry(timestamp, (float) revenue));
                            }
                            // Update the chart with the new data
                            updateBarChart();
                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        queue.add(saleRequest);
    }
    private static final int[] BAR_COLORS = new int[] {
            Color.rgb(104, 241, 175),
            Color.rgb(164, 228, 251),
            Color.rgb(242, 247, 158),
            Color.rgb(255, 102, 0),
            Color.rgb(247, 164, 184)
    };
    private void updateBarChart() {
        // Create a BarDataSet object from the mSalesData list
        BarDataSet dataSet = new BarDataSet(mSalesData, "Ventes");
     //   dataSet.setDrawValues(false);


        // Set the colors of the bars manually
        for (int i = 0; i < mSalesData.size(); i++) {
            int color = BAR_COLORS[i % BAR_COLORS.length];
            //dataSet.setBarBorderColor(i, color);
            // dataSet.setBarFillColor(i, color);
            dataSet.setColors(i, color);
        }

      //  dataSet.setColors(BAR_COLORS);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.FRANCE, "%.2f Fbu", value);
            }
        });
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);

        // Create a BarData object from the BarDataSet object
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);

        // Set the BarData object to the BarChart view
        mBarChart.setData(barData);

        // Calculate the total sales revenue
        double totalRevenue = 0;
        for (BarEntry entry : mSalesData) {
            totalRevenue += entry.getY();
        }

        // Set the product count and sale count TextViews
        mProductCountTextView.setText(String.valueOf(mSalesData.size()));
        mSaleCountTextView.setText(String.format(Locale.FRANCE, "%.2f Fbu", totalRevenue));

        // Refresh the BarChart view
        mBarChart.invalidate();
    }
}
