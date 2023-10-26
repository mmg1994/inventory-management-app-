package com.example.sawasawa.product;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sawasawa.MyConstants;
import com.example.sawasawa.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShowProductFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProductAdapterShow productAdapter;
    private List<ProductShow> productListShow = new ArrayList<>();

    private SharedPreferences mSharedPreferences;
    private TextView mUserIdTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_product, container, false);

        //recupperation du username (inputter)
        mSharedPreferences = requireActivity().getSharedPreferences("my_app", Context.MODE_PRIVATE);
        mUserIdTextView = view.findViewById(R.id.user_id_text_viewe);
        // Retrieve the user ID from SharedPreferences
        String username = mSharedPreferences.getString("user_id", "");

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productAdapter = new ProductAdapterShow(productListShow);
        recyclerView.setAdapter(productAdapter);

        // Add items to productList before creating the adapter
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = MyConstants.API_BASE_URL + "products_repport_show.php?user=" + username;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,

                response -> {

                    try {
                        JSONArray jsonArray = response.getJSONArray("productListShow");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String name = jsonObject.getString("name");
                            String article = jsonObject.getString("article");
                            double price = jsonObject.getDouble("price");
                            ProductShow product = new ProductShow(name, article, price);
                            productListShow.add(product);
                        }
                        productAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("ShowProductFragment", "Error: " + error.getMessage());
                });
        queue.add(request);

        return view;
    }

}