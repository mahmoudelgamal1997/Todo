package dev.elgaml.noteit.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModel
import dev.elgaml.noteit.R
import dev.elgaml.noteit.data.Note
import dev.elgaml.noteit.databinding.ActivityOpenNoteBinding

class OpenNote : AppCompatActivity() {

    private var binding: ActivityOpenNoteBinding? = null
    private var selectedNote: Note? = null
    lateinit var viewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpenNoteBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        viewModel = NoteViewModel(application)
        selectedNote = intent.getSerializableExtra("SelectedNote") as Note

        binding?.llBg?.setBackgroundColor(selectedNote!!.color!!)
        binding?.tvTitleOpen?.text = selectedNote?.title
        binding?.tvDesOpen?.text = selectedNote?.des

        //back button functionality
        binding?.ivBackButton?.setOnClickListener {
            finish()
        }

        binding?.ivDeleteButton?.setOnClickListener {
            if (selectedNote != null){
            viewModel.removeNote(selectedNote!!)
            Toast.makeText(applicationContext,getString(R.string.remove),Toast.LENGTH_SHORT).show()
                finish()
        }
        }

        binding?.ivEditButton?.setOnClickListener {
            val editNoteIntent = Intent(this, EditNote::class.java)
            editNoteIntent.putExtra("EditSelectedNote", selectedNote)
            startActivity(editNoteIntent)
        }



        binding?.tvTitleOpen?.text = selectedNote?.title
        binding?.tvDesOpen?.text = selectedNote?.des
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}