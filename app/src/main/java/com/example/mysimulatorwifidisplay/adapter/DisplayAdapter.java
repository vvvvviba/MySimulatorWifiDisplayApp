package com.example.mysimulatorwifidisplay.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mysimulatorwifidisplay.R;
import com.example.mysimulatorwifidisplay.mojo.Display;

import java.util.List;

/**
 * @ClassName: DisplayAdapter
 * @Description:
 * @Author: shuailin.wang
 * @CreateDate: 2023/7/18
 */
public class DisplayAdapter extends RecyclerView.Adapter<DisplayAdapter.ViewHolder> {

    private List<Display> mDeviceList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View displayView;
        TextView nameTextView;
        TextView addrTextView;
        TextView typeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            displayView = itemView;
            nameTextView = itemView.findViewById(R.id.p2p_device_name);
            addrTextView = itemView.findViewById(R.id.p2p_device_address);
            typeTextView = itemView.findViewById(R.id.p2p_device_primary_type);
        }
    }

    public DisplayAdapter(List mDeviceList) {
        this.mDeviceList = mDeviceList;
    }

    @NonNull
    @Override
    public DisplayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.displayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                Display display = mDeviceList.get(position);
                Toast.makeText(view.getContext(), "you clicked " + display.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DisplayAdapter.ViewHolder holder, int position) {
        Display display = mDeviceList.get(position);
        holder.nameTextView.setText(display.getName());
        holder.addrTextView.setText(display.getAddress());
        holder.typeTextView.setText(display.getPrimaryType());
    }

    @Override
    public int getItemCount() {
        return mDeviceList.size();
    }
}
