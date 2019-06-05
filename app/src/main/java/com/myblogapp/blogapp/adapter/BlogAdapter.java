package com.myblogapp.blogapp.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.myblogapp.blogapp.Activities.BlogListActivity;
import com.myblogapp.blogapp.Models.BlogEntry;
import com.myblogapp.blogapp.R;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by piasatuito on 30/3/17.
 */

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {

    private List<BlogEntry> blogList;
    private BlogListActivity mActivity;

    public BlogAdapter(BlogListActivity activity, RealmList<BlogEntry> data) {
        this.mActivity = activity;
        this.blogList = data;
    }


    public class BlogViewHolder extends RecyclerView.ViewHolder {
        ImageView imageCover, inactive;
        RelativeLayout notActiveOverlay;
        TextView title, author, date, content;
        ImageView popMenu;
        CardView holder;
        BlogEntry blogEntry;

        public BlogViewHolder(View itemView) {
            super(itemView);

            imageCover = (ImageView) itemView.findViewById(R.id.imageCover);
            title = (TextView) itemView.findViewById(R.id.title);
            author = (TextView) itemView.findViewById(R.id.author);
            date = (TextView) itemView.findViewById(R.id.date);
            content = (TextView) itemView.findViewById(R.id.content);
            holder = (CardView) itemView.findViewById(R.id.holder);
            popMenu = (ImageView) itemView.findViewById(R.id.popMenu);
            inactive = (ImageView) itemView.findViewById(R.id.inactive);
            notActiveOverlay = (RelativeLayout) itemView.findViewById(R.id.notActiveOverlay);

        }

        public void bind(BlogEntry blogs) {
            blogEntry = blogs;

            Glide.with(mActivity).load(blogEntry.getImagePath()).placeholder(R.drawable.no_image).centerCrop().into(imageCover);
            title.setText(blogEntry.getTitle());
            date.setText(blogEntry.getDate());
            author.setText(blogEntry.getAuthor().getName());
            content.setText(blogEntry.getContent());

            if (blogEntry.isActive()) {
                notActiveOverlay.setVisibility(View.GONE);
                popMenu.setEnabled(true);
                popMenu.setClickable(true);
                holder.setEnabled(false);
                holder.setClickable(false);
                popMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MenuBuilder menuBuilder = new MenuBuilder(mActivity);
                        MenuInflater inflater = new MenuInflater(mActivity);
                        inflater.inflate(R.menu.menu_item, menuBuilder);
                        MenuPopupHelper optionsMenu = new MenuPopupHelper(mActivity, menuBuilder, view);
                        optionsMenu.setForceShowIcon(true);

                        menuBuilder.setCallback(new MenuBuilder.Callback() {
                            @Override
                            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.action_view:
                                        mActivity.viewBlogEntry(blogEntry.getID(), getAdapterPosition(), imageCover);
                                        return true;

                                    case R.id.action_edit:
                                        mActivity.editBlogEntry(blogEntry.getID());
                                        return true;

                                    case R.id.action_delete:
                                        updateBlog(blogEntry, "Delete Blog", "Are you sure you want to delete this topic?", false);
                                        return true;
                                    default:
                                        return false;
                                }
                            }

                            @Override
                            public void onMenuModeChange(MenuBuilder menu) {}
                        });

                        optionsMenu.show();
                    }
                });

            } else {
                notActiveOverlay.setVisibility(View.VISIBLE);
                popMenu.setEnabled(false);
                popMenu.setClickable(false);
                holder.setEnabled(true);
                holder.setClickable(true);
                Glide.with(mActivity).load(R.drawable.inactive).centerCrop().into(inactive);
                holder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateBlog(blogEntry, "Re-activate Blog", "Are you sure you want to re-activate this topic?", true);
                    }
                });
            }
        }
    }

    private void updateBlog(final BlogEntry blogEntry, String title, String msg, final boolean isActive) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        Realm realm2 = Realm.getDefaultInstance();
                        realm2.beginTransaction();
                        blogEntry.setActive(isActive);
                        realm2.copyToRealmOrUpdate(blogEntry);
                        realm2.commitTransaction();
                        notifyDataSetChanged();

                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void setList(RealmList<BlogEntry> data){
        blogList = data;
        notifyDataSetChanged();
    }


    @Override
    public BlogAdapter.BlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item, null);
        BlogViewHolder viewHolder = new BlogViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BlogAdapter.BlogViewHolder holder, int position) {
        holder.bind(blogList.get(position));
    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }


}
