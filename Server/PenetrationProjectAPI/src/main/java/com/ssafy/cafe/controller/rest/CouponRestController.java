package com.ssafy.cafe.controller.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.cafe.model.dto.Coupon;
import com.ssafy.cafe.model.dto.User;
import com.ssafy.cafe.model.service.CouponService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/rest/coupon")
@CrossOrigin(allowCredentials = "true", originPatterns = { "*" })
public class CouponRestController {

	@Autowired
	CouponService cService;

	@GetMapping("/{userId}")
	@ApiOperation(value = "특정 User의 쿠폰 목록을 반환한다.", response = List.class)
	public List<Coupon> getCouponByUser(@PathVariable String userId) {
		return cService.getCouponByUser(userId);
	}
	
	@PostMapping
	@ApiOperation(value = "쿠폰 정보를 추가한다.", response = Integer.class)
	public Integer insert(@RequestBody Coupon coupon) {
		cService.addCoupon(coupon);;
		return 1;
	}
}
