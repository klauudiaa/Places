package io.tsh.countries;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.tsh.countries.extras.Constants;
import io.tsh.countries.fragments.FragmentCountriesList;
import io.tsh.countries.fragments.FragmentDetails;
import io.tsh.countries.log.L;
import io.tsh.countries.model.Place;

import static junit.framework.Assert.assertTrue;

public class MainActivity extends AppCompatActivity implements
        FragmentCountriesList.ActivityCallback,
FragmentDetails.OnFragmentInteractionListener{


    @Bind(R.id.toolbar) protected Toolbar toolbar;
    @Bind(R.id.progressBar) protected ProgressBar progressBar;

    private FragmentCountriesList mCountriesList;
    private FragmentDetails mFragmentDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        mCountriesList = (FragmentCountriesList) getSupportFragmentManager()
                .findFragmentByTag(FragmentCountriesList.TAG);

        if(mCountriesList==null){

            mCountriesList =  new FragmentCountriesList();
            getSupportFragmentManager().beginTransaction().add(R.id.frameLayout,
                    mCountriesList, FragmentCountriesList.TAG
            ).commit();
        }

        mFragmentDetails = (FragmentDetails) getSupportFragmentManager().findFragmentById(R.id.fragmentDetails);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPlaceSelected(int placeId) {
        if(mFragmentDetails == null) {
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra(Constants.PLACE_ID, placeId);
            startActivity(intent);
        }else{
            mFragmentDetails.getDetailsRequest(placeId);
        }
    }

    @Override
    public void displayPlace(Place pace) {
        setTitle(pace.getName());
    }

    @Override
    public void setProgressBarVisiblity(int visible) {
        if(progressBar !=null)
        progressBar.setVisibility(visible);
    }

    @Override
    public void startSettingsActivity() {
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        startActivityForResult(intent, Constants.INTERNET_STATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCountriesList.onActivityResult(requestCode, resultCode, data);
    }
}
