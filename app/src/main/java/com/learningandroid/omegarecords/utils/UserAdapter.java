package com.learningandroid.omegarecords.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.learningandroid.omegarecords.R;
import com.learningandroid.omegarecords.ViewUserDetailsActivity;
import com.learningandroid.omegarecords.domain.LoggedInUser;
import com.learningandroid.omegarecords.domain.User;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * user adapter for fill recycler view with users list and loggedInUser
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private static final String URL = "https://robohash.org/";

    Context context;
    ArrayList<User> users;

    public UserAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_list_row, parent, false);
        return new UserViewHolder(view);
    }

    /**
     * if the user is loggedInUser, position will be equal to users.length
     * otherwise, position is the index of the USERS array
     */
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.name.setText(user.getName());
        holder.email.setText(user.getEmail());

        if(user instanceof LoggedInUser && ((LoggedInUser) user).getSelfPortraitPath() != null){
            File file = new File(((LoggedInUser) user).getSelfPortraitPath());
            holder.photo.setImageURI(Uri.fromFile(file));
        } else {
            Picasso.get().load(URL + user.getName()).into(holder.photo);
        }
    }

    /**
     * recycler view displays users and loggedInUser, to item count = users.length + 1
     */
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
                if(user instanceof LoggedInUser){
                    viewUserDetailsIntent.putExtra("logged_in_user_details", ActivityUtils.getGsonParser().toJson(user));
                } else {
                    viewUserDetailsIntent.putExtra("user_details", ActivityUtils.getGsonParser().toJson(user));
                }
                context.startActivity(viewUserDetailsIntent);
            });
        }
    }
}
