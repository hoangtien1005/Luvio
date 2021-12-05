package com.android.Luvio1.firebase;

import com.android.Luvio1.models.User;
import com.android.Luvio1.utilities.Constants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class DBUserManager {

    private DatabaseReference databaseReference;
    public DBUserManager(){
        FirebaseDatabase db=FirebaseDatabase.getInstance("https://cham-5e6ab-default-rtdb.asia-southeast1.firebasedatabase.app/");
        databaseReference = db.getReference(Constants.KEY_COLLECTION_USER);

    }
    public Task<Void> add(User user)
    {
        return databaseReference.child(user.getFsId()).setValue(user);

    }

    public Task<Void> update(String key, HashMap<String ,Object> hashMap)
    {
        return databaseReference.child(key).updateChildren(hashMap);
    }
    public Task<Void> remove(String key)
    {
        return databaseReference.child(key).removeValue();
    }

    public Query get(String key)
    {
        if(key == null)
        {

            return databaseReference.orderByKey().limitToFirst(8);
        }
        return databaseReference.orderByKey().startAfter(key).limitToFirst(8);
    }

    public Query get()
    {
        return databaseReference.orderByKey();
    }
}
