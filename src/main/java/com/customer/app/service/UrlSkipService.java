/**
 * 
 */
package com.customer.app.service;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Pradheep
 *
 */
@Service
@Slf4j
public class UrlSkipService {

	@Value("${skipUrls}")
	private String skipUrls;

	private List<String> skipURLCheck;

	private void loadSkipUrls() {
		if (null == skipURLCheck) {
			log.info(skipUrls);
			skipURLCheck = Arrays.asList(skipUrls.split(","));
		}
	}

	public boolean canSkipUrl(HttpServletRequest request) {
		loadSkipUrls();
		log.info("Printing the URL's" + skipURLCheck.toString());
		StringBuffer requestUrl = request.getRequestURL();
		boolean isFilterRequired = false;
		Iterator<String> skipFilterCheckIterator = skipURLCheck.iterator();
		while (skipFilterCheckIterator.hasNext()) {
			if (requestUrl.toString().contains(skipFilterCheckIterator.next())) {
				isFilterRequired = true;
				break;
			}
		}
		return isFilterRequired;
	}
}
