package com.android.Luvio;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class CustomDialog extends DialogFragment {

    String type = "isBlocked";
    int layout = R.layout.dialog_is_blocked;

    public CustomDialog() {

    }

    public CustomDialog(String type) {
        if(type.equals("rating")) {
            this.type = type;
            layout = R.layout.dialog_rating;
        }
        else if (type.equals("block")) {
            this.type = type;
            layout = R.layout.dialog_block;
        }
        else if (type.equals("isBlocked")) {
            this.type = type;
            layout = R.layout.dialog_is_blocked;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(layout, container, false);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().setTitle(type);
        if(type.equals("rating")) {
            Button btnDone = (Button)view.findViewById(R.id.btnDone);
            btnDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }
        else if(type.equals("block")) {

            Button btnRemove = (Button) view.findViewById(R.id.btnRemove);
            Button btnContinue = (Button) view.findViewById(R.id.btnContinue);
            btnContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }

        return view;
    }
}
