package br.pucminas.databaseproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;

import br.pucminas.databaseproject.bd.UserDAO;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements Validator.ValidationListener{

    //Layout for main activity
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    //Progress bar
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    //List of students
    @BindView(R.id.listUsers)
    ListView listUsers;

    private List<User> list;

    //New user input fields
    //Name
    @NotEmpty(messageResId = R.string.msg_error_not_empty)
    EditText edtName;
    //Age
    @NotEmpty(messageResId = R.string.msg_error_not_empty)
    EditText edtEmail;
    //Phone
    @NotEmpty(messageResId = R.string.msg_error_not_empty)
    EditText edtPassword;

    //Instance of API service
    //private APIService service;
    //Instance of list adapter
    private AdapterListUser adapter;
    //Instance of validator fields
    private Validator validator;
    //
    private boolean isUpdating;
    private User userUpdating;
    //Instance of dialog form (needed to be global for validation dismiss)
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Binding butterknife to this activity
        ButterKnife.bind(this);

        validator = new Validator(this);
        validator.setValidationListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAddUser();
            }
        });

        //Get list items
        loadList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            showDialogAbout();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onValidationSucceeded() {

        //Get contents from input fields
        String name = edtName.getText().toString();
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();

        if (isUpdating) {
            //Update info
            userUpdating.setName(name);
            userUpdating.setEmail(email);
            userUpdating.setPassword(password);

            //Update user
            callUpdateUser(userUpdating);
        }
        else //Instantiate new user and save it
            callAddUser(new User(name, email, password));
        dialog.dismiss();

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    void loadList(){
        //Make progressbar visible while populating the list
        progressBar.setVisibility(View.VISIBLE);

        UserDAO bd = new UserDAO(this);
        list = bd.Read();

        //Instantiate adapter for list of users
        adapter = new AdapterListUser(MainActivity.this, list);
        listUsers.setAdapter(adapter);

        //Make progressbar visible while populating the list
        progressBar.setVisibility(View.GONE);

    }

    private void showDialogAddUser(){
        //Create inflater
        LayoutInflater inflater = getLayoutInflater();
        //Inflate dialog
        final View view = inflater.inflate(R.layout.dialog_add_user, null);

        //Get all fields from form
        edtName = ButterKnife.findById(view, R.id.edtName);
        edtEmail = ButterKnife.findById(view, R.id.edtEmail);
        edtPassword = ButterKnife.findById(view, R.id.edtPassword);

        //Create builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //Set custom view
        builder.setView(view);
        //Set title
        builder.setTitle(R.string.dialog_title_new_user);
        //Set message
        builder.setMessage(R.string.dialog_msg_required);

        //Set positive button behavior (save)
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Do nothing here because we override this button later to change the close behaviour.
                //However, we still need this because on older versions of Android unless we
                //pass a handler the button doesn't get instantiated
            }
        });

        //Set negative button behavior (cancel)
        builder.setNegativeButton("Cancel", null);

        //Create and show dialog
        dialog = builder.create();
        //Prevent dismiss from outside touch
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();

        //Custom click (needed for avoiding dismissing the dialog when errors occurred)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Validate input fields
                isUpdating = false;
                validator.validate();
            }
        });
    }

    public void showDialogUpdateUser(User user){
        //Create inflater
        LayoutInflater inflater = getLayoutInflater();
        //Inflate dialog
        final View view = inflater.inflate(R.layout.dialog_add_user, null);

        //Get all fields from form
        edtName = ButterKnife.findById(view, R.id.edtName);
        edtEmail = ButterKnife.findById(view, R.id.edtEmail);
        edtPassword = ButterKnife.findById(view, R.id.edtPassword);

        //Fill fields with current values
        edtName.setText(user.getName());
        edtEmail.setText(user.getEmail());
        edtPassword.setText(user.getPassword());

        userUpdating = user;

        //Create builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //Set custom view
        builder.setView(view);
        //Set title
        builder.setTitle(R.string.dialog_title_update_user);
        //Set message
        builder.setMessage(R.string.dialog_msg_required);

        //Set positive button behavior (save)
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Do nothing here because we override this button later to change the close behaviour.
                //However, we still need this because on older versions of Android unless we
                //pass a handler the button doesn't get instantiated
            }
        });

        //Set negative button behavior (cancel)
        builder.setNegativeButton("Cancel", null);

        //Create and show dialog
        dialog = builder.create();
        //Prevent dismiss from outside touch
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();

        //Custom click (needed for avoiding dismissing the dialog when errors occurred)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                isUpdating = true;
                //Validate input fields
                validator.validate();
            }
        });
    }

    private void callAddUser(User user){
            UserDAO bd = new UserDAO(this);
            bd.Create(user);

            //Refresh list
            loadList();

            Toast.makeText(this, "Created successfully!!", Toast.LENGTH_SHORT).show();
    }

    public void callDeleteUser(User user){
        UserDAO bd = new UserDAO(this);
        bd.Delete(user);

        //Refresh list
        loadList();

        Toast.makeText(this, "Deleted successfully!!", Toast.LENGTH_SHORT).show();
    }

    private void callUpdateUser(User user){
        UserDAO bd = new UserDAO(this);
        bd.Update(user);

        //Refresh list
        loadList();

        Toast.makeText(this, "Updated successfully!!", Toast.LENGTH_SHORT).show();
    }

    private void showDialogAbout(){

        //Create builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //Set title
        builder.setTitle(R.string.dialog_title_about);

        builder.setMessage("Student: Lucas Miranda de Oliveira\nId: 67505\n\nProject for \"Databases for Mobile Devices\"." );

        //Set positive button behavior (save)
        builder.setNeutralButton("Close", null);

        //Create and show dialog
        dialog = builder.create();
        dialog.show();
    }
}
