package taxilive.taxt.africa;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import util.DotsProgressBar;
import util.UrlConstant;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    Button btn_next;
    EditText mob_no, password;
    TextInputLayout input_layout_mobnumber, input_layout_password;
    String str_mobnumber, str_password;
    String tag_json_obj = "json_obj_req";

    DotsProgressBar progressBar;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {

        Bundle args = new Bundle();

        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_login, container, false);
        btn_next = (Button) view.findViewById(R.id.btn_next);
        mob_no = (EditText) view.findViewById(R.id.mob_no);
        password = (EditText) view.findViewById(R.id.password);
        input_layout_mobnumber = (TextInputLayout) view.findViewById(R.id.input_layout_mob_no);
        input_layout_password = (TextInputLayout) view.findViewById(R.id.input_layout_password);
        progressBar = (DotsProgressBar) view.findViewById(R.id.dotsProgressBar);
        progressBar.setDotsCount(4);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                submitForm();
                closeKeyboard();

            }
        });

        return view;
    }

    private void submitForm() {
        str_mobnumber = mob_no.getText().toString().trim();
        str_password = password.getText().toString().trim();
        if (!isValidMobile()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        userLogin();

    }

    private void closeKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void userLogin() {

        progressBar.setVisibility(View.VISIBLE);
        progressBar.start();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("mobile", str_mobnumber);
            jsonBody.put("password", str_password);
            jsonBody.put("regfrom", "app");


            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    UrlConstant.LOGIN_URL, jsonBody,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(final JSONObject response) {
                            Log.e(tag_json_obj, response.toString());
                            try {
                                String Status = response.getString("result");
                                final String message = response.getString("message").toString();

                                final JSONObject customer_data = response.getJSONObject("customer_profile");

                                if (Status.equals("success")) {

                                    Intent i = new Intent(getActivity(), HomeScreen.class);
                                    startActivity(i);

                                } else {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            android.support.v7.app.AlertDialog.Builder dlgAlert = new android.support.v7.app.AlertDialog.Builder(getActivity());
                                            dlgAlert.setMessage(message);
                                            dlgAlert.setPositiveButton("OK", null);
                                            dlgAlert.setCancelable(true);
                                            dlgAlert.create().show();
                                        }
                                    });

                                }

                            } catch (Exception e) {

                            }

                            progressBar.stop();
                            progressBar.setVisibility(View.GONE);
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.e(tag_json_obj, "Error: " + error.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            android.support.v7.app.AlertDialog.Builder dlgAlert = new android.support.v7.app.AlertDialog.Builder(getActivity());
                            dlgAlert.setMessage("Error while fetching data, please try again");
                            dlgAlert.setPositiveButton("OK", null);
                            dlgAlert.setCancelable(true);
                            dlgAlert.create().show();
                        }
                    });
                    progressBar.stop();
                    progressBar.setVisibility(View.GONE);
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
            };

            MyAppDefault.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private boolean isValidMobile() {

        str_mobnumber = mob_no.getText().toString().trim();

        if (str_mobnumber.isEmpty() || str_mobnumber.length() < 10) {

            Toast.makeText(getActivity(), "Not Valid Number", Toast.LENGTH_LONG).show();
            input_layout_mobnumber.setError("Please enter valid number");
            requestFocus(input_layout_mobnumber);

            return false;
        } else {
            input_layout_mobnumber.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        str_password = password.getText().toString().trim();
        if (password.getText().toString().trim().isEmpty() && str_password.length() < 6) {
            input_layout_password.setError("Invalid Password");
            requestFocus(password);
            return false;
        } else {
            input_layout_password.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}
