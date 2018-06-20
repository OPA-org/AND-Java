package and;

public class Interface  {
    private String index;
    private String description;
    private String ip_address;
    private String subnet_mask;
    private String mac_address;
    private String connected_to;

    public Interface(String index,String description, String ip_address, String subnet_mask, String mac_address, String connected_to) {
        this.index = index;
        this.description = description;
        this.ip_address = ip_address;
        this.subnet_mask = subnet_mask;
        this.mac_address = mac_address;
        this.connected_to = connected_to;
    }
    
    public Interface(String index,String description, String ip_address, String subnet_mask, String mac_address) {
        this.index = index;
        this.description = description;
        this.ip_address = ip_address;
        this.subnet_mask = subnet_mask;
        this.mac_address = mac_address;
    }
    
    public Interface(String index,String description, String ip_address, String mac_address) {
        this.index = index;
        this.description = description;
        this.ip_address = ip_address;
        this.mac_address = mac_address;
    }
    
    public Interface(String ip_address){
        this.ip_address = ip_address;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public String getSubnet_mask() {
        return subnet_mask;
    }

    public void setSubnet_mask(String subnet_mask) {
        this.subnet_mask = subnet_mask;
    }

    public String getMac_address() {
        return mac_address;
    }

    public void setMac_address(String mac_address) {
        this.mac_address = mac_address;
    }

    public String getConnected_to() {
        return connected_to;
    }

    public void setConnected_to(String connected_to) {
        this.connected_to = connected_to;
    }
    
    @Override
    public String toString() {
        if(subnet_mask != null){
            return "Interface\n"+"\tindex: "+this.index +"\n\tip: "+ip_address+"\n\tmask: "+subnet_mask+"\n\tmac: "+mac_address;
        }else{
            return "Interface\n"+"\tindex: "+this.index +"\n\tip: "+ip_address+"\n\ttype: "+mac_address;
        }
    }
}
