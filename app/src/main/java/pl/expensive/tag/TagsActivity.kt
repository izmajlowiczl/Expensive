package pl.expensive.tag

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_tags.*
import pl.expensive.*
import pl.expensive.storage.Tag

class TagsActivity : AppCompatActivity() {
    private val tagsRepository by lazy { Injector.app().tagsRepository() }

    private val storeTagContinuation: (Tag?, Boolean) -> Unit = { storedTag, success ->
        if (success) {
            adapter.addFront(storedTag!!)
            vTags.smoothScrollToPosition(0)

            // Restore filter
            adapter.replace(tagsRepository.list())

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
                val input = vTagsFilterInput.text
                if (!input.isNullOrBlank()) {
                    storeTag(Tag(name = input.toString()), tagsRepository, storeTagContinuation)
                }
            }
            return@OnEditorActionListener false
        })

        vTagsFilterInput.afterTextChanged1 { text ->
            adapter.replace(filterTags(text, tagsRepository.list()))
        }

        vTagsCreate.setOnClickListener {
            val input = vTagsFilterInput.text
            if (!input.isNullOrBlank()) {
                storeTag(Tag(name = input.toString()), tagsRepository, storeTagContinuation)
            }
        }

        vTags.layoutManager = LinearLayoutManager(this)
        vTags.adapter = adapter
        adapter.replace(tagsRepository.list().toMutableList())
    }
}
