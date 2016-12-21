package com.idotools.browser.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.idotools.browser.R;
import com.idotools.browser.utils.ActivitySlideAnim;
import com.idotools.browser.utils.LanguageUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wuxiaojun on 16-12-20.
 */

public class SplashActivity extends BaseActivity {

    @BindView(R.id.id_iv_start_page)
    ImageView id_iv_start_page;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            startActivity(new Intent(SplashActivity.this, DmzjActivity.class));
            ActivitySlideAnim.slideInAnim(SplashActivity.this);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        //获取推送数据
        getGeTuiMsg();

    }

    public void getGeTuiMsg() {
        String url = getIntent().getStringExtra("url");
        if (!TextUtils.isEmpty(url)) {
            Intent mIntent = new Intent(SplashActivity.this, DmzjActivity.class);
            mIntent.putExtra("url", url);
            startActivity(mIntent);
            ActivitySlideAnim.slideInAnim(SplashActivity.this);
            finish();
        } else {
            startPageAnim();
        }
    }

    private void startPageAnim() {
        mHandler.sendEmptyMessageDelayed(0, 2000);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeMessages(0);
            mHandler = null;
        }
    }

}
