package com.bss.controller;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bss.service.TutorialService;


@Controller
@RequestMapping(path="/main")
public class TutorialController {
	@Autowired
	private TutorialService tutorialService;
	
	private static Logger logger = LoggerFactory.getLogger(TutorialController.class);
	
	public TutorialController() {
		logger.debug("TutorialController");
	}
	
	@PostConstruct
	public void init() {
		logger.info("TutorialController init");
	}
	
	@RequestMapping(path="/")
	public String appMain()throws Exception {
		return "view";
	}
	
}
