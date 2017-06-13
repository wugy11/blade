package io.example.blog.controller;

import com.blade.ioc.annotation.Inject;
import com.blade.mvc.Const;
import com.blade.mvc.annotation.*;
import com.blade.mvc.http.HttpMethod;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.http.Session;
import com.blade.mvc.multipart.FileItem;
import com.blade.mvc.ui.RestResponse;
import io.example.blog.model.Article;
import io.example.blog.service.AService;

import java.io.File;

/**
 * @author biezhi 2017/5/31
 */
@Path
public class IndexController {

	@Inject
	private AService aService;

	@Route(values = "/hello", method = HttpMethod.GET)
	public void index(Response response) {
		// aService.sayHi();
		response.text("hello world!");
	}

	@Route(values = "/user", method = HttpMethod.GET)
	public String userPage(Request request, Session session) {
		request.attribute("name", "biezhi");
		request.attribute("github", "https://github.com/biezhi");
		if (null != session) {
			session.attribute("loginUser", "admin");
		}
		return "user.html";
	}

	@Route(values = "/save", method = HttpMethod.POST)
	@JSON
	public RestResponse<?> saveArticle(@BodyParam Article article, Request request) {
		System.out.println(article);
		if (null == article) {
			System.out.println(request.bodyToString());
		}
		return RestResponse.ok();
	}

	@Route(values = "upload", method = HttpMethod.POST)
	@JSON
	public RestResponse<?> upload(@MultipartParam("img2") FileItem fileItem) {
		System.out.println(fileItem);
		return RestResponse.ok();
	}

	@Route(values = "exp1", method = HttpMethod.GET)
	@JSON
	public void exp1() {
		int a = 1 / 0;
	}

	@Route(values = "exp2", method = HttpMethod.GET)
	public void exp2() {
		aService.exp();
	}

	@Route(values = "empty", method = HttpMethod.GET)
	public void empty() {
		// System.out.println("empty request");
	}

	@Route(values = "download", method = HttpMethod.GET)
	public void download(Response response) throws Exception {
		String path = Const.CLASSPATH + "static/a.txt";
		response.donwload("文件.txt", new File(path));
	}

	@Route(values = "redirect", method = HttpMethod.GET)
	public void redirect(@QueryParam String url, Response response) {
		response.redirect(url);
	}
}
