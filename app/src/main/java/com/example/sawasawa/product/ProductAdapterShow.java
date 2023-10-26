package com.example.sawasawa.product;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.sawasawa.R;

import java.util.List;
import java.util.Locale;

public class ProductAdapterShow extends RecyclerView.Adapter<ProductAdapterShow.ProductShowViewHolder> {
    private List<ProductShow> productListShow;

    public ProductAdapterShow(List<ProductShow> productList) {
        this.productListShow = productList;
    }

    @Override
    public ProductShowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_show, parent, false);
        return new ProductShowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductShowViewHolder holder, int position) {
        ProductShow product = productListShow.get(position);
        holder.nameTextView.setText(product.getName());
        holder.articleTextView.setText(product.getArticle());
        holder.priceTextView.setText(String.format(Locale.getDefault(), "%.2f Fbu", product.getPrice()));
    }

    @Override
    public int getItemCount() {
        return productListShow.size();
    }

    public static class ProductShowViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView articleTextView;
        private TextView priceTextView;

        public ProductShowViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.text_name);
            articleTextView = itemView.findViewById(R.id.text_article);
            priceTextView = itemView.findViewById(R.id.text_price);
        }
    }
}
