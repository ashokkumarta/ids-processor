package tmf.ids.processors

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

class TMFArtifactResponseProcessor : TMFProcessor() {

    override fun process(exchange: Exchange) {
        if (LOG.isDebugEnabled) {
            LOG.debug("[IN] ${this::class.java.simpleName}")
        }

        // Proceed normally and send ArtifactResponseMessage
        ArtifactResponseMessageBuilder().run {
            let {
                if (LOG.isDebugEnabled) {
                    LOG.debug("Serialisation header: {}", SERIALIZER.serialize(it.build()))
                }
                exchange.message.setHeader(IDSCP2_HEADER, it)
            }
        }


        val payLoad = exchange.getProperty(TMF_CONTENT_PAY_LOAD)?.let {
            if (it is String) {
                it
            } else {
                it.toString()
            }
        }?: TMF_CONTENT_NOT_FOUND

        var data: ArrayList<DigitalContent> = ArrayList<DigitalContent>()
        data.add(createDigitalContent(payLoad))
        val artifactDate = Utils.createGregorianCalendarTimestamp(System.currentTimeMillis())

        val rData = DataResourceBuilder().run {
            _contentPart_(data)
            _version_(TMF_CONTENT_MODEL_VERSION)
            _created_(artifactDate)
        }.build()

        // create sample artifact

        LOG.debug("Before Serialisation body: ")

        SERIALIZER.serialize(rData).let {
            if (LOG.isDebugEnabled) {
                LOG.debug("Serialisation body: {}", it)
            }
            exchange.message.body = it
        }
        LOG.debug("After Serialisation body: ")
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(this::class.java)
    }
}
