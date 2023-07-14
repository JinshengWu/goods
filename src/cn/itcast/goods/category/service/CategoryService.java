package cn.itcast.goods.category.service;

import java.sql.SQLException;
import java.util.List;

import cn.itcast.goods.category.dao.CategoryDao;
import cn.itcast.goods.category.domain.Category;
/*
 * *����ģ��ҵ���
 */
public class CategoryService {
	private CategoryDao categoryDao=new CategoryDao();
	
	/*
	 * *��ѯָ�����������ӷ���ĸ���
	 */
	public int findChildrenCountByParent(String pid) {
		try {
			return categoryDao.findChildrenCountByParent(pid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	 ** ɾ������
	 */
	public void delete(String cid) {
		try {
			categoryDao.delete(cid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	 ** �޸ķ���
	 */
	public void edit(Category category) {
		try {
			categoryDao.edit(category);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * *���ط���
	 */
	public Category load(String cid) {
		try {
			return categoryDao.load(cid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	 ** ��ӷ���
	 */
	public void add(Category category) {
		try {
			categoryDao.add(category);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * *��ѯ���з���
	 */
	public List<Category> findAll(){
		try {
			return categoryDao.findAll();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	 ** ��ȡ���и����࣬�����ӷ���
	 */
	public List<Category> findParents(){
		try {
			return categoryDao.findParents();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	/*
	 * *��ѯָ���������������ӷ���
	 */
	public List<Category> findChild(String pid){
		try {
			return categoryDao.findByParent(pid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
