package and;

public class Connection {
    private int ID;
    private Agent A;
    private Agent B;
    private static int counter = 1;
    public Connection(Agent A, Agent B) {
        this.ID = this.counter++;
        this.A = A;
        this.B = B;
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
