package taxilive.taxt.africa;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
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
public class RegisterFragment extends Fragment {

    String str_first_name, str_last_name, str_email, str_mobnumber, str_province, str_password;
    Button btn_next;
    String[] province_group = {"Western Cape", "Eastern Cape", "Northern Cape", "North West", "Free State", "Kwazulu Natal", "Gauteng", "Limpopo", "Mpumalanga"};
    ArrayAdapter<String> spinner_province_group;
    String tag_json_obj = "json_obj_req";
    String mobile_no;
    DotsProgressBar progressBar;
    private EditText first_name, last_name, email, mobnumber, province, password, cpassword;
    private TextInputLayout input_layout_province, input_layout_mobnumber, input_layout_email, input_layout_last_name, input_layout_fname, input_layout_password, input_layout_cpassword;

    public RegisterFragment() {
        // Required empty public constructor
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static RegisterFragment newInstance() {

        Bundle args = new Bundle();

        RegisterFragment fragment = new RegisterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.activity_signup, container, false);

        input_layout_fname = (TextInputLayout) view.findViewById(R.id.input_layout_fname);
        input_layout_last_name = (TextInputLayout) view.findViewById(R.id.input_layout_last_name);
        input_layout_email = (TextInputLayout) view.findViewById(R.id.input_layout_email);
        input_layout_mobnumber = (TextInputLayout) view.findViewById(R.id.input_layout_mobnumber);
        input_layout_province = (TextInputLayout) view.findViewById(R.id.input_layout_province);
        input_layout_password = (TextInputLayout) view.findViewById(R.id.input_layout_cpassword);
        input_layout_cpassword = (TextInputLayout) view.findViewById(R.id.input_layout_password);
        btn_next = (Button) view.findViewById(R.id.btn_next);
        first_name = (EditText) view.findViewById(R.id.first_name);
        last_name = (EditText) view.findViewById(R.id.last_name);
        email = (EditText) view.findViewById(R.id.email);
        mobnumber = (EditText) view.findViewById(R.id.mobnumber);
        province = (EditText) view.findViewById(R.id.province);
        password = (EditText) view.findViewById(R.id.password);
        cpassword = (EditText) view.findViewById(R.id.cpassword);
        progressBar = (DotsProgressBar) view.findViewById(R.id.dotsProgressBar);
        progressBar.setDotsCount(4);

        spinner_province_group = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, province_group);

        province.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Select Province")
                        .setAdapter(spinner_province_group, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                province.setText(province_group[which].toString());
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });


        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                str_first_name = first_name.getText().toString();
                str_last_name = last_name.getText().toString();
                str_email = email.getText().toString();
                str_mobnumber = mobnumber.getText().toString();
                str_province = province.getText().toString();
                str_password = password.getText().toString();

                if (!validateFirstname()) {
                    return;
                }
                if (!validateEmail()) {
                    return;
                }
                if (!validatePassword()) {
                    return;
                }
                if (!validateConfirmPassword()) {
                    return;
                }
                if (!validatePasswordMatch()) {
                    return;
                }
                if (!isValidMobile()) {
                    return;
                }
                if (!isValidProvince()) {
                    return;
                }

                closeKeyboard();
                registerUser();


            }
        });

        return view;
    }

    public void registerUser() {

        progressBar.setVisibility(View.VISIBLE);
        progressBar.start();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("first_name", str_first_name);
            jsonBody.put("last_name", str_last_name);
            jsonBody.put("email", str_email);
            jsonBody.put("mobile", str_mobnumber);
            jsonBody.put("password", str_password);
            jsonBody.put("provience", str_province);
            jsonBody.put("country", "South Africa");
            jsonBody.put("regfrom", "app");


            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    UrlConstant.REGISTRATION_URL, jsonBody,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(final JSONObject response) {
                            Log.e(tag_json_obj, response.toString());
                            try {
                                String Status = response.getString("result");
                                final String message = response.getString("message").toString();

                                final JSONObject customer_data = response.getJSONObject("customer_profile");

                                if (Status.equals("success")) {

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                mobile_no = customer_data.getString("mobile");
                                                generateOtp(mobile_no);
                                            } catch (Exception e) {

                                            }

                                            progressBar.stop();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });

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

    public void generateOtp(String mob) {

        progressBar.setVisibility(View.VISIBLE);
        progressBar.start();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("mobile", mob);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    UrlConstant.OTP_GENERATE, jsonBody,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(final JSONObject response) {
                            Log.e(tag_json_obj, response.toString());
                            try {
                                String Status = response.getString("result");
                                final String message = response.getString("message").toString();

                                if (Status.equals("success")) {

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            try {
                                                String otp = response.getString("otp");
                                                Intent sign_up = new Intent(getActivity(), Phoneverification.class);
                                                sign_up.putExtra("otp", otp);
                                                sign_up.putExtra("mobile", mobile_no);
                                                startActivity(sign_up);
                                            } catch (Exception e) {

                                            }

                                            progressBar.stop();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });

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

    private boolean validateEmail() {
        str_email = email.getText().toString().trim();

        if (str_email.isEmpty() || !isValidEmail(str_email)) {
            Toast.makeText(getActivity(), "Enter Valid mail", Toast.LENGTH_LONG).show();
            input_layout_email.setError("Enter Valid mail");
            requestFocus(input_layout_email);
            return false;
        } else {
            input_layout_email.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateFirstname() {
        str_first_name = first_name.getText().toString().trim();

        if (str_first_name.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter  name", Toast.LENGTH_LONG).show();
            input_layout_fname.setError("Please enter name");
            requestFocus(input_layout_fname);
            return false;
        } else {
            input_layout_fname.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean isValidMobile() {

        str_mobnumber = mobnumber.getText().toString().trim();

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


    private boolean isValidProvince() {

        str_province = province.getText().toString().trim();

        if (str_province.isEmpty()) {
            Toast.makeText(getActivity(), "Select Blood Group", Toast.LENGTH_LONG).show();
            input_layout_province.setError("Please select province");
            requestFocus(input_layout_province);

            return false;
        } else {
            input_layout_province.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword() {
        if (password.getText().toString().trim().isEmpty() && password.getText().toString().length() < 6) {

            Toast.makeText(getActivity(), "Password should be length of 6 or more", Toast.LENGTH_LONG).show();
            input_layout_password.setError("Password should be length of 6 or more");
            requestFocus(input_layout_password);

            return false;
        } else {
            input_layout_password.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateConfirmPassword() {
        if (cpassword.getText().toString().trim().isEmpty() && cpassword.getText().toString().length() < 6) {

            Toast.makeText(getActivity(), "Password should be length of 6", Toast.LENGTH_LONG).show();
            input_layout_cpassword.setError("Password should be length of 6 or more");
            requestFocus(input_layout_cpassword);
            return false;
        } else {
            input_layout_cpassword.setErrorEnabled(false);
        }

        if (!cpassword.getText().toString().trim().equals(password.getText().toString().trim())) {

            Toast.makeText(getActivity(), "Password Mismatch", Toast.LENGTH_LONG).show();
            input_layout_cpassword.setError("Password Mismatch");
            requestFocus(input_layout_cpassword);
            return false;
        } else {
            input_layout_cpassword.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePasswordMatch() {

        if (!cpassword.getText().toString().trim().equals(password.getText().toString().trim())) {
            Toast.makeText(getActivity(), "Password Mismatch", Toast.LENGTH_LONG).show();
            input_layout_cpassword.setError("Password Mismatch");
            requestFocus(input_layout_cpassword);
            return false;
        } else {
            input_layout_cpassword.setErrorEnabled(false);
        }


        return true;
    }

    private void closeKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}
