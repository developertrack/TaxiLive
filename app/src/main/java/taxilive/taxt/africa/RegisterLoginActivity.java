package taxilive.taxt.africa;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class RegisterLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_login);
        initPager();
        initView();
    }

    private void initView() {
        final RelativeLayout rlLoginHeader = findViewById(R.id.rlLoginHeader);
        final NestedScrollView nsvLoginRoot = findViewById(R.id.nsvLoginRoot);

        nsvLoginRoot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                nsvLoginRoot.getWindowVisibleDisplayFrame(rect);
                int screenHeight = nsvLoginRoot.getRootView().getHeight();
                int mainInvisibleHeight = screenHeight - rect.bottom;
                // 未起作用  不知道为什么
                if (mainInvisibleHeight > screenHeight / 4) {
                    nsvLoginRoot.scrollTo(0, rlLoginHeader.getHeight());
                } else {
                    nsvLoginRoot.scrollTo(0, 0);
                }
            }
        });
    }

    /**
     * 初始化Pager
     */
    private void initPager() {
        TabLayout tlLogin = findViewById(R.id.tlLogin);
        ViewPager vpLogin = findViewById(R.id.vpLogin);
        final List<Fragment> mFragments = new ArrayList<>();
        mFragments.add(LoginFragment.newInstance());
        mFragments.add(RegisterFragment.newInstance());
        final String[] mTitles = new String[]{"Login", "Register"};

        vpLogin.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTitles[position];
            }
        });
        tlLogin.setupWithViewPager(vpLogin);
    }


}
