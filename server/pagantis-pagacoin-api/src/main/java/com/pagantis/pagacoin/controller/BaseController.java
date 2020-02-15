package com.pagantis.pagacoin.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = { "http://localhost:3000" })
public abstract class BaseController {
	
	protected static final String BLANK = " ";
	
    @Autowired
    private MessageSource messageSource;

	protected String getMessage(String key) {
		//TODO: Cuando se traduzcan los mensajes a otros idiomas, se detectará con LocaleContextHolder.getLocale()
		return messageSource.getMessage(key, null, new Locale("es"));
	}
}
