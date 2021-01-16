package com.pinder.app.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.pinder.app.R
import com.pinder.app.adapters.ChatAdapter
import com.pinder.app.models.ChatObject
import com.pinder.app.ui.dialogs.SharedPreferencesHelper
import com.pinder.app.util.SendFirebaseNotification
import com.pinder.app.utils.BuildVariantsHelper
import com.pinder.app.viewmodels.CommunicationViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ChatFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var mDatabaseUserChat: DatabaseReference? = null
    var mDatabaseChat: DatabaseReference? = null
    var mDatabaseUser: DatabaseReference? = null
    var notify = false
    private var myRecyclerView: RecyclerView? = null
    private var mChatAdapter: ChatAdapter? = null
    private var mChatLayoutManager: RecyclerView.LayoutManager? = null
    private var mSendEditText: EditText? = null
    private var mSendButton: Button? = null
    private var profileImage: ImageView? = null
    private var userNameTextView: TextView? = null
    private var currentUserID: String? = null
    private lateinit var matchId: String
    private var chatId: String? = null
    private var profileImageUrl: String? = null
    private var myProfileImageUrl: kotlin.String? = null
    private var myName: String? = null
    private val resultChat = ArrayList<ChatObject>()
    private var backArrowImage: ImageView? = null
    private val TAG = "ChatActivity"
    private var chatRoomExists = true

    @Inject
    lateinit var communicationViewModel: CommunicationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChatFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                ChatFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileImage = view.findViewById(R.id.profileImage)
        userNameTextView = view.findViewById(R.id.userName)
        mSendEditText = view.findViewById(R.id.message)
        mSendButton = view.findViewById(R.id.button_send)
        matchId = communicationViewModel.matchId!!
        currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        mDatabaseUserChat = FirebaseDatabase.getInstance()
                .reference.child("Users")
                .child(currentUserID!!).child("connections")
                .child("matches")
                .child(matchId)
                .child("ChatId")
        mDatabaseChat = FirebaseDatabase.getInstance().reference.child("Chat")
        mDatabaseUser = FirebaseDatabase.getInstance().reference.child("Users")
        fillImagesAndName()

        setRecyclerViewAndAdapter()
        handleBackArrow()
        userNameTextView!!.setOnClickListener { goToUsersProfile() }
        profileImage!!.setOnClickListener { goToUsersProfile() }
        //this functions helps fix recyclerView while opening keyboards
        //this functions helps fix recyclerView while opening keyboards
        mSendButton!!.setOnClickListener {
            sendMessage()
            notify = true
        }
    }

    private fun setRecyclerViewAndAdapter() {
        myRecyclerView = view?.findViewById(R.id.recyclerView)
        mChatLayoutManager = LinearLayoutManager(context)
        myRecyclerView!!.layoutManager = mChatLayoutManager
        mChatAdapter = ChatAdapter(getDataSetChat(), context)
        myRecyclerView!!.adapter = mChatAdapter
        myRecyclerView!!.addOnLayoutChangeListener { view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom) {
                myRecyclerView!!.scrollBy(0, oldBottom - bottom)
            }
        }
        mChatAdapter!!.setOnItemClickListener(object : ChatAdapter.OnItemClickListener {
            override fun onProfileClick(position: Int) {}
            override fun onMessageClick(position: Int) {}
        })
    }

    private fun goToUsersProfile() {
        //TODO
//        val intent = Intent(this@ChatActivity, UsersProfilesActivity::class.java)
        communicationViewModel.fromActivity = "ChatActivity"
    }

    private fun fillImagesAndName() {
        var matchName = ""
        var matchImageUrl = "default"
        val sp: SharedPreferences = requireContext().getSharedPreferences("SP_USER", Context.MODE_PRIVATE)
        myProfileImageUrl = SharedPreferencesHelper.getCurrentProfilePicture(context)
            communicationViewModel.matchName?.let {
                matchName = communicationViewModel.matchName!!
            }
        if (communicationViewModel.matchProfileUrl!=null){
            matchImageUrl = communicationViewModel.matchProfileUrl!!
        } else myProfileImageUrl = "default"
        profileImageUrl = matchImageUrl
        userNameTextView!!.text = matchName
        when (matchImageUrl) {
            "default" -> profileImage?.let { Glide.with(requireContext()).load(R.drawable.ic_profile_hq).into(it) }
            else -> {
                profileImage?.let { Glide.with(it).clear(profileImage!!) }
                profileImage?.let { Glide.with(requireContext()).load(matchImageUrl).into(it) }
            }
        }
        getChatId()
    }

    private fun sendMessage() {
        val sendMessageText = mSendEditText!!.text.toString().trim()
        if (!sendMessageText.isEmpty()) {
            if (chatRoomExists) {
                val newMessageDb = mDatabaseChat!!.push()
                val newMessage: MutableMap<String, String?> = HashMap()
                newMessage["createdByUser"] = currentUserID
                newMessage["text"] = sendMessageText
                newMessageDb.setValue(newMessage)
                val msg = sendMessageText
                val database = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserID!!)
                database.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        myName = dataSnapshot.child("name").value.toString()
                        if (notify) {
                            SendFirebaseNotification.sendNotification(matchId, currentUserID, myProfileImageUrl, myName, context?.getString(R.string.notification_body_message))
                        }
                        notify = false
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            } else {
                Toast.makeText(context, "Looks like you are not a match anymore :(", Toast.LENGTH_SHORT).show()
            }
        }
        mSendEditText!!.setText(null)
    }

    private fun getChatId() {
        mDatabaseUserChat!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    chatId = dataSnapshot.value.toString()
                    mDatabaseChat = mDatabaseChat!!.child(chatId!!)
                    getChatMessages()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun getChatMessages() {
        mDatabaseChat!!.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                Log.d(TAG, "onChildAdded: $dataSnapshot    s: $s")
                if (dataSnapshot.exists()) {
                    var message: String? = null
                    var createdByUser: String? = null
                    if (dataSnapshot.child("text").value != null) {
                        message = dataSnapshot.child("text").value.toString()
                    }
                    if (dataSnapshot.child("createdByUser").value != null) {
                        createdByUser = dataSnapshot.child("createdByUser").value.toString()
                    }
                    if (message != null && createdByUser != null) {
                        var currentUserBoolean = false
                        var imageUrl = profileImageUrl
                        if (createdByUser == currentUserID) {
                            currentUserBoolean = true
                            imageUrl = myProfileImageUrl
                        }
                        val newMessage = ChatObject(message, currentUserBoolean, imageUrl)
                        resultChat.add(newMessage)
                        mChatAdapter!!.notifyDataSetChanged()
                    }
                }
                myRecyclerView!!.scrollToPosition(mChatAdapter!!.itemCount - 1)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                Log.d(TAG, "onChildChanged: $dataSnapshot    s: $s")
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                mDatabaseChat!!.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.d(TAG, "Checker :")
                        if (snapshot.exists()) {
                            Log.d(TAG, "Checker onDataChange: $dataSnapshot")
                        } else {
                            chatRoomExists = false
                            Log.d(TAG, "Checker onDataChange: data not exists")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
                Log.d(TAG, "onChildRemoved: $dataSnapshot")
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
                Log.d(TAG, "onChildMoved: $dataSnapshot    s: $s")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, "onCancelled: $databaseError")
            }
        })
    }

    private fun getDataSetChat(): List<ChatObject?>? {
        return resultChat
    }

    fun onBackPressed() {
        //TODO zdefiniowac powrót do main view
//        if (fromActivity.equals("notification")) {
//            val intent = Intent(this, MainActivity::class.java)
//            intent.putExtra("fromActivity", "chatActivity")
//            startActivity(intent)
//            onDestroy()
//        } else {
//            super.onBackPressed()
//        }
    }

    fun handleBackArrow() {
        backArrowImage = view?.findViewById(R.id.back_arrow)
        BuildVariantsHelper.disableButton(backArrowImage)
        backArrowImage!!.setOnClickListener { v -> onBackPressed() }
    }
}