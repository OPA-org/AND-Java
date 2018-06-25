package and;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javafx.util.Pair;

public class AND {

    public static void main(String[] args) throws IOException, Exception {
        
        ArrayList<String> R = new ArrayList<>();
        ArrayList<String> IPs = new ArrayList<>();
        device_discovery("192.168.3.2", R, IPs);
        System.out.println("IPs:-");
        for (String s : IPs) {
            System.out.println(s);
        }
        
        /*ArrayList<String> IPs = new ArrayList<>();
        IPs.add("192.168.3.1");
        IPs.add("192.168.2.1");
        IPs.add("192.168.3.2");
        IPs.add("192.168.10.1");
        IPs.add("192.168.2.2");
        IPs.add("192.168.10.2");
        IPs.add("192.168.4.1");
        IPs.add("192.168.6.1");
        IPs.add("192.168.6.2");
        IPs.add("192.168.6.3");
        IPs.add("192.168.5.1");
        IPs.add("192.168.5.3");
        IPs.add("192.168.5.2");
        IPs.add("192.168.5.4");
        IPs.add("192.168.5.5");
        IPs.add("192.168.5.6");
        IPs.add("192.168.4.2");*/
        
        ArrayList<Agent> agents = new ArrayList<>();
        create_nodes(IPs, agents);
        System.out.println("Agents:-");
        for (Agent a : agents) {
            System.out.println(a);
        }
        
        ArrayList<InterfaceConnection> Connections = create_connections(agents);
        Connections = Filter_Connections(Connections);
        
        System.out.println("Connections:-");
        for(InterfaceConnection c: Connections){
            System.out.println(c.toString()+
                    "\n===========================");
        }
        
//        ArrayList<String> oids = new ArrayList<>();
//        oids.add(OIDS.ipNetToMediaNetAddress);
//        oids.add(OIDS.ipNetToMediaPhysAddress);
//        System.out.println(SNMP_methods.getfromwalk_multi("192.168.5.5", OIDS.ipNetToMediaTable, oids));
//        oids = new ArrayList<>();
//        oids.add(OIDS.dot1dTpFdbAddress);
//        oids.add(OIDS.dot1dTpFdbPort);
//        System.out.println(SNMP_methods.getfromwalk_multi("192.168.5.5", OIDS.dot1dTpFdbTable, oids));
            
        //System.out.println(SNMP_methods.getfromsnmpget_single("192.168.5.3", OIDS.dot1dBaseBridgeAddress));
//        Switch sw = create_Switch("192.168.5.3");
        /*ArrayList<Interface> usedinf = sw.get_UsedInterfaces();
        if (!usedinf.isEmpty()) {
            for (int j = 0; j < usedinf.size(); j++) {
                ArrayList<String> nexthops = SNMP_methods.getfromwalk_single(usedinf.get(j).getIp_address(),
                        OIDS.ipNetToMediaTable, OIDS.ipNetToMediaNetAddress);
                ArrayList<String> tempnh = (ArrayList<String>) nexthops.clone();
                for (String s : tempnh) {
                    if (sw.has_IPaddress(s)) {
                        nexthops.remove(s);
                    } else if (!MiscellaneousMethods.isHostIP(s)) {
                        nexthops.remove(s);
                    }
                }
                System.out.println("");
            }
        }*/
        //System.out.println(SNMP_methods.getfromsnmpget_single("192.168.5.6", OIDS.sysName));
        //System.out.println(SNMP_methods.getfromwalk_single("192.168.5.3", "1.3.6.1.2.1.17.1.4", "1.3.6.1.2.1.17.1.4.1"));
        //System.out.println(SNMP_methods.getfromwalk_single("192.168.5.3", "1.3.6.1.2.1.17.4.3", "1.3.6.1.2.1.17.4.3.1"));
        /*Switch sw = create_Switch("192.168.5.3");
        for (Interface interface1 : sw.getInterfaces()) {
                   System.out.println(interface1.toString());
        }*/
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
                        String basebridgeaddr = SNMP_methods.getfromsnmpget_single(IP, OIDS.dot1dBaseBridgeAddress);
                        if(!basebridgeaddr.equals("error")){
                            //bridge Mib yes
                            //multiple interface with same mac addr
                            if(basebridgeaddr.equals("00:00:00:00:00:00")){
                                Agent router = create_Router(IP);
                                if (!agents.contains(router)) {
                                    agents.add(router);
                                }
                            } else {
                                Agent switchh = create_Switch(IP);
                                if (!agents.contains(switchh)) {
                                    agents.add(switchh);
                                }
                            }
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
                                    Agent switchh = create_Switch(IP);
                                    if (!agents.contains(switchh)) {
                                        agents.add(switchh);
                                    }
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
                            Agent switchh = create_Switch(IP);
                            if (!agents.contains(switchh)) {
                                agents.add(switchh);
                            }
                        }else{
                            //bride mib no
                            Interface inter = new Interface("1", "PC port", IP, "");
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
                            Agent switchh = create_Switch(IP);
                            if (!agents.contains(switchh)) {
                                agents.add(switchh);
                            }
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
                                    Agent switchh = create_Switch(IP);
                                    if (!agents.contains(switchh)) {
                                        agents.add(switchh);
                                    }
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
                Interface inter = new Interface("1", "PC port", IP, "");
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
    
    public static Boolean has_similar_connection(ArrayList<InterfaceConnection> connections,Agent A,Agent B){
        for(InterfaceConnection c : connections){
            if(c.getAgentA().equals(A) && c.getAgentB().equals(B)){
                return true;
            }else if(c.getAgentA().equals(B) && c.getAgentB().equals(A)){
                return true;
            }
        }
        return false;
    }
    
    public static ArrayList<InterfaceConnection> create_connections(ArrayList<Agent> agents) throws IOException{
        ArrayList<Switch> switches = get_switches(agents);
        ArrayList<Router> routers = get_routers(agents);
        ArrayList<Host> hosts = get_hosts(agents);
        
        ArrayList<AgentPair> switchpairs = get_pairs((ArrayList<Agent>)((Object)switches));
        ArrayList<AgentPair> routerpairs = get_pairs((ArrayList<Agent>)((Object)routers));
        ArrayList<AgentPair> switchrouterpairs = get_pairs((ArrayList<Agent>)((Object)switches),(ArrayList<Agent>)((Object)routers));
        ArrayList<AgentPair> routerhostpairs = get_pairs((ArrayList<Agent>)((Object)routers),(ArrayList<Agent>)((Object)hosts));
        ArrayList<AgentPair> switchhostpairs = get_pairs((ArrayList<Agent>)((Object)switches),(ArrayList<Agent>)((Object)hosts));
        
        ArrayList<InterfaceConnection> Connections = new ArrayList<>();
        
        System.out.println("Switch to switch");
        ArrayList<InterfaceConnection> switch_to_switch = Switch_to_switch_connectivity(switchpairs);
        Connections.addAll(switch_to_switch);
        System.out.println("Switch to router");
        ArrayList<InterfaceConnection> switch_to_router = Switch_to_router_connectivity(switchrouterpairs,Connections);
        System.out.println("Router to router");
        ArrayList<InterfaceConnection> router_to_router = Router_to_router_connectivity((ArrayList<Agent>)((Object)routers),Connections);
        System.out.println("Router to host");
        ArrayList<InterfaceConnection> router_to_host = Router_to_host_connectivity(routerhostpairs,Connections);
        System.out.println("Switch to host");
        ArrayList<InterfaceConnection> switch_to_host = Switch_to_host_connectivity(switchhostpairs,Connections);
        
        return Connections;
    
    }
    
    public static ArrayList<Switch> get_switches(ArrayList<Agent> agents){
        ArrayList<Switch> switches = new ArrayList<>();
        for(Agent agent:agents){
            if(agent.getClass().getSimpleName().equals("Switch")){
                switches.add((Switch)agent);
            }
        }
        return switches;
    }
    
    public static ArrayList<Router> get_routers(ArrayList<Agent> agents){
        ArrayList<Router> routers = new ArrayList<>();
        for(Agent agent:agents){
            if(agent.getClass().getSimpleName().equals("Router")){
                routers.add((Router)agent);
            }
        }
        return routers;
    }
    
    public static ArrayList<Host> get_hosts(ArrayList<Agent> agents){
        ArrayList<Host> hosts = new ArrayList<>();
        for(Agent agent:agents){
            if(agent.getClass().getSimpleName().equals("Host")){
                hosts.add((Host)agent);
            }
        }
        return hosts;
    }
    
    public static ArrayList<AgentPair> get_pairs(ArrayList<Agent> agents){
        ArrayList<AgentPair> agentspairs = new ArrayList<>();
        for(int i = 0 ; i < agents.size(); i++){
            for(int j = i+1 ; j < agents.size() ; j++){
                AgentPair pair = new AgentPair(agents.get(i),agents.get(j));
                agentspairs.add(pair);
            }
        }
        return agentspairs;
    }
    
    public static ArrayList<AgentPair> get_pairs(ArrayList<Agent> agents1,ArrayList<Agent> agents2){
        ArrayList<AgentPair> agentspairs = new ArrayList<>();
        for(int i = 0 ; i < agents1.size(); i++){
            for(int j = 0 ; j < agents2.size() ; j++){
                AgentPair pair = new AgentPair(agents1.get(i),agents2.get(j));
                agentspairs.add(pair);
            }
        }
        return agentspairs;
    }
    
    public static ArrayList<InterfaceConnection> Switch_to_switch_connectivity(ArrayList<AgentPair> switchpairs) throws IOException{
        ArrayList<InterfaceConnection> connections = new ArrayList<>();
        for (int i = 0; i < switchpairs.size(); i++) {
            String Switch1_ip = switchpairs.get(i).getAgent1().getIPAddress();
            String Switch2_ip = switchpairs.get(i).getAgent2().getIPAddress();
            ArrayList<String> Agent1_MacList = switchpairs.get(i).getAgent1().get_mac_addresses();
            ArrayList<String> Agent2_MacList = switchpairs.get(i).getAgent2().get_mac_addresses();
            for (String Agent1_MacList_Mac : Agent1_MacList) {
                if (MiscellaneousMethods.Mac_is_connected(Agent1_MacList_Mac, connections)) {
                    continue;
                }
                ArrayList<String> afts_Agent1 = SNMP_methods.getfromwalk_single(Switch1_ip, OIDS.dot1dTpFdbTable, OIDS.dot1dTpFdbAddress);
                for (String Agent2_MacList_Mac : Agent2_MacList) {
                    if (MiscellaneousMethods.Mac_is_connected(Agent2_MacList_Mac, connections)) {
                        continue;
                    }
                    ArrayList<String> afts_Agent2 = SNMP_methods.getfromwalk_single(Switch2_ip, OIDS.dot1dTpFdbTable, OIDS.dot1dTpFdbAddress);
                    if (afts_Agent1.contains(Agent2_MacList_Mac) && afts_Agent2.contains(Agent1_MacList_Mac)) {
                        Interface Switch1_Interface = switchpairs.get(i).getAgent1().GetInterface_byMacAddress(Agent1_MacList_Mac);
                        Interface Switch2_Interface = switchpairs.get(i).getAgent2().GetInterface_byMacAddress(Agent2_MacList_Mac);
                        InterfaceConnection connection = new InterfaceConnection(Switch1_Interface, Switch2_Interface, switchpairs.get(i).getAgent1(), switchpairs.get(i).getAgent2());
                        connections.add(connection);
                    }
                }
            }
        }
        return connections;
    }
    
    public static ArrayList<InterfaceConnection> Switch_to_router_connectivity(ArrayList<AgentPair> switchrouterpairs,ArrayList<InterfaceConnection> connections) throws IOException{
        for (int i = 0; i < switchrouterpairs.size(); i++) {
            String Switch_ip = switchrouterpairs.get(i).getAgent1().getIPAddress();
            String Router_ip = switchrouterpairs.get(i).getAgent2().getIPAddress();
            ArrayList<String> Switch_MacList = switchrouterpairs.get(i).getAgent1().get_mac_addresses();
            ArrayList<String> Router_MacList = switchrouterpairs.get(i).getAgent2().get_mac_addresses();
            for (String Switch_MacList_Mac : Switch_MacList) {
                if (MiscellaneousMethods.Mac_is_connected(Switch_MacList_Mac, connections)) {
                    continue;
                }
                ArrayList<String> afts_Switch = SNMP_methods.getfromwalk_single(Switch_ip, OIDS.dot1dTpFdbTable, OIDS.dot1dTpFdbAddress);
                ArrayList<String> aftsports_Switch = SNMP_methods.getfromwalk_single(Switch_ip, OIDS.dot1dTpFdbTable, OIDS.dot1dTpFdbPort);
                Interface interface_of_MAC = switchrouterpairs.get(i).getAgent1().GetInterface_byMacAddress(Switch_MacList_Mac);
                if(!aftsports_Switch.contains(interface_of_MAC.getIndex())){
                    continue;
                }
                ArrayList<Integer> portoccurunces_Switch = new ArrayList<>();
                ArrayList<Integer> removing_indices = new ArrayList<>();
                for(int p = 0 ; p < aftsports_Switch.size() ; p++){
                    int count = 0;
                    for(int l = 0 ; l < aftsports_Switch.size() ; l++){
                        if(aftsports_Switch.get(p).equals(aftsports_Switch.get(l))){
                            count++;
                        }
                    }
                    portoccurunces_Switch.add(count);
                }
                for(int p = 0 ; p < afts_Switch.size() ; p++){
                    if(portoccurunces_Switch.get(p) > 1){
                        removing_indices.add(p);
                    }
                }
             
                Collections.reverse(removing_indices);
                
                for(int p = 0 ; p < removing_indices.size() ; p++){
                    afts_Switch.remove((int)removing_indices.get(p));
                }
                
                for (String Router_MacList_Mac : Router_MacList) {
                    if(afts_Switch.contains(Router_MacList_Mac)){
                        Interface Switch_Interface = switchrouterpairs.get(i).getAgent1().GetInterface_byMacAddress(Switch_MacList_Mac);
                        Interface Router_Interface = switchrouterpairs.get(i).getAgent2().GetInterface_byMacAddress(Router_MacList_Mac);
                        InterfaceConnection connection = new InterfaceConnection(Switch_Interface, Router_Interface, switchrouterpairs.get(i).getAgent1(), switchrouterpairs.get(i).getAgent2());
                        connections.add(connection);
                    }
                }
            }
        }
        return connections;
    } 
    
    public static ArrayList<InterfaceConnection> Router_to_router_connectivity(ArrayList<Agent> routers, ArrayList<InterfaceConnection> Connections) throws IOException {
        for (int i = 0; i < routers.size(); i++) {
            Router A = (Router) routers.get(i);
            ArrayList<Interface> usedinf = A.get_UsedInterfaces();
            ArrayList<String> nexthops = SNMP_methods.getfromwalk_single(A.getIPAddress(),
                    OIDS.ipNetToMediaTable, OIDS.ipNetToMediaNetAddress);
            ArrayList<String> tempnh = (ArrayList<String>) nexthops.clone();
            for (String s : tempnh) {
                if (A.has_IPaddress(s)) {
                    nexthops.remove(s);
                } else if (!MiscellaneousMethods.isHostIP(s)) {
                    nexthops.remove(s);
                }
            }
            if (!usedinf.isEmpty()) {
                for (int j = 0; j < usedinf.size(); j++) {
                    
                    if (i == 0) {
                        for (String s : nexthops) {
                            String Mask = usedinf.get(j).getSubnet_mask();
                            String infNetAddress = MiscellaneousMethods.getNetworkIP(usedinf.get(j).getIp_address(), Mask);
                            String ipNetAddress = MiscellaneousMethods.getNetworkIP(s, Mask);
                            if (infNetAddress.equals(ipNetAddress)) {
                                Router B = (Router) get_Agent_by_Ip(routers, s);
                                if (B != null) {
                                    Interface B_Interface = B.get_Interface_by_Interface_IP_address_property(s);
                                    InterfaceConnection interfaceConnection = new InterfaceConnection(usedinf.get(j), B_Interface, A, B);
                                    Connections.add(interfaceConnection);
                                }
                            }
                        }
                    } else {
                        for (String s : nexthops) {
                            String Mask = usedinf.get(j).getSubnet_mask();
                            String infNetAddress = MiscellaneousMethods.getNetworkIP(usedinf.get(j).getIp_address(), Mask);
                            String ipNetAddress = MiscellaneousMethods.getNetworkIP(s, Mask);
                            if (infNetAddress.equals(ipNetAddress)) {
                                Router B = (Router) get_Agent_by_Ip(routers, s);
                                if (B != null && !has_similar_connection(Connections, A, B)) {
                                    Interface B_Interface = B.get_Interface_by_Interface_IP_address_property(s);
                                    InterfaceConnection interfaceConnection = new InterfaceConnection(usedinf.get(j), B_Interface, A, B);
                                    Connections.add(interfaceConnection);
                                }
                            }
                        }
                    }
                }
            }
        }
        return Connections;
    }
    
    public static ArrayList<InterfaceConnection> Router_to_host_connectivity(ArrayList<AgentPair> routerhostpairs, ArrayList<InterfaceConnection> Connections) throws IOException {
        for (int i = 0; i < routerhostpairs.size(); i++) {
            Router A = (Router) routerhostpairs.get(i).getAgent1();
            ArrayList<String> oids = new ArrayList<>();
            oids.add(OIDS.ipNetToMediaIfIndex);
            oids.add(OIDS.ipNetToMediaNetAddress);
            ArrayList<ArrayList<String>> nexthops = SNMP_methods.getfromwalk_multi(A.getIPAddress(),
                    OIDS.ipNetToMediaTable, oids);
            ArrayList<Integer> removing_indexes = new ArrayList<>();
            
            for (int n = 0 ; n < nexthops.get(0).size() ; n++){
                String ip = nexthops.get(1).get(n);
                if (A.has_IPaddress(ip)) {
                    removing_indexes.add(n);
                } else if (!MiscellaneousMethods.isHostIP(ip)) {
                    removing_indexes.add(n);
                }
            }
            
            Collections.reverse(removing_indexes);
            
            for(int n = 0 ; n < removing_indexes.size() ; n++){
                nexthops.get(0).remove((int)removing_indexes.get(n));
                nexthops.get(1).remove((int)removing_indexes.get(n));
            }
            
            ArrayList<Interface> usedinf = A.get_UsedInterfaces();
            ArrayList<Interface> tempintfs = (ArrayList<Interface>) usedinf.clone();
            for (Interface intf : tempintfs) {
                if(MiscellaneousMethods.IP_is_connected(intf.getIp_address(), Connections)){
                    usedinf.remove(intf);
                }
            }
            
            ArrayList<String> intfindex = new ArrayList<>();
            
            for (Interface intf : usedinf) {
                intfindex.add(intf.getIndex());
            }
            
            removing_indexes = new ArrayList<>();
            
            for (int n = 0 ; n < nexthops.get(0).size() ; n++){
                if (!intfindex.contains(nexthops.get(0).get(n))) {
                    removing_indexes.add(n);
                }
            }
            
            Collections.reverse(removing_indexes);
            
            for(int n = 0 ; n < removing_indexes.size() ; n++){
                nexthops.get(0).remove((int)removing_indexes.get(n));
                nexthops.get(1).remove((int)removing_indexes.get(n));
            }
            
            Host B = (Host) routerhostpairs.get(i).getAgent2();
            for(int n = 0 ; n < nexthops.get(0).size() ; n++){
                String ip = nexthops.get(1).get(n);
                if(ip.equals(B.getIPAddress())){
                    Interface interfaceA = A.GetInterface_index(nexthops.get(0).get(n));
                    Interface interfaceB = B.getAnInterface();
                    InterfaceConnection interfaceconnection = new InterfaceConnection(interfaceA, interfaceB, A, B);
                    Connections.add(interfaceconnection);
                    break;
                }
            }
            
        }
        return Connections;
    }
    
    public static ArrayList<InterfaceConnection> Switch_to_host_connectivity(ArrayList<AgentPair> switchhostpairs, ArrayList<InterfaceConnection> Connections) throws IOException {
        for(AgentPair switchhostpair : switchhostpairs){
            String host_IP = switchhostpair.getAgent2().getIPAddress();
            if(MiscellaneousMethods.IP_is_connected(host_IP, Connections)){
                continue;
            }
            String switch_IP = switchhostpair.getAgent1().getIPAddress();
            ArrayList<String> oids = new ArrayList<>();
            oids.add(OIDS.ipNetToMediaNetAddress);
            oids.add(OIDS.ipNetToMediaPhysAddress);
            ArrayList<ArrayList<String>> switch_arptable = SNMP_methods.getfromwalk_multi(switch_IP, OIDS.ipNetToMediaTable, oids);
            oids = new ArrayList<>();
            oids.add(OIDS.dot1dTpFdbAddress);
            oids.add(OIDS.dot1dTpFdbPort);
            ArrayList<ArrayList<String>> switch_macaddrtable = SNMP_methods.getfromwalk_multi(switch_IP, OIDS.dot1dTpFdbTable, oids);
            ArrayList<Integer> portoccurunces_Switch = new ArrayList<>();
            ArrayList<Integer> removing_indices = new ArrayList<>();
            for (int p = 0; p < switch_macaddrtable.get(1).size(); p++) {
                int count = 0;
                for (int l = 0; l < switch_macaddrtable.get(1).size(); l++) {
                    if (switch_macaddrtable.get(1).get(p).equals(switch_macaddrtable.get(1).get(l))) {
                        count++;
                    }
                }
                portoccurunces_Switch.add(count);
            }

            for (int p = 0; p < switch_macaddrtable.get(1).size(); p++) {
                if (portoccurunces_Switch.get(p) > 1) {
                    removing_indices.add(p);
                }
            }

            Collections.reverse(removing_indices);

            for (int p = 0; p < removing_indices.size(); p++) {
                switch_macaddrtable.get(0).remove((int) removing_indices.get(p));
                switch_macaddrtable.get(1).remove((int) removing_indices.get(p));
            }
            
            int indexofhost_in_arptable = switch_arptable.get(0).indexOf(host_IP);
            String mac_of_host = switch_arptable.get(1).get(indexofhost_in_arptable);
            
            for(int i = 0; i < switch_macaddrtable.get(0).size() ; i++){
                if(switch_macaddrtable.get(0).get(i).equals(mac_of_host)){
                    Switch switchA = (Switch)switchhostpair.getAgent1();
                    Host hostB = (Host)switchhostpair.getAgent2();
                    Interface switch_interface = switchA.GetInterface_index(switch_macaddrtable.get(1).get(i));
                    if(MiscellaneousMethods.Interface_is_connected(switch_interface, Connections)){
                        continue;
                    }
                    hostB.set_Mac_Address(mac_of_host);
                    Interface host_interface = hostB.getAnInterface();
                    InterfaceConnection interfaceConnection = new InterfaceConnection(switch_interface, host_interface, switchA, hostB);
                    Connections.add(interfaceConnection);
                    break;
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
        ArrayList<Interface> switcifs = new ArrayList<>();
        for (int i = 0; i < interfaces.get(0).size(); i++) {
            Interface routerif = new Interface(interfaces.get(0).get(i),
                    interfaces.get(2).get(i),
                    interfaces.get(3).get(i),
                    interfaces.get(4).get(i),
                    interfaces.get(1).get(i));
            switcifs.add(routerif);
        }
        Router router = new Router(sysdescr, sysname, switcifs);
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
        ArrayList<String> ips = new ArrayList<>();
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
        }
        interfaces.add(ips);
        ArrayList<Interface> switchifs = new ArrayList<>();
        for (int i = 0; i < interfaces.get(0).size(); i++) {
            Interface switchif = new Interface(interfaces.get(0).get(i),
                    interfaces.get(2).get(i),
                    interfaces.get(3).get(i),
                    interfaces.get(1).get(i));
            switchifs.add(switchif);
        }
        Switch switchh = new Switch(sysdescr, sysname, switchifs);
        return switchh;
    }
    
    public static ArrayList<InterfaceConnection> Filter_Connections(ArrayList<InterfaceConnection> connections){
        ArrayList<InterfaceConnection> temp = (ArrayList<InterfaceConnection>) connections.clone();
        for (InterfaceConnection connection : connections) {
            if(connection.getInterfaceA().getDescription().contains("Vlan") || connection.getInterfaceB().getDescription().contains("Vlan")){
                temp.remove(connection);
            }else if(connection.getInterfaceA().getDescription().contains("vlan") || connection.getInterfaceB().getDescription().contains("vlan")){
                temp.remove(connection);
            }else if(connection.getInterfaceA().getDescription().contains("Null") || connection.getInterfaceB().getDescription().contains("Null")){
                temp.remove(connection);
            }else if(connection.getInterfaceA().getDescription().contains("null") || connection.getInterfaceB().getDescription().contains("null")){
                temp.remove(connection);
            }
        }
        return temp;
    }
}

















/*public static ArrayList<Connection> create_connections(ArrayList<Agent> agents) throws IOException{
        ArrayList<Connection> Connections = new ArrayList<>();
        for (int i = 0; i < agents.size(); i++) {
            Agent A = agents.get(i);
            if (!A.getClass().getSimpleName().equals("Host")) {
                ArrayList<Interface> usedinf = A.get_UsedInterfaces();
                if (!usedinf.isEmpty()) {
                    for (int j = 0; j < usedinf.size(); j++) {
                        ArrayList<String> nexthops = SNMP_methods.getfromwalk_single(usedinf.get(j).getIp_address(),
                                OIDS.ipNetToMediaTable, OIDS.ipNetToMediaNetAddress);
                        ArrayList<String> tempnh = (ArrayList<String>) nexthops.clone();
                        for (String s : tempnh) {
                            if (A.has_IPaddress(s)) {
                                nexthops.remove(s);
                            } else if (!MiscellaneousMethods.isHostIP(s)) {
                                nexthops.remove(s);
                            }
                        }
                        if (A.getClass().getSimpleName().equals("Router")) {
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
                        }else{
                            
                        }
                    }
                }
            }
        }
        return Connections;
    }*/