package com.example.contacts_mbaas;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.net.URI;

public class ContactInfo extends AppCompatActivity {

    TextView tvChar, tvName, tvNumber;
    ImageView ivCall, ivMail, ivEdit, ivDelete;
    EditText etName, etNumber, etMail;
    Button btnSave;

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;

    boolean edit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);

        tvChar = findViewById(R.id.tvChar);
        tvName = findViewById(R.id.tvName);
        tvNumber = findViewById(R.id.tvNumber);

        ivCall = findViewById(R.id.ivCall);
        ivMail = findViewById(R.id.ivMail);
        ivEdit = findViewById(R.id.ivEdit);
        ivDelete = findViewById(R.id.ivDelete);

        etName = findViewById(R.id.etName);
        etNumber = findViewById(R.id.etNumber);
        etMail = findViewById(R.id.etMail);

        btnSave = findViewById(R.id.btnSave);

        etName.setVisibility(View.GONE);
        etNumber.setVisibility(View.GONE);
        etMail.setVisibility(View.GONE);
        btnSave.setVisibility(View.GONE);

        final int index = getIntent().getIntExtra("index", 0);

        etName.setText(ApplicationClass.contacts.get(index).getName());
        etNumber.setText(ApplicationClass.contacts.get(index).getNumber());
        etMail.setText(ApplicationClass.contacts.get(index).getEmail());

        tvChar.setText(ApplicationClass.contacts.get(index).getName().toUpperCase().charAt(0) + "");
        tvName.setText(ApplicationClass.contacts.get(index).getName());
        tvNumber.setText(ApplicationClass.contacts.get(index).getNumber());

        ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel: " +
                        ApplicationClass.contacts.get(index).getNumber()));
                startActivity(intent);
            }
        });

        ivMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, ApplicationClass.contacts.get(index).getEmail());
                startActivity(Intent.createChooser(intent, "Send mail to " +
                        ApplicationClass.contacts.get(index).getName()));
            }
        });

        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edit = !edit;

                if(edit) {
                    etName.setVisibility(View.VISIBLE);
                    etNumber.setVisibility(View.VISIBLE);
                    etMail.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.VISIBLE);
                }
                else {
                    etName.setVisibility(View.GONE);
                    etNumber.setVisibility(View.GONE);
                    etMail.setVisibility(View.GONE);
                    btnSave.setVisibility(View.GONE);
                }
            }
        });

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder dialog = new AlertDialog.Builder(ContactInfo.this);

                dialog.setMessage("Are you sure you want to delete this contact?");
                dialog.setTitle("Delete contact");

                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        showProgress(true);
                        tvLoad.setText("Deleting contact. please wait...");

                        Backendless.Persistence.of(Contact.class).remove(ApplicationClass.contacts.get(index),
                                new AsyncCallback<Long>() {
                                    @Override
                                    public void handleResponse(Long response) {

                                        ApplicationClass.contacts.remove(index);
                                        Toast.makeText(ContactInfo.this, "Contact deleted successfully!", Toast.LENGTH_SHORT).show();

                                        setResult(RESULT_OK);
                                        ContactInfo.this.finish();

                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {

                                        Toast.makeText(ContactInfo.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                                        showProgress(false);
                                    }
                                });
                    }
                });

                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                dialog.show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etName.getText().toString().isEmpty() || etMail.getText().toString().isEmpty() ||
                        etNumber.getText().toString().isEmpty()) {
                    Toast.makeText(ContactInfo.this, "Please enter all the fields!", Toast.LENGTH_SHORT).show();
                }
                else {

                    ApplicationClass.contacts.get(index).setName(etName.getText().toString().trim());
                    ApplicationClass.contacts.get(index).setNumber(etNumber.getText().toString().trim());
                    ApplicationClass.contacts.get(index).setEmail(etMail.getText().toString().trim());

                    showProgress(true);
                    tvLoad.setText("Updating contact. please wait...");

                    Backendless.Persistence.save(ApplicationClass.contacts.get(index), new AsyncCallback<Contact>() {
                        @Override
                        public void handleResponse(Contact response) {

                            tvChar.setText(ApplicationClass.contacts.get(index).getName().toUpperCase().charAt(0) + "");
                            tvName.setText(ApplicationClass.contacts.get(index).getName());
                            tvNumber.setText(ApplicationClass.contacts.get(index).getNumber());

                            Toast.makeText(ContactInfo.this, "Contact updated successfully!", Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {

                            Toast.makeText(ContactInfo.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        }
                    });
                }
            }
        });
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

            tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
