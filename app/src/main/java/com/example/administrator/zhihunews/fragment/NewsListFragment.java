package com.example.administrator.zhihunews.fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.administrator.zhihunews.R;
import com.example.administrator.zhihunews.app.ClintApplication;
import com.example.administrator.zhihunews.db.daoImp.NewsItemDaoImp;
import com.example.administrator.zhihunews.db.model.NewsItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/11/23.
 */

public class NewsListFragment extends BaseFragment {
    private ListView mListView;
    private SimpleAdapter mAdapter;
    private View mView;
    private List<Map<String, Object>> mList;

    // 初始化视图
    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //  应该使用view 去充气
        mView = inflater.inflate(R.layout.tab_layout, container, false);
        // 找到里面的listView
        mListView = (ListView) mView.findViewById(R.id.tab_listview);
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new SimpleAdapter(getActivity(), getData(), R.layout.tab_listview_item,
                new String[]{"img", "title", "body"},
                new int[]{R.id.itemimg, R.id.itemtitle, R.id.itembody});      //配置适配器，并获取对应Item中的ID
        mListView.setAdapter(mAdapter);
    }

    // 获取指定日期的新闻标题等
    private void fetchDaysNewsList(Date date) {
        // 解析时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        // url构造
        String url = "http://news.at.zhihu.com/api/4/news/before/" + sdf.format(date);
        StringRequest mRequest = new StringRequest(Request.Method.GET, url,

                // 响应成功
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // 插入数据库
                        NewsItemDaoImp newsItemDaoImp = new NewsItemDaoImp(mActivity);
                        newsItemDaoImp.addDay(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        // 响应失败
                        System.out.println("error ----");
                    }
                }
        );
       ClintApplication.getmRequestQueue().add(mRequest);
    }

    // 静态工厂方法获取Fragment实例
    public static NewsListFragment newInstance() {
        NewsListFragment newsListFragment = new NewsListFragment();
        return newsListFragment;
    }

    private List<? extends Map<String, ?>> getData() {
        Date date = new Date();

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE, 1);// 将日期向后推一天
        fetchDaysNewsList(calendar.getTime());
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        //将需要的值传入map中

        NewsItemDaoImp newsItemDaoImp = new NewsItemDaoImp(getActivity());
        List<NewsItem> newsList = newsItemDaoImp.findDate(date);
        for (NewsItem item:newsList
             ) {
            System.out.println(item.getTitle());
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("title", item.getDate());
            map.put("body", item.getTitle());
            map.put("img", R.drawable.zzf2);
            list.add(map);
        }


//        map = new HashMap<String, Object>();
//        map.put("title", "校内最新消息通知");
//        map.put("body", "校级活动，化工电影本周放啥？艺设妹子有什么动向？点我查看");
//        map.put("img", R.drawable.zzf3);
//        list.add(map);
//
//        map = new HashMap<String, Object>();
//        map.put("title", "圈内交流园地");
//        map.put("body", "来都来了，何不进来说几句？");
//        map.put("img", R.drawable.zzf4);
//        list.add(map);

        return list;
    }

}
