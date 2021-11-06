package ch.heigvd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import ch.heigvd.databinding.ActivitySerializationBinding
import ch.heigvd.iict.sym.lab.comm.CommunicationEventListener
import ch.heigvd.model.Directory
import ch.heigvd.model.Person
import ch.heigvd.model.Phone
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

/**
 * Activity in which is realized the point of the laboratory about serialization
 * @author Allemann, Balestrieri, Gomes
 */
class SerializationActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySerializationBinding
    private lateinit var adapter: ArrayAdapter<Person>
    private val directory = Directory()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_serialization)

        // Binding components
        binding = ActivitySerializationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle response of SymComManager and update UI
        val mcm = SymComManager(object : CommunicationEventListener {
            override fun handleServerResponse(response: String) {
                println(response)
            }
        })

        // Adding listener on button to send data
        binding.btnSendAsJSON.setOnClickListener {
            if (validateFormAndGetPerson()) {
                val person = getPerson()
                val string = Json.encodeToString(person)
                mcm.sendRequest(
                    getString(R.string.api_txt),
                    Json.encodeToString(string),
                    "application/json"
                )
                resetForm()
            }
        }

        // Add adapter to the list view
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, directory.people)
        binding.listLogs.adapter = adapter;
    }

    /**
     * Validate that required fields are not empty
     * Return if the form is valid or not
     */
    private fun validateFormAndGetPerson(): Boolean {
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
    private fun getPerson(): Person {
        // Data retrieval
        val middlename = binding.tbxMiddleName.text.toString()
        val homeNumber = binding.tbxHomeNumber.text.toString()
        val mobileNumber = binding.tbxMobileNumber.text.toString()
        val workNumber = binding.tbxWorkNumber.text.toString()

        // Form is well filled, return person
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
            if (middlename == "") null else middlename,
            phones.toList()
        )
    }

    /**
     * Reset form fields
     */
    private fun resetForm() {
        binding.tbxFirstName.text.clear()
        binding.tbxMiddleName.text.clear()
        binding.tbxHomeNumber.text.clear()
        binding.tbxMobileNumber.text.clear()
        binding.tbxWorkNumber.text.clear()
    }
}