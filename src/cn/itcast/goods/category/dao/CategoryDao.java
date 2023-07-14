package cn.itcast.goods.category.dao;
/*
 * ����־ò�
 */

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.category.domain.Category;
import cn.itcast.jdbc.TxQueryRunner;

public class CategoryDao {
	private QueryRunner qr=new TxQueryRunner();
	
	/*
	 * ��һ��map�е�����ӳ�䵽Category��
	 */
	private Category toCategory(Map<String,Object> map) {
		/*
		 * map{cid:xx, cname:xx, pid:xx, desc:xx, orderBy:xx}
		 * Category{cid:xx, cname:xx, parent:(cid=pid), desc:xx}
		 */
		Category category=CommonUtils.toBean(map, Category.class);
		String pid=(String) map.get("pid");
		if(pid != null) {//�������ID��Ϊ��
			/*
			 * ʹ��һ�������������װ��pid
			 * �ٰѸ��������ø�Category
			 */
			Category parent=new Category();
			parent.setCid(pid);
			category.setParent(parent);
		}
		return category;
	}
	
	/*
	 * ���԰Ѷ��map(List<Map>)ӳ��ɶ��Category(List<Category>)
	 */
	private List<Category> toCategoryList(List<Map<String,Object>> mapList){
		List<Category> categoryList=new ArrayList<Category>();
		for(Map<String,Object> map:mapList) {
			Category c=toCategory(map);
			categoryList.add(c);
		}
		return categoryList;
	}
	
	/*
	 * �������з���
	 */
	public List<Category> findAll() throws SQLException{
		/*
		 * 1.��ѯ������һ������
		 */
		String sql="select * from t_category where pid is null order by orderBy";
		List<Map<String,Object>> mapList=qr.query(sql, new MapListHandler());
		
		List<Category> parents=toCategoryList(mapList);
		
		/*
		 *2.ѭ���������е�һ�����࣬Ϊÿһ��һ������������Ķ������� 
		 */
		for(Category parent:parents) {
			//��ѯ����ǰ������������ӷ���
			List<Category> children=findByParent(parent.getCid());
			//���ø�������
			parent.setChildren(children);
		}
		return parents;
	}
	
	/*
	 * ͨ���������ѯ�ӷ���
	 */
	public List<Category> findByParent(String pid) throws SQLException{
		String sql="select * from t_category where pid=? order by orderBy";
		List<Map<String,Object>> mapList=qr.query(sql, new MapListHandler(),pid);
		return toCategoryList(mapList);
	}
	
	/*
	 * *��ӷ���
	 */
	public void add(Category category) throws SQLException {
		String sql = "insert into t_category(cid,cname,pid,`desc`) values(?,?,?,?)";
		/*
		 * ��Ϊһ�����࣬û��parent����������
		 * �������������Ҫ�������η��࣬������Ҫ�ж�
		 */
		String pid = null;//һ������
		if(category.getParent() != null) {
			pid = category.getParent().getCid();
		}
		Object[] params = {category.getCid(), category.getCname(), pid, category.getDesc()};
		qr.update(sql,params);
	}
	
	/*
	 * *��ȡ���и����࣬�����ӷ���
	 */
	public List<Category> findParents() throws SQLException{
		/*
		 * 1.��ѯ������һ������
		 */
		String sql="select * from t_category where pid is null order by orderBy";
		List<Map<String,Object>> mapList=qr.query(sql, new MapListHandler());
		
		return toCategoryList(mapList);
	}
	
	
	/*
	 * *���ط���
	 * ���ɼ���һ�����࣬Ҳ�ɼ��ض�������
	 */
	public Category load(String cid) throws SQLException {
		String sql = "select * from t_category where cid=?";
		return toCategory(qr.query(sql, new MapHandler(),cid));
	}
	
	
	/*
	 * �޸ķ���
	 * �����޸�һ�����࣬Ҳ���޸Ķ�������
	 */
	public void edit(Category category) throws SQLException {
		String sql = "update t_category set cname=?, pid=?, `desc`=? where cid=?";
		String pid = null;
		if(category.getParent() !=null) {
			pid = category.getParent().getCid();
		}
		Object[] params = {category.getCname(), pid, category.getDesc(), category.getCid()};
		qr.update(sql,params);
	}
	
	
	/*
	 * ��ѯָ�����������ӷ���ĸ���
	 */
	public int findChildrenCountByParent(String pid) throws SQLException {
		String sql = "select count(*) from t_category where pid=?";
		Number cnt = (Number)qr.query(sql, new ScalarHandler(), pid);
		return cnt == null ? 0 : cnt.intValue();
	}
	
	
	/*
	 * ɾ������
	 */
	public void delete(String cid) throws SQLException {
		String sql = "delete from t_category where cid=?";
		qr.update(sql,cid);
	}
}
