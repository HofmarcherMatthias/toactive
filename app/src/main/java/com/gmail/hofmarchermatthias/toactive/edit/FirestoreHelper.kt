package com.gmail.hofmarchermatthias.toactive.edit

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

object FirestoreHelper {
    fun push(item:Any, path:String){


    }

    fun fetchFromDocument(requester:OnSuccessListener<Any>, objClass:Class<Any>, path:String){
        val db = FirebaseFirestore.getInstance()
        val doc = db.document(path)
        doc.get().addOnSuccessListener(requester)
    }


    public interface FirestoreDataReceiver{
        public fun onFirestoreDataFetched(obj:Any)
    }
}