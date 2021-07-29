package tmf.ids.processors

import de.fhg.aisec.ids.camel.idscp2.Constants.IDSCP2_HEADER
import de.fhg.aisec.ids.camel.idscp2.Utils
import de.fhg.aisec.ids.camel.idscp2.Utils.SERIALIZER
import de.fraunhofer.iais.eis.*
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.slf4j.LoggerFactory
import java.util.*


val TMF_CONTENT_MODEL_KEY = "tmf_content_model"
val TMF_CONTENT_MODEL_VERSION_KEY = "tmf_content_model_version"
val TMF_CONTENT_PROVIDER_KEY = "tmf_content_provider"
val TMF_CONTENT_ISSUED_AT_KEY = "tmf_content_issued_at"
val TMF_CONTENT_USAGE_TNC_KEY = "tmf_content_usage_tnc"
val TMF_CONTENT_UID_KEY = "tmf_content_uid"
val TMF_CONTENT_PROVIDER_EMAIL_KEY = "tmf_content_provider_email"
val TMF_CONTENT_PROVIDER_PRIMARY_PHONE_KEY = "tmf_content_provider_primary_phone"
val TMF_CONTENT_PROVIDER_QUOTE_KEY = "tmf_content_provider_quote"
val TMF_CONTENT_PROVIDER_QUOTE_MASKED_KEY = "tmf_content_provider_quote_masked"
val TMF_CONTENT_PAY_LOAD = "tmf_content_pay_load"
val TMF_CONTENT_PROVIDER_PRIMARY_PHONE_MASKED_KEY = "tmf_content_provider_primary_phone_masked"

val TMF_CONTENT_MODEL = "TMF Generic Model"
val TMF_CONTENT_MODEL_VERSION = "0.1-ex"
val TMF_CONTENT_PROVIDER = "A Confidential Provider"
val TMF_CONTENT_USAGE_TNC = "TMF or its participants assume no responsibility and/or liability for any data exchanged on this platform."
val TMF_CONTENT_NOT_FOUND = "Could not serve the requested content."
val TMF_CONTENT_PROVIDER_EMAIL = "Confidential@Secret.org"
val TMF_CONTENT_PROVIDER_PRIMARY_PHONE = "123-456-7890"
val TMF_CONTENT_PROVIDER_PRIMARY_PHONE_MASKED = "123-XXX-XX90"
val TMF_CONTENT_PROVIDER_QUOTE = "49.95"
val TMF_CONTENT_PROVIDER_QUOTE_MASKED = "**.**"


open class TMFProcessor : Processor {

    override fun process(exchange: Exchange) {
        if (LOG.isDebugEnabled) {
            LOG.debug("[IN] ${this::class.java.simpleName}")
        }

        val wData = ConnectorUpdateMessageBuilder().build()
        wData.setProperty("tmf_message", "Welcome! You have joined the trusted tmf connector. Happy exchanging data...")

        // Proceed normally and send ArtifactResponseMessage
        ArtifactResponseMessageBuilder().run {
            let {
                if (LOG.isDebugEnabled) {
                    LOG.debug("Serialisation header: {}", SERIALIZER.serialize(it.build()))
                }
                exchange.message.setHeader(IDSCP2_HEADER, it)
            }
        }
        SERIALIZER.serialize(wData).let {
            if (LOG.isDebugEnabled) {
                LOG.debug("Serialisation body: {}", it)
            }
            exchange.message.body = it
        }
    }

    public fun createDigitalContent(content: String) : DigitalContent {
        val dc = createDigitalContent()
        dc.setProperty(TMF_CONTENT_PAY_LOAD, content)
        return dc
    }

    private fun createDigitalContent() : DigitalContent {
        // create sample artifact
        val artifactDate = Utils.createGregorianCalendarTimestamp(System.currentTimeMillis())
        val random = Random()

        val dc = DataResourceBuilder().build()
        dc.setProperty(TMF_CONTENT_MODEL_KEY, TMF_CONTENT_MODEL)
        dc.setProperty(TMF_CONTENT_MODEL_VERSION_KEY, TMF_CONTENT_MODEL_VERSION)
        dc.setProperty(TMF_CONTENT_PROVIDER_KEY, TMF_CONTENT_PROVIDER)
        dc.setProperty(TMF_CONTENT_ISSUED_AT_KEY, artifactDate)
        dc.setProperty(TMF_CONTENT_USAGE_TNC_KEY, TMF_CONTENT_USAGE_TNC)
        dc.setProperty(TMF_CONTENT_UID_KEY, UUID.randomUUID())

        dc.setProperty(TMF_CONTENT_PROVIDER_EMAIL_KEY, TMF_CONTENT_PROVIDER_EMAIL)
        if(random.nextBoolean()) {
            dc.setProperty(TMF_CONTENT_PROVIDER_PRIMARY_PHONE_KEY, TMF_CONTENT_PROVIDER_PRIMARY_PHONE)
        } else {
            dc.setProperty(TMF_CONTENT_PROVIDER_PRIMARY_PHONE_MASKED_KEY, TMF_CONTENT_PROVIDER_PRIMARY_PHONE_MASKED)
        }

        if(random.nextBoolean()) {
            dc.setProperty(TMF_CONTENT_PROVIDER_QUOTE_KEY, TMF_CONTENT_PROVIDER_QUOTE)
        } else {
            dc.setProperty(TMF_CONTENT_PROVIDER_QUOTE_MASKED_KEY, TMF_CONTENT_PROVIDER_QUOTE_MASKED)
        }

        return dc
    }


    companion object {
        private val LOG = LoggerFactory.getLogger(TMFArtifactResponseProcessor::class.java)
    }
}
