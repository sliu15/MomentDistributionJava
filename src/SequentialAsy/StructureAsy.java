package SequentialAsy;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author Shu Liu
 */
public class StructureAsy {

    public static StructureAsy createStructureFromFile(String filename) {
        try {
            Scanner input = new Scanner(new File(filename));
            StructureAsy structure = new StructureAsy();
            int numOfNodes = input.nextInt();
            System.out.print("Reading from file " + filename + " with " + numOfNodes + " nodes...");
            for (int i = 0; i < numOfNodes; i++) {
                structure.addNode(new NodeAsy(input.nextInt(), input.next().startsWith("F")));
            }
            int numOfBeams = input.nextInt();
            for (int i = 0; i < numOfBeams; i++) {
                structure.connectNodes(input.nextInt(), input.nextDouble(), input.nextDouble(), input.nextDouble(), input.nextInt(), input.nextDouble(), input.nextDouble(), input.nextDouble());
            }
            structure.normalize();
            System.out.println(" Done");
            return structure;
        } catch (FileNotFoundException ex) {
            System.out.println("File not found");
            return null;
        }
    }

    HashMap<Integer, NodeAsy> nodeMap;

    public StructureAsy() {
        nodeMap = new HashMap();
    }

    public void addNode(NodeAsy newNode) {
        if (nodeMap.containsKey(newNode.id)) {
            System.out.println("Warning: " + newNode.toString() + " already exists");
        } else {
            nodeMap.put(newNode.id, newNode);
        }
    }

    public void connectNodes(int nodeID1, double df1, double cof1, double moment1,
            int nodeID2, double df2, double cof2, double moment2) {

        if (!nodeMap.containsKey(nodeID1)) {
            System.out.println("Error: node " + nodeID1 + " doesn't exists in structure");
            return;
        }
        if (!nodeMap.containsKey(nodeID2)) {
            System.out.println("Error: node " + nodeID2 + " doesn't exists in structure");
            return;
        }

        NodeAsy node1 = nodeMap.get(nodeID1);
        NodeAsy node2 = nodeMap.get(nodeID2);

        BeamAsy beam1 = new BeamAsy();
        BeamAsy beam2 = new BeamAsy();

        node1.beams.add(beam1);
        node2.beams.add(beam2);

        beam1.df = df1;
        beam1.cof = cof1;
        beam1.moment = moment1;
        beam1.otherEndNode = node2;
        beam1.otherEndBeam = beam2;

        beam2.df = df2;
        beam2.cof = cof2;
        beam2.moment = moment2;
        beam2.otherEndNode = node1;
        beam2.otherEndBeam = beam1;
    }

    public void normalize() {
        Iterator<Map.Entry<Integer, NodeAsy>> iter = nodeMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Integer, NodeAsy> entry = iter.next();
            if (entry.getValue().isIsolated()) {
                iter.remove();
            } else {
                entry.getValue().normalize();
            }
        }
    }

    public void printStructure() {
        System.out.println("Total number of nodes: " + nodeMap.size());
        for (NodeAsy node : nodeMap.values()) {
            System.out.println(node);
        }
    }

    public NodeAsy[] getNodes() {
        NodeAsy[] nodes = new NodeAsy[nodeMap.size()];
        int index = 0;
        for (NodeAsy node : nodeMap.values()) {
            nodes[index] = node;
            index++;
        }
        return nodes;
    }

    public double getMaxUnbalanced() {
        double max = 0;
        for (NodeAsy node : nodeMap.values()) {
            max = Math.max(max, Math.abs(node.getUnbalancedMoment()));
        }

        return max;
    }
}
