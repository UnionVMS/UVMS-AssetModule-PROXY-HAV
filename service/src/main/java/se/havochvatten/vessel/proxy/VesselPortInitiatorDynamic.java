/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package se.havochvatten.vessel.proxy;

import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.ws.BindingProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.havochvatten.service.client.vesselws.v2_1.VesselPortType;
import se.havochvatten.service.client.vesselws.v2_1.VesselService;
import se.havochvatten.vessel.proxy.constant.ParameterKey;
import se.havochvatten.vessel.proxy.exception.VesselProxyException;

/**
 **/
@Stateless
public class VesselPortInitiatorDynamic implements VesselPortInitiator {
    final static Logger LOG = LoggerFactory.getLogger(VesselPortInitiatorDynamic.class);

    @EJB
    ParameterService parameterService;

    @Override
    public VesselPortType getVesselPort() throws VesselProxyException {
    	LOG.info("Initiating vessel port.");
        final VesselService service = new VesselService();
        VesselPortType port = service.getVesselPortType();
        BindingProvider bp = (BindingProvider) port;
        Map<String, Object> context = bp.getRequestContext();
        String endpointAddress = parameterService.getStringValue(ParameterKey.NATIONAL_SERVICE_ENDPOINT);
        context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
        return port;
    }
}