package edu.mit.blocks.codeblockutil.custom;

import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.renderable.BlockLabel;
import edu.mit.blocks.renderable.NameLabel;
import edu.mit.blocks.workspace.Workspace;

/**
 * Default BlockLabel builder invoked if no custom builder is specified
 * 
 * @author laurentschall
 */
public class DefaultBlockLabelBuilder implements IBlockLabelBuilder {

	public NameLabel buildBlockLabel(Workspace workspace, Long blockID) {
		Block block = workspace.getEnv().getBlock(blockID);
		return new NameLabel(workspace, block.getBlockLabel(), BlockLabel.Type.NAME_LABEL, block.isLabelEditable(), blockID);
	}

}
