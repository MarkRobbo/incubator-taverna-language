package net.sf.taverna.scufl2.rdf.port;

import net.sf.taverna.scufl2.rdf.common.Ontology;

import org.openrdf.elmo.annotations.rdf;

@rdf(Ontology.CORE + "TransmittingPort")
public interface SenderPort extends Port {

}