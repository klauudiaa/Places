package io.tsh.countries.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.tsh.countries.R;
import io.tsh.countries.extras.Constants;
import io.tsh.countries.log.L;
import io.tsh.countries.model.Place;
import io.tsh.countries.json.PlaceGson;
import io.tsh.countries.network.VolleyErrorListener;
import io.tsh.countries.network.URLs;
import io.tsh.countries.network.VolleySingleton;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentCountriesList extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener{
    public static final String TAG = FragmentCountriesList.class.getName()+"_TAG" ;

    @Bind(R.id.swipeRefreshLayout)protected SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.recyclerView)      protected RecyclerView mRecyclerView;
    @Bind(R.id.emptyDataSet)      protected TextView emptyDataSet;
    private StaggeredGridLayoutManager mLayoutManager;
    private PlaceAdapter mAdapter;
    private RecyclerView.AdapterDataObserver mAdapterObserver;
    private ActivityCallback mCallback;
    private StringRequest request;

    public FragmentCountriesList() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View  view = inflater.inflate(R.layout.fragment_countries_list, container, false);

        ButterKnife.bind(this, view);

        mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if(mAdapter==null) {
            mAdapter = new PlaceAdapter(getActivity(), this);
            mAdapterObserver = new RecyclerView.AdapterDataObserver() {
                private void check(){
                    L.m("onChanged");
                    if( mAdapter.getItemCount() == 0 )
                        emptyDataSet.setVisibility(View.VISIBLE);
                    else
                        emptyDataSet.setVisibility(View.GONE);
                }
                @Override
                public void onChanged() {
                    super.onChanged();
                    check();
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    super.onItemRangeRemoved(positionStart, itemCount);
                    check();
                }

                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    check();
                }
            };
            mAdapter.registerAdapterDataObserver(mAdapterObserver);
        }
        mRecyclerView.setAdapter(mAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        if(savedInstanceState != null)
           mSwipeRefreshLayout.setRefreshing(savedInstanceState.getBoolean(Constants.IS_REFRESHING));

        if(isOnline(getActivity())){
            L.m("Starting download request");
            loadCountriesList();
            mCallback.setProgressBarVisiblity(View.VISIBLE);
        }else{
           mCallback.startSettingsActivity();
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constants.IS_REFRESHING, mSwipeRefreshLayout.isRefreshing());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof ActivityCallback)
            mCallback = (ActivityCallback) context;
        else {
            throw new ClassCastException(context.toString()
                    + " must implemenet " + ActivityCallback.class.getName());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       // L.m("onActivityResult");
        if(isOnline(getActivity())){
            loadCountriesList();
            mCallback.setProgressBarVisiblity(View.VISIBLE);
        }
    }

    private void loadCountriesList() {

        request = new StringRequest(
                Request.Method.GET,
                URLs.URL_PLACES,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        L.m("Result delivered: " + response);
                        if (mAdapter != null) {
                            try {
                                Gson gson = PlaceGson.getInstance();
                                List<Place> places = gson.fromJson(response,
                                        new TypeToken<List<Place>>() {
                                        }.getType());

                                Collections.sort(places, Place.Comparators.DATE_ASC);

                                mAdapter.setData(places);
                            }catch (RuntimeException exc){
                                mAdapter.setData(new ArrayList<Place>());
                            }
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                },
                new VolleyErrorListener(getActivity())
        );
        VolleySingleton.getInstance(getActivity()).getRequestQueue().add(request);

    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        if(isOnline(getActivity())) {
            loadCountriesList();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        request.cancel();
        mAdapter.unregisterAdapterDataObserver(mAdapterObserver);
        mCallback = null;
        VolleySingleton.getInstance(getActivity()).getRequestQueue().getCache().clear();
    }

    @Override
    public void onClick(View v) {
        RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(v);
        int position = holder.getAdapterPosition();
        int placeId = mAdapter.getItem(position).getId();
        mCallback.onPlaceSelected(placeId);
    }

    public interface ActivityCallback {
         void onPlaceSelected(int placeId);
         void setProgressBarVisiblity(int visible);

        void startSettingsActivity();
    }
    static class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {
        private ImageLoader mImageLoader;
        private List<Place> mPlaces = new ArrayList<>();
        private SimpleDateFormat mFormat;
        private Context mContext;
        private View.OnClickListener mCallback;
        private ActivityCallback mActivity;

        public PlaceAdapter(Context context, View.OnClickListener  activityCallback) {
            this.mCallback = activityCallback;
            this.mFormat   = new SimpleDateFormat(Constants.DATE_FORMAT);
            this.mContext  = context;
            this.mActivity = (ActivityCallback) context;
            this.mImageLoader =  VolleySingleton.getInstance(mContext).getImageLoader();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.line_item_country, parent, false);

            v.setOnClickListener(mCallback);
            return new ViewHolder(v);
        }

        public void setData(List<Place> places){
            int size =  mPlaces.size();
            mPlaces.clear();
            notifyItemRangeRemoved(0, size);

            mPlaces.addAll(places);
            mActivity.setProgressBarVisiblity(View.GONE);

            if(places.size() > 0)
                notifyItemRangeInserted(0, places.size());

        }

        public Place getItem(int position){
            return mPlaces.get(position);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Place   p   = mPlaces.get(position);
            String date = mFormat.format(p.getDate().getTime());

            holder.tvCountryName.setText(p.getName());
            holder.tvDate.setText(date);

            String imageUrl = p.getPictureUrl();

            //set empty image
            holder.imageView.setImageResource(android.R.color.transparent);
            holder.imageView.setImageUrl(imageUrl, mImageLoader);
        }


        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);
            mImageLoader = null;
            mContext = null;
            mCallback = null;
            mActivity = null;
        }

        @Override
        public int getItemCount() {
            return mPlaces.size();
        }



        public static class ViewHolder extends RecyclerView.ViewHolder{
            @Bind(R.id.tvCountryName) protected TextView tvCountryName;
            @Bind(R.id.tvDate)        protected TextView tvDate;
            @Bind(R.id.imageView)     protected NetworkImageView imageView;


            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

        }
    }
}
