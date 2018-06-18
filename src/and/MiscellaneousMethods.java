package and;

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
    
}
