package com.example.demo.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.services.LitService;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

@RestController
public class LitController {
	@Autowired
	LitService litService;
	@GetMapping("/hello")
	public String sayHello()
	{
		return "Hello";
	}
	@PostMapping("/lit")
	public void getStory(@RequestHeader String url,@RequestHeader String  title) throws IOException
	{
		StringBuilder sb=new StringBuilder();
		sb=litService.getStory(url, title,sb);
		File f=new File(title);
		FileUtils.write(f, sb.toString(), Charset.forName("UTF-8"), true);
	}
	@PostMapping("/lit-series")
	public void getSeries(@RequestHeader String url,@RequestHeader String  title) throws IOException {
	litService.getSeriesStandalone(url, title);


	}
	@PostMapping("/lit-author")
	public void getAuthors(@RequestHeader String url,@RequestHeader String  title,@RequestHeader boolean isStories) throws IOException {
        StringBuilder sb=litService.getAuthors2(url,title,isStories);


	}

}
