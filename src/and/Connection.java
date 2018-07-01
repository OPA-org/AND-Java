/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package and;

public class Connection {
    private Interface interfaceA;
    private Interface interfaceB;
    private Agent agentA;
    private Agent agentB;
    private String Type;
    
    public Connection(Interface interfaceA, Interface interfaceB, Agent agentA, Agent agentB, String Type) {
        this.interfaceA = interfaceA;
        this.interfaceB = interfaceB;
        this.agentA = agentA;
        this.agentB = agentB;
        this.Type = Type;
    }
    
    public Interface getInterfaceA() {
        return interfaceA;
    }

    public void setInterfaceA(Interface interfaceA) {
        this.interfaceA = interfaceA;
    }

    public Interface getInterfaceB() {
        return interfaceB;
    }

    public void setInterfaceB(Interface interfaceB) {
        this.interfaceB = interfaceB;
    }

    public Agent getAgentA() {
        return agentA;
    }

    public void setAgentA(Agent agentA) {
        this.agentA = agentA;
    }

    public Agent getAgentB() {
        return agentB;
    }

    public void setAgentB(Agent agentB) {
        this.agentB = agentB;
    }

    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }
    
    public String toString(){
        return Type +"\n"+agentA.toString() +"\n" + interfaceA.toString() + "\n" + agentB.toString() + "\n" + interfaceB.toString();
    }
    
}
