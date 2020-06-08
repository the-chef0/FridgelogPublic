/*
This is just a slightly modified version of the ViewPager that prevents navigating by swiping.
I wanted to force the user to navigate by tapping on the BottomNavigationView for simplicity,
so that we don't have to listen for swipe events.
 */
package com.example.fridgelog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class NoSwipePager extends ViewPager
{
    private boolean enabled;

    public NoSwipePager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.enabled = true;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (this.enabled)
        {
            return super.onTouchEvent(event);
        }
        return false;
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        if (this.enabled)
        {
            return super.onInterceptTouchEvent(event);
        }
        return false;
    }
    public void setPagingEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
}