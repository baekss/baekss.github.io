package com.bss.controller;

import java.util.Map;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="/api")
public class TutorialApiController {
	
	@RequestMapping(path="/informations", method= {RequestMethod.POST, RequestMethod.PUT})
	public ModelMap getInformationsPOST(@RequestBody Map<String, String> map) throws Exception{
		ModelMap resultMap = new ModelMap();
		resultMap.addAllAttributes(map);
		return resultMap;
	}
	
	@RequestMapping(path="/informations", method=RequestMethod.GET)
	public ModelMap getInformationsGET(@RequestParam("id") String id) throws Exception{
		ModelMap resultMap = new ModelMap();
		resultMap.put("id", id);
		return resultMap;
	}
}
