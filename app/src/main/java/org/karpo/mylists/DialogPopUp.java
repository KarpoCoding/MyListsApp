package org.karpo.mylists;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.ArrayList;

public class DialogPopUp extends AppCompatDialogFragment {
    private EditText addItemEdtTxtDialog, editItemEdtTxtDialog, addSeriesDialogEdtTxt;
    private DialogPopUpListener listener;
    private final String whichDialog, curItemInList;
    private final int position;
    private final RecyclerViewListActivity context;
    private ImageView addSeriesDialogAddASeriesButton, addSeriesDialogDeleteSeriesButton;

    private Spinner addSeriesDialogSpinner, seriesSpinnerEditItemDialog, seriesIndexSpinnerEditItemDialog;
    private ArrayAdapter<String> seriesListAdapter, seriesSpinnerEditItemDialogAdapter, seriesIndexSpinnerEditItemDialogAdapter;
    private TextView seriesTextViewEditItemDialog, belongsToSeriesTxtViewEditItemDialog, theSeriesItBelongsToTxtViewEditItemDialog, seriesEndingOfSeriesNameTxtView, seriesIndexTxtViewEditItemDialog, seriesIndexTxtViewEditItemDialogActualIndex;
    private Button applyItemsSeriesInfoButtonEditItemDialog, resetItemsSeriesInfoButtonEditItemDialog;

    public DialogPopUp(RecyclerViewListActivity context, String whichDialog, String curItemInList, int position) {
        this.context = context;
        this.whichDialog = whichDialog;
        this.curItemInList = curItemInList;
        this.position = position;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // setting the inflater var
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // creating the alert dialog builder var
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // if the add_item dialog has been called
        if (this.whichDialog.equals(getResources().getString(R.string.add_item_dialog))) {
            // getting the layout file we want to show when showing the dialog (i think)
            View viewAddItem = inflater.inflate(R.layout.add_item_dialog, null);

            // creating the dialog
            builder.setView(viewAddItem)
                    .setTitle(getResources().getString(R.string.add_item_dialog_title))
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        // when you press the Cancel button
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })

                    // todo: add a thingy where you can assign items selected to a certain series and choose which index they are on the series
                    // when you press the Add button
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String itemToAdd = addItemEdtTxtDialog.getText().toString();
                            // the listener takes the text and then sends it to the RecyclerViewListActivity
                            listener.addItem(itemToAdd);
                        }
                    });
            // initializing the addItem edit text
            addItemEdtTxtDialog = viewAddItem.findViewById(R.id.addItemEdtTxtDialog);
        }

        // if the add_series dialog has been called
        if (this.whichDialog.equals(getResources().getString(R.string.add_series_dialog))) {
            View viewAddSeries = inflater.inflate(R.layout.add_series_dialog, null);

            // creating the dialog
            builder.setView(viewAddSeries)
                    .setTitle(getResources().getString(R.string.add_series_dialog_title))
                    // when you press the Add button
                    .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            
                        }
                    });

            // initializing the addSeries Dialog stuff
            addSeriesDialogEdtTxt = viewAddSeries.findViewById(R.id.addSeriesDialogEdtTxt);
            addSeriesDialogSpinner = viewAddSeries.findViewById(R.id.addSeriesDialogSpinner);
            addSeriesDialogAddASeriesButton = viewAddSeries.findViewById(R.id.addSeriesDialogAddASeriesButton);
            addSeriesDialogDeleteSeriesButton = viewAddSeries.findViewById(R.id.addSeriesDialogDeleteSeriesButton);

            // creating an adapter for the series spinner and then applying it to the spinner
            seriesListAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, context.seriesList);
            addSeriesDialogSpinner.setAdapter(seriesListAdapter);

            // when you click the add button inside the addSeries dialog
            addSeriesDialogAddASeriesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // taking the info that's written in the editText
                    String seriesToAdd = addSeriesDialogEdtTxt.getText().toString();
                    if (!seriesToAdd.equals("")) {
                        // the listener adds the series to the series list
                        listener.addSeries(seriesToAdd);
                        // clears the edit text
                        addSeriesDialogEdtTxt.setText("");
                        // toast message 'seriesToAdd' added to series
                        Toast.makeText(context, "'" + seriesToAdd + "' series added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "The field is empty!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // when you click the delete button inside the addSeries dialog - deletes series from the series list
            addSeriesDialogDeleteSeriesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // makes sure the spinner isn't empty before proceeding
                    if (addSeriesDialogSpinner != null && addSeriesDialogSpinner.getSelectedItem() != null) {
                        // takes the size of the series list
                        int sizeOfSeries = context.seriesList.size();

                        // opening an extra dialog to make sure the user really wants to delete the series
                        listener.areYouSureYouWantToDelete(getResources().getString(R.string.are_you_sure_you_want_to_delete_dialog), addSeriesDialogSpinner.getSelectedItem().toString());

                        // checks if the size of the series list is the same, if not proceeds to refresh the list
                        if (context.seriesList.size() != sizeOfSeries) {
                            // putting the info inside the series list in a brand new list then using it to refresh the seriesAdapter
                            ArrayList<String> newSeriesList = new ArrayList<>();
                            newSeriesList.addAll(context.seriesList);

                            // refreshing the adapter
                            seriesListAdapter.clear();
                            seriesListAdapter.addAll(newSeriesList);
                        }

                    // if the spinner is somehow empty, toast message: something went wrong
                    } else {
                        Toast.makeText(context, "Something went wrong..", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // if the are_you_sure_you_want_to_delete dialog has been called
        if (whichDialog.equals(getResources().getString(R.string.are_you_sure_you_want_to_delete_dialog))) {
            // creating the dialog
            builder.setTitle(getResources().getString(R.string.are_you_sure_you_want_to_delete_dialog_title) + " '" + curItemInList + "'?")
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        // when you click the 'No' button
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })

                    // when you press the 'Yes' button
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // calling the deleteSeries method on the cur series in list
                            listener.deleteSeries(curItemInList);
                        }
                    });
        }

        // if the clear_all dialog has been called
        if (this.whichDialog.equals(getResources().getString(R.string.clear_all_dialog))) {
            // creating the dialog
            builder.setTitle(getResources().getString(R.string.clear_all_dialog_title))
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        // when you press the Cancel button
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })

                    // when you press the Clear button
                    .setPositiveButton("Clear", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // the listener takes the current list and clears it
                            listener.listToClear();
                        }
                    });
        }

        // if the edit_item dialog has been called
        if (this.whichDialog.equals(getResources().getString(R.string.edit_item_dialog))) {
            // getting the layout file we want to show when showing the dialog (i think)
            View viewEditItem = inflater.inflate(R.layout.edit_item_dialog, null);

            // gets the series info of the item
            ArrayList<String> curItemSeriesInfo = listener.getItemSeriesInfo(curItemInList);
            // set the info of the item into two vars - 'seriesItBelongsTo' and 'seriesIndex'
            String seriesItBelongsTo = curItemSeriesInfo.get(0);
            String seriesIndex = curItemSeriesInfo.get(1);

            // creating the dialog
            builder.setView(viewEditItem)
                    .setTitle(getResources().getString(R.string.edit_item_dialog_title))
                    .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                        // when you press the Delete button
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            listener.deleteItem(curItemInList, position);
                        }
                    })

                    // when you press the Apply button
                    .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            // initializing the editItem Edit Text
            editItemEdtTxtDialog = viewEditItem.findViewById(R.id.editItemEdtTxtDialog);
            editItemEdtTxtDialog.setText(curItemInList);

            // initializing TextViews
            seriesTextViewEditItemDialog = viewEditItem.findViewById(R.id.seriesTextViewEditItemDialog);
            seriesIndexTxtViewEditItemDialogActualIndex = viewEditItem.findViewById(R.id.seriesIndexTxtViewEditItemDialogActualIndex);
            theSeriesItBelongsToTxtViewEditItemDialog = viewEditItem.findViewById(R.id.theSeriesItBelongsToTxtViewEditItemDialog);

            // initializing the textViews that are just for view
            belongsToSeriesTxtViewEditItemDialog = viewEditItem.findViewById(R.id.belongsToSeriesTxtViewEditItemDialog);
            seriesEndingOfSeriesNameTxtView = viewEditItem.findViewById(R.id.seriesEndingOfSeriesNameTxtView);
            seriesIndexTxtViewEditItemDialog = viewEditItem.findViewById(R.id.seriesIndexTxtViewEditItemDialog);

            // initializing Buttons
            applyItemsSeriesInfoButtonEditItemDialog = viewEditItem.findViewById(R.id.applyItemsSeriesInfoButtonEditItemDialog);
            resetItemsSeriesInfoButtonEditItemDialog = viewEditItem.findViewById(R.id.resetItemsSeriesInfoButtonEditItemDialog);

            // initializing series's spinners, the series spinner snd seriesIndex spinner
            seriesSpinnerEditItemDialog = viewEditItem.findViewById(R.id.seriesSpinnerEditItemDialog);
            seriesIndexSpinnerEditItemDialog = viewEditItem.findViewById(R.id.seriesIndexSpinnerEditItemDialog);

            // setting the adapter for the seriesSpinnerEditItemDialog
            seriesSpinnerEditItemDialogAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, context.seriesEditItemList);
            seriesSpinnerEditItemDialog.setAdapter(seriesSpinnerEditItemDialogAdapter);

            // if the series belongs to a series then show it, if not then it won't show
            if (!seriesItBelongsTo.equals(getResources().getString(R.string.none))) {
                // making the textViews visible
                makeSeriesGuiInEditItemDialogVisible();

                // using the values of the item's series info to update the TextViews
                theSeriesItBelongsToTxtViewEditItemDialog.setText(seriesItBelongsTo);
                seriesIndexTxtViewEditItemDialogActualIndex.setText(seriesIndex);
            }

            // when you select an item on the series spinner
            seriesSpinnerEditItemDialog.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // if the item selected is not 'None' then make the seriesIndexSpinner visible and set its adapter
                    if (!seriesSpinnerEditItemDialog.getSelectedItem().toString().equals(getResources().getString(R.string.series_edit_item_list_placeholder))) {
                        seriesIndexSpinnerEditItemDialog.setVisibility(View.VISIBLE);
                        ArrayList<String> seriesIndexes = new ArrayList<>();

                        // adding the index count to seriesIndexes list
                        for (int i = 1; i < listener.howManyItemsInSeries(seriesSpinnerEditItemDialog.getSelectedItem().toString()) + 1; i++) {
                            seriesIndexes.add(Integer.toString(i));
                        }

                        // creating the adapter and setting it
                        seriesIndexSpinnerEditItemDialogAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, seriesIndexes);
                        seriesIndexSpinnerEditItemDialog.setAdapter(seriesIndexSpinnerEditItemDialogAdapter);
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
//                    seriesIndexSpinnerEditItemDialog.setVisibility(View.GONE);
                }
            });

            // when you press the 'Apply Series' button
            applyItemsSeriesInfoButtonEditItemDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // getting the info from the series Spinner and seriesIndex spinner and applying it on the item's info in the DB
                    String seriesItBelongsToUpdated = seriesSpinnerEditItemDialog.getSelectedItem().toString();

                    // takes the text written on the editItemEdtTxtDialog EditText and puts it into a var - updatedItemText
                    String updatedItemText = editItemEdtTxtDialog.getText().toString();

                    // if the spinner isn't selecting 'None' then save all of the series info
                    if (!seriesItBelongsToUpdated.equals(getResources().getString(R.string.none))) {
                        String seriesIndex = seriesIndexSpinnerEditItemDialog.getSelectedItem().toString();

                        // update the item's series info
                        listener.applyItemsSeriesInfoInDB(updatedItemText, seriesItBelongsToUpdated, Integer.parseInt(seriesIndex));

                        // also updating the TextViews accordingly
                        theSeriesItBelongsToTxtViewEditItemDialog.setText(seriesItBelongsToUpdated);
                        seriesIndexTxtViewEditItemDialogActualIndex.setText(seriesIndex);

                        // making the textViews visible
                        makeSeriesGuiInEditItemDialogVisible();

                        // refreshes the items in the list
                        listener.refreshItemsInList();

                        // toast message: item's series info updated
                        Toast.makeText(context, "'" + updatedItemText + "' series info updated", Toast.LENGTH_SHORT).show();
                    }
//                    } else {
//                        // toast message: You're not selecting a series!
//                        Toast.makeText(context, "You're not selecting a series!", Toast.LENGTH_SHORT).show();
//                    }
                    // edits the item if it's text had changed
                    if (!curItemInList.equals(updatedItemText)) {
                        listener.editItem(curItemInList, updatedItemText, position);
                    }
                }
            });

            // when you press the 'Reset Series Info' button - it resets all its info
            resetItemsSeriesInfoButtonEditItemDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // making the textViews not visible
                    makeSeriesGuiInEditItemDialogNotVisible();
                    // resets of the series info to default values and refreshes the list
                    listener.applyItemsSeriesInfoInDB(editItemEdtTxtDialog.getText().toString(), getResources().getString(R.string.none), 0);
                    listener.refreshItemsInList();
                    // toast message: item's series info has been reset
                    Toast.makeText(context, "'" + editItemEdtTxtDialog.getText().toString() + "' series info has been reset", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // returning the builder
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // try and except
        try {
            listener = (DialogPopUpListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement DialogPopUpListener");
        }
    }

    // dialog pop ups methods
    public interface DialogPopUpListener {
        void addItem(String item);
        void addSeries(String seriesToAdd);
        void deleteSeries(String seriesToDelete);
        ArrayList<String> getItemSeriesInfo(String curItemInList);
        int howManyItemsInSeries(String selectedSeries);
        void refreshItemsInList();
        void applyItemsSeriesInfoInDB(String curItemInList, String seriesItBelongsTo, int seriesIndex);
        void areYouSureYouWantToDelete(String whichDialog, String curSeriesInList);
        void editItem(String curItemInList, String newItemValue, int position);
        void deleteItem(String itemToDelete, int position);
        void listToClear();
    }

    /**
     * making all of the TextViews in the editItem dialog related to Series visible.
     */
    private void makeSeriesGuiInEditItemDialogVisible() {
        belongsToSeriesTxtViewEditItemDialog.setVisibility(View.VISIBLE);
        theSeriesItBelongsToTxtViewEditItemDialog.setVisibility(View.VISIBLE);
        seriesEndingOfSeriesNameTxtView.setVisibility(View.VISIBLE);
        seriesIndexTxtViewEditItemDialog.setVisibility(View.VISIBLE);
        seriesIndexTxtViewEditItemDialogActualIndex.setVisibility(View.VISIBLE);
        seriesIndexSpinnerEditItemDialog.setVisibility(View.VISIBLE);
    }

    /**
     * making all of the TextViews in the editItem dialog related to Series visible AND ALSO THE INDEX SPINNER.
     */
    private void makeSeriesGuiInEditItemDialogNotVisible() {
        belongsToSeriesTxtViewEditItemDialog.setVisibility(View.GONE);
        theSeriesItBelongsToTxtViewEditItemDialog.setVisibility(View.GONE);
        seriesEndingOfSeriesNameTxtView.setVisibility(View.GONE);
        seriesIndexTxtViewEditItemDialog.setVisibility(View.GONE);
        seriesIndexTxtViewEditItemDialogActualIndex.setVisibility(View.GONE);
        seriesIndexSpinnerEditItemDialog.setVisibility(View.GONE);
    }
}
