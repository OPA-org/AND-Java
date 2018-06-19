package and;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;
import javafx.util.Pair;

public class AND {

    public static void main(String[] args) throws IOException, Exception {
        
        /*ArrayList<String> R = new ArrayList<>();
        ArrayList<String> IPs = new ArrayList<>();
        device_discovery("192.168.3.2", R, IPs);
        System.out.println("IPs:-");
        for (String s : IPs) {
            System.out.println(s);
        }
        ArrayList<Agent> agents = new ArrayList<>();
        create_nodes(IPs, agents);
        System.out.println("Agents:-");
        for (Agent a : agents) {
            System.out.println(a);
        }

        ArrayList<Connection> Connections = create_connections(agents);
        
        System.out.println("Connections:-");
        for(Connection c: Connections){
            System.out.println("ID: "+c.getID()
                    +"\tA: " +c.getA()
                    +"\tB: "+c.getB()
                    +"\tSubnet: "+c.getSubnet()
                    +"\tMask: "+c.getMask());
        }*/
        
        /*ArrayList<String> oids = new ArrayList<>();
        oids.add(OIDS.ipNetToMediaIfIndex);
        oids.add(OIDS.ipNetToMediaNetAddress);
        System.out.println(SNMP_methods.getfromwalk_multi("192.168.5.2", OIDS.ipNetToMediaTable, oids));*/
        //System.out.println(SNMP_methods.getfromsnmpget_single("192.168.5.3", OIDS.dot1dBaseBridgeAddress));
        Switch sw = create_Switch("192.168.5.3");
        System.err.println(sw);
    }

    static void create_nodes(ArrayList<String> IPs, ArrayList<Agent> agents) throws IOException, Exception {
        for (String IP : IPs) {
            Boolean visited = false;
            for (Agent agent : agents) {
                if (agent.has_IPaddress(IP)) {
                    visited = true;
                }
            }
            if (visited) {
                continue;
            }
            String sysservresult = SNMP_methods.getfromsnmpget_single(IP, OIDS.sysServices);
            if (!sysservresult.equals("error")) {
                String binsysserv = new StringBuilder(MiscellaneousMethods.converttobinary(Integer.valueOf(sysservresult)).substring(1,8)).reverse().toString();
                if(binsysserv.charAt(1) == '1'){
                    //L2 yes
                    if(binsysserv.charAt(2) == '1'){
                        //L3 yes
                        //hnshof el bridge mib
                        if(!SNMP_methods.getfromsnmpget_single(IP, OIDS.dot1dBaseBridgeAddress).equals("error")){
                            //bridge Mib yes
                            //multiple interface with same mac addr
                        }else{
                            //bride mib no
                            if(binsysserv.charAt(6) == '1'){
                                //L7 yes
                                if(SNMP_methods.getfromsnmpget_single(IP, OIDS.dot1dStpProtocolSpecification).equals("error")){
                                    //router
                                    Agent router = create_Router(IP);
                                    if (!agents.contains(router)) {
                                        agents.add(router);
                                    }
                                }else{
                                    //switch
                                    
                                }
                            }else{
                                //L7 no
                                Agent router = create_Router(IP);
                                if (!agents.contains(router)) {
                                    agents.add(router);
                                }
                            }
                        }
                    }else{
                        //L3 no
                        //hnshof el bridgemib
                        if(!SNMP_methods.getfromsnmpget_single(IP, OIDS.dot1dBaseBridgeAddress).equals("error")){
                            //bridge Mib yes
                            //switch
                        }else{
                            //bride mib no
                            Interface inter = new Interface(IP);
                            Agent host = new Host(inter);
                            agents.add(host);
                        }
                    }
                }else{
                    //L2 No
                    if(binsysserv.charAt(2) == '1'){
                        //L3 yes
                        if(binsysserv.charAt(3) == '1'){
                            //L4 yes
                            //switch
                        }else{
                            //L4 no
                            if(binsysserv.charAt(6) == '1'){
                                //L7 yes
                                if(SNMP_methods.getfromsnmpget_single(IP, OIDS.dot1dStpProtocolSpecification).equals("error")){
                                    //router
                                    Agent router = create_Router(IP);
                                    if (!agents.contains(router)) {
                                        agents.add(router);
                                    }
                                }else{
                                    //switch
                                    
                                }
                            }else{
                                //L7 no
                                Agent router = create_Router(IP);
                                if (!agents.contains(router)) {
                                    agents.add(router);
                                }
                            }
                        }
                    }else{
                        //L3 no
                    }
                }
                
            } else {
                Interface inter = new Interface(IP);
                Agent host = new Host(inter);
                agents.add(host);
            }
        }
    }

    static void device_discovery(String gateway_IP, ArrayList<String> R, ArrayList<String> IPs) throws IOException, Exception {
        R.add(gateway_IP);
        int n = 0;
        while (true) {
            if (!SNMP_methods.getfromsnmpget_single(R.get(n), OIDS.sysDescr).equals("error")) {
                ArrayList<String> oids = new ArrayList<>();
                oids.add(OIDS.ipRouteType);
                oids.add(OIDS.ipRouteNextHop);
                ArrayList<ArrayList<String>> Next_hops = SNMP_methods.getfromwalk_multi(R.get(n), OIDS.ipRouteTable, oids);
                for (int i = 0; i < Next_hops.get(0).size(); i++) {
                    if (Next_hops.get(0).get(i).equals("4")) {
                        if (MiscellaneousMethods.isHostIP(Next_hops.get(1).get(i))) {
                            if (!R.contains(Next_hops.get(1).get(i))) {
                                R.add(Next_hops.get(1).get(i));
                            }
                            if (!IPs.contains(Next_hops.get(1).get(i))) {
                                IPs.add(Next_hops.get(1).get(i));
                            }
                        }
                    }
                }
            } else {
                //R[n].type= host
                if (MiscellaneousMethods.isHostIP(R.get(n))) {
                    if (!IPs.contains(R.get(n))) {
                        IPs.add(R.get(n));
                    }
                }
            }
            n++;
            if (n >= R.size()) {
                break;
            }
        }
        n = 0;
        while (true) {
            if (IPs.isEmpty()) {
                break;
            }
            if (!SNMP_methods.getfromsnmpget_single(IPs.get(n), OIDS.sysDescr).equals("error")) {
                ArrayList<String> NetToMediaAddresses = SNMP_methods.getfromwalk_single(IPs.get(n), OIDS.ipNetToMediaTable, OIDS.ipNetToMediaNetAddress);
                for (String ND : NetToMediaAddresses) {
                    if (MiscellaneousMethods.isHostIP(ND)) {
                        if (!IPs.contains(ND)) {
                            IPs.add(ND);
                        }
                    }
                }
            }
            n++;
            if (n >= IPs.size()) {
                break;
            }
        }
    }
    public static Agent get_Agent_by_Ip(ArrayList<Agent>agents,String ip){
        for (Agent B : agents) {
            if(B.has_IPaddress(ip))
                return B;
        }
        return null;
    }
    
    public static Boolean has_similar_connection(ArrayList<Connection> connections,Agent A,Agent B){
        for(Connection c : connections){
            if(c.getA().equals(A) && c.getB().equals(B)){
                return true;
            }else if(c.getA().equals(B) && c.getB().equals(A)){
                return true;
            }
        }
        return false;
    }
    
    public static ArrayList<Connection> create_connections(ArrayList<Agent> agents) throws IOException{
        ArrayList<Connection> Connections = new ArrayList<>();
        for (int i = 0; i < agents.size(); i++) {
            Agent A = agents.get(i);
            if (A.getClass().getSimpleName().equals("Router")) {
                ArrayList<Interface> usedinf = ((Router) A).get_UsedInterfaces();
                if (!usedinf.isEmpty()) {
                    for (int j = 0; j < usedinf.size(); j++) {
                        ArrayList<String> nexthops = SNMP_methods.getfromwalk_single(usedinf.get(j).getIp_address(),
                                OIDS.ipNetToMediaTable, OIDS.ipNetToMediaNetAddress);
                        ArrayList<String> tempnh = (ArrayList<String>) nexthops.clone();
                        for (String s : tempnh) {
                            if (((Router) A).has_IPaddress(s)) {
                                nexthops.remove(s);
                            } else if (!MiscellaneousMethods.isHostIP(s)) {
                                nexthops.remove(s);
                            }
                        }
                        if (i == 0) {
                            for (String s : nexthops) {
                                String Mask = usedinf.get(j).getSubnet_mask();
                                String infNetAddress = MiscellaneousMethods.getNetworkIP(usedinf.get(j).getIp_address(), Mask);
                                String ipNetAddress = MiscellaneousMethods.getNetworkIP(s, Mask);
                                if (infNetAddress.equals(ipNetAddress)) {
                                    Agent B = get_Agent_by_Ip(agents, s);
                                    if (B != null) {
                                        Connections.add(new Connection(A, B, ipNetAddress, Mask));
                                    }
                                }
                            }
                        } else {
                            for (String s : nexthops) {
                                String Mask = usedinf.get(j).getSubnet_mask();
                                String infNetAddress = MiscellaneousMethods.getNetworkIP(usedinf.get(j).getIp_address(), Mask);
                                String ipNetAddress = MiscellaneousMethods.getNetworkIP(s, Mask);
                                if (infNetAddress.equals(ipNetAddress)) {
                                    Agent B = get_Agent_by_Ip(agents, s);
                                    if (B != null && !has_similar_connection(Connections, A, B)) {
                                        Connections.add(new Connection(A, B, ipNetAddress, Mask));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return Connections;
    }
    
    public static Router create_Router(String IP) throws Exception {
        ArrayList<String> oids = new ArrayList<>();
        oids.add(OIDS.sysDescr);
        oids.add(OIDS.sysName);
        oids = SNMP_methods.getfromsnmpget_multi(IP, oids);
        String sysdescr = oids.get(0);
        String sysname = oids.get(1);
        ArrayList<ArrayList<String>> interfaces = new ArrayList<>();
        oids = new ArrayList<>();
        oids.add(OIDS.ifIndex);
        oids.add(OIDS.ifPhyaddress);
        oids.add(OIDS.ifDescr);
        interfaces = SNMP_methods.getfromwalk_multi(IP, OIDS.ifTable, oids);
        oids = new ArrayList<>();
        oids.add(OIDS.ipNetToMediaIfIndex);
        oids.add(OIDS.ipNetToMediaNetAddress);
        oids.add(OIDS.ipNetToMediaPhysAddress);
        ArrayList<ArrayList<String>> ntm = SNMP_methods.getfromwalk_multi(IP, OIDS.ipNetToMediaTable, oids);
        oids = new ArrayList<>();
        oids.add(OIDS.ipRouteIfIndex);
        oids.add(OIDS.ipRouteMask);
        ArrayList<ArrayList<String>> iproutes = SNMP_methods.getfromwalk_multi(IP, OIDS.ipRouteTable, oids);
        ArrayList<String> ips = new ArrayList<>();
        ArrayList<String> masks = new ArrayList<>();
        for (int i = 0; i < interfaces.get(0).size(); i++) {
            if (ntm.get(2).contains(interfaces.get(1).get(i))) {
                int index = ntm.get(2).indexOf(interfaces.get(1).get(i));
                if (ntm.get(0).get(index).equals(interfaces.get(0).get(i))
                        && ntm.get(2).get(index).equals(interfaces.get(1).get(i))) {
                    ips.add(ntm.get(1).get(index));
                } else {
                    ips.add("");
                }
            } else {
                ips.add("");
            }
            if (iproutes.get(0).contains(interfaces.get(0).get(i))) {
                int index = iproutes.get(0).indexOf(interfaces.get(0).get(i));
                masks.add(iproutes.get(1).get(index));
            } else {
                masks.add("");
            }
        }
        interfaces.add(ips);
        interfaces.add(masks);
        /*for(int i = 0; i < interfaces.get(0).size(); i++){
                    System.out.println("ifIndex: " +interfaces.get(0).get(i) 
                    + "\tifPhyaddress: " +interfaces.get(1).get(i)
                    + "\tIP: " +interfaces.get(3).get(i) 
                    +"\tMASK: "+ interfaces.get(4).get(i));
                }*/
        ArrayList<Interface> routerifs = new ArrayList<>();
        for (int i = 0; i < interfaces.get(0).size(); i++) {
            Interface routerif = new Interface(interfaces.get(0).get(i),
                    interfaces.get(1).get(i),
                    interfaces.get(3).get(i),
                    interfaces.get(4).get(i),
                    interfaces.get(2).get(i));
            routerifs.add(routerif);
        }
        Router router = new Router(sysdescr, sysname, routerifs);
        return router;
    }
    public static Switch create_Switch(String IP) throws Exception {
        ArrayList<String> oids = new ArrayList<>();
        oids.add(OIDS.sysDescr);
        oids.add(OIDS.sysName);
        oids = SNMP_methods.getfromsnmpget_multi(IP, oids);
        String sysdescr = oids.get(0);
        String sysname = oids.get(1);
        ArrayList<ArrayList<String>> interfaces = new ArrayList<>();
        oids = new ArrayList<>();
        oids.add(OIDS.ifIndex);
        oids.add(OIDS.ifPhyaddress);
        oids.add(OIDS.ifDescr);
        interfaces = SNMP_methods.getfromwalk_multi(IP, OIDS.ifTable, oids);
        oids = new ArrayList<>();
        oids.add(OIDS.ipNetToMediaIfIndex);
        oids.add(OIDS.ipNetToMediaNetAddress);
        oids.add(OIDS.ipNetToMediaPhysAddress);
        ArrayList<ArrayList<String>> ntm = SNMP_methods.getfromwalk_multi(IP, OIDS.ipNetToMediaTable, oids);
        oids = new ArrayList<>();
        oids.add(OIDS.ipRouteIfIndex);
        oids.add(OIDS.ipRouteMask);
        ArrayList<ArrayList<String>> iproutes = SNMP_methods.getfromwalk_multi(IP, OIDS.ipRouteTable, oids);
        ArrayList<String> ips = new ArrayList<>();
        ArrayList<String> masks = new ArrayList<>();
        for (int i = 0; i < interfaces.get(0).size(); i++) {
            if (ntm.get(2).contains(interfaces.get(1).get(i))) {
                int index = ntm.get(2).indexOf(interfaces.get(1).get(i));
                if (ntm.get(0).get(index).equals(interfaces.get(0).get(i))
                        && ntm.get(2).get(index).equals(interfaces.get(1).get(i))) {
                    ips.add(ntm.get(1).get(index));
                } else {
                    ips.add("");
                }
            } else {
                ips.add("");
            }
            if (iproutes.get(0).contains(interfaces.get(0).get(i))) {
                int index = iproutes.get(0).indexOf(interfaces.get(0).get(i));
                masks.add(iproutes.get(1).get(index));
            } else {
                masks.add("");
            }
        }
        interfaces.add(ips);
        interfaces.add(masks);
        /*for(int i = 0; i < interfaces.get(0).size(); i++){
                    System.out.println("ifIndex: " +interfaces.get(0).get(i) 
                    + "\tifPhyaddress: " +interfaces.get(1).get(i)
                    + "\tIP: " +interfaces.get(3).get(i) 
                    +"\tMASK: "+ interfaces.get(4).get(i));
                }*/
        ArrayList<Interface> routerifs = new ArrayList<>();
        for (int i = 0; i < interfaces.get(0).size(); i++) {
            Interface routerif = new Interface(interfaces.get(0).get(i),
                    interfaces.get(1).get(i),
                    interfaces.get(3).get(i),
                    interfaces.get(4).get(i),
                    interfaces.get(2).get(i));
            routerifs.add(routerif);
        }
        Switch switchh = new Switch(sysdescr, sysname, routerifs);
        return switchh;
    }
}
