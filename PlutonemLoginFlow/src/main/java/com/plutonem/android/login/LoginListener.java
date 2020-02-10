package com.plutonem.android.login;

import java.util.ArrayList;

public interface LoginListener {
    LoginMode getLoginMode();

    // Login email password callbacks
    void loggedInViaPassword();

    // Signup
    void doStartSignup();
    void showSignupPhonePassword(String phone);
}
