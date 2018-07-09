#!/usr/bin/env python
#
# AUTO-GENERATED CODE.  DO NOT MODIFY!
#
# Source: scanner.spd.xml
from ossie.cf import CF
from ossie.cf import CF__POA
from ossie.utils import uuid

from frontend import FrontendScannerDevice
from frontend import digital_scanning_tuner_delegation
from frontend import rfinfo_delegation
from ossie.threadedcomponent import *
from ossie.properties import simple_property
from ossie.properties import simpleseq_property
from ossie.properties import struct_property
from ossie.properties import structseq_property

import Queue, copy, time, threading
from ossie.resource import usesport, providesport, PortCallError
import bulkio
import frontend
from omniORB import any as _any
from frontend import FRONTEND
from ossie.properties import struct_to_props
BOOLEAN_VALUE_HERE=False

class enums:
    # Enumerated values for FRONTEND::scanner_allocation
    class frontend_scanner_allocation:
        # Enumerated values for FRONTEND::scanner_allocation::mode
        class mode:
            SPAN_SCAN = "SPAN_SCAN"
            DISCRETE_SCAN = "DISCRETE_SCAN"
    
        # Enumerated values for FRONTEND::scanner_allocation::control_mode
        class control_mode:
            TIME_BASED = "TIME_BASED"
            SAMPLE_BASED = "SAMPLE_BASED"

class scanner_base(CF__POA.Device, FrontendScannerDevice, digital_scanning_tuner_delegation, rfinfo_delegation, ThreadedComponent):
        # These values can be altered in the __init__ of your derived class

        PAUSE = 0.0125 # The amount of time to sleep if process return NOOP
        TIMEOUT = 5.0 # The amount of time to wait for the process thread to die when stop() is called
        DEFAULT_QUEUE_SIZE = 100 # The number of BulkIO packets that can be in the queue before pushPacket will block

        def __init__(self, devmgr, uuid, label, softwareProfile, compositeDevice, execparams):
            FrontendScannerDevice.__init__(self, devmgr, uuid, label, softwareProfile, compositeDevice, execparams)
            ThreadedComponent.__init__(self)

            self.listeners={}
            # self.auto_start is deprecated and is only kept for API compatibility
            # with 1.7.X and 1.8.0 devices.  This variable may be removed
            # in future releases
            self.auto_start = False
            # Instantiate the default implementations for all ports on this device
            self.port_RFInfo_in = frontend.InRFInfoPort("RFInfo_in", self)
            self.port_DigitalTuner_in = frontend.InDigitalScanningTunerPort("DigitalTuner_in", self)
            self.port_dataDouble_out = bulkio.OutDoublePort("dataDouble_out")
            self.addPropertyChangeListener('connectionTable',self.updated_connectionTable)
            self.device_kind = "FRONTEND::TUNER"
            self.frontend_listener_allocation = frontend.fe_types.frontend_listener_allocation()
            self.frontend_tuner_allocation = frontend.fe_types.frontend_tuner_allocation()
            self.frontend_scanner_allocation = frontend.fe_types.frontend_scanner_allocation()

        def start(self):
            FrontendScannerDevice.start(self)
            ThreadedComponent.startThread(self, pause=self.PAUSE)

        def stop(self):
            FrontendScannerDevice.stop(self)
            if not ThreadedComponent.stopThread(self, self.TIMEOUT):
                raise CF.Resource.StopError(CF.CF_NOTSET, "Processing thread did not die")

        def updated_connectionTable(self, id, oldval, newval):
            self.port_dataDouble_out.updateConnectionFilter(newval)

        def releaseObject(self):
            try:
                self.stop()
            except Exception:
                self._log.exception("Error stopping")
            FrontendScannerDevice.releaseObject(self)

        ######################################################################
        # PORTS
        # 
        # DO NOT ADD NEW PORTS HERE.  You can add ports in your derived class, in the SCD xml file, 
        # or via the IDE.

        port_RFInfo_in = providesport(name="RFInfo_in",
                                      repid="IDL:FRONTEND/RFInfo:1.0",
                                      type_="data")

        port_DigitalTuner_in = providesport(name="DigitalTuner_in",
                                            repid="IDL:FRONTEND/DigitalScanningTuner:1.0",
                                            type_="control")

        port_dataDouble_out = usesport(name="dataDouble_out",
                                       repid="IDL:BULKIO/dataDouble:1.0",
                                       type_="data")

        ######################################################################
        # PROPERTIES
        # 
        # DO NOT ADD NEW PROPERTIES HERE.  You can add properties in your derived class, in the PRF xml file
        # or by using the IDE.
        lastScanStrategy = simple_property(id_="lastScanStrategy",
                                           name="lastScanStrategy",
                                           type_="string",
                                           mode="readonly",
                                           action="external",
                                           kinds=("property",))


        lastScanStartTimeSec = simple_property(id_="lastScanStartTimeSec",
                                               name="lastScanStartTimeSec",
                                               type_="double",
                                               mode="readonly",
                                               action="external",
                                               kinds=("property",))


        class frontend_tuner_status_struct_struct(frontend.default_frontend_tuner_status_struct_struct):
            scan_mode_enabled = simple_property(
                                                id_="FRONTEND::tuner_status::scan_mode_enabled",
                                                
                                                name="scan_mode_enabled",
                                                type_="boolean")
        
            supports_scan = simple_property(
                                            id_="FRONTEND::tuner_status::supports_scan",
                                            
                                            name="supports_scan",
                                            type_="boolean")
        
            def __init__(self, allocation_id_csv="", bandwidth=0.0, center_frequency=0.0, enabled=False, group_id="", rf_flow_id="", sample_rate=0.0, scan_mode_enabled=False, supports_scan=False, tuner_type=""):
                frontend.default_frontend_tuner_status_struct_struct.__init__(self, allocation_id_csv=allocation_id_csv, bandwidth=bandwidth, center_frequency=center_frequency, enabled=enabled, group_id=group_id, rf_flow_id=rf_flow_id, sample_rate=sample_rate, tuner_type=tuner_type)
                self.scan_mode_enabled = scan_mode_enabled
                self.supports_scan = supports_scan
        
            def __str__(self):
                """Return a string representation of this structure"""
                d = {}
                d["allocation_id_csv"] = self.allocation_id_csv
                d["bandwidth"] = self.bandwidth
                d["center_frequency"] = self.center_frequency
                d["enabled"] = self.enabled
                d["group_id"] = self.group_id
                d["rf_flow_id"] = self.rf_flow_id
                d["sample_rate"] = self.sample_rate
                d["scan_mode_enabled"] = self.scan_mode_enabled
                d["supports_scan"] = self.supports_scan
                d["tuner_type"] = self.tuner_type
                return str(d)
        
            @classmethod
            def getId(cls):
                return "FRONTEND::tuner_status_struct"
        
            @classmethod
            def isStruct(cls):
                return True
        
            def getMembers(self):
                return frontend.default_frontend_tuner_status_struct_struct.getMembers(self) + [("scan_mode_enabled",self.scan_mode_enabled),("supports_scan",self.supports_scan)]

        connectionTable = structseq_property(id_="connectionTable",
                                             structdef=bulkio.connection_descriptor_struct,
                                             defvalue=[],
                                             configurationkind=("property",),
                                             mode="readonly")



        # Rebind tuner status property with custom struct definition
        frontend_tuner_status = FrontendScannerDevice.frontend_tuner_status.rebind()
        frontend_tuner_status.structdef = frontend_tuner_status_struct_struct

        def frontendTunerStatusChanged(self,oldValue, newValue):
            pass

        def getTunerStatus(self,allocation_id):
            tuner_id = self.getTunerMapping(allocation_id)
            if tuner_id < 0:
                raise FRONTEND.FrontendException(("ERROR: ID: " + str(allocation_id) + " IS NOT ASSOCIATED WITH ANY TUNER!"))
            _props = self.query([CF.DataType(id='FRONTEND::tuner_status',value=_any.to_any(None))])
            return _props[0].value._v[tuner_id]._v

        def assignListener(self,listen_alloc_id, allocation_id):
            # find control allocation_id
            existing_alloc_id = allocation_id
            if self.listeners.has_key(existing_alloc_id):
                existing_alloc_id = self.listeners[existing_alloc_id]
            self.listeners[listen_alloc_id] = existing_alloc_id

            old_table = self.connectionTable
            new_entries = []
            for entry in self.connectionTable:
                if entry.connection_id == existing_alloc_id:
                    tmp = bulkio.connection_descriptor_struct()
                    tmp.connection_id = listen_alloc_id
                    tmp.stream_id = entry.stream_id
                    tmp.port_name = entry.port_name
                    new_entries.append(tmp)

            for new_entry in new_entries:
                foundEntry = False
                for entry in self.connectionTable:
                    if entry.connection_id == new_entry.connection_id and \
                       entry.stream_id == entry.stream_id and \
                       entry.port_name == entry.port_name:
                        foundEntry = True
                        break

                if not foundEntry:
                    self.connectionTable.append(new_entry)

            self.connectionTableChanged(old_table, self.connectionTable)

        def connectionTableChanged(self, oldValue, newValue):
            self.port_dataDouble_out.updateConnectionFilter(newValue)

        def removeListener(self,listen_alloc_id):
            if self.listeners.has_key(listen_alloc_id):
                del self.listeners[listen_alloc_id]

            old_table = self.connectionTable
            for entry in list(self.connectionTable):
                if entry.connection_id == listen_alloc_id:
                    self.connectionTable.remove(entry)

            # Check to see if port "port_dataDouble_out" has a connection for this listener
            tmp = self.port_dataDouble_out._get_connections()
            for i in range(len(self.port_dataDouble_out._get_connections())):
                connection_id = tmp[i].connectionId
                if connection_id == listen_alloc_id:
                    self.port_dataDouble_out.disconnectPort(connection_id)
            self.connectionTableChanged(old_table, self.connectionTable)


        def removeAllocationIdRouting(self,tuner_id):
            allocation_id = self.getControlAllocationId(tuner_id)
            old_table = self.connectionTable
            for entry in list(self.connectionTable):
                if entry.connection_id == allocation_id:
                    self.connectionTable.remove(entry)

            for key,value in self.listeners.items():
                if (value == allocation_id):
                    for entry in list(self.connectionTable):
                        if entry.connection_id == key:
                            self.connectionTable.remove(entry)

            self.connectionTableChanged(old_table, self.connectionTable)

        def removeStreamIdRouting(self,stream_id, allocation_id):
            old_table = self.connectionTable
            for entry in list(self.connectionTable):
                if allocation_id == "":
                    if entry.stream_id == stream_id:
                        self.connectionTable.remove(entry)
                else:
                    if entry.stream_id == stream_id and entry.connection_id == allocation_id:
                        self.connectionTable.remove(entry)

            for key,value in self.listeners.items():
                if (value == allocation_id):
                    for entry in list(self.connectionTable):
                        if entry.connection_id == key and entry.stream_id == stream_id:
                            self.connectionTable.remove(entry)

            self.connectionTableChanged(old_table, self.connectionTable)

        def matchAllocationIdToStreamId(self,allocation_id, stream_id, port_name):
            if port_name != "":
                for entry in list(self.connectionTable):
                    if entry.port_name != port_name:
                        continue
                    if entry.stream_id != stream_id:
                        continue
                    if entry.connection_id != allocation_id:
                        continue
                    # all three match. This is a repeat
                    return

                old_table = self.connectionTable;
                tmp = bulkio.connection_descriptor_struct()
                tmp.connection_id = allocation_id
                tmp.port_name = port_name
                tmp.stream_id = stream_id
                self.connectionTable.append(tmp)
                self.connectionTableChanged(old_table, self.connectionTable)
                return

            old_table = self.connectionTable;
            tmp = bulkio.connection_descriptor_struct()
            tmp.connection_id = allocation_id
            tmp.port_name = "dataDouble_out"
            tmp.stream_id = stream_id
            self.connectionTable.append(tmp)
            self.connectionTableChanged(old_table, self.connectionTable)

