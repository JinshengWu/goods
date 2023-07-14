package cn.itcast.goods.cart.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.cart.domain.CartItem;
import cn.itcast.goods.user.domain.User;
import cn.itcast.jdbc.TxQueryRunner;

public class CartItemDao {
	private QueryRunner qr=new TxQueryRunner();
	
	/*
	 * ��������where�Ӿ�
	 */
	public String toWhereSql(int len) {
		StringBuilder sb = new StringBuilder("cartItemId in(");
		for(int i = 0; i < len; i++) {
			sb.append("?");
			if(i < len - 1) {
				sb.append(",");
			}
		}
		sb.append(")");
		return sb.toString();
	}
	
	/*
	 * ���ض��CartItem
	 */
	public List<CartItem> loadCartItems(String cartItemIds) throws SQLException{
		/*
		 * 1.��cartItemIdsת��������
		 */
		Object[] cartItemIdArray = cartItemIds.split(",");
		/*
		 * ����where�Ӿ�
		 */
		String whereSql = toWhereSql(cartItemIdArray.length);
		/*
		 * 3.����sql���
		 */
		String sql = "select * from t_cartitem c, t_book b where c.bid= b.bid and "+whereSql;
		/*
		 * 4.ִ��sql������List<CartItem>
		 */
		return toCartItemList(qr.query(sql, new MapListHandler(), cartItemIdArray));
	}
	
	
	/*
	 **��id��ѯ 
	 */
	public CartItem findByCartItemId(String cartItemId) throws SQLException {
		String sql = "select * from t_cartitem c, t_book b where c.bid=b.bid and c.cartItemId=?";
		Map<String,Object> map = qr.query(sql, new MapHandler(),cartItemId);
		return toCartItem(map);
	}
	
	/*
	 **����ɾ��
	 */
	public void batchDelete(String cartItemIds) throws SQLException {
		/*
		 * ��Ҫ�Ȱ�cartItemIdsת��������
		 * 1.��cartItemIdsת����where�Ӿ�
		 * 2.��delete from ������һ��Ȼ��ִ��
		 */
		Object[] cartItemIdArray = cartItemIds.split(",");
		String whereSql = toWhereSql(cartItemIdArray.length);
		String sql = "delete from t_cartitem where " + whereSql;
		qr.update(sql,cartItemIdArray);//����cartItemIdArray������Object��������
	}
	
	/*
	 * *��ѯĳ���û���ĳ��ͼ��Ĺ��ﳵ��Ŀ�Ƿ����
	 */
	public CartItem findByUidAndBid(String uid,String bid) throws SQLException {
		String sql = "select * from t_cartitem where uid=? and bid=?";
		Map<String, Object> map = qr.query(sql, new MapHandler(), uid, bid);
		CartItem cartItem = toCartItem(map);
		return cartItem;
	}
	
	/*
	 * *�޸�ָ����Ŀ������
	 */
	public void updateQuantity(String cartItemId, int quantity) throws SQLException {
		String sql = "update t_cartitem set quantity=? where cartItemId=?";
		qr.update(sql, quantity, cartItemId);
	}
	
	/*
	 * *������Ŀ
	 */
	public void addCartItem(CartItem cartItem) throws SQLException {
		String sql = "insert into t_cartitem(cartItemId, quantity, bid, uid)"+
				" values(?,?,?,?)";
		Object[] params = {cartItem.getCartItemId(), cartItem.getQuantity(), 
				cartItem.getBook().getBid(), cartItem.getUser().getUid()};
		qr.update(sql,params);
	}
	
	/*
	 * *��һ��mapӳ���һ��Cartitem
	 */
	private CartItem toCartItem(Map<String,Object> map) {
		if(map == null || map.size() == 0) return null;
		CartItem cartItem = CommonUtils.toBean(map, CartItem.class);
		User user = CommonUtils.toBean(map, User.class);
		Book book = CommonUtils.toBean(map, Book.class);
		cartItem.setBook(book);
		cartItem.setUser(user);
		return cartItem;
	}
	
	/*
	 * �Ѷ��Map(List<Map>)ӳ��ɶ��CartItem(List<CartItem>)
	 */
	private List<CartItem> toCartItemList(List<Map<String,Object>> mapList){
		List<CartItem> cartItemList = new ArrayList<CartItem>();
		for(Map<String,Object> map : mapList) {
			CartItem cartItem=toCartItem(map);
			cartItemList.add(cartItem);
		}
		return cartItemList;
	}
	
	/*
	 **ͨ���û���ѯ���ﳵ��Ŀ 
	 */
	public List<CartItem> findByUser(String uid) throws SQLException{
		String sql = "select * from t_cartitem c,t_book b where c.bid=b.bid and uid=? order by c.orderBy";
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler(), uid);
		return toCartItemList(mapList);
	}
}
