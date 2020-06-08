/*
I copied this from a YouTube video, but there isn't anything to explain here.
 */
package com.example.fridgelog;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentAdapter extends FragmentStatePagerAdapter
{
    private final List<Fragment> fragmentList = new ArrayList<>();

    public FragmentAdapter(FragmentManager fm)
    {
        super(fm);
    }
    public void addFragment(Fragment fragment)
    {
        fragmentList.add(fragment);
    }
    @Override
    public Fragment getItem(int position)
    {
        return fragmentList.get(position);
    }
    @Override
    public int getCount()
    {
        return fragmentList.size();
    }
}