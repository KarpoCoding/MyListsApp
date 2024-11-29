package org.karpo.mylists;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {
    private final Map<String, ArrayList<String>> dictOfAllLists;
    String whichListToGoTo;
    RecyclerViewListActivity context;

    public MyRecyclerViewAdapter(RecyclerViewListActivity context, Map<String, ArrayList<String>> dictOfAllLists, String whichListToGoTo) {
        this.context = context;
        this.dictOfAllLists = dictOfAllLists;
        this.whichListToGoTo = whichListToGoTo;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // with this code the program knows what content to show in the recyclerview and also from which list it should take the info
        holder.itemNameTxtView.setText(Objects.requireNonNull(this.dictOfAllLists.get(whichListToGoTo)).get(position));

        // when you press the item in the list
        holder.itemNameTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!holder.itemNameTxtView.getText().toString().contains("\n")) {
                    // calling openDialog function from the RecyclerViewListActivity and giving it the whichDialog and the curItemInList String
                    context.openDialog(context.getResources().getString(R.string.edit_item_dialog), holder.itemNameTxtView.getText().toString(), position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        // this method needs to get the length of the recyclerview its showing
        return Objects.requireNonNull(dictOfAllLists.get(whichListToGoTo)).size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // initializing RecyclerView's vars
        TextView itemNameTxtView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            // syncing items
            itemNameTxtView = itemView.findViewById(R.id.nameOfItem);
        }
    }
}
