package com.pinyougou.user.controller;

import com.pinyougou.pojo.TbUser;
import com.pinyougou.utils.PhoneFormatCheckUtils;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.user.service.UserService;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Reference
	private UserService userService;

	@RequestMapping("/register")
	public Result register(@RequestBody TbUser user,String smscode){
		try {
			if(!userService.compareCode(user.getPhone(), smscode)){
				return new Result(false,"验证码输入错误");
			}
			userService.add(user);
			return new Result(true,"注册成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"注册失败");

		}
	}
	@RequestMapping("/sendCode")
	public Result sendCode(String phone){
		if (!PhoneFormatCheckUtils.isChinaPhoneLegal(phone)) {
			return new Result(false,"手机号格式错误");
		}
		try {
			userService.sendCode(phone);
			return new Result(true,"发送成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"发送失败");
		}
	}



}
