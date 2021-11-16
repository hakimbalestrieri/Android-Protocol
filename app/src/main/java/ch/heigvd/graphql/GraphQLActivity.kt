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
import java.lang.Exception

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
    private var spinnerLastSelection = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph_qlactivity)

        // Binding components
        binding = ActivityGraphQlactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Add adapter to the spinner
        authorsAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, authors)
        binding.spinner.adapter = authorsAdapter

        // Add adapter to the list
        booksAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, books)
        binding.lstBooks.adapter = booksAdapter

        // Load asynchronously authors
        SymComManager(object : CommunicationEventListener {
            override fun handleServerResponse(response: Any) {
                parseAuthors(response as String)
            }
        }).sendRequest(
            getString(R.string.api_graphql),
            "{\"query\": \"{findAllAuthors{id, name}}\"}",
            mapOf("content-type" to "application/json")
        )

        // Set action when an item is selected
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                // Avoid reloading books if the selected item has not changed
                if (spinnerLastSelection == position) return
                spinnerLastSelection = position

                // Load asynchronously books
                SymComManager(object : CommunicationEventListener {
                    override fun handleServerResponse(response: Any) {
                        parseBooks(response as String)
                    }
                }).sendRequest(
                    getString(R.string.api_graphql),
                    "{\"query\": \"{findAuthorById(id: " + authors[position].id + "){books{title}}}\"}\n",
                    mapOf("content-type" to "application/json")
                )
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    /**
     * Parse books from query response
     * @param response query response
     */
    private fun parseBooks(response: String) {
        try {
            books.clear()
            val booksArray =
                response.substring(
                    response.indexOf("["),
                    response.indexOf("]") + 1
                )
            val result = Json.parseToJsonElement(booksArray) as JsonArray
            result.forEach {
                books.add(
                    Json.decodeFromJsonElement(
                        Book.serializer(),
                        it
                    )
                )
            }
        } catch (exception: Exception) {
            books.clear()
        }
        booksAdapter.notifyDataSetChanged()
    }

    /**
     * Parse authors from query response
     * @param response query response
     */
    private fun parseAuthors(response: String) {
        try {
            authors.clear()
            val authorsArray =
                response.substring(response.indexOf("["), response.indexOf("]") + 1)
            val authorsAsJsonArray = Json.parseToJsonElement(authorsArray) as JsonArray
            authorsAsJsonArray.forEach {
                authors.add(Json.decodeFromJsonElement(Author.serializer(), it))
            }
        } catch (exception: Exception) {
            authors.clear()
        }
        authorsAdapter.notifyDataSetChanged()
    }
}
