package com.example.abc.newfickrbrowser;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by VIVEK on 10/2/2017.
 */

class FlickrRecycleViewAdaptor extends RecyclerView.Adapter<FlickrRecycleViewAdaptor.FlickrImageViewHolder> {
    private static final String TAG = "FlickrRecycleViewAdapto";
    private List<Photo> mPhotoList;
    private Context mContext;

    public FlickrRecycleViewAdaptor( Context mContext ,List<Photo> mPhotoList) {
        this.mPhotoList = mPhotoList;
        this.mContext = mContext;
    }

    @Override
    public FlickrImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // called by the layout manager when it need a new view
        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse,parent,false);
        return new FlickrImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FlickrImageViewHolder holder, int position) {
        // called by the layout manager when it wants new data in an existing row

        if (mPhotoList == null || mPhotoList.size() == 0) {
            holder.thumbnail.setImageResource(R.drawable.placeholder);
            holder.title.setText("NO photo match your search");


        } else {

            Photo photoItem = mPhotoList.get(position);
            Log.d(TAG, "onBindViewHolder: " + photoItem.getTags() + " -->" + position);
            Picasso.with(mContext).load(photoItem.getImage())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.thumbnail);

            holder.title.setText(photoItem.getTitle());
        }
    }

    @Override
    public int getItemCount() {
        // Log.d(TAG, "getItemCount: called");
        if ((mPhotoList != null) && (mPhotoList.size() != 0)){
            return mPhotoList.size() ;
        } else {
            return 0;
        }
    }

    void loadNewData (List<Photo> newPhoto){
        mPhotoList = newPhoto;
        notifyDataSetChanged();
    }

    public Photo getPhoto(int position) {
        return ((mPhotoList != null) && (mPhotoList.size()!= 0) ? mPhotoList.get(position):null);
    }

    static class FlickrImageViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "FlickrImageViewHolder";

        ImageView thumbnail = null;
        TextView title = null;

        public FlickrImageViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "FlickrImageViewHolder: starts");
            this.thumbnail = (ImageView)itemView.findViewById(R.id.thumbnail);
            this.title = (TextView) itemView.findViewById(R.id.title);
        }
    }

}
