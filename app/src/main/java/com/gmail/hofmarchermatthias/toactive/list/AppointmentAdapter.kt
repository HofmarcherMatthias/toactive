package com.gmail.hofmarchermatthias.toactive.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.gmail.hofmarchermatthias.toactive.R
import com.gmail.hofmarchermatthias.toactive.model.Appointment
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.synthetic.main.item_appointment.view.*

class AppointmentAdapter(firestoreRecyclerOptions: FirestoreRecyclerOptions<Appointment>)
    : FirestoreRecyclerAdapter<Appointment,
        AppointmentAdapter.AppointmentHolder>(firestoreRecyclerOptions)
{
    lateinit var onItemClickListener: OnItemClickListener get set


    override fun onBindViewHolder(holder: AppointmentHolder, position: Int, model: Appointment) {
        holder.itemView.tv_title.text = model.title
        holder.itemView.tv_description.text = model.description
        holder.itemView.tv_timestamp.text = model.timestamp.seconds.toString()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): AppointmentHolder {
        val v =LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_appointment, viewGroup, false)

        return AppointmentHolder(v)
    }

    fun deleteItem(position: Int){
        snapshots.getSnapshot(position).reference.delete()
    }

    inner class AppointmentHolder(v:View): RecyclerView.ViewHolder(v) {
        init {
            v.setOnClickListener{
                val currentPos = adapterPosition
                if(currentPos != RecyclerView.NO_POSITION && ::onItemClickListener.isInitialized){
                    onItemClickListener.onItemClick(snapshots.getSnapshot(currentPos), currentPos)
                }
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(documentSnapshot: DocumentSnapshot, position: Int)
    }

}