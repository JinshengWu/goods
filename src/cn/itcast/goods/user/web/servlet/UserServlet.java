package cn.itcast.goods.user.web.servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.user.domain.User;
import cn.itcast.goods.user.service.UserService;
import cn.itcast.goods.user.service.exception.UserException;
import cn.itcast.servlet.BaseServlet;

/**
 * Servlet implementation class UserServlet
 */
@WebServlet("/UserServlet")
public class UserServlet extends BaseServlet {
	private UserService userService = new UserService();

	/*
	 * ajax用户名是否注册校验
	 */
	public String ajaxValidateLoginname(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 获取用户名
		 */
		String loginname = req.getParameter("loginname");
		/*
		 * 通过service得到校验结果
		 */
		boolean b = userService.ajaxValidateLoginname(loginname);
		/*
		 * 发给客户端
		 */
		resp.getWriter().print(b);
		return null;
	}

	/*
	 * ajax Email是否注册校验
	 */
	public String ajaxValidateEmail(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 获取用email
		 */
		String email = req.getParameter("email");
		/*
		 * 通过service得到校验结果
		 */
		boolean b = userService.ajaxValidateEmail(email);
		/*
		 * 发给客户端
		 */
		resp.getWriter().print(b);
		return null;
	}

	/*
	 * 验证码是否正确
	 */
	public String ajaxValidateVerifyCode(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		/*
		 * 1.获取输入框验证码
		 */
		String VerifyCode = req.getParameter("verifyCode");
		/*
		 * 2.获取图片上真实验证码
		 */
		String vcode = (String) req.getSession().getAttribute("vCode");
		/*
		 * 进行忽略大小写比较，得到结果
		 */
		boolean b = VerifyCode.equalsIgnoreCase(vcode);
		/*
		 * 发送给客户端
		 */
		resp.getWriter().print(b);
		return null;
	}

	/*
	 * 注册功能
	 */
	public String regist(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		/*
		 * 1. 封装表单数据到User对象中
		 */
		User formUser = CommonUtils.toBean(req.getParameterMap(), User.class);

		/*
		 * 2. 对表单数据进行服务器端校验
		 */
		Map<String, String> errors = validateRegist(formUser, req.getSession());
		if (errors.size() > 0) {
			req.setAttribute("form", formUser);
			req.setAttribute("errors", errors);
			return "f:/jsps/user/regist.jsp";
		}
		/*
		 * 3. 调用userService完成注册
		 */
		userService.regist(formUser);

		/*
		 * 4. 保存注册成功信息，转发到msg.jsp显示
		 */
		req.setAttribute("code", "success");// 是成功信息还是错误信息
		req.setAttribute("msg", "恭喜，注册成功！请马上到邮箱完成激活！");
		return "f:/jsps/msg.jsp";
	}

	/*
	 * 注册校验 对表单的字段进行逐个校验，如果有错误，使用当前字段名称为key,错误信息为value,保存到map中 返回map
	 */
	private Map<String, String> validateRegist(User formUser, HttpSession session) {
		Map<String, String> errors = new HashMap<String, String>();
		/*
		 * 校验登录名
		 */
		// 1.对loginname进行校验
		String loginname = formUser.getLoginname();
		if (loginname == null || loginname.isEmpty()) {
			errors.put("loginname", "用户名不能为空！");
		} else if (loginname.length() < 3 || loginname.length() > 20) {
			errors.put("loginname", "用户名长度必须在3~20之间！");
		} else if (!userService.ajaxValidateLoginname(loginname)) {
			errors.put("loginname", "用户名已被注册过！");
		}

		// 2. 对loginpass进行校验
		String loginpass = formUser.getLoginpass();
		if (loginpass == null || loginpass.isEmpty()) {
			errors.put("loginpass", "密码不能为空！");
		} else if (loginpass.length() < 3 || loginpass.length() > 20) {
			errors.put("loginpass", "密码长度必须在3~20之间！");
		}

		// 3.对确认密码进行校验
		String reloginpass = formUser.getReloginpass();
		if (reloginpass == null || reloginpass.isEmpty()) {
			errors.put("reloginpass", "确认密码不能为空！");
		} else if (!reloginpass.equalsIgnoreCase(loginpass)) {
			errors.put("reloginpass", "两次输入密码不一致！");
		}

		// 4.对Email进行校验
		String email = formUser.getEmail();
		if (email == null || email.isEmpty()) {
			errors.put("email", "Email不能为空！");
		} else if (!email.matches("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$")) {
			errors.put("email", "错误的Email格式！");
		} else if (!userService.ajaxValidateEmail(email)) {
			errors.put("email", "Email已被注册过！");
		}

		// 5. 对验证码进行校验
		String verifyCode = formUser.getVerifyCode();
		String vCode = (String) session.getAttribute("vCode");
		if (verifyCode == null || verifyCode.isEmpty()) {
			errors.put("verifyCode", "验证码不能为空！");
		} else if (verifyCode.length() != 4) {
			errors.put("verifyCode", "验证码错误！");
		} else if (!verifyCode.equalsIgnoreCase(vCode)) {
			errors.put("verifyCode", "验证码错误！");
		}
		return errors;
	}

	/*
	 * 激活功能
	 */
	public String activation(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		/*
		 * 1.获取参数激活码 2.用激活码调用service方法完成激活
		 * >service方法可能抛出异常，把异常信息拿来，保存到request中，转发到msg.jsp显示
		 * 3.保存成功信息到request中，转发到msg.jsp显示
		 */
		String code = req.getParameter("activationCode");
		try {
			userService.activation(code);
			req.setAttribute("code", "success");// 通知msg.jsp显示对号
			req.setAttribute("msg", "恭喜，激活成功，请马上登录！");
		} catch (UserException e) {
			// 说明抛出了异常
			req.setAttribute("msg", e.getMessage());
			req.setAttribute("code", "error");// 通知msg.jsp显示x
		}
		return "f:/jsps/msg.jsp";
	}

	/*
	 * 修改密码
	 */
	public String updatePassword(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1.封装表单数据到user中 
		 * 2.从session中获取uid 
		 * 3.使用uid和表单中的oldpass和newpass来调用service方法
		 * >如果出现异常，保存异常信息到request中，转发到pwd.jsp 4.保存成功信息到request中 5.转发到msg.jsp
		 */
		User formUser = CommonUtils.toBean(req.getParameterMap(), User.class);
		User user = (User) req.getSession().getAttribute("sessionUser");
		// 如果用户没有登录，返回到登陆页面，显示错误信息
		if (user == null) {
			req.setAttribute("msg", "您还没有登录！");
			return "f:/jsps/user/login.jsp";
		}

		try {
			userService.updatePassword(user.getUid(), formUser.getNewpass(), formUser.getLoginpass());
			req.setAttribute("msg", "修改密码成功");
			req.setAttribute("code", "success");
			return "f:/jsps/msg.jsp";
		} catch (UserException e) {
			req.setAttribute("msg", e.getMessage());
			req.setAttribute("user", formUser);
			return "f:/jsps/user/pwd.jsp";
		}
	}

	/*
	 * 退出功能
	 */
	public String quit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.getSession().invalidate();
		return "r:/jsps/user/login.jsp";
	}

	/*
	 * 登录功能
	 */
	public String login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		/*
		 * 1.封装表单数据到User 
		 * 2.校验表单数据 
		 * 3.使用service查询，得到User 
		 * 4.查看用户是否存在，如果不存在：
		 * *保存错误信息：用户名或密码错误 *保存用户数据，为了回显 *转发到login.jsp 
		 * 5.如果存在，查看状态，如果状态为false：
		 * *保存错误信息：您没有激活
		 *  *保存表单数据：为了回显 *转发到login.jsp 
		 * 6.登陆成功 *保存当前查询出的user到session中
		 * *保存当前的用户到cookie中，注意中文需要编码处理。
		 */
		/*
		 * 1.封装表单数据到user
		 */
		User formUser = CommonUtils.toBean(req.getParameterMap(), User.class);
		/*
		 * 2.校验
		 */
		Map<String, String> errors = validateLogin(formUser, req.getSession());
		if (errors.size() > 0) {
			req.setAttribute("form", formUser);
			req.setAttribute("errors", errors);
			return "f:/jsps/user/login.jsp";
		}
		/*
		 * 3.调用userService#login()方法
		 */
		User user = userService.login(formUser);
		/*
		 * 4.开始判断
		 */
		if (user == null) {
			req.setAttribute("msg", "用户名或密码错误！");
			req.setAttribute("user", formUser);
			return "f:/jsps/user/login.jsp";
		} else {
			if (!user.isStatus()) {
				req.setAttribute("msg", "你还没激活！");
				req.setAttribute("user", formUser);
				return "f:/jsps/user/login.jsp";
			} else {
				// 保存用户到session
				req.getSession().setAttribute("sessionUser", user);
				// 获取用户名保存到cookie中
				String loginname = user.getLoginname();
				loginname = URLEncoder.encode(loginname, "utf-8");
				Cookie cookie = new Cookie("loginname", loginname);
				cookie.setMaxAge(60 * 60 * 24 * 10);// 保存10天
				resp.addCookie(cookie);
				return "r:/index.jsp";// 重定向到主页
			}
		}
	}

	/*
	 * 登录校验方法,后期可以自己加东西
	 */
	private Map<String, String> validateLogin(User formUser, HttpSession session) {
		Map<String, String> errors = new HashMap<String, String>();

		return errors;
	}

}
