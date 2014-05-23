package com.djs.android.lightstrandcontrol;

import com.djs.anroid.lightstrandcontrol.R;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends FragmentActivity implements OnItemClickListener
{

	private String[] 				mPlanetTitles;
	private ActionBarDrawerToggle 	mDrawerToggle;
    private DrawerLayout 			mDrawerLayout;
    private ListView 				mDrawerList;
    
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mPlanetTitles 	= getResources().getStringArray(R.array.fragment_titles);
        mDrawerLayout 	= (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList		= (ListView) findViewById(R.id.left_drawer);
        
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) 
        {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                //getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                //getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        if (mDrawerLayout != null) //no drawerlayout with larger devices
        {
        	// Set the drawer toggle as the DrawerListener
        	mDrawerLayout.setDrawerListener(mDrawerToggle);
        	
        	getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
             
        	 // Set the adapter for the list view
            mDrawerList.setAdapter(new ArrayAdapter<String>(this,R.layout.drawer_list_item, R.id.title, mPlanetTitles));
            
            // Set the list's click listener
            mDrawerList.setOnItemClickListener(this);
            
            //add the default view 
            SelectDrawerItem(0);
        }                   
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) 
	{
        super.onPostCreate(savedInstanceState);
        
        if (mDrawerLayout != null) //no drawerlayout with larger devices
        {
        	// Sync the toggle state after onRestoreInstanceState has occurred.
        	mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) 
    {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) 
        {
          return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }
	
	/* Called whenever we call invalidateOptionsMenu() */
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) 
//    {
//        // If the nav drawer is open, hide action items related to the content view
//        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//       // menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
//        return super.onPrepareOptionsMenu(menu);
//    }

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu)
//	{
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}
	
	@Override
	public void setTitle(CharSequence title) 
	{
		String tmpTitle = getString(R.string.app_name) + " : " + title;
	    getActionBar().setTitle(tmpTitle);
	}

	/**
	 * The user has selected an item from the drawer layout, maybe time to changes views
	 */
	@Override
	public void onItemClick(AdapterView parent, View view, int position, long id)
	{
		SelectDrawerItem(position);		
	}

	/**
	 * Encapsulates the logic of changing out fragments when the user clicks on a drawer item.
	 * This allows the logic to be re-used in the OnCreate method to select the default fragment.
	 * 
	 * @param position The position in the R.array.fragment_titles array of strings.
	 */
	protected void SelectDrawerItem(int position)
	{
		
	    Fragment fragment = null;
	    switch (position)
		{
		case 0:
			fragment = new ControlFragment();
			break;
		case 1:
			fragment = new LogFragment();
			break;
		default:
			return;			
		}	    

	    // Insert the fragment by replacing any existing fragment
	    FragmentManager fragmentManager = getSupportFragmentManager();
	    fragmentManager.beginTransaction()
	                   .replace(R.id.content_frame, fragment)
	                   .commit();

	    // Highlight the selected item, update the title, and close the drawer
	    mDrawerList.setItemChecked(position, true);
	    setTitle(mPlanetTitles[position]);
	    mDrawerLayout.closeDrawer(mDrawerList);
	}
}
