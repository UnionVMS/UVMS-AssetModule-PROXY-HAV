/*
﻿﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.
 
This file is part of the Integrated Data Fisheries Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a copy
of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package se.havochvatten.vessel.proxy.bean;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.havochvatten.vessel.proxy.ParameterService;
import se.havochvatten.vessel.proxy.constant.Constants;
import se.havochvatten.vessel.proxy.constant.ParameterKey;
import se.havochvatten.vessel.proxy.entity.Parameter;
import se.havochvatten.vessel.proxy.exception.VesselProxyException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelException;

@Stateless
public class ParameterServiceBean implements ParameterService {

    @PersistenceContext(unitName = "proxyPU")
    EntityManager em;

    final static Logger LOG = LoggerFactory.getLogger(ParameterServiceBean.class);

    @Override
    public String getStringValue(ParameterKey key) throws VesselProxyException {
        try {
            Query query = em.createNamedQuery(Constants.FIND_BY_NAME);
            query.setParameter("key", key.getKey());
            Parameter entity = (Parameter) query.getSingleResult();
            return entity.getParamValue();
        } catch (Exception e) {
            LOG.error("[ Error when getting string value from parameter key. ]");
            throw new VesselProxyException(e.getMessage());
        }
    }

    @Override
    public Boolean getBooleanValue(ParameterKey key) throws VesselProxyException {
        try {
            Query query = em.createNamedQuery(Constants.FIND_BY_NAME);
            query.setParameter("key", key.getKey());
            Parameter entity = (Parameter) query.getSingleResult();
            return parseBooleanValue(entity.getParamValue());
        } catch (AssetModelException e) {
            LOG.error("[ Error when getting boolean value from parameter key. ]");
            throw new VesselProxyException(e.getMessage());
        }
    }

    private Boolean parseBooleanValue(String value) throws InputArgumentException, AssetModelException {
        try {
            if (value.equalsIgnoreCase("true")) {
                return Boolean.TRUE;
            } else if (value.equalsIgnoreCase("false")) {
                return Boolean.FALSE;
            } else {
                throw new InputArgumentException("The String value provided does not equal boolean value, value provided = " + value);
            }
        } catch (Exception e) {
            LOG.error("[ Error when parsing boolean value from string. ] ");
            throw new AssetModelException(e.getMessage());
        }
    }
}