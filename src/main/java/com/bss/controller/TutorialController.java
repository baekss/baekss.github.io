package com.bss.controller;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
@RequestMapping(path="/main")
public class TutorialController {
	
	private static Logger logger = LoggerFactory.getLogger(TutorialController.class);
	
	@PostConstruct
	public void init() {
		logger.debug("init");
	}
	
	@RequestMapping(path="/")
	public String appMain()throws Exception {
		return "view";
	}
	
}
