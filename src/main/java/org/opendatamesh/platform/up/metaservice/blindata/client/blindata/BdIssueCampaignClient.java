package org.opendatamesh.platform.up.metaservice.blindata.client.blindata;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.issuemngt.BDIssueCampaignRes;

import java.util.Optional;

public interface BdIssueCampaignClient {
    Optional<BDIssueCampaignRes> getIssueCampaign(String campaignName);

    BDIssueCampaignRes createCampaign(BDIssueCampaignRes newIssueCampaign);
}
