package com.example.oneuiapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import java.lang.reflect.Field;
import java.util.List;

public class DrawerListAdapter extends RecyclerView.Adapter<DrawerListViewHolder> {
    
    private Context mContext;
    private List<Fragment> mFragments;
    private DrawerListener mListener;
    private int mSelectedPos;

    public interface DrawerListener {
        boolean onDrawerItemSelected(int position);
    }

    public DrawerListAdapter(@NonNull Context context, List<Fragment> fragments, DrawerListener listener) {
        mContext = context;
        mFragments = fragments;
        mListener = listener;
    }

    @NonNull
    @Override
    public DrawerListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.drawer_list_item, parent, false);
        return new DrawerListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrawerListViewHolder holder, int position) {
        Fragment fragment = mFragments.get(position);

        int iconRes = 0;
        if (fragment instanceof HomeFragment) {
            iconRes = getOneUiIconId("ic_oui_home_outline");
            holder.setTitle("Home");
        } else if (fragment instanceof SettingsFragment) {
            iconRes = getOneUiIconId("ic_oui_settings_outline");
            holder.setTitle("Settings");
        }

        if (iconRes != 0) {
            Drawable d = AppCompatResources.getDrawable(mContext, iconRes);
            holder.itemView.findViewById(android.R.id.icon);
            holder.setIcon(iconRes);
        }

        holder.setSelected(position == mSelectedPos);
        holder.itemView.setOnClickListener(v -> {
            final int itemPos = holder.getBindingAdapterPosition();
            if (mListener != null && mListener.onDrawerItemSelected(itemPos)) {
                setSelectedItem(itemPos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFragments.size();
    }

    public void setSelectedItem(int position) {
        mSelectedPos = position;
        notifyItemRangeChanged(0, getItemCount());
    }

    // مساعد لجلب الأيقونة من مكتبة oneui
    private int getOneUiIconId(String name) {
        try {
            Class<?> r = Class.forName("dev.oneuiproject.oneui.R$drawable");
            Field f = r.getField(name);
            return f.getInt(null);
        } catch (Exception e) {
            return 0;
        }
    }
}
