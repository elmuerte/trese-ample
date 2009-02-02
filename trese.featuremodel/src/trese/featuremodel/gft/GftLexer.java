// $ANTLR 3.1.1 C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g 2009-01-21 13:15:48

/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodel.gft;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class GftLexer extends Lexer {
    public static final int RBRACK=12;
    public static final int CONSTRAINTS=13;
    public static final int MANDOPT=6;
    public static final int ALLTOKENS=17;
    public static final int XOR=9;
    public static final int LBRACK=10;
    public static final int WS=16;
    public static final int REQUIRES=14;
    public static final int COMMA=11;
    public static final int EXCLUDES=15;
    public static final int IDENTIFIER=4;
    public static final int OR=8;
    public static final int EQUALS=5;
    public static final int EOF=-1;
    public static final int STRING=7;

    // delegates
    // delegators

    public GftLexer() {;} 
    public GftLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public GftLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g"; }

    // $ANTLR start "MANDOPT"
    public final void mMANDOPT() throws RecognitionException {
        try {
            int _type = MANDOPT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:86:10: ( 'MandOpt' )
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:86:12: 'MandOpt'
            {
            match("MandOpt"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MANDOPT"

    // $ANTLR start "OR"
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:87:6: ( 'Or' )
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:87:8: 'Or'
            {
            match("Or"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OR"

    // $ANTLR start "XOR"
    public final void mXOR() throws RecognitionException {
        try {
            int _type = XOR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:88:7: ( 'Xor' )
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:88:9: 'Xor'
            {
            match("Xor"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "XOR"

    // $ANTLR start "EXCLUDES"
    public final void mEXCLUDES() throws RecognitionException {
        try {
            int _type = EXCLUDES;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:89:10: ( 'Excludes' )
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:89:12: 'Excludes'
            {
            match("Excludes"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EXCLUDES"

    // $ANTLR start "REQUIRES"
    public final void mREQUIRES() throws RecognitionException {
        try {
            int _type = REQUIRES;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:90:10: ( 'Requires' )
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:90:12: 'Requires'
            {
            match("Requires"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "REQUIRES"

    // $ANTLR start "CONSTRAINTS"
    public final void mCONSTRAINTS() throws RecognitionException {
        try {
            int _type = CONSTRAINTS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:91:13: ( 'constraints' )
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:91:15: 'constraints'
            {
            match("constraints"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CONSTRAINTS"

    // $ANTLR start "EQUALS"
    public final void mEQUALS() throws RecognitionException {
        try {
            int _type = EQUALS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:93:10: ( '=' )
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:93:12: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EQUALS"

    // $ANTLR start "LBRACK"
    public final void mLBRACK() throws RecognitionException {
        try {
            int _type = LBRACK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:94:10: ( '[' )
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:94:12: '['
            {
            match('['); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LBRACK"

    // $ANTLR start "RBRACK"
    public final void mRBRACK() throws RecognitionException {
        try {
            int _type = RBRACK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:95:10: ( ']' )
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:95:12: ']'
            {
            match(']'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RBRACK"

    // $ANTLR start "COMMA"
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:96:8: ( ',' )
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:96:10: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMMA"

    // $ANTLR start "IDENTIFIER"
    public final void mIDENTIFIER() throws RecognitionException {
        try {
            int _type = IDENTIFIER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:99:2: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )* )
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:99:4: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:99:36: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='0' && LA1_0<='9')||(LA1_0>='A' && LA1_0<='Z')||LA1_0=='_'||(LA1_0>='a' && LA1_0<='z')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IDENTIFIER"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:103:2: ( '\"' (~ ( '\"' ) )* '\"' )
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:103:4: '\"' (~ ( '\"' ) )* '\"'
            {
            match('\"'); 
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:103:8: (~ ( '\"' ) )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>='\u0000' && LA2_0<='!')||(LA2_0>='#' && LA2_0<='\uFFFF')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:103:8: ~ ( '\"' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            match('\"'); 
            setText(getText().substring(1, getText().length()-1));

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:107:2: ( ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )+ )
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:107:5: ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )+
            {
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:107:5: ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0>='\t' && LA3_0<='\n')||(LA3_0>='\f' && LA3_0<='\r')||LA3_0==' ') ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:
            	    {
            	    if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||(input.LA(1)>='\f' && input.LA(1)<='\r')||input.LA(1)==' ' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);

            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "ALLTOKENS"
    public final void mALLTOKENS() throws RecognitionException {
        try {
            int _type = ALLTOKENS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:110:2: ( '\\u0000' .. '\\uffff' )
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:110:4: '\\u0000' .. '\\uffff'
            {
            matchRange('\u0000','\uFFFF'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ALLTOKENS"

    public void mTokens() throws RecognitionException {
        // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:1:8: ( MANDOPT | OR | XOR | EXCLUDES | REQUIRES | CONSTRAINTS | EQUALS | LBRACK | RBRACK | COMMA | IDENTIFIER | STRING | WS | ALLTOKENS )
        int alt4=14;
        alt4 = dfa4.predict(input);
        switch (alt4) {
            case 1 :
                // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:1:10: MANDOPT
                {
                mMANDOPT(); 

                }
                break;
            case 2 :
                // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:1:18: OR
                {
                mOR(); 

                }
                break;
            case 3 :
                // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:1:21: XOR
                {
                mXOR(); 

                }
                break;
            case 4 :
                // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:1:25: EXCLUDES
                {
                mEXCLUDES(); 

                }
                break;
            case 5 :
                // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:1:34: REQUIRES
                {
                mREQUIRES(); 

                }
                break;
            case 6 :
                // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:1:43: CONSTRAINTS
                {
                mCONSTRAINTS(); 

                }
                break;
            case 7 :
                // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:1:55: EQUALS
                {
                mEQUALS(); 

                }
                break;
            case 8 :
                // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:1:62: LBRACK
                {
                mLBRACK(); 

                }
                break;
            case 9 :
                // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:1:69: RBRACK
                {
                mRBRACK(); 

                }
                break;
            case 10 :
                // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:1:76: COMMA
                {
                mCOMMA(); 

                }
                break;
            case 11 :
                // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:1:82: IDENTIFIER
                {
                mIDENTIFIER(); 

                }
                break;
            case 12 :
                // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:1:93: STRING
                {
                mSTRING(); 

                }
                break;
            case 13 :
                // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:1:100: WS
                {
                mWS(); 

                }
                break;
            case 14 :
                // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodel\\src\\trese\\featuremodels\\gft\\Gft.g:1:103: ALLTOKENS
                {
                mALLTOKENS(); 

                }
                break;

        }

    }


    protected DFA4 dfa4 = new DFA4(this);
    static final String DFA4_eotS =
        "\1\uffff\6\20\5\uffff\1\16\2\uffff\1\20\1\uffff\1\35\4\20\6\uffff"+
        "\1\20\1\uffff\1\43\4\20\1\uffff\13\20\1\63\3\20\1\uffff\1\67\1\70"+
        "\1\20\2\uffff\2\20\1\74\1\uffff";
    static final String DFA4_eofS =
        "\75\uffff";
    static final String DFA4_minS =
        "\1\0\1\141\1\162\1\157\1\170\1\145\1\157\5\uffff\1\0\2\uffff\1"+
        "\156\1\uffff\1\60\1\162\1\143\1\161\1\156\6\uffff\1\144\1\uffff"+
        "\1\60\1\154\1\165\1\163\1\117\1\uffff\1\165\1\151\1\164\1\160\1"+
        "\144\2\162\1\164\2\145\1\141\1\60\2\163\1\151\1\uffff\2\60\1\156"+
        "\2\uffff\1\164\1\163\1\60\1\uffff";
    static final String DFA4_maxS =
        "\1\uffff\1\141\1\162\1\157\1\170\1\145\1\157\5\uffff\1\uffff\2"+
        "\uffff\1\156\1\uffff\1\172\1\162\1\143\1\161\1\156\6\uffff\1\144"+
        "\1\uffff\1\172\1\154\1\165\1\163\1\117\1\uffff\1\165\1\151\1\164"+
        "\1\160\1\144\2\162\1\164\2\145\1\141\1\172\2\163\1\151\1\uffff\2"+
        "\172\1\156\2\uffff\1\164\1\163\1\172\1\uffff";
    static final String DFA4_acceptS =
        "\7\uffff\1\7\1\10\1\11\1\12\1\13\1\uffff\1\15\1\16\1\uffff\1\13"+
        "\5\uffff\1\7\1\10\1\11\1\12\1\14\1\15\1\uffff\1\2\5\uffff\1\3\17"+
        "\uffff\1\1\3\uffff\1\4\1\5\3\uffff\1\6";
    static final String DFA4_specialS =
        "\1\0\13\uffff\1\1\60\uffff}>";
    static final String[] DFA4_transitionS = {
            "\11\16\2\15\1\16\2\15\22\16\1\15\1\16\1\14\11\16\1\12\20\16"+
            "\1\7\3\16\4\13\1\4\7\13\1\1\1\13\1\2\2\13\1\5\5\13\1\3\2\13"+
            "\1\10\1\16\1\11\1\16\1\13\1\16\2\13\1\6\27\13\uff85\16",
            "\1\17",
            "\1\21",
            "\1\22",
            "\1\23",
            "\1\24",
            "\1\25",
            "",
            "",
            "",
            "",
            "",
            "\0\32",
            "",
            "",
            "\1\34",
            "",
            "\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "\1\36",
            "\1\37",
            "\1\40",
            "\1\41",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\42",
            "",
            "\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "\1\44",
            "\1\45",
            "\1\46",
            "\1\47",
            "",
            "\1\50",
            "\1\51",
            "\1\52",
            "\1\53",
            "\1\54",
            "\1\55",
            "\1\56",
            "\1\57",
            "\1\60",
            "\1\61",
            "\1\62",
            "\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "\1\64",
            "\1\65",
            "\1\66",
            "",
            "\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "\1\71",
            "",
            "",
            "\1\72",
            "\1\73",
            "\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            ""
    };

    static final short[] DFA4_eot = DFA.unpackEncodedString(DFA4_eotS);
    static final short[] DFA4_eof = DFA.unpackEncodedString(DFA4_eofS);
    static final char[] DFA4_min = DFA.unpackEncodedStringToUnsignedChars(DFA4_minS);
    static final char[] DFA4_max = DFA.unpackEncodedStringToUnsignedChars(DFA4_maxS);
    static final short[] DFA4_accept = DFA.unpackEncodedString(DFA4_acceptS);
    static final short[] DFA4_special = DFA.unpackEncodedString(DFA4_specialS);
    static final short[][] DFA4_transition;

    static {
        int numStates = DFA4_transitionS.length;
        DFA4_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA4_transition[i] = DFA.unpackEncodedString(DFA4_transitionS[i]);
        }
    }

    class DFA4 extends DFA {

        public DFA4(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 4;
            this.eot = DFA4_eot;
            this.eof = DFA4_eof;
            this.min = DFA4_min;
            this.max = DFA4_max;
            this.accept = DFA4_accept;
            this.special = DFA4_special;
            this.transition = DFA4_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( MANDOPT | OR | XOR | EXCLUDES | REQUIRES | CONSTRAINTS | EQUALS | LBRACK | RBRACK | COMMA | IDENTIFIER | STRING | WS | ALLTOKENS );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA4_0 = input.LA(1);

                        s = -1;
                        if ( (LA4_0=='M') ) {s = 1;}

                        else if ( (LA4_0=='O') ) {s = 2;}

                        else if ( (LA4_0=='X') ) {s = 3;}

                        else if ( (LA4_0=='E') ) {s = 4;}

                        else if ( (LA4_0=='R') ) {s = 5;}

                        else if ( (LA4_0=='c') ) {s = 6;}

                        else if ( (LA4_0=='=') ) {s = 7;}

                        else if ( (LA4_0=='[') ) {s = 8;}

                        else if ( (LA4_0==']') ) {s = 9;}

                        else if ( (LA4_0==',') ) {s = 10;}

                        else if ( ((LA4_0>='A' && LA4_0<='D')||(LA4_0>='F' && LA4_0<='L')||LA4_0=='N'||(LA4_0>='P' && LA4_0<='Q')||(LA4_0>='S' && LA4_0<='W')||(LA4_0>='Y' && LA4_0<='Z')||LA4_0=='_'||(LA4_0>='a' && LA4_0<='b')||(LA4_0>='d' && LA4_0<='z')) ) {s = 11;}

                        else if ( (LA4_0=='\"') ) {s = 12;}

                        else if ( ((LA4_0>='\t' && LA4_0<='\n')||(LA4_0>='\f' && LA4_0<='\r')||LA4_0==' ') ) {s = 13;}

                        else if ( ((LA4_0>='\u0000' && LA4_0<='\b')||LA4_0=='\u000B'||(LA4_0>='\u000E' && LA4_0<='\u001F')||LA4_0=='!'||(LA4_0>='#' && LA4_0<='+')||(LA4_0>='-' && LA4_0<='<')||(LA4_0>='>' && LA4_0<='@')||LA4_0=='\\'||LA4_0=='^'||LA4_0=='`'||(LA4_0>='{' && LA4_0<='\uFFFF')) ) {s = 14;}

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA4_12 = input.LA(1);

                        s = -1;
                        if ( ((LA4_12>='\u0000' && LA4_12<='\uFFFF')) ) {s = 26;}

                        else s = 14;

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 4, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

}