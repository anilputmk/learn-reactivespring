package com.learnreactivespring.router;

import java.util.*;
import java.util.stream.Collectors;

public class TreeNodeTraversal {

    public class Node{
        int data;
        Node left, right;
        int level;

        public Node(int data) {
            this.data = data;
            left = right = null;
            this.level = 0;
        }
    }

    private Node buildNode() {
        Node root = new Node(1);
        root.level = 0;

        Node parent = new Node(2);
        Node leftChild = new Node(4);
        Node rightChild = new Node(5);
        parent.right = rightChild;
        parent.left = leftChild;
        root.left = parent;

        parent = new Node(3);
        leftChild = new Node(6);
        rightChild = new Node(7);
        parent.right = rightChild;
        parent.left = leftChild;

        root.right = parent;

        return root;
    }

    public  void findDiagonalSum() {
        Node treeNode = buildNode();
        Map<Integer, Integer> map = new HashMap<>();

        Queue<Node> queue = new LinkedList<>();
        queue.add(treeNode);

        while(!queue.isEmpty()) {
            Node current = queue.remove();

            int level = current.level;
            while (current != null) {

                int prevSum = map.get(level) == null ? 0 : map.get(level);
                map.put(level, prevSum + current.data);

                if (current.left != null) {
                    current.left.level = current.level + 1;
                    queue.add(current.left);
                }

                current = current.right;
            }
        }

        map.values().stream().forEach(value -> System.out.println(value));
    }
}
