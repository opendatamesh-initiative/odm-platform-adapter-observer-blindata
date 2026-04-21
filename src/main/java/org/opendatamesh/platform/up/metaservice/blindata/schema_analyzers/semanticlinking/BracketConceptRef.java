package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Concept reference parsed from the inner text of a leading bracket segment
 * (e.g. {@code Stock} or {@code lux:ProductSku} from {@code [Stock]} / {@code [lux:ProductSku]}).
 */
final class BracketConceptRef {

    private static final Pattern PREFIXED_INNER = Pattern.compile("^([^:]+):(.+)$");

    private final Optional<String> namespacePrefix;
    private final String conceptName;

    private BracketConceptRef(String namespacePrefix, String conceptName) {
        this.namespacePrefix = Optional.ofNullable(namespacePrefix);
        this.conceptName = conceptName;
    }

    static BracketConceptRef parse(String innerBracketText) {
        Matcher m = PREFIXED_INNER.matcher(innerBracketText);
        if (m.matches()) {
            return new BracketConceptRef(m.group(1), m.group(2));
        }
        return new BracketConceptRef(null, innerBracketText);
    }

    Optional<String> getNamespacePrefix() {
        return namespacePrefix;
    }

    String getConceptName() {
        return conceptName;
    }
}
