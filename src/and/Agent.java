package and;

import java.util.ArrayList;

public abstract class Agent {

    private boolean visited;

    public Agent(boolean visited) {
        this.visited = visited;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public abstract Boolean has_IPaddress(String IP);

    public abstract ArrayList<Interface> get_UsedInterfaces();

    public abstract String getIPAddress();

    public abstract Interface GetInterface_byMacAddress(String mac_address);
    
    public abstract Interface GetInterface_byindex(String index);
    
    public abstract ArrayList<String> get_mac_addresses();

    @Override
    public abstract String toString();
}
