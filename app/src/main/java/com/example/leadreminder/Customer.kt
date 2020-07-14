package com.example.leadreminder

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Customer(
    @SerializedName("APP ID")
    val appId:Int,
    @SerializedName("SHOP NAME")
    val shopName: String?,
    @SerializedName("ADDRESS")
    val address: String?,
    @SerializedName("lastContactDate")
    var lastContactDate:String?,
    @SerializedName("nextContactDate")
    var nextContactDate:String?,
    @SerializedName("Status")
    var status:String?,
    @SerializedName("Reason")
    val reason:String?,
    @SerializedName("MOBILE NO")
    val mobileno: String?
):Parcelable,Serializable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString() ,
        parcel.readString() ,
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(appId)
        parcel.writeString(shopName)
        parcel.writeString(address)
        parcel.writeString(lastContactDate)
        parcel.writeString(nextContactDate)
        parcel.writeString(status)
        parcel.writeString(reason)
        parcel.writeString(mobileno)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "Customer(appId=$appId, shopName=$shopName, address=$address, lastContactDate=$lastContactDate, nextContactDate=$nextContactDate, status=$status, reason=$reason, mobileno=$mobileno)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Customer

        if (appId != other.appId) return false
        if (shopName != other.shopName) return false
        if (address != other.address) return false
        if (lastContactDate != other.lastContactDate) return false
        if (nextContactDate != other.nextContactDate) return false
        if (status != other.status) return false
        if (reason != other.reason) return false
        if (mobileno != other.mobileno) return false

        return true
    }

    override fun hashCode(): Int {
        var result = appId
        result = 31 * result + (shopName?.hashCode() ?: 0)
        result = 31 * result + (address?.hashCode() ?: 0)
        result = 31 * result + (lastContactDate?.hashCode() ?: 0)
        result = 31 * result + (nextContactDate?.hashCode() ?: 0)
        result = 31 * result + (status?.hashCode() ?: 0)
        result = 31 * result + (reason?.hashCode() ?: 0)
        result = 31 * result + (mobileno?.hashCode() ?:0)
        return result
    }

    companion object CREATOR : Parcelable.Creator<Customer> {
        override fun createFromParcel(parcel: Parcel): Customer {
            return Customer(parcel)
        }

        override fun newArray(size: Int): Array<Customer?> {
            return arrayOfNulls(size)
        }
    }
}