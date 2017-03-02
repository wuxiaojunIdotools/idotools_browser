package com.browser.dmzj.yinyangshi.manager.popupwindow;

import android.content.Context;

import com.base.browser.manager.popupwindow.MainPopupWindow;
import com.browser.dmzj.yinyangshi.utils.DoAnalyticsManager;
import com.idotools.utils.LogUtils;

/**
 * Created by wuxiaojun on 17-2-28.
 */

public class MyMainPopupWindow extends MainPopupWindow {


    public MyMainPopupWindow(Context context, String className) {
        super(context, className);
    }

    @Override
    protected void eventId(int id) {
        switch (id){
            case com.base.browser.R.id.id_share:
                DoAnalyticsManager.event(mContext, DoAnalyticsManager.DOT_KEY_SHARE_CLICK);

                break;
            case com.base.browser.R.id.id_records:
                DoAnalyticsManager.event(mContext, DoAnalyticsManager.DOT_KEY_RECORDS_CLICK);

                break;
            case com.base.browser.R.id.id_add_shortcut:
                DoAnalyticsManager.event(mContext, DoAnalyticsManager.DOT_KEY_SHORTCUT_CLICK);

                break;
            case com.base.browser.R.id.id_night_mode:
                DoAnalyticsManager.event(mContext, DoAnalyticsManager.DOT_KEY_NIGHT_MODE_CLICK);

                break;
        }
    }


}
