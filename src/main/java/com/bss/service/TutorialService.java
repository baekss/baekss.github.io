package com.bss.service;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;



@Service
public class TutorialService {
	private static Logger logger = LoggerFactory.getLogger(TutorialService.class);
	
	public TutorialService() {
		logger.debug("TutorialService");
	}
	
	@PostConstruct
	public void init() {
		logger.debug("TutorialService init");
	}
	
}
