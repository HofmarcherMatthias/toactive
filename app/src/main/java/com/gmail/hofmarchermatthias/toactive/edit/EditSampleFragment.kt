package com.gmail.hofmarchermatthias.toactive.edit

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.sucho.placepicker.AddressData
import com.sucho.placepicker.Constants
import com.sucho.placepicker.PlacePicker
import kotlinx.android.synthetic.main.fragment_edit_sample.*


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_ID = "id"

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

    private var location: GeoPoint? = null
    private lateinit var collectionReference: CollectionReference
    private var id: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private var hostActivity: Activity? = null

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param id
         * @return A new instance of fragment EditSampleFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(id: String) =
            EditSampleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ID, id)
                }
            }

        const val TAG="EditSampleFragment"
        const val PLACE_PICKER_REQUEST=611
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getString(ARG_ID)
        }

        this.collectionReference = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().uid!!).collection("Data")


        if(id != null){
            collectionReference.document(id!!).get().addOnSuccessListener{
                onHostAppointmentFetched(it.toObject(Appointment::class.java)!!)
            }.addOnFailureListener{ Log.e(TAG, "HostDocument could not be fetched!")}
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_sample, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //On ClickEvents
        btn_location_body_change.setOnClickListener{this.openPlacePicker()}
        btn_save.setOnClickListener{
            this.save()
            dismiss()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context !is Activity){
            throw RuntimeException(context.toString()+ " must be an Activity")
        }

        if (context !is OnFragmentInteractionListener) {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
        listener = context
        hostActivity = context
    }



    private fun onHostAppointmentFetched(hA: Appointment) {
        tv_title.setText(hA.title)
        tv_description.setText(hA.description)
        if(hA.location != null){
            location = hA.location
            tv_location_header_current.text = hA.location.toString()
        }else{
            tv_location_header_current.text = "No location set"
        }
    }

    private fun save(){
        val appointment = makeAppointmentFromInput()

        if(id.isNullOrBlank()){
            collectionReference.add(appointment)
        }else{
            val documentReference = collectionReference.document(id!!)
            documentReference.set(appointment)
        }
    }

    private fun makeAppointmentFromInput(): Appointment {
        val appointment = Appointment()
        appointment.title = tv_title.text.toString()
        appointment.description = tv_description.text.toString()
        appointment.timestamp = Timestamp.now()
        appointment.location = location

        return appointment
    }


    private fun openPlacePicker() {
        val intent = PlacePicker.IntentBuilder()
            .setLatLong(40.748672, -73.985628)  // Initial Latitude and Longitude the Map will load into
            .showLatLong(true)
            .setMapZoom(12.0f)
            .setAddressRequired(true) // Set If return only Coordinates if cannot fetch Address for the coordinates. Default: True
            //.hideMarkerShadow(true)
            //.setMarkerDrawable(R.drawable.marker)
            .setMarkerImageImageColor(R.color.colorPrimary)
            //.setFabColor(R.color.fabColor)
            //.setPrimaryTextColor(R.color.primaryTextColor) // Change text color of Shortened Address
            //.setSecondaryTextColor(R.color.secondaryTextColor) // Change text color of full Address
            .build(hostActivity!!)
        startActivityForResult(intent, PLACE_PICKER_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            PLACE_PICKER_REQUEST->onPlacePickerResult(resultCode, data)
        }
    }

    private fun onPlacePickerResult(resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK){
            val addressData = data?.getParcelableExtra<AddressData>(Constants.ADDRESS_INTENT)

            checkNotNull(addressData)
            this.location = GeoPoint(addressData.latitude, addressData.longitude)
            tv_location_header_current.text = location.toString()

        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
        hostActivity = null
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
