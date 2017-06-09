package io.example.blog.controller;

import com.blade.mvc.annotation.Path;
import com.blade.mvc.annotation.Route;
import com.blade.mvc.http.HttpMethod;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.middlewares.CsrfMiddleware;

/**
 * @author biezhi 2017/6/5
 */
@Path
public class AuthController {

	@Route(values = "login", method = HttpMethod.GET)
	public void login(Request request, Response response) {
		response.text(request.attribute(CsrfMiddleware.CSRF_TOKEN));
	}

	@Route(values = "login", method = HttpMethod.POST)
	@CsrfMiddleware.ValidToken
	public void doLogin(Response response) {
		response.text("登录成功");
	}

}
