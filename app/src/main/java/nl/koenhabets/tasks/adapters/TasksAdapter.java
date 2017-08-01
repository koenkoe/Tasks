package nl.koenhabets.tasks.adapters;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.koenhabets.tasks.Api;
import nl.koenhabets.tasks.R;
import nl.koenhabets.tasks.TaskItem;

public class TasksAdapter extends ArrayAdapter<TaskItem> {
    public TasksAdapter(Context context, List<TaskItem> taskItems) {
        super(context, 0, taskItems);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final TaskItem taskItem = getItem(position);

        final String subject = taskItem.getSubject();
        boolean isCompleted = taskItem.isCompleted();
        long ts = taskItem.getDate();
        final String id = taskItem.getId();

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_item, parent, false);
        }

        TextView textViewSubject = (TextView) convertView.findViewById(R.id.textViewSubject);
        final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
        TextView textViewDate = (TextView) convertView.findViewById(R.id.textViewDate);

        Date d = new Date(ts * 1000);
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-mm-dd hh:mm");
        textViewDate.setText(dt.format(d));

        textViewSubject.setText(subject);
        checkBox.setChecked(isCompleted);

        Log.i("pri", taskItem.getPriority() + "");

        if (taskItem.getPriority() == 0){
            textViewSubject.setTextColor(Color.parseColor("#757575"));
        } else if (taskItem.getPriority() == 1){
            textViewSubject.setTextColor(Color.parseColor("#FB8C00"));
        } else if (taskItem.getPriority() == 2){
            textViewSubject.setTextColor(Color.parseColor("#F44336"));
        }

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final boolean isChecked = checkBox.isChecked();
                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                final String userId = currentFirebaseUser.getUid();
                final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

                Query queryRef = database.child("users").child(userId).child("items").orderByChild("id").equalTo(id);

                queryRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                        String key = snapshot.getRef().getKey();
                        database.child("users").child(userId).child("items").child(key).child("completed").setValue(isChecked);
                    }
                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    }
                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                    }
                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });

        return convertView;
    }
}
