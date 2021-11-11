package com.team6.apps.search.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;


@RestController
public class FilterChainController {

	@Autowired
	@Qualifier("springSecurityFilterChain")
	private Filter springSecurityFilterChain;

	@RequestMapping("/filterChain")
	public @ResponseBody
	Map<Integer, Map<Integer, String>> getSecurityFilterChain(){
		return this.getSecurityFilterChainProxy();
	}

	public Map<Integer, Map<Integer, String>> getSecurityFilterChainProxy(){
		Map<Integer, Map<Integer, String>> filterChains= new HashMap<Integer, Map<Integer, String>>();
		int i = 1;
		FilterChainProxy filterChainProxy = (FilterChainProxy) springSecurityFilterChain;
		for(SecurityFilterChain secfc :  filterChainProxy.getFilterChains()){
			//filters.put(i++, secfc.getClass().getName());
			Map<Integer, String> filters = new HashMap<Integer, String>();
			int j = 1;
			for(Filter filter : secfc.getFilters()){
				filters.put(j++, filter.getClass().getName());
			}
			filterChains.put(i++, filters);
		}
		return filterChains;
	}
}
