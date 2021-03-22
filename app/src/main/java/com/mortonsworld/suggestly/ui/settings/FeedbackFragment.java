package com.mortonsworld.suggestly.ui.settings;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mortonsworld.suggestly.R;
import com.mortonsworld.suggestly.utility.Config;

public class FeedbackFragment extends Fragment implements View.OnClickListener, TextWatcher {
    private EditText feedbackMessage;
    private Button feedbackButton;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        feedbackMessage = view.findViewById(R.id.feedback_message);
        feedbackButton = view.findViewById(R.id.feedback_button);

        feedbackMessage.addTextChangedListener(this);
        feedbackButton.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        if (isMessageLengthLessThanMin()){
            String MESSAGE_ERROR_MESSAGE_ERROR = "Message must be greater than 5 characters";
            feedbackMessage.setError(MESSAGE_ERROR_MESSAGE_ERROR);
            return;
        }

        composeEmail(feedbackMessage.getText().toString());
    }

    public boolean isMessageLengthLessThanMin(){
        return feedbackMessage.getText().length() < 5;
    }

    public void composeEmail(String message) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{Config.SUGGESTLY_EMAIL});
        i.putExtra(Intent.EXTRA_SUBJECT, Config.SUGGESTLY_EMAIL_SUBJECT);
        i.putExtra(Intent.EXTRA_TEXT, message);
        try {
            startActivity(Intent.createChooser(i, "Send feedback..."));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(requireContext(), R.string.email_application_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        feedbackButton.setEnabled(!isMessageLengthLessThanMin());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}