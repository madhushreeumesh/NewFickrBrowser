package com.example.abc.newfickrbrowser;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by VIVEK on 9/30/2017.
 */

class GetFlickrJsonData extends AsyncTask <String, Void, List<Photo>> implements GetRawData.OnDownloadComplete {
    private static final String TAG = "GetFlickrJsonData";

    private List<Photo> mPhotoList = null;
    private String mBaseURL;
    private String mLanguage;
    private boolean mMatchAll;

    private final OnDataAvailable mCallBack;

    private boolean runningOnSameThread = false;

    interface OnDataAvailable{
        void onDataAvailable(List<Photo> data , DownloadStatus status);

    }

    public GetFlickrJsonData( OnDataAvailable callBack , String baseURL, String language, boolean matchAll ) {
        Log.d(TAG, "GetFlickrJsonData: called");
        mBaseURL = baseURL;
        mLanguage = language;
        mMatchAll = matchAll;
        mCallBack = callBack;
    }

    void executeOnSameThread(String searchCriteria){
        Log.d(TAG, "executeOnSameThread: starts");
        runningOnSameThread = true;
        String desstinationUri = createUri(searchCriteria , mLanguage, mMatchAll);
        GetRawData getRawData = new GetRawData(this);
        getRawData.execute(desstinationUri);
        Log.d(TAG, "executeOnSameThread: end");

    }

    @Override
    protected void onPostExecute(List<Photo> photos) {
        Log.d(TAG, "onPostExecute: starts");
        if (mCallBack != null){
            mCallBack.onDataAvailable(mPhotoList , DownloadStatus.OK);
        }

        Log.d(TAG, "onPostExecute: ends");
    }

    @Override
    protected List<Photo> doInBackground(String... params) {
        Log.d(TAG, "doInBackground: starts");
        String destinationUri = createUri(params[0],mLanguage,mMatchAll);

        GetRawData getRawData = new GetRawData(this);
        getRawData.runInsideThread(destinationUri);
        Log.d(TAG, "doInBackground: ends");
        return mPhotoList ;
    }

    private String createUri(String searchCriteria, String lang, boolean mMatchAll){
        Log.d(TAG, "createUri: status");

        Uri uri = Uri.parse(mBaseURL).buildUpon()
                .appendQueryParameter("tag",searchCriteria)
                .appendQueryParameter("tagmode",mMatchAll? "ALL" :"ANY")
                .appendQueryParameter("lang",lang)
                .appendQueryParameter("format","json")
                .appendQueryParameter("nojsoncallback","1").build();

        return Uri.parse(mBaseURL).buildUpon()
                .appendQueryParameter("tags" ,searchCriteria)
                .appendQueryParameter("tagmode",mMatchAll? "ALL " :"ANY")
                .appendQueryParameter("lang", lang)
                .appendQueryParameter("format","json")
                .appendQueryParameter("nojsoncallback", "1")
                .build().toString();
    }

    @Override
    public void onDownloadComplete(String data, DownloadStatus status) {
        Log.e(TAG, "onDownloadComplete: starts satatus"+ status );

        if (status ==DownloadStatus.OK){
            mPhotoList = new ArrayList<>();

            try {
                JSONObject  jsonData = new JSONObject(data);
                JSONArray itemArray = jsonData.getJSONArray("items");

                for (int i=0 ; i<itemArray.length(); i++){
                    JSONObject jsonPhoto = itemArray.getJSONObject(i);
                    String title = jsonPhoto.getString("title");
                    String author = jsonPhoto.getString("author");
                    String authorId = jsonPhoto.getString("author_id");
                    String tags = jsonPhoto.getString("tags");

                    JSONObject jsonMedia = jsonPhoto.getJSONObject("media");
                    String photoUrl = jsonMedia.getString("m");

                    String link = photoUrl.replaceFirst("_m.","_b.");


                    Photo photoObject = new Photo(title, author, authorId, link, tags, photoUrl);
                    mPhotoList.add(photoObject);

                    Log.d(TAG, "onDownloadComplete: " + photoUrl.toString());




                }
            }catch (JSONException json){
                json.printStackTrace();
                Log.e(TAG, "onDownloadComplete: error processing data "+ json.getMessage() );
                status = DownloadStatus.FAILED_OR_EMPTY;
            }
        }

        if (runningOnSameThread && mCallBack != null){
            mCallBack.onDataAvailable(mPhotoList,status);
        }
        Log.d(TAG, "onDownloadComplete: ends");
    }
}

