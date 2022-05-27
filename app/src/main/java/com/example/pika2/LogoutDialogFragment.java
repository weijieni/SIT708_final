package com.example.pika2;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


public class LogoutDialogFragment extends DialogFragment {

    public interface LogoutDialogListener{
        void onDialogPositiveClick(DialogFragment dialogFragment);
        void onDialogNegativeClick(DialogFragment dialogFragment);
    }

    LogoutDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (LogoutDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(requireActivity() + "must implement LogoutDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure LOG OUT?")
                .setPositiveButton("LOG OUT", (dialog, which) -> listener
                        .onDialogPositiveClick(LogoutDialogFragment.this))
                .setNegativeButton("CANCEL", (dialog, which) -> listener
                        .onDialogNegativeClick(LogoutDialogFragment.this));
        return builder.create();
    }
}
