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
package org.eclipse.che.api.factory.server.jpa;

import com.google.inject.persist.Transactional;

import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.jdbc.jpa.DuplicateKeyException;
import org.eclipse.che.api.core.jdbc.jpa.IntegrityConstraintViolationException;
import org.eclipse.che.api.factory.server.model.impl.FactoryImpl;
import org.eclipse.che.api.factory.server.spi.FactoryDao;
import org.eclipse.che.commons.lang.Pair;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * @author Anton Korneta
 */
public class JpaFactoryDao implements FactoryDao {

    @Inject
    private Provider<EntityManager> managerProvider;

    @Override
    public FactoryImpl create(FactoryImpl factory) throws ConflictException, ServerException {
        requireNonNull(factory);
        try {
            return doCreate(factory);
        } catch (DuplicateKeyException ex) {
            throw new ConflictException(ex.getLocalizedMessage());
        } catch (IntegrityConstraintViolationException ex) {
            throw new ConflictException("Could not create factory with creator that refers on non-existent user");
        } catch (RuntimeException ex) {
            throw new ServerException(ex.getLocalizedMessage(), ex);
        }
    }

    @Override
    public FactoryImpl update(FactoryImpl update) throws NotFoundException, ConflictException, ServerException {
        requireNonNull(update);
        try {
            return doUpdate(update);
        } catch (DuplicateKeyException ex) {
            throw new ConflictException(ex.getLocalizedMessage());
        } catch (RuntimeException ex) {
            throw new ServerException(ex.getLocalizedMessage(), ex);
        }
    }

    @Override
    public void remove(String id) throws NotFoundException, ServerException {
        requireNonNull(id);
        try {
            doRemove(id);
        } catch (RuntimeException ex) {
            throw new ServerException(ex.getLocalizedMessage(), ex);
        }
    }

    @Override
    @Transactional
    public FactoryImpl getById(String id) throws NotFoundException, ServerException {
        requireNonNull(id);
        try {
            final FactoryImpl factory = managerProvider.get().find(FactoryImpl.class, id);
            if (factory == null) {
                throw new NotFoundException(format("Factory with id '%s' doesn't exist", id));
            }
            return factory;
        } catch (RuntimeException ex) {
            throw new ServerException(ex.getLocalizedMessage(), ex);
        }
    }

    @Override
    @Transactional
    public List<FactoryImpl> getByAttribute(int maxItems,
                                            int skipCount,
                                            List<Pair<String, String>> attributes) throws ServerException {

        return null;
    }

    @Transactional
    protected FactoryImpl doCreate(FactoryImpl factory) {
        final EntityManager manager = managerProvider.get();
        manager.persist(factory);
        return manager.find(FactoryImpl.class, factory.getId());
    }

    @Transactional
    protected FactoryImpl doUpdate(FactoryImpl update) throws NotFoundException {
        final EntityManager manager = managerProvider.get();
        if (manager.find(FactoryImpl.class, update.getId()) == null) {
            throw new NotFoundException(format("Could not update factory with id %s because it doesn't exist", update.getId()));
        }
        return manager.merge(update);
    }

    @Transactional
    protected void doRemove(String id) {
        final EntityManager manager = managerProvider.get();
        manager.remove(manager.find(FactoryImpl.class, id));
    }
}