package com.ssafy.cafe.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Coupon {
	private Integer id;
	private String userId;
	private String title;
	private String endDate;
	private Integer percent;
	private String used;
	
	@Builder
	public Coupon(Integer id, String userId, String title, String endDate, Integer percent, String used) {
		super();
		this.id = id;
		this.userId = userId;
		this.title = title;
		this.endDate = endDate;
		this.percent = percent;
		this.used = used;
	}
	
	public Coupon(String userId, String title, String endDate, Integer percent, String used) {
		this.userId = userId;
		this.title = title;
		this.endDate = endDate;
		this.percent = percent;
		this.used = used;
	}
}
