//package com.android.Luvio.firebase;
//
//import com.android.Luvio.models.User;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.Query;
//
//import java.util.HashMap;
//
//public class FireStoreDBManager {
//
//    private FirebaseFirestore db;
//    public FireStoreDBManager (){
//        db= FirebaseFirestore.getInstance();
//
//
//    }
//
//    public User getUser(String collection,String key){
//        db.collection(collection)
//                .document(key).get()
//                .addOnCompleteListener()
//    }
//
//}
