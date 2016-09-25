package br.pucminas.databaseproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by luket on 25-Sep-16.
 */
public class AdapterListUser extends BaseAdapter {
    private final MainActivity mainActivity;
    private List<User> users;
    private LayoutInflater inflater;

    public AdapterListUser(MainActivity mainActivity, List<User> users) {
        this.mainActivity = mainActivity;
        this.users = users;

        //Inflate MainActivity
        inflater = mainActivity.getLayoutInflater();

    }

    @Override
    public int getCount() {

        return users != null ? users.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        //Get student of position from list
        final User user = users.get(position);

        //Instantiate holder (butterknife)
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.adapter_item_user, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        //Set student's info
        holder.txtName.setText(user.getName());
        holder.txtEmail.setText(user.getEmail().toString());
        holder.txtPassword.setText(user.getPassword());

        //Set click of delete button
        holder.imgUpdate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mainActivity.showDialogUpdateUser(user);
            }
        });

        //Set click of delete button
        holder.imgDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                //Set confirmation message
                builder.setMessage("Do you wish to delete '" + user.getName() + "'?");
                //Set positive button behavior, deleting student
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Delete student passing his Id
                        mainActivity.callDeleteUser(user);
                    }
                });
                //Set negative button, cancel
                builder.setNegativeButton("No", null);
                AlertDialog dialog = builder.create();
                //Show dialog
                dialog.show();
            }
        });

        return view;
    }

    static class ViewHolder {
        @BindView(R.id.txtName)
        TextView txtName;
        @BindView(R.id.txtEmail)
        TextView txtEmail;
        @BindView(R.id.txtPassword)
        TextView txtPassword;
        @BindView(R.id.imgUpdate)
        ImageView imgUpdate;
        @BindView(R.id.imgDelete)
        ImageView imgDelete;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
