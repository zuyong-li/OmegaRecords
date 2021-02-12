package com.learningandroid.omegarecords.utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.learningandroid.omegarecords.NavigationPane;
import com.learningandroid.omegarecords.R;
import com.learningandroid.omegarecords.ViewUserDetailsActivity;
import com.learningandroid.omegarecords.domain.User;
import com.squareup.picasso.Picasso;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private static final String URL = "https://robohash.org/";

    Context context;
    User[] users;
    User me;

    public UserAdapter(Context context, User[] users, User me) {
        this.context = context;
        this.users = users;
        this.me = me;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_list_row, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User userBinded = (position < users.length) ? users[position] : me;
        holder.name.setText(userBinded.getName());
        holder.email.setText(userBinded.getEmail());
        Picasso.get().load(URL + userBinded.getName()).into(holder.photo);

        holder.userListRow.setOnClickListener((View view) -> {
            Intent viewUserDetailsIntent = new Intent(context, ViewUserDetailsActivity.class);
            viewUserDetailsIntent.putExtra("user_position", position);
            context.startActivity(viewUserDetailsIntent);
        });
    }

    @Override
    public int getItemCount() {
        return users.length + 1;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView name, email;
        ImageView photo;
        ConstraintLayout userListRow;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.view_user_name);
            email = itemView.findViewById(R.id.view_user_email);
            photo = itemView.findViewById(R.id.view_user_photo);
            userListRow = itemView.findViewById(R.id.userListRow);
        }
    }
}
