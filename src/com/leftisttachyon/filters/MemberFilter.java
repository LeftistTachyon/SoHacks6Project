package com.leftisttachyon.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet Filter implementation class Filter_
 */
@WebFilter("/member")
public class MemberFilter implements Filter {

    /**
     * Default constructor. 
     */
    public MemberFilter() {
        // do nothing
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// do nothing...?
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		if (req instanceof HttpServletRequest && res instanceof HttpServletResponse) {
			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) res;
			
			if(request.getSession().getAttribute("username") == null) {
				StringBuffer buffer = request.getRequestURL();
				String queryString = request.getQueryString();
				if(queryString != null) {
					buffer.append("?");
					buffer.append(queryString);
				}
				request.getSession().setAttribute("redirectTo", buffer.toString());
				response.sendRedirect(request.getContextPath() + "/site?page=login");
			} else {
				// pass the request along the filter chain
				chain.doFilter(request, response);			
			}
		} else {
			// pass the request along the filter chain
			chain.doFilter(req, res);	
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// do nothing
	}

}
