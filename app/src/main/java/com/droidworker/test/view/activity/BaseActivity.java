package com.droidworker.test.view.activity;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.droidworker.test.R;
import com.google.common.base.CaseFormat;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author DroidWorkerLYF
 */
public class BaseActivity extends AppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_toolbar_title)
    TextView mTitleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView();
        ButterKnife.bind(this);
        setToolbar();
    }

    /**
     * 更具类名来绑定布局
     */
    protected void setContentView() {
        Class<?> clazz = this.getClass();

        while (true) {
            String clazzName = clazz.getSimpleName();
            String layoutName = "activity_" + CaseFormat.UPPER_CAMEL.to(CaseFormat
                    .LOWER_UNDERSCORE, clazzName.substring(0, clazzName.indexOf("Activity")));
            int layoutId = getResources().getIdentifier(layoutName, "layout", getPackageName());

            if (layoutId != 0) {
                setContentView(layoutId);
                break;
            } else {
                clazz = clazz.getSuperclass();
                if (clazz.getClass().getSimpleName().equals(BaseActivity.class.getSimpleName())) {
                    throw new IllegalStateException(
                            "there is no layout for this activity");
                }
            }
        }
    }

    /**
     * 设置toolbar
     */
    protected void setToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        PackageManager packageManager = getPackageManager();
        try {
            ActivityInfo activityInfo = packageManager.getActivityInfo(getComponentName(), 0);
            mTitleView.setText(activityInfo.loadLabel(packageManager).toString());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();

        ButterKnife.unbind(this);
    }
}
