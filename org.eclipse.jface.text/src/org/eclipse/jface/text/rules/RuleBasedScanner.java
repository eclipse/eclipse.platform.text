/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.jface.text.rules;


import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;


/**
 * A generic scanner which can be "programmed" with a sequence of rules.
 * The scanner is used to get the next token by evaluating its rule in sequence until
 * one is successful. If a rule returns a token which is undefined, the scanner will proceed to 
 * the next rule. Otherwise the token provided by the rule will be returned by 
 * the scanner. If no rule returned a defined token, this scanner returns a token
 * which returns <code>true</code> when calling <code>isOther</code>, unless the end 
 * of the file is reached. In this case the token returns <code>true</code> when calling
 * <code>isEOF</code>.
 *
 * @see IRule
 */
public class RuleBasedScanner implements ICharacterScanner, ITokenScanner {
	
	/** The list of rules of this scanner */
	protected IRule[] fRules;
	/** The token to be returned by default if no rule fires */
	protected IToken fDefaultReturnToken;
	/** The document to be scanned */
	protected IDocument fDocument;
	/** The cached legal line delimiters of the document */
	protected char[][] fDelimiters;
	/** The offset of the next character to be read */
	protected int fOffset;
	/** The end offset of the range to be scanned */
	protected int fRangeEnd;
	/** The offset of the last read token */
	protected int fTokenOffset;
	/** The cached column of the current scanner position */
	protected int fColumn;
	/** Internal setting for the uninitialized column cache. */
	protected static final int UNDEFINED= -1;
	
	/**
	 * Creates a new rule based scanner which does not have any rule.
	 */
	public RuleBasedScanner() {
	}
	
	/**
	 * Configures the scanner with the given sequence of rules.
	 *
	 * @param rules the sequence of rules controlling this scanner
	 */
	public void setRules(IRule[] rules) {
		if (rules != null) {
			fRules= new IRule[rules.length];
			System.arraycopy(rules, 0, fRules, 0, rules.length);
		} else 
			fRules= null;
	}
	
	/**
	 * Configures the scanner's default return token. This is the token
	 * which is returned when non of the rules fired and EOF has not been
	 * reached.
	 * 
	 * @param defaultReturnToken the default return token
	 * @since 2.0
	 */
	public void setDefaultReturnToken(IToken defaultReturnToken) {
		Assert.isNotNull(defaultReturnToken.getData());
		fDefaultReturnToken= defaultReturnToken;
	}
	
	/*
	 * @see ITokenScanner#setRange(IDocument, int, int)
	 */
	public void setRange(IDocument document, int offset, int length) {
		
		fDocument= document;
		fOffset= offset;
		fColumn= UNDEFINED;
		fRangeEnd= Math.min(fDocument.getLength(), offset + length);
		
		String[] delimiters= fDocument.getLegalLineDelimiters();
		fDelimiters= new char[delimiters.length][];
		for (int i= 0; i < delimiters.length; i++)
			fDelimiters[i]= delimiters[i].toCharArray();
			
		if (fDefaultReturnToken == null)
			fDefaultReturnToken= new Token(null);
	}
	
	/*
	 * @see ITokenScanner#getTokenOffset()
	 */
	public int getTokenOffset() {
		return fTokenOffset;
	}
	
	/*
	 * @see ITokenScanner#getTokenLength()
	 */
	public int getTokenLength() {
		if (fOffset < fRangeEnd)
			return fOffset - getTokenOffset();
		return fRangeEnd - getTokenOffset();
	}
	

	/*
	 * @see ICharacterScanner#getColumn()
	 */
	public int getColumn() {
		if (fColumn == UNDEFINED) {
			try {
				int line= fDocument.getLineOfOffset(fOffset);
				int start= fDocument.getLineOffset(line);
				
				fColumn= fOffset - start;
				
			} catch (BadLocationException ex) {
			}
		}
		return fColumn;
	}
	
	/*
	 * @see ICharacterScanner#getLegalLineDelimiters()
	 */
	public char[][] getLegalLineDelimiters() {
		return fDelimiters;
	}
	
	/*
	 * @see ITokenScanner#nextToken()
	 */
	public IToken nextToken() {
		
		fTokenOffset= fOffset;
		fColumn= UNDEFINED;
		
		if (fRules != null) {
			for (int i= 0; i < fRules.length; i++) {
				IToken token= (fRules[i].evaluate(this));
				if (!token.isUndefined())
					return token;
			}
		}
		
		if (read() == EOF)
			return Token.EOF;
		return fDefaultReturnToken;
	}
	
	/*
	 * @see ICharacterScanner#read()
	 */
	public int read() {
		
		try {
			
			if (fOffset < fRangeEnd) {
				try {
					return fDocument.getChar(fOffset);
				} catch (BadLocationException e) {
				}
			}
			
			return EOF;
		
		} finally {
			++ fOffset;
			fColumn= UNDEFINED;
		}
	}
	
	/*
	 * @see ICharacterScanner#unread()
	 */
	public void unread() {
	    	--fOffset;
	}
}


