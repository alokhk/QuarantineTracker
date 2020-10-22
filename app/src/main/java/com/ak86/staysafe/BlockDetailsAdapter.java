package com.ak86.staysafe;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
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
        holder.blockName.setText(Validator.decodeFromFirebaseKey(block.getBlockName()));
        holder.blockDescr.setText(block.getBlockDescr());
        holder.blockInCharge.setText(Validator.decodeFromFirebaseKey(block.getBlockInCharge()));
        holder.blockCapacity.setText(String.valueOf(block.getBlockCapacity()));
        holder.blockOccupied.setText(String.valueOf(block.getBlockOccupied()));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        if(block.getMedicalDate() !=null && block.getQuarantineEndDate() != null){
            holder.blockEndDate.setText(sdf.format(block.getQuarantineEndDate()));
            holder.blockMedDate.setText(sdf.format(block.getMedicalDate()));
        }

        if(block.getBlockDescr().equals("Isolation") || block.getBlockDescr().equals("Holding") || block.getBlockDescr().equals("Transit") || block.getBlockOccupied()==0 ){
            holder.blockEndDate.setVisibility(View.GONE);
            holder.blockMedDate.setVisibility(View.GONE);
            holder.tvQE.setVisibility(View.GONE);
            holder.tvMD.setVisibility(View.GONE);
        }



    }

    @Override
    public int getItemCount() {
        return blockList.size();
    }

    public class BlockViewHolder extends RecyclerView.ViewHolder{

        TextView blockName, blockDescr, blockInCharge, blockCapacity, blockOccupied, blockEndDate, blockMedDate, tvQE, tvMD;


        public BlockViewHolder(View itemView){
            super(itemView);
            blockName = itemView.findViewById(R.id.personNameFd);
            blockDescr = itemView.findViewById(R.id.blockDescrFd);
            blockInCharge = itemView.findViewById(R.id.detailBlockICNameFd);
            blockCapacity = itemView.findViewById(R.id.detailBlockCapacityFd);
            blockOccupied = itemView.findViewById(R.id.detailBlockOccupiedFd);
            blockEndDate = itemView.findViewById(R.id.detailQuarantineEndDate);
            blockMedDate = itemView.findViewById(R.id.detailMedDateFd);
            tvQE = itemView.findViewById(R.id.tvQuarantineEnds);
            tvMD = itemView.findViewById(R.id.tvMedDate);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Log.println(Log.ASSERT,"CardClick", Validator.encodeForFirebaseKey(blockName.getText().toString()));
                    Intent intent = new Intent(context, BlockDetailActivity.class);
                    intent.putExtra("selectedBlockName",Validator.encodeForFirebaseKey(blockName.getText().toString()));
                    context.startActivity(intent);
                }
            });
        }
    }


}
