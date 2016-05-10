package com.example.dengue.dengue_android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


class TestFragmentAdapter extends FragmentPagerAdapter {
    protected static final String[] CONTENT = new String[]{"intro1", "intro2", "intro3",};

    private int mCount = CONTENT.length;

    public TestFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return TestFragment.newInstance(CONTENT[position % CONTENT.length]);
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TestFragmentAdapter.CONTENT[position % CONTENT.length];
    }

    public void setCount(int count) {
        if (count > 0 && count <= 10) {
            mCount = count;
            notifyDataSetChanged();
        }
    }

    public static final class TestFragment extends Fragment {
        private static final String KEY_CONTENT = "TestFragment:Content";

        public static TestFragment newInstance(String content) {
            TestFragment fragment = new TestFragment();

            StringBuilder builder = new StringBuilder();
            builder.append(content);
            fragment.mContent = builder.toString();

            return fragment;
        }

        private String mContent = "???";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
                mContent = savedInstanceState.getString(KEY_CONTENT);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView;
            switch (mContent)
            {
                case "intro1":
                    rootView = inflater.inflate(R.layout.intro1, container, false);
                    break;
                case "intro2":
                    rootView = inflater.inflate(R.layout.intro2, container, false);
                    break;
                case "intro3":
                    rootView = inflater.inflate(R.layout.intro3, container, false);
                    break;
                default:
                    rootView = inflater.inflate(R.layout.hot, container, false);
                    break;
            }

            return rootView;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putString(KEY_CONTENT, mContent);
        }
    }
}
