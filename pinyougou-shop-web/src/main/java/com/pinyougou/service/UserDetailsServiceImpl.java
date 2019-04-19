package com.pinyougou.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;

public class UserDetailsServiceImpl implements UserDetailsService {
	
	private SellerService sellerService;
	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		//构建角色对象
		List<GrantedAuthority> grantAuths=new ArrayList<>();
		grantAuths.add(new SimpleGrantedAuthority("ROLE_SELLER"));
		//获取数据库中商家用户对象
		TbSeller seller = sellerService.findOne(username);
		if (seller!=null&&"1".equals(seller.getStatus())) {
			return new User(username, seller.getPassword(),grantAuths);
		}else {
			return null;
		}
	}
	

}
