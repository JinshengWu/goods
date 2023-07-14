package cn.itcast.goods.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;


@WebFilter("/*")
public class CharSetFilter implements Filter {
	/*
	 * 
	 * ����ȫվ��������
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");	
		//tomcat7����뷽ʽΪutf-8����Ҫ��дhttpServletRequest
		chain.doFilter(request, response);
	}
}
