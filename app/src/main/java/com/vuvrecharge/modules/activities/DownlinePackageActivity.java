package com.vuvrecharge.modules.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vuvrecharge.R;
import com.vuvrecharge.base.BaseActivity;
import com.vuvrecharge.modules.adapter.DownlinePackageAdapter;
import com.vuvrecharge.modules.model.DownlinePackageData;
import com.vuvrecharge.modules.presenter.DefaultPresenter;
import com.vuvrecharge.modules.view.DefaultView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DownlinePackageActivity extends BaseActivity implements DefaultView {

    private DownlinePackageAdapter adapter;

    private DefaultPresenter mDefaultPresenter;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.no_data)
    TextView no_data;
    @BindView(R.id.list_item)
    RecyclerView list_item;

    @BindView(R.id.loading)
    LinearLayout loading;

    @BindView(R.id.no_internet)
    LinearLayout no_internet;
    @BindView(R.id.retry)
    TextView retry;
    String data = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycommison_package);
        ButterKnife.bind(this);
        setToolbar(mToolbar);
        try {
            Intent intent = getIntent();
            data = intent.getStringExtra("data");
            if (data.equals("Add Member")) {
                title.setText(data);
            } else {
                title.setText(data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeEventsList();
        mDefaultPresenter = new DefaultPresenter(this);
        mDefaultPresenter.myDownlinePackages(device_id + "");

    }

    @Override
    protected void onResume() {
        super.onResume();
        setLayout(no_internet, retry, "downloadpackage");
    }

    private void initializeEventsList() {
        adapter = new DownlinePackageAdapter(getLayoutInflater());
        list_item.setHasFixedSize(true);
        list_item.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        list_item.setAdapter(adapter);
    }

    @Override
    public void onError(String error) {
        showError(error);
    }

    @Override
    public void onSuccess(String message) {
        printLog(message);
        try {
            JSONArray jsonObject = new JSONArray(message);
            Gson gson = new Gson();
            Type type_ = new TypeToken<List<DownlinePackageData>>() {
            }.getType();
            List<DownlinePackageData> dataList = gson.fromJson(jsonObject.toString(), type_);
            adapter.addEvents(dataList);
            if (dataList.size() == 0) {
                no_data.setVisibility(View.VISIBLE);
            } else {
                no_data.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccess(String data, String data_other) {

    }

    @Override
    public void onSuccessOther(String data) {

    }

    @Override
    public void onSuccessOther(String data, String data_other) {

    }

    @Override
    public void onShowDialog(String message) {
        showLoading(loading);
    }

    @Override
    public void onHideDialog() {
        hideLoading(loading);
    }

    @Override
    public void onShowToast(String message) {
        showToast(message);
    }

    @Override
    public void onPrintLog(String message) {
        printLog(message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideAllDialog();
    }

}