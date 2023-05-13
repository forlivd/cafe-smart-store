package com.ssafy.cafe.model.service;

import java.util.List;

import com.ssafy.cafe.model.dto.Coupon;

public interface CouponService {

	List<Coupon> getCouponByUser(String userId);
	
	public void addCoupon(Coupon coupon);
}
