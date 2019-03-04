package com.myluckyday.test.paylinesdk.wallet.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.myluckyday.test.paylinesdk.R
import com.myluckyday.test.paylinesdk.core.presentation.WebFragment
import com.myluckyday.test.paylinesdk.core.presentation.WebViewModel
import com.myluckyday.test.paylinesdk.core.util.IntentExtraDelegate
import kotlinx.android.synthetic.main.activity_wallet.*

internal class WalletActivity: AppCompatActivity() {

    companion object {

        private var Intent.uri by IntentExtraDelegate.Uri("EXTRA_URI")

        fun buildIntent(context: Context, uri: Uri): Intent {
            return Intent(context, WalletActivity::class.java).apply {
                this.uri = uri
            }
        }
    }

    private val viewModel: WebViewModel by lazy {
        ViewModelProviders.of(this).get(WebViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)

        showWebFragment()
    }

    private fun showWebFragment() {

        val fragmentManager = supportFragmentManager
        fragmentManager.executePendingTransactions()

        val uri = intent.uri ?: return
        val webFragment = WebFragment.createInstance(uri)
        fragmentManager
            .beginTransaction()
            .replace(R.id.frameLayout_fragmentContainer_wallet, webFragment, WebFragment::class.java.name)
            .commit()

        b_cancel_wallet_activity.setOnClickListener {

        }
    }

}
