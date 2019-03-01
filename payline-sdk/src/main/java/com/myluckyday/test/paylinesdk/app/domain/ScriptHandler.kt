package com.myluckyday.test.paylinesdk.app.domain

import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.myluckyday.test.paylinesdk.app.data.WidgetState
import org.json.JSONObject

internal data class ScriptHandler(private val listener: (ScriptEvent)->Unit) {

    @JavascriptInterface
    override fun toString(): String {
        return "PaylineSdkAndroid"
    }

    @JavascriptInterface
    fun didShowState(payload: String?) {
        val json = JSONObject(payload)
        val state = json.optString("state") ?: return
        val wState = WidgetState.valueOf(state)
        listener(ScriptEvent.DidShowState(wState))
    }

    @JavascriptInterface
    fun finalStateHasBeenReached(payload: String?) {
        val json = JSONObject(payload)
        val state = json.optString("state") ?: return
        val wState = WidgetState.valueOf(state)
        listener(ScriptEvent.FinalStateHasBeenReached(wState))
    }

    fun execute(action: ScriptAction, inWebView: WebView, callback: (String)->Unit) {
        inWebView.evaluateJavascript(action.command, callback)
    }
}
