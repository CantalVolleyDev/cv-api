package com.jtouzy.cv.api.security;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.jtouzy.cv.model.classes.User;

public class TokenHelper {
	private static final String tokenChars = "azertyuiopmlkjhgfdsqwxcvnAZERTYUIOPMLKJHGFDSQWXCVN0987654321";
	private static final BiMap<String, String> tokenReplacements = ImmutableBiMap.of("@", "b", ".", "B");
	
	public static String getUserToken(User user) {
		StringBuilder token = new StringBuilder();
		token.append(user.getMail()).reverse();
		Random random = new Random();
		StringBuilder tokenCopy = new StringBuilder(token);
		int length = tokenCopy.length()*2;
		for (int i = 0; i < length; i ++) {
			if (i % 2 == 0) {
				token.insert(i, (Object)tokenChars.charAt(random.nextInt(tokenChars.length()-1)));
			}
		}
		return getStringReplacements(token, tokenReplacements);
	}
	
	public static String getUserID(String token) {
		StringBuilder userID = new StringBuilder();
		int length = token.length();
		for (int i = 0; i < length; i ++) {
			if (i % 2 != 0) {
				userID.append(token.charAt(i));
			}
		}
		return new StringBuilder(getStringReplacements(userID, tokenReplacements.inverse()))
				.reverse().toString();
	}
	
	private static String getStringReplacements(StringBuilder token, Map<String,String> replacements) {
		String newToken = token.toString();
		Iterator<Map.Entry<String,String>> it = replacements.entrySet().iterator();
		Map.Entry<String,String> tokenReplacementChar;
		while (it.hasNext()) {
			tokenReplacementChar = it.next();
			newToken = newToken.replace(tokenReplacementChar.getKey(), tokenReplacementChar.getValue());
		}
		return newToken;
	}
}
