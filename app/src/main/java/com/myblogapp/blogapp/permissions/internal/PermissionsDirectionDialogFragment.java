package com.myblogapp.blogapp.permissions.internal;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatDialogFragment;

import java.util.List;

/**
 * This is an internal API, do not use outside!
 * <p>
 * Created by pia on 31/3/17.
 *
 */
public class PermissionsDirectionDialogFragment extends AppCompatDialogFragment {

    public static final String DEFAULT_TAG = "internal.PermissionsDirectionDialogFragment";

    public static final String ARG_PERMISSIONS = "arg.permissions";
    public static final String ARG_RECREATE_ACTIVITY = "arg.recreate";

    public static PermissionsDirectionDialogFragment newInstance(List<String> permissions) {
        return newInstance(false, permissions);
    }

    public static PermissionsDirectionDialogFragment newInstance(String... permissions) {
        return newInstance(false, permissions);
    }

    public static PermissionsDirectionDialogFragment newInstance(boolean reCreateActivity, List<String> permissions) {
        return newInstance(reCreateActivity, permissions.toArray(new String[permissions.size()]));
    }

    public static PermissionsDirectionDialogFragment newInstance(boolean reCreateActivity, String... permissions) {
        PermissionsDirectionDialogFragment fragment = new PermissionsDirectionDialogFragment();

        Bundle args = new Bundle();
        args.putBoolean(ARG_RECREATE_ACTIVITY, reCreateActivity);
        args.putStringArray(ARG_PERMISSIONS, permissions);

        fragment.setArguments(args);
        return fragment;
    }

    public void show(FragmentManager manager) {
        show(manager.beginTransaction(), DEFAULT_TAG);
    }

    public int show(FragmentTransaction transaction) {
        return show(transaction, DEFAULT_TAG);
    }

    public void removePreviousAndShow(FragmentManager manager) {
        removePreviousAndShow(manager, DEFAULT_TAG);
    }

    public void removePreviousAndShow(FragmentManager manager, String tag) {
        Fragment f = manager.findFragmentByTag(tag);
        @SuppressLint("CommitTransaction")
        FragmentTransaction ft = manager.beginTransaction();
        show(f == null ? ft : ft.remove(f), tag);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // TODO Fix case when `getArguments() == null`
        final String[] permissions = getArguments().getStringArray(ARG_PERMISSIONS);

        return PermissionDialogs.directToAppPermissions(getActivity()
                , getArguments().getBoolean(ARG_RECREATE_ACTIVITY, false)
                , permissions).create();
    }

}

