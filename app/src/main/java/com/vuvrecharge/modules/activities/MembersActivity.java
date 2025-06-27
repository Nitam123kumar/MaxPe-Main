package com.vuvrecharge.modules.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vuvrecharge.R;
import com.vuvrecharge.base.BaseActivity;
import com.vuvrecharge.base.BaseMethod;
import com.vuvrecharge.databinding.AddBalanceBinding;
import com.vuvrecharge.modules.adapter.MemberAdapter;
import com.vuvrecharge.modules.model.MemberData;
import com.vuvrecharge.modules.presenter.DefaultPresenter;
import com.vuvrecharge.modules.view.DefaultView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MembersActivity extends BaseActivity implements DefaultView, View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.ref_no)
    EditText ref_no;
    @BindView(R.id.search)
    TextView search;
    @BindView(R.id.select_to_date)
    TextView select_to_date;
    @BindView(R.id.select_from_date_img)
    LinearLayout select_from_date_img;
    @BindView(R.id.select_from_date)
    TextView select_from_date;
    @BindView(R.id.select_to_date_img)
    LinearLayout select_to_date_img;

    MemberAdapter adapter;
    @BindView(R.id.list_item)
    RecyclerView list_item;

    @BindView(R.id.no_internet)
    LinearLayout no_internet;
    @BindView(R.id.retry)
    TextView retry;
    private DefaultPresenter mDefaultPresenter;

    @BindView(R.id.loading)
    LinearLayout loading;
    static Date fromDate;
    static Date toDate;
    static String from_date = "";
    static String to_date = "";
    int page = 1;
    int remaining_pages = 3;
    int pastVisibleItems;
    int visibleItemCount;
    int totalItemCount;
    int previousTotal = 0;

    DefaultView mDefaultView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_members);
        ButterKnife.bind(this);
        setToolbar(mToolbar);
        title.setText("All Members");
        mDefaultView = this;
        search.setOnClickListener(this);
        select_from_date.setOnClickListener(this);
        select_to_date.setOnClickListener(this);
        select_from_date_img.setOnClickListener(this);
        select_to_date_img.setOnClickListener(this);
        mDefaultPresenter = new DefaultPresenter(this);
        Calendar cal = Calendar.getInstance();
        toDate = cal.getTime();
        Date today = new Date();
        Calendar cal1 = new GregorianCalendar();
        cal1.setTime(today);
        cal1.add(Calendar.DAY_OF_MONTH, -30);
        fromDate = cal1.getTime();
        from_date = BaseMethod.format1.format(fromDate);
        to_date = BaseMethod.format1.format(toDate);
        String from_date_local = BaseMethod.dateFormat.format(toDate);
        select_from_date.setText(from_date_local);
        select_to_date.setText(from_date_local);
        initializeEventsList();
        mDefaultPresenter.membersList(device_id + "", page + "",
                from_date + "", to_date + "", "", "Yes");
    }



    boolean isLoading = true;

    private void initializeEventsList() {

        if (mDatabase.getUserData().getUser_type().equals("Distributor") ||
                mDatabase.getUserData().getUser_type().equals("Master_Distributor")) {
        }

        adapter = new MemberAdapter(getLayoutInflater());
        list_item.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        list_item.setLayoutManager(linearLayoutManager);
        list_item.setAdapter(adapter);

        list_item.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = linearLayoutManager.getChildCount();
                totalItemCount = linearLayoutManager.getItemCount();
                pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();

                if (dy > 0) {

                    if (isLoading) {
                        if (totalItemCount > previousTotal) {
                            isLoading = false;
                            previousTotal = totalItemCount;
                        }
                    }

                    if (!isLoading && (totalItemCount - visibleItemCount) <= (pastVisibleItems)) {
                        if (remaining_pages > 0) {
                            page++;
                            mDefaultPresenter.membersList(device_id + "", page + "",
                                    from_date + "", to_date + "", "", "No");
                            isLoading = true;
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setLayout(no_internet, retry, "Member");
    }

    @Override
    public void onClick(@NonNull View v) {
        DialogFragment newFragment;
        switch (v.getId()) {
            case R.id.search:
                hideKeyBoard(ref_no);
                page = 1;
                remaining_pages = 0;
                pastVisibleItems = 0;
                visibleItemCount = 0;
                totalItemCount = 0;
                previousTotal = 0;
                String ref_no_ = ref_no.getText().toString().trim();
                mDefaultPresenter.membersList(device_id + "", page + "",
                        from_date + "", to_date + "", ref_no_ + "", "Yes");
                break;
            case R.id.addMore:
                Intent intent = new Intent(getActivity(), AddMemberActivity.class);
                startActivity(intent);
                break;
            case R.id.select_from_date_img:
                newFragment = new SelectDate(select_from_date, select_to_date);
                newFragment.show(getSupportFragmentManager(), "Select From Date");
                break;
            case R.id.select_to_date_img:
                newFragment = new SelectDate(select_from_date, select_to_date);
                newFragment.show(getSupportFragmentManager(), "Select To Date");
                break;
        }
    }

    @Override
    public void onError(String error) {
        if (bottomSheet != null) {
            showError(bottomSheet, error);
        } else {
            showError(error);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hideKeyBoard(ref_no);
    }

    @Override
    public void onSuccess(String message) {

    }

    @Override
    public void onSuccess(String message, String second_message) {
        try {
            isLoading = false;
            JSONObject jsonObject = new JSONObject(message);
            printLog(jsonObject.toString());
            remaining_pages = Integer.parseInt(jsonObject.getString("remaining_pages"));
            JSONArray jsonArray = jsonObject.getJSONArray("history_data");
            Gson gson = new Gson();
            Type type_ = new TypeToken<List<MemberData>>() {
            }.getType();
            List<MemberData> passbookData = gson.fromJson(jsonArray.toString(), type_);
            if (second_message.equals("Yes")) {
                initializeEventsList();
            }
            adapter.addData(passbookData, second_message, mDatabase, mDefaultView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccessOther(String data) {
        addBalance(data);
    }

    public void addBalance(String data) {
        try {
            AddBalanceBinding binding_ = DataBindingUtil.inflate(LayoutInflater.from(getActivity()),
                    R.layout.add_balance, null, false);
            dialog = new BottomSheetDialog(getActivity(), R.style.AppBottomSheetDialogTheme);
            dialog.setContentView(binding_.getRoot());
            dialog.setCancelable(false);
            changeStatusBarColor(dialog);

            bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setSkipCollapsed(false);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setPeekHeight(600);
            }
            binding_.radioGroup.setVisibility(View.GONE);
            if (mDatabase.getUserData().getUser_type().equals("Admin")) {
                binding_.radioGroup.setVisibility(View.VISIBLE);
            }

            String[] split = data.split("####");

            submit = binding_.submit;
            loading_dialog = binding_.loading;

            binding_.dialogImage.setOnClickListener(v -> {

                if (submit.getVisibility() == View.VISIBLE) {
                    hideKeyBoard(binding_.amount);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            bottomSheet = null;
                            dialog = null;
                        }
                    }, 200);
                }

            });

            binding_.add.setSelected(true);
            binding_.submit.setOnClickListener(v -> {
                String amount = binding_.amount.getText().toString();
                String mpin = binding_.mpinTxt.getText().toString();
                String type = "";
                if (binding_.add.isChecked()) {
                    type = "Add";
                } else {
                    type = "Less";
                }
                if (TextUtils.isEmpty(amount.trim())) {
                    showError(bottomSheet, "Please enter amount");
                    return;
                } if (TextUtils.isEmpty(mpin.trim())) {
                    showError(bottomSheet, "Please enter mpin");
                    return;
                }

                binding_.submit.setVisibility(View.GONE);
                hideKeyBoard(binding_.mpinTxt);
                mDefaultPresenter.transferBalance(device_id + "", amount + "",
                        type + "", split[0] + "", split[1] + "",mpin);
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onSuccessOther(String data, String data_other) {
        try {
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
            Toast.makeText(getActivity(), data, Toast.LENGTH_LONG).show();
            JSONObject jsonObject = new JSONObject(data_other);
            adapter.refreshItem(jsonObject.getString("balance"),
                    Integer.parseInt(jsonObject.getString("position")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class SelectDate extends DialogFragment {

        TextView from_;
        TextView to_;

        public SelectDate(TextView from_, TextView to_) {
            this.from_ = from_;
            this.to_ = to_;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            String tag = getTag();
            if (tag != null) {
                switch (tag) {
                    case "Select From Date":
                        calendar.setTime(fromDate);
                        break;
                    case "Select To Date":
                        calendar.setTime(toDate);
                        break;
                }
            }

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dpd = new DatePickerDialog(getActivity(), R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(0);
                    cal.set(year, month, day, 0, 0, 0);
                    String tag = getTag();
                    if (tag != null) {
                        switch (tag) {
                            case "Select From Date":
                                fromDate = cal.getTime();
                                from_date = BaseMethod.format1.format(fromDate);
                                String from_date_local = BaseMethod.dateFormat.format(fromDate);
                                if (toDate.before(fromDate)) {
                                    toDate = cal.getTime();
                                    to_date = BaseMethod.format1.format(toDate);
                                    to_.setText(from_date_local);
                                }
                                from_.setText(from_date_local);
                                break;
                            case "Select To Date":
                                toDate = cal.getTime();
                                to_date = BaseMethod.format1.format(toDate);
                                String to_date_local = BaseMethod.dateFormat.format(toDate);
                                to_.setText(to_date_local);
                                break;
                        }
                    }
                }
            }, year, month, day);
            if (tag != null) {
                if ("Select To Date".equals(tag)) {
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.setTime(fromDate);
                    dpd.getDatePicker().setMinDate(calendar1.getTimeInMillis());
                }
            }
            dpd.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
            return dpd;
        }
    }

    @Override
    public void onShowDialog(String message) {
        if (bottomSheet != null) {
            showLoading(loading_dialog);
            submit.setVisibility(View.GONE);
        } else {
            showLoading(loading);
        }
    }

    @Override
    public void onHideDialog() {
        if (bottomSheet != null) {
            hideLoading(loading_dialog);
            submit.setVisibility(View.VISIBLE);
        } else {
            hideLoading(loading);
        }
    }

    @Override
    public void onShowToast(String message) {
        if (bottomSheet != null) {
            showToast(bottomSheet, message);
        } else {
            showToast(message);
        }
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
