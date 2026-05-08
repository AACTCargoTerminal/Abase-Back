package com.aact.authservice.controller;


import com.aact.authservice.dto.InfraUser;
import com.aact.authservice.dto.LoginReq;
import com.aact.authservice.dto.SetUserDTO;
import com.aact.authservice.service.UserService;
import com.aact.common.ResponseDTO;
import com.aact.common.SysException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("user")
@Validated
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDTO<?> login(HttpServletRequest reqs, @RequestBody @Validated LoginReq.LoginDTO req) {
        return userService.login(req,reqs);
    }
    @GetMapping(value = "/verity")
    public ResponseDTO<?> verity() {
        return userService.verity();
    }
    //logout
    @GetMapping(value = "/logout")
    public ResponseDTO<?> logout(HttpServletRequest hReq) {
        return userService.logout(hReq);
    }

    @GetMapping(value = "/buildMenu")
    public ResponseDTO<?> buildMenu() {
        return userService.buildMenu();
    }

    @GetMapping(value = "/getPgmInfo")
    public ResponseDTO<?> getPgmInfo(@RequestParam("pgmId") String pgmId) {
        return userService.getPgmInfo(pgmId);
    }

    @GetMapping(value = "/getUserInfo")
    public ResponseDTO<?> getUserInfo() {

        return userService.getUserInfo();
    }

    @PostMapping(value = "/setUserInfo")
    public ResponseDTO<?> setUserInfo(HttpServletRequest req,@ModelAttribute SetUserDTO dto) {
        return userService.setUserInfo(req,dto);
    }
    @PostMapping(value = "/setUserInfoMgm")
    public ResponseDTO<?> setUserInfoMgm(@RequestBody InfraUser.SaveUserDTO dto) {
        return userService.setUserInfoMgm(dto);
    }
    //setUserDelete
    @GetMapping("/setUserDelete")
    public ResponseDTO<?> setUserDelete(@RequestParam("userId") String userId) {
        return userService.setUserDelete(userId);
    }

    @GetMapping("/getUserList")
    public ResponseDTO<?> getUserList(@RequestParam("depCode") String depCode) {
        return userService.getUserList(depCode);
    }

    @GetMapping("/getUserList2")
    public ResponseDTO<?> getUserList2(@RequestParam("depCode") String depCode,@RequestParam("userId") String userId,@RequestParam("userName") String userName,@RequestParam("usableFlag") String usableFlag) {
        return userService.getUserList(depCode,userId,userName,usableFlag);
    }


    @PostMapping("/setUserSign")
    public ResponseDTO<?> setUserSign(HttpServletRequest req, @RequestParam("files") List<MultipartFile> files) {
        return userService.setUserSign(req,files);
    }

    @GetMapping("/getUserSign")
    public ResponseDTO<?> getUserSign(@RequestParam("userId") String userId) {
        return userService.getUserSign(userId);
    }

    @GetMapping(value = "/getUserInfo2")
    public ResponseDTO<?> getUserInfo2(@RequestParam("userId") String userId) {

        return userService.getUserInfo(userId);
    }

    @GetMapping(value = "/getUserRel")
    public ResponseDTO<?> getUserRel(@RequestParam("userSid") BigDecimal userSid, @RequestParam("usableFlag") String usableFlag) {

        return userService.getUserRel(userSid,usableFlag);
    }

    @PostMapping(value = "/setUserRel")
    public ResponseDTO<?> setUserRel(@RequestBody Map<String,List<InfraUser.UserRelDTO>> dtos) {

        if(dtos.get("SAVE")==null){
            throw new SysException("setUserRel","요청데이터가 없습니다.");
        }

        return userService.setUserRel(dtos.get("SAVE"));
    }

    @PostMapping(value = "/delUserRel")
    public ResponseDTO<?> delUserRel(@RequestBody Map<String,List<InfraUser.UserRelDTO>> dtos) {

        if(dtos.get("DEL")==null){
            throw new SysException("setUserRel","요청데이터가 없습니다.");
        }

        return userService.delUserRel(dtos.get("DEL"));
    }
}
