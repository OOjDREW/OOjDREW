// $ANTLR 2.7.5 (20050530): "POSLParser-Java.g" -> "POSLLexer.java"$
// OO jDREW Version 0.93
// 
// Copyright (c) 2005 Marcel Ball
//
// This software is licensed under the LGPL (LESSER GENERAL PUBLIC LICENSE) License.
// Please see "license.txt" in the root directory of this software package for more details.
//
// Disclaimer: Please see disclaimer.txt in the root directory of this package.

package jdrew.oo.util.parsing;

import jdrew.oo.util.*;
import java.util.*;

/**
 * This Class was generated with ANTLR, you can download the grammar use to create
 * the class from OOjDREW's homepage.
 * <p>Title: OO jDREW</p>
 *
 * <p>Description: Reasoning Engine for the Semantic Web - Supporting OO RuleML
 * 0.88</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * @author Marcel A. Ball
 * @version 0.89
 */

import java.io.InputStream;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;
import antlr.CharStreamException;
import antlr.CharStreamIOException;
import antlr.ANTLRException;
import java.io.Reader;
import java.util.Hashtable;
import antlr.CharScanner;
import antlr.InputBuffer;
import antlr.ByteBuffer;
import antlr.CharBuffer;
import antlr.Token;
import antlr.CommonToken;
import antlr.RecognitionException;
import antlr.NoViableAltForCharException;
import antlr.MismatchedCharException;
import antlr.TokenStream;
import antlr.ANTLRHashString;
import antlr.LexerSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.SemanticException;

public class POSLLexer extends antlr.CharScanner implements
        POSLParserTokenTypes, TokenStream {
    public POSLLexer(InputStream in) {
        this(new ByteBuffer(in));
    }

    public POSLLexer(Reader in) {
        this(new CharBuffer(in));
    }

    public POSLLexer(InputBuffer ib) {
        this(new LexerSharedInputState(ib));
    }

    public POSLLexer(LexerSharedInputState state) {
        super(state);
        caseSensitiveLiterals = true;
        setCaseSensitive(true);
        literals = new Hashtable();
    }

    public Token nextToken() throws TokenStreamException {
        Token theRetToken = null;
        tryAgain:
                for (; ; ) {
            Token _token = null;
            int _ttype = Token.INVALID_TYPE;
            resetText();
            try { // for char stream error handling
                try { // for lexical error handling
                    switch (LA(1)) {
                    case '|': {
                        mPIPE(true);
                        theRetToken = _returnToken;
                        break;
                    }
                    case '!': {
                        mBANG(true);
                        theRetToken = _returnToken;
                        break;
                    }
                    case '^': {
                        mHAT(true);
                        theRetToken = _returnToken;
                        break;
                    }
                    case ';': {
                        mSEMI(true);
                        theRetToken = _returnToken;
                        break;
                    }
                    case '[': {
                        mLBRACK(true);
                        theRetToken = _returnToken;
                        break;
                    }
                    case ']': {
                        mRBRACK(true);
                        theRetToken = _returnToken;
                        break;
                    }
                    case '(': {
                        mLPAREN(true);
                        theRetToken = _returnToken;
                        break;
                    }
                    case ')': {
                        mRPAREN(true);
                        theRetToken = _returnToken;
                        break;
                    }
                    case '?': {
                        mQMARK(true);
                        theRetToken = _returnToken;
                        break;
                    }
                    case ',': {
                        mCOMMA(true);
                        theRetToken = _returnToken;
                        break;
                    }
                    case '.': {
                        mPERIOD(true);
                        theRetToken = _returnToken;
                        break;
                    }
                    case '{': {
                        mLBRACE(true);
                        theRetToken = _returnToken;
                        break;
                    }
                    case '}': {
                        mRBRACE(true);
                        theRetToken = _returnToken;
                        break;
                    }
                    case '_': {
                        mUSCORE(true);
                        theRetToken = _returnToken;
                        break;
                    }
                    case '<': {
                        mURI(true);
                        theRetToken = _returnToken;
                        break;
                    }
                    case '"': {
                        mQSYMBOL(true);
                        theRetToken = _returnToken;
                        break;
                    }
                    case '%': {
                        mCOMMENT(true);
                        theRetToken = _returnToken;
                        break;
                    }
                    case '/': {
                        mMLCOMMENT(true);
                        theRetToken = _returnToken;
                        break;
                    }
                    case '\t':
                    case '\n':
                    case '\r':
                    case ' ': {
                        mWS(true);
                        theRetToken = _returnToken;
                        break;
                    }
                    default:
                        if ((LA(1) == ':') && (LA(2) == '-')) {
                            mIMP(true);
                            theRetToken = _returnToken;
                        } else if ((LA(1) == '-') && (LA(2) == '>')) {
                            mARROW(true);
                            theRetToken = _returnToken;
                        } else if ((LA(1) == ':') && (true)) {
                            mCOLON(true);
                            theRetToken = _returnToken;
                        } else if ((_tokenSet_0.member(LA(1))) && (true)) {
                            mSYMBOL(true);
                            theRetToken = _returnToken;
                        } else {
                            if (LA(1) == EOF_CHAR) {
                                uponEOF();
                                _returnToken = makeToken(Token.EOF_TYPE);
                            } else {
                                throw new NoViableAltForCharException((char) LA(
                                        1), getFilename(), getLine(), getColumn());
                            }
                        }
                    }
                    if (_returnToken == null) {
                        continue tryAgain; // found SKIP token
                    }
                    _ttype = _returnToken.getType();
                    _ttype = testLiteralsTable(_ttype);
                    _returnToken.setType(_ttype);
                    return _returnToken;
                } catch (RecognitionException e) {
                    throw new TokenStreamRecognitionException(e);
                }
            } catch (CharStreamException cse) {
                if (cse instanceof CharStreamIOException) {
                    throw new TokenStreamIOException(((CharStreamIOException)
                            cse).io);
                } else {
                    throw new TokenStreamException(cse.getMessage());
                }
            }
        }
    }

    public final void mPIPE(boolean _createToken) throws RecognitionException,
            CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = PIPE;
        int _saveIndex;

        match('|');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    public final void mBANG(boolean _createToken) throws RecognitionException,
            CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = BANG;
        int _saveIndex;

        match('!');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    public final void mHAT(boolean _createToken) throws RecognitionException,
            CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = HAT;
        int _saveIndex;

        match('^');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    public final void mCOLON(boolean _createToken) throws RecognitionException,
            CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = COLON;
        int _saveIndex;

        match(':');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    public final void mSEMI(boolean _createToken) throws RecognitionException,
            CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = SEMI;
        int _saveIndex;

        match(';');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    public final void mLBRACK(boolean _createToken) throws RecognitionException,
            CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = LBRACK;
        int _saveIndex;

        match('[');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    public final void mRBRACK(boolean _createToken) throws RecognitionException,
            CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = RBRACK;
        int _saveIndex;

        match(']');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    public final void mLPAREN(boolean _createToken) throws RecognitionException,
            CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = LPAREN;
        int _saveIndex;

        match('(');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    public final void mRPAREN(boolean _createToken) throws RecognitionException,
            CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = RPAREN;
        int _saveIndex;

        match(')');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    public final void mQMARK(boolean _createToken) throws RecognitionException,
            CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = QMARK;
        int _saveIndex;

        match('?');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    public final void mCOMMA(boolean _createToken) throws RecognitionException,
            CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = COMMA;
        int _saveIndex;

        match(',');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    public final void mPERIOD(boolean _createToken) throws RecognitionException,
            CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = PERIOD;
        int _saveIndex;

        match('.');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    public final void mLBRACE(boolean _createToken) throws RecognitionException,
            CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = LBRACE;
        int _saveIndex;

        match('{');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    public final void mRBRACE(boolean _createToken) throws RecognitionException,
            CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = RBRACE;
        int _saveIndex;

        match('}');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    public final void mUSCORE(boolean _createToken) throws RecognitionException,
            CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = USCORE;
        int _saveIndex;

        match('_');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    public final void mIMP(boolean _createToken) throws RecognitionException,
            CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = IMP;
        int _saveIndex;

        match(":-");
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    public final void mARROW(boolean _createToken) throws RecognitionException,
            CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = ARROW;
        int _saveIndex;

        match("->");
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    public final void mURI(boolean _createToken) throws RecognitionException,
            CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = URI;
        int _saveIndex;

        match('<');
        {
            int _cnt88 = 0;
            _loop88:
                    do {
                switch (LA(1)) {
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z': {
                    matchRange('a', 'z');
                    break;
                }
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z': {
                    matchRange('A', 'Z');
                    break;
                }
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9': {
                    matchRange('0', '9');
                    break;
                }
                case '_': {
                    match('_');
                    break;
                }
                case ':': {
                    match(':');
                    break;
                }
                case '/': {
                    match('/');
                    break;
                }
                case '.': {
                    match('.');
                    break;
                }
                case '?': {
                    match('?');
                    break;
                }
                case '&': {
                    match('&');
                    break;
                }
                case '%': {
                    match('%');
                    break;
                }
                case '#': {
                    match('#');
                    break;
                }
                case '-': {
                    match('-');
                    break;
                }
                default: {
                    if (_cnt88 >= 1) {
                        break _loop88;
                    } else {
                        throw new NoViableAltForCharException((char) LA(1),
                                getFilename(), getLine(), getColumn());
                    }
                }
                }
                _cnt88++;
            } while (true);
        }
        match('>');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    public final void mSYMBOL(boolean _createToken) throws RecognitionException,
            CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = SYMBOL;
        int _saveIndex;

        {
            switch (LA(1)) {
            case '-': {
                match('-');
                break;
            }
            case '$':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z': {
                break;
            }
            default: {
                throw new NoViableAltForCharException((char) LA(1), getFilename(),
                        getLine(), getColumn());
            }
            }
        }
        {
            switch (LA(1)) {
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z': {
                matchRange('a', 'z');
                break;
            }
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z': {
                matchRange('A', 'Z');
                break;
            }
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9': {
                matchRange('0', '9');
                break;
            }
            case '$': {
                match('$');
                break;
            }
            default: {
                throw new NoViableAltForCharException((char) LA(1), getFilename(),
                        getLine(), getColumn());
            }
            }
        }
        {
            _loop93:
                    do {
                switch (LA(1)) {
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z': {
                    matchRange('a', 'z');
                    break;
                }
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z': {
                    matchRange('A', 'Z');
                    break;
                }
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9': {
                    matchRange('0', '9');
                    break;
                }
                case '_': {
                    match('_');
                    break;
                }
                case '.': {
                    match('.');
                    break;
                }
                case '$': {
                    match('$');
                    break;
                }
                default: {
                    break _loop93;
                }
                }
            } while (true);
        }
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    public final void mQSYMBOL(boolean _createToken) throws
            RecognitionException, CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = QSYMBOL;
        int _saveIndex;

        match('\"');
        {
            int _cnt97 = 0;
            _loop97:
                    do {
                if ((_tokenSet_1.member(LA(1)))) {
                    {
                        match(_tokenSet_1);
                    }
                } else {
                    if (_cnt97 >= 1) {
                        break _loop97;
                    } else {
                        throw new NoViableAltForCharException((char) LA(1),
                                getFilename(), getLine(), getColumn());
                    }
                }

                _cnt97++;
            } while (true);
        }
        match('\"');
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    public final void mCOMMENT(boolean _createToken) throws
            RecognitionException, CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = COMMENT;
        int _saveIndex;

        match('%');
        {
            _loop101:
                    do {
                if ((_tokenSet_2.member(LA(1)))) {
                    {
                        match(_tokenSet_2);
                    }
                } else {
                    break _loop101;
                }

            } while (true);
        }
        _ttype = Token.SKIP;
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    public final void mMLCOMMENT(boolean _createToken) throws
            RecognitionException, CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = MLCOMMENT;
        int _saveIndex;

        match("/%");
        {
            _loop105:
                    do {
                switch (LA(1)) {
                case '\u0000':
                case '\u0001':
                case '\u0002':
                case '\u0003':
                case '\u0004':
                case '\u0005':
                case '\u0006':
                case '\u0007':
                case '\u0008':
                case '\t':
                case '\u000b':
                case '\u000c':
                case '\r':
                case '\u000e':
                case '\u000f':
                case '\u0010':
                case '\u0011':
                case '\u0012':
                case '\u0013':
                case '\u0014':
                case '\u0015':
                case '\u0016':
                case '\u0017':
                case '\u0018':
                case '\u0019':
                case '\u001a':
                case '\u001b':
                case '\u001c':
                case '\u001d':
                case '\u001e':
                case '\u001f':
                case ' ':
                case '!':
                case '"':
                case '#':
                case '$':
                case '&':
                case '\'':
                case '(':
                case ')':
                case '*':
                case '+':
                case ',':
                case '-':
                case '.':
                case '/':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case ':':
                case ';':
                case '<':
                case '=':
                case '>':
                case '?':
                case '@':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                case '[':
                case '\\':
                case ']':
                case '^':
                case '_':
                case '`':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                case '{':
                case '|':
                case '}':
                case '~':
                case '\u007f': {
                    {
                        match(_tokenSet_3);
                    }
                    break;
                }
                case '\n': {
                    match('\n');
                    newline();
                    break;
                }
                default: {
                    break _loop105;
                }
                }
            } while (true);
        }
        match("%/");
        _ttype = Token.SKIP;
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }

    public final void mWS(boolean _createToken) throws RecognitionException,
            CharStreamException, TokenStreamException {
        int _ttype;
        Token _token = null;
        int _begin = text.length();
        _ttype = WS;
        int _saveIndex;

        {
            switch (LA(1)) {
            case ' ': {
                match(' ');
                break;
            }
            case '\t': {
                match('\t');
                break;
            }
            case '\r': {
                match('\r');
                match('\n');
                newline();
                break;
            }
            case '\n': {
                match('\n');
                newline();
                break;
            }
            default: {
                throw new NoViableAltForCharException((char) LA(1), getFilename(),
                        getLine(), getColumn());
            }
            }
        }
        _ttype = Token.SKIP;
        if (_createToken && _token == null && _ttype != Token.SKIP) {
            _token = makeToken(_ttype);
            _token.setText(new String(text.getBuffer(), _begin,
                                      text.length() - _begin));
        }
        _returnToken = _token;
    }


    private static final long[] mk_tokenSet_0() {
        long[] data = {287984154266566656L, 576460743847706622L, 0L, 0L};
        return data;
    }

    public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
    private static final long[] mk_tokenSet_1() {
        long[] data = { -17179869185L, -1L, 0L, 0L};
        return data;
    }

    public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
    private static final long[] mk_tokenSet_2() {
        long[] data = { -1025L, -1L, 0L, 0L};
        return data;
    }

    public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
    private static final long[] mk_tokenSet_3() {
        long[] data = { -137438954497L, -1L, 0L, 0L};
        return data;
    }

    public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());

}
