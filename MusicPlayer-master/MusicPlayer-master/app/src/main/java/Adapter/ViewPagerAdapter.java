package Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments;
    private ArrayList<String>titles;

    //(required) The ViewPager Adapter needs to have a parameterized constructor that
    // accepts the FragmentManager instance. Which is responsible for managing the fragments.
    // A FragmentManager manages Fragments in Android, specifically,
    // it handles transactions between fragments. A transaction is a way to add, replace, or remove fragments.
    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
        this.fragments = new ArrayList<>();
        this.titles = new ArrayList<>();
    }


    //This method is responsible for populating the fragments
    // and fragmentTitle lists. which hold the fragments and titles respectively.
    public void addFragments(Fragment fragment, String title)
    {
        fragments.add(fragment);
        titles.add(title);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) { //Returns the fragment at the pos index.  (Required to override)
        return fragments.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) { //(optional) Similar to getItem() this methods retuns the title of the page at index pos.
        return titles.get(position);
    }

    @Override
    public int getCount() {  //This method returns the number of fragments to display.  (Required to Override).
        return fragments.size();
    }
}
