package org.example.CampusMart.web.service.impl;

import com.aliyun.oss.OSS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.CampusMart.common.exception.CampusMartException;
import org.example.CampusMart.common.oss.OssProperties;
import org.example.CampusMart.common.result.ResultCodeEnum;
import org.example.CampusMart.model.entity.Picture;
import org.example.CampusMart.web.service.PictureService;
import org.example.CampusMart.web.mapper.PictureMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
* @author a32271
* @description 针对表【picture】的数据库操作Service实现
* @createDate 2025-12-27 23:08:03
*/
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService{

    @Autowired
    private OSS ossClient;

    @Autowired
    private OssProperties ossProperties;

    @Override
    public String upload(MultipartFile file) {
        try{
            String filename = new SimpleDateFormat("yyyyMMdd").format(new Date()) +
                    "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
            ossClient.putObject(
                    ossProperties.getBucketName(),
                    filename,
                    file.getInputStream()
            );
            String url = "https://" + ossProperties.getBucketName() + "." + ossProperties.getEndpoint() + "/" + filename;
            return url;
        }catch(Exception e){
            e.printStackTrace();
            throw new CampusMartException(ResultCodeEnum.DATA_ERROR);
        }
    }
}




