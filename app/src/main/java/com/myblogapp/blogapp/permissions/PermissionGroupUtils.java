package com.myblogapp.blogapp.permissions;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Size;

import com.myblogapp.blogapp.R;

import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Created by pia on 31/3/17.
 */

public class PermissionGroupUtils {

    public static PermissionGroupInfo getPermissionGroup(@NonNull PackageManager packageManager, @NonNull String permission) {
        // http://stackoverflow.com/a/33837301
        try {
            PermissionInfo permissionInfo = packageManager.getPermissionInfo(permission, 0);
            return packageManager.getPermissionGroupInfo(permissionInfo.group, 0);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Fail-safe handling without the need to rethrow the exception.
            throw new RuntimeException(e);
        }
    }

    public static String getPermissionGroupsLabel(@NonNull Context context, @NonNull @Size(min = 1) String... permissions) {
        return getPermissionGroupsLabel(context, context.getPackageManager(), permissions);
    }

    public static String getPermissionGroupsLabel(@NonNull Context context, @NonNull PackageManager packageManager, @NonNull @Size(min = 1) String... permissions) {
        //noinspection ConstantConditions
        if (permissions == null || permissions.length == 0) {
            throw new IllegalArgumentException("Requires at least one input permission");
        } else if (permissions.length == 1) {
            return getPermissionGroup(packageManager, permissions[0]).loadLabel(packageManager).toString();
        }

        // Gather all permission groups and ensure one unique mapping is set. Also respects order.
        LinkedHashMap<String, String> permissionGroupLabels = new LinkedHashMap<>();

        for (String permission : permissions) {
            PermissionGroupInfo permissionGroup = getPermissionGroup(packageManager, permission);
            String permissionGroupName = permissionGroup.name;

            if (!permissionGroupLabels.containsKey(permissionGroupName)) {
                permissionGroupLabels.put(permissionGroupName,
                        permissionGroup.loadLabel(packageManager).toString());
            }
        }

        // Then we create a single string that merges permission group labels with separators (i.e. ",") and conjunctions (i.e. "and" or "&")

        StringBuilder sb = new StringBuilder();

        String sep = context.getString(R.string.label_permissions_open_settings_separator);
        String and = context.getString(R.string.label_permissions_open_settings_and);

        Collection<String> values = permissionGroupLabels.values();
        final int size = values.size(), lastIndex = size - 1, secondLastIndex = size - 2;

        int i = 0;
        for (String label : values) {
            sb.append(label);

            if (i == secondLastIndex) {
                sb.append(and);
            } else if (i != lastIndex) {
                sb.append(sep);
            } else {
                break; // No need to proceed, we are the last.
            }

            i++;
        }

        return sb.toString();
    }

}
