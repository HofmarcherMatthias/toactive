package com.gmail.hofmarchermatthias.toactive.edit

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.gmail.hofmarchermatthias.toactive.R
import com.gmail.hofmarchermatthias.toactive.model.Appointment
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_edit_sample.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PATH = "path"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [EditSampleFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [EditSampleFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class EditSampleFragment : DialogFragment() {

    private var path: String? = null
    private var listener: OnFragmentInteractionListener? = null

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param path Parameter 1.
         * @return A new instance of fragment EditSampleFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(path: String) =
            EditSampleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PATH, path)
                }
            }

        const val TAG="EditSampleFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            path = it.getString(ARG_PATH)
        }

        if(path != null){
            FirebaseFirestore.getInstance().document(path!!).get().addOnSuccessListener{
                onHostAppointmentFetched(it.toObject(Appointment::class.java)!!)
            }.addOnFailureListener{ Log.e(TAG, "HostDocument could not be fetched!")}
        }
    }

    private fun onHostAppointmentFetched(hA: Appointment) {
        tv_edit_title.text = hA.title
        tv_edit_description.text = hA.description
        if(hA.location != null){
            tv_edit_coordinates.text = hA.location.toString()
        }else{
            tv_edit_coordinates.text = "No location set"
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_sample, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }


}
