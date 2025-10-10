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
    private int mSelectedPos = 0; // العنصر المحدد حالياً

    public interface DrawerListener {
        /**
         * يتم استدعاؤها عند الضغط على عنصر في الدرج
         * @param position موضع العنصر المضغوط
         * @return true إذا تم تغيير الـ Fragment بنجاح، false إذا كان نفس العنصر محدد
         */
        boolean onDrawerItemSelected(int position);
    }

    public DrawerListAdapter(
            @NonNull Context context, List<Fragment> fragments, DrawerListener listener) {
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

        // تحديد الأيقونة والعنوان بناءً على نوع الـ Fragment
        int iconRes = 0;
        String title = "";
        
        if (fragment instanceof HomeFragment) {
            iconRes = getOneUiIconId("ic_oui_home");
            title = mContext.getString(R.string.drawer_home);
        } else if (fragment instanceof SettingsFragment) {
            iconRes = getOneUiIconId("ic_oui_settings");
            title = mContext.getString(R.string.drawer_settings);
        }

        // تطبيق الأيقونة والعنوان
        if (iconRes != 0) {
            holder.setIcon(iconRes);
        }
        if (!title.isEmpty()) {
            holder.setTitle(title);
        }

        // تحديد حالة التحديد (محدد أم لا)
        // هذا مهم جداً لإظهار العنصر المحدد بشكل صحيح بعد recreate()
        holder.setSelected(position == mSelectedPos);
        
        // معالج الضغط على العنصر
        holder.itemView.setOnClickListener(v -> {
            // الحصول على الموضع الحالي للعنصر المضغوط
            // استخدام getBindingAdapterPosition() بدلاً من getAdapterPosition() 
            // لأنها أكثر دقة في حالات التحديث المتعددة
            final int itemPos = holder.getBindingAdapterPosition();
            
            if (itemPos == RecyclerView.NO_POSITION) {
                // الموضع غير صالح، تجاهل الضغطة
                return;
            }
            
            // إخطار الـ listener بالضغطة
            boolean selectionChanged = false;
            if (mListener != null) {
                selectionChanged = mListener.onDrawerItemSelected(itemPos);
            }
            
            // إذا تم تغيير التحديد، حدّث الـ adapter
            // هذا يضمن أن العنصر الجديد يظهر بشكل محدد والعنصر القديم يعود لحالته العادية
            if (selectionChanged) {
                setSelectedItem(itemPos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFragments != null ? mFragments.size() : 0;
    }

    /**
     * تحديث العنصر المحدد في القائمة
     * هذه الدالة مهمة جداً للحفاظ على التحديد الصحيح بعد recreate()
     * @param position موضع العنصر الذي يجب تحديده
     */
    public void setSelectedItem(int position) {
        if (position < 0 || position >= getItemCount()) {
            return; // موضع غير صالح
        }
        
        int previousPos = mSelectedPos;
        mSelectedPos = position;
        
        // تحديث العنصر السابق والعنصر الجديد فقط لتحسين الأداء
        // بدلاً من تحديث جميع العناصر بـ notifyDataSetChanged()
        if (previousPos != position) {
            // تحديث العنصر السابق ليصبح غير محدد
            notifyItemChanged(previousPos);
            // تحديث العنصر الجديد ليصبح محدداً
            notifyItemChanged(position);
        }
    }

    /**
     * دالة مساعدة للحصول على معرّف الأيقونة من مكتبة OneUI
     * @param name اسم الأيقونة بدون البادئة ic_oui_
     * @return معرّف المورد للأيقونة، أو 0 إذا لم يتم العثور عليها
     */
    private int getOneUiIconId(String name) {
        try {
            Class<?> r = Class.forName("dev.oneuiproject.oneui.R$drawable");
            Field f = r.getField(name);
            return f.getInt(null);
        } catch (Exception e) {
            // فشل في العثور على الأيقونة، أعد 0
            return 0;
        }
    }
    
    /**
     * الحصول على موضع العنصر المحدد حالياً
     * @return موضع العنصر المحدد
     */
    public int getSelectedPosition() {
        return mSelectedPos;
    }
}
