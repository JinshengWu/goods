package cn.itcast.goods.admin.book.web.servlet;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.book.service.BookService;
import cn.itcast.goods.category.domain.Category;
import cn.itcast.goods.category.service.CategoryService;
import cn.itcast.goods.pager.PageBean;
import cn.itcast.servlet.BaseServlet;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class AdminBookServlet
 */
@WebServlet("/admin/AdminBookServlet")
public class AdminBookServlet extends BaseServlet {
	private BookService bookService = new BookService();
	private CategoryService categoryService = new CategoryService();
	
	/*
	 * *删除图片
	 */
	public String delete(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		String bid = req.getParameter("bid");
		Book book = bookService.load(bid);
		String savepath = this.getServletContext().getRealPath("/");//获取真实路径
		new File(savepath,book.getImage_w()).delete();//删除图片
		new File(savepath,book.getImage_b()).delete();//删除图片
		
		bookService.delete(bid);
		
		req.setAttribute("msg", "删除图书成功！");
		return "f:/adminjsps/msg.jsp";
	}
	
	
	/*
	 * *修改图书
	 */
	public String edit(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1.把表单数据封装到Book对象中
		 * 2.封装cid到Category中
		 * 3.把Category赋给Book
		 * 4.调用Service完成工作
		 * 5.保存成功信息，转发到msg.jsp
		 */
		Map map = req.getParameterMap();
		Book book = CommonUtils.toBean(map, Book.class);
		Category category = CommonUtils.toBean(map, Category.class);
		book.setCategory(category);
		
		bookService.edit(book);
		req.setAttribute("msg", "修改图书成功！");
		return "f:/adminjsps/msg.jsp";
		
	}
	
	
	/*
	 * *加载图书
	 */
	public String load(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1.获取bid，得到Book对象，保存之
		 */
		String bid = req.getParameter("bid");
		Book book = bookService.load(bid);
		req.setAttribute("book", book);
		
		/*
		 * 2.获取所有一级分类，保存之
		 */
		req.setAttribute("parents", categoryService.findParents());
		/*
		 * 3.获取当前图书所属的一级分类下的所有二级分类
		 */
		String pid = book.getCategory().getParent().getCid();
		req.setAttribute("children", categoryService.findChild(pid));
		/*
		 * 4.转发到desc.jsp显示
		 */
		return "f:/adminjsps/admin/book/desc.jsp";
	}
	
	
	/*
	 * *添加图书：第一步
	 */
	public String addPre(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1.获取所有一级分类
		 * 2.转发到add.jsp,该页面会在下拉列表中显示所有一级分类
		 */
		List<Category> parents = categoryService.findParents();
		req.setAttribute("parents", parents);
		return "f:/adminjsps/admin/book/add.jsp";
	}
	
	
	public String ajaxFindChildren(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1.获取pid
		 * 2.通过pid查询出所有2及分类
		 * 3.把List<Category>转换成json,输出给客户端		
		 */
		String pid = req.getParameter("pid");
		List<Category> children = categoryService.findChild(pid);
		String json = toJson(children);
		resp.getWriter().print(json);
		
		return null;
	}
	
	//{"cid":"fdsafdsa","cname":"fdsafdsa"}
	private String toJson(Category category) {
		StringBuilder sb = new StringBuilder("{");
		sb.append("\"cid\"").append(":").append("\"").append(category.getCid()).append("\"");
		sb.append(",");
		sb.append("\"cname\"").append(":").append("\"").append(category.getCname()).append("\"");
		sb.append("}");
		return sb.toString();
	}
	
	//[{"cid":"fdsafdsa","cname":"fdsafdsa"},{"cid":"fdsafdsa","cname":"fdsafdsa"}]
	private String toJson(List<Category> categoryList) {
		StringBuilder sb = new StringBuilder("[");
		for(int i = 0; i < categoryList.size(); i++) {
			sb.append(toJson(categoryList.get(i)));
			if(i < categoryList.size()-1) {
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}
	
	/*
	 * *显示所有分类
	 */
	public String findCategoryAll(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1.通过service得到所有分类
		 * 2.保存到request中，转发到left.jsp
		 */
		List<Category> parents=categoryService.findAll();
		req.setAttribute("parents", parents);
		return "f:/adminjsps/admin/book/left.jsp";
	}
	
	
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
	 ** 按分类查
	 */
	public String findByCategory(HttpServletRequest req, HttpServletResponse resp) 
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
		 * 3.获取查询条件，本方法就是cid，既分类的id
		 */
		String cid = req.getParameter("cid");
		/*
		 * 4.使用pc和cid调用service#findByCategory得到PageBean
		 */
		PageBean<Book> pb = bookService.findByCategory(cid, pc);
		/*
		 * 给PageBean设置url，保存PageBean，转发到/jsps/book/list.jsp
		 */
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/adminjsps/admin/book/list.jsp";
	}
	
	/*
	 * *按作者查
	 */
	public String findByAuthor(HttpServletRequest req, HttpServletResponse resp) 
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
		 * 3.获取查询条件
		 */
		String author = req.getParameter("author");
		/*
		 * 4.使用pc和cid调用service#findByCategory得到PageBean
		 */
		PageBean<Book> pb = bookService.findByAuthor(author, pc);
		/*
		 * 给PageBean设置url，保存PageBean，转发到/jsps/book/list.jsp
		 */
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/adminjsps/admin/book/list.jsp";
	}
	
	/*
	 * *按出版社查
	 */
	public String findByPress(HttpServletRequest req, HttpServletResponse resp) 
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
		 * 3.获取查询条件
		 */
		String press = req.getParameter("press");
		/*
		 * 4.使用pc和cid调用service#findByCategory得到PageBean
		 */
		PageBean<Book> pb = bookService.findByPress(press, pc);
		/*
		 * 给PageBean设置url，保存PageBean，转发到/jsps/book/list.jsp
		 */
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/adminjsps/admin/book/list.jsp";
	}
	
	/*
	 * *按书名查
	 */
	public String findByBname(HttpServletRequest req, HttpServletResponse resp) 
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
		 * 3.获取查询条件
		 */
		String bname = req.getParameter("bname");
		/*
		 * 4.使用pc和cid调用service#findByCategory得到PageBean
		 */
		PageBean<Book> pb = bookService.findByBname(bname, pc);
		/*
		 * 给PageBean设置url，保存PageBean，转发到/jsps/book/list.jsp
		 */
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/adminjsps/admin/book/list.jsp";
	}
	
	
	/*
	 * *多条件组合查
	 */
	public String findByCombination(HttpServletRequest req, HttpServletResponse resp) 
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
		 * 3.获取查询条件
		 */
		Book criteria = CommonUtils.toBean(req.getParameterMap(), Book.class);
		/*
		 * 4.使用pc和cid调用service#findByCategory得到PageBean
		 */
		PageBean<Book> pb = bookService.findByCombination(criteria, pc);
		/*
		 * 给PageBean设置url，保存PageBean，转发到/jsps/book/list.jsp
		 */
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/adminjsps/admin/book/list.jsp";
	}
}
