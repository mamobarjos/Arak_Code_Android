package com.arakadds.arak.presentation.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.arakadds.arak.common.helper.ActivityHelper
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.model.message.Message
import com.arakadds.arak.model.message.User
import com.arakadds.arak.presentation.activities.authentication.LoginActivity
import com.arakadds.arak.presentation.activities.chat.UserService
import com.arakadds.arak.presentation.activities.home.HomeActivity
import com.arakadds.arak.presentation.adapters.UserAdapter
import com.google.firebase.database.*
import io.paperdb.Paper

/*
* created by hussam zuriqat at 15/3/21
* */

class ChatActivity : AppCompatActivity(), UserService.UserServiceCallBack, UserAdapter.ItemClickListener {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }

    var currentUser: User? = null
    var userService: UserService? = null
    var messageList: ArrayList<Message> = ArrayList()
    var updatedMessageList: List<Message> = ArrayList()
    var userAdapter: UserAdapter? = null
    var messageUsers: RecyclerView? = null
    var title_: TextView? = null
    var backImage: ImageView? = null
    var refresh: SwipeRefreshLayout? = null
    var emptyView: LinearLayout? = null
    var loaderProgressBar: ProgressBar? = null
    var noDataFoundTextView: TextView? = null
    var noDataFoundImageView: ImageView? = null
    var backImageView: ImageView? = null
    var smallLogoImageView: ImageView? = null
    var email: String? = null
    var token: String = ""
    var isFirstLunch: String = "1"
    var id: Int? = null
    var fullname: String = ""
    var imageAvatar: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.arakadds.arak.R.layout.activity_chat_list)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR //Prevent screen rotation
        //window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        val  language = preferenceHelper.getLanguage()
        messageUsers = findViewById(com.arakadds.arak.R.id.chat_services_recyclerView_id)
        loaderProgressBar = findViewById(com.arakadds.arak.R.id.chat_progressBar_id)
        noDataFoundTextView = findViewById(com.arakadds.arak.R.id.chat_empty_view_textView_id)
        noDataFoundImageView = findViewById(com.arakadds.arak.R.id.chat_empty_view_imageView_id)
        backImageView = findViewById(com.arakadds.arak.R.id.chat_back_icon_imageView_id)
        //smallLogoImageView = findViewById(com.arakadds.arak.R.id.my_details_back_imageView_id)

        backImageView?.setOnClickListener {
            ActivityHelper.goToActivity(this, HomeActivity::class.java, false);
        }

       /* smallLogoImageView?.setOnClickListener {
            ActivityHelper.goToActivity(this, HomeActivity::class.java, false);
        }*/

        userService = UserService()
    }

    override fun onResume() {
        super.onResume()
        userAdapter = null;
        id = preferenceHelper.getKeyUserId()
        if (id == -1) {
            ActivityHelper.goToActivity(this, LoginActivity::class.java, false)
            return
        }
        email =preferenceHelper.getUserEmail()
        fullname = preferenceHelper.getUserFullName().toString()
        imageAvatar =preferenceHelper.getUserImage()
        token =preferenceHelper.getToken()
        showLoader(true)
        checkUser()
        if (isFirstLunch == "1") {
            isFirstLunch = "0"
        } else {
            if (token != "non") {
                //ActivityHelper.goToActivity(getActivity(), ChatActivity.class, false);
                //(HomeActivity)getActivity().hi


                /*if (activity is HomeActivity) {
                    (activity as HomeActivity?)!!.hiddedNavBar(true)
                }
                requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_id, ChatActivity())
                        .addToBackStack(null)
                        .commit()*/
            } else {

                Toast.makeText(this,resources.getString(com.arakadds.arak.R.string.toast_login_with_account), Toast.LENGTH_SHORT).show();
                ActivityHelper.goToActivity(this, LoginActivity::class.java, false)
            }
        }
    }

    private fun checkUser() {
        // check user if not register it
        userService?.getUser(id.toString(), this)
    }

    private fun updateUserProfile() {
        if (id == -1) {
            ActivityHelper.goToActivity(this, LoginActivity::class.java, false)
            return
        }
        val data = HashMap<String, Any?>()
        data["Id"] = id.toString()
        data["email"] = email
        data["img_avatar"] = imageAvatar
        data["fullname"] = fullname
        userService?.UpdateUser(id.toString(), data)
        fetchMessageList()
    }


    private fun registerAuthUser() {
        if (id == -1) {
            ActivityHelper.goToActivity(this, LoginActivity::class.java, false)
            return
        }
        val data = HashMap<String, Any?>()
        data["Id"] = id.toString()
        data["email"] = email
        data["img_avatar"] = imageAvatar
        data["fullname"] = fullname
        userService?.CreateNewUser(id.toString(), data)
        currentUser = User(email, fullname, id.toString(), imageAvatar)
        fetchMessageList()
    }

    private fun showLoader(isVisable: Boolean) {
        loaderProgressBar?.visibility = if (isVisable) View.VISIBLE else View.GONE
        messageUsers?.visibility = if (isVisable) View.GONE else View.VISIBLE
    }

    private fun showEmptyView(hasData: Boolean) {

        noDataFoundImageView?.visibility = if (hasData) View.GONE else View.VISIBLE
        noDataFoundTextView?.visibility = if (hasData) View.GONE else View.VISIBLE
        messageUsers?.visibility = if (hasData) View.VISIBLE else View.GONE
    }

    private fun fetchMessageList() {

        checkMessageExists(object : CheckIsExists {
            override fun isExistsCallBack(isExist: Boolean) {
                if (isExist) {
                    startFetch()
                    showLoader(false)
                    showEmptyView(true)
                    emptyView?.visibility = View.GONE
                    refresh?.visibility = View.VISIBLE
                    messageUsers?.visibility = View.VISIBLE

                } else {
                    showLoader(false)
                    showEmptyView(false)
                    refresh?.isRefreshing = false
                    emptyView?.visibility = View.VISIBLE
                    refresh?.visibility = View.GONE
                    messageUsers?.visibility = View.GONE
                }
            }
        })
    }

    private fun startFetch() {
        messageList = ArrayList()
        updatedMessageList = ArrayList()
        val ref = FirebaseDatabase.getInstance().reference.child("user-messages")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.childrenCount == 0L) {
                    refresh?.isRefreshing = false
                    return
                }
                try {
                    var message: Message? = snapshot.getValue(Message::class.java) ?: return;
                    if (message?.sender_Id.equals(id.toString(), ignoreCase = false) || message?.to_Id.equals(id.toString(), ignoreCase = false)) {
                        var destenation = if (message?.sender_Id.equals(id.toString(), ignoreCase = true)) message?.to_Id else message?.sender_Id
                        Log.d("ssssss", "onChildAdded: " + destenation);
                        messageList.removeAll { it -> destenation.equals(it.chatPartnerId(id.toString()), ignoreCase = false) }
                        message?.let { messageList.add(it) };
                        attemptReload()
                    }

                } catch (e: Exception) {
                    // handler
                    e.printStackTrace()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.childrenCount == 0L) {
                    refresh?.isRefreshing = false
                    return
                }
                try {
                    var message: Message? = snapshot.getValue(Message::class.java) ?: return;
                    if (message?.sender_Id.equals(id.toString(), ignoreCase = false) || message?.to_Id.equals(id.toString(), ignoreCase = false)) {
                        var destenation = if (message?.sender_Id.equals(id.toString(), ignoreCase = true)) message?.to_Id else message?.sender_Id
                        Log.d("ssssss", "onChildAdded: " + destenation);
                        messageList.removeAll { it -> destenation.equals(it.chatPartnerId(id.toString()), ignoreCase = false) }
                        message?.let { messageList.add(it) };
                        attemptReload()
                    }

                } catch (e: Exception) {
                    // handler
                    e.printStackTrace()
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun checkMessageExists(checkIsExists: CheckIsExists) {
        if (userAdapter != null) {
            messageList.clear()
            userAdapter?.updateList(messageList)
        }
        val reference = FirebaseDatabase.getInstance().reference.child("user-messages")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                checkIsExists.isExistsCallBack(dataSnapshot.exists())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                checkIsExists.isExistsCallBack(false)
            }
        })
    }
    private fun attemptReload() {
        handleReloadTable()
    }

    // Function to remove duplicates from an ArrayList
    private fun handleReloadTable() {
        reloadAdapter()
    }
    private fun reloadAdapter() {
        refresh?.isRefreshing = false
        if (userAdapter != null) {
            userAdapter?.updateList(messageList)
            messageUsers?.invalidate()
        } else {
            userAdapter = UserAdapter(this, messageList,preferenceHelper.getKeyUserId().toString() ,this)
            messageUsers?.adapter = userAdapter
        }
    }

    override fun onItemClick(view: View, position: Int, user: User) {
        var message = messageList[position]
        var destenation = if (message.sender_Id.equals(id.toString(), ignoreCase = true)) message.to_Id else message.sender_Id

        val userIdAccount =preferenceHelper.getKeyUserId().toString()
        val chatKey = if (userIdAccount > destenation!!) "$userIdAccount-$destenation" else "$destenation-$userIdAccount"

        if (message.to_Id == userIdAccount) {
            updateMessgeSeen(chatKey);
        }

        preferenceHelper.setStoreEmail(user.email)
        preferenceHelper.setStoreFullName(user.fullname)
        preferenceHelper.setStoreImageAvatar(user.img_avatar)
        preferenceHelper.setProductId(message.productId.toInt())
        preferenceHelper.setStoreId(destenation.toInt())
        startActivity(Intent(this, ChatActivity::class.java))
    }

    private fun updateMessgeSeen(chatKey: String) {
        FirebaseDatabase.getInstance().reference.child("user-messages").child(chatKey).child("seen").setValue(true);
    }

    internal interface CheckIsExists {
        fun isExistsCallBack(isExist: Boolean)
    }

    companion object {
        private const val TAG = "UserMessageListActivity : "
    }

    override fun userCallBack(user: User?) {
        if (user == null) {
            registerAuthUser()
        } else {
            updateUserProfile()
        }
    }

 /*   fun onBackPressed() {
        if (token != "non") {
            //ActivityHelper.goToActivity(getActivity(), ChatActivity.class, false);
            //(HomeActivity)getActivity().hi
            if (activity is HomeActivity) {
                (activity as HomeActivity?)!!.hiddedNavBar(true)
            }
            requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_id, HomeFragment())
                    .addToBackStack(null)
                    .commit()
        } else {
            Toaster.show(resources.getString(R.string.toast_login_with_account))
            ActivityHelper.goToActivity(activity, LoginActivity::class.java, false)
        }
    }*/
}