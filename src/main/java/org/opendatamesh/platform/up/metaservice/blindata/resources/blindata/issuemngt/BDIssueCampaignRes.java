package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.issuemngt;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration.BDShortUserRes;

import java.util.Date;


public class BDIssueCampaignRes {
	private String uuid;
	private String name;
	private String description;
	private BDShortUserRes creator;
	private BDShortUserRes owner;
	private Date startDate;
	private Date endDate;
	private String status;
	private Integer openIssues;
	private Integer closedIssues;
	private Boolean isPrivate;

	public BDIssueCampaignRes() {
		//DO NOTHING
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BDShortUserRes getCreator() {
		return creator;
	}

	public void setCreator(BDShortUserRes creator) {
		this.creator = creator;
	}

	public BDShortUserRes getOwner() {
		return owner;
	}

	public void setOwner(BDShortUserRes owner) {
		this.owner = owner;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getOpenIssues() {
		return openIssues;
	}

	public void setOpenIssues(Integer openIssues) {
		this.openIssues = openIssues;
	}

	public Integer getClosedIssues() {
		return closedIssues;
	}

	public void setClosedIssues(Integer closedIssues) {
		this.closedIssues = closedIssues;
	}

	public Boolean getPrivate() {
		return isPrivate;
	}

	public void setPrivate(Boolean aPrivate) {
		isPrivate = aPrivate;
	}

	@Override
	public String toString() {
		return "BDIssueCampaignRes{" +
				"uuid='" + uuid + '\'' +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", creator=" + creator +
				", owner=" + owner +
				", startDate=" + startDate +
				", endDate=" + endDate +
				", status='" + status + '\'' +
				", openIssues=" + openIssues +
				", closedIssues=" + closedIssues +
				", isPrivate=" + isPrivate +
				'}';
	}
}
