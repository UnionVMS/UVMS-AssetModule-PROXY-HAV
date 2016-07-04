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
package se.havochvatten.vessel.proxy.constant;

public class Constants {

    public static final String QUEUE_VESSEL_NATIONAL = "java:/jms/queue/UVMSAssetNational";
    public static final String QUEUE_NAME_VESSEL_NATIONAL = "UVMSAssetNational";
    
    public static final String VESSEL_CONNECTION_FACTORY = "java:jboss/DefaultJMSConnectionFactory";
    public static final String CONNECTION_TYPE = "javax.jms.MessageListener";
    public static final String DESTINATION_TYPE_QUEUE = "javax.jms.Queue";

    public static final String FIND_BY_NAME = "Parameter.findByName";

    public static final String CONNECTION_FACTORY = "java:/ConnectionFactory";
}