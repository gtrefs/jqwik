package net.jqwik.execution.pipeline;

import net.jqwik.*;

public class PredecessorNotSubmittedException extends JqwikException {

	public PredecessorNotSubmittedException(ExecutionTask task, ExecutionTask predecessor) {
		super(String.format("Predecessor [%s] must be submitted before [%s] can be run.", predecessor.toString(), task.toString()));
	}
}
