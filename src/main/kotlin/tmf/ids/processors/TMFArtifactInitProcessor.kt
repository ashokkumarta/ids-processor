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

class TMFArtifactInitProcessor : TMFProcessor() {

    override fun process(exchange: Exchange) {

        LOG.info("[IN] ${this::class.java.simpleName}")

        exchange.getProperty(ARTIFACT_URI_PROPERTY)?.let {
            LOG.info("Processing URI: {}", it)
            if (it is URI) {
                it
            } else {
                URI.create(it.toString())
            }
        }?.let {
            LOG.info("Adding URI: {} to provider DB", it)
            ProviderDB.availableArtifactURIs[it] = "AVAILABLE"
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(this::class.java)
    }
}
