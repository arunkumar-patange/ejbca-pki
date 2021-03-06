/*************************************************************************
 *                                                                       *
 *  EJBCA Community: The OpenSource Certificate Authority                *
 *                                                                       *
 *  This software is free software; you can redistribute it and/or       *
 *  modify it under the terms of the GNU Lesser General Public           *
 *  License as published by the Free Software Foundation; either         *
 *  version 2.1 of the License, or any later version.                    *
 *                                                                       *
 *  See terms of license at gnu.org.                                     *
 *                                                                       *
 *************************************************************************/
package org.ejbca.core.ejb.services;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.ejb.Local;
import javax.ejb.Timer;

import org.ejbca.core.model.services.IWorker;

/**
 * Local interface for ServiceSession.
 * @version $Id: ServiceSessionLocal.java 19902 2014-09-30 14:32:24Z anatom $
 */
@Local
public interface ServiceSessionLocal extends ServiceSession {

    /** @return HashMap mapping service id (Integer) to service name (String). */
    HashMap<Integer, String> getServiceIdToNameMap();

    /**
     * Internal method used from load() to separate timer access from database
     * access transactions.
     */
	Map<Integer, Long> getNewServiceTimeouts(final HashSet<Serializable> existingTimers);
   
    /**
     * Return the configured interval for the specified worker or
     * IInterval.DONT_EXECUTE if it could not be found.
     */
	long getServiceInterval(final Integer serviceId);

    /**
     * Reads the current timeStamp values and tries to update them in a single transaction.
     * If the database commit is successful the method returns the worker, otherwise an
     * exception is thrown.
     * 
     * Should only be called from timeoutHandler
     */
	IWorker getWorkerIfItShouldRun(final Integer timerInfo, final long nextTimeout);

	/** Executes a the service in a separate in no transaction. */
	void executeServiceInNoTransaction(final IWorker worker, final String serviceName);
	
    /** Cancels a timer with the given Id. */
	void cancelTimer(final Integer id);
	
	/** The timeout method */
    void timeoutHandler(final Timer timer);

}
