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
package se.havochvatten.vessel.proxy.dto;

import eu.europa.ec.fisheries.wsdl.asset.types.Asset;

import javax.jms.Message;

/**
 **/
public class ProxyResponse {

    private Message jmsMessage;
    private Asset response;
    private String errorMessage;

    public ProxyResponse(Message message) {
        this.jmsMessage = message;
    }

    public ProxyResponse(Message jmsMessage, Asset response) {
        this.jmsMessage = jmsMessage;
        this.response = response;
    }

    public ProxyResponse(Message jmsMessage, String errorMessage) {
        this.jmsMessage = jmsMessage;
        this.errorMessage = errorMessage;
    }

    public Message getJmsMessage() {
        return jmsMessage;
    }

    public void setJmsMessage(Message jmsMessage) {
        this.jmsMessage = jmsMessage;
    }

    public Asset getResponse() {
        return response;
    }

    public void setResponse(Asset response) {
        this.response = response;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}