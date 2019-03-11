package com.desktop.ultraman.function;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ClearCacheRequest;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class WeatherGet {


    private String city;
    private String text=null;
    private Context context;
    private RequestQueue mRequestQueue;
    private String url;

    private AMapLocationListener mLocationListener;
    private AMapLocationClient mLocationClient;

    public WeatherGet(Context context){
        this.context=context;
        this.city="";
        mRequestQueue=Volley.newRequestQueue(context);
        init();
    }

    private void init(){
        //声明定位回调监听器
        mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                Log.i("debug.city", "code: " + aMapLocation.getErrorCode());
                Log.i("debug.city", "type: " + aMapLocation.getLocationType());
                Log.i("debug.city", "city: " + aMapLocation.getCity());
                Log.i("debug.city","District: "+aMapLocation.getDistrict());
                Log.i("debug.city","address:"+aMapLocation.getAddress());
                city=aMapLocation.getDistrict();
                Getweather();
            }
        };
        //
        //声明AMapLocationClientOption对象初始化
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        mLocationOption.setOnceLocation(true);
        mLocationOption.setOnceLocationLatest(true);
        mLocationOption.setNeedAddress(true);
        mLocationOption.setMockEnable(true);
        mLocationOption.setLocationCacheEnable(false);
        //声明AMapLocationClient类对象初始化
        mLocationClient = new AMapLocationClient(context.getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.setLocationListener(mLocationListener);
        mLocationClient.startLocation();
    }
    private String getCity() {
        Log.i("debug.city",""+city);
        return city;
    }
    public void Getweather(){
        text="无法获取城市信息";
        if(getCity()==null){
            //text="无法获取城市信息";
            return ;
        }
        String theCity=getCity();//.replace("市","");
        File cacheDir =new File(context.getCacheDir(),"volley");
        DiskBasedCache cache=new DiskBasedCache(cacheDir);
        mRequestQueue.start();
        mRequestQueue.add(new ClearCacheRequest(cache,null));
        Log.i("debug.weather","=====getWeather");
        Log.i("debug.weather"," process 2");
        url="http://wthrcdn.etouch.cn/weather_mini?city="+theCity;
        JsonObjectRequest mRequest=new myJsonObjectRequest(url, null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    Log.i("debug.weather","weather get");
                    JSONObject data=new JSONObject(jsonObject.getString("data"));
                    JSONArray forecast=data.getJSONArray("forecast");
                    JSONObject today=forecast.getJSONObject(0);
                    String wendu=data.getString("wendu");
                    String high=today.getString("high");
                    String low=today.getString("low");
                    String date=today.getString("date");
                    String type=today.getString("type");
                    //String city=data.getString("city");
                    Log.i("debug.weather",type);
                    text=date+" "+city+'\n'+"当前气候 "+wendu+"℃ "+type+'\n'+high+" "+low;
                    Log.i("debug.weather"," process 3"+'\n'+text);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i("debug.weather","weather get error");
            }
        }){
            @Override
            public byte[] getBody() {
                try {
                    return url.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        mRequestQueue.add(mRequest);
        Log.i("debug.weather"," process 4: "+text);
        mLocationClient.stopLocation();
        mLocationClient.onDestroy();
       // return text;
    }

    private class myJsonObjectRequest extends JsonObjectRequest {
        myJsonObjectRequest(String url, JSONObject jsonRequest,
                            Response.Listener<JSONObject> listener, Response.ErrorListener errorListener){
            super(url,jsonRequest,listener,errorListener);
        }
        @Override
        protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
            response.headers.put("HTTP.CONTENT_TYPE", "utf-8");
            try {
                String jsonString = new String(response.data, "utf-8");
                return Response.success(new JSONObject(jsonString),
                        HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException | JSONException e) {
                // TODO Auto-generated catch block
                return Response.error(new ParseError(e));
            }
        }
    }

    public String getText(){
        Log.i("debug.weather"," process 1"+text);
        return text;

    }

}
