package org.example.CampusMart.web.service;

public interface SmsService {

    boolean sendVerifyCode(String phone, String code);
}
