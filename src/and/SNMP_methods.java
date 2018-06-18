package and;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

public class SNMP_methods {
    
    private static Map<String, String> doWalk(String tableOid, Target target) throws IOException {
        Map<String, String> result = new TreeMap<>();
        TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
        Snmp snmp = new Snmp(transport);
        transport.listen();

        TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
        List events = treeUtils.getSubtree(target, new OID(tableOid));
        if (events == null || events.size() == 0) {
            System.out.println("Error: Unable to read table...");
            return result;
        }

        for (int i = 0 ; i < events.size() ; i++) {
            TreeEvent event = (TreeEvent)events.get(i);
            if (event == null) {
                continue;
            }
            if (event.isError()) {
                System.out.println("Error: table OID [" + tableOid + "] " + event.getErrorMessage());
                continue;
            }
            VariableBinding[] varBindings = event.getVariableBindings();
            if (varBindings == null || varBindings.length == 0) {
                continue;
            }
            for (VariableBinding varBinding : varBindings) {
                if (varBinding == null) {
                    continue;
                }

                result.put("." + varBinding.getOid().toString(), varBinding.getVariable().toString());
            }
        }
        snmp.close();
        return result;
    }
    
    private static CommunityTarget init_walk(String ip)throws IOException{
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("public"));
        target.setAddress(GenericAddress.parse("udp:"+ip.trim()+"/161")); // supply your own IP and port
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version2c);
        return target;
    }
    
    public static ArrayList<ArrayList<String>> getfromwalk_multi(String ip,String TableOID,ArrayList<String> EntryOIDs) throws IOException{
        CommunityTarget target = init_walk(ip);

        ArrayList<ArrayList<String>> entries = new ArrayList<>();
        
        Map<String, String> result = doWalk("."+TableOID.trim(), target); // ifTable, mib-2 interfaces
                                            // 1.3.6.1.2.1.2.2  ,  1.3.6.1.2.1.4.21 , 1.3.6.1.2.1.4.22
                                            
        for (String entryoid : EntryOIDs) {
            ArrayList<String> entrydata = new ArrayList<>();
            for (Map.Entry<String, String> entry : result.entrySet()) {
                if (entry.getKey().startsWith("." + entryoid.trim() + ".")) {
                    entrydata.add(entry.getValue());
                }
            }
            entries.add(entrydata);
        }
        
        return entries;
    }
    
    public static ArrayList<String> getfromwalk_single(String ip,String TableOID,String EntryOID) throws IOException{
        CommunityTarget target = init_walk(ip);

        ArrayList<String> entrydata = new ArrayList<>();
        
        Map<String, String> result = doWalk("."+TableOID.trim(), target); // ifTable, mib-2 interfaces
                                            // 1.3.6.1.2.1.2.2  ,  1.3.6.1.2.1.4.21 , 1.3.6.1.2.1.4.22
                                            
        for (Map.Entry<String, String> entry : result.entrySet()) {
            if (entry.getKey().startsWith("." + EntryOID.trim() + ".")) {
                entrydata.add(entry.getValue());
            }
        }

        return entrydata;
    }
    
    private static String snmpGet(String strAddress, String comm, String strOID) {
        String str = "error";
        try {
            OctetString community = new OctetString(comm);
            strAddress = strAddress + "/" + "161";
            Address targetaddress = new UdpAddress(strAddress);
            TransportMapping transport = new DefaultUdpTransportMapping();
            transport.listen();
            CommunityTarget comtarget = new CommunityTarget();
            comtarget.setCommunity(community);
            comtarget.setVersion(SnmpConstants.version1);
            comtarget.setAddress(targetaddress);
            comtarget.setRetries(2);
            comtarget.setTimeout(5000);
            PDU pdu = new PDU();
            ResponseEvent response;
            Snmp snmp;
            pdu.add(new VariableBinding(new OID(strOID+".0")));
            pdu.setType(PDU.GET);
            snmp = new Snmp(transport);
            response = snmp.get(pdu, comtarget);
            if (response != null) {
                PDU pduresponse = response.getResponse();
                if (response.getResponse() != null) {
                    if (response.getResponse().getErrorStatusText().equalsIgnoreCase("Success")) {
                        str = pduresponse.getVariableBindings().firstElement().toString();
                        if (str.contains("=")) {
                            int len = str.indexOf("=");
                            str = str.substring(len + 1, str.length()).trim();
                        }
                    }
                }
            } else {
                System.out.println("Feeling like a TimeOut occured ");
            }
            snmp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }
    
    public static String getfromsnmpget_single(String ipaddress,String OID) throws IOException, Exception{
        String result = snmpGet(ipaddress,"public",OID);
        return result;
    }
    
    public static ArrayList<String> getfromsnmpget_multi(String ipaddress,ArrayList<String> OIDs) throws IOException, Exception{
        ArrayList<String> results = new ArrayList<>();
        for(String OID: OIDs){
            results.add(snmpGet(ipaddress,"public", OID));
        }
        return results;
    }
    
}
