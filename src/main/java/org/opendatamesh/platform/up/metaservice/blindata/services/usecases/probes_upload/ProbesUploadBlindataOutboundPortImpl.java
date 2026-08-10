package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.probes_upload;

import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdProbesClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.probes.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;
import java.util.function.Predicate;

class ProbesUploadBlindataOutboundPortImpl implements ProbesUploadBlindataOutboundPort {

    private static final int PAGE_SIZE = 100;

    private final BdProbesClient bdProbesClient;

    ProbesUploadBlindataOutboundPortImpl(BdProbesClient bdProbesClient) {
        this.bdProbesClient = bdProbesClient;
    }

    @Override
    public Optional<BDQualityProbesProjectRes> findProbeProjectByName(String projectName) {
        if (!StringUtils.hasText(projectName)) {
            return Optional.empty();
        }
        QualityProbesProjectSearchOptions searchOptions = new QualityProbesProjectSearchOptions();
        searchOptions.setSearch(projectName);
        return findFirstMatch(
                pageNumber -> bdProbesClient.getProjects(PageRequest.of(pageNumber, PAGE_SIZE), searchOptions),
                project -> Objects.equals(projectName, project.getName())
        );
    }

    @Override
    public BDQualityProbesProjectRes createProbeProject(BDQualityProbesProjectRes project) {
        return bdProbesClient.createProject(project);
    }

    @Override
    public Optional<BDQualityProbesDefinitionRes> findProbeDefinitionByProjectAndName(String projectUuid, String probeName) {
        if (!StringUtils.hasText(projectUuid) || !StringUtils.hasText(probeName)) {
            return Optional.empty();
        }
        QualityProbesDefinitionSearchOptions searchOptions = new QualityProbesDefinitionSearchOptions();
        searchOptions.setProjectUuid(projectUuid);
        searchOptions.setSearch(probeName);
        searchOptions.setLastVersion(Boolean.TRUE);
        return findFirstMatch(
                pageNumber -> bdProbesClient.getDefinitions(PageRequest.of(pageNumber, PAGE_SIZE), searchOptions),
                definition -> Objects.equals(probeName, definition.getName())
        );
    }

    @Override
    public BDQualityProbesDefinitionRes createProbeDefinition(BDQualityProbesDefinitionRes definition) {
        return bdProbesClient.createDefinition(definition);
    }

    @Override
    public BDQualityProbesDefinitionRes overwriteProbeDefinition(String rootUuid, BDQualityProbesDefinitionRes definition) {
        return bdProbesClient.overwriteDefinition(rootUuid, definition);
    }

    @Override
    public Optional<BDQualityProbesConnectionRes> findProbeConnectionByName(String connectionName) {
        if (!StringUtils.hasText(connectionName)) {
            return Optional.empty();
        }
        QualityProbesConnectionSearchOptions searchOptions = new QualityProbesConnectionSearchOptions();
        searchOptions.setSearch(connectionName);
        return findFirstMatch(
                pageNumber -> bdProbesClient.getConnections(PageRequest.of(pageNumber, PAGE_SIZE), searchOptions),
                connection -> Objects.equals(connectionName, connection.getName())
        );
    }

    @Override
    public Optional<BDQualityProbesTagRes> findTagByProjectAndName(String projectUuid, String tagName) {
        if (!StringUtils.hasText(projectUuid) || !StringUtils.hasText(tagName)) {
            return Optional.empty();
        }
        QualityProbesTagSearchOptions searchOptions = new QualityProbesTagSearchOptions();
        searchOptions.setProjectUuid(projectUuid);
        searchOptions.setSearch(tagName);
        return findFirstMatch(
                pageNumber -> bdProbesClient.getTags(PageRequest.of(pageNumber, PAGE_SIZE), searchOptions),
                tag -> Objects.equals(tagName, tag.getName())
        );
    }

    @Override
    public void deleteTag(String tagUuid) {
        bdProbesClient.deleteTag(tagUuid);
    }

    @Override
    public BDQualityProbesTagRes createTag(BDQualityProbesTagRes tag) {
        return bdProbesClient.createTag(tag);
    }

    /**
     * Searches are filtered server side, so a page smaller than the requested size is the last one to fetch.
     */
    private <T> Optional<T> findFirstMatch(IntFunction<Page<T>> pageLoader, Predicate<T> matcher) {
        int pageNumber = 0;
        Page<T> page;
        do {
            page = pageLoader.apply(pageNumber);
            Optional<T> match = page.getContent().stream().filter(matcher).findFirst();
            if (match.isPresent()) {
                return match;
            }
            pageNumber++;
        } while (page.getNumberOfElements() == PAGE_SIZE && page.hasNext());
        return Optional.empty();
    }
}
