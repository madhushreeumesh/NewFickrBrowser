package com.example.abc.newfickrbrowser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements GetFlickrJsonData.OnDataAvailable ,
        RecyclerItemClickListner.OnRecyclerClickListner {
    private static final String TAG = "MainActivity";
    private FlickrRecycleViewAdaptor mFlickerRecycleViewAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activateToolbar(false);


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListner(this , recyclerView, this));

        mFlickerRecycleViewAdaptor = new FlickrRecycleViewAdaptor(this, new ArrayList<Photo>());
        recyclerView.setAdapter(mFlickerRecycleViewAdaptor);

        // GetRawData getRawData = new GetRawData(this);
        // getRawData.execute("https://api.flickr.com/services/feeds/photos_public.gne?tags=android,naugat,sdk&tagmode=any&format=json&nojsoncallback=1");

        Log.d(TAG, "onCreate: ends");
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: starts");
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String queryResult = sharedPreferences.getString(FLICKER_QUERY,"");
        if (queryResult.length() > 0 ){

            GetFlickrJsonData getFlickrJsonData = new GetFlickrJsonData(this,"https://api.flickr.com/services/feeds/photos_public.gne" ,"en-us",true);
            //getFlickrJsonData.executeOnSameThread("android, naugat");
            getFlickrJsonData.execute( queryResult);

        }
//        GetFlickrJsonData getFlickrJsonData = new GetFlickrJsonData(this,"https://api.flickr.com/services/feeds/photos_public.gne" ,"en-us",true);
//        //getFlickrJsonData.executeOnSameThread("android, naugat");
//        getFlickrJsonData.execute("android ");
        Log.d(TAG, "onResume: ends");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.d(TAG, "onCreateOptionsMenu: returned:"+ true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        if (id== R.id.action_search){
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        }
        Log.d(TAG, "onOptionsItemSelected: returned : returned");

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onDataAvailable(List<Photo> data , DownloadStatus status){
        Log.d(TAG, "onDataAvailable: start");
        if (status == DownloadStatus.OK){
            mFlickerRecycleViewAdaptor.loadNewData(data);
            Log.d(TAG, "onDataAvailable: data is : " + data);
        } else {
            // download or processing gailed
            Log.e(TAG, "onDataAvailable: failed with status " + status );
        }
        Log.d(TAG, "onDataAvailable: ends");
    }


    @Override
    public void OnItemClick(View view, int position) {
        Log.d(TAG, "onItemClick: statrs");
        Toast.makeText(MainActivity.this, "Normal tap at position " + position, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void OnItemLongClick(View view, int position) {
        Log.d(TAG, "onItemLongClick: statrs");
        // Toast.makeText(MainActivity.this, "Normal tap at position " + position, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,PhotoDetailActivity.class);
        intent.putExtra(PHOTO_TRANSFER,mFlickerRecycleViewAdaptor.getPhoto(position));
        startActivity(intent);


    }
}
