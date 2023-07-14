package cn.itcast.goods.user.service;
/*
 * �û�ģ��ҵ���
 */

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.user.dao.UserDao;
import cn.itcast.goods.user.domain.User;
import cn.itcast.goods.user.service.exception.UserException;
import cn.itcast.mail.Mail;
import cn.itcast.mail.MailUtils;

public class UserService {
	private UserDao userDao=new UserDao();
	
	public void updatePassword(String uid,String newPass,String oldPass) throws UserException {
		try {
			/*
			 * У��������
			 */
			boolean bool=userDao.findByUidAndPassword(uid, oldPass);
			if(!bool) {//���������
				throw new UserException("���������");
			}
			
			/*
			 * �޸�����
			 */
			userDao.updatePassword(uid, newPass);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * ��¼����
	 */
	public User login(User user) {
		try {
			return userDao.findByLoginnameAndLoginpass(user.getLoginname(), user.getLoginpass());
		} catch (SQLException e) {
			throw new RuntimeException();
		}
	}
	
	/*
	 * �����
	 */
	public void activation(String code) throws UserException {
		/*
		 * 1.ͨ���������ѯ�û�
		 * 2.���UserΪnull,˵������Ч�����룬�׳��쳣�������쳣��Ϣ����Ч�����룩
		 * 3.�鿴�û�״̬�Ƿ�Ϊtrue,���Ϊtrue,�׳��쳣�������쳣��Ϣ���벻Ҫ���μ��
		 * 4.�޸��û�״̬Ϊtrue
		 */
		try {
			User user=userDao.findByCode(code);
			if(user==null) throw new UserException("��Ч�ļ����룡");
			if(user.isStatus()) throw new UserException("���Ѿ�������ˣ��벻Ҫ���μ���");
			userDao.updateStatus(user.getUid(), true);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * �û���У��
	 */
	public boolean ajaxValidateLoginname(String loginname){
		try {
			return userDao.ajaxValidateLoginname(loginname);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * EmailУ��
	 */
	public boolean ajaxValidateEmail(String email){
		try {
			return userDao.ajaxValidateEmail(email);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * ע�Ṧ��
	 */
	public void regist(User user) {
		/*
		 * ���ݲ�ȫ
		 */
		user.setUid(CommonUtils.uuid());
		user.setStatus(false);
		user.setActivationCode(CommonUtils.uuid()+CommonUtils.uuid());
		/*
		 * 2.�����ݿ����
		 */
		try {
			userDao.add(user);
		} catch (SQLException e) {
			throw new RuntimeException();
		}
		
		/*
		 * 3.���ʼ�
		 */
		
		/*
		 * �������ļ����ݼ��ص�prop��
		 */
		Properties prop=new Properties();
		try {
			prop.load(this.getClass().getClassLoader().getResourceAsStream("email_template.properties"));
		} catch (IOException e1) {
			throw new RuntimeException();
		}
		/*
		 * ��¼�������õ�session
		 */
		String host=prop.getProperty("host");//������������
		String name=prop.getProperty("username");//��¼��
		String pass=prop.getProperty("password");//��¼����
		Session session=MailUtils.createSession(host, name, pass);
		
		/*
		 * ����Mail����
		 */
		String from=prop.getProperty("from");
		String to=user.getEmail();
		String subject=prop.getProperty("subject");
		//MessageFormat.format������ѵ�һ�������е�{0}��ʹ�õڶ��������滻
		//��MessageFormat.format("���{0},��{1}!","����","ȥ����")������"�����������ȥ����!"
		String content=MessageFormat.format(prop.getProperty("content"), user.getActivationCode());
		Mail mail=new Mail(from,to,subject,content);
		/*
		 * ���ʼ�
		 */
		try {
			MailUtils.send(session, mail);
		} catch (MessagingException e) {
			throw new RuntimeException();
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}
}
