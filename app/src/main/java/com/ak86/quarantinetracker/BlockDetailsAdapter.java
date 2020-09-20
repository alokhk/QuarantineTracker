package com.ak86.quarantinetracker;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BlockDetailsAdapter extends RecyclerView.Adapter<BlockDetailsAdapter.BlockViewHolder>
{
    List<Block> blockList;
    Context context;

    public BlockDetailsAdapter(List<Block> blockList) {
        this.blockList = blockList;
    }

    @NonNull
    @Override
    public BlockDetailsAdapter.BlockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View iteView = inflater.inflate(R.layout.item_list, parent, false);
        BlockViewHolder viewHolder = new BlockViewHolder(iteView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BlockViewHolder holder, int position) {
        Block block = blockList.get(position);
        holder.blockName.setText(block.getBlockName());
        holder.blockInCharge.setText(block.getBlockInCharge());
        holder.blockCapacity.setText(String.valueOf(block.getBlockCapacity()));



    }

    @Override
    public int getItemCount() {
        return blockList.size();
    }

    public class BlockViewHolder extends RecyclerView.ViewHolder{

        TextView blockName, blockInCharge, blockCapacity;
        public BlockViewHolder(View itemView){
            super(itemView);
            blockName = itemView.findViewById(R.id.detailBlockNameFd);
            blockInCharge = itemView.findViewById(R.id.detailBlockICNameFd);
            blockCapacity = itemView.findViewById(R.id.detailBlockCapacityFd);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Log.println(Log.ASSERT,"CardClick", blockName.getText().toString());
                    Intent intent = new Intent(context, BlockDetailActivity.class);
                    intent.putExtra("selectedBlockName",blockName.getText().toString());
                    context.startActivity(intent);
                }
            });
        }
    }


}
