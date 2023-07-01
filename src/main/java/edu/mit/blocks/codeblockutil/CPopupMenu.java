package edu.mit.blocks.codeblockutil;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import edu.mit.blocks.codeblockutil.CScrollPane.ScrollPolicy;

public class CPopupMenu extends JPopupMenu implements ActionListener {

    private static final long serialVersionUID = 328149080311L;
    private JComponent view;
    private int items = 0;
    //private int MARGIN = 20;

    CPopupMenu() {
        super();
        this.setLayout(new BorderLayout());
        Color background = CGraphite.white;
        this.setBackground(background);
        this.setOpaque(false);
        this.removeAll();
        this.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, CGraphite.blue));
        view = new JPanel(new GridLayout(0, 1));
        view.setBackground(background);
        JComponent scroll = new CTracklessScrollPane(view,
                ScrollPolicy.VERTICAL_BAR_AS_NEEDED,
                ScrollPolicy.HORIZONTAL_BAR_NEVER,
                9, CGraphite.blue, background);
        this.add(scroll);
    }

    @Override
    public Insets getInsets() {
        return new Insets(5, 5, 5, 5);
    }

    public void add(CMenuItem item) {
        items++;
        item.addActionListener(this);
        view.add(item);
    }

    public void setZoomLevel(double zoom) {
        // change the size of the panel and the text
        int ITEM_HEIGHT = 14;
        view.setPreferredSize(new Dimension((int) (100 * zoom), (int) (items * ITEM_HEIGHT * zoom)));
        this.setPopupSize((int) (100 * zoom), (int) (Math.min(items * ITEM_HEIGHT + 10, 100) * zoom));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (this.isVisible()) {
            this.setVisible(false);
        }
    }

}
