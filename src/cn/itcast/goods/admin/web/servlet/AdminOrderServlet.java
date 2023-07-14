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
	 * ��ȡ��ǰҳ��
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
	 * ��ȡurl��ҳ���еķ�ҳ��������Ҫʹ������Ϊ������Ŀ�꣡
	 */
	/*
	 * http://localhost:8080/goods/BookServlet?methed=findByCategory&cid==xxx&pc=3
	 * /goods/BookServlet+methed=findByCategory&cid==xxx&pc=3
	 */
	private String getUrl(HttpServletRequest req) {
		String url = req.getRequestURI() + "?" + req.getQueryString();
		/*
		 * ���url�д���pc��������ȡ������������ڲ��ý�ȡ
		 */
		int index = url.lastIndexOf("&pc=");
		if(index != -1) {
			url = url.substring(0, index);
		}
		return url;
	}
	
	/*
	 * *��ѯ���ж���
	 */
	public String findAll(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1.�õ�pc:���ҳ�洫�ݣ�ʹ��ҳ��ģ����û����pc=1
		 */
		int pc = getpc(req);
		/*
		 * 2.�õ�url
		 */
		String url = getUrl(req);
		/*
		 * 4.ʹ��pc��cid����service#findByCategory�õ�PageBean
		 */
		PageBean<Order> pb = orderService.findAll(pc);
		/*
		 * ��PageBean����url������PageBean��ת����/jsps/book/list.jsp
		 */
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/adminjsps/admin/order/list.jsp";
	}
	
	/*
	 **��״̬��ѯ
	 */
	public String findByStatus(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1.�õ�pc:���ҳ�洫�ݣ�ʹ��ҳ��ģ����û����pc=1
		 */
		int pc = getpc(req);
		/*
		 * 2.�õ�url
		 */
		String url = getUrl(req);
		/*
		 * ��ȡ���Ӳ���
		 */
		int status = Integer.parseInt(req.getParameter("status"));
		/*
		 * 4.ʹ��pc��cid����service#findByCategory�õ�PageBean
		 */
		PageBean<Order> pb = orderService.findByStatus(status, pc);
		/*
		 * ��PageBean����url������PageBean��ת����/jsps/book/list.jsp
		 */
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/adminjsps/admin/order/list.jsp";
	}
	
	/*
	 ** �鿴������ϸ��Ϣ
	 */
	public String load(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		String oid = req.getParameter("oid");
		Order order = orderService.load(oid);
		req.setAttribute("order", order);
		String btn = req.getParameter("btn");//btn˵�����û�����ĸ������������ʱ�������
		req.setAttribute("btn", btn);
		return "f:/adminjsps/admin/order/desc.jsp";
	}
	
	/*
	 ** ȡ������
	 */
	public String cancel(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		String oid = req.getParameter("oid");
		/*
		 * У�鶩��״̬
		 */
		int status = orderService.findStatus(oid);
		if(status != 1) {
			req.setAttribute("code", "error");
			req.setAttribute("msg", "״̬���ԣ�����ȡ����");
			return "f:/adminjsps/msg.jsp";
		}
		orderService.updateStatus(oid, 5);//����״̬ȡ��
		req.setAttribute("code", "success");
		req.setAttribute("msg", "���Ķ�����ȡ��");
		return "f:/adminjsps/msg.jsp";
	}
	
	/*
	 * *��������
	 */
	public String deliver(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		String oid = req.getParameter("oid");
		/*
		 * У�鶩��״̬
		 */
		int status = orderService.findStatus(oid);
		if(status != 2) {
			req.setAttribute("code", "error");
			req.setAttribute("msg", "״̬���ԣ����ܷ�����");
			return "f:/adminjsps/msg.jsp";
		}
		orderService.updateStatus(oid, 3);//����״̬ȡ��
		req.setAttribute("code", "success");
		req.setAttribute("msg", "���Ķ����ѷ���");
		return "f:/adminjsps/msg.jsp";
	}
}
