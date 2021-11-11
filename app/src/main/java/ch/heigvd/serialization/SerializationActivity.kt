package ch.heigvd.serialization

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ch.heigvd.*
import ch.heigvd.databinding.ActivitySerializationBinding
import ch.heigvd.serialization.protobuf.DirectoryOuterClass
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import org.w3c.dom.Document
import org.w3c.dom.DocumentType
import org.w3c.dom.Element
import java.io.StringWriter
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
            SymComManager(object : CommunicationEventListener {
                override fun handleServerResponse(response: String) {
                    val result = Json.decodeFromString(SimplePerson.serializer(), response)
                    binding.txtResult.text = result.toString()
                }
            }).sendRequest(
                getString(R.string.api_json),
                Json.encodeToString(
                    SimplePerson(
                        binding.tbxName.text.toString(),
                        binding.tbxFirstName.text.toString()
                    )
                ),
                "application/json"
            )
            resetForm()
        }

        // Adding listener on button to send XML data
        binding.btnSendXML.setOnClickListener {
            val person = getXMLPerson()
            val docBuilder : DocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            val document = docBuilder.newDocument()
            val rootElement = document.createElement("directory")
            val personElement = document.createElement("person")
            val nameElement = document.createElement("name")
            nameElement.appendChild(document.createTextNode(person.name))
            personElement.appendChild(nameElement)
            val firstnameElement = document.createElement("firstname")
            firstnameElement.appendChild(document.createTextNode(person.firstname))
            personElement.appendChild(firstnameElement)
            val middlenameElement = document.createElement("middlename")
            middlenameElement.appendChild(document.createTextNode(person.middlename))
            personElement.appendChild(middlenameElement)
            val homePhoneElement = document.createElement("phone")
            homePhoneElement.setAttribute("type", "home")
            person.phone.forEach {
                val phoneElement = document.createElement("phone")
                phoneElement.setAttribute("type", it.type.type)
                phoneElement.appendChild(document.createTextNode(it.number))
                personElement.appendChild(phoneElement)
            }
            rootElement.appendChild(personElement)
            document.appendChild(rootElement)

            val doctype: DocumentType = document.implementation.createDocumentType(
                "ONMETQUOIICI",
                "ONMETQUOIICI",
                "http://mobile.iict.ch/directory.dtd",
            )

            val transformer = TransformerFactory.newInstance().newTransformer()
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.systemId)
            val outWriter = StringWriter()
            val result = StreamResult( outWriter )
            transformer.transform( DOMSource(document), result )
            val sb = outWriter.buffer
            val finalstring = sb.toString()
            if (validateForm()) {

                val xml = finalstring
                SymComManager(object : CommunicationEventListener {
                    override fun handleServerResponse(response: String) {

                    }
                }).sendRequest(
                    getString(R.string.api_xml),
                    xml,
                    "application/xml"
                )
            }
            resetForm()
        }

        // Adding listener on button to send Protobuf data
        binding.btnSendAsProtoBuf.setOnClickListener {

            SymComManagerProtobuf(object : CommunicationEventListenerProtobuf {
                override fun handleServerResponseByteArray(response: ByteArray) {
                    unserializeProtobuf(response)
                }
            }).sendRequest(
                getString(R.string.api_protobuf),
                serializeProtobuf(),
                "application/protobuf"
            )
            resetForm()
        }

    }


    /**
     * Deserialize Protobuf
     *
     */
    private fun unserializeProtobuf(input : ByteArray){

        var inputProtobuf = DirectoryOuterClass.Directory.newBuilder().mergeFrom(input);
        var personProtobuf = inputProtobuf.getResults(0)


        var name = personProtobuf.name
        var firstname = personProtobuf.firstname
        var middlename = personProtobuf.middlename

        val phones : MutableList<Phone> = mutableListOf()

        for (phone in personProtobuf.phoneList){

            val type = phone.type

            var typeTxt = Phone.Type.home

            when (type) {
                DirectoryOuterClass.Phone.Type.HOME -> typeTxt = Phone.Type.home
                DirectoryOuterClass.Phone.Type.MOBILE -> typeTxt = Phone.Type.mobile
                DirectoryOuterClass.Phone.Type.WORK ->  typeTxt = Phone.Type.work
            }

            var phoneIn = Phone(typeTxt,phone.number)
            phones.add(phoneIn)
        }

        val person = Person(personProtobuf.name,personProtobuf.firstname,personProtobuf.middlename,phones)

        binding.txtResult.text = person.toString()
    }


    /**
     * Serialize Protobuf
     * Return a biteArray
     */
    private fun serializeProtobuf() : ByteArray {


        val person = getXMLPerson() //TODO : getXML ? getString plutôt non ?

        val protobufPerson = DirectoryOuterClass.Person.newBuilder().setFirstname(person.firstname)
            .setMiddlename(person.middlename).setName(person.name)

        var typeProtobuf : DirectoryOuterClass.Phone.Type
        typeProtobuf = DirectoryOuterClass.Phone.Type.HOME //Demande une init

        for(i in person.phone) {

            //TODO : Faire des vérifs
            when (i.type.toString()) {
                "home" ->  typeProtobuf = DirectoryOuterClass.Phone.Type.HOME
                "mobile" -> typeProtobuf = DirectoryOuterClass.Phone.Type.MOBILE
                "work" -> typeProtobuf = DirectoryOuterClass.Phone.Type.WORK
            }
            protobufPerson.addPhoneBuilder().setNumber(i.number).setType(typeProtobuf)


        }
        var protobufDirectory = DirectoryOuterClass.Directory.newBuilder().addResults(protobufPerson).build().toByteArray()
        return protobufDirectory
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
     */
    private fun getXMLPerson(): Person {
        // Data retrieval
        val middlename = binding.tbxMiddleName.text.toString()
        val homeNumber = binding.tbxHomeNumber.text.toString()
        val mobileNumber = binding.tbxMobileNumber.text.toString()
        val workNumber = binding.tbxWorkNumber.text.toString()

        // Form is well filled, return person
        val phones = mutableListOf<Phone>()
        val homePhone = if (homeNumber == "") null else Phone(Phone.Type.home, homeNumber)
        if (homePhone != null) phones.add(homePhone)
        val mobilePhone = if (mobileNumber == "") null else Phone(Phone.Type.mobile, mobileNumber)
        if (mobilePhone != null) phones.add(mobilePhone)
        val workPhone = if (workNumber == "") null else Phone(Phone.Type.work, workNumber)
        if (workPhone != null) phones.add(workPhone)

        return Person(
            binding.tbxName.text.toString(),
            binding.tbxFirstName.text.toString(),
            if (middlename == "") null else middlename,
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
}