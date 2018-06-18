package and;

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
    
    @Override
    public abstract String toString();
}
