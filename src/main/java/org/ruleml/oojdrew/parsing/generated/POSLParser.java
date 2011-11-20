// $ANTLR 2.7.5 (20050530): "POSLParser-Java.g" -> "POSLParser.java"$
// OO jDREW - An Object Oriented extension of the Java Deductive Reasoning Engine for the Web
// Copyright (C) 2005 Marcel Ball
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

package org.ruleml.oojdrew.parsing.generated;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.ruleml.oojdrew.util.DefiniteClause;
import org.ruleml.oojdrew.util.SymbolTable;
import org.ruleml.oojdrew.util.Term;
import org.ruleml.oojdrew.util.Types;

import antlr.NoViableAltException;
import antlr.ParserSharedInputState;
import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenBuffer;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.collections.impl.BitSet;

public class POSLParser extends antlr.LLkParser implements POSLParserTokenTypes {

    // instance variables
    private Hashtable skolemMap = new Hashtable();
    private static int aid = 1000;
    private Vector variableNames;
    private Hashtable varClasses;


    // Add other methods

    private int internVar(String varname) {
        int idx;
        idx = variableNames.indexOf(varname);
        if (idx == -1) {
            idx = variableNames.size();
            variableNames.add(varname);
        }

        return -(idx + 1);
    }

    private Hashtable buildTypetable() {
        Hashtable ht = new Hashtable();

        Enumeration e = varClasses.keys();

        while (e.hasMoreElements()) {
            Object key = e.nextElement();
            Vector value = (Vector) varClasses.get(key);
            int[] types = new int[value.size()];
            for (int i = 0; i < types.length; i++) {
                types[i] = ((Integer) value.get(i)).intValue();
            }

            int type = Types.greatestLowerBound(types);
            ht.put(key, type);
        }

        return ht;
    }

    private void fixVarTypes(Term ct, Hashtable types) {
        for (int i = 0; i < ct.subTerms.length; i++) {
            if (ct.subTerms[i].isExpr()) {
                fixVarTypes(ct.subTerms[i], types);
            } else if (ct.subTerms[i].getSymbol() < 0) {
                Integer sym = ct.subTerms[i].getSymbol();
                Integer type = (Integer) types.get(sym);
                ct.subTerms[i].type = type.intValue();
            }
        }
    }


    protected POSLParser(TokenBuffer tokenBuf, int k) {
        super(tokenBuf, k);
        tokenNames = _tokenNames;
    }

    public POSLParser(TokenBuffer tokenBuf) {
        this(tokenBuf, 3);
    }

    protected POSLParser(TokenStream lexer, int k) {
        super(lexer, k);
        tokenNames = _tokenNames;
    }

    public POSLParser(TokenStream lexer) {
        this(lexer, 3);
    }

    public POSLParser(ParserSharedInputState state) {
        super(state, 3);
        tokenNames = _tokenNames;
    }

    public final void rulebase(
            Vector clauses
            ) throws RecognitionException, TokenStreamException {

        DefiniteClause c;

        try { // for error handling
            {
                _loop3:
                        do {
                    if ((LA(1) == SYMBOL || LA(1) == QSYMBOL)) {
                        c = clause(true);
                        if (inputState.guessing == 0) {
                            clauses.add(c);
                        }
                    } else {
                        break _loop3;
                    }

                } while (true);
            }
            match(Token.EOF_TYPE);
        } catch (RecognitionException ex) {
            if (inputState.guessing == 0) {
                reportError(ex);
                recover(ex, _tokenSet_0);
            } else {
                throw ex;
            }
        }
    }

    public final DefiniteClause clause(
            boolean newVars
            ) throws RecognitionException, TokenStreamException {
        DefiniteClause dc = null;

        if (newVars) {
            variableNames = new Vector();
            varClasses = new Hashtable();
        }
        Vector atms = new Vector();
        Term head;

        try { // for error handling
            head = atom(true);
            if (inputState.guessing == 0) {
                atms.add(head);
            }
            {
                switch (LA(1)) {
                case IMP: {
                    match(IMP);
                    atoms(atms);
                    break;
                }
                case PERIOD: {
                    break;
                }
                default: {
                    throw new NoViableAltException(LT(1), getFilename());
                }
                }
            }
            match(PERIOD);
            if (inputState.guessing == 0) {

                Hashtable types = buildTypetable();
                for (int i = 0; i < atms.size(); i++) {
                    fixVarTypes((Term) atms.get(i), types);
                }
                dc = new DefiniteClause(atms, variableNames);

            }
        } catch (RecognitionException ex) {
            if (inputState.guessing == 0) {
                reportError(ex);
                recover(ex, _tokenSet_1);
            } else {
                throw ex;
            }
        }
        return dc;
    }

    public final Term atom(
            boolean head
            ) throws RecognitionException, TokenStreamException {
        Term atm = null;

        Vector params = new Vector();
        int r;
        Term o;

        try { // for error handling
            r = rel();
            if (inputState.guessing == 0) {

                if (r == SymbolTable.IASSERT) {
                    return assertatom();
                } else if (r == SymbolTable.INAF) {
                    return nafatom();
                }

            }
            match(LPAREN);
            {
                boolean synPredMatched14 = false;
                if (((_tokenSet_2.member(LA(1))) && (_tokenSet_3.member(LA(2))) &&
                     (_tokenSet_4.member(LA(3))))) {
                    int _m14 = mark();
                    synPredMatched14 = true;
                    inputState.guessing++;
                    try {
                        {
                            oid();
                        }
                    } catch (RecognitionException pe) {
                        synPredMatched14 = false;
                    }
                    rewind(_m14);
                    inputState.guessing--;
                }
                if (synPredMatched14) {
                    o = oid();
                    if (inputState.guessing == 0) {
                        params.add(o);
                    }
                } else if ((_tokenSet_5.member(LA(1))) &&
                           (_tokenSet_6.member(LA(2))) &&
                           (_tokenSet_7.member(LA(3)))) {
                    if (inputState.guessing == 0) {

                        if (head) {
                            String symname = "$gensym" + SymbolTable.genid++;
                            int symid = SymbolTable.internSymbol(symname);
                            Term t2 = new Term(symid, SymbolTable.IOID,
                                               Types.IOBJECT);
                            params.add(t2);
                        } else {
                            String varname = "$ANON" + aid++;
                            int symid = internVar(varname);
                            Integer sym = symid;

                            Term t2 = new Term(symid, SymbolTable.IOID,
                                               Types.IOBJECT);
                            params.add(t2);

                            Vector v = new Vector();
                            v.add(Types.IOBJECT);
                            varClasses.put(sym, v);
                        }

                    }
                } else {
                    throw new NoViableAltException(LT(1), getFilename());
                }

            }
            {
                switch (LA(1)) {
                case PIPE:
                case BANG:
                case LBRACK:
                case USCORE:
                case QMARK:
                case SYMBOL:
                case QSYMBOL: {
                    ps(params);
                    break;
                }
                case RPAREN: {
                    break;
                }
                default: {
                    throw new NoViableAltException(LT(1), getFilename());
                }
                }
            }
            match(RPAREN);
            if (inputState.guessing == 0) {

                atm = new Term(r, SymbolTable.INOROLE, Types.IOBJECT, params);
                atm.atom = true;

            }
        } catch (RecognitionException ex) {
            if (inputState.guessing == 0) {
                reportError(ex);
                recover(ex, _tokenSet_8);
            } else {
                throw ex;
            }
        }
        return atm;
    }

    public final void atoms(
            Vector atms
            ) throws RecognitionException, TokenStreamException {

        Term a;

        try { // for error handling
            a = atom(false);
            if (inputState.guessing == 0) {
                atms.add(a);
            }
            {
                _loop8:
                        do {
                    if ((LA(1) == COMMA)) {
                        match(COMMA);
                        a = atom(false);
                        if (inputState.guessing == 0) {
                            atms.add(a);
                        }
                    } else {
                        break _loop8;
                    }

                } while (true);
            }
        } catch (RecognitionException ex) {
            if (inputState.guessing == 0) {
                reportError(ex);
                recover(ex, _tokenSet_9);
            } else {
                throw ex;
            }
        }
    }

    public final Term nafatom() throws RecognitionException,
            TokenStreamException {
        Term naf = null;

        Vector params = new Vector();

        try { // for error handling
            match(LPAREN);
            atoms(params);
            match(RPAREN);
            if (inputState.guessing == 0) {

                naf = new Term(SymbolTable.INAF, SymbolTable.INOROLE,
                               Types.IOBJECT, params);
                naf.atom = true;

            }
        } catch (RecognitionException ex) {
            if (inputState.guessing == 0) {
                reportError(ex);
                recover(ex, _tokenSet_0);
            } else {
                throw ex;
            }
        }
        return naf;
    }

    public final Term assertatom() throws RecognitionException,
            TokenStreamException {
        Term ass = null;

        DefiniteClause c;

        try { // for error handling
            match(LPAREN);
            c = clause(false);
            match(RPAREN);
            if (inputState.guessing == 0) {

                Vector params = new Vector();
                for (int i = 0; i < c.atoms.length; i++) {
                    params.add(c.atoms[i]);
                }
                ass = new Term(SymbolTable.IASSERT, SymbolTable.INOROLE,
                               Types.IOBJECT, params);
                ass.atom = true;

            }
        } catch (RecognitionException ex) {
            if (inputState.guessing == 0) {
                reportError(ex);
                recover(ex, _tokenSet_0);
            } else {
                throw ex;
            }
        }
        return ass;
    }

    public final int rel() throws RecognitionException, TokenStreamException {
        int r = -1;

        String sym;

        try { // for error handling
            sym = symbol();
            if (inputState.guessing == 0) {

                r = SymbolTable.internSymbol(sym);

            }
        } catch (RecognitionException ex) {
            if (inputState.guessing == 0) {
                reportError(ex);
                recover(ex, _tokenSet_10);
            } else {
                throw ex;
            }
        }
        return r;
    }

    public final Term oid() throws RecognitionException, TokenStreamException {
        Term t = null;

        try { // for error handling
            t = term();
            match(HAT);
            if (inputState.guessing == 0) {

                t.role = SymbolTable.IOID;

            }
        } catch (RecognitionException ex) {
            if (inputState.guessing == 0) {
                reportError(ex);
                recover(ex, _tokenSet_5);
            } else {
                throw ex;
            }
        }
        return t;
    }

    public final void ps(
            Vector terms
            ) throws RecognitionException, TokenStreamException {

        try { // for error handling
            switch (LA(1)) {
            case PIPE: {
                prest(terms);
                {
                    switch (LA(1)) {
                    case SEMI: {
                        match(SEMI);
                        slots(terms);
                        break;
                    }
                    case RPAREN:
                    case BANG:
                    case RBRACK: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(LT(1), getFilename());
                    }
                    }
                }
                {
                    switch (LA(1)) {
                    case BANG: {
                        srest(terms);
                        break;
                    }
                    case RPAREN:
                    case RBRACK: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(LT(1), getFilename());
                    }
                    }
                }
                break;
            }
            case BANG: {
                srest(terms);
                break;
            }
            default:
                if ((_tokenSet_2.member(LA(1))) && (_tokenSet_11.member(LA(2)))) {
                    pos(terms);
                    {
                        switch (LA(1)) {
                        case PIPE: {
                            prest(terms);
                            break;
                        }
                        case RPAREN:
                        case SEMI:
                        case BANG:
                        case RBRACK: {
                            break;
                        }
                        default: {
                            throw new NoViableAltException(LT(1), getFilename());
                        }
                        }
                    }
                    {
                        switch (LA(1)) {
                        case SEMI: {
                            match(SEMI);
                            slots(terms);
                            break;
                        }
                        case RPAREN:
                        case BANG:
                        case RBRACK: {
                            break;
                        }
                        default: {
                            throw new NoViableAltException(LT(1), getFilename());
                        }
                        }
                    }
                    {
                        switch (LA(1)) {
                        case BANG: {
                            srest(terms);
                            break;
                        }
                        case RPAREN:
                        case RBRACK: {
                            break;
                        }
                        default: {
                            throw new NoViableAltException(LT(1), getFilename());
                        }
                        }
                    }
                } else if ((LA(1) == SYMBOL || LA(1) == QSYMBOL) &&
                           (LA(2) == ARROW)) {
                    slots(terms);
                    {
                        switch (LA(1)) {
                        case RPAREN:
                        case PIPE:
                        case BANG:
                        case RBRACK: {
                            {
                                {
                                    switch (LA(1)) {
                                    case PIPE: {
                                        prest(terms);
                                        {
                                            switch (LA(1)) {
                                            case SEMI: {
                                                match(SEMI);
                                                slots(terms);
                                                break;
                                            }
                                            case RPAREN:
                                            case BANG:
                                            case RBRACK: {
                                                break;
                                            }
                                            default: {
                                                throw new NoViableAltException(
                                                        LT(1), getFilename());
                                            }
                                            }
                                        }
                                        break;
                                    }
                                    case RPAREN:
                                    case BANG:
                                    case RBRACK: {
                                        break;
                                    }
                                    default: {
                                        throw new NoViableAltException(LT(1),
                                                getFilename());
                                    }
                                    }
                                }
                                {
                                    switch (LA(1)) {
                                    case BANG: {
                                        srest(terms);
                                        break;
                                    }
                                    case RPAREN:
                                    case RBRACK: {
                                        break;
                                    }
                                    default: {
                                        throw new NoViableAltException(LT(1),
                                                getFilename());
                                    }
                                    }
                                }
                            }
                            break;
                        }
                        case SEMI: {
                            {
                                match(SEMI);
                                pos(terms);
                                {
                                    switch (LA(1)) {
                                    case PIPE: {
                                        prest(terms);
                                        break;
                                    }
                                    case RPAREN:
                                    case SEMI:
                                    case BANG:
                                    case RBRACK: {
                                        break;
                                    }
                                    default: {
                                        throw new NoViableAltException(LT(1),
                                                getFilename());
                                    }
                                    }
                                }
                                {
                                    switch (LA(1)) {
                                    case SEMI: {
                                        match(SEMI);
                                        slots(terms);
                                        break;
                                    }
                                    case RPAREN:
                                    case BANG:
                                    case RBRACK: {
                                        break;
                                    }
                                    default: {
                                        throw new NoViableAltException(LT(1),
                                                getFilename());
                                    }
                                    }
                                }
                                {
                                    switch (LA(1)) {
                                    case BANG: {
                                        srest(terms);
                                        break;
                                    }
                                    case RPAREN:
                                    case RBRACK: {
                                        break;
                                    }
                                    default: {
                                        throw new NoViableAltException(LT(1),
                                                getFilename());
                                    }
                                    }
                                }
                            }
                            break;
                        }
                        default: {
                            throw new NoViableAltException(LT(1), getFilename());
                        }
                        }
                    }
                } else {
                    throw new NoViableAltException(LT(1), getFilename());
                }
            }
        } catch (RecognitionException ex) {
            if (inputState.guessing == 0) {
                reportError(ex);
                recover(ex, _tokenSet_12);
            } else {
                throw ex;
            }
        }
    }

    public final void pos(
            Vector terms
            ) throws RecognitionException, TokenStreamException {

        Term t;

        try { // for error handling
            t = term();
            if (inputState.guessing == 0) {
                terms.add(t);
            }
            {
                _loop44:
                        do {
                    if ((LA(1) == COMMA)) {
                        match(COMMA);
                        t = term();
                        if (inputState.guessing == 0) {
                            terms.add(t);
                        }
                    } else {
                        break _loop44;
                    }

                } while (true);
            }
        } catch (RecognitionException ex) {
            if (inputState.guessing == 0) {
                reportError(ex);
                recover(ex, _tokenSet_13);
            } else {
                throw ex;
            }
        }
    }

    public final void prest(
            Vector terms
            ) throws RecognitionException, TokenStreamException {

        Term v, p;

        try { // for error handling
            match(PIPE);
            {
                switch (LA(1)) {
                case QMARK: {
                    v = var();
                    if (inputState.guessing == 0) {

                        v.role = SymbolTable.IPREST;
                        terms.add(v);

                    }
                    break;
                }
                case LBRACK: {
                    p = posplex();
                    if (inputState.guessing == 0) {

                        p.role = SymbolTable.IPREST;
                        terms.add(p);

                    }
                    break;
                }
                default: {
                    throw new NoViableAltException(LT(1), getFilename());
                }
                }
            }
        } catch (RecognitionException ex) {
            if (inputState.guessing == 0) {
                reportError(ex);
                recover(ex, _tokenSet_14);
            } else {
                throw ex;
            }
        }
    }

    public final void slots(
            Vector terms
            ) throws RecognitionException, TokenStreamException {

        Term s;

        try { // for error handling
            s = slot();
            if (inputState.guessing == 0) {
                terms.add(s);
            }
            {
                _loop47:
                        do {
                    if ((LA(1) == SEMI) && (LA(2) == SYMBOL || LA(2) == QSYMBOL) &&
                        (LA(3) == ARROW)) {
                        match(SEMI);
                        s = slot();
                        if (inputState.guessing == 0) {
                            terms.add(s);
                        }
                    } else {
                        break _loop47;
                    }

                } while (true);
            }
        } catch (RecognitionException ex) {
            if (inputState.guessing == 0) {
                reportError(ex);
                recover(ex, _tokenSet_13);
            } else {
                throw ex;
            }
        }
    }

    public final void srest(
            Vector terms
            ) throws RecognitionException, TokenStreamException {

        Term v, p;

        try { // for error handling
            match(BANG);
            {
                switch (LA(1)) {
                case QMARK: {
                    v = var();
                    if (inputState.guessing == 0) {

                        v.role = SymbolTable.IREST;
                        terms.add(v);

                    }
                    break;
                }
                case LBRACK: {
                    p = slotplex();
                    if (inputState.guessing == 0) {

                        p.role = SymbolTable.IREST;
                        terms.add(p);

                    }
                    break;
                }
                default: {
                    throw new NoViableAltException(LT(1), getFilename());
                }
                }
            }
        } catch (RecognitionException ex) {
            if (inputState.guessing == 0) {
                reportError(ex);
                recover(ex, _tokenSet_12);
            } else {
                throw ex;
            }
        }
    }

    public final Term term() throws RecognitionException, TokenStreamException {
        Term t = null;

        try { // for error handling
            switch (LA(1)) {
            case QMARK: {
                t = var();
                break;
            }
            case USCORE: {
                t = skolem();
                break;
            }
            case LBRACK: {
                t = plex();
                break;
            }
            default:
                if ((LA(1) == SYMBOL || LA(1) == QSYMBOL) &&
                    (_tokenSet_15.member(LA(2)))) {
                    t = ind();
                } else if ((LA(1) == SYMBOL || LA(1) == QSYMBOL) &&
                           (LA(2) == LBRACK)) {
                    t = cterm();
                } else {
                    throw new NoViableAltException(LT(1), getFilename());
                }
            }
        } catch (RecognitionException ex) {
            if (inputState.guessing == 0) {
                reportError(ex);
                recover(ex, _tokenSet_16);
            } else {
                throw ex;
            }
        }
        return t;
    }

    public final Term var() throws RecognitionException, TokenStreamException {
        Term v = null;

        String vname = "";
        int t;

        try { // for error handling
            match(QMARK);
            {
                switch (LA(1)) {
                case SYMBOL:
                case QSYMBOL: {
                    vname = symbol();
                    break;
                }
                case COMMA:
                case RPAREN:
                case SEMI:
                case HAT:
                case PIPE:
                case BANG:
                case RBRACK:
                case COLON: {
                    break;
                }
                default: {
                    throw new NoViableAltException(LT(1), getFilename());
                }
                }
            }
            if (inputState.guessing == 0) {

                String sym;
                if (vname.equals("")) {
                    sym = "$ANON" + aid++;
                } else {
                    sym = vname;
                }
                v = new Term(internVar(sym), SymbolTable.INOROLE, Types.IOBJECT);

            }
            {
                switch (LA(1)) {
                case COLON: {
                    match(COLON);
                    t = type();
                    if (inputState.guessing == 0) {
                        v.type = t;
                    }
                    break;
                }
                case COMMA:
                case RPAREN:
                case SEMI:
                case HAT:
                case PIPE:
                case BANG:
                case RBRACK: {
                    break;
                }
                default: {
                    throw new NoViableAltException(LT(1), getFilename());
                }
                }
            }
            if (inputState.guessing == 0) {

                Integer symI = v.symbol;
                Integer typeI = v.type;

                Vector v2;

                if (varClasses.containsKey(symI)) {
                    v2 = (Vector) varClasses.get(symI);
                } else {
                    v2 = new Vector();
                    varClasses.put(symI, v2);
                }

                v2.add(typeI);

            }
        } catch (RecognitionException ex) {
            if (inputState.guessing == 0) {
                reportError(ex);
                recover(ex, _tokenSet_16);
            } else {
                throw ex;
            }
        }
        return v;
    }

    public final Term posplex() throws RecognitionException,
            TokenStreamException {
        Term pp = null;

        Vector params = new Vector();

        try { // for error handling
            match(LBRACK);
            {
                switch (LA(1)) {
                case LBRACK:
                case USCORE:
                case QMARK:
                case SYMBOL:
                case QSYMBOL: {
                    pos(params);
                    break;
                }
                case PIPE:
                case RBRACK: {
                    break;
                }
                default: {
                    throw new NoViableAltException(LT(1), getFilename());
                }
                }
            }
            {
                switch (LA(1)) {
                case PIPE: {
                    prest(params);
                    break;
                }
                case RBRACK: {
                    break;
                }
                default: {
                    throw new NoViableAltException(LT(1), getFilename());
                }
                }
            }
            match(RBRACK);
            if (inputState.guessing == 0) {

                pp = new Term(SymbolTable.IPLEX, SymbolTable.INOROLE,
                              Types.IOBJECT, params);

            }
        } catch (RecognitionException ex) {
            if (inputState.guessing == 0) {
                reportError(ex);
                recover(ex, _tokenSet_14);
            } else {
                throw ex;
            }
        }
        return pp;
    }

    public final Term slotplex() throws RecognitionException,
            TokenStreamException {
        Term sp = null;

        Vector params = new Vector();

        try { // for error handling
            match(LBRACK);
            {
                switch (LA(1)) {
                case SYMBOL:
                case QSYMBOL: {
                    slots(params);
                    break;
                }
                case BANG:
                case RBRACK: {
                    break;
                }
                default: {
                    throw new NoViableAltException(LT(1), getFilename());
                }
                }
            }
            {
                switch (LA(1)) {
                case BANG: {
                    srest(params);
                    break;
                }
                case RBRACK: {
                    break;
                }
                default: {
                    throw new NoViableAltException(LT(1), getFilename());
                }
                }
            }
            match(RBRACK);
            if (inputState.guessing == 0) {

                sp = new Term(SymbolTable.IPLEX, SymbolTable.INOROLE,
                              Types.IOBJECT, params);

            }
        } catch (RecognitionException ex) {
            if (inputState.guessing == 0) {
                reportError(ex);
                recover(ex, _tokenSet_12);
            } else {
                throw ex;
            }
        }
        return sp;
    }

    public final Term slot() throws RecognitionException, TokenStreamException {
        Term s = null;

        int r;

        try { // for error handling
            r = role();
            match(ARROW);
            s = term();
            if (inputState.guessing == 0) {

                s.role = r;

            }
        } catch (RecognitionException ex) {
            if (inputState.guessing == 0) {
                reportError(ex);
                recover(ex, _tokenSet_13);
            } else {
                throw ex;
            }
        }
        return s;
    }

    public final int role() throws RecognitionException, TokenStreamException {
        int r = -1;

        String sym;

        try { // for error handling
            sym = symbol();
            if (inputState.guessing == 0) {

                r = SymbolTable.internRole(sym);

            }
        } catch (RecognitionException ex) {
            if (inputState.guessing == 0) {
                reportError(ex);
                recover(ex, _tokenSet_17);
            } else {
                throw ex;
            }
        }
        return r;
    }

    public final Term ind() throws RecognitionException, TokenStreamException {
        Term i = null;

        String sym;
        int symid, t;

        try { // for error handling
            sym = symbol();
            if (inputState.guessing == 0) {

                symid = SymbolTable.internSymbol(sym);
                i = new Term(symid, SymbolTable.INOROLE, Types.IOBJECT);

            }
            {
                switch (LA(1)) {
                case COLON: {
                    match(COLON);
                    t = type();
                    if (inputState.guessing == 0) {
                        i.type = t;
                    }
                    break;
                }
                case COMMA:
                case RPAREN:
                case SEMI:
                case HAT:
                case PIPE:
                case BANG:
                case RBRACK: {
                    break;
                }
                default: {
                    throw new NoViableAltException(LT(1), getFilename());
                }
                }
            }
        } catch (RecognitionException ex) {
            if (inputState.guessing == 0) {
                reportError(ex);
                recover(ex, _tokenSet_16);
            } else {
                throw ex;
            }
        }
        return i;
    }

    public final Term cterm() throws RecognitionException, TokenStreamException {
        Term ct = null;

        Vector params = new Vector();
        int c, t;

        try { // for error handling
            c = ctor();
            match(LBRACK);
            {
                switch (LA(1)) {
                case PIPE:
                case BANG:
                case LBRACK:
                case USCORE:
                case QMARK:
                case SYMBOL:
                case QSYMBOL: {
                    ps(params);
                    break;
                }
                case RBRACK: {
                    break;
                }
                default: {
                    throw new NoViableAltException(LT(1), getFilename());
                }
                }
            }
            match(RBRACK);
            if (inputState.guessing == 0) {

                ct = new Term(c, SymbolTable.INOROLE, Types.IOBJECT, params);

            }
            {
                switch (LA(1)) {
                case COLON: {
                    match(COLON);
                    t = type();
                    if (inputState.guessing == 0) {
                        ct.type = t;
                    }
                    break;
                }
                case COMMA:
                case RPAREN:
                case SEMI:
                case HAT:
                case PIPE:
                case BANG:
                case RBRACK: {
                    break;
                }
                default: {
                    throw new NoViableAltException(LT(1), getFilename());
                }
                }
            }
        } catch (RecognitionException ex) {
            if (inputState.guessing == 0) {
                reportError(ex);
                recover(ex, _tokenSet_16);
            } else {
                throw ex;
            }
        }
        return ct;
    }

    public final Term skolem() throws RecognitionException,
            TokenStreamException {
        Term sko = null;

        String skoname = "";
        String sym = "";
        int t;

        try { // for error handling
            match(USCORE);
            {
                switch (LA(1)) {
                case SYMBOL:
                case QSYMBOL: {
                    skoname = symbol();
                    break;
                }
                case COMMA:
                case RPAREN:
                case SEMI:
                case HAT:
                case PIPE:
                case BANG:
                case RBRACK:
                case COLON: {
                    break;
                }
                default: {
                    throw new NoViableAltException(LT(1), getFilename());
                }
                }
            }
            if (inputState.guessing == 0) {

                if (skoname.equals("")) {
                    sym = "$gensym" + SymbolTable.genid++;
                } else {
                    if (this.skolemMap.containsKey(skoname)) {
                        sym = (String) skolemMap.get(skoname);
                    } else {
                        sym = "$gensym" + (SymbolTable.genid++) + "$" + skoname;
                        skolemMap.put(skoname, sym);
                    }
                }

                sko = new Term(SymbolTable.internSymbol(sym),
                               SymbolTable.INOROLE, Types.IOBJECT);

            }
            {
                switch (LA(1)) {
                case COLON: {
                    match(COLON);
                    t = type();
                    if (inputState.guessing == 0) {
                        sko.type = t;
                    }
                    break;
                }
                case COMMA:
                case RPAREN:
                case SEMI:
                case HAT:
                case PIPE:
                case BANG:
                case RBRACK: {
                    break;
                }
                default: {
                    throw new NoViableAltException(LT(1), getFilename());
                }
                }
            }
        } catch (RecognitionException ex) {
            if (inputState.guessing == 0) {
                reportError(ex);
                recover(ex, _tokenSet_16);
            } else {
                throw ex;
            }
        }
        return sko;
    }

    public final Term plex() throws RecognitionException, TokenStreamException {
        Term p = null;

        Vector params = new Vector();

        try { // for error handling
            match(LBRACK);
            {
                switch (LA(1)) {
                case PIPE:
                case BANG:
                case LBRACK:
                case USCORE:
                case QMARK:
                case SYMBOL:
                case QSYMBOL: {
                    ps(params);
                    break;
                }
                case RBRACK: {
                    break;
                }
                default: {
                    throw new NoViableAltException(LT(1), getFilename());
                }
                }
            }
            match(RBRACK);
            if (inputState.guessing == 0) {

                p = new Term(SymbolTable.IPLEX, SymbolTable.INOROLE,
                             Types.IOBJECT, params);

            }
        } catch (RecognitionException ex) {
            if (inputState.guessing == 0) {
                reportError(ex);
                recover(ex, _tokenSet_16);
            } else {
                throw ex;
            }
        }
        return p;
    }

    public final int ctor() throws RecognitionException, TokenStreamException {
        int c = -1;

        String sym;

        try { // for error handling
            sym = symbol();
            if (inputState.guessing == 0) {

                c = SymbolTable.internSymbol(sym);

            }
        } catch (RecognitionException ex) {
            if (inputState.guessing == 0) {
                reportError(ex);
                recover(ex, _tokenSet_18);
            } else {
                throw ex;
            }
        }
        return c;
    }

    public final int type() throws RecognitionException, TokenStreamException {
        int t = -1;

        String sym;

        try { // for error handling
            sym = symbol();
            if (inputState.guessing == 0) {

                t = Types.typeID(sym);

            }
        } catch (RecognitionException ex) {
            if (inputState.guessing == 0) {
                reportError(ex);
                recover(ex, _tokenSet_16);
            } else {
                throw ex;
            }
        }
        return t;
    }

    public final String symbol() throws RecognitionException,
            TokenStreamException {
        String sym = null;

        Token s = null;
        Token qs = null;

        try { // for error handling
            switch (LA(1)) {
            case SYMBOL: {
                s = LT(1);
                match(SYMBOL);
                if (inputState.guessing == 0) {
                    sym = s.getText();
                }
                break;
            }
            case QSYMBOL: {
                qs = LT(1);
                match(QSYMBOL);
                if (inputState.guessing == 0) {
                    sym = qs.getText();
                    sym = sym.substring(1, sym.length() - 1);
                }
                break;
            }
            default: {
                throw new NoViableAltException(LT(1), getFilename());
            }
            }
        } catch (RecognitionException ex) {
            if (inputState.guessing == 0) {
                reportError(ex);
                recover(ex, _tokenSet_19);
            } else {
                throw ex;
            }
        }
        return sym;
    }

    public final String uri() throws RecognitionException, TokenStreamException {
        String sym = null;

        Token u = null;

        try { // for error handling
            u = LT(1);
            match(URI);
            if (inputState.guessing == 0) {
                sym = u.getText();
            }
        } catch (RecognitionException ex) {
            if (inputState.guessing == 0) {
                reportError(ex);
                recover(ex, _tokenSet_0);
            } else {
                throw ex;
            }
        }
        return sym;
    }


    public static final String[] _tokenNames = {
                                               "<0>",
                                               "EOF",
                                               "<2>",
                                               "NULL_TREE_LOOKAHEAD",
                                               "IMP",
                                               "PERIOD",
                                               "COMMA",
                                               "LPAREN",
                                               "RPAREN",
                                               "SEMI",
                                               "HAT",
                                               "PIPE",
                                               "BANG",
                                               "LBRACK",
                                               "RBRACK",
                                               "ARROW",
                                               "COLON",
                                               "USCORE",
                                               "QMARK",
                                               "SYMBOL",
                                               "QSYMBOL",
                                               "URI",
                                               "LBRACE",
                                               "RBRACE",
                                               "COMMENT",
                                               "MLCOMMENT",
                                               "WS"
    };

    private static final long[] mk_tokenSet_0() {
        long[] data = {2L, 0L};
        return data;
    }

    public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
    private static final long[] mk_tokenSet_1() {
        long[] data = {1573122L, 0L};
        return data;
    }

    public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
    private static final long[] mk_tokenSet_2() {
        long[] data = {1974272L, 0L};
        return data;
    }

    public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
    private static final long[] mk_tokenSet_3() {
        long[] data = {2063360L, 0L};
        return data;
    }

    public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
    private static final long[] mk_tokenSet_4() {
        long[] data = {2096960L, 0L};
        return data;
    }

    public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
    private static final long[] mk_tokenSet_5() {
        long[] data = {1980672L, 0L};
        return data;
    }

    public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
    private static final long[] mk_tokenSet_6() {
        long[] data = {2095984L, 0L};
        return data;
    }

    public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
    private static final long[] mk_tokenSet_7() {
        long[] data = {2095986L, 0L};
        return data;
    }

    public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
    private static final long[] mk_tokenSet_8() {
        long[] data = {368L, 0L};
        return data;
    }

    public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
    private static final long[] mk_tokenSet_9() {
        long[] data = {288L, 0L};
        return data;
    }

    public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
    private static final long[] mk_tokenSet_10() {
        long[] data = {128L, 0L};
        return data;
    }

    public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
    private static final long[] mk_tokenSet_11() {
        long[] data = {2063168L, 0L};
        return data;
    }

    public static final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
    private static final long[] mk_tokenSet_12() {
        long[] data = {16640L, 0L};
        return data;
    }

    public static final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());
    private static final long[] mk_tokenSet_13() {
        long[] data = {23296L, 0L};
        return data;
    }

    public static final BitSet _tokenSet_13 = new BitSet(mk_tokenSet_13());
    private static final long[] mk_tokenSet_14() {
        long[] data = {21248L, 0L};
        return data;
    }

    public static final BitSet _tokenSet_14 = new BitSet(mk_tokenSet_14());
    private static final long[] mk_tokenSet_15() {
        long[] data = {89920L, 0L};
        return data;
    }

    public static final BitSet _tokenSet_15 = new BitSet(mk_tokenSet_15());
    private static final long[] mk_tokenSet_16() {
        long[] data = {24384L, 0L};
        return data;
    }

    public static final BitSet _tokenSet_16 = new BitSet(mk_tokenSet_16());
    private static final long[] mk_tokenSet_17() {
        long[] data = {32768L, 0L};
        return data;
    }

    public static final BitSet _tokenSet_17 = new BitSet(mk_tokenSet_17());
    private static final long[] mk_tokenSet_18() {
        long[] data = {8192L, 0L};
        return data;
    }

    public static final BitSet _tokenSet_18 = new BitSet(mk_tokenSet_18());
    private static final long[] mk_tokenSet_19() {
        long[] data = {131008L, 0L};
        return data;
    }

    public static final BitSet _tokenSet_19 = new BitSet(mk_tokenSet_19());

}
