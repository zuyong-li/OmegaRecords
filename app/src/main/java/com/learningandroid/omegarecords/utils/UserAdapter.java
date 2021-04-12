package com.learningandroid.omegarecords.utils;

import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.learningandroid.omegarecords.R;
import com.learningandroid.omegarecords.component.activity.ViewUserDetailsActivity;
import com.learningandroid.omegarecords.storage.entity.LoggedInUser;
import com.learningandroid.omegarecords.storage.entity.User;
import com.learningandroid.omegarecords.viewmodel.ImageViewModel;
import com.learningandroid.omegarecords.viewmodel.LoggedInUserViewModel;


import java.util.ArrayList;
import java.util.List;

/**
 * user adapter for fill recycler view with users list and loggedInUser
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    Context context;
    List<User> users;
    ImageViewModel imageViewModel;
    LoggedInUserViewModel loggedInUserViewModel;

    public UserAdapter(Context context, List<User> users, ImageViewModel imageViewModel,
                       LoggedInUserViewModel loggedInUserViewModel) {
        this.context = context;
        this.users = users;
        this.imageViewModel = imageViewModel;
        this.loggedInUserViewModel = loggedInUserViewModel;
    }

    public void setUsers(List<User> newUserList) {
        newUserList.add(0, users.get(0));
        users = newUserList;
        notifyDataSetChanged();
    }

    public User userAt(int position) {
        return users.get(position);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_list_row, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.name.setText(user.getName());
        holder.email.setText(user.getEmail());
        ImageUtils.setUserImage(holder.photo, user, imageViewModel, loggedInUserViewModel);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public class UserViewHolder extends RecyclerView.ViewHolder {

        TextView name, email;
        ImageView photo;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.view_user_name);
            email = itemView.findViewById(R.id.view_user_email);
            photo = itemView.findViewById(R.id.view_user_photo);

            itemView.findViewById(R.id.userListRow).setOnClickListener((View view) -> {
                Intent viewUserDetailsIntent = new Intent(context, ViewUserDetailsActivity.class);

                User user = users.get(getAdapterPosition());
                if (user instanceof LoggedInUser) {
                    viewUserDetailsIntent.putExtra("logged_in_user_details", GsonProvider.getInstance().toJson(user));
                } else {
                    viewUserDetailsIntent.putExtra("user_details", GsonProvider.getInstance().toJson(user));
                }
                context.startActivity(viewUserDetailsIntent);
            });
        }
    }
}
