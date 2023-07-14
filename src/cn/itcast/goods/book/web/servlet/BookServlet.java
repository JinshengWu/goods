package cn.itcast.goods.book.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.book.service.BookService;
import cn.itcast.goods.pager.PageBean;
import cn.itcast.servlet.BaseServlet;

@WebServlet("/BookServlet")
public class BookServlet extends BaseServlet {
	private BookService bookService = new BookService();
	
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
	 * *��bid��ѯ
	 */
	public String load(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String bid = req.getParameter("bid");
		Book book = bookService.load(bid);
		req.setAttribute("book", book);
		return "f:/jsps/book/desc.jsp";
	}
	
	/*
	 ** �������
	 */
	public String findByCategory(HttpServletRequest req, HttpServletResponse resp) 
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
		 * 3.��ȡ��ѯ����������������cid���ȷ����id
		 */
		String cid = req.getParameter("cid");
		/*
		 * 4.ʹ��pc��cid����service#findByCategory�õ�PageBean
		 */
		PageBean<Book> pb = bookService.findByCategory(cid, pc);
		/*
		 * ��PageBean����url������PageBean��ת����/jsps/book/list.jsp
		 */
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/jsps/book/list.jsp";
	}
	
	/*
	 * *�����߲�
	 */
	public String findByAuthor(HttpServletRequest req, HttpServletResponse resp) 
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
		 * 3.��ȡ��ѯ����
		 */
		String author = req.getParameter("author");
		/*
		 * 4.ʹ��pc��cid����service#findByCategory�õ�PageBean
		 */
		PageBean<Book> pb = bookService.findByAuthor(author, pc);
		/*
		 * ��PageBean����url������PageBean��ת����/jsps/book/list.jsp
		 */
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/jsps/book/list.jsp";
	}
	
	/*
	 * *���������
	 */
	public String findByPress(HttpServletRequest req, HttpServletResponse resp) 
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
		 * 3.��ȡ��ѯ����
		 */
		String press = req.getParameter("press");
		/*
		 * 4.ʹ��pc��cid����service#findByCategory�õ�PageBean
		 */
		PageBean<Book> pb = bookService.findByPress(press, pc);
		/*
		 * ��PageBean����url������PageBean��ת����/jsps/book/list.jsp
		 */
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/jsps/book/list.jsp";
	}
	
	/*
	 * *��������
	 */
	public String findByBname(HttpServletRequest req, HttpServletResponse resp) 
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
		 * 3.��ȡ��ѯ����
		 */
		String bname = req.getParameter("bname");
		/*
		 * 4.ʹ��pc��cid����service#findByCategory�õ�PageBean
		 */
		PageBean<Book> pb = bookService.findByBname(bname, pc);
		/*
		 * ��PageBean����url������PageBean��ת����/jsps/book/list.jsp
		 */
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/jsps/book/list.jsp";
	}
	
	
	/*
	 * *��������ϲ�
	 */
	public String findByCombination(HttpServletRequest req, HttpServletResponse resp) 
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
		 * 3.��ȡ��ѯ����
		 */
		Book criteria = CommonUtils.toBean(req.getParameterMap(), Book.class);
		/*
		 * 4.ʹ��pc��cid����service#findByCategory�õ�PageBean
		 */
		PageBean<Book> pb = bookService.findByCombination(criteria, pc);
		/*
		 * ��PageBean����url������PageBean��ת����/jsps/book/list.jsp
		 */
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/jsps/book/list.jsp";
	}
}
