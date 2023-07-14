package cn.itcast.goods.admin.admin.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.admin.admin.domain.Admin;
import cn.itcast.goods.admin.admin.service.AdminService;
import cn.itcast.servlet.BaseServlet;

/**
 * Servlet implementation class AdminServlet
 */
@WebServlet("/AdminServlet")
public class AdminServlet extends BaseServlet {
	private AdminService adminService = new AdminService();
	
	
	public String login(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * *封装表单数据到Admin
		 */
		Admin form = CommonUtils.toBean(req.getParameterMap(),Admin.class);
		Admin admin = adminService.login(form);
		if(admin == null ) {
			req.setAttribute("msg", "用户名或密码错误！");
			return "f:/adminjsps/login.jsp";
		}
		req.getSession().setAttribute("admin", admin);
		return "r:/adminjsps/admin/index.jsp";
	}
       

}
