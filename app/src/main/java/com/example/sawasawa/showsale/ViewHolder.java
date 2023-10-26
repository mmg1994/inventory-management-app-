package com.example.sawasawa.showsale;

import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.sawasawa.R;

public class ViewHolder extends RecyclerView.ViewHolder {
    private TextView nameTextView;
    private TextView emailTextView;

    public ViewHolder(View itemView) {
        super(itemView);
        nameTextView = itemView.findViewById(R.id.text_name);
        emailTextView = itemView.findViewById(R.id.text_email);

        // Create a ShapeDrawable with black borders
        ShapeDrawable shapeDrawable = new ShapeDrawable();
        shapeDrawable.setShape(new RectShape());
        shapeDrawable.getPaint().setColor(Color.WHITE);
        shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
        shapeDrawable.getPaint().setStrokeWidth(4f);
        shapeDrawable.getPaint().setAntiAlias(true);
        shapeDrawable.getPaint().setStrokeJoin(Paint.Join.ROUND);
        shapeDrawable.getPaint().setPathEffect(new CornerPathEffect(10));
        shapeDrawable.getPaint().setStrokeMiter(10);

        // Apply the ShapeDrawable as the background of the itemView
        itemView.setBackground(shapeDrawable);
    }

    public void bind(Person person) {
        nameTextView.setText(person.getName());
        emailTextView.setText(person.getEmail());
    }
}