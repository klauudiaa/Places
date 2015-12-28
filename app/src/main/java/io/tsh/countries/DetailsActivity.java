package io.tsh.countries;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.tsh.countries.extras.Constants;
import io.tsh.countries.fragments.FragmentDetails;
import io.tsh.countries.model.Place;

public class DetailsActivity extends AppCompatActivity implements
        FragmentDetails.OnFragmentInteractionListener{

    private FragmentDetails mFragmentDetails;
    private int   mCountryId;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.progressBar) protected ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCountryId = getIntent().getIntExtra(Constants.PLACE_ID, -1);

        if(mCountryId > 0 ) {

            mFragmentDetails = (FragmentDetails) getSupportFragmentManager().findFragmentByTag(FragmentDetails.TAG);

            if(mFragmentDetails == null) {
                mFragmentDetails = new FragmentDetails();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.PLACE_ID, mCountryId);
                mFragmentDetails.setArguments(bundle);

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.detailsFragmentContent, mFragmentDetails, FragmentDetails.TAG)
                        .commit();
            }

        }
        if(savedInstanceState!=null){
            boolean visible = savedInstanceState.getBoolean(Constants.PROGRESS_BAR_STATE);
            progressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constants.PROGRESS_BAR_STATE, progressBar.getVisibility() == View.VISIBLE);
    }

    @Override
    public void displayPlace(Place place) {
        setTitle(place.getName());
    }

    @Override
    public void setProgressBarVisiblity(int visible) {
        if(progressBar!=null)
            progressBar.setVisibility(visible);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);
         switch(item.getItemId()){
             case android.R.id.home:
                 onBackPressed();
         }
        return true;
    }
}
