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

class TMFArtifactRequestProcessor : TMFProcessor() {

    override fun process(exchange: Exchange) {
        if (LOG.isDebugEnabled) {
            LOG.debug("[IN] ${this::class.java.simpleName}")
        }

        val artifactRequestMessage = exchange.message.getHeader(
            IDSCP2_HEADER, ArtifactRequestMessage::class.java
        )

        val requestedArtifact = artifactRequestMessage.requestedArtifact

        val usedContract = ProviderDB.artifactUrisMapped2ContractAgreements[requestedArtifact]

        if (LOG.isDebugEnabled) {
            LOG.debug("Contract for requested Artifact found {}", usedContract)
        }

        if (!ProviderDB.availableArtifactURIs.containsKey(requestedArtifact)) {
            createRejectionMessage(exchange, artifactRequestMessage, RejectionReason.NOT_FOUND)
        } else if (!ProviderDB.contractAgreements.containsKey(usedContract)) {
            createRejectionMessage(exchange, artifactRequestMessage, RejectionReason.NOT_AUTHORIZED)
        } else {

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


            // Proceed normally and send ArtifactResponseMessage
            ArtifactResponseMessageBuilder().run {
                _correlationMessage_(artifactRequestMessage.id)
                _transferContract_(usedContract)            
                let {
                    if (LOG.isDebugEnabled) {
                        LOG.debug("Serialisation header: {}", SERIALIZER.serialize(it.build()))
                    }
                    exchange.message.setHeader(IDSCP2_HEADER, it)
                }
            }
            LOG.debug("Before Serialisation body: ")

            SERIALIZER.serialize(rData).let {
                if (LOG.isDebugEnabled) {
                    LOG.debug("Serialisation body: {}", it)
                }
                exchange.message.body = it
            }
            LOG.debug("After Serialisation body: ")
        }
    }


    private fun createRejectionMessage(
        exchange: Exchange,
        artifactRequestMessage: ArtifactRequestMessage,
        rejectionReason: RejectionReason
    ) {
        if (LOG.isDebugEnabled) {
            LOG.debug("Constructing RejectionMessage for requested artifact: {}", rejectionReason)
        }
        RejectionMessageBuilder()
            ._correlationMessage_(artifactRequestMessage.correlationMessage)
            ._rejectionReason_(rejectionReason)
            .let {
                if (LOG.isDebugEnabled) {
                    LOG.debug("Serialisation header: {}", SERIALIZER.serialize(it.build()))
                }
                exchange.message.setHeader(IDSCP2_HEADER, it)
            }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(this::class.java)
    }
}
