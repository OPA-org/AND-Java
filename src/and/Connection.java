package and;

public class Connection {
    private int ID;
    private Agent A;
    private Agent B;
    private String Subnet;
    private String Mask;
    private static int counter = 1;
    
    public Connection(Agent A, Agent B,String Subnet,String Mask) {
        this.ID = this.counter++;
        this.A = A;
        this.B = B;
        this.Subnet = Subnet;
        this.Mask = Mask;
    }

    public String getSubnet() {
        return Subnet;
    }

    public void setSubnet(String Subnet) {
        this.Subnet = Subnet;
    }

    public String getMask() {
        return Mask;
    }

    public void setMask(String Mask) {
        this.Mask = Mask;
    }
    
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Agent getA() {
        return A;
    }

    public void setA(Agent A) {
        this.A = A;
    }

    public Agent getB() {
        return B;
    }

    public void setB(Agent B) {
        this.B = B;
    }
    
}
