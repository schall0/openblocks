package edu.mit.blocks.codeblockutil.custom;

import edu.mit.blocks.renderable.NameLabel;
import edu.mit.blocks.workspace.Workspace;

/**
 * Enable to customize BlockLabel creation from outside the framework
 * 
 * @author laurentschall
 */
public interface IBlockLabelBuilder {

	NameLabel buildBlockLabel(Workspace workspace, Long blockID);

}
