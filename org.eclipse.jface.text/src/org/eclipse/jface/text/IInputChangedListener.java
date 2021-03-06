/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jface.text;


/**
 * A listener which is notified when the target's input changes.
 * <p>
 * Clients can implement that interface and its extension interfaces.</p>
 *
 * @since 3.4
 */
public interface IInputChangedListener {

	/**
	 * Called when a the input has changed.
	 *
	 * @param newInput the new input, or <code>null</code> iff the listener should not show any new input
	 */
	void inputChanged(Object newInput);
}
