package com.example.qrapp;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * List Adapter for QRCode that has a remove button
 * on each item to allow the user to remove the QRCode from their account
 * NOTE: This adapter is only used for the current user's QR codes, the button is preset to
 * remove the QRCode from the current user's account. using this adapter for another user's
 * QR codes will cause the button to remove the QRCode from the current user's account.
 * will throw an error if the current user doesn't have the QRCode in their account,
 * but I cant do any further checks because the adapter is not aware of the current user
 */
public class RemovableQRCAdapter extends QRcAdapter {

    private FirebaseFirestore db;


    public RemovableQRCAdapter(ArrayList<QRCode> items, Context context) {
        super(items, context);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(mycontext);
        View row = inflater.inflate(R.layout.item_qrc_removable, viewGroup, false);
        QRCode qrCode = items.get(i);
        TextView qrCodeName = row.findViewById(R.id.QRCName);
        TextView qrCodePoints = row.findViewById(R.id.score);
        TextView qrCodeIcon = row.findViewById(R.id.visual);
        qrCodeName.setText(qrCode.getName());
        qrCodePoints.setText(qrCode.getPoints().toString());
        qrCodeIcon.setText(qrCode.getIcon());
        Button removeButton = row.findViewById(R.id.button_remove);

        // set the remove button listener
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // remove the QRCode
                removeQRCode(i);
            }
        });


        return row;
    }

    /**
     * Remove the QRCode from the database and the local list
     * @return true if the QRCode was removed successfully, false otherwise
     */
    private boolean removeQRCode(int targetIndex) {
        // get the QRCode
        QRCode targetQRCode = items.get(targetIndex);
        //check that the user is logged in
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // raise a toast informing the user there is something wrong with their login,
            // then return without doing anything
            Toast.makeText(mycontext, "You are not logged in, please log in and try again", Toast.LENGTH_SHORT).show();
            return false;
        }
        //get the current users id
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //check that the QRCode is in the current user's account
        Object playersScannedRaw = targetQRCode.getPlayersScanned();
        ArrayList<String> playersScanned = null;
        String ErrorString = "The QRCode is not in the current user's account, this likely means the adapter 'RemovableQRCAdapter' is being used for a user other than the current user.";
        if (playersScannedRaw instanceof ArrayList){
            playersScanned = (ArrayList<String>) playersScannedRaw;
            if (!playersScanned.contains(currentUserId)) {
                //if this happens the class is being used incorrectly, throw an error
                throw new Error(ErrorString);
            }
        } else {
            throw new Error(ErrorString);
        }

        // remove the current user from the QRCode's list of players scanned
        playersScanned.remove(currentUserId);
        // update the database with the new list of players scanned, wait for the update to complete
        Task updateJob = db.collection("QRCodes").document(targetQRCode.getHashed()).update("playersScanned", playersScanned);

        // wait for the update to complete
        while (!updateJob.isComplete()) {
            // do nothing
        }

        // check that the update was successful
        if (!updateJob.isSuccessful()) {
            // raise a toast informing the user that the update failed
            Toast.makeText(mycontext, "Failed to remove QRCode from your account", Toast.LENGTH_SHORT).show();
            return false;
        }

        // remove the QRCode from the list
        items.remove(targetIndex);
        // notify the adapter that the data has changed
        notifyDataSetChanged();

        return true;
    }

}
