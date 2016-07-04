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
package se.havochvatten.vessel.proxy.bean;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.havochvatten.service.client.vesselws.v2_1.GetVesselByCFRResponse;
import se.havochvatten.service.client.vesselws.v2_1.GetVesselByIRCSResponse;
import se.havochvatten.service.client.vesselws.v2_1.GetVesselByIdResponse;
import se.havochvatten.service.client.vesselws.v2_1.VesselException;
import se.havochvatten.service.client.vesselws.v2_1.VesselPortType;
import se.havochvatten.vessel.proxy.VesselPortInitiator;
import se.havochvatten.vessel.proxy.VesselProxy;
import se.havochvatten.vessel.proxy.exception.VesselProxyException;
import se.havochvatten.vessel.proxy.exception.VesselProxyMappingException;
import se.havochvatten.vessel.proxy.mapper.VesselRequestMapper;
import se.havochvatten.vessel.proxy.mapper.VesselResponseMapper;

/**
 **/
@Stateless
public class VesselProxyBean implements VesselProxy {

    final static Logger LOG = LoggerFactory.getLogger(VesselProxyBean.class);

    @EJB(beanName="VesselPortInitiatorTimerStatic")
    VesselPortInitiator vesselPort;

    @Override
    public Asset getVesselByCFR(String cfr) throws VesselProxyException {
        try {
            LOG.info("Getting vessel by CFR from National register: {}.", cfr);

            VesselPortType port = vesselPort.getVesselPort();
            GetVesselByCFRResponse resp = port.getVesselByCFR(VesselRequestMapper.mapToGetByCfrRequest(cfr));
            return VesselResponseMapper.mapGetByCfrToDto(resp);
        } catch (VesselException e) {
            LOG.error("[ Error when getting vessel by CFR. ] ");
            throw new VesselProxyException("Error when retrieveing from Webservice, Error message: " + e.getMessage());
        } catch (VesselProxyMappingException e) {
            LOG.error("[ Error when getting vessel by CFR. ] ");
            throw new VesselProxyException("Error when mapping: " + e.getMessage());
        }
    }

    @Override
    public Asset getVesselById(String id) throws VesselProxyException {
        try {
            LOG.info("Getting vessel by ID from National register: {}.", id);

            VesselPortType port = vesselPort.getVesselPort();
            GetVesselByIdResponse resp = port.getVesselById(VesselRequestMapper.mapToGetByIdRequest(id));
            return VesselResponseMapper.mapGetByIdToDto(resp);
        } catch (VesselException e) {
            LOG.error("[ Error when getting vessel by ID. ] ");
            throw new VesselProxyException("Error when retrieveing from Webservice, Error message: " + e.getMessage());
        } catch (VesselProxyMappingException e) {
            LOG.error("[ Error when getting vessel by ID. ] ");
            throw new VesselProxyException("Error when mapping: " + e.getMessage());
        }
    }

    @Override
    public Asset getVesselByIRCS(String ircs) throws VesselProxyException {
        try {
            LOG.info("Getting vessel by IRCS from National register: {}.", ircs);

            VesselPortType port = vesselPort.getVesselPort();
            GetVesselByIRCSResponse resp = port.getVesselByIRCS(VesselRequestMapper.mapToGetByIRCSRequest(ircs));
            return VesselResponseMapper.mapGetByIrcsSToDto(resp);
        } catch (VesselException e) {
            LOG.error("[ Error when getting vessel by IRCS. ] ");
            throw new VesselProxyException("Error when retrieveing from Webservice, Error message: " + e.getMessage());
        } catch (VesselProxyMappingException e) {
            LOG.error("[ Error when getting vessel by IRCS. ] ");
            throw new VesselProxyException("Error when mapping: " + e.getMessage());
        }
    }

}