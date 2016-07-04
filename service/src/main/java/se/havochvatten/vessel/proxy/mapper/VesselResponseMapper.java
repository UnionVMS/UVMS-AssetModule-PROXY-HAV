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
package se.havochvatten.vessel.proxy.mapper;

import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import eu.europa.ec.fisheries.wsdl.asset.types.CarrierSource;
import se.havochvatten.service.client.vesselws.v2_1.GetVesselByCFRResponse;
import se.havochvatten.service.client.vesselws.v2_1.GetVesselByIRCSResponse;
import se.havochvatten.service.client.vesselws.v2_1.GetVesselByIdResponse;
import se.havochvatten.service.client.vesselws.v2_1.error.DefaultPortType;
import se.havochvatten.service.client.vesselws.v2_1.error.Vessel;
import se.havochvatten.vessel.proxy.exception.VesselProxyMappingException;

/**
 **/
public class VesselResponseMapper {

    public static Asset mapGetByCfrToDto(GetVesselByCFRResponse response) throws VesselProxyMappingException {
        Asset dto = getVesselDto(response.getVessel());
        AssetId vesselId = new AssetId();
        vesselId.setType(AssetIdType.CFR);
        vesselId.setValue(dto.getCfr());
		dto.setAssetId(vesselId);
		return dto;
    }

    public static Asset mapGetByIdToDto(GetVesselByIdResponse response) throws VesselProxyMappingException {
        Asset dto = getVesselDto(response.getVessel());
        AssetId vesselId = new AssetId();
        vesselId.setType(AssetIdType.INTERNAL_ID);
        vesselId.setValue(response.getVessel().getVesselId());
        dto.setAssetId(vesselId);
        return dto;
    }

    public static Asset mapGetByIrcsSToDto(GetVesselByIRCSResponse response) throws VesselProxyMappingException {
        Asset dto = getVesselDto(response.getVessel());
        AssetId vesselId = new AssetId();
        vesselId.setType(AssetIdType.IRCS);
        vesselId.setValue(dto.getIrcs());
        dto.setAssetId(vesselId);
        return dto;
    }

    private static Asset getVesselDto(Vessel vessel) throws VesselProxyMappingException {

        if (vessel != null) {

            Asset dto = new Asset();

            dto.setActive(vessel.isActive());
            dto.setCfr(vessel.getCfr());
            dto.setCountryCode(vessel.getIso3AlphaNation());
            dto.setPowerMain(vessel.getEnginePower());
            dto.setHomePort(getPort(vessel.getDefaultPort()));
            dto.setName(vessel.getVesselName());
            dto.setIrcs(vessel.getIrcs());
            dto.setLengthOverAll(vessel.getLoa());
            dto.setSource(CarrierSource.NATIONAL);
            dto.setGrossTonnage(vessel.getEuTon());
            dto.setHasLicense(vessel.isHasLicense());
            
            if (vessel.getIrcs() != null && !vessel.getIrcs().isEmpty()) {
                dto.setHasIrcs("Y");
            } else {
            	dto.setHasIrcs("N");
            }
            
            dto.setExternalMarking(vessel.getDistrict());
            
            return dto;
        } else {
            return new Asset();
        }
    }

    private static String getPort(DefaultPortType port) {
        if (port != null) {
            return port.getPort();
        } else {
            return "";
        }

    }

}