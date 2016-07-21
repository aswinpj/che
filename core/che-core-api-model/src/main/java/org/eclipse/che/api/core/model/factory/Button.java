/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.api.core.model.factory;

/**
 * Describes factory button
 *
 * @author Anton Korneta
 */
public interface Button {

    enum ButtonType {
        logo, nologo
    }

    /**
     * @return Type of the button
     */
    ButtonType getType();

    /**
     * @return button attributes
     */
    ButtonAttributes getAttributes();
}