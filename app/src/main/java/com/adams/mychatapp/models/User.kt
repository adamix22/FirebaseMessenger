package com.adams.mychatapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val username: String,val profileimageurl: String, val uid: String ) : Parcelable {
    constructor() :this("","","")



}