package io.github.nicobdroid.lunagarden;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class LegalDocumentActivity extends AppCompatActivity {
    private static final String EXTRA_LEGAL_TITLE = "legal_document_title";
    private static final String EXTRA_LEGAL_URL = "legal_document_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String pageTitle = getIntent().getStringExtra(EXTRA_LEGAL_TITLE);
        if (pageTitle != null && !pageTitle.isEmpty()) {
            setTitle(pageTitle);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        WebView webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient());
        setContentView(webView);
        EdgeToEdgeHelper.applyToContentRoot(this);

        String pageUrl = getIntent().getStringExtra(EXTRA_LEGAL_URL);
        if (pageUrl != null && !pageUrl.isEmpty()) {
            webView.loadUrl(pageUrl);
        } else {
            webView.loadData("<html><body></body></html>", "text/html", "utf-8");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

