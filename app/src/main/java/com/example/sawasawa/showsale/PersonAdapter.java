package com.example.sawasawa.showsale;

import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.sawasawa.R;

import java.util.List;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.ViewHolder> {
    private List<Person> people;

    public PersonAdapter(List<Person> people) {
        this.people = people;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_person, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Person person = people.get(position);
        holder.nameTextView.setText(person.getName());
        holder.emailTextView.setText(person.getEmail());

        // Créer un ShapeDrawable avec des bordures noires
        ShapeDrawable shapeDrawable = new ShapeDrawable();
        shapeDrawable.setShape(new RectShape());
        shapeDrawable.getPaint().setColor(Color.WHITE);
        shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
        shapeDrawable.getPaint().setStrokeWidth(4f); // increase border width to 4
        shapeDrawable.getPaint().setAntiAlias(true);
        shapeDrawable.getPaint().setStrokeJoin(Paint.Join.ROUND);
        shapeDrawable.getPaint().setPathEffect(new CornerPathEffect(10));
        shapeDrawable.getPaint().setStrokeMiter(10);

// Appliquer le ShapeDrawable comme fond de l'élément
        holder.itemView.setBackground(shapeDrawable);
    }

    @Override
    public int getItemCount() {
        return people.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView emailTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.text_name);
            emailTextView = itemView.findViewById(R.id.text_email);
        }
    }
}