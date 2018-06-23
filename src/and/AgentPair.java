package and;

public class AgentPair implements Comparable<Object>{
    Agent agent1;
    Agent agent2;

    public AgentPair(Agent agent1, Agent agent2) {
        this.agent1 = agent1;
        this.agent2 = agent2;
    }

    public Agent getAgent1() {
        return agent1;
    }

    public void setAgent1(Agent agent1) {
        this.agent1 = agent1;
    }

    public Agent getAgent2() {
        return agent2;
    }

    public void setAgent2(Agent agent2) {
        this.agent2 = agent2;
    }

    @Override
    public int compareTo(Object o) {
        AgentPair pair2 = (AgentPair) o;
        if ((this.agent1 == pair2.agent1 ||this.agent1 == pair2.agent2 )&& (this.agent2 == pair2.agent1 ||this.agent2 == pair2.agent2 )) {
            return 0;
        }
       return -1;
    }
    
}
