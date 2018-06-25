package and;

import java.util.ArrayList;

public class MiscellaneousMethods {
    
    public static Boolean isHostIP(String IP) {
        if(IP.endsWith(".0") || IP.endsWith(".255")){
           return false;
        }
        return true;
    }
    
    public static Boolean isNetworkIP(String IP) {
        return IP.endsWith(".0");
    }
    
    public static Boolean isBroadcastIP(String IP) {
        return IP.endsWith(".255");
    }
    
    public static String getNetworkIP(String IP,String Mask){
        ArrayList<Integer> MaskVals = splitIP(Mask);
        ArrayList<Integer> IPVals = splitIP(IP);
        String Networkip = "";
        for(int i = 0 ; i < MaskVals.size() ; i++){
            if(MaskVals.get(i) == 0){
                Networkip += "0.";
            }else if(MaskVals.get(i) == 255){
                Networkip += IPVals.get(i).toString() + ".";
            }else{
                String MaskBin = converttobinary(MaskVals.get(i));
                String IPBin = converttobinary(IPVals.get(i));
                int LastOneIndex = MaskBin.lastIndexOf("1")+1;
                String IPnetwork = IPBin.substring(0, LastOneIndex);
                int ipoctet = converttoint(IPnetwork);
                Networkip += ipoctet + "."; 
            }
        }
        Networkip = Networkip.substring(0, Networkip.length()-1);
        return Networkip;
    }
    
    public static Boolean isIPinSubnet(String IP,String NetworkIP,String NetworkMask){
        if(getNetworkIP(IP, NetworkMask).equals(NetworkIP)){
            return true;
        }else{
            return false;
        }
    }
    
    public static String converttobinary(int n){
        String Binary = Integer.toBinaryString(n);
        if(Binary.length() < 8){
            int addno = 8 - Binary.length();
            String zeros = "";
            for(int i = 0 ; i < addno ; i++){
                zeros += "0";
            }
            Binary = zeros + Binary;
        }
        return Binary;
    }
    
    public static int converttoint(String binary){
        if(binary.length() < 8){
            int diff = 8 - binary.length();
            for(int i = 0 ; i < diff ; i++){
                binary += "0";
            }
        }
        return Integer.parseInt(binary, 2);
    }
    
    public static ArrayList<Integer> splitIP(String IP){
        String[] octets = IP.split("\\.");
        ArrayList<Integer> octetsval = new ArrayList<>();
        for (String octet : octets) {
            octetsval.add(Integer.valueOf(octet));
        }
        return octetsval;
    }
    
    public static Boolean Mac_is_connected(String Mac_Address, ArrayList<InterfaceConnection> connections){
        for (InterfaceConnection connection : connections) {
            if(connection.getInterfaceA().getMac_address().equals(Mac_Address)||connection.getInterfaceB().getMac_address().equals(Mac_Address)){
                return true;
            }
        }
        return false;
    }
    
    public static Boolean IP_is_connected(String IP_Address, ArrayList<InterfaceConnection> connections){
        for (InterfaceConnection connection : connections) {
            if(connection.getInterfaceA().getIp_address().equals(IP_Address)||connection.getInterfaceB().getIp_address().equals(IP_Address)){
                return true;
            }
        }
        return false;
    }
    
    public static Boolean Interface_is_connected(Interface intf, ArrayList<InterfaceConnection> connections){
        for (InterfaceConnection connection : connections) {
            if(connection.getInterfaceA().equals(intf)||connection.getInterfaceB().equals(intf)){
                return true;
            }
        }
        return false;
    }
    
}
