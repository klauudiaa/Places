package io.tsh.countries.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.tsh.countries.R;
import io.tsh.countries.extras.Constants;
import io.tsh.countries.json.PlaceGson;
import io.tsh.countries.log.L;
import io.tsh.countries.model.Place;
import io.tsh.countries.network.URLs;
import io.tsh.countries.network.VolleyErrorListener;
import io.tsh.countries.network.VolleySingleton;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentDetails.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class FragmentDetails extends Fragment {
    public static final String TAG = FragmentDetails.class.getName()+"_TAG";
    private Place mPlace;
    private RequestQueue  mRequestQueue;
    private ImageLoader   mImageLoader;
    private StringRequest mSinglePlaceRequest;


    @Bind(R.id.imageView)    protected  NetworkImageView imageView;
    @Bind(R.id.tvDescription)protected  TextView  tvDescription;
    @Bind(R.id.tvDate)       protected  TextView  tvDate;
    @Bind(R.id.detailsRoot)  protected  RelativeLayout relativeLayout;


    private OnFragmentInteractionListener mCallback;


    public FragmentDetails() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImageLoader = VolleySingleton.getInstance(getActivity()).getImageLoader();
        mRequestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();

        Bundle arguments = getArguments();
         if(arguments!=null){
            int placeId =  arguments.getInt(Constants.PLACE_ID);
            getDetailsRequest(placeId);
         }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void getDetailsRequest(int placeId){
        L.m("Details for place: "+ placeId);


        mCallback.setProgressBarVisiblity(View.VISIBLE);

        String url = URLs.URL_PLACES+ "/" + placeId ;

        L.m(url);
        mSinglePlaceRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        L.m(response);
                        try {
                            Gson gson = PlaceGson.getInstance();
                            mPlace = gson.fromJson(response, Place.class);
                            getPlacePhoto();
                            displayPlace();
                        }catch (RuntimeException exc){
                            L.e(exc.getMessage());
                            L.t(getActivity().getApplicationContext(), "Unsupported exception");
                            //getActivity().finish();
                        }
                    }
                },
                new VolleyErrorListener(getActivity())
        );
        mRequestQueue.add(mSinglePlaceRequest);
    }

    private void getPlacePhoto() {

        imageView.setImageUrl(mPlace.getPictureUrl(), mImageLoader);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement " + OnFragmentInteractionListener.class.getName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    private void displayPlace(){
        L.m("Displaying place: " + mPlace);
        // format date
        mCallback.displayPlace(mPlace);


        if(mPlace.getDate()!=null) {
            SimpleDateFormat format = new SimpleDateFormat(Constants.DATE_FORMAT);
            String date = format.format(mPlace.getDate().getTime());
            tvDate.setText(date);
        }else{
            tvDate.setText("");
        }

        String descr = mPlace.getDescription()!=null ?  mPlace.getDescription() : "";
        tvDescription.setText(descr);
        mCallback.setProgressBarVisiblity(View.GONE);

    }

    /**
     * Button See More on Click action that opens browser to display additional info about place.
     */
    @OnClick(R.id.button)
    public void buttonSeeMoreOnClick(){
        try {
            Intent browseMore = new Intent(android.content.Intent.ACTION_VIEW);
            browseMore.setData(Uri.parse(mPlace.getSeeMoreUrl()));
            browseMore.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(browseMore);
        }catch(NullPointerException ext){
            L.t(getActivity(), getActivity().getString(R.string.empty_details_url));
        }
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        void displayPlace(Place pace);
        void setProgressBarVisiblity(int visible);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSinglePlaceRequest.cancel();
        mCallback = null;
    }
}
