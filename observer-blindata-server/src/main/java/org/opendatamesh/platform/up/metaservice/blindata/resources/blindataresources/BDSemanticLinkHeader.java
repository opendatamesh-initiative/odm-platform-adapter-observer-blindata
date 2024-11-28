package org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

public class BDSemanticLinkHeader {

    private SemanticLinkHeaderId id;

    private BDLogicaFieldRes physicalField;


    private BDLogicaFieldRes logicalField;

    private String defaultNamespaceIdentifier;

    private String semanticLinkString;

    private Set<BDSemanticLinkElement> semanticLinkElements = new HashSet<>();
}
