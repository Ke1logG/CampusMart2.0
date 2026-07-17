package org.example.CampusMart.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.CampusMart.model.entity.AlipayAccountBind;
import org.example.CampusMart.web.mapper.AlipayAccountBindMapper;
import org.example.CampusMart.web.service.AlipayAccountBindService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AlipayAccountBindServiceImpl extends ServiceImpl<AlipayAccountBindMapper, AlipayAccountBind> implements AlipayAccountBindService {

    @Override
    public AlipayAccountBind getByUserId(Long userId) {
        LambdaQueryWrapper<AlipayAccountBind> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlipayAccountBind::getUserID, userId);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public boolean bind(Long userId, String account, String realName) {
        AlipayAccountBind exist = getByUserId(userId);
        if (exist != null) {
            exist.setAccount(account);
            exist.setRealName(realName);
            exist.setUpdateTime(new Date());
            baseMapper.updateById(exist);
            return true;
        }
        AlipayAccountBind bind = new AlipayAccountBind();
        bind.setUserID(userId);
        bind.setAccount(account);
        bind.setRealName(realName);
        bind.setCreateTime(new Date());
        bind.setUpdateTime(new Date());
        baseMapper.insert(bind);
        return true;
    }
}
