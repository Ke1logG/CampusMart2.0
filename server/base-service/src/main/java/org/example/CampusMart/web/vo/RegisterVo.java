package org.example.CampusMart.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "用户注册信息")
@Data
public class RegisterVo {
    @Schema(description="username")
    @NotBlank(message = "昵称不能为空")
    private String username;

    @Schema(description="password")
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description="phone")
    @NotNull(message = "手机号不能为空")
    @Min(value = 10000000000L, message = "手机号必须为11位数字")
    @Max(value = 99999999999L, message = "手机号必须为11位数字")
    private Long phone;

    @Schema(description="school")
    private String schoolName;

    @Schema(description="student id")
    private Long studentID;
}
