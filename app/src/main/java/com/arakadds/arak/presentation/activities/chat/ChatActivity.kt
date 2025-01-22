package com.arakadds.arak.presentation.activities.chat

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.RecyclerView
import com.arakadds.arak.common.fcm.FirebaseManage
import com.arakadds.arak.common.helper.ActivityHelper
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.TimeShow
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.model.message.Message
import com.arakadds.arak.model.message.User
import com.arakadds.arak.presentation.activities.authentication.LoginActivity
import com.arakadds.arak.presentation.adapters.MessageAdapter

import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import io.paperdb.Paper
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class ChatActivity : AppCompatActivity(), UserService.UserServiceCallBack {


    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }

    var currentUser: User? = null
    var messageList: MutableList<Message>? = ArrayList()
    var messageAdapter: MessageAdapter? = null
    var chatRecyclerView: RecyclerView? = null
    var messageEditText: EditText? = null
    var send: TextView? = null
    var attachment: ImageView? = null
    var backImageView: ImageView? = null
    var date: TextView? = null
    var userTextView: TextView? = null
    var userService: UserService? = null
    var email: String? = null
    var id: Int? = null
    var fullname: String = ""
    var imageAvatar: String? = null

    var destinationEmail: String = ""
    var destinationId: Int? = null
    var destinationFullname: String = ""
    var destinationImageAvatar: String = ""
    var destinationProductId: String = ""
    var isExistMessage: Boolean = false
    var chatKey: String = ""
    var context: Context? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // set Windows Flags to Full Screen
        // using setFlags function
        /*getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        setContentView(com.arakadds.arak.R.layout.activity_chat);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR //Prevent screen rotation
        //window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        val  language = preferenceHelper.getLanguage()

        updateChatView(language)
        initUi()

        FirebaseManage.shared().updateStatus(preferenceHelper.getKeyUserId(), "1")
    }

    fun updateChatView(lang: String) {
        context = LocaleHelper.setLocale(applicationContext, lang)
    }

    private fun initUi() {
        this.supportActionBar?.hide()
        chatRecyclerView = findViewById(com.arakadds.arak.R.id.recyclerView)
        messageEditText = findViewById(com.arakadds.arak.R.id.et_message)
        send = findViewById(com.arakadds.arak.R.id.bt_send)
        attachment = findViewById(com.arakadds.arak.R.id.attach)
        userTextView = findViewById(com.arakadds.arak.R.id.chat_top_bar_title_TextView_id)
        backImageView = findViewById(com.arakadds.arak.R.id.chat_top_bar_back_ImageView_id)
        userService = UserService()

        backImageView?.setOnClickListener {
            finish()
        }
        attachment?.setOnClickListener(View.OnClickListener { fromGallery })

        send?.setOnClickListener(View.OnClickListener {
            val data: MutableMap<String, Any?> = HashMap()
            if (messageEditText?.text == null || messageEditText?.text.toString().trim { it <= ' ' }
                    .isEmpty()) {
                //Toaster.show(context?.resources?.getString(R.string.MyDetails_activity_Save))
                //Toaster.show("Please Enter message")
                return@OnClickListener
            }
            data["text"] = messageEditText?.text.toString()
            sendNewMessage(data)
        })

        messageEditText?.setOnClickListener(View.OnClickListener {
            chatRecyclerView?.postDelayed(
                Runnable {
                    if (chatRecyclerView?.adapter != null) if (chatRecyclerView?.adapter?.itemCount!! > 0) chatRecyclerView?.smoothScrollToPosition(
                        chatRecyclerView?.adapter!!.itemCount - 1
                    )
                },
                500
            )
        })

        id = preferenceHelper.getKeyUserId()
        email = preferenceHelper.getUserEmail()
        fullname = preferenceHelper.getUserFullName().toString()
        imageAvatar = preferenceHelper.getUserImage()
        destinationId = preferenceHelper.getStoreId()
        destinationEmail = preferenceHelper.getStoreEmail()
        destinationFullname = preferenceHelper.getStoreFullName()
        destinationImageAvatar = preferenceHelper.getStoreImageAvatar()
        destinationProductId = preferenceHelper.getProductId().toString()

        userTextView?.text = destinationFullname

        if (id == null || destinationId == null) {
            ActivityHelper.goToActivity(applicationContext, LoginActivity::class.java, false)
            return
        }

        if (id == -1 || destinationId == -1) {
            ActivityHelper.goToActivity(applicationContext, LoginActivity::class.java, false)
            return
        }

        chatKey =
            if (id!! > destinationId!!) id.toString() + "-" + destinationId.toString() else destinationId.toString() + "-" + id.toString()
        checkUser()
    }

    private fun checkUser() {
        // check user if not register it
        userService?.getUser(id.toString(), this)
        checkDestination(destinationProductId)
    }

    private fun updateUserProfile() {
        val data = HashMap<String, Any?>()
        data["Id"] = id.toString()
        data["email"] = email
        data["img_avatar"] = imageAvatar
        data["fullname"] = fullname
        userService?.UpdateUser(id.toString(), data)
    }

    private fun registerAuthUser() {
        val data = HashMap<String, Any?>()
        data["Id"] = id.toString()
        data["email"] = email
        data["img_avatar"] = imageAvatar
        data["fullname"] = fullname
        userService?.CreateNewUser(id.toString(), data)
        currentUser = User(email, fullname, id.toString(), imageAvatar)
    }


    private fun updateDestinationInfo() {
        if (destinationId == null || destinationId?.toString()?.isEmpty() == true) {
            return
        }
        val data = HashMap<String, Any?>()
        data["Id"] = destinationId.toString()
        data["email"] = destinationEmail
        data["img_avatar"] = destinationImageAvatar
        data["fullname"] = destinationFullname
        userService!!.UpdateUser(destinationId.toString(), data)
    }

    private fun checkDestination(userId: String?) {
        userService!!.CheckExists(userId) { user ->
            if (user == null) {
                registerDestination()
            } else {
                updateDestinationInfo()
            }
            checkMessages()

        }
    }

    private fun checkMessages() {
        FirebaseDatabase.getInstance().reference.child("messages")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        isExistMessage = true;
                        fetchMessages()
                    } else {
                        date?.visibility = GONE
                        messageEditText!!.isFocusable = true
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun registerDestination() {
        val data = HashMap<String, Any?>()
        data["Id"] = destinationId.toString()
        data["email"] = destinationEmail
        data["img_avatar"] = destinationImageAvatar
        data["fullname"] = destinationFullname
        userService!!.CreateNewUser(destinationId.toString(), data)
    }


//
//    private fun takeVideo() {
//        val intent: Intent = if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
//            Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
//        } else {
//            Intent(Intent.ACTION_PICK, MediaStore.Video.Media.INTERNAL_CONTENT_URI)
//        }
//        intent.type = "video/*"
//        intent.action = Intent.ACTION_GET_CONTENT
//        startActivityForResult(intent, PICK_VIDEO)
//    }
//

    private val fromGallery: Unit
        get() {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, REQUEST_IMAGE_CAPTURE)
        }

    /*  private fun takePhoto() {

          val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
          if (takePictureIntent.resolveActivity(packageManager) != null) {
              takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
              startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
          }
      }*/


    private fun sendNewMessage(data: MutableMap<String, Any?>) {
        val messageDatabaseReference = FirebaseDatabase.getInstance().reference.child("messages")
        val childRef = messageDatabaseReference.push().key
        val timeStamp = Date().time
        data["to_Id"] = destinationId.toString()
        data["Id"] = childRef
        data["sender_Id"] = id.toString()
        data["productId"] = destinationProductId.toString()
        data["timestamp"] = timeStamp / 1000
        messageDatabaseReference.child(chatKey).child(childRef!!).setValue(data)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    data["id"] = childRef
                    FirebaseDatabase.getInstance().reference.child("user-messages").child(chatKey)
                        .updateChildren(data)
                    if (!isExistMessage) {
                        fetchMessages()
                    }
                    var message = if (data.containsKey("text")) data["text"] else "Image Sent";
                    //send notification
                    FirebaseManage.shared().messagesStatus(
                        destinationId.toString(),
                        fullname,
                        message.toString(),
                        chatKey
                    )
                    messageEditText!!.setText("")
                } else {
                    //resources.getString(R.string.login_Welcome)
                    // If sign in fails, display a message to the user.
                    //Toaster.show(context?.resources?.getString(R.string.MyDetails_activity_Save))


                }
            }
    }

    private fun fetchMessages() {
        val messageDatabaseReference =
            FirebaseDatabase.getInstance().reference.child("messages").child(chatKey)
        messageDatabaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.value != null) {
                    val message = snapshot.getValue(Message::class.java)
                    if (message != null) {
                        if (messageList?.find { it.id == message.id } == null) {
                            messageList!!.add(message)
                        } else {
                            messageList?.removeAll { it.id == message.id }
                            messageList!!.add(message)
                        }
                    }
                    attemptReload()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        FirebaseManage.shared().updateStatus(preferenceHelper.getKeyUserId(), "0")
    }

    private fun attemptReload() {
        if (messageAdapter == null) {
            messageAdapter = MessageAdapter(this@ChatActivity, messageList, id.toString())
            chatRecyclerView!!.adapter = messageAdapter

            /*LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity);
            linearLayoutManager.setReverseLayout(true);
            chatRecyclerView.setLayoutManager(linearLayoutManager);*/
        } else {
            messageAdapter!!.notifyDataSetChanged()
        }
        if (messageList!!.size > 0) {
            chatRecyclerView!!.postDelayed({
                chatRecyclerView!!.smoothScrollToPosition(
                    chatRecyclerView!!.adapter!!.itemCount - 1
                )
            }, 1000)
            var timeShow = TimeShow()

            date?.text =
                messageList?.get(messageList!!.size - 1)?.timestamp?.let { timeShow.getDate(it) };
            date?.visibility = if (date?.text?.isEmpty() == true) GONE else VISIBLE
            chatRecyclerView?.scrollToPosition(messageList!!.size - 1);

        } else {
            date?.visibility = GONE
        }


    }


    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        resultData: Intent?,
    ) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (resultData != null && requestCode == REQUEST_IMAGE_CAPTURE) {
            try {
                if (resultData.data == null) {
                    val bitmap = resultData.extras!!["data"] as Bitmap?
                    uploadImageToFirebase(bitmap)
                } else {
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, resultData.data)
                    uploadImageToFirebase(bitmap)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    private fun uploadImageToFirebase(bitmapImage: Bitmap?) {

        val id = UUID.randomUUID().toString()
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child("messages_images").child(id)
        val baos = ByteArrayOutputStream()
        bitmapImage!!.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = storageRef.putBytes(data)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {

                throw task.exception!!
            }
            // Continue with the task to get the download URL
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->

            if (task.isSuccessful) {
                val downloadUri = task.result
                val map: MutableMap<String, Any?> = HashMap()
                map["imageUrl"] = downloadUri.toString()
                sendNewMessage(map)
            }
        }
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 12
        private const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 222
        private const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_GALLARY = 333
        private const val LOCATION_REQUEST = 444
        private const val PICK_VIDEO = 555
        private val LOCATION_PERMS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onPause() {
        super.onPause()
        FirebaseManage.shared().updateStatus(preferenceHelper.getKeyUserId(), "0")
    }

    override fun userCallBack(user: User?) {
        if (user == null) {
            registerAuthUser()
        } else {
            updateUserProfile()
        }
    }
}

typealias Callback = (Boolean) -> Unit