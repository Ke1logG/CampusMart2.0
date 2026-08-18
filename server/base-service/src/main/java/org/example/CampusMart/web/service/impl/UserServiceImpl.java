package org.example.CampusMart.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.CampusMart.common.exception.CampusMartException;
import org.example.CampusMart.common.result.ResultCodeEnum;
import org.example.CampusMart.common.utils.CodeUtil;
import org.example.CampusMart.common.utils.JwtUtil;
import org.example.CampusMart.model.entity.User;
import org.example.CampusMart.web.service.UserService;
import org.example.CampusMart.web.mapper.UserMapper;
import org.example.CampusMart.web.vo.LoginVo;
import org.example.CampusMart.web.vo.RegisterVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean register(RegisterVo registerVo) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, registerVo.getUsername());
        User user = userMapper.selectOne(queryWrapper);
        if (user!=null) {
            throw new CampusMartException(ResultCodeEnum.ADMIN_ACCOUNT_EXIST_ERROR);
        }
        user = new User();
        BeanUtils.copyProperties(registerVo,user);
        user.setNickname("User"+ CodeUtil.getRandomCode(5));
        user.setProfileSignature("hello everyone");
        userMapper.insert(user);
        return true;
    }

    @Override
    public String login(LoginVo loginVo) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, loginVo.getUsername());
        User user = userMapper.selectOne(queryWrapper);
        if (user==null) {
            throw new CampusMartException(ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR);
        }
        if (!user.getPassword().equals(loginVo.getPassword())) {
            throw new CampusMartException(ResultCodeEnum.ADMIN_ACCOUNT_ERROR);
        }

        return JwtUtil.createToken(user.getUserID(), user.getUsername());
    }

    @Override
    @Cacheable(value = "user:info", key = "#id", sync = true)
    public User getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(value = "user:info", key = "#entity.userID")
    public boolean save(User entity) {
        return super.save(entity);
    }

    @Override
    @CacheEvict(value = "user:info", key = "#entity.userID")
    public boolean updateById(User entity) {
        return super.updateById(entity);
    }

    @Override
    @CacheEvict(value = "user:info", key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}




