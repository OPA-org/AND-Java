package and;

import java.util.ArrayList;

public class Host extends Agent{
    private Interface anInterface;

    public Host(Interface anInterface) {
        super(false);
        this.anInterface = anInterface;
    }

    public Interface getAnInterface() {
        return anInterface;
    }

    public void setAnInterface(Interface anInterface) {
        this.anInterface = anInterface;
    }
    
    @Override
    public Boolean has_IPaddress(String IP){
        return this.anInterface.getIp_address().equals(IP);
    }

    @Override
    public String toString() {
        return "PC of IP: " + anInterface.getIp_address();
    }

    @Override
    public ArrayList<Interface> get_UsedInterfaces() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
