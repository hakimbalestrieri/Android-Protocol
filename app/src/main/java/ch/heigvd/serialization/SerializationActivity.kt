package ch.heigvd.serialization

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ch.heigvd.CommunicationEventListener
import ch.heigvd.R
import ch.heigvd.SymComManager
import ch.heigvd.databinding.ActivitySerializationBinding
import ch.heigvd.serialization.protobuf.DirectoryOuterClass
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

/**
 * Activity in which is realized the point of the laboratory about serialization
 * @author Allemann, Balestrieri, Gomes
 */
class SerializationActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySerializationBinding
    private var directory = Directory()

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
            if (validateForm()) {
                val person = getXMLPerson()
                directory.people.add(person)
                val xml = getString(R.string.xml_header) + directory.serializeToXML()
                SymComManager(object : CommunicationEventListener {
                    override fun handleServerResponse(response: String) {
                        val directory = Directory.parseXML(response)
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

            val person = getXMLPerson() //TODO : getXML ? getString plutôt non ?

            val protobufOut = DirectoryOuterClass.Person.newBuilder().setFirstname(person.firstname)
                .setMiddlename(person.middlename).setName(person.name)


            var typeProtobuf : DirectoryOuterClass.Phone.Type
            typeProtobuf = DirectoryOuterClass.Phone.Type.HOME //Demande une init

            for(i in person.phone) {

                when (i.type.toString()) {
                    "home" ->  typeProtobuf = DirectoryOuterClass.Phone.Type.HOME
                    "mobile" -> typeProtobuf = DirectoryOuterClass.Phone.Type.MOBILE
                    "work" -> typeProtobuf = DirectoryOuterClass.Phone.Type.WORK
                }

                protobufOut.addPhoneBuilder().setNumber(i.number).setType(typeProtobuf)
            }

            //Affichage
            println(protobufOut.build())

            SymComManager(object : CommunicationEventListener {
                override fun handleServerResponse(response: String) {
                    //val directory = Directory.parseXML(response)
                    println(response);
                }
            }).sendRequest(
                getString(R.string.api_protobuf),
                protobufOut.build().toString(),
                "application/protobuf"
            )
            resetForm()

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