package com.idotools.browser.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.NativeAd;
import com.idotools.browser.R;
import com.idotools.browser.adapter.viewHolder.DmzjViewHolder;
import com.idotools.browser.adapter.viewHolder.DmzjViewHolderTypeAd;
import com.idotools.browser.adapter.viewHolder.FooterViewHolder;
import com.idotools.browser.adapter.viewHolder.Header2ViewHolder;
import com.idotools.browser.adapter.viewHolder.HeaderViewHolder;
import com.idotools.browser.bean.BannerResp;
import com.idotools.browser.bean.DmzjBeanResp;
import com.idotools.browser.manager.viewpager.FragmentViewPagerManger;
import com.idotools.browser.manager.viewpager.ViewPagerManager;
import com.idotools.browser.minterface.OnItemClickListener;
import com.idotools.browser.utils.Constant;
import com.idotools.browser.utils.DoAnalyticsManager;
import com.idotools.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wuxiaojun on 16-11-9.
 */
public class DmzjRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int LOAD_MORE_NO = 2;//没有更多
    public static final int LOAD_MORE_LOADING = 0;//正在加载
    public static final int LOAD_MORE_COMPILE = 1;//加载完成
    private static final int VIEW_TYPE_HEADER1 = 9998;//表示当前view类型为正常viewType
    private static final int VIEW_TYPE_HEADER2 = 9999;//表示当前view类型为正常viewType
    private static final int VIEW_TYPE_NORMAL = 10000;//表示当前view类型为正常viewType
    private static final int VIEW_TYPE_FOOTER = 10001;//表示当前view类型是footerView
    private static final int VIEW_TYPE_AD = 19999;//当前类型是10005

    public Context mContext;
    private View footerView;//加载更多布局
    private int status_add_more;//加载更多状态
    public List<DmzjBeanResp.DmzjBean> mList;
    private LayoutInflater inflater;
    private String classificationStr;//分类
    private String briefIntroductionStr;//简介
    private OnItemClickListener mOnItemClickListener;

    private View headView1;//head view 1
    private View headView2;//head view 2

    // viewpager banner head1
    private ViewPagerManager mBannerViewPagerManager;
    private List<BannerResp.BannerBean> mBannerBeanList;

    // viewpager fragment head2
    private FragmentManager mFragmentManager;
    private FragmentViewPagerManger mFragmentVPManager;


    private HashMap<String, NativeAd> nativeAdHashMap = new HashMap<>();
    private HashMap<String, List<View>> mNativeClickViewMap = new HashMap<>();
    private HashMap<String, AdChoicesView> mNativeAdChoicesMap = new HashMap<>();


    public DmzjRecyclerAdapter(Context context, List<DmzjBeanResp.DmzjBean> list) {
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.mList = list;
        headView1 = inflater.inflate(R.layout.item_dmzj_header, null);
        headView2 = inflater.inflate(R.layout.item_dmzj_header2, null);
        classificationStr = context.getString(R.string.string_classification) + ":";
        briefIntroductionStr = context.getString(R.string.string_brief_introduction) + ":";
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_FOOTER) {
            return new FooterViewHolder(footerView);
        } else if (viewType == VIEW_TYPE_HEADER1) {
            return new HeaderViewHolder(headView1);
        } else if (viewType == VIEW_TYPE_HEADER2) {
            return new Header2ViewHolder(headView2);
        } else if (viewType == VIEW_TYPE_AD) {
            return new DmzjViewHolderTypeAd(inflater.inflate(R.layout.item_dmzj_native_ad, null));
        }
        return new DmzjViewHolder(inflater.inflate(R.layout.item_dmzj, null));
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DmzjViewHolder) {
            //设置图片
            position = position - 2;
            final DmzjBeanResp.DmzjBean bean = mList.get(position);
            if (bean != null) {
                DmzjViewHolder dmzjViewHolder = (DmzjViewHolder) holder;
                bindDmzjViewHolder(dmzjViewHolder, bean);
            } else {
                final DmzjViewHolderTypeAd adViewHolder = (DmzjViewHolderTypeAd) holder;
                bindNativeAdViewHolder(adViewHolder, position);
            }

        } else if (holder instanceof Header2ViewHolder) {
            //第二个head
            Header2ViewHolder mHeaderViewHolder2 = (Header2ViewHolder) holder;
            bindHeadViewHolder2(mHeaderViewHolder2);

        } else if (holder instanceof HeaderViewHolder) {
            //第一个head
            HeaderViewHolder mHeaderViewHolder1 = (HeaderViewHolder) holder;
            initBannerAdapter(mHeaderViewHolder1);

        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            bindFooterViewHolder(footerViewHolder);
        }
    }


    private void bindHeadViewHolder2(Header2ViewHolder mHeaderViewHolder2) {
        if (mFragmentVPManager == null) {
            mFragmentVPManager = new FragmentViewPagerManger(mHeaderViewHolder2.vp_fragment,
                    mHeaderViewHolder2.iv_fm_first, mHeaderViewHolder2.iv_fm_second,
                    mHeaderViewHolder2.iv_fm_third, mFragmentManager, mContext);
            mFragmentVPManager.initFragment();
            mFragmentVPManager.setTextMoreClickListener(mHeaderViewHolder2.tv_more, mContext);
        }
    }

    /***
     * 初始化banner
     */
    private void initBannerAdapter(HeaderViewHolder mHeaderViewHolder) {
        if (mBannerViewPagerManager == null) {
            mBannerViewPagerManager = new ViewPagerManager(mContext,
                    mHeaderViewHolder.id_viewpager, mHeaderViewHolder.id_ll_dot,
                    mHeaderViewHolder.id_iv_one, mBannerBeanList);
            mBannerViewPagerManager.initViewPager();
        } else {
            mBannerViewPagerManager.refreshAdapter(mBannerBeanList);
        }
    }


    private void bindDmzjViewHolder(DmzjViewHolder dmzjViewHolder, final DmzjBeanResp.DmzjBean bean) {
        //设置图片 android:background="@mipmap/img_default"
        glideLoadImg(bean.cover, dmzjViewHolder.id_iv_img);
        //设置信息
        dmzjViewHolder.id_tv_title.setText(bean.title);
        dmzjViewHolder.id_tv_tag.setText(classificationStr + getTags(bean.tags));
        dmzjViewHolder.id_tv_description.setText(briefIntroductionStr + bean.description);
        dmzjViewHolder.id_ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClickListener(bean.mobileUrl, bean.cover, bean.title);
                }
            }
        });
    }

    /***
     * 绑定广告view
     *
     * @param dmzjViewHolder
     * @param position
     */
    private void bindNativeAdViewHolder(DmzjViewHolderTypeAd dmzjViewHolder, int position) {
        String currentPositionStr = position + "";

        NativeAd mNativeAd = nativeAdHashMap.get(currentPositionStr);
        if (mNativeAd == null) {//实例化广告
            synchronized ("loadAd") {
                mNativeAd = new NativeAd(mContext, Constant.FACEBOOK_PLACEMENT_ID);
                mNativeAd.setAdListener(new NativeAdListener(dmzjViewHolder, mNativeAd, currentPositionStr));
                mNativeAd.loadAd(NativeAd.MediaCacheFlag.ALL);
                nativeAdHashMap.put(currentPositionStr, mNativeAd);
            }
        } else {
            //设置view上的内容
            setNativeAdView(dmzjViewHolder, mNativeAd, currentPositionStr);
        }
    }

    /***
     * 绑定footerViewHolder
     *
     * @param footerViewHolder
     */
    private void bindFooterViewHolder(FooterViewHolder footerViewHolder) {
        switch (status_add_more) {
            case LOAD_MORE_LOADING:
                footerViewHolder.progressBar.showNow();
                footerViewHolder.id_ll_footer.setVisibility(View.VISIBLE);
                break;
            case LOAD_MORE_COMPILE:
                footerViewHolder.progressBar.hideNow();
                footerViewHolder.id_ll_footer.setVisibility(View.GONE);
                break;
            case LOAD_MORE_NO:
                footerViewHolder.progressBar.hideNow();
                footerViewHolder.id_ll_footer.setVisibility(View.GONE);
                break;
        }
    }

    private void setNativeAdView(DmzjViewHolderTypeAd dmzjViewHolder, NativeAd mNativeAd, String currentPosition) {
        try {
            mNativeAd.unregisterView();
            // Set the Text.
            dmzjViewHolder.nativeAdTitle.setText(mNativeAd.getAdTitle());
            dmzjViewHolder.nativeAdBody.setText(mNativeAd.getAdBody());
            dmzjViewHolder.nativeAdCallToAction.setText(mNativeAd.getAdCallToAction());
            dmzjViewHolder.nativeAdMedia.setNativeAd(mNativeAd);
            AdChoicesView adChoicesView = mNativeAdChoicesMap.get(currentPosition);
            if (adChoicesView != null) {
                if (adChoicesView.getParent() != null) {
                    ((ViewGroup) adChoicesView.getParent()).removeView(adChoicesView);
                }
                dmzjViewHolder.adChoicesContainer.removeAllViews();
                dmzjViewHolder.adChoicesContainer.addView(adChoicesView);
            }

            // Register the Title and CTA button to listen for clicks.
            List<View> clickableViews = mNativeClickViewMap.get(currentPosition);
            if (clickableViews != null) {
                clickableViews.add(dmzjViewHolder.nativeAdTitle);
                clickableViews.add(dmzjViewHolder.nativeAdCallToAction);
                mNativeAd.registerViewForInteraction(dmzjViewHolder.native_ad_unit, clickableViews);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 广告回调内部类
     */
    private class NativeAdListener implements AdListener {

        DmzjViewHolderTypeAd dmzjViewHolder;
        NativeAd mNativeAd;
        String currentPosition;

        public NativeAdListener(DmzjViewHolderTypeAd dmzjViewHolder, NativeAd mNativeAd, String currentPosition) {
            this.dmzjViewHolder = dmzjViewHolder;
            this.mNativeAd = mNativeAd;
            this.currentPosition = currentPosition;
        }

        @Override
        public void onError(Ad ad, AdError error) {
            LogUtils.e("加载失败:" + error.getErrorCode() + "=" + error.getErrorMessage());
        }

        @Override
        public void onAdLoaded(Ad ad) {
            // Set the Text.
            dmzjViewHolder.nativeAdTitle.setText(mNativeAd.getAdTitle());
            dmzjViewHolder.nativeAdBody.setText(mNativeAd.getAdBody());
            dmzjViewHolder.nativeAdCallToAction.setText(mNativeAd.getAdCallToAction());

            LogUtils.e("获取广告图片路径" + mNativeAd.getAdCoverImage().getUrl());
            dmzjViewHolder.nativeAdMedia.setNativeAd(mNativeAd);

            AdChoicesView adChoicesView = new AdChoicesView(mContext, mNativeAd, true);
            dmzjViewHolder.adChoicesContainer.addView(adChoicesView);
            mNativeAdChoicesMap.put(currentPosition, adChoicesView);

            // Register the Title and CTA button to listen for clicks.
            List<View> clickableViews = new ArrayList<>();
            clickableViews.add(dmzjViewHolder.nativeAdTitle);
            clickableViews.add(dmzjViewHolder.nativeAdCallToAction);
            mNativeAd.registerViewForInteraction(dmzjViewHolder.native_ad_unit, clickableViews);
            mNativeClickViewMap.put(currentPosition, clickableViews);
        }

        @Override
        public void onAdClicked(Ad ad) {
            // Ad clicked callback
            DoAnalyticsManager.event(mContext, DoAnalyticsManager.DOT_KEY_UPDATE_AD_CLICK);
        }
    }

    private String getTags(String[] tags) {
        String str = null;
        if (tags != null && tags.length > 0) {
            StringBuilder sb = new StringBuilder();
            int length = tags.length;
            for (int i = 0; i < length; i++) {
                sb.append(tags[i] + ",");
            }
            str = sb.substring(0, sb.length() - 1);
        }
        return str;
    }

    /***
     * 给view设置点击事件
     *
     * @param imageView
     * @param url
     */
    private void setOnClickListener(ImageView imageView, final String url, final String imgUrl, final String title) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClickListener(url, imgUrl, title);
                }
            }
        });
    }

    /***
     * 加载图片
     *
     * @param cover
     * @param imageView
     */
    private void glideLoadImg(String cover, ImageView imageView) {
        Glide.with(mContext)
                .load(cover)
                .error(R.mipmap.img_default)
                .placeholder(R.mipmap.img_default)
                .centerCrop()
                .crossFade()
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return mList.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (isFooterPosition(position)) {
            return VIEW_TYPE_FOOTER;
        } else if (position == 0) {
            return VIEW_TYPE_HEADER1;
        } else if (position == 1) {
            return VIEW_TYPE_HEADER2;
        } else {
            return judgmentType(position);
        }
    }

    private int judgmentType(int position) {
        if (mList.get(position - 2) != null) {
            return VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_AD;
        }
    }

    /***
     * 添加更多
     *
     * @param list
     */
    public void addMoreItem(List<DmzjBeanResp.DmzjBean> list, int status) {
        this.mList.addAll(list);
        this.status_add_more = status;
        notifyDataSetChanged();
    }

    /***
     * 重置数据
     *
     * @param list
     */
    public void resetAdapter(List<DmzjBeanResp.DmzjBean> list) {
        if (mList != null) {
            mList.clear();
        }
        mList.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * 修改加载更多状态
     */
    public void changeAddMoreStatus(int status) {
        this.status_add_more = status;
        if (status_add_more == LOAD_MORE_LOADING) {
        }
        notifyDataSetChanged();
    }

    /***
     * 判断是否为加载更多view类型
     *
     * @param position
     * @return
     */
    private boolean isFooterPosition(int position) {
        if (position >= (getItemCount() - 1)) {
            return true;
        }
        return false;
    }

    /***
     * 设置fragment里面的数据
     *
     * @param list
     */
    public void setHeadView2Data(List<DmzjBeanResp.DmzjBean> list) {
        if (mFragmentVPManager != null) {
            mFragmentVPManager.setFragmentDmzjBeanList(list);
            notifyDataSetChanged();
        }
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setBannerBeanList(List<BannerResp.BannerBean> list) {
        this.mBannerBeanList = list;
    }

    public void setFragmentManager(FragmentManager fm) {
        this.mFragmentManager = fm;
    }

    public void setFragmentVPManager(FragmentViewPagerManger fragmentVPManager) {
        this.mFragmentVPManager = fragmentVPManager;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

}
