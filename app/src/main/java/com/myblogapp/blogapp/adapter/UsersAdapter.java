package com.myblogapp.blogapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.myblogapp.blogapp.Activities.ViewBlogListActivity;
import com.myblogapp.blogapp.Models.User;
import com.myblogapp.blogapp.R;
import com.myblogapp.blogapp.Utils.MyLogger;
import com.myblogapp.blogapp.Utils.UserUtil;

import java.util.ArrayList;

import io.realm.Realm;

/**
 * Created by piasatuito on 30/3/17.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.BlogEntryViewHolder>{

    private Context mContext;
    private ArrayList<User> usersList;
    private User loggedUser;

    public UsersAdapter(Context context, ArrayList<User> usersList){
        this.mContext = context;
        this.loggedUser = UserUtil.getInstance().getLoggedUser();
        this.usersList = usersList;
    }

    @Override public BlogEntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.user_item_layout, parent, false);
        return new BlogEntryViewHolder(view);
    }

    @Override public void onBindViewHolder(BlogEntryViewHolder holder, int position) {
        holder.bind(usersList.get(position));
    }

    @Override public int getItemCount() {
        return usersList.size();
    }

    public void setList(ArrayList<User> data){
        usersList = data;
        notifyDataSetChanged();
    }

    class BlogEntryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView userNameTxt;
        Button viewBlogBtn;
        Button blockUserBtn;
        User mUser;


        public BlogEntryViewHolder(View itemView) {
            super(itemView);

            userNameTxt = (TextView) itemView.findViewById(R.id.userName);
            viewBlogBtn = (Button) itemView.findViewById(R.id.viewBlogBtn);
            blockUserBtn = (Button) itemView.findViewById(R.id.blockBtn);

            viewBlogBtn.setOnClickListener(this);
            blockUserBtn.setOnClickListener(this);


        }

        public void bind(User user){
            mUser = user;
            userNameTxt.setText(mUser.getName());
            if(loggedUser.getBlockUserList().contains(user)){
                blockUserBtn.setText("UNBLOCK USER");
            }

            MyLogger.showLog(" blocked user " + user.getBlockUserList().contains(loggedUser));

            if(user.getBlockUserList().contains(loggedUser)){
                MyLogger.showLog(" blocked user ");
                viewBlogBtn.setAlpha(.5f);
                viewBlogBtn.setClickable(false);
            }

        }

        @Override public void onClick(View view) {
            switch (view.getId()){
                case R.id.viewBlogBtn :
                    viewBlog(mUser);
                    break;
                case R.id.blockBtn :
                    if(blockUserBtn.getText().toString().equalsIgnoreCase("BLOCK USER")){
                        blockUser(mUser,true);
                    } else {
                        blockUser(mUser,false);
                    }
                    break;
            }

        }
        private void blockUser(final User user, boolean toBlock){

            final Realm realm = Realm.getDefaultInstance();
            if(toBlock){
                //BLOCK USER
                realm.executeTransaction(new Realm.Transaction() {
                    @Override public void execute(Realm realm1) {
                        User logUser = realm1.where(User.class).equalTo("name",loggedUser.getName()).findFirst();
                        User blockUser = realm1.where(User.class).equalTo("name",user.getName()).findFirst();

                        logUser.getBlockUserList().add(blockUser);
                        MyLogger.showLog("logUser.getBlockUserList() : "+logUser.getBlockUserList().size());
                        realm1.insertOrUpdate(logUser);
                        MyLogger.showLog("new logged USER block user size : "+logUser.getBlockUserList().size());

                    }
                });

                blockUserBtn.setText("UNBLOCK USER");
            } else {
                //UNBLOCK USER
                realm.executeTransaction(new Realm.Transaction() {
                    @Override public void execute(Realm realm1) {
                        User logUser = realm1.where(User.class).equalTo("name",loggedUser.getName()).findFirst();
                        User unblockUser = realm1.where(User.class).equalTo("name",user.getName()).findFirst();

                        logUser.getBlockUserList().remove(unblockUser);
                        MyLogger.showLog("logUser.getBlockUserList() : "+logUser.getBlockUserList().size());
                        realm1.insertOrUpdate(logUser);
                        MyLogger.showLog("new logged USER block user size : "+logUser.getBlockUserList().size());

                    }
                });

                blockUserBtn.setText("BLOCK USER");

            }
            realm.close();
            MyLogger.showLog("block user size : "+loggedUser.getBlockUserList().size());
        }

        private void viewBlog(User user){
            Intent intent = new Intent(mContext, ViewBlogListActivity.class);
            intent.putExtra("user", user.getName());
            mContext.startActivity(intent);
        }
    }
}
