package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.issuemngt;

public enum BDIssueSeverityLevelRes {
	BLOCKER(5000),
	CRITICAL(4000),
	MAJOR(3000),
	MINOR(2000),
	INFO(1000);

	public final Integer value;

	private BDIssueSeverityLevelRes(Integer value) {
		this.value = value;
	}
}
