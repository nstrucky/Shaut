package com.ventoray.shaut.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ventoray.shaut.R;

public class MainActivity extends AppCompatActivity {

    Button button1;
    Button button2;
    Toast toast;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);

        button1.setOnClickListener(clickListener);
        button2.setOnClickListener(clickListener);

        toast = Toast.makeText(this, null, Toast.LENGTH_SHORT);
    }



    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button1:
                    optionOne();
                    break;

                case R.id.button2:
                    opitonTwo();
                    break;
            }
            toast.show();
        }
    };


    private void optionOne() {
        toast.setText("option 1");

        DatabaseReference ref = database.getReference("users");

        ref.orderByChild("friends/Nick").equalTo(true).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String key = snapshot.getKey();
                            Log.d("MainActivity", "Key: " +key);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );



    }

    private void opitonTwo() {
        toast.setText("option 2");
    }


}
