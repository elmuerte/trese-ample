// $ANTLR 3.1.1 C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g 2009-01-21 13:15:48

/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodels.gft;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

/**
 * Grammar for parsing the generalized feature tree specification as defined in
 * "Analysis of Feature Models using Generalised Feature Trees" by Pim van den Broek
 * and Ismenia Galvao.
 *
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
public class GftParser extends GftParserBase {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "IDENTIFIER", "EQUALS", "MANDOPT", "STRING", "OR", "XOR", "LBRACK", "COMMA", "RBRACK", "CONSTRAINTS", "REQUIRES", "EXCLUDES", "WS", "ALLTOKENS"
    };
    public static final int RBRACK=12;
    public static final int CONSTRAINTS=13;
    public static final int MANDOPT=6;
    public static final int ALLTOKENS=17;
    public static final int WS=16;
    public static final int LBRACK=10;
    public static final int XOR=9;
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


        public GftParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public GftParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return GftParser.tokenNames; }
    public String getGrammarFileName() { return "C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g"; }



    // $ANTLR start "gft"
    // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g:34:1: gft : ( node )+ ( constraints ( constraint )+ )? ;
    public final void gft() throws RecognitionException {
        try {
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g:38:2: ( ( node )+ ( constraints ( constraint )+ )? )
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g:38:4: ( node )+ ( constraints ( constraint )+ )?
            {
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g:38:4: ( node )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==IDENTIFIER) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g:38:4: node
            	    {
            	    pushFollow(FOLLOW_node_in_gft46);
            	    node();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);

            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g:38:10: ( constraints ( constraint )+ )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==CONSTRAINTS) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g:38:11: constraints ( constraint )+
                    {
                    pushFollow(FOLLOW_constraints_in_gft50);
                    constraints();

                    state._fsp--;

                    // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g:38:23: ( constraint )+
                    int cnt2=0;
                    loop2:
                    do {
                        int alt2=2;
                        int LA2_0 = input.LA(1);

                        if ( (LA2_0==IDENTIFIER) ) {
                            alt2=1;
                        }


                        switch (alt2) {
                    	case 1 :
                    	    // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g:38:23: constraint
                    	    {
                    	    pushFollow(FOLLOW_constraint_in_gft52);
                    	    constraint();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt2 >= 1 ) break loop2;
                                EarlyExitException eee =
                                    new EarlyExitException(2, input);
                                throw eee;
                        }
                        cnt2++;
                    } while (true);


                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "gft"


    // $ANTLR start "node"
    // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g:41:1: node : id= IDENTIFIER EQUALS ( MANDOPT name= STRING mand= nodeList opt= nodeList | OR name= STRING chld= nodeList | XOR name= STRING chld= nodeList ) ;
    public final void node() throws RecognitionException {
        Token id=null;
        Token name=null;
        List<String> mand = null;

        List<String> opt = null;

        List<String> chld = null;


        try {
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g:45:2: (id= IDENTIFIER EQUALS ( MANDOPT name= STRING mand= nodeList opt= nodeList | OR name= STRING chld= nodeList | XOR name= STRING chld= nodeList ) )
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g:45:4: id= IDENTIFIER EQUALS ( MANDOPT name= STRING mand= nodeList opt= nodeList | OR name= STRING chld= nodeList | XOR name= STRING chld= nodeList )
            {
            id=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_node70); 
            match(input,EQUALS,FOLLOW_EQUALS_in_node72); 
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g:46:2: ( MANDOPT name= STRING mand= nodeList opt= nodeList | OR name= STRING chld= nodeList | XOR name= STRING chld= nodeList )
            int alt4=3;
            switch ( input.LA(1) ) {
            case MANDOPT:
                {
                alt4=1;
                }
                break;
            case OR:
                {
                alt4=2;
                }
                break;
            case XOR:
                {
                alt4=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g:46:4: MANDOPT name= STRING mand= nodeList opt= nodeList
                    {
                    match(input,MANDOPT,FOLLOW_MANDOPT_in_node78); 
                    name=(Token)match(input,STRING,FOLLOW_STRING_in_node82); 
                    pushFollow(FOLLOW_nodeList_in_node86);
                    mand=nodeList();

                    state._fsp--;

                    pushFollow(FOLLOW_nodeList_in_node90);
                    opt=nodeList();

                    state._fsp--;

                     mandOptFeature((id!=null?id.getText():null), (name!=null?name.getText():null), mand, opt); 

                    }
                    break;
                case 2 :
                    // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g:48:4: OR name= STRING chld= nodeList
                    {
                    match(input,OR,FOLLOW_OR_in_node99); 
                    name=(Token)match(input,STRING,FOLLOW_STRING_in_node103); 
                    pushFollow(FOLLOW_nodeList_in_node107);
                    chld=nodeList();

                    state._fsp--;

                     orFeature((id!=null?id.getText():null), (name!=null?name.getText():null), chld); 

                    }
                    break;
                case 3 :
                    // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g:50:4: XOR name= STRING chld= nodeList
                    {
                    match(input,XOR,FOLLOW_XOR_in_node116); 
                    name=(Token)match(input,STRING,FOLLOW_STRING_in_node120); 
                    pushFollow(FOLLOW_nodeList_in_node124);
                    chld=nodeList();

                    state._fsp--;

                     xorFeature((id!=null?id.getText():null), (name!=null?name.getText():null), chld); 

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "node"


    // $ANTLR start "nodeList"
    // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g:55:1: nodeList returns [List<String> ids = new ArrayList();] : LBRACK (id1= IDENTIFIER ( COMMA id2= IDENTIFIER )* )? RBRACK ;
    public final List<String> nodeList() throws RecognitionException {
        List<String> ids =  new ArrayList();;

        Token id1=null;
        Token id2=null;

        try {
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g:59:2: ( LBRACK (id1= IDENTIFIER ( COMMA id2= IDENTIFIER )* )? RBRACK )
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g:59:4: LBRACK (id1= IDENTIFIER ( COMMA id2= IDENTIFIER )* )? RBRACK
            {
            match(input,LBRACK,FOLLOW_LBRACK_in_nodeList149); 
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g:60:2: (id1= IDENTIFIER ( COMMA id2= IDENTIFIER )* )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==IDENTIFIER) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g:60:3: id1= IDENTIFIER ( COMMA id2= IDENTIFIER )*
                    {
                    id1=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_nodeList156); 
                    ids.add((id1!=null?id1.getText():null));
                    // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g:61:3: ( COMMA id2= IDENTIFIER )*
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( (LA5_0==COMMA) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g:61:4: COMMA id2= IDENTIFIER
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_nodeList163); 
                    	    id2=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_nodeList167); 
                    	    ids.add((id2!=null?id2.getText():null));

                    	    }
                    	    break;

                    	default :
                    	    break loop5;
                        }
                    } while (true);


                    }
                    break;

            }

            match(input,RBRACK,FOLLOW_RBRACK_in_nodeList178); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ids;
    }
    // $ANTLR end "nodeList"


    // $ANTLR start "constraints"
    // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g:65:1: constraints : CONSTRAINTS EQUALS clds= nodeList ;
    public final void constraints() throws RecognitionException {
        List<String> clds = null;


        try {
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g:69:2: ( CONSTRAINTS EQUALS clds= nodeList )
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g:69:4: CONSTRAINTS EQUALS clds= nodeList
            {
            match(input,CONSTRAINTS,FOLLOW_CONSTRAINTS_in_constraints192); 
            match(input,EQUALS,FOLLOW_EQUALS_in_constraints194); 
            pushFollow(FOLLOW_nodeList_in_constraints198);
            clds=nodeList();

            state._fsp--;

             includeConstraints(clds); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "constraints"


    // $ANTLR start "constraint"
    // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g:73:1: constraint : id= IDENTIFIER EQUALS (req= REQUIRES | exl= EXCLUDES ) f1= STRING f2= STRING ;
    public final void constraint() throws RecognitionException {
        Token id=null;
        Token req=null;
        Token exl=null;
        Token f1=null;
        Token f2=null;


        	String ctype = null;

        try {
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g:80:2: (id= IDENTIFIER EQUALS (req= REQUIRES | exl= EXCLUDES ) f1= STRING f2= STRING )
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g:80:4: id= IDENTIFIER EQUALS (req= REQUIRES | exl= EXCLUDES ) f1= STRING f2= STRING
            {
            id=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_constraint224); 
            match(input,EQUALS,FOLLOW_EQUALS_in_constraint226); 
            // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g:80:25: (req= REQUIRES | exl= EXCLUDES )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==REQUIRES) ) {
                alt7=1;
            }
            else if ( (LA7_0==EXCLUDES) ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g:80:26: req= REQUIRES
                    {
                    req=(Token)match(input,REQUIRES,FOLLOW_REQUIRES_in_constraint231); 
                    ctype = (req!=null?req.getText():null);

                    }
                    break;
                case 2 :
                    // C:\\local\\mhendrik_ample\\.workspace\\trese.featuremodels\\src\\trese\\featuremodels\\gft\\Gft.g:80:61: exl= EXCLUDES
                    {
                    exl=(Token)match(input,EXCLUDES,FOLLOW_EXCLUDES_in_constraint238); 
                    ctype = (exl!=null?exl.getText():null);

                    }
                    break;

            }

            f1=(Token)match(input,STRING,FOLLOW_STRING_in_constraint246); 
            f2=(Token)match(input,STRING,FOLLOW_STRING_in_constraint250); 
             constraint((id!=null?id.getText():null), ctype, (f1!=null?f1.getText():null), (f2!=null?f2.getText():null)); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "constraint"

    // Delegated rules


 

    public static final BitSet FOLLOW_node_in_gft46 = new BitSet(new long[]{0x0000000000002012L});
    public static final BitSet FOLLOW_constraints_in_gft50 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_constraint_in_gft52 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_IDENTIFIER_in_node70 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_EQUALS_in_node72 = new BitSet(new long[]{0x0000000000000340L});
    public static final BitSet FOLLOW_MANDOPT_in_node78 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_STRING_in_node82 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_nodeList_in_node86 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_nodeList_in_node90 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_node99 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_STRING_in_node103 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_nodeList_in_node107 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_XOR_in_node116 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_STRING_in_node120 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_nodeList_in_node124 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACK_in_nodeList149 = new BitSet(new long[]{0x0000000000001010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_nodeList156 = new BitSet(new long[]{0x0000000000001800L});
    public static final BitSet FOLLOW_COMMA_in_nodeList163 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_nodeList167 = new BitSet(new long[]{0x0000000000001800L});
    public static final BitSet FOLLOW_RBRACK_in_nodeList178 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONSTRAINTS_in_constraints192 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_EQUALS_in_constraints194 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_nodeList_in_constraints198 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_constraint224 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_EQUALS_in_constraint226 = new BitSet(new long[]{0x000000000000C000L});
    public static final BitSet FOLLOW_REQUIRES_in_constraint231 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_EXCLUDES_in_constraint238 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_STRING_in_constraint246 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_STRING_in_constraint250 = new BitSet(new long[]{0x0000000000000002L});

}