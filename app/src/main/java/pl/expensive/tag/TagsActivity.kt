package pl.expensive.tag

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_tags.*
import pl.expensive.*
import pl.expensive.storage.Tag
import pl.expensive.storage.listTags

class TagsActivity : AppCompatActivity() {
    private val database by lazy { Injector.app().db() }

    private val storeTagContinuation: (Tag?, Boolean) -> Unit = { storedTag, success ->
        if (success) {
            adapter.addFront(storedTag!!)
            vTags.smoothScrollToPosition(0)

            // Restore filter
            adapter.replace(listTags(database))

            vTagsFilterInput.hideKeyboard()
            vTagsFilterInput.text.clear()
        } else {
            toast("Oops.. Couldn't create tag.")
        }
    }

    private val tagSelected: (Tag, Boolean) -> Unit = { tag, isChecked ->
        // TODO: where to store state with selected tags?
    }
    private val adapter by lazy { TagsAdapter(tagSelected) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tags)
        Injector.app().inject(this)

        vTagsBack.setOnClickListener { finish() }

        vTagsFilterInput.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                storeTag(vTagsFilterInput.text, database, storeTagContinuation)
            }
            return@OnEditorActionListener false
        })

        vTagsFilterInput.afterTextChanged1 { text ->
            adapter.replace(filterTags(text, listTags(database)))
        }

        vTagsCreate.setOnClickListener {
            storeTag(vTagsFilterInput.text, database, storeTagContinuation)
        }

        vTags.layoutManager = LinearLayoutManager(this)
        vTags.adapter = adapter
        adapter.replace(listTags(database).toMutableList())
    }
}
