package com.sroyc.assurance.core.data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class AssuranceUserDetails extends User implements OAuth2User {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8635330806810975584L;
	private final transient Map<String, Object> attributes;

	public AssuranceUserDetails(OAuth2User oauth2User) {
		this(oauth2User.getAttributes().get("username") != null ? oauth2User.getAttributes().get("username").toString()
				: oauth2User.getAttributes().get("email").toString(), "NO_PASS", oauth2User.getAuthorities(),
				oauth2User.getAttributes());
	}

	public AssuranceUserDetails(String username, String password, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		this.attributes = new HashMap<>();
	}

	public AssuranceUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
		this.attributes = new HashMap<>();
	}

	public AssuranceUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities,
			Map<String, Object> attributes) {
		super(username, password, authorities);
		this.attributes = attributes;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return Collections.unmodifiableMap(this.attributes);
	}

	@Override
	public String getName() {
		return getUsername();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((getUsername() == null) ? 0 : getUsername().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AssuranceUserDetails other = (AssuranceUserDetails) obj;
		if (this.getUsername() == null) {
			if (other.getUsername() != null)
				return false;
		} else if (!getUsername().equals(other.getUsername()))
			return false;
		return true;
	}

}
