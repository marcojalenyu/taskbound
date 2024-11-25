package com.mobdeve.s17.taskbound;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;

public class ProfileEditFragment extends DialogFragment {

    // UI components
    private EditText etUserName;
    private Button btnSaveDetails;
    private ImageView ivUserPicture;
    // Data components
    private User currentUser;
    private DatabaseReference cloudTaskDB;
    private LocalDBManager localDB;

    /**
     * Default constructor for ProfileEditFragment
     */
    public ProfileEditFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_profile, container, false);
        initializeUI(view);
        initializeData();
        return view;
    }

    /**
     * This method initializes the UI components of the dialog box.
     * @param view - the view of the dialog box
     */
    private void initializeUI(View view) {
        this.etUserName = view.findViewById(R.id.usernameEdit);
        this.btnSaveDetails = view.findViewById(R.id.saveButton);
        this.ivUserPicture = view.findViewById(R.id.profileImage);
        btnSaveDetails.setOnClickListener(this::btnClickedSaved);
        ivUserPicture.setOnClickListener(this::btnClickedProfile);
    }

    private void initializeData() {
        this.currentUser = UserSession.getInstance().getCurrentUser();
        this.cloudTaskDB = FirebaseDatabase.getInstance().getReference("tasks").child(currentUser.getUserID());
        this.localDB = new LocalDBManager(getContext());

        this.etUserName.setText(this.currentUser.getUserName());
        this.ivUserPicture.setImageResource(this.currentUser.getCollectiblesList().get(this.currentUser.getPicture()).getCollectibleImage());
    }

    /**
     * This method is called when the save button is clicked.
     */
    public void btnClickedSaved(View v) {
        // Get the task information
        String name = String.valueOf(etUserName.getText());
        currentUser.setUserName(name);
        try {
            localDB.updateUser(currentUser);
            dismiss();
            ((HomeActivity) getActivity()).onResume();
        } catch (Exception e) {
            Log.e("LoginReal", e + "");
        }
    }

    public void btnClickedProfile(View v) {
        Intent intent = new Intent(getContext(), CollectiblesActivity.class);
        startActivity(intent);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.CENTER;
            window.setAttributes(params);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
}
