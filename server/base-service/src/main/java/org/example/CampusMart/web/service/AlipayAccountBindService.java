package org.example.CampusMart.web.service;

import org.example.CampusMart.model.entity.AlipayAccountBind;

public interface AlipayAccountBindService {

    AlipayAccountBind getByUserId(Long userId);

    boolean bind(Long userId, String account, String realName);
}
