package cn.itcast.goods.order.web.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.cart.domain.CartItem;
import cn.itcast.goods.cart.service.CartItemService;
import cn.itcast.goods.order.domain.Order;
import cn.itcast.goods.order.domain.OrderItem;
import cn.itcast.goods.order.service.OrderService;
import cn.itcast.goods.pager.PageBean;
import cn.itcast.goods.user.domain.User;
import cn.itcast.servlet.BaseServlet;

@WebServlet("/OrderServlet")
public class OrderServlet extends BaseServlet {
	private OrderService orderService = new OrderService();
	private CartItemService cartItemService = new CartItemService();
	
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
	 * ֧��׼��
	 */
	public String paymentPre(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		req.setAttribute("order", orderService.load(req.getParameter("oid")));
		return "f:/jsps/order/pay.jsp";
	}
	
	
	/*
	 * *ȡ������
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
			return "f:/jsps/msg.jsp";
		}
		orderService.updateStatus(oid, 5);//����״̬ȡ��
		req.setAttribute("code", "success");
		req.setAttribute("msg", "���Ķ�����ȡ��");
		return "f:/jsps/msg.jsp";
	}
	
	/*
	 * *ȷ���ջ�
	 */
	public String confirm(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		String oid = req.getParameter("oid");
		/*
		 * У�鶩��״̬
		 */
		int status = orderService.findStatus(oid);
		if(status != 3) {
			req.setAttribute("code", "error");
			req.setAttribute("msg", "״̬���ԣ�����ȷ���ջ���");
			return "f:/jsps/msg.jsp";
		}
		orderService.updateStatus(oid, 4);//����״̬ȡ��
		req.setAttribute("code", "success");
		req.setAttribute("msg", "��ϲ�����׳ɹ�");
		return "f:/jsps/msg.jsp";
	}
	
	
	/*
	 * *���ض���
	 */
	public String load(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		String oid = req.getParameter("oid");
		Order order = orderService.load(oid);
		req.setAttribute("order", order);
		String btn = req.getParameter("btn");//btn˵�����û�����ĸ������������ʱ�������
		req.setAttribute("btn", btn);
		return "f:/jsps/order/desc.jsp";
	}
	
	
	/*
	 * *���ɶ���
	 */
	public String createOrder(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1.��ȡ���й��ﳵ��Ŀ��id,��ѯ֮
		 */ 
		String cartItemIds = req.getParameter("cartItemIds");
		List<CartItem> cartItemList = cartItemService.loadCartItems(cartItemIds);
		/*
		 * 2.����Order
		 */
		Order order = new Order();
		order.setOid(CommonUtils.uuid());//��������
		order.setOrdertime(String.format("%tF %<tT", new Date()));//�µ�ʱ��
		order.setStatus(1);//����״̬��1��ʾδ����
		order.setAddress(req.getParameter("address"));//�����ջ���ַ
		User owner = (User)req.getSession().getAttribute("sessionUser");
		order.setOwner(owner);//���ö���������
		
		BigDecimal total = new BigDecimal("0");
		for(CartItem cartItem : cartItemList) {
			total = total.add(new BigDecimal(cartItem.getSubtotal() + ""));
		}
		order.setTotal(total.doubleValue());//�����ܼ�
		
		/*
		 * 3.����List<OrderItem>
		 * һ��CartItem��Ӧһ��OrderItem
		 */
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		for(CartItem cartitem : cartItemList) {
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderItemId(CommonUtils.uuid());
			orderItem.setQuantity(cartitem.getQuantity());
			orderItem.setSubtotal(cartitem.getSubtotal());
			orderItem.setBook(cartitem.getBook());
			orderItem.setOrder(order);
			orderItemList.add(orderItem);
		}
		order.setOrderItemList(orderItemList);
		
		/*
		 * 4.����service�������
		 */
		orderService.createOrder(order);
		
		//ɾ�����ﳵ��Ŀ
		cartItemService.batchDelete(cartItemIds);
		
		/*
		 * 5.���涩����ת����ordersucc.jsp
		 */
		req.setAttribute("order", order);
		return "f:/jsps/order/ordersucc.jsp";
	}
	
	
	/*
	 * *�ҵĶ���
	 */
	public String myOrders(HttpServletRequest req, HttpServletResponse resp) 
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
		 * 3.�ӵ�ǰsession�л�ȡ
		 */
		User user = (User)req.getSession().getAttribute("sessionUser");
		/*
		 * 4.ʹ��pc��cid����service#findByCategory�õ�PageBean
		 */
		PageBean<Order> pb = orderService.myOrders(user.getUid(), pc);
		/*
		 * ��PageBean����url������PageBean��ת����/jsps/book/list.jsp
		 */
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/jsps/order/list.jsp";
	}
}