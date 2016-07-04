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
package se.havochvatten.vessel.proxy.message;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Observes;
import javax.jms.*;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMapperException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetFault;
import eu.europa.ec.fisheries.wsdl.asset.types.SingleAssetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.havochvatten.vessel.proxy.constant.Constants;
import se.havochvatten.vessel.proxy.dto.ProxyResponse;
import se.havochvatten.vessel.proxy.event.VesselProxyErrorEvent;
import se.havochvatten.vessel.proxy.event.VesselProxyMessageRecievedEvent;

/**
 **/
@LocalBean
@Stateless
public class VesselProxyMessageSender {

    final static Logger LOG = LoggerFactory.getLogger(VesselProxyMessageSender.class);

    @Resource(lookup = Constants.CONNECTION_FACTORY)
    private ConnectionFactory connectionFactory;

    private Connection connection = null;
    private Session session = null;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void sendMessage(@Observes @VesselProxyMessageRecievedEvent ProxyResponse message) {
        try {
            LOG.info("Sending message back from VessselProxy [ National register ] to recipient om JMS Queue {} with correlationID: {}.", message.getJmsMessage().getJMSReplyTo(), message.getJmsMessage().getJMSMessageID());

            connectToQueue();

            SingleAssetResponse request = new SingleAssetResponse();
            request.setAsset(message.getResponse());
            
            String data = JAXBMarshaller.marshallJaxBObjectToString(request);
            
            TextMessage responseMessage = session.createTextMessage(data);
            responseMessage.setJMSCorrelationID(message.getJmsMessage().getJMSMessageID());

            getProducer(session, message.getJmsMessage().getJMSReplyTo()).send(responseMessage);

        } catch (JMSException | AssetModelMapperException e) {
            LOG.error("[ Error when sending reply back to recipient. ] {} {}", e.getMessage(), e.getStackTrace());
        } finally {
            try {
                connection.stop();
                connection.close();
            } catch (JMSException e) {
                LOG.error("[ Error when stopping or closing JMS queue. ] {} {}", e.getMessage(), e.getStackTrace());
            }
        }

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void sendErrorMessage(@Observes @VesselProxyErrorEvent ProxyResponse message) {
        try {
            LOG.info("Sending error message back from VessselProxy [ National register ] to recipient om JMS Queue {} with correlationID: {}.", message.getJmsMessage().getJMSReplyTo(), message.getJmsMessage().getJMSMessageID());

            connectToQueue();

            AssetFault request = new AssetFault();
            request.setFault(message.getErrorMessage());

            String data = JAXBMarshaller.marshallJaxBObjectToString(request);

            TextMessage response = session.createTextMessage(data);
            response.setJMSCorrelationID(message.getJmsMessage().getJMSMessageID());
            getProducer(session, message.getJmsMessage().getJMSReplyTo()).send(response);

        } catch (AssetModelMapperException | JMSException e) {
            LOG.error("[ Error when sending error message back to recipient. ] ", e);
        } finally {
            try {
                connection.stop();
                connection.close();
            } catch (JMSException e) {
                LOG.error("[ Error when stopping or closing JMS queue. ] ", e);
            }
        }

    }

    private void connectToQueue() throws JMSException {
        connection = connectionFactory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        connection.start();
    }

    private javax.jms.MessageProducer getProducer(Session session, Destination destination) throws JMSException {
        javax.jms.MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        producer.setTimeToLive(60000L);
        return producer;
    }

}