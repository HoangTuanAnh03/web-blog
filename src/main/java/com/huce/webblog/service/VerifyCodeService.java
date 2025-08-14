package com.huce.webblog.service;

import com.huce.webblog.dto.request.VerifyNewPasswordRequest;
import com.huce.webblog.entity.User;
import com.huce.webblog.entity.VerificationCode;

public interface VerifyCodeService {

     void delete (VerificationCode verificationCode);

     VerificationCode findByEmail(String email);

     VerificationCode save(VerificationCode verificationCode);

     Boolean isTimeOutRequired(VerificationCode verificationCode, long ms);

     User verifyRegister(String code);

     User verifyForgotPassword(VerifyNewPasswordRequest request);
}
