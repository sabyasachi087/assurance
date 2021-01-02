package com.sroyc.assurance.core.basic;

import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Marker interface, will be invoked by {@linkplain BasicAuthenticationProvider}
 */
public interface BasicUserDetailsService extends UserDetailsService {

}
