package com.example.pika2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ExitTaskDialogFragment extends DialogFragment {

    public interface ExitTaskDialogListener {
        void onDialogPositiveClick(DialogFragment dialogFragment);
        void onDialogNegativeClick(DialogFragment dialogFragment);
    }

    ExitTaskDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (ExitTaskDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(requireActivity() +
                    "must implement ExitTaskDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure EXIT the task? Current task progress will lose.")
                .setPositiveButton("EXIT", (dialog, which) -> listener
                        .onDialogPositiveClick(ExitTaskDialogFragment.this))
                .setNegativeButton("CANCEL", (dialog, which) -> listener
                        .onDialogNegativeClick(ExitTaskDialogFragment.this));
        return builder.create();
    }
}
