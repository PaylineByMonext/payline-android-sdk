package com.payline.mobile.core.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.payline.mobile.paylinesdk.R
import com.payline.mobile.core.data.ContextInfoKey
import com.payline.mobile.core.data.ContextInfoResult
import com.payline.mobile.core.domain.SdkAction
import com.payline.mobile.core.domain.SdkResult
import com.payline.mobile.core.util.BundleDelegate
import com.payline.mobile.payment.domain.PaymentScriptAction
import com.payline.mobile.payment.domain.PaymentSdkAction
import com.payline.mobile.payment.domain.PaymentSdkResult
import kotlinx.android.synthetic.main.fragment_web.*
import org.json.JSONArray
import android.webkit.WebViewClient
import android.webkit.WebView
import com.payline.mobile.payment.presentation.PaymentInterface
import com.payline.mobile.wallet.presentation.WalletActivity
import com.payline.mobile.wallet.presentation.WalletInterface


internal class WebFragment: Fragment() {

    companion object {

        private var Bundle.uri by BundleDelegate.Uri("EXTRA_URI")

        fun createInstance(uri: Uri): WebFragment {
            return WebFragment().apply {
                arguments = Bundle().apply {
                    this.uri = uri
                }
            }
        }
    }

    private lateinit var viewModel: WebViewModel

    private val actionReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.getParcelableExtra<SdkAction?>(SdkAction.EXTRA_SDK_ACTION) ?: return
            handleSdkAction(action)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_web, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!).get(WebViewModel::class.java)

        setupWebView()
        injectScriptHandler()

        // Listen for SdkAction broadcasts
        val actionFilter = IntentFilter(SdkAction.BROADCAST_SDK_ACTION)
        LocalBroadcastManager.getInstance(activity!!).registerReceiver(actionReceiver, actionFilter)

        arguments?.uri?.let {
            web_view.loadUrl(it.toString())
        }

        web_view.setWebViewClient(object : WebViewClient() {

            override fun onPageFinished(view: WebView, url: String) {
                if(activity is PaymentInterface) (activity as PaymentInterface).stopPaymentLoader()
                else (activity as WalletInterface).stopWalletLoader()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(actionReceiver)
    }

    private fun setupWebView() {
        web_view.settings.javaScriptEnabled = true
    }

    private fun injectScriptHandler() {
        web_view.addJavascriptInterface(viewModel.scriptHandler, viewModel.scriptHandler.toString())
    }

    /**
     * TODO: need to externalize this
     * Need to map SdkAction to ScriptAction and then produce correct SdkResult (based on SdkAction)
     */
    private fun handleSdkAction(action: SdkAction) {
        if (action is PaymentSdkAction) {
            when (action) {
                is PaymentSdkAction.GetLanguage -> getLanguage()
                is PaymentSdkAction.IsSandbox -> isSandbox()
                is PaymentSdkAction.GetContextInfo -> getContextInfo(action)
                is PaymentSdkAction.EndToken -> endToken(action)
            }
        }
    }

    private fun getLanguage() {
        viewModel.scriptHandler.execute(
            PaymentScriptAction.GetLanguage,
            web_view
        ) { language ->
            broadcast(PaymentSdkResult.DidGetLanguage(language))
        }
    }

    private fun isSandbox() {
        viewModel.scriptHandler.execute(
            PaymentScriptAction.IsSandbox,
            web_view
        ) { result ->
            broadcast(PaymentSdkResult.DidGetIsSandbox(result.toBoolean()))
        }
    }

    private fun endToken(action: PaymentSdkAction.EndToken) {
        viewModel.scriptHandler.execute(
            PaymentScriptAction.EndToken(
                action.handledByMerchant,
                action.additionalData
            ), web_view
        ) {}
    }

    private fun getContextInfo(action: PaymentSdkAction.GetContextInfo) {
        viewModel.scriptHandler.execute(
            PaymentScriptAction.GetContextInfo(
                action.key
            ), web_view
        ) { contextInfoData ->

            val parsed = when (action.key) {
                ContextInfoKey.AMOUNT_SMALLEST_UNIT, ContextInfoKey.CURRENCY_DIGITS  -> ContextInfoResult.Int(
                    action.key,
                    contextInfoData.toInt()
                )

                ContextInfoKey.ORDER_DETAILS -> {
                    if (contextInfoData == "null") {
                        ContextInfoResult.ObjectArray(action.key, JSONArray())
                    } else {
                        ContextInfoResult.ObjectArray(action.key, JSONArray(contextInfoData))
                    }
                }
                else -> ContextInfoResult.String(action.key, contextInfoData)
            }

            broadcast(PaymentSdkResult.DidGetContextInfo(parsed))
        }
    }

    private fun broadcast(result: SdkResult) {
        LocalBroadcastManager.getInstance(activity!!).sendBroadcast(
            Intent(SdkResult.BROADCAST_SDK_RESULT).apply {
                putExtra(SdkResult.EXTRA_SDK_RESULT, result)
            }
        )
    }
}
