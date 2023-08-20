package edu.mit.blocks.demo;

import edu.mit.blocks.controller.WorkspaceController;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;

class DemoPanel extends JPanel {
    DemoPanel() {
        final WorkspaceController wc = new WorkspaceController();
        wc.setLangDefFilePath("support/lang_def.xml");
        wc.loadFreshWorkspace();

        wc.loadProjectFromPath("data/demo1.xml");

        setLayout(new GridLayout(1,1));
        add(wc.getWorkspacePanel());
    }
}
