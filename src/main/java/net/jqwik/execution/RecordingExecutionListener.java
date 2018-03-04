package net.jqwik.execution;

import net.jqwik.api.*;
import net.jqwik.properties.*;
import net.jqwik.recording.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.reporting.*;

import java.util.*;

public class RecordingExecutionListener implements EngineExecutionListener {

	private final TestRunRecorder recorder;
	private final EngineExecutionListener listener;
	private Map<TestDescriptor, String> seeds = new IdentityHashMap<>();

	RecordingExecutionListener(TestRunRecorder recorder, EngineExecutionListener listener) {
		this.recorder = recorder;
		this.listener = listener;
	}

	@Override
	public void dynamicTestRegistered(TestDescriptor testDescriptor) {
		listener.dynamicTestRegistered(testDescriptor);
	}

	@Override
	public void executionSkipped(TestDescriptor testDescriptor, String reason) {
		listener.executionSkipped(testDescriptor, reason);
	}

	@Override
	public void executionStarted(TestDescriptor testDescriptor) {
		listener.executionStarted(testDescriptor);
	}

	@Override
	public void executionFinished(TestDescriptor testDescriptor, TestExecutionResult testExecutionResult) {
		recordTestRun(testDescriptor, testExecutionResult);
		listener.executionFinished(testDescriptor, testExecutionResult);
	}

	private void recordTestRun(TestDescriptor testDescriptor, TestExecutionResult testExecutionResult) {
		String seed = seeds.computeIfAbsent(testDescriptor, ignore -> Property.SEED_NOT_SET);
		TestRun run = new TestRun(testDescriptor.getUniqueId(), testExecutionResult.getStatus(), seed);
		recorder.record(run);
	}

	@Override
	public void reportingEntryPublished(TestDescriptor testDescriptor, ReportEntry entry) {
		rememberSeed(testDescriptor, entry);
		listener.reportingEntryPublished(testDescriptor, entry);
	}

	private void rememberSeed(TestDescriptor testDescriptor, ReportEntry entry) {
		Map<String, String> entries = entry.getKeyValuePairs();
		if (entries.containsKey(CheckResultReportEntry.SEED_REPORT_KEY)) {
			String reportedSeed = getReportedSeed(entries);
			seeds.put(testDescriptor, reportedSeed);
		}
	}

	private String getReportedSeed(Map<String, String> entries) {
		String reportedSeed = Property.SEED_NOT_SET;
		try {
			reportedSeed = entries.get(CheckResultReportEntry.SEED_REPORT_KEY);
		} catch (NumberFormatException ignore) {
		}
		return reportedSeed;
	}
}
