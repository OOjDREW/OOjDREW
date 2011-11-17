// OO jDREW Version 0.93
// Copyright (c) 2007 Marcel Ball and Ben Craig
//
// This software is licensed under the LGPL (LESSER GENERAL PUBLIC LICENSE) License.
// Please see "license.txt" in the root directory of this software package for more details.
//
// Disclaimer: Please see disclaimer.txt in the root directory of this package.

package jdrew.oo.util;

/**
 * An exception that is thrown if there is an error parsing input correctly.
 * This is used in the SubsumesParser class if a parse exception occurs.
 *
 * <p>Title: OO jDREW</p>
 *
 * <p>Description: Reasoning Engine for the Semantic Web - Supporting OO RuleML
 * 0.88</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * @author Ben L. Craig
 * @version 0.93
 */
public class SubsumesException extends Exception {

	public SubsumesException() {
		super();
	}

	public SubsumesException(String message) {
		super(
				"Only subsumes facts are allowed.  \n More details in the following message: "
						+ '\n' + message);
	}

}
