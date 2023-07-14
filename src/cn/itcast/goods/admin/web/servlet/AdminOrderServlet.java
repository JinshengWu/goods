package cn.itcast.goods.admin.web.servlet;

import cn.itcast.goods.order.domain.Order;
import cn.itcast.goods.order.service.OrderService;
import cn.itcast.goods.pager.PageBean;
import cn.itcast.goods.user.domain.User;
import cn.itcast.servlet.BaseServlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/admin/AdminOrderServlet")
public class AdminOrderServlet extends BaseServlet {
	private OrderService orderService = new OrderService();
	
	
	/*
	 * 获取当前页码
	 */
	private int getpc(HttpServletRequest req) {
		int pc = 1;
		String param = req.getParameter("pc");
		if (param != null && !param.trim().isEmpty()) {
			try {
				pc = Integer.parseInt(param);
			} catch (Exception e) {
			}
		}
		return pc;
	}
	
	
	/*
	 * 截取url，页面中的分页导航中需要使用它作为超链接目标！
	 */
	/*
	 * http://localhost:8080/goods/BookServlet?methed=findByCategory&cid==xxx&pc=3
	 * /goods/BookServlet+methed=findByCategory&cid==xxx&pc=3
	 */
	private String getUrl(HttpServletRequest req) {
		String url = req.getRequestURI() + "?" + req.getQueryString();
		/*
		 * 如果url中存在pc参数，截取掉，如果不存在不用截取
		 */
		int index = url.lastIndexOf("&pc=");
		if(index != -1) {
			url = url.substring(0, index);
		}
		return url;
	}
	
	/*
	 * *查询所有订单
	 */
	public String findAll(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1.得到pc:如果页面传递，使用页面的，如果没传，pc=1
		 */
		int pc = getpc(req);
		/*
		 * 2.得到url
		 */
		String url = getUrl(req);
		/*
		 * 4.使用pc和cid调用service#findByCategory得到PageBean
		 */
		PageBean<Order> pb = orderService.findAll(pc);
		/*
		 * 给PageBean设置url，保存PageBean，转发到/jsps/book/list.jsp
		 */
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/adminjsps/admin/order/list.jsp";
	}
	
	/*
	 **按状态查询
	 */
	public String findByStatus(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1.得到pc:如果页面传递，使用页面的，如果没传，pc=1
		 */
		int pc = getpc(req);
		/*
		 * 2.得到url
		 */
		String url = getUrl(req);
		/*
		 * 获取链接参数
		 */
		int status = Integer.parseInt(req.getParameter("status"));
		/*
		 * 4.使用pc和cid调用service#findByCategory得到PageBean
		 */
		PageBean<Order> pb = orderService.findByStatus(status, pc);
		/*
		 * 给PageBean设置url，保存PageBean，转发到/jsps/book/list.jsp
		 */
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/adminjsps/admin/order/list.jsp";
	}
	
	/*
	 ** 查看订单详细信息
	 */
	public String load(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		String oid = req.getParameter("oid");
		Order order = orderService.load(oid);
		req.setAttribute("order", order);
		String btn = req.getParameter("btn");//btn说明了用户点击哪个超链接来访问本方法的
		req.setAttribute("btn", btn);
		return "f:/adminjsps/admin/order/desc.jsp";
	}
	
	/*
	 ** 取消订单
	 */
	public String cancel(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		String oid = req.getParameter("oid");
		/*
		 * 校验订单状态
		 */
		int status = orderService.findStatus(oid);
		if(status != 1) {
			req.setAttribute("code", "error");
			req.setAttribute("msg", "状态不对，不能取消！");
			return "f:/adminjsps/msg.jsp";
		}
		orderService.updateStatus(oid, 5);//设置状态取消
		req.setAttribute("code", "success");
		req.setAttribute("msg", "您的订单已取消");
		return "f:/adminjsps/msg.jsp";
	}
	
	/*
	 * *发货功能
	 */
	public String deliver(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		String oid = req.getParameter("oid");
		/*
		 * 校验订单状态
		 */
		int status = orderService.findStatus(oid);
		if(status != 2) {
			req.setAttribute("code", "error");
			req.setAttribute("msg", "状态不对，不能发货！");
			return "f:/adminjsps/msg.jsp";
		}
		orderService.updateStatus(oid, 3);//设置状态取消
		req.setAttribute("code", "success");
		req.setAttribute("msg", "您的订单已发货");
		return "f:/adminjsps/msg.jsp";
	}
}
