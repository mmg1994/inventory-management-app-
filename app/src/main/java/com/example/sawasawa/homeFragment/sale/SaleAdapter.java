package com.example.sawasawa.homeFragment.sale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SaleAdapter extends ArrayAdapter<Saleshow> {

    public SaleAdapter(Context context, List<Saleshow> sales) {
        super(context, 0, sales);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        Saleshow sale = getItem(position);

        TextView nameTextView = convertView.findViewById(android.R.id.text1);
        nameTextView.setText(sale.getName());

        TextView priceTextView = convertView.findViewById(android.R.id.text2);
        priceTextView.setText("$" + sale.getPrice());

        return convertView;
    }
}