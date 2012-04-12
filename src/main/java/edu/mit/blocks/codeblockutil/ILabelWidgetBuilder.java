package edu.mit.blocks.codeblockutil;

import java.awt.Color;

import edu.mit.blocks.renderable.BlockLabel;

/**
 * Enable to customize LabelWidget creation from outside the framework
 * 
 * @author laurentschall
 */
public interface ILabelWidgetBuilder {

	LabelWidget buildLabelWidget(BlockLabel blockLabel, String initLabelText, Color fieldColor, Color tooltipBackground, BlockLabel.Type labelType);

}
