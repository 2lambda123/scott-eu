package se.ericsson.cf.scott.sandbox.whc.xtra.planning

import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import org.eclipse.lyo.oslc4j.core.model.Link
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI

/**
 * TODO
 *
 * @since   TODO
 */
class PlanRequestBuilder {
    companion object {
        val log: Logger = LoggerFactory.getLogger(PlanRequestBuilder::class.java)
    }

    private lateinit var problemBuilder: ProblemBuilder

    private lateinit var domain: Model

    fun build(baseURI: URI): Model {
        // TODO Andrew@2019-02-19: remove all use of URIs beforehand?

        val m = ModelFactory.createDefaultModel()

        m.add(domain)

        val state: ProblemRequestState = problemBuilder.build(baseURI)
        m.add(state.model)

        // FIXME Andrew@2019-02-20: where is the min fn added?

        log.debug("Completed building a planning request")

        return m
    }

    fun domainFromModel(domain: Model): PlanRequestBuilder {
        this.domain = domain
        return this
    }

    fun withStateBuilder(problemBuilder: ProblemBuilder): PlanRequestBuilder {
        this.problemBuilder = problemBuilder
        return this
    }


}
