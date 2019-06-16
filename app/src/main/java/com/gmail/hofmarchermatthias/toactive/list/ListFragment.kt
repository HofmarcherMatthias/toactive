package com.gmail.hofmarchermatthias.toactive.list

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.gmail.hofmarchermatthias.toactive.R
import com.gmail.hofmarchermatthias.toactive.edit.EditSampleFragment
import com.gmail.hofmarchermatthias.toactive.model.Appointment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_list.*



/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ListFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ListFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ListFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null

    //region database
    private val db = FirebaseFirestore.getInstance()
    private val notebookRef = db.collection("Users")
        .document(FirebaseAuth.getInstance().uid!!)
        .collection("Data")
    //endregion database

    private lateinit var appointmentAdapter: AppointmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab_add_appointment.setOnClickListener {
            this.editAppointment("")
        }


        setUpRecyclerView()
    }

    /**
     * Function invokes EditSampleFragment
     * @param id can be passed to edit existing document instead of creating new one
     */
    private fun editAppointment(id: String) {
        val editSampleFragment = EditSampleFragment.newInstance(id)
        editSampleFragment.setTargetFragment(this@ListFragment, 1)
        editSampleFragment.show(fragmentManager!!, "EditSampleFragment")
    }

    override fun onStart() {
        super.onStart()
        appointmentAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        appointmentAdapter.stopListening()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnArchiveFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     *
     */
    private fun setUpRecyclerView(){
        val query = notebookRef
            .orderBy("timestamp", Query.Direction.ASCENDING)

        val options = FirestoreRecyclerOptions.Builder<Appointment>()
            .setQuery(query, Appointment::class.java)
            .build()

        appointmentAdapter = AppointmentAdapter(options)
        recycler_view.setHasFixedSize(true)
        recycler_view.adapter = appointmentAdapter

        val linearLayoutManager = LinearLayoutManager(this.context)

        val dividerItemDecoration = DividerItemDecoration(
            recycler_view.context,
            linearLayoutManager.orientation
        )
        recycler_view.layoutManager = linearLayoutManager
        recycler_view.addItemDecoration(dividerItemDecoration)

        ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
                appointmentAdapter.deleteItem(p0.adapterPosition)
            }
        }).attachToRecyclerView(recycler_view)

        appointmentAdapter.onItemClickListener=(object :
            AppointmentAdapter.OnItemClickListener {
            override fun onItemClick(documentSnapshot: DocumentSnapshot, position: Int) {

                val id = documentSnapshot.id
                editAppointment(id)
            }
        })
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }
}
