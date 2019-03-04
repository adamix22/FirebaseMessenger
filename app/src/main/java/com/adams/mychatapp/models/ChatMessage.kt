package com.adams.mychatapp.models

class ChatMessage(val Id:String,val text:String, val fromId:String,val toId:String,val timeStamp:Long ){
    constructor() : this("","","","",-1)
}