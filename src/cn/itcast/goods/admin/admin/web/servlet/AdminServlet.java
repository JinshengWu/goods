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
		 * *��װ�����ݵ�Admin
		 */
		Admin form = CommonUtils.toBean(req.getParameterMap(),Admin.class);
		Admin admin = adminService.login(form);
		if(admin == null ) {
			req.setAttribute("msg", "�û������������");
			return "f:/adminjsps/login.jsp";
		}
		req.getSession().setAttribute("admin", admin);
		return "r:/adminjsps/admin/index.jsp";
	}
       

}
