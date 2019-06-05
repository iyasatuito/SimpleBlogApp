package com.myblogapp.blogapp.permissions.internal;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.myblogapp.blogapp.R;
import com.myblogapp.blogapp.permissions.PermissionGroupUtils;

/**
 * Created by pia on 31/3/17.
 */

public class PermissionDialogs {

    public static AlertDialog.Builder directToAppPermissions(Context context, DialogInterface.OnClickListener onOpenAppSettings, String... permissions) {
        String permissionGroupsLabel = PermissionGroupUtils.getPermissionGroupsLabel(context, permissions);

        return new AlertDialog.Builder(context)
                .setMessage(context.getString(R.string.message_permissions_open_settings, permissionGroupsLabel))
                .setPositiveButton(R.string.action_permissions_open_settings, onOpenAppSettings)
                .setCancelable(false);
    }

    public static AlertDialog.Builder directToAppPermissions(final Context context, String... permissions) {
        return directToAppPermissions(context, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface d, int w) {
                onOpenAppSettings(context);
            }
        }, permissions);
    }

    public static AlertDialog.Builder directToAppPermissionsAndRecreate(final Activity activity, String... permissions) {
        return directToAppPermissions(activity, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface d, int w) {
                onOpenAppSettingsAndRecreate(activity);
            }
        }, permissions);
    }

    public static AlertDialog.Builder directToAppPermissions(Activity activity, boolean reCreateActivity, String... permissions) {
        return reCreateActivity
                ? directToAppPermissions(activity, permissions)
                : directToAppPermissionsAndRecreate(activity, permissions);
    }

    static void onOpenAppSettings(Context context) {
        context.startActivity(createIntentForAppSettings(context));
    }

    static void onOpenAppSettingsAndRecreate(Activity activity) {
        activity.startActivity(createIntentForAppSettings(activity));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.releaseInstance();
        }
    }

    static Intent createIntentForAppSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        return intent;
    }

}
