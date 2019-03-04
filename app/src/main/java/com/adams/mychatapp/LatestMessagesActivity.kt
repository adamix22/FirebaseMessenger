package com.adams.mychatapp

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.adams.mychatapp.NewMessageActivity.Companion.USER_KEY
import com.adams.mychatapp.models.ChatMessage
import com.adams.mychatapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.latest_messages_view.view.*

class LatestMessagesActivity : AppCompatActivity() {
    companion object {
        var currentUser: User? = null
    }
 val adapter = GroupAdapter<ViewHolder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)
        supportActionBar?.title ="Messenger"
       // supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.CYAN))

        recyclerview_latest_messages.adapter = adapter
        recyclerview_latest_messages.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(this, ChatLogActivity::class.java)
            val row = item as latestMessages
            intent.putExtra(USER_KEY,row.chatPartner)
            startActivity(intent)
        }
        verifyUser()
        currentUser()
        //dummyRows()
        listenForLatestMessages()


    }
    private fun refreshView(){
        adapter.clear()
        latestMessagesHashMap.values.forEach {

            adapter.add(latestMessages(it))

        }
    }
    val latestMessagesHashMap = HashMap<String,ChatMessage>()
    private fun listenForLatestMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object :  ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMessagesHashMap[p0.key!!] = chatMessage
                refreshView()
                //adapter.add(latestMessages(chatMessage))


            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMessagesHashMap[p0.key!!] = chatMessage
                refreshView()

                //adapter.add(latestMessages(chatMessage))
            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })


    }
    class latestMessages(val chatMessage: ChatMessage) : Item<ViewHolder>(){
        var chatPartner : User? = null
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.latest_messages.text = chatMessage.text
            val partnerId : String
            if (chatMessage.fromId== FirebaseAuth.getInstance().uid){
                partnerId = chatMessage.toId
            }
            else{
                partnerId = chatMessage.fromId
            }
            val ref = FirebaseDatabase.getInstance().getReference("/users/$partnerId")
            ref.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                     chatPartner = p0.getValue(User::class.java)
                    viewHolder.itemView.username_latest_messages.text = chatPartner?.username
                    val target = viewHolder.itemView.imageView_latest_messages
                    Picasso.get().load(chatPartner?.profileimageurl).into(target)
                }

                override fun onCancelled(p0: DatabaseError) {

                }

            })

        }
        override fun getLayout(): Int {
          return R.layout.latest_messages_view
        }
    }
    private fun currentUser(){
        val uid =FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                Log.d("message","current user is ${currentUser?.uid}")
            }
        })

    }

    private fun verifyUser() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null){
            val intent = Intent(this,RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_bar,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.new_message ->{
                val intent = Intent(this,NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.sign_out ->{
                FirebaseAuth.getInstance().signOut()
                Toast.makeText(this,"Signed you Out",Toast.LENGTH_SHORT).show()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
