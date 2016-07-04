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
package se.havochvatten.vessel.proxy.message;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMapperException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.wsdl.asset.source.GetAssetRequest;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetDataSourceRequest;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.havochvatten.vessel.proxy.VesselProxy;
import se.havochvatten.vessel.proxy.constant.Constants;
import se.havochvatten.vessel.proxy.dto.ProxyResponse;
import se.havochvatten.vessel.proxy.event.VesselProxyErrorEvent;
import se.havochvatten.vessel.proxy.event.VesselProxyMessageRecievedEvent;
import se.havochvatten.vessel.proxy.exception.VesselProxyException;

import javax.ejb.*;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 **/
@MessageDriven(mappedName = Constants.QUEUE_VESSEL_NATIONAL, activationConfig = {
    @ActivationConfigProperty(propertyName = "messagingType", propertyValue = Constants.CONNECTION_TYPE),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = Constants.DESTINATION_TYPE_QUEUE),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = Constants.QUEUE_NAME_VESSEL_NATIONAL),
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
})
public class VesselProxyMessageReciever implements MessageListener {

    final static Logger LOG = LoggerFactory.getLogger(VesselProxyMessageReciever.class);

    @Inject
    @VesselProxyErrorEvent
    Event<ProxyResponse> errorEvent;

    @Inject
    @VesselProxyMessageRecievedEvent
    Event<ProxyResponse> responseRecievedEvent;

    @EJB
    VesselProxy vesselProxy;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void onMessage(Message message) {
        try {
            recvieveMessage(message);
        } catch (JMSException | VesselProxyException | AssetModelException e) {
            LOG.error("[ Error in reciving in VesselProxy National. ] ");
        }
    }

    public void recvieveMessage(Message message) throws JMSException, VesselProxyException, AssetModelException, AssetModelMapperException {

        TextMessage textMessage = (TextMessage) message;
        LOG.info("Recieved message in National proxy.");

        try {

            AssetDataSourceRequest request = JAXBMarshaller.unmarshallTextMessage(textMessage, AssetDataSourceRequest.class);

            switch (request.getMethod()) {
                case GET:
                    GetAssetRequest getRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, GetAssetRequest.class);
                    AssetId vesselId = getRequest.getId();
                    getVessel(textMessage, vesselId.getType(), vesselId.getValue());
                    break;
                case PING:
                case CREATE:
                case DELETE:
                case GROUP_CREATE:
                case GROUP_DELETE:
                case GROUP_GET:
                case GROUP_LIST:
                case GROUP_UPDATE:
                case HISTORY_GET:
                case HISTORY_LIST:
                case LIST:
                case LIST_GET_BY_GROUP:
                case UPDATE:
                case UPSERT:
                    errorEvent.fire(new ProxyResponse(message, "Method " + request.getMethod().name() + " not implemented!"));
                    LOG.error("[ Error, method {} not implemented. ]", request.getMethod().name());
                    throw new VesselProxyException("Method " + request.getMethod().name() + " implemented!");
                default:
                    errorEvent.fire(new ProxyResponse(message, "Method " + request.getMethod().name() + " not implemented!"));
                    LOG.error("[ Error, method {} not implemented. ]", request.getMethod().name());
                    throw new VesselProxyException("Method " + request.getMethod().name() + " not implemented!");
            }

        } catch (JMSException e) {
            LOG.error("[ Error when receiving message. ] ");
            throw new VesselProxyException(e.getMessage());
        }

    }

    public void getVessel(TextMessage message, AssetIdType queryType, String data) throws JMSException, VesselProxyException {
        try {
            Asset response = null;

            switch (queryType) {
                case CFR:
                    response = vesselProxy.getVesselByCFR(data);
                    break;
                case INTERNAL_ID:
                    response = vesselProxy.getVesselById(data);
                    break;
                case IRCS:
                    response = vesselProxy.getVesselByIRCS(data);
                    break;
                case MMSI:
                case IMO:
                    throw new VesselProxyException("Getter with ID type  " + queryType.name() + " not implemented!");
                default:
                    throw new VesselProxyException("Getter with ID type  " + queryType.name() + " not implemented!");
            }
            
            ProxyResponse proxyResponse = new ProxyResponse(message, response);
            responseRecievedEvent.fire(proxyResponse);

        } catch (VesselProxyException e) {
            errorEvent.fire(new ProxyResponse(message, e.getMessage()));
            LOG.error("[ Error when getting vessel. ] ");
            throw new VesselProxyException(e.getMessage());
        }
    }

}