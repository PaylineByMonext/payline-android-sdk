package com.payline.mobile.androidsdk.payment.domain

import android.os.Parcel
import android.os.Parcelable
import com.payline.mobile.androidsdk.core.data.ContextInfoResult
import com.payline.mobile.androidsdk.core.data.WidgetState
import com.payline.mobile.androidsdk.core.domain.SdkResult

internal sealed class PaymentSdkResult: SdkResult {

    class DidShowPaymentForm(): PaymentSdkResult() {

        override fun writeToParcel(dest: Parcel, flags: Int) {
            //The function doesn't have parameters
        }

        private constructor(parcel: Parcel): this()

        override fun describeContents(): Int = 0

        companion object {
            @JvmField
            val CREATOR = object: Parcelable.Creator<DidShowPaymentForm> {
                override fun createFromParcel(source: Parcel): DidShowPaymentForm = DidShowPaymentForm(source)
                override fun newArray(size: Int): Array<DidShowPaymentForm?> = arrayOfNulls(size)
            }
        }
    }

    class DidFinishPaymentForm(val state: WidgetState): PaymentSdkResult() {

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeString(state.name)
        }

        private constructor(parcel: Parcel): this(WidgetState.valueOf(parcel.readString()!!))

        override fun describeContents(): Int = 0

        companion object {
            @JvmField
            val CREATOR = object: Parcelable.Creator<DidFinishPaymentForm> {
                override fun createFromParcel(source: Parcel): DidFinishPaymentForm = DidFinishPaymentForm(source)
                override fun newArray(size: Int): Array<DidFinishPaymentForm?> = arrayOfNulls(size)
            }
        }
    }

    data class DidGetIsSandbox(val isSandbox: Boolean): PaymentSdkResult() {

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeInt(if(isSandbox) 1 else 0)
        }

        private constructor(parcel: Parcel): this(parcel.readInt() == 1)

        override fun describeContents(): Int = 0

        companion object {
            @JvmField
            val CREATOR = object: Parcelable.Creator<DidGetIsSandbox> {
                override fun createFromParcel(source: Parcel): DidGetIsSandbox = DidGetIsSandbox(source)
                override fun newArray(size: Int): Array<DidGetIsSandbox?> = arrayOfNulls(size)
            }
        }
    }

    data class DidGetLanguageCode(val language: String): PaymentSdkResult() {

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeString(language)
        }

        private constructor(parcel: Parcel): this(parcel.readString()!!)

        override fun describeContents(): Int = 0

        companion object {
            @JvmField
            val CREATOR = object: Parcelable.Creator<DidGetLanguageCode> {
                override fun createFromParcel(source: Parcel): DidGetLanguageCode = DidGetLanguageCode(source)
                override fun newArray(size: Int): Array<DidGetLanguageCode?> = arrayOfNulls(size)
            }
        }
    }

    data class DidGetContextInfo(val result: ContextInfoResult): PaymentSdkResult() {

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeParcelable(result, 0)
        }

        private constructor(parcel: Parcel): this(parcel.readParcelable<ContextInfoResult>(ContextInfoResult::class.java.classLoader)!!)

        override fun describeContents(): Int = 0

        companion object {
            @JvmField
            val CREATOR = object: Parcelable.Creator<DidGetContextInfo> {
                override fun createFromParcel(source: Parcel): DidGetContextInfo = DidGetContextInfo(source)
                override fun newArray(size: Int): Array<DidGetContextInfo?> = arrayOfNulls(size)
            }
        }
    }
}
