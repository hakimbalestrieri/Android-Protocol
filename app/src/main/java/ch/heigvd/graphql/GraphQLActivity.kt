package ch.heigvd.graphql

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import ch.heigvd.CommunicationEventListener
import ch.heigvd.R
import ch.heigvd.SymComManager
import ch.heigvd.databinding.ActivityGraphQlactivityBinding
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray


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
                val authorsArray =
                    response.substring(response.indexOf("["), response.indexOf("]") + 1)
                val authorsAsJsonArray = Json.parseToJsonElement(authorsArray) as JsonArray
                authorsAsJsonArray.forEach {
                    authors.add(Json.decodeFromJsonElement(Author.serializer(), it))
                }
                authorsAdapter.notifyDataSetChanged()
            }
        }).sendRequest(
            // TODO : Limiter la réponse ? 2000 enregistrements...
            getString(R.string.api_graphql),
            "{\"query\": \"{findAllAuthors{id, name}}\"}",
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
                        val booksArray =
                            response.substring(
                                response.indexOf("["),
                                response.indexOf("]") + 1
                            )
                        val booksAsJsonArray = Json.parseToJsonElement(booksArray) as JsonArray
                        booksAsJsonArray.forEach {
                            books.add(
                                Json.decodeFromJsonElement(
                                    Book.serializer(),
                                    it
                                )
                            )
                        }
                        booksAdapter.notifyDataSetChanged()
                    }
                }).sendRequest(
                    // TODO : Limiter la réponse ?
                    getString(R.string.api_graphql),
                    "{\"query\": \"{findAuthorById(id: " + authors[position].id + "){books{title}}}\"}\n",
                    "application/json"
                )
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }
}
