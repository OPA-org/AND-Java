package and;

import java.util.ArrayList;

public class Router extends Agent{
    private ArrayList<Interface> interfaces;
    private String sysDescr;
    private String sysName;
    public Router(String sysDescr,String sysName,ArrayList<Interface> interfaces) {
        super(false);
        this.sysDescr = sysDescr;
        this.sysName = sysName;
        this.interfaces = interfaces;
    }
    
    public Router(String sysDescr,String sysName){
        super(false);
        this.sysDescr = sysDescr;
        this.sysName = sysName;
        this.interfaces = new ArrayList<>();
    }
    
    public Router() {
        super(false);
        this.sysDescr = "";
        this.sysName = "";
        this.interfaces = new ArrayList<>();
    }

    public ArrayList<Interface> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(ArrayList<Interface> interfaces) {
        this.interfaces = interfaces;
    }
    
    public void add_Interface(Interface anInterface){
        interfaces.add(anInterface);
    }
    
    public Interface get_Interface_by_ID_in_list(int i){
        return interfaces.get(i);
    }
    
    /*public Interface get_Interface_by_Interface_number_property(int number){
        for (int i=0;i<interfaces.size();i++){
            if (interfaces.get(i).getNumber()==number)
                return interfaces.get(i);
        }
        return null;
    }*/
    
    public Interface get_Interface_by_Interface_IP_address_property(String ip_address){
        for (int i=0;i<interfaces.size();i++){
            if (interfaces.get(i).getIp_address().equals(ip_address))
                return interfaces.get(i);
        }
        return null;
    }
    
    public Interface get_Interface_by_Interface_MAC_address_property(String mac_address){
        for (int i=0;i<interfaces.size();i++){
            if (interfaces.get(i).getMac_address()==mac_address)
                return interfaces.get(i);
        }
        return null;
    }
    
    public Interface get_Interface_by_Interface_Connected_to_property(String connected_to){
        for (int i=0;i<interfaces.size();i++){
            if (interfaces.get(i).getConnected_to()==connected_to)
                return interfaces.get(i);
        }
        return null;
    }
    
    @Override
    public Boolean has_IPaddress(String IP){
        for(Interface inter: this.interfaces){
            if(inter.getIp_address().equals(IP)){
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return sysName;
    }
    
    public ArrayList<Interface> get_UsedInterfaces(){
        ArrayList<Interface> used = new ArrayList<>();
        for(Interface inf: this.interfaces){
            if(!inf.getIp_address().isEmpty()){
                used.add(inf);
            }
        }
        return used;
    }
}
