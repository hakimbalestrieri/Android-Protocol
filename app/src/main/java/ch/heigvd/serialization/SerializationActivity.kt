package ch.heigvd.serialization

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import ch.heigvd.*
import ch.heigvd.databinding.ActivitySerializationBinding
import ch.heigvd.serialization.protobuf.DirectoryOuterClass
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import org.w3c.dom.DocumentType
import java.io.StringWriter
import java.lang.Exception
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 * Activity in which is realized the point of the laboratory about serialization
 * @author Allemann, Balestrieri, Gomes
 */
class SerializationActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySerializationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_serialization)

        // Binding components
        binding = ActivitySerializationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Adding listener on button to send JSON data
        binding.btnSendAsJSON.setOnClickListener {
            if (validateForm()) {
                val directory = Directory(mutableListOf(getPersonToSend()))
                SymComManager(object : CommunicationEventListener {
                    override fun handleServerResponse(response: Any) {
                        try {
                            val directory =
                                Json.decodeFromString(Directory.serializer(), response as String)
                            binding.txtResult.text = directory.people.toString()
                        } catch (exception: Exception) {
                            exception.message?.let { it1 -> Log.d(TAG, it1) }
                            binding.txtResult.text = "An error occurred, check debug logs"
                        }
                    }
                }).sendRequest(
                    getString(R.string.api_json),
                    Json.encodeToString(directory),
                    mapOf("content-type" to "application/json")
                )
                resetForm()
            }

        }

        // Adding listener on button to send XML data
        binding.btnSendXML.setOnClickListener {
            if (validateForm()) {
                val directory = Directory(mutableListOf(getPersonToSend()))

                // Generate XML document
                val docBuilder: DocumentBuilder =
                    DocumentBuilderFactory.newInstance().newDocumentBuilder()
                val document = docBuilder.newDocument()
                val directoryXML = Directory.serializeAsXML(directory, document)
                document.appendChild(directoryXML)

                // Transform document to XML
                val transformer = TransformerFactory.newInstance().newTransformer()
                transformer.setOutputProperty(
                    OutputKeys.DOCTYPE_SYSTEM,
                    getString(R.string.api_dtd_url)
                )
                val outWriter = StringWriter()
                transformer.transform(DOMSource(document), StreamResult(StringWriter()))

                // Send request
                SymComManager(object : CommunicationEventListener {
                    override fun handleServerResponse(response: Any) {
                        try {
                            val directory = Directory.deserializeXML(response as String)
                            binding.txtResult.text = directory.people.toString()
                        } catch (exception: Exception) {
                            exception.message?.let { it1 -> Log.d(TAG, it1) }
                            binding.txtResult.text = "An error occurred, check debug logs"
                        }
                    }

                }).sendRequest(
                    getString(R.string.api_xml),
                    outWriter.buffer.toString(),
                    mapOf("content-type" to "application/xml")
                )
            }
            resetForm()
        }

        // Adding listener on button to send Protobuf data
        binding.btnSendAsProtoBuf.setOnClickListener {
            if (validateForm()) {
                val directory = Directory(mutableListOf(getPersonToSend()))
                SymComManager(object : CommunicationEventListener {
                    override fun handleServerResponse(response: Any) {
                        try {
                            val directory = Directory.deserializeProtobuf(response as ByteArray)
                            binding.txtResult.text = directory.people.toString()
                        } catch (exception: Exception) {
                            exception.message?.let { it1 -> Log.d(TAG, it1) }
                            binding.txtResult.text = "An error occurred, check debug logs"
                        }
                    }
                }).sendRequest(
                    getString(R.string.api_protobuf),
                    Directory.serializeAsProtoBuf(directory),
                    mapOf("content-type" to "application/protobuf")
                )
                resetForm()
            }
        }
    }

    /**
     * Validate that required fields are not empty
     * Return if the form is valid or not
     */
    private fun validateForm(): Boolean {
        if (binding.tbxName.text.toString() == "") {
            binding.tbxName.error = "This field is required"
            return false
        }
        if (binding.tbxFirstName.text.toString() == "") {
            binding.tbxFirstName.error = "This field is required"
            return false
        }
        if (binding.tbxHomeNumber.text.toString() == ""
            && binding.tbxMobileNumber.text.toString() == ""
            && binding.tbxWorkNumber.text.toString() == ""
        ) {
            binding.tbxHomeNumber.error = "At least one phone number has to be set"
            binding.tbxMobileNumber.error = "At least one phone number has to be set"
            binding.tbxWorkNumber.error = "At least one phone number has to be set"
            return false
        }
        return true
    }

    /**
     * Get the person matching form data
     * @return a Person object ready to be serialized
     */
    private fun getPersonToSend(): Person {
        // Data retrieval
        val homeNumber = binding.tbxHomeNumber.text.toString()
        val mobileNumber = binding.tbxMobileNumber.text.toString()
        val workNumber = binding.tbxWorkNumber.text.toString()

        // Get phone numbers
        val phones = mutableListOf<Phone>()
        val homePhone = if (homeNumber == "") null else Phone(Phone.Type.HOME, homeNumber)
        if (homePhone != null) phones.add(homePhone)
        val mobilePhone = if (mobileNumber == "") null else Phone(Phone.Type.MOBILE, mobileNumber)
        if (mobilePhone != null) phones.add(mobilePhone)
        val workPhone = if (workNumber == "") null else Phone(Phone.Type.WORK, workNumber)
        if (workPhone != null) phones.add(workPhone)

        return Person(
            binding.tbxName.text.toString(),
            binding.tbxFirstName.text.toString(),
            binding.tbxMiddleName.text.toString(),
            phones.toList()
        )
    }

    /**
     * Reset form fields
     */
    private fun resetForm() {
        binding.tbxName.text.clear()
        binding.tbxFirstName.text.clear()
        binding.tbxMiddleName.text.clear()
        binding.tbxHomeNumber.text.clear()
        binding.tbxMobileNumber.text.clear()
        binding.tbxWorkNumber.text.clear()
    }

    companion object {
        private val TAG = SerializationActivity::class.java.simpleName
    }
}