package com.example.abc.newfickrbrowser;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by VIVEK on 11/3/2017.
 */

class RecyclerItemClickListner extends RecyclerView.SimpleOnItemTouchListener {

    private final static String TAG = "RecyclerItemClickListner";

    interface OnRecyclerClickListner{
        void OnItemClick(View view , int position);
        void OnItemLongClick(View view , int position);
    }

    private final   OnRecyclerClickListner mListner;
    private final   GestureDetectorCompat mGestureDetector;

    public RecyclerItemClickListner(Context context, final RecyclerView recyclerView, final OnRecyclerClickListner mListner) {
        this.mListner = mListner;
        mGestureDetector = new GestureDetectorCompat(context, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d(TAG, "onSingleTapUp: starts");
                View childView = recyclerView.findChildViewUnder(e.getX(),e.getY());
                if(childView != null && mListner != null){
                    Log.d(TAG, "onSingleTapUp: calling listener on item click");
                    mListner.OnItemClick(childView,recyclerView.getChildAdapterPosition(childView));

                }
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.d(TAG, "onLongPress: starts");
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && mListner != null){
                    Log.d(TAG, "onLongPress: calling listner onLongPress");
                    mListner.OnItemLongClick(childView,recyclerView.getChildAdapterPosition(childView));
                }

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        }) ;
    }


    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        Log.d(TAG, "onInterceptTouchEvent: Starts :");
        if(mGestureDetector != null){
            boolean result = mGestureDetector.onTouchEvent(e);
            Log.d(TAG, "onInterceptTouchEvent: returned result");
            return result;
        }else {
            Log.d(TAG, "onInterceptTouchEvent: returned false");
            return false;
        }
    }
}
