/* Generated By:JavaCC: Do not edit this line. TermParserConstants.java */
package gnu.prolog.io.parser.gen;

public interface TermParserConstants
{

	int EOF = 0;
	int LAYOUT_TEXT_SEQUENCE = 1;
	int LAYOUT_TEXT = 2;
	int COMMENT = 3;
	int SINGLE_LINE_COMMENT = 4;
	int BRACKETED_COMMENT = 5;
	int NAME_TOKEN = 6;
	int IDENTIFIER_TOKEN = 7;
	int GRAPHIC_TOKEN = 8;
	int GRAPHIC_TOKEN_CHAR = 9;
	int QUOTED_TOKEN = 10;
	int SINGLE_QUOTED_ITEM = 11;
	int CONTINUATION_ESCAPE_SEQUENCE = 12;
	int SEMICOLON_TOKEN = 13;
	int CUT_TOKEN = 14;
	int SINGLE_QUOTED_CHAR = 15;
	int DOUBLE_QUOTED_CHAR = 16;
	int BACK_QUOTED_CHAR = 17;
	int NON_QUOTE_CHAR = 18;
	int META_ESCAPE_SEQUENCE = 19;
	int CONTROL_ESCAPE_SEQUENCE = 20;
	int SYMOLIC_CONTROL_CHAR = 21;
	int SYMOLIC_ALERT_CHAR = 22;
	int SYMOLIC_BACKSPACE_CHAR = 23;
	int SYMOLIC_FORM_FEED_CHAR = 24;
	int SYMOLIC_NEW_LINE_CHAR = 25;
	int SYMOLIC_HORIZONTAL_TAB_CHAR = 26;
	int SYMOLIC_VERTICAL_TAB_CHAR = 27;
	int SYMOLIC_CARRIAGE_RETURN_CHAR = 28;
	int SYMOLIC_HEXADECIMAL_CHAR = 29;
	int OCTAL_ESCAPE_SEQUENCE = 30;
	int HEXADECIMAL_ESCAPE_SEQUENCE = 31;
	int VARIABLE_TOKEN = 32;
	int ANONYMOUS_VARIABLE = 33;
	int NAMED_VARIABLE = 34;
	int VARIABLE_INDICATOR_CHAR = 35;
	int INTEGER_TOKEN = 36;
	int INTEGER_CONSTANT = 37;
	int CHARACTER_CODE_CONSTANT = 38;
	int BINARY_CONSTANT = 39;
	int OCTAL_CONSTANT = 40;
	int HEXADECIMAL_CONSTANT = 41;
	int FLOAT_NUMBER_TOKEN = 42;
	int FRACTION = 43;
	int EXPONENT = 44;
	int CHAR_CODE_LIST_TOKEN = 45;
	int DOUBLE_QUOTED_ITEM = 46;
	int BACK_QUOTED_STRING = 47;
	int BACK_QUOTED_ITEM = 48;
	int OPEN_TOKEN = 49;
	int CLOSE_TOKEN = 50;
	int OPEN_LIST_TOKEN = 51;
	int CLOSE_LIST_TOKEN = 52;
	int OPEN_CURLY_TOKEN = 53;
	int CLOSE_CURLY_TOKEN = 54;
	int HEAD_TAIL_SEPARATOR_TOKEN = 55;
	int COMMA_TOKEN = 56;
	int END_TOKEN = 57;
	int END_CHAR = 58;
	int CHARARCTER = 59;
	int GRAPHIC_CHAR = 60;
	int GRAPHIC_CHAR_PERIOD = 61;
	int ALPHA_NUMERIC_CHAR = 62;
	int ALPHA_CHAR = 63;
	int LETTER_CHAR = 64;
	int CAPITAL_LETTER_CHAR = 65;
	int SMALL_LETTER_CHAR = 66;
	int DECIMAL_DIGIT_CHAR = 67;
	int BINARY_DIGIT_CHAR = 68;
	int OCTAL_DIGIT_CHAR = 69;
	int HEXADECIMAL_DIGIT_CHAR = 70;
	int UNDERSCORE_CHAR = 71;
	int SOLO_CHAR = 72;
	int CUT_CHAR = 73;
	int OPEN_CHAR = 74;
	int CLOSE_CHAR = 75;
	int COMMA_CHAR = 76;
	int SEMICOLON_CHAR = 77;
	int OPEN_LIST_CHAR = 78;
	int CLOSE_LIST_CHAR = 79;
	int OPEN_CURLY_CHAR = 80;
	int CLOSE_CURLY_CHAR = 81;
	int HEAD_TAIL_SEPARATOR_CHAR = 82;
	int END_LINE_COMMENT_CHAR = 83;
	int LAYOUT_CHAR = 84;
	int SPACE_CHAR = 85;
	int NEW_LINE_CHAR = 86;
	int META_CHAR = 87;
	int BACKSLASH_CHAR = 88;
	int SINGLE_QUOTE_CHAR = 89;
	int DOUBLE_QUOTE_CHAR = 90;
	int BACK_QUOTE_CHAR = 91;

	int DEFAULT = 0;

	String[] tokenImage = { "<EOF>", "<LAYOUT_TEXT_SEQUENCE>", "<LAYOUT_TEXT>", "<COMMENT>", "<SINGLE_LINE_COMMENT>",
			"<BRACKETED_COMMENT>", "<NAME_TOKEN>", "<IDENTIFIER_TOKEN>", "<GRAPHIC_TOKEN>", "<GRAPHIC_TOKEN_CHAR>",
			"<QUOTED_TOKEN>", "<SINGLE_QUOTED_ITEM>", "<CONTINUATION_ESCAPE_SEQUENCE>", "<SEMICOLON_TOKEN>", "<CUT_TOKEN>",
			"<SINGLE_QUOTED_CHAR>", "<DOUBLE_QUOTED_CHAR>", "<BACK_QUOTED_CHAR>", "<NON_QUOTE_CHAR>",
			"<META_ESCAPE_SEQUENCE>", "<CONTROL_ESCAPE_SEQUENCE>", "<SYMOLIC_CONTROL_CHAR>", "\"a\"", "\"b\"", "\"f\"",
			"\"n\"", "\"t\"", "\"v\"", "\"r\"", "\"x\"", "<OCTAL_ESCAPE_SEQUENCE>", "<HEXADECIMAL_ESCAPE_SEQUENCE>",
			"<VARIABLE_TOKEN>", "<ANONYMOUS_VARIABLE>", "<NAMED_VARIABLE>", "<VARIABLE_INDICATOR_CHAR>", "<INTEGER_TOKEN>",
			"<INTEGER_CONSTANT>", "<CHARACTER_CODE_CONSTANT>", "<BINARY_CONSTANT>", "<OCTAL_CONSTANT>",
			"<HEXADECIMAL_CONSTANT>", "<FLOAT_NUMBER_TOKEN>", "<FRACTION>", "<EXPONENT>", "<CHAR_CODE_LIST_TOKEN>",
			"<DOUBLE_QUOTED_ITEM>", "<BACK_QUOTED_STRING>", "<BACK_QUOTED_ITEM>", "<OPEN_TOKEN>", "<CLOSE_TOKEN>",
			"<OPEN_LIST_TOKEN>", "<CLOSE_LIST_TOKEN>", "<OPEN_CURLY_TOKEN>", "<CLOSE_CURLY_TOKEN>",
			"<HEAD_TAIL_SEPARATOR_TOKEN>", "<COMMA_TOKEN>", "<END_TOKEN>", "\".\"", "<CHARARCTER>", "<GRAPHIC_CHAR>",
			"<GRAPHIC_CHAR_PERIOD>", "<ALPHA_NUMERIC_CHAR>", "<ALPHA_CHAR>", "<LETTER_CHAR>", "<CAPITAL_LETTER_CHAR>",
			"<SMALL_LETTER_CHAR>", "<DECIMAL_DIGIT_CHAR>", "<BINARY_DIGIT_CHAR>", "<OCTAL_DIGIT_CHAR>",
			"<HEXADECIMAL_DIGIT_CHAR>", "\"_\"", "<SOLO_CHAR>", "\"!\"", "\"(\"", "\")\"", "\",\"", "\";\"", "\"[\"",
			"\"]\"", "\"{\"", "\"}\"", "\"|\"", "\"%\"", "<LAYOUT_CHAR>", "<SPACE_CHAR>", "<NEW_LINE_CHAR>", "<META_CHAR>",
			"\"\\\\\"", "\"\\\'\"", "\"\\\"\"", "\"`\"", };

}
