package com.data;

import com.google.common.base.CharMatcher;

/**
 * Author: Roshan C Subudhi
 * USC, Columbia
 * Ph: 803-743-2899
 * 
 * for EHG, Boston, MA
 * 
 * This class checks if the data entered is correct or wrong.
 */
public class Validation {
	
	static boolean isName(String name){
		if (name.equals(CharMatcher.JAVA_LETTER.retainFrom(name))){
			return true;
		}
		else{
			return false;
		}
	}

	static boolean isAlphaNumeric(String alphanum){
		if (alphanum.equals(CharMatcher.JAVA_LETTER_OR_DIGIT.retainFrom(alphanum))){
			return true;
		}
		else{
			return false;
		}
	}
	
	static boolean isWord(String word){
		CharMatcher textMatcher = CharMatcher.JAVA_LETTER_OR_DIGIT.or(CharMatcher.WHITESPACE);
		if (word.equals(textMatcher.retainFrom(word))){
			return true;
		}
		else{
			return false;
		}
	}
	
	static boolean isText(String text){
		CharMatcher textMatcher = CharMatcher.JAVA_LETTER_OR_DIGIT.or(CharMatcher.WHITESPACE).or(CharMatcher.anyOf(".,!:-"));
		if (text.equals(textMatcher.retainFrom(text))){
			return true;
		}
		else{
			return false;
		}
	}
	
	static boolean isNumber(String number){
		if (number.equals(CharMatcher.JAVA_DIGIT.retainFrom(number))){
			return true;
		}
		else{
			return false;
		}
	}
}
