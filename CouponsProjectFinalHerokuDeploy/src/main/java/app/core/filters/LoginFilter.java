package app.core.filters;

import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;

import app.core.entities.Company;
import app.core.entities.Customer;
import app.core.exceptions.CouponsSystemException;
import app.core.login.ClientType;
import app.core.login.LoginManager;
import app.core.managers.JwtUtil;
import app.core.services.ClientService;
import app.core.services.UserDetailLoadServer;

public class LoginFilter implements Filter {

	private UserDetailLoadServer userDetailLoadServer;
	private JwtUtil tokenUtil;
	private LoginManager loginManager;

	public LoginFilter(UserDetailLoadServer userDetailLoadServer, JwtUtil tokenUtil, LoginManager loginManager) {
		super();
		this.userDetailLoadServer = userDetailLoadServer;
		this.tokenUtil = tokenUtil;
		this.loginManager = loginManager;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		String requestTokenHeader = req.getHeader("token");
		String url = req.getRequestURI();
		System.out.println("Hello from filter url: " + requestTokenHeader);
		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			String jwtToken = requestTokenHeader.substring(7);
			System.out.println("Hello from Token: " + jwtToken);
			
			try {
				ClientType clientType = tokenUtil.extractUserType(jwtToken);
				String clientEmail = tokenUtil.extractUserEmail(jwtToken);
				System.out.println(jwtToken);
				switch (clientType) {
				case ADMINISTRATOR:
					if (url.contains("/admin")) {
						if (tokenUtil.validateToken(jwtToken, userDetailLoadServer.getAdminEmail())) {
							ClientService clientService = loginManager.login(userDetailLoadServer.getAdminEmail(),
									userDetailLoadServer.getAdminPassword(), ClientType.ADMINISTRATOR);
							req.setAttribute("service", clientService);
							chain.doFilter(request, response);
						} else {
							resp.sendError(HttpStatus.UNAUTHORIZED.value(), "Unvalid JwtToken");
						}
					} else {
						resp.sendError(HttpStatus.UNAUTHORIZED.value(), "Method Not Allowed !");
					}
					break;
				case COMPANY:
					if (url.contains("/company")) {
						Company company = userDetailLoadServer.getCompanyDetailsByEmail(clientEmail);
						if (tokenUtil.validateToken(jwtToken, company.getEmail())) {
								if (company.getPasswordTimeStamp() == null || tokenUtil.extractIssued(jwtToken).after(new Date(company.getPasswordTimeStamp()))) {
									ClientService clientService = loginManager.login(clientEmail,
											company.getPassword(), ClientType.COMPANY);
									req.setAttribute("service", clientService);
									chain.doFilter(request, response);
								} else {
									resp.sendError(HttpStatus.UNAUTHORIZED.value(), "Paswword Changed !");
								}

						} else {
							resp.sendError(HttpStatus.UNAUTHORIZED.value(), "Unvalid JwtToken !");
						}
					} else
						resp.sendError(HttpStatus.UNAUTHORIZED.value(), "Method Not Allowed !");
					break;
				case CUSTOMER:
					if (url.contains("/customer")) {
						Customer customer = userDetailLoadServer.getCustomerDetailsByEmail(clientEmail);
						if (tokenUtil.validateToken(jwtToken, customer.getEmail())) {
							if (customer.getPasswordTimeStamp() == null || tokenUtil.extractIssued(jwtToken).after(new Date(customer.getPasswordTimeStamp())) ) {
								ClientService clientService = loginManager.login(clientEmail,
										customer.getPassword(), ClientType.CUSTOMER);
								req.setAttribute("service", clientService);
								chain.doFilter(request, response);
							} else {
								resp.sendError(HttpStatus.UNAUTHORIZED.value(), "Paswword Changed !");
							}

						} else {
							resp.sendError(HttpStatus.UNAUTHORIZED.value(), "Unvalid JwtToken !");
						}
					}
					else {
						resp.sendError(HttpStatus.UNAUTHORIZED.value(), "Method Not Allowed !");
					}
					break;
				}

			} catch (CouponsSystemException e) {
				resp.sendError(HttpStatus.UNAUTHORIZED.value(), "Unvalid Credentials");
			} 
		} else {
			resp.sendError(HttpStatus.UNAUTHORIZED.value(), "JWT Token does not begin with Bearer String");
		}

	}

}
