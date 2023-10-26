package com.example.sawasawa.homeFragment;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sawasawa.AddProduct.Product;
import com.example.sawasawa.MyConstants;


import com.example.sawasawa.MyPrintDocumentAdapter;
import com.example.sawasawa.R;
import com.example.sawasawa.ScanningActivity;
import com.example.sawasawa.homeFragment.sale.CartAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ShoppingFragment extends Fragment {

    // private static final String ENDPOINT = "get_product_info.php";


    private List<Product> cart = new ArrayList<>();
    private double totalPrice = 0;
    private TextView totalPriceTextView, emptyCartTextView, mUserIdTextView;

    private boolean isInitialized = false;
    private RecyclerView cartRecyclerView;

    private CartAdapter adapter;

    //  private Button submit_button;


    private Button scanButton;
    private ListView listView;
    private List<String> productList;
    // private ArrayAdapter<String> adapter;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int SAVE_PDF_REQUEST_CODE = 1;
    private PdfDocument pdfDocument;
    private Uri pdfUri;



    private SharedPreferences mSharedPreferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping, container, false);

        productList = new ArrayList<>();
        cartRecyclerView = view.findViewById(R.id.cart_recycler_view);
        adapter = new CartAdapter(getActivity(), this);
        cartRecyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        cartRecyclerView.setLayoutManager(layoutManager);
        totalPriceTextView = view.findViewById(R.id.total_price_text_view);
        totalPriceTextView.setText("Fbu" + totalPrice);
        emptyCartTextView = view.findViewById(R.id.empty_cart_text_view);

        //recupperation du username (inputter)
        mSharedPreferences = requireActivity().getSharedPreferences("my_app", Context.MODE_PRIVATE);
        mUserIdTextView = view.findViewById(R.id.user_id_text_view);
        // Retrieve the user ID from SharedPreferences
        String userId = mSharedPreferences.getString("user_id", "");

        // Display the user ID in the TextView
        mUserIdTextView.setText(userId);

        // Launch the scanner when the FAB button is clicked
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ScanningActivity.class);
                startActivityForResult(intent, ScanningActivity.REQUEST_CODE);
            }
        });

        Button submitButton = view.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cart.isEmpty()) {
                    Toast.makeText(getContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
                } else {
                    showSaleDialog();
                }
            }
        });
        Button pdfButton = view.findViewById(R.id.pdf_button);
        pdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }
    private ProgressDialog progressDialog;

    private void showLoadingDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Retrieving product information...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ScanningActivity.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String qrCode = data.getStringExtra(ScanningActivity.EXTRA_RESULT);

            String url = MyConstants.API_BASE_URL + "get_product_info.php?qr_code=" + qrCode;

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                // Get the product name and price from the response
                                String name = response.getString("name");
                                double price = response.getDouble("price");
                                String barcode = response.getString("barcode");
                                // Add the product to the cart and update the total price
                                Product product = new Product(name, price,barcode);
                                cart.add(product);
                                adapter.addProduct(product);
                                totalPrice += price;
                                totalPriceTextView.setText("Fbu" + totalPrice);

                                cartRecyclerView.setVisibility(View.VISIBLE);
                                emptyCartTextView.setVisibility(View.GONE);
                            } catch (JSONException e) {
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

            // Add the request to the RequestQueue
            RequestQueue queue = Volley.newRequestQueue(getContext());
            queue.add(request);
        }
    }

    // Calculate the total price of the items in the cart
    private double calculateTotalPrice() {

        double totalPrice = 0;
        for (Product product : cart) {
            totalPrice += product.getPrice();
        }
        return totalPrice;
    }
    public void updateTotalPriceOnRemove(double removedProductPrice) {
        totalPrice -= removedProductPrice;
        updateTotalPrice(totalPrice);
    }
    public void updateTotalPrice(double totalPrice) {
        // Set the text of the total price TextView

        totalPriceTextView.setText("Total price: Fbu" + String.format("%.2f", totalPrice));
    }

    // Submit the sale information to the server using Volley
    private void submitSale() {

        String url =MyConstants.API_BASE_URL +"submit_sale.php";
        JSONArray productsSold = new JSONArray();
        for (Product p : cart) {
            JSONObject product = new JSONObject();
            try {
                product.put("name", p.getName());
                //  product.put("description", p.getDescription());
                product.put("price", p.getPrice());
                productsSold.put(product);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONObject saleInfo = new JSONObject();
        try {
            saleInfo.put("date", getCurrentDate());
            saleInfo.put("time", getCurrentTime());
            saleInfo.put("total_price", totalPrice);
            saleInfo.put("products_sold", productsSold);
            saleInfo.put("inputter", inputter());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, saleInfo,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getContext(), "Sale submitted successfully!", Toast.LENGTH_SHORT).show();
                     //   clearCart();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }


    //**********************************************************************************************************
    // Helper method to get the current date in the desired format
    private  String inputter(){
        String inputter = mUserIdTextView.getText().toString();
        return inputter;
    }
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    // Helper method to get the current time in the desired format
    private String getCurrentTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return timeFormat.format(new Date());
    }

    private String getCartText() {
        StringBuilder sb = new StringBuilder();
        for (Product product : cart) {
            sb.append(product.getName()).append(" (Fbu").append(product.getPrice()).append(")").append("\n");
        }
        return sb.toString();
    }

    private void showSaleDialog() {
            // Create the dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomDialog);
            View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_sale, null);
            builder.setView(view);
            AlertDialog dialog = builder.create();

            // Set the dialog content
            TextView cartTextView = view.findViewById(R.id.cart_text_view);
            cartTextView.setText(getCartText()); // Method that returns a string of the cart items
            TextView totalPriceTextView = view.findViewById(R.id.total_price_text_view);
        totalPriceTextView.setText("Total price: Fbu" + String.format("%.2f", totalPrice));

        // Add the Wi-Fi print button
        Button printButton = view.findViewById(R.id.print_button);
        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printSaleOverWifi(dialog);
            }
        });


        Button submitSaleButton = view.findViewById(R.id.submit_sale_button);
        submitSaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitSale();
                dialog.dismiss();
            }
        });

        // Show the dialog
        dialog.show();
    }
///////////////////////////////////////////////////
private void printSaleOverWifi(AlertDialog dialog) {

    // Set the print job name
    String jobName = "Sale Receipt";

    // Get the print manager
    PrintManager printManager = (PrintManager) getContext().getSystemService(Context.PRINT_SERVICE);

    // Create a print job builder
    PrintAttributes.MediaSize pageSize = PrintAttributes.MediaSize.ISO_A4;
    // Create a print job builder with custom paper size
    PrintAttributes.MediaSize customSize = new PrintAttributes.MediaSize("custom", "Custom Size", 500, 1500);
    PrintAttributes attributes = new PrintAttributes.Builder()
            .setMediaSize(customSize)
            .setResolution(new PrintAttributes.Resolution("dpi", "Print", 300, 300))
            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
            .build();
    MyPrintDocumentAdapter printAdapter = new MyPrintDocumentAdapter(getContext(), dialog.getWindow().getDecorView().findViewById(android.R.id.content));
    PrintJob printJob = printManager.print(jobName, printAdapter, attributes);

    // Wait for the print job to complete
    if (printJob.isCompleted()) {
        Toast.makeText(getContext(), "Print job completed", Toast.LENGTH_SHORT).show();
    } else if (printJob.isFailed()) {
        Toast.makeText(getContext(), "Print job failed", Toast.LENGTH_SHORT).show();
    }

}
//////////////////////////////////////////////////


}