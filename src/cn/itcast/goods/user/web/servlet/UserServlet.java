package cn.itcast.goods.user.web.servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.user.domain.User;
import cn.itcast.goods.user.service.UserService;
import cn.itcast.goods.user.service.exception.UserException;
import cn.itcast.servlet.BaseServlet;

/**
 * Servlet implementation class UserServlet
 */
@WebServlet("/UserServlet")
public class UserServlet extends BaseServlet {
	private UserService userService = new UserService();

	/*
	 * ajax�û����Ƿ�ע��У��
	 */
	public String ajaxValidateLoginname(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * ��ȡ�û���
		 */
		String loginname = req.getParameter("loginname");
		/*
		 * ͨ��service�õ�У����
		 */
		boolean b = userService.ajaxValidateLoginname(loginname);
		/*
		 * �����ͻ���
		 */
		resp.getWriter().print(b);
		return null;
	}

	/*
	 * ajax Email�Ƿ�ע��У��
	 */
	public String ajaxValidateEmail(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * ��ȡ��email
		 */
		String email = req.getParameter("email");
		/*
		 * ͨ��service�õ�У����
		 */
		boolean b = userService.ajaxValidateEmail(email);
		/*
		 * �����ͻ���
		 */
		resp.getWriter().print(b);
		return null;
	}

	/*
	 * ��֤���Ƿ���ȷ
	 */
	public String ajaxValidateVerifyCode(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		/*
		 * 1.��ȡ�������֤��
		 */
		String VerifyCode = req.getParameter("verifyCode");
		/*
		 * 2.��ȡͼƬ����ʵ��֤��
		 */
		String vcode = (String) req.getSession().getAttribute("vCode");
		/*
		 * ���к��Դ�Сд�Ƚϣ��õ����
		 */
		boolean b = VerifyCode.equalsIgnoreCase(vcode);
		/*
		 * ���͸��ͻ���
		 */
		resp.getWriter().print(b);
		return null;
	}

	/*
	 * ע�Ṧ��
	 */
	public String regist(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		/*
		 * 1. ��װ�����ݵ�User������
		 */
		User formUser = CommonUtils.toBean(req.getParameterMap(), User.class);

		/*
		 * 2. �Ա����ݽ��з�������У��
		 */
		Map<String, String> errors = validateRegist(formUser, req.getSession());
		if (errors.size() > 0) {
			req.setAttribute("form", formUser);
			req.setAttribute("errors", errors);
			return "f:/jsps/user/regist.jsp";
		}
		/*
		 * 3. ����userService���ע��
		 */
		userService.regist(formUser);

		/*
		 * 4. ����ע��ɹ���Ϣ��ת����msg.jsp��ʾ
		 */
		req.setAttribute("code", "success");// �ǳɹ���Ϣ���Ǵ�����Ϣ
		req.setAttribute("msg", "��ϲ��ע��ɹ��������ϵ�������ɼ��");
		return "f:/jsps/msg.jsp";
	}

	/*
	 * ע��У�� �Ա����ֶν������У�飬����д���ʹ�õ�ǰ�ֶ�����Ϊkey,������ϢΪvalue,���浽map�� ����map
	 */
	private Map<String, String> validateRegist(User formUser, HttpSession session) {
		Map<String, String> errors = new HashMap<String, String>();
		/*
		 * У���¼��
		 */
		// 1.��loginname����У��
		String loginname = formUser.getLoginname();
		if (loginname == null || loginname.isEmpty()) {
			errors.put("loginname", "�û�������Ϊ�գ�");
		} else if (loginname.length() < 3 || loginname.length() > 20) {
			errors.put("loginname", "�û������ȱ�����3~20֮�䣡");
		} else if (!userService.ajaxValidateLoginname(loginname)) {
			errors.put("loginname", "�û����ѱ�ע�����");
		}

		// 2. ��loginpass����У��
		String loginpass = formUser.getLoginpass();
		if (loginpass == null || loginpass.isEmpty()) {
			errors.put("loginpass", "���벻��Ϊ�գ�");
		} else if (loginpass.length() < 3 || loginpass.length() > 20) {
			errors.put("loginpass", "���볤�ȱ�����3~20֮�䣡");
		}

		// 3.��ȷ���������У��
		String reloginpass = formUser.getReloginpass();
		if (reloginpass == null || reloginpass.isEmpty()) {
			errors.put("reloginpass", "ȷ�����벻��Ϊ�գ�");
		} else if (!reloginpass.equalsIgnoreCase(loginpass)) {
			errors.put("reloginpass", "�����������벻һ�£�");
		}

		// 4.��Email����У��
		String email = formUser.getEmail();
		if (email == null || email.isEmpty()) {
			errors.put("email", "Email����Ϊ�գ�");
		} else if (!email.matches("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$")) {
			errors.put("email", "�����Email��ʽ��");
		} else if (!userService.ajaxValidateEmail(email)) {
			errors.put("email", "Email�ѱ�ע�����");
		}

		// 5. ����֤�����У��
		String verifyCode = formUser.getVerifyCode();
		String vCode = (String) session.getAttribute("vCode");
		if (verifyCode == null || verifyCode.isEmpty()) {
			errors.put("verifyCode", "��֤�벻��Ϊ�գ�");
		} else if (verifyCode.length() != 4) {
			errors.put("verifyCode", "��֤�����");
		} else if (!verifyCode.equalsIgnoreCase(vCode)) {
			errors.put("verifyCode", "��֤�����");
		}
		return errors;
	}

	/*
	 * �����
	 */
	public String activation(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		/*
		 * 1.��ȡ���������� 2.�ü��������service������ɼ���
		 * >service���������׳��쳣�����쳣��Ϣ���������浽request�У�ת����msg.jsp��ʾ
		 * 3.����ɹ���Ϣ��request�У�ת����msg.jsp��ʾ
		 */
		String code = req.getParameter("activationCode");
		try {
			userService.activation(code);
			req.setAttribute("code", "success");// ֪ͨmsg.jsp��ʾ�Ժ�
			req.setAttribute("msg", "��ϲ������ɹ��������ϵ�¼��");
		} catch (UserException e) {
			// ˵���׳����쳣
			req.setAttribute("msg", e.getMessage());
			req.setAttribute("code", "error");// ֪ͨmsg.jsp��ʾx
		}
		return "f:/jsps/msg.jsp";
	}

	/*
	 * �޸�����
	 */
	public String updatePassword(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1.��װ�����ݵ�user�� 
		 * 2.��session�л�ȡuid 
		 * 3.ʹ��uid�ͱ��е�oldpass��newpass������service����
		 * >��������쳣�������쳣��Ϣ��request�У�ת����pwd.jsp 4.����ɹ���Ϣ��request�� 5.ת����msg.jsp
		 */
		User formUser = CommonUtils.toBean(req.getParameterMap(), User.class);
		User user = (User) req.getSession().getAttribute("sessionUser");
		// ����û�û�е�¼�����ص���½ҳ�棬��ʾ������Ϣ
		if (user == null) {
			req.setAttribute("msg", "����û�е�¼��");
			return "f:/jsps/user/login.jsp";
		}

		try {
			userService.updatePassword(user.getUid(), formUser.getNewpass(), formUser.getLoginpass());
			req.setAttribute("msg", "�޸�����ɹ�");
			req.setAttribute("code", "success");
			return "f:/jsps/msg.jsp";
		} catch (UserException e) {
			req.setAttribute("msg", e.getMessage());
			req.setAttribute("user", formUser);
			return "f:/jsps/user/pwd.jsp";
		}
	}

	/*
	 * �˳�����
	 */
	public String quit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.getSession().invalidate();
		return "r:/jsps/user/login.jsp";
	}

	/*
	 * ��¼����
	 */
	public String login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		/*
		 * 1.��װ�����ݵ�User 
		 * 2.У������� 
		 * 3.ʹ��service��ѯ���õ�User 
		 * 4.�鿴�û��Ƿ���ڣ���������ڣ�
		 * *���������Ϣ���û������������ *�����û����ݣ�Ϊ�˻��� *ת����login.jsp 
		 * 5.������ڣ��鿴״̬�����״̬Ϊfalse��
		 * *���������Ϣ����û�м���
		 *  *��������ݣ�Ϊ�˻��� *ת����login.jsp 
		 * 6.��½�ɹ� *���浱ǰ��ѯ����user��session��
		 * *���浱ǰ���û���cookie�У�ע��������Ҫ���봦��
		 */
		/*
		 * 1.��װ�����ݵ�user
		 */
		User formUser = CommonUtils.toBean(req.getParameterMap(), User.class);
		/*
		 * 2.У��
		 */
		Map<String, String> errors = validateLogin(formUser, req.getSession());
		if (errors.size() > 0) {
			req.setAttribute("form", formUser);
			req.setAttribute("errors", errors);
			return "f:/jsps/user/login.jsp";
		}
		/*
		 * 3.����userService#login()����
		 */
		User user = userService.login(formUser);
		/*
		 * 4.��ʼ�ж�
		 */
		if (user == null) {
			req.setAttribute("msg", "�û������������");
			req.setAttribute("user", formUser);
			return "f:/jsps/user/login.jsp";
		} else {
			if (!user.isStatus()) {
				req.setAttribute("msg", "�㻹û���");
				req.setAttribute("user", formUser);
				return "f:/jsps/user/login.jsp";
			} else {
				// �����û���session
				req.getSession().setAttribute("sessionUser", user);
				// ��ȡ�û������浽cookie��
				String loginname = user.getLoginname();
				loginname = URLEncoder.encode(loginname, "utf-8");
				Cookie cookie = new Cookie("loginname", loginname);
				cookie.setMaxAge(60 * 60 * 24 * 10);// ����10��
				resp.addCookie(cookie);
				return "r:/index.jsp";// �ض�����ҳ
			}
		}
	}

	/*
	 * ��¼У�鷽��,���ڿ����Լ��Ӷ���
	 */
	private Map<String, String> validateLogin(User formUser, HttpSession session) {
		Map<String, String> errors = new HashMap<String, String>();

		return errors;
	}

}
