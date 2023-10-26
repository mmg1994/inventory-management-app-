package com.example.sawasawa.Article;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.example.sawasawa.R;

import java.util.List;

public class ArticleAdapter extends ArrayAdapter<Article> {

    private Context context;
    private List<Article> articles;

    public ArticleAdapter(Context context, List<Article> articles) {
        super(context, R.layout.article_list_item, articles);
        this.context = context;
        this.articles = articles;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.article_list_item, null);
        }

        Article article = articles.get(position);
        if (article != null) {
            TextView articleName = view.findViewById(R.id.articleName);
            articleName.setText(article.getName());
        }

        return view;
    }
}