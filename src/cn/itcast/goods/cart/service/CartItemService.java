package cn.itcast.goods.cart.service;

import java.sql.SQLException;
import java.util.List;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.cart.dao.CartItemDao;
import cn.itcast.goods.cart.domain.CartItem;

public class CartItemService {
	private CartItemDao cartItemDao=new CartItemDao();
	
	/*
	 * ���ض��CartItem
	 */
	public List<CartItem> loadCartItems(String cartItemIds){
		try {
			return cartItemDao.loadCartItems(cartItemIds);
		} catch (SQLException e) {
			throw new RuntimeException();
		}
	}
	
	
	/*
	 * �޸Ĺ��ﳵ��Ŀ����
	 */
	public CartItem updateQuantity(String cartItemId, int quantity) {
		try {
			cartItemDao.updateQuantity(cartItemId, quantity);
			return cartItemDao.findByCartItemId(cartItemId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * *����ɾ��
	 */
	public void batchDelete(String cartItemIds) {
		try {
			cartItemDao.batchDelete(cartItemIds);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	/*
	 * *�����Ŀ
	 */
	public void add(CartItem cartItem) {
		try {
			/*
			 * 1.ʹ��uid��bidȥ���ݿ��в�ѯ�����Ŀ�Ƿ����
			 */
			CartItem _cartItem = cartItemDao.findByUidAndBid(
					cartItem.getUser().getUid(),cartItem.getBook().getBid());
			if(_cartItem == null) {//���ԭ��û�������Ŀ����ô��������Ŀ
				cartItem.setCartItemId(CommonUtils.uuid());
				cartItemDao.addCartItem(cartItem);
			}else {//���ԭ���������Ŀ���޸�����
				//ʹ��ԭ�е�����������Ŀ������֮�ͣ�����Ϊ�µ�����
				int quantity = cartItem.getQuantity() + _cartItem.getQuantity();
				//�޸��������Ŀ������
				cartItemDao.updateQuantity(_cartItem.getCartItemId(), quantity);
			}
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	
	/*
	 ** �ҵĹ��ﳵ����
	 */
	public List<CartItem> myCart(String uid){
		try {
			return cartItemDao.findByUser(uid);
		} catch (SQLException e) {
			throw new RuntimeException();
		}
	}
}
