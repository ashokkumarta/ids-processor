package tmf.ids.processors

import de.fhg.aisec.ids.camel.idscp2.Constants.ARTIFACT_URI_PROPERTY
import de.fhg.aisec.ids.camel.idscp2.Constants.IDSCP2_HEADER
import de.fhg.aisec.ids.camel.idscp2.ProviderDB
import de.fhg.aisec.ids.camel.idscp2.Utils
import de.fhg.aisec.ids.camel.idscp2.Utils.SERIALIZER
import de.fraunhofer.iais.eis.*
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import java.net.URI

class TMFContractInitProcessor : TMFProcessor() {

    override fun process(exchange: Exchange) {
        if (LOG.isDebugEnabled) {
            LOG.debug("[IN] ${this::class.java.simpleName}")
        }

        ArtifactRequestMessageBuilder().run {
            exchange.getProperty(ARTIFACT_URI_PROPERTY)?.let {
                if (it is URI) {
                    it
                } else {
                    URI.create(it.toString())
                }
            }?.let {
                _requestedArtifact_(it)
            }
            let {
                if (LOG.isDebugEnabled) {
                    LOG.debug("Serialisation header: {}", SERIALIZER.serialize(it.build()))
                }
                exchange.message.setHeader(IDSCP2_HEADER, "TMFContractInitMessage")
            }
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(this::class.java)
    }
}
