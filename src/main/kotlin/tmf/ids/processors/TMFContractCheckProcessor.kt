
package tmf.ids.processors

import de.fhg.aisec.ids.camel.idscp2.Constants.IDSCP2_HEADER
import de.fhg.aisec.ids.camel.idscp2.Constants.IDS_TYPE
import de.fraunhofer.iais.eis.ArtifactRequestMessage
import de.fraunhofer.iais.eis.ArtifactResponseMessage
import de.fraunhofer.iais.eis.ContractAgreementMessage
import de.fraunhofer.iais.eis.ContractOfferMessage
import de.fraunhofer.iais.eis.ContractRejectionMessage
import de.fraunhofer.iais.eis.ContractRequestMessage
import de.fraunhofer.iais.eis.ContractResponseMessage
import de.fraunhofer.iais.eis.Message
import de.fraunhofer.iais.eis.RejectionMessage
import de.fraunhofer.iais.eis.ResourceUpdateMessage
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.slf4j.LoggerFactory

class TMFContractCheckProcessor : Processor {
    override fun process(exchange: Exchange) {
        processHeader(exchange)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(TMFContractCheckProcessor::class.java)

        fun processHeader(exchange: Exchange) {
            if (LOG.isDebugEnabled) {
                LOG.debug("[IN] ${TMFContractCheckProcessor::class.java.simpleName}")
            }
            exchange.message.getHeader(IDSCP2_HEADER, Message::class.java)?.let { header ->
                val messageType = when (header) {
                    is ArtifactRequestMessage -> ArtifactRequestMessage::class.simpleName
                    is ArtifactResponseMessage -> ArtifactResponseMessage::class.simpleName
                    is ContractRequestMessage -> ContractRequestMessage::class.simpleName
                    is ContractResponseMessage -> ContractResponseMessage::class.simpleName
                    is ContractOfferMessage -> ContractOfferMessage::class.simpleName
                    is ContractAgreementMessage -> ContractAgreementMessage::class.simpleName
                    is ContractRejectionMessage -> ContractRejectionMessage::class.simpleName
                    is ResourceUpdateMessage -> ResourceUpdateMessage::class.simpleName
                    is RejectionMessage -> RejectionMessage::class.simpleName
                    else -> header::class.simpleName
                }
                if (LOG.isDebugEnabled) {
                    LOG.debug("Detected ids-type: {}", messageType)
                }
                exchange.setProperty(IDS_TYPE, messageType)
            }
        }
    }
}