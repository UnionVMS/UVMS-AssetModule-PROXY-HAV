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

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMapperException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetDataSourceRequestMapper;
import eu.europa.ec.fisheries.wsdl.asset.types.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import se.havochvatten.vessel.proxy.VesselProxy;
import se.havochvatten.vessel.proxy.dto.ProxyResponse;
import se.havochvatten.vessel.proxy.exception.VesselProxyException;
import se.havochvatten.vessel.proxy.message.mock.MockData;

import javax.enterprise.event.Event;
import javax.jms.*;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 **/
@RunWith(MockitoJUnitRunner.class)
public class TestVesselProxyMessageRecciever {

    private static final String VESSELID = "1";
    private static final String VESSELCFR = "CFR123456789";
    private static final String VESSELIRCS = "IRCS";
    private static final Asset VESSEL = MockData.getVesselDto(Integer.parseInt(VESSELID));

    @Mock
    VesselProxy vesselProxy;

    @Mock
    JMSContext context;

    @InjectMocks
    VesselProxyMessageReciever receiver;

    @Mock
    TextMessage message;

    @Mock
    ObjectMessage wrongMessageType;

    @Mock
    TextMessage replyMessage;

    @Mock
    Queue queue;

    @Mock
    //@VesselProxyErrorEvent
    Event<ProxyResponse> errorEvent;

    @Mock
    //@VesselProxyMessageRecievedEvent
    Event<ProxyResponse> responseRecievedEvent;

    @Mock
    JMSProducer producer;

    private String VESSEL_BY_ID;
    private String VESSEL_BY_CFR;
    private String VESSEL_BY_IRCS;
    private String VESSEL_LIST;
    private String VESSEL_CREATE;
    private String VESSEL_UPDATE;

    private String getVesselMethodList() {
        try {
            AssetListQuery query = new AssetListQuery();
            AssetListCriteria search = new AssetListCriteria();

            List<AssetListCriteriaPair> criterias = new ArrayList<>();

            AssetListCriteriaPair criteria = new AssetListCriteriaPair();
            criteria.setKey(ConfigSearchField.IRCS);
            criteria.setValue("IRCS");
            criterias.add(criteria);

            search.getCriterias().addAll(criterias);

            AssetListPagination pagination = new AssetListPagination();
            pagination.setListSize(1);
            pagination.setPage(1);

            query.setAssetSearchCriteria(search);
            query.setPagination(pagination);
            return AssetDataSourceRequestMapper.mapGetAssetList(query);
        } catch (Exception e) {
            return "";
        }
    }

    @Before
    public void setUp() throws JMSException, AssetModelMapperException {
        VESSEL_BY_ID = AssetDataSourceRequestMapper.mapGetAssetById(VESSELID, AssetIdType.INTERNAL_ID);
        VESSEL_BY_CFR = AssetDataSourceRequestMapper.mapGetAssetById(VESSELCFR, AssetIdType.CFR);
        VESSEL_BY_IRCS = AssetDataSourceRequestMapper.mapGetAssetById(VESSELIRCS, AssetIdType.IRCS);
        VESSEL_LIST = getVesselMethodList();
        VESSEL_CREATE = AssetDataSourceRequestMapper.mapCreateAsset(VESSEL, "TEST");
        VESSEL_UPDATE = AssetDataSourceRequestMapper.mapUpdateAsset(VESSEL, "TEST");

        MockitoAnnotations.initMocks(this);
        when(context.createTextMessage()).thenReturn(replyMessage);
        when(message.getJMSReplyTo()).thenReturn(queue);
        when(context.createProducer()).thenReturn(producer);
    }

    @Test
    public void testGetVesselById() {
        try {

            when(context.createTextMessage()).thenReturn(message);
            when(message.getText()).thenReturn(VESSEL_BY_ID);
            when(vesselProxy.getVesselById(VESSELID)).thenReturn(MockData.getVesselDto(Integer.parseInt(VESSELID)));

            TextMessage msg = context.createTextMessage();
            msg.setText(VESSEL_BY_ID);

            receiver.recvieveMessage(msg);

            Mockito.verify(responseRecievedEvent).fire(any(ProxyResponse.class));
            Mockito.verify(vesselProxy).getVesselById(VESSELID);
            Mockito.verify(vesselProxy, Mockito.times(0)).getVesselByIRCS(VESSELIRCS);
            Mockito.verify(vesselProxy, Mockito.times(0)).getVesselByCFR(VESSELCFR);
        } catch (VesselProxyException | AssetModelException | JMSException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void testGetVesselByIRCS() {
        try {

            when(context.createTextMessage()).thenReturn(message);
            when(message.getText()).thenReturn(VESSEL_BY_IRCS);
            when(vesselProxy.getVesselByCFR(VESSELIRCS)).thenReturn(MockData.getVesselDto(Integer.parseInt(VESSELID)));

            TextMessage msg = context.createTextMessage();
            msg.setText(VESSEL_BY_IRCS);
            receiver.recvieveMessage(msg);

            Mockito.verify(responseRecievedEvent).fire(any(ProxyResponse.class));
            Mockito.verify(vesselProxy).getVesselByIRCS(VESSELIRCS);
            Mockito.verify(vesselProxy, Mockito.times(0)).getVesselByCFR(VESSELCFR);
            Mockito.verify(vesselProxy, Mockito.times(0)).getVesselById(VESSELID);

        } catch (VesselProxyException | AssetModelException | JMSException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void testGetVesselByCFR() {
        try {

            when(context.createTextMessage()).thenReturn(message);
            when(message.getText()).thenReturn(VESSEL_BY_CFR);
            when(vesselProxy.getVesselByCFR(VESSELCFR)).thenReturn(MockData.getVesselDto(Integer.parseInt(VESSELID)));

            TextMessage msg = context.createTextMessage();
            msg.setText(VESSEL_BY_CFR);
            receiver.recvieveMessage(msg);

            Mockito.verify(responseRecievedEvent).fire(any(ProxyResponse.class));
            Mockito.verify(vesselProxy).getVesselByCFR(VESSELCFR);
            Mockito.verify(vesselProxy, Mockito.times(0)).getVesselByIRCS(VESSELIRCS);
            Mockito.verify(vesselProxy, Mockito.times(0)).getVesselById(VESSELID);
        } catch (VesselProxyException | AssetModelException | JMSException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Ignore
    @Test(expected = VesselProxyException.class)
    public void testNotImplementedList() throws VesselProxyException {

        try {
            when(context.createTextMessage()).thenReturn(message);
            when(message.getText()).thenReturn(VESSEL_LIST);

            TextMessage msg = context.createTextMessage();
            msg.setText(VESSEL_LIST);
            receiver.recvieveMessage(msg);

            Mockito.verify(errorEvent).fire(any(ProxyResponse.class));
            Mockito.verify(vesselProxy, Mockito.times(0)).getVesselByCFR(VESSELCFR);
            Mockito.verify(vesselProxy, Mockito.times(0)).getVesselByIRCS(VESSELIRCS);
            Mockito.verify(vesselProxy, Mockito.times(0)).getVesselById(VESSELID);
        } catch (JMSException | AssetModelException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test(expected = VesselProxyException.class)
    public void testNotImplementedCreate() throws VesselProxyException {

        try {
            when(context.createTextMessage()).thenReturn(message);
            when(message.getText()).thenReturn(VESSEL_UPDATE);

            TextMessage msg = context.createTextMessage();
            msg.setText(VESSEL_UPDATE);
            receiver.recvieveMessage(msg);

            Mockito.verify(errorEvent).fire(any(ProxyResponse.class));
            Mockito.verify(vesselProxy, Mockito.times(0)).getVesselByCFR(VESSELCFR);
            Mockito.verify(vesselProxy, Mockito.times(0)).getVesselByIRCS(VESSELIRCS);
            Mockito.verify(vesselProxy, Mockito.times(0)).getVesselById(VESSELID);
        } catch (JMSException | AssetModelException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test(expected = VesselProxyException.class)
    public void testNotImplementedUpdate() throws VesselProxyException {

        try {
            when(context.createTextMessage()).thenReturn(message);
            when(message.getText()).thenReturn(VESSEL_UPDATE);

            TextMessage msg = context.createTextMessage();
            msg.setText(VESSEL_UPDATE);
            receiver.recvieveMessage(msg);

            Mockito.verify(errorEvent).fire(any(ProxyResponse.class));
            Mockito.verify(vesselProxy, Mockito.times(0)).getVesselByCFR(VESSELCFR);
            Mockito.verify(vesselProxy, Mockito.times(0)).getVesselByIRCS(VESSELIRCS);
            Mockito.verify(vesselProxy, Mockito.times(0)).getVesselById(VESSELID);
        } catch (JMSException | AssetModelException ex) {
            Assert.fail(ex.getMessage());
        }
    }

}