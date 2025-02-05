package edu.mit.blocks.codeblocks;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;

import edu.mit.blocks.renderable.RenderableBlock;

import edu.mit.blocks.workspace.Workspace;
import edu.mit.blocks.workspace.WorkspaceListener;

/**
 * <code>BlockLinkChecker</code> determines if two <code>Block</code> objects can connect.  In particular, 
 * <code>BlockLinkChecker</code> will report which sockets of the two <code>Block</code> objects can connect.
 * Interested <code>Block</code> objects may make a static call to canLink() to determine if it can link to another
 * <code>Block</code> object.
 * <p>
 * <code>BlockLinkChecker</code> uses a list of <code>LinkRule</code>s to check the <code>Connector</code>s of each
 * <code>Block</code>.  Rules may be added, inserted, and removed from the checker.  
 * <p>
 * There is only one instance of the <code>BlockLinkChecker</code>.
 */
public class BlockLinkChecker {

    /**
     * Adds a rule to the end of this checker's list of rules.
     * If the rule already exists in the rule list, the rule is removed in the original location and 
     * added to the end of the list.
     * @param rule the desired LinkRule to be added
     */
    public static void addRule(Workspace workspace, LinkRule rule) {
    	workspace.getEnv().getRules().add(rule);
        if (rule instanceof WorkspaceListener) {
            workspace.addWorkspaceListener((WorkspaceListener) rule);
        }
    }

    /**
     * Returns a BlockLink instance if the two specified blocks can connect at the specified 
     * block connectors at each block; null if no link is possible.
     * @param workspace The workspace in use  
     * @param block1 Block instance to compare 
     * @param block2 Block instance to compare
     * @param con1 the BlockConnector at block1 to compare against con2
     * @param con2 the BlockConnector at block2 to compare against con1
     */
    public static BlockLink canLink(Workspace workspace, Block block1, Block block2, BlockConnector con1, BlockConnector con2) {
        if (checkRules(workspace, block1, block2, con1, con2)) {
            return BlockLink.getBlockLink(workspace, block1, block2, con1, con2);
        }

        return null;
    }

    /**
     * Checks to see if a <code>RenderableBlock</code>s can connect to other <code>RenderableBlock</code>s.
     * This would mean that they have <code>BlockConnector</code>s that satisfy at least one of the <code>LinkRule</code>s,
     * and that these sockets are in close proximity.
     * @param workspace The workspace in use
     * @param rblock1 one of the blocks to check
     * @param otherBlocks the other blocks to check against
     * @return a <code>BlockLink</code> object that gives the two closest matching <code>BlockConnector</code>s in these blocks,
     * or null if no such matching exists.
     */
    public static BlockLink getLink(Workspace workspace, RenderableBlock rblock1, Iterable<RenderableBlock> otherBlocks) {
        Block block1 = workspace.getEnv().getBlock(rblock1.getBlockID());
        BlockConnector closestSocket1 = null;
        BlockConnector closestSocket2 = null;
        Block closestBlock2 = null;
        // TODO get a better value
        double MAX_LINK_DISTANCE = 20.0;
        double closestDistance = MAX_LINK_DISTANCE;
        double currentDistance;

        for (RenderableBlock rblock2 : otherBlocks) {
            BlockConnector currentPlug = getPlugEquivalent(block1);
            Block block2 = workspace.getEnv().getBlock(rblock2.getBlockID());
            if (block1.equals(block2) || !rblock1.isVisible() || !rblock2.isVisible() || rblock1.isCollapsed() || rblock2.isCollapsed()) {
                continue;
            }

            Point2D currentPlugPoint = null;
            Point2D currentSocketPoint = null;
            if (currentPlug != null) {
                currentPlugPoint = getAbsoluteSocketPoint(rblock1, currentPlug);
                for (BlockConnector currentSocket : getSocketEquivalents(block2)) {
                    currentSocketPoint = getAbsoluteSocketPoint(rblock2, currentSocket);
                    currentDistance = currentPlugPoint.distance(currentSocketPoint);
                    if ((currentDistance < closestDistance) && checkRules(workspace, block1, block2, currentPlug, currentSocket)) {
                        closestBlock2 = block2;
                        closestSocket1 = currentPlug;
                        closestSocket2 = currentSocket;
                        closestDistance = currentDistance;
                    }
                }
            }

            currentPlug = getPlugEquivalent(block2);
            if (currentPlug != null) {
                currentPlugPoint = getAbsoluteSocketPoint(rblock2, currentPlug);
                for (BlockConnector currentSocket : getSocketEquivalents(block1)) {
                    currentSocketPoint = getAbsoluteSocketPoint(rblock1, currentSocket);
                    currentDistance = currentPlugPoint.distance(currentSocketPoint);
                    if ((currentDistance < closestDistance) && checkRules(workspace, block1, block2, currentSocket, currentPlug)) {
                        closestBlock2 = block2;
                        closestSocket1 = currentSocket;
                        closestSocket2 = currentPlug;
                        closestDistance = currentDistance;
                    }
                }
            }
        }

        if (closestSocket1 == null) {
            return null;
        }

        return BlockLink.getBlockLink(workspace, block1, closestBlock2, closestSocket1, closestSocket2);
    }

    /**
     * Checks if a potential link satisfies ANY of the rules loaded into the link checker
     * @param block1 one Block in the potential link
     * @param block2 the other Block
     * @param socket1 the BlockConnector from block1 in the potential link
     * @param socket2 the BlockConnector from block2
     * @return true if the pairing of block1 and block2 at socket1 and socket2 passes any rules, false otherwise
     */
    private static boolean checkRules(Workspace workspace, Block block1, Block block2, BlockConnector socket1, BlockConnector socket2) {
        ArrayList<LinkRule> rules = workspace.getEnv().getRules();
    	Iterator<LinkRule> rulesList = Collections.unmodifiableList(rules).iterator();
        LinkRule currentRule = null;
        boolean foundRule = false;
        while (rulesList.hasNext()) {
            currentRule = rulesList.next();
            boolean canLink = currentRule.canLink(block1, block2, socket1, socket2);
            if (!currentRule.isMandatory()) {
                foundRule |= canLink;
            } else if (!canLink) {
                return false;
            }
        }
        return foundRule;
    }

    /**
     * Gets the screen coordinate of the center of a socket.
     * @param block the RenderableBlock containting the socket
     * @param socket the desired socket
     * @return a Point2D that represents the center of the socket on the screen.
     */
    private static Point2D getAbsoluteSocketPoint(RenderableBlock block, BlockConnector socket) {
        Point2D relativePoint = block.getSocketPixelPoint(socket);
        Point2D blockPosition = block.getLocationOnScreen();
        return new Point2D.Double(relativePoint.getX() + blockPosition.getX(), relativePoint.getY() + blockPosition.getY());
    }

    public static boolean hasPlugEquivalent(Block b) {
        if (b == null) {
            return false;
        }
        boolean hasPlug = b.hasPlug();
        boolean hasBefore = b.hasBeforeConnector();
        // Should have at most one plug-type connector
        assert (!(hasPlug & hasBefore));
        return hasPlug | hasBefore;
    }

    public static BlockConnector getPlugEquivalent(Block b) {
        if (!hasPlugEquivalent(b)) {
            return null;
        }
        if (b.hasPlug()) {
            return b.getPlug();
        }
        return b.getBeforeConnector();
    }

    public static Iterable<BlockConnector> getSocketEquivalents(Block b) {
        if (b == null) {
            return new ArrayList<BlockConnector>();
        }
        if (!b.hasAfterConnector()) {
            return b.getSockets();
        }
        ArrayList<BlockConnector> socketEquivalents = new ArrayList<BlockConnector>();
        for (BlockConnector socket : b.getSockets()) {
            socketEquivalents.add(socket);
        }
        socketEquivalents.add(b.getAfterConnector());
        return Collections.unmodifiableList(socketEquivalents);
    }

}
