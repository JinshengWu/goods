package cn.itcast.goods.book.service;

import java.sql.SQLException;

import cn.itcast.goods.book.dao.BookDao;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.pager.PageBean;

public class BookService {
	private BookDao bookDao=new BookDao();
	
	/*
	 * *删除图书
	 */
	public void delete(String bid) {
		try {
			bookDao.delete(bid);;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * *修改图书
	 */
	public void edit(Book book) {
		try {
			bookDao.edit(book);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	/*
	 * *返回当前分类下图书个数
	 */
	public int findBookCountByCategory(String cid) {
		try {
			return bookDao.findBookCountByCategory(cid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * *加载图书
	 */
	public Book load(String bid) {
		try {
			return bookDao.findByBid(bid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * *按分类查
	 */
	public PageBean<Book> findByCategory(String cid,int pc){
		try {
			return bookDao.findByCategory(cid, pc);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * *按书名模糊查
	 */
	public PageBean<Book> findByBname(String bname,int pc){
		try {
			return bookDao.findByBname(bname, pc);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * *按作者模糊查
	 */
	public PageBean<Book> findByAuthor(String author,int pc){
		try {
			return bookDao.findByAuthor(author, pc);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * *按出版社查
	 */
	public PageBean<Book> findByPress(String press,int pc){
		try {
			return bookDao.findByPress(press, pc);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * *多条件组合查
	 */
	public PageBean<Book> findByCombination(Book criteria,int pc){
		try {
			return bookDao.findByCombination(criteria, pc);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	
	/*
	 ** 添加图书
	 */
	public void add(Book book) {
		try {
			bookDao.add(book);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}

