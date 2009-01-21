/**
 * Grammar for parsing the generalized feature tree specification as defined in
 * "Analysis of Feature Models using Generalised Feature Trees" by Pim van den Broek
 * and Ismenia Galvao.
 *
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
grammar Gft;

options {
	superClass = GftParserBase;
}

@parser::header {
/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodels.gft;
}

@lexer::header {
/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodels.gft;
}

/**
 * A generalized feature tree specification
 */
gft
	: node+ (constraints constraint+)?
	;

/**
 * Definition of a node
 */
node
	: id=IDENTIFIER EQUALS 
	( MANDOPT name=STRING mand=nodeList opt=nodeList
		{ mandOptFeature($id.text, $name.text, mand, opt); }
	| OR name=STRING chld=nodeList
		{ orFeature($id.text, $name.text, chld); }
	| XOR name=STRING chld=nodeList
		{ xorFeature($id.text, $name.text, chld); }
	)
	;
	
/**
 * List of nodes
 */
nodeList returns [List<String> ids = new ArrayList();]
	: LBRACK 
	(id1=IDENTIFIER {ids.add($id1.text);}
		(COMMA id2=IDENTIFIER {ids.add($id2.text);} )*
	)? RBRACK
	;
	
/**
 * Defines the constraints to use
 */
constraints
	: CONSTRAINTS EQUALS clds=nodeList
		{ includeConstraints(clds); }
	;	
	
/**
 * Constraint definition, uses the feature names rather than the node ids
 */
constraint
@init {
	String ctype = null;
}
	: id=IDENTIFIER EQUALS (req=REQUIRES {ctype = $req.text;} |exl=EXCLUDES {ctype = $exl.text;} ) f1=STRING f2=STRING
		{ constraint($id.text, ctype, $f1.text, $f2.text); }
	;

// $<Tokens

MANDOPT		: 'MandOpt';
OR			: 'Or';
XOR			: 'Xor';
EXCLUDES	: 'Excludes';
REQUIRES	: 'Requires';
CONSTRAINTS	: 'constraints';

EQUALS 		: '=';
LBRACK 		: '[';
RBRACK 		: ']';
COMMA		: ',';

IDENTIFIER
	: ('a' .. 'z' | 'A' .. 'Z' | '_') ('a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_')*
	;
	
STRING
	: '"' ~('"')* '"' {setText(getText().substring(1, getText().length()-1));}
	;

WS
	:  (' '|'\r'|'\t'|'\u000C'|'\n')+ {$channel=HIDDEN;};

ALLTOKENS 	
	: '\u0000' .. '\uffff';

// $>
