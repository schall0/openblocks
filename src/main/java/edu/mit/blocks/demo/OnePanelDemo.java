package edu.mit.blocks.demo;

import javax.swing.*;
import java.awt.*;

public class OnePanelDemo {
    public static void main(String[] args) {

        JFrame frame = new JFrame("Open Blocks Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(100, 100, 800, 500);
        frame.add(new DemoPanel(), BorderLayout.CENTER);
        frame.setVisible(true);
    }

}
