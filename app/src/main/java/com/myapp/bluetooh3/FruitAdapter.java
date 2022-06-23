package com.myapp.bluetooh3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
//用于将上下文、listview 子项布局的 id 和数据都传递过来
public class FruitAdapter extends ArrayAdapter<DeviceInformation> {
    public FruitAdapter(@NonNull Context context, int resource, @NonNull List<DeviceInformation> objects) {
        super(context, resource, objects);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        DeviceInformation deviceInformation=getItem(position);//获取当前项的 Fruit 实例
        View view;
        //新增一个内部类 ViewHolder，用于对控件的实例进行缓存
        ViewHolder viewHolder;
        if (convertView==null){
            //为每一个子项加载设定的布局
            view= LayoutInflater.from(getContext()).inflate(R.layout.fruit_item,parent,false);
            viewHolder=new ViewHolder();
            //分别获取 imageview 和 textview 的实例

            viewHolder.fruitname =view.findViewById(R.id.fruit_name);
            viewHolder.fruitprice=view.findViewById(R.id.fruit_price);
            view.setTag(viewHolder);//将 viewHolder 存储在 view 中
        }else {
            view=convertView;
            viewHolder= (ViewHolder) view.getTag();//重新获取 viewHolder
        }
        // 设置要显示的图片和文字
        viewHolder.fruitname.setText(deviceInformation.getDeviceName());
        viewHolder.fruitprice.setText(deviceInformation.getDeviceAddress());
        return view;
    }
    private class ViewHolder {

        TextView fruitname;
        TextView fruitprice;
    }
}
