package com.example.sawasawa.homeFragment.sale;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;


import com.example.sawasawa.AddProduct.Product;
import com.example.sawasawa.R;
import com.example.sawasawa.homeFragment.ShoppingFragment;

import java.util.ArrayList;
import java.util.List;
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<Product> productList = new ArrayList<>();
    private OnItemClickListener mListener;
    private ShoppingFragment mActivity;

    public interface OnItemClickListener {
        void onDeleteClick(int position);
        void onTotalPriceUpdate(double totalPrice);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public CartAdapter(FragmentActivity activity, ShoppingFragment fragment) {
        mActivity = fragment;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView priceTextView;
        private Button deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.product_name_text_view);
            priceTextView = itemView.findViewById(R.id.product_price_text_view);
            deleteButton = itemView.findViewById(R.id.delete_button);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        removeProduct(position);
                        updateTotalPrice();
                        if (mListener != null) {
                            mListener.onDeleteClick(position);
                        }
                    }
                }
            });
        }

        public void bind(Product product) {
            nameTextView.setText(product.getName());
            priceTextView.setText("Fbu" + product.getPrice());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(productList.get(position));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void addProduct(Product product) {
        productList.add(product);
        notifyItemInserted(productList.size() - 1);
        updateTotalPrice();
    }

    public void removeProduct(int position) {
        Product removedProduct = productList.get(position);
        double removedProductPrice = removedProduct.getPrice();
        productList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, productList.size());
        mActivity.updateTotalPriceOnRemove(removedProductPrice);
        updateTotalPrice();
    }

    public double getTotalPrice() {
        double totalPrice = 0;
        for (Product product : productList) {
            totalPrice += product.getPrice();
        }
        return totalPrice;
    }

    public void updateTotalPrice() {
        double totalPrice = getTotalPrice();
        if (mListener != null) {
            mListener.onTotalPriceUpdate(totalPrice);
        }
    }
}