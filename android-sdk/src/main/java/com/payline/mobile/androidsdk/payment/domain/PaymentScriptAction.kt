package com.payline.mobile.androidsdk.payment.domain

import com.payline.mobile.androidsdk.core.data.ContextInfoKey
import com.payline.mobile.androidsdk.core.domain.web.ScriptAction
import org.json.JSONObject

internal sealed class PaymentScriptAction: ScriptAction {

    data class UpdateWebPaymentData(val paymentData: JSONObject): PaymentScriptAction() {

        override val command: String
            get() = ScriptAction.commandWrapper("updateWebPaymentData('$paymentData')")
    }

    object IsSandbox: PaymentScriptAction() {

        override val command: String
            get() = ScriptAction.commandWrapper("isSandbox()")
    }

    data class EndToken(val handledByMerchant: Boolean, val additionalData: String): PaymentScriptAction() {

        override val command: String
            get() {
                val jsCallback = "function() { PaylineSdkAndroid.didEndToken(); }"
                val comm = "endToken('$additionalData', $jsCallback, null, $handledByMerchant)"
                return ScriptAction.commandWrapper(comm)
            }
    }

    object GetLanguageCode: PaymentScriptAction() {

        override val command: String
            get() = ScriptAction.commandWrapper("getLanguageCode()")
    }

    data class GetContextInfo(val key: ContextInfoKey): PaymentScriptAction() {

        override val command: String
            get() = ScriptAction.commandWrapper("getContextInfo('${key.value}')")
    }
}
