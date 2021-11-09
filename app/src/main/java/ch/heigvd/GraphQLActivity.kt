package ch.heigvd

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import ch.heigvd.databinding.ActivityGraphQlactivityBinding
import ch.heigvd.iict.sym.lab.comm.CommunicationEventListener
import ch.heigvd.model.Author
import ch.heigvd.model.Book
import kotlinx.serialization.json.Json


/**
 * Activity in which is realized the point of the laboratory about Graph QL
 * @author Allemann, Balestrieri, Gomes
 */
class GraphQLActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGraphQlactivityBinding
    private lateinit var authorsAdapter: ArrayAdapter<Author>
    private val authors = mutableListOf<Author>()
    private lateinit var booksAdapter: ArrayAdapter<Book>
    private val books = mutableListOf<Book>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph_qlactivity)

        // Binding components
        binding = ActivityGraphQlactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load asynchronously authors
        SymComManager(object : CommunicationEventListener {
            override fun handleServerResponse(response: String) {
                // TODO : Dégeulasse
                val authorsArray =
                    response.substring(response.indexOf("[") + 1, response.indexOf("]")).split(",")
                authorsArray.forEach { authors.add(Json.decodeFromString(Author.serializer(), it)) }
                authorsAdapter.notifyDataSetChanged()
            }
        }).sendRequest(
            // TODO : Limiter la réponse... 2000 enregistrements...
            getString(R.string.api_graphql),
            "{\"query\": \"{findAllAuthors{name}}\"}",
            "application/json"
        )

        // Add adapter to the spinner
        authorsAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, authors)
        binding.spinner.adapter = authorsAdapter

        // Add adapter to the list
        booksAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, books)
        binding.lstBooks.adapter = booksAdapter

        // Set action when an item is selected
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                // Load asynchronously books
                SymComManager(object : CommunicationEventListener {
                    override fun handleServerResponse(response: String) {
                        books.clear()
                        // TODO : Dégeulasse
                        val booksArray =
                            response.substring(response.indexOf("[") + 1, response.indexOf("]"))
                                .split(",")
                        booksArray.forEach {
                            books.add(
                                Json.decodeFromString(
                                    Book.serializer(),
                                    it
                                )
                            )
                        }
                        booksAdapter.notifyDataSetChanged()
                    }
                }).sendRequest(
                    // TODO : Limiter la réponse...
                    getString(R.string.api_graphql),
                    "{\"query\": \"{findAuthorById(id: 1){books{title}}}\"}\n",
                    "application/json"
                )
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }
}
