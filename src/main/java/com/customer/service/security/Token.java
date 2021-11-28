/**
 * 
 */
package com.customer.service.security;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Pradheep
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class Token {

	private String token;

	private String refreshToken;
}
