package cn.itcast.goods.admin.category.web.servlet;


import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.service.BookService;
import cn.itcast.goods.category.domain.Category;
import cn.itcast.goods.category.service.CategoryService;
import cn.itcast.servlet.BaseServlet;

/**
 * Servlet implementation class AdminCategoryServlet
 */
@WebServlet("/admin/AdminCategoryServlet")
public class AdminCategoryServlet extends BaseServlet{
	private CategoryService categoryService = new CategoryService();
	private BookService bookService = new BookService();
	
	
	/*
	 * *��ѯ���з���
	 */
	public String findAll(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		req.setAttribute("parents", categoryService.findAll());
		return "f:/adminjsps/admin/category/list.jsp";
	}
	
	/*
	 * *���һ������
	 */
	public String addParent(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1.��װ�����ݵ�category��
		 * 2.����service��add����������
		 * 3.����findAll(),����list.jsp��ʾ���з���
		 */
		Category parent = CommonUtils.toBean(req.getParameterMap(), Category.class);
		parent.setCid(CommonUtils.uuid());//����cid
		categoryService.add(parent);
		return findAll(req, resp);
	}
	
	
	public String addChild(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1.��װ�����ݵ�category��
		 * 2.��Ҫ�ֶ��İѱ��е�pidӳ�䵽child������
		 * 3.����service��add����������
		 * 4.����findAll(),����list.jsp��ʾ���з���
		 */
		Category child = CommonUtils.toBean(req.getParameterMap(), Category.class);
		child.setCid(CommonUtils.uuid());//����cid
		
		//�ֶ�ӳ��pid
		String pid = req.getParameter("pid");
		Category parent = new Category();
		parent.setCid(pid);
		child.setParent(parent);
		
		categoryService.add(child);
		return findAll(req, resp);
	}
	
	/*
	 ** ��Ӷ��������һ��
	 */
	public String addChildPre(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		String pid = req.getParameter("pid");// ��ǰ����ĸ���id\
		List<Category> parents = categoryService.findParents();
		req.setAttribute("pid", pid);
		req.setAttribute("parents", parents);
		
		return "f:/adminjsps/admin/category/add2.jsp";
	}
	
	/*
	 * *�޸�һ�������һ��
	 */
	public String editParentPre(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1.��ȡ�����е�cid
		 * 2.ʹ��cid����Category
		 * 3.����Category
		 * 4.ת����edit.jspҳ����ʾCategory
		 */
		String cid = req.getParameter("cid");
		Category parent = categoryService.load(cid);
		req.setAttribute("parent", parent);
		return "f:/adminjsps/admin/category/edit.jsp";
	}
	
	
	/*
	 * *�޸�һ������ڶ���
	 */
	public String editParent(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1.��װ�����ݵ�Category
		 * 2.����service��������޸�
		 * 3.ת����list.jsp��ʾ���з���(retrun findAll())	
		 */
		Category parent = CommonUtils.toBean(req.getParameterMap(), Category.class);
		categoryService.edit(parent);
		return findAll(req, resp);
	}
	
	
	/*
	 * *�޸Ķ��������һ��
	 */
	public String editChildPre(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1.��ȡ���Ӳ���cid,ͨ��cid����Category������֮
		 * 2.��ѯ������1�����࣬����֮
		 * 3.ת����edit2.jsp
		 */
		String cid = req.getParameter("cid");
		Category child = categoryService.load(cid);
		req.setAttribute("child", child);
		req.setAttribute("parents", categoryService.findParents());
		
		return "f:/adminjsps/admin/category/edit2.jsp";
	}
	
	/*
	 * *�޸Ķ�������ڶ���
	 */
	public String editChild(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1.��װ��������Category child
		 * 2.�ѱ��е�pid��װ��child
		 * 3.����service.edit()����޸�
		 * 4.���ص�list.jsp
		 */
		Category child = CommonUtils.toBean(req.getParameterMap(), Category.class);
		String pid = req.getParameter("pid");
		Category parent = new Category();
		parent.setCid(pid);
		child.setParent(parent);
		
		categoryService.edit(child);
		return findAll(req, resp);
	}
	
	/*
	 ** ɾ��һ������
	 */
	public String deleteParent(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1.��ȡ���Ӳ���cid������һ��һ�������id
		 * 2.ͨ��cid���鿴�ø��������ӷ���ĸ���
		 * 3.�������0��˵�������ӷ��࣬����ɾ�������������Ϣ��ת����msg.jsp
		 * 4.�������0��ɾ��֮������list.jsp
		 */
		String cid = req.getParameter("cid");
		int cnt = categoryService.findChildrenCountByParent(cid);
		if(cnt > 0) {
			req.setAttribute("msg", "�÷����»����ӷ��࣬����ɾ����");
			return "f:/adminjsps/msg.jsp";
		}else {
			categoryService.delete(cid);
			return findAll(req, resp);
		}
	}
	
	/*
	 * *ɾ����������
	 */
	public String deleteChild(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1.��ȡcid����2������id
		 * 2.��ȥ�÷����µ�ͼ�����
		 * 3.�������0�����������Ϣ��ת����msg.jsp
		 * 4.�������0��ɾ��֮�����ص�list.jsp
		 */
		String cid = req.getParameter("cid");
		int cnt = bookService.findBookCountByCategory(cid);
		if(cnt > 0) {
			req.setAttribute("msg", "�÷����»�����ͼ�飬����ɾ����");
			return "f:/adminjsps/msg.jsp";
		}else {
			categoryService.delete(cid);
			return findAll(req, resp);
		}
	}
}
