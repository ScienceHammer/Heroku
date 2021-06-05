package app.core.managers;

import app.core.login.ClientType;


public class UserDetails {


	public String email;
	public ClientType clientType;
	
	public UserDetails(String email, ClientType clientType) {
		super();
		this.email = email;
		this.clientType = clientType;
	}


}
