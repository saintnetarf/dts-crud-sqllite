package id.go.kominfo.fitri.aplikasisqlite1;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


import id.go.kominfo.fitri.aplikasisqlite1.adapter.FriendAdapter;
import id.go.kominfo.fitri.aplikasisqlite1.dbhelper.FriendDB;
import id.go.kominfo.fitri.aplikasisqlite1.model.Friend;

public class MainActivity extends AppCompatActivity {
    public static final String MODE = "mode";
    public static final String FRIEND = "friend";
    public static final String FRIENDS = "friends";
    public static final int ADD_MODE = 0;
    public static final int VIEW_MODE = 1;
    public static final int EDIT_MODE = 2;
    public static final String POSITION = "position";

    private final List<Friend> mList = new ArrayList<>();
    private FriendAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar1));

        mAdapter = new FriendAdapter(mList, this);

        if (savedInstanceState != null) {
            mList.clear();
            //noinspection unchecked
            mList.addAll((Collection<? extends Friend>) Objects.requireNonNull(savedInstanceState.getSerializable(FRIENDS)));
        }

        ListView listView = findViewById(R.id.listview);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(this::onItemClick);
        listView.setOnItemLongClickListener(this::onItemLongClick);

        findViewById(R.id.fab).setOnClickListener(this::addItem);
    }

    private void addItem(View view) {
        Intent intent = new Intent(this, AddAndViewActivity.class);
        intent.putExtra(MainActivity.MODE, MainActivity.ADD_MODE);
        resultLauncher.launch(intent);
    }

    private boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        CharSequence[] items = {"Edit", "Delete"};
        int[] checked = {-1};
        new AlertDialog.Builder(this)
                .setTitle("Your Action?")
                .setSingleChoiceItems(items, checked[0], (dialogInterface, which) -> checked[0]=which)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Yes", (dialogInterface, which) -> {
                    switch (checked[0]) {
                        case 0 : //edit
                            Intent intent = new Intent(this, AddAndViewActivity.class);
                            intent.putExtra(MainActivity.MODE, MainActivity.EDIT_MODE);
                            intent.putExtra(MainActivity.POSITION, i);
                            intent.putExtra(MainActivity.FRIEND, mList.get(i));
                            resultLauncher.launch(intent);
                            break;
                        case 1 : //delete
                            new AlertDialog.Builder(this)
                                    .setTitle("Confirm")
                                    .setMessage("Delete " + mList.get(i).toString() + "?")
                                    .setNegativeButton("Cancel", null)
                                    .setPositiveButton("Yes", (dialogInterface1, i1) -> {
                                        try (FriendDB dbOne = new FriendDB(this)) {
                                            dbOne.delete(mList.get(i).getId());
                                        }
                                        mList.remove(i);
                                        mAdapter.notifyDataSetChanged();
                                    }).show();
                            break;
                    }
                }).show();
        return true;
    }

    private void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, AddAndViewActivity.class);
        intent.putExtra(MainActivity.MODE, MainActivity.VIEW_MODE);
        intent.putExtra(MainActivity.FRIEND, mList.get(i));
        startActivity(intent);
    }

    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            if (result.getData().getIntExtra(MainActivity.MODE, -1) == MainActivity.ADD_MODE) {
                Friend friend = (Friend) result.getData().getSerializableExtra(FRIEND);
                assert friend != null;
                try (FriendDB dbOne = new FriendDB(this)) {
                    dbOne.insert(friend);
                }
            } else if (result.getData().getIntExtra(MainActivity.MODE, -1) == MainActivity.EDIT_MODE) {
                Friend friend = (Friend) result.getData().getSerializableExtra(FRIEND);
                assert friend != null;
                try (FriendDB dbOne = new FriendDB(this)) {
                    dbOne.update(friend);
                }
            }
        }
    });

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //kalau menu item mi_about di klik
        //tampilkan pesan
        if (item.getItemId() == R.id.mi_about) {
            new AlertDialog.Builder(this)
                    .setTitle("Info")
                    .setMessage(R.string.about_msg)
                    .setPositiveButton("OK", null).show();
        }
        //kalau menu item mi_exit di klik tutup aplikasi
        else if(item.getItemId() == R.id.mi_exit) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("Close App?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Yes", (dialog, which) -> finish()).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(MainActivity.FRIENDS, (Serializable) mList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try (FriendDB db = new FriendDB(this)) {
            mList.clear();
            mList.addAll(db.getAllFriends());
            mAdapter.notifyDataSetChanged();
        }


    }
}