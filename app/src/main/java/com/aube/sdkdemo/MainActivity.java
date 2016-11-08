package com.aube.sdkdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.aube.rvi.AubeAgent;
import com.aube.sdkdemo.model.RichVideoResponse;
import com.aube.sdkdemo.model.VideoItem;
import com.aube.sdkdemo.model.VideoModel;
import com.huyn.baseframework.net.BFJSONRequest;
import com.huyn.baseframework.net.HttpRequestLoader;
import com.huyn.baseframework.net.OpenApi;
import com.huyn.baseframework.utils.Constant;
import com.huyn.baseframework.utils.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    private LinearLayout mVideoLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AubeAgent.init(MainActivity.this, "thephone-android", "f5835541d842a30f3b7a03962c091950");

        OpenApi.setRouter(OpenApi.ROUTER_TEST);

        findViewById(R.id.test_sdk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVideoList();
            }
        });

        mVideoLayout = (LinearLayout) findViewById(R.id.video_container);

        findViewById(R.id.test_report).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AubeAgent.report(MainActivity.this);
            }
        });
    }

    private void getVideoList() {
        HashMap<String, String> params = new HashMap<>();
        params.put(OpenApi.API_METHOD, OpenApi.MODULE_MORE);
        params.put("templateCode", "index");
        params.put("modelCode", "episode");
        params.put("maxnum", "20");
//        params.put("subModelCode", "PreviousEpisodes");
        params.put("subModelCode", "10114");
        BFJSONRequest request = new BFJSONRequest(VideoModel.class, params, new Response.Listener<VideoModel>() {
            @Override
            public void onResponse(VideoModel response) {
                if(response == null)
                    return;
                setupData(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onStart() {
            }
        });
        HttpRequestLoader.getInstance(this).startHttpLoader(request);
    }

    private void setupData(VideoModel model) {
        for(int i=0; i<model.data.size(); i++) {
            if("episode".equalsIgnoreCase(model.data.get(i).modelCode)) {
                final List<VideoItem> videos = model.data.get(i).dataDetail;
                for(int j=0; j<videos.size(); j++) {
                    final VideoItem item = videos.get(j);
                    TextView text = new TextView(this);
                    text.setGravity(Gravity.CENTER_VERTICAL);
                    text.setText(videos.get(j).videoTitle);
                    text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openVideo(item);
                        }
                    });
                    mVideoLayout.addView(text, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200));
                }
            }
        }
    }

    private void openVideo(final VideoItem videoItem) {
        HashMap<String, String> parameterMap = new HashMap<>();
        parameterMap.put("userid", Constant.CC_USERID);
        parameterMap.put("videoid", videoItem.videono);

        StringBuffer sb = new StringBuffer();
        String url = "http://p.bokecc.com/servlet/app/playinfo";
        sb.append(url).append(url.endsWith("?") ? "" : "?");
        Map<String, String> postdata = SaxParamParser.doTHQS(parameterMap);

        for (Map.Entry<String, String> item : postdata.entrySet()) {
            sb.append("").append(item.getKey()).append("=").append(item.getValue()).append("&");
        }

        String requestUrl = sb.toString();
        BFJSONRequest request = new BFJSONRequest(requestUrl, RichVideoResponse.class, new Response.Listener<RichVideoResponse>() {

            @Override
            public void onResponse(RichVideoResponse response) {
                if(response != null) {
                    String url = response.getOneUrl(10);
                    if(StringUtils.isNotBlank(url)) {
                        startPlay(url, videoItem);
                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onStart() {
            }
        });
        HttpRequestLoader.getInstance(this).startHttpLoader(request);
    }

    private void startPlay(String url, VideoItem id) {
        Intent intent = new Intent(this, VideoPlayActivity.class);
        intent.putExtra(VideoPlayActivity.VIDEO_URI, url);
        intent.putExtra(VideoPlayActivity.VIDEO_ID, id);
        startActivity(intent);

//        listConstructors();
    }

    private void listConstructors() {
        try {
//            Class clazz = Class.forName("android.view.LayoutInflater");
            LayoutInflater inflater = LayoutInflater.from(this);
            Class clazz = inflater.getClass();
            String className = clazz.getName();
            System.out.println("-----------" + className);
            while (!className.equalsIgnoreCase("android.view.LayoutInflater")) {
                clazz = clazz.getSuperclass();
                className = clazz.getName();
            }
            Field fields = clazz.getDeclaredField("sConstructorMap");
            fields.setAccessible(true);
            System.out.println("-----------" + fields.getName());
            System.out.println("-----------" + fields.getType());
            HashMap<String, Constructor> values = (HashMap<String, Constructor>) fields.get(inflater);
            System.out.println("---size:" + values.size());

            Iterator<Map.Entry<String, Constructor>> iterator = values.entrySet().iterator();
            List<String> removedKey = new ArrayList<>();
            int count = 0;
            while (iterator.hasNext()) {
                Map.Entry<String, Constructor> item = iterator.next();
                System.out.println(item.getKey() + "----------------" + item.getValue().getName());

                if(item.getKey().contains("com.aube.interact")) {
                    count ++;
                    removedKey.add(item.getKey());
                }
            }
            for(String s : removedKey)
                values.remove(s);
            System.out.println("---size:" + values.size() + "/removed:" + count);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
