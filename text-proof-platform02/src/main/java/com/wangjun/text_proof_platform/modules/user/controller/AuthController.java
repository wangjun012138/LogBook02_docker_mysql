package com.wangjun.text_proof_platform.modules.user.controller;


import com.wangjun.text_proof_platform.common.ApiResponse;
import com.wangjun.text_proof_platform.modules.user.dto.ChangePwdRequest;
import com.wangjun.text_proof_platform.modules.user.dto.LoginRequest;
import com.wangjun.text_proof_platform.modules.user.dto.RegisterRequest;
import com.wangjun.text_proof_platform.modules.user.dto.ResetPwdRequest;
import com.wangjun.text_proof_platform.modules.user.entity.User;
import com.wangjun.text_proof_platform.modules.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // 发送验证码
    @PostMapping("/code")
    public ApiResponse<Void> sendCode(@RequestParam String email) {
        authService.sendCode(email);
        return ApiResponse.success("验证码已发送");
    }

    // 注册
    @PostMapping("/register")
    public ApiResponse<Void> register(@RequestBody RegisterRequest req) {
        authService.register(req.getEmail(), req.getUsername(), req.getPassword(), req.getCode());
        return ApiResponse.success("注册成功");
    }

    // 登录
    @PostMapping("/login")
    public ApiResponse<Object> login(@RequestBody LoginRequest req) {
        try {
            User user = authService.login(req.getAccount(), req.getPassword());
            // 注意：生产环境应返回 JWT Token，此处简化返回用户ID
            return ApiResponse.success("登录成功", user.getId());
        } catch (Exception e) {
            return ApiResponse.error(401, e.getMessage());
        }
    }

    // 修改密码 (旧密码方式)
    @PostMapping("/password/change")
    public ApiResponse<Void> changePassword(@RequestBody ChangePwdRequest req) {
        authService.changePassword(req.getUsername(), req.getOldPassword(), req.getNewPassword());
        return ApiResponse.success("密码修改成功");
    }

    // 重置密码 (忘记密码方式)
    @PostMapping("/password/reset")
    public ApiResponse<Void> resetPassword(@RequestBody ResetPwdRequest req) {
        authService.resetPassword(req.getEmail(), req.getCode(), req.getNewPassword());
        return ApiResponse.success("密码重置成功");
    }
}
